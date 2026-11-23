# Entorno de desarrollo

## Instalado en este equipo

- Eclipse Modeling Tools 2025-06, instalado desde Eclipse Foundation.
- La distribución de Eclipse incluye una JVM 21 para ejecutar el IDE.
- Java 17 está disponible para compilar el código generado.
- Git está disponible.

## Complementos de Eclipse que requiere el proyecto

La entrega 2025-06 de Eclipse es la base del entorno. Instala estos componentes desde `Help > Install New Software...`:

1. **Papyrus Desktop 7.1.0** — sitio oficial: <https://download.eclipse.org/modeling/mdt/papyrus/papyrus-desktop/updates/releases/2025-06/>
2. **ATL 4.12.0** — releases oficiales: <https://download.eclipse.org/mmt/atl/builds/release/>
3. **Acceleo 3.7.16** — busca “Acceleo” en `Help > Eclipse Marketplace...` o en las actualizaciones de Eclipse Modeling Tools.

La documentación oficial publica Papyrus Desktop 7.1.0 para Eclipse 2025-06 y Acceleo 3.7.16 como compatible hasta Eclipse 2025-09. ATL 4.12.0 está publicado en su repositorio oficial de releases. Instala los tres desde Eclipse y reinícialo al finalizar. No fuerces complementos si el instalador muestra dependencias sin resolver.

## Java y SQLite

- El código generado se configurará para Java 17.
- SQLite es una base de datos embebida, así que no necesita un servidor.
- La aplicación Java usará el controlador JDBC de SQLite como dependencia cuando se implemente ese módulo. La herramienta de línea de comandos de SQLite es opcional para inspeccionar manualmente una base de datos.

## Estructura del repositorio

- `models/uml/`: modelos Papyrus de entrada.
- `models/relational/`: metamodelo Ecore y modelos relacionales.
- `transformations/atl/`: transformación UML a relacional.
- `generator/acceleo/`: plantillas de generación Java y SQL.
- `examples/biblioteca/`: ejemplo pequeño para recorrer el flujo completo.
- `generated/`: salidas reproducibles; no guardar aquí resultados temporales o específicos del equipo.

El espacio de trabajo local de Eclipse vive en `.eclipse-workspace/` y su configuración en `.eclipse-configuration/`; ambos se excluyen del repositorio.
