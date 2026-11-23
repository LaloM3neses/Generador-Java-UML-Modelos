# Entorno de desarrollo

## Instalado en este equipo

- Eclipse Modeling Tools 2026-09, instalado desde el paquete oficial de Eclipse Foundation.
- Java 17 disponible para definir compatibilidad del código generado.
- Java 25 usado por Eclipse 2026-09 en este equipo.
- Git disponible.

## Complementos de Eclipse requeridos

Instala desde `Help > Install New Software...` y reinicia Eclipse al terminar:

1. **Papyrus Desktop 7.1.0** — sitio oficial: <https://download.eclipse.org/modeling/mdt/papyrus/papyrus-desktop/updates/releases/2025-06/>
2. **ATL 4.12.0** — sitio oficial de releases: <https://download.eclipse.org/mmt/atl/builds/release/>
3. **Acceleo 3.7.16** — se distribuye desde el sitio de actualizaciones de Eclipse y el Marketplace. Busca “Acceleo” en `Help > Eclipse Marketplace...` o en el sitio de releases de la versión instalada.

Las versiones publicadas de Papyrus y Acceleo no corresponden a la misma entrega de Eclipse Modeling Tools instalada aquí. Si el instalador reporta incompatibilidad, usar Papyrus Desktop 7.1.0 como aplicación independiente o instalar una versión de Eclipse Modeling Tools 2025-06 para el espacio de trabajo de modelado. No forzar instalaciones con dependencias incompletas.

## Java y SQLite

- Configura el proyecto para compilar el código generado con Java 17.
- No es necesario instalar un servidor SQLite: es una base de datos embebida.
- Para que una aplicación Java abra bases SQLite, se añadirá el controlador JDBC como dependencia del módulo Java cuando se implemente esa parte. El ejecutable de SQLite es opcional para inspección manual.

## Estructura de este repositorio

- `models/uml/`: modelos Papyrus de entrada.
- `models/relational/`: modelo Ecore y modelos relacionales resultantes.
- `transformations/atl/`: transformación UML a relacional.
- `generator/acceleo/`: plantillas de generación de Java y SQL.
- `examples/biblioteca/`: ejemplo pequeño para recorrer el flujo completo.
- `generated/`: salidas reproducibles; no guardar aquí resultados temporales o específicos del equipo.
