package edu.modelos.generador;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

public final class ModelGenerator {
    private static final String XMI = "http://www.omg.org/spec/XMI/20131001";
    private ModelGenerator() {}

    private static final class Column {
        String name, javaType, sqlType, refTable, refColumn;
        boolean nullable, pk, unique;
    }
    private static final class Table {
        String name;
        final List<Column> columns = new ArrayList<>();
    }
    private static final class Entity {
        String id, name;
        final List<Column> columns = new ArrayList<>();
        Column pk;
        Table table;
    }
    private static final class End {
        String type;
        int lower, upper;
    }

    public static void main(String[] args) {
        try {
            Path project = Path.of("").toAbsolutePath();
            Path model = args.length > 0 ? Path.of(args[0]).toAbsolutePath()
                    : project.resolve("examples/biblioteca/biblioteca.uml");
            Path output = args.length > 1 ? Path.of(args[1]).toAbsolutePath() : project;
            ModelGenerator generator = new ModelGenerator();
            List<Entity> entities = generator.readEntities(model);
            List<List<End>> associations = generator.readAssociations(model);
            List<Table> tables = generator.transform(entities, associations);
            generator.writeOutputs(entities, tables, output);
            System.out.println("Generacion completada: " + entities.size() + " entidades, " + tables.size() + " tablas.");
            System.out.println("SQL: " + output.resolve("generated/sql/schema.sql"));
            System.out.println("Java: " + output.resolve("generated/java/edu/modelos/generated"));
            System.out.println("Modelo relacional: " + output.resolve("generated/relational/relational-model.json"));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private Document parse(Path path) throws Exception {
        if (!Files.isRegularFile(path)) throw new IOException("No existe el modelo UML: " + path);
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        f.setFeature("http://xml.org/sax/features/external-general-entities", false);
        f.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        return f.newDocumentBuilder().parse(path.toFile());
    }

    private List<Entity> readEntities(Path path) throws Exception {
        Document d = parse(path);
        List<Element> elements = new ArrayList<>();
        collectTyped(d.getDocumentElement(), "Class", elements);
        List<Entity> result = new ArrayList<>();
        Map<String, Entity> ids = new HashMap<>();
        for (Element e : elements) {
            Entity entity = new Entity();
            entity.id = attr(e, "id");
            entity.name = requiredName(e);
            identifier(entity.name, "clase");
            if (ids.containsKey(entity.id)) throw new IllegalArgumentException("xmi:id duplicado: " + entity.id);
            for (Element a : children(e, "ownedAttribute")) {
                if (!a.getAttribute("association").isBlank()) continue;
                String name = requiredName(a);
                identifier(name, "atributo");
                Column c = column(name, type(a.getAttribute("type")));
                c.nullable = integer(a.getAttribute("lower"), 0) == 0;
                if (name.equalsIgnoreCase("id")) { c.pk = true; c.nullable = false; }
                entity.columns.add(c);
            }
            if (entity.columns.stream().filter(c -> c.pk).count() > 1)
                throw new IllegalArgumentException("La clase " + entity.name + " declara mas de un id.");
            if (entity.columns.stream().noneMatch(c -> c.pk)) {
                Column id = column("id", "Integer");
                id.pk = true; id.nullable = false; entity.columns.add(0, id);
            }
            entity.pk = entity.columns.stream().filter(c -> c.pk).findFirst().orElseThrow();
            entity.table = new Table();
            entity.table.name = entity.name;
            entity.table.columns.addAll(entity.columns);
            ids.put(entity.id, entity);
            result.add(entity);
        }
        if (result.isEmpty()) throw new IllegalArgumentException("El modelo no contiene clases UML.");
        uniqueEntities(result);
        return result;
    }

    private List<List<End>> readAssociations(Path path) throws Exception {
        Document d = parse(path);
        List<Element> associations = new ArrayList<>();
        collectTyped(d.getDocumentElement(), "Association", associations);
        Map<String, Element> props = new HashMap<>();
        List<Element> allProps = new ArrayList<>();
        collectNamed(d.getDocumentElement(), "ownedAttribute", allProps);
        collectNamed(d.getDocumentElement(), "ownedEnd", allProps);
        for (Element p : allProps) props.put(attr(p, "id"), p);

        List<List<End>> result = new ArrayList<>();
        for (Element association : associations) {
            List<Element> elements = children(association, "ownedEnd");
            if (elements.isEmpty()) {
                for (String ref : association.getAttribute("memberEnd").trim().split("\\s+")) {
                    String id = ref.contains("#") ? ref.substring(ref.lastIndexOf('#') + 1) : ref;
                    if (props.containsKey(id)) elements.add(props.get(id));
                }
            }
            List<End> ends = new ArrayList<>();
            for (Element e : elements) {
                End end = new End();
                end.type = e.getAttribute("type");
                end.lower = integer(e.getAttribute("lower"), 0);
                end.upper = upper(e.getAttribute("upper"));
                ends.add(end);
            }
            if (!ends.isEmpty()) result.add(ends);
        }
        return result;
    }

    private List<Table> transform(List<Entity> entities, List<List<End>> associations) {
        Map<String, Entity> byId = new HashMap<>(), byName = new HashMap<>();
        List<Table> tables = new ArrayList<>();
        for (Entity e : entities) {
            byId.put(e.id, e); byName.put(e.name, e); tables.add(e.table);
        }
        for (List<End> ends : associations) {
            if (ends.size() != 2) throw new IllegalArgumentException("Solo se aceptan asociaciones binarias.");
            End a = ends.get(0), b = ends.get(1);
            Entity ea = byId.getOrDefault(a.type, byName.get(a.type));
            Entity eb = byId.getOrDefault(b.type, byName.get(b.type));
            if (ea == null || eb == null) throw new IllegalArgumentException("Asociacion con una clase no encontrada.");
            if (a.upper < 0 && b.upper < 0) {
                Table join = new Table();
                join.name = ea.name + "_" + eb.name;
                Column left = foreign(snake(ea.name) + "_id", ea);
                Column right = foreign(snake(eb.name) + "_id", eb);
                left.pk = true; right.pk = true;
                left.nullable = false; right.nullable = false;
                join.columns.add(left); join.columns.add(right);
                uniqueColumns(join); tables.add(join);
            } else if (a.upper < 0) {
                addForeign(ea.table, eb, b.lower == 0, false);
            } else if (b.upper < 0) {
                addForeign(eb.table, ea, a.lower == 0, false);
            } else {
                addForeign(eb.table, ea, a.lower == 0 || b.lower == 0, true);
            }
        }
        for (Table t : tables) uniqueColumns(t);
        uniqueTables(tables);
        return tables;
    }

    private void addForeign(Table table, Entity target, boolean nullable, boolean unique) {
        String base = snake(target.name) + "_id", name = base;
        int suffix = 2;
        while (containsColumn(table, name)) name = base + "_" + suffix++;
        Column c = foreign(name, target);
        c.nullable = nullable; c.unique = unique; table.columns.add(c);
    }

    private boolean containsColumn(Table table, String name) {
        for (Column c : table.columns) if (c.name.equalsIgnoreCase(name)) return true;
        return false;
    }

    private Column foreign(String name, Entity target) {
        Column c = column(name, target.pk.javaType);
        c.sqlType = target.pk.sqlType; c.refTable = target.name; c.refColumn = target.pk.name;
        return c;
    }

    private Column column(String name, String type) {
        Column c = new Column(); c.name = name; c.javaType = type;
        c.sqlType = switch (type) {
            case "String" -> "TEXT";
            case "Integer", "Boolean" -> "INTEGER";
            case "Double" -> "REAL";
            default -> throw new IllegalArgumentException("Tipo no admitido: " + type);
        };
        return c;
    }

    private String type(String value) {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) throw new IllegalArgumentException("Atributo sin tipo UML.");
        String tail = v.substring(Math.max(v.lastIndexOf('#'), v.lastIndexOf('/')) + 1).toLowerCase(Locale.ROOT);
        return switch (tail) {
            case "string", "estring" -> "String";
            case "integer", "int", "eint" -> "Integer";
            case "boolean", "eboolean" -> "Boolean";
            case "real", "double", "edouble" -> "Double";
            default -> throw new IllegalArgumentException("Tipo UML no admitido: " + value);
        };
    }

    private void writeOutputs(List<Entity> entities, List<Table> tables, Path root) throws IOException {
        Path sql = root.resolve("generated/sql");
        Path java = root.resolve("generated/java/edu/modelos/generated");
        Path relational = root.resolve("generated/relational");
        Files.createDirectories(sql); Files.createDirectories(java); Files.createDirectories(relational);
        Files.writeString(sql.resolve("schema.sql"), sql(tables).stripTrailing() + "\n", StandardCharsets.UTF_8);
        Files.writeString(relational.resolve("relational-model.json"), json(tables).stripTrailing() + "\n", StandardCharsets.UTF_8);
        for (Entity e : entities) Files.writeString(java.resolve(e.name + ".java"), java(e).stripTrailing() + "\n", StandardCharsets.UTF_8);
    }

    private String sql(List<Table> tables) {
        StringBuilder out = new StringBuilder("-- Generado automaticamente desde UML/XMI.\nPRAGMA foreign_keys = ON;\n\n");
        for (Table t : tables) {
            out.append("CREATE TABLE IF NOT EXISTS ").append(q(t.name)).append(" (\n");
            List<String> parts = new ArrayList<>();
            for (Column c : t.columns) {
                StringBuilder col = new StringBuilder("  ").append(q(c.name)).append(' ').append(c.sqlType);
                if (c.pk && t.columns.stream().filter(x -> x.pk).count() == 1 && c.sqlType.equals("INTEGER")) col.append(" PRIMARY KEY");
                if ((!c.nullable || c.pk) && !(c.pk && "INTEGER".equals(c.sqlType))) col.append(" NOT NULL");
                if (c.unique && !c.pk) col.append(" UNIQUE");
                if (c.javaType.equals("Boolean")) col.append(" CHECK (").append(q(c.name)).append(" IN (0, 1))");
                parts.add(col.toString());
            }
            List<Column> keys = t.columns.stream().filter(c -> c.pk).toList();
            if (keys.size() > 1) parts.add("  PRIMARY KEY (" + String.join(", ", keys.stream().map(c -> q(c.name)).toList()) + ")");
            for (Column c : t.columns) if (c.refTable != null)
                parts.add("  FOREIGN KEY (" + q(c.name) + ") REFERENCES " + q(c.refTable) + "(" + q(c.refColumn) + ")");
            out.append(String.join(",\n", parts)).append("\n);\n\n");
        }
        return out.toString();
    }

    private String java(Entity e) {
        StringBuilder out = new StringBuilder("package edu.modelos.generated;\n\n/** Generado desde UML/XMI; no editar manualmente. */\npublic class ")
                .append(e.name).append(" {\n");
        for (Column c : e.columns) out.append("    private ").append(c.javaType).append(' ').append(c.name).append(";\n");
        out.append("\n    public ").append(e.name).append("() {}\n");
        for (Column c : e.columns) {
            String cap = Character.toUpperCase(c.name.charAt(0)) + c.name.substring(1);
            out.append("\n    public ").append(c.javaType).append(" get").append(cap).append("() { return ").append(c.name).append("; }\n");
            out.append("    public void set").append(cap).append('(').append(c.javaType).append(' ').append(c.name).append(") { this.")
                    .append(c.name).append(" = ").append(c.name).append("; }\n");
        }
        return out.append("}\n").toString();
    }

    private String json(List<Table> tables) {
        StringBuilder out = new StringBuilder("{\n  \"tables\": [\n");
        for (int i = 0; i < tables.size(); i++) {
            Table t = tables.get(i);
            out.append("    { \"name\": \"").append(escape(t.name)).append("\", \"columns\": [\n");
            for (int j = 0; j < t.columns.size(); j++) {
                Column c = t.columns.get(j);
                out.append("      {\"name\": \"").append(escape(c.name)).append("\", \"type\": \"").append(c.sqlType)
                   .append("\", \"nullable\": ").append(c.nullable).append(", \"primaryKey\": ").append(c.pk);
                if (c.refTable != null) out.append(", \"references\": \"").append(escape(c.refTable)).append("(").append(escape(c.refColumn)).append(")\"");
                out.append("}").append(j + 1 < t.columns.size() ? "," : "").append("\n");
            }
            out.append("    ]}").append(i + 1 < tables.size() ? "," : "").append("\n");
        }
        return out.append("  ]\n}\n").toString();
    }

    private void uniqueEntities(List<Entity> entities) {
        Set<String> names = new HashSet<>();
        for (Entity e : entities) {
            if (!names.add(e.name.toLowerCase(Locale.ROOT))) throw new IllegalArgumentException("Clase duplicada: " + e.name);
            uniqueColumns(e.table);
        }
    }
    private void uniqueTables(List<Table> tables) {
        Set<String> names = new HashSet<>();
        for (Table t : tables) if (!names.add(t.name.toLowerCase(Locale.ROOT)))
            throw new IllegalArgumentException("Tabla duplicada: " + t.name);
    }
    private void uniqueColumns(Table t) {
        Set<String> names = new HashSet<>();
        for (Column c : t.columns) if (!names.add(c.name.toLowerCase(Locale.ROOT)))
            throw new IllegalArgumentException("Columna duplicada en " + t.name + ": " + c.name);
    }
    private void identifier(String s, String kind) {
        if (s == null || !s.matches("[A-Za-z_][A-Za-z0-9_]*")) throw new IllegalArgumentException("Nombre de " + kind + " invalido: " + s);
    }
    private String requiredName(Element e) {
        String n = e.getAttribute("name").trim();
        if (n.isEmpty()) throw new IllegalArgumentException("Elemento UML sin nombre.");
        return n;
    }
    private String attr(Element e, String local) {
        String v = e.getAttributeNS(XMI, local);
        return v.isBlank() ? e.getAttribute("xmi:" + local) : v;
    }
    private int integer(String s, int fallback) { return s == null || s.isBlank() ? fallback : Integer.parseInt(s); }
    private int upper(String s) { return s == null || s.isBlank() ? 1 : (s.equals("*") ? -1 : Integer.parseInt(s)); }
    private void collectTyped(Element parent, String kind, List<Element> out) {
        NodeList list = parent.getElementsByTagNameNS("*", "*");
        for (int i = 0; i < list.getLength(); i++) if (list.item(i) instanceof Element e) {
            String t = attr(e, "type");
            if (t.endsWith(":" + kind) || t.equals(kind)) out.add(e);
        }
    }
    private void collectNamed(Element parent, String name, List<Element> out) {
        NodeList list = parent.getElementsByTagNameNS("*", name);
        for (int i = 0; i < list.getLength(); i++) if (list.item(i) instanceof Element e) out.add(e);
    }
    private List<Element> children(Element parent, String name) {
        List<Element> out = new ArrayList<>();
        NodeList list = parent.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) if (list.item(i) instanceof Element e && name.equals(e.getLocalName())) out.add(e);
        return out;
    }
    private String snake(String s) { return s.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT); }
    private String q(String s) { return "\"" + s.replace("\"", "\"\"") + "\""; }
    private String escape(String s) { return s.replace("\\", "\\\\").replace("\"", "\\\""); }
}
