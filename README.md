# Generador-Java-UML-Modelos

Proyecto de Desarrollo Basado en Modelos. El prototipo lee un modelo UML serializado en XMI y genera un modelo relacional intermedio, clases Java 17 y DDL para SQLite.

## Ejecutar el ejemplo

Abre PowerShell en la raíz del repositorio y ejecuta:

```powershell
.\scripts\run.ps1
```

El script compila el generador con JDK 17, procesa el ejemplo, produce las salidas y compila las clases Java generadas. También valida el DDL con SQLite: crea una base temporal, comprueba las cuatro tablas y las relaciones, y confirma que una clave foránea inválida se rechaza. El ejecutable local de SQLite está en `E:\Proyectos eclipse\tools\sqlite`; no se modifica el `PATH` global.

Para indicar otro modelo y carpeta de salida:

```powershell
.\scripts\run.ps1 .\ruta\modelo.uml .
```

## Ejemplo y salidas

El modelo de biblioteca contiene Autor, Libro y Editorial, atributos básicos, una relación muchos a muchos Autor--Libro y una relación uno a muchos Editorial--Libro. La ejecución actual informa tres entidades y produce cuatro tablas.

- `generated/relational/relational-model.json`
- `generated/java/edu/modelos/generated/*.java`
- `generated/sql/schema.sql`

No edites manualmente las salidas: se regeneran desde el modelo UML/XMI.

## Alcance y estado

Se admiten String, Integer, Boolean y Real/Double. Las asociaciones binarias uno a muchos y muchos a muchos del ejemplo se convierten a claves foráneas y tabla de unión. No se implementan herencia, operaciones UML, asociaciones n-arias, interfaz gráfica ni una aplicación CRUD.

El prototipo Java y sus clases generadas compilan. Eclipse Modeling Tools 2025-06 abre el proyecto con cero errores y una advertencia en la vista de problemas. SQLite 3.54.1 valida el DDL generado, las relaciones y el rechazo de una clave foránea inválida. Papyrus, ATL y Acceleo están instalados como herramientas de evolución, pero su flujo de transformación todavía no forma parte del ejecutable Java verificado.

Consulta `docs/TOOLCHAIN.md` para el entorno y `docs/documentacion-latex/main.tex` para la memoria académica. Las capturas reales no se pudieron guardar desde la herramienta de Recortes en esta sesión; la memoria conserva marcadores explícitos en esos lugares.
