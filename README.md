# Generador-Java-UML-Modelos

Proyecto de Desarrollo Basado en Modelos. El prototipo lee un modelo UML serializado en XMI y genera un modelo relacional intermedio, clases Java 17 y DDL para SQLite.

## Ejecutar el ejemplo

Abre PowerShell en la raíz del repositorio y ejecuta:

```powershell
.\scripts\run.ps1
```

El script compila el generador con el JDK 17 en `E:\Proyectos eclipse\tools\jdk-17.0.21.1+2`, transforma el ejemplo, produce las salidas y compila las clases Java generadas. Si ese JDK no está en la ruta, busca `javac` y `java` en el sistema.

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

El prototipo Java está ejecutable y sus clases generadas compilan. Eclipse Modeling Tools 2025-06 y los complementos ATL, Acceleo y Papyrus se prepararon desde sitios oficiales, pero Eclipse actualmente no inicia por un error del servicio `org.eclipse.core.runtime`. Por tanto, el uso de Papyrus y el flujo ATL/Acceleo aún no están validados. El DDL se genera, pero falta ejecutarlo en una instalación de SQLite.

Consulta `docs/TOOLCHAIN.md` para el entorno y `docs/documentacion-latex/main.tex` para la memoria académica. Las capturas reales del IDE y la ejecución están pendientes; no se presentan imágenes simuladas.
