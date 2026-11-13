# Generador-Java-UML-Modelos

Proyecto de Desarrollo Basado en Modelos.

## Descripción

Proyecto final para la materia de Desarrollo Basado en Modelos. El sistema tomará un modelo UML de clases y generará un modelo relacional, clases Java y un script SQL compatible con SQLite.

## Objetivo

Construir un flujo de desarrollo basado en modelos que permita describir la estructura de un sistema una sola vez en UML y producir automáticamente sus representaciones relacional y de código.

## Entrada

Un modelo UML de clases creado en Eclipse Papyrus. La primera versión admitirá:

- Clases.
- Atributos con tipos básicos acordados para el proyecto.
- Asociaciones uno a uno, uno a muchos y muchos a muchos.

## Flujo de transformación

1. El modelo UML se transforma a un modelo relacional mediante ATL.
2. Acceleo genera desde el modelo relacional las clases Java y el script SQL.
3. SQLite ejecuta el script para comprobar que el esquema de base de datos se crea correctamente.

## Salidas

- Un modelo relacional con tablas, columnas, claves primarias y claves foráneas.
- Tablas intermedias para las asociaciones muchos a muchos.
- Clases Java para las entidades del modelo.
- Un script SQL para crear el esquema en SQLite.

## Validaciones iniciales

- Los nombres de clases y atributos deben ser únicos dentro de su ámbito.
- Los atributos deben usar tipos admitidos.
- Las asociaciones deben tener extremos válidos y multiplicidades admitidas.

## Fuera del alcance inicial

- Herencia e interfaces.
- Operaciones o métodos definidos en las clases UML.
- Interfaces gráficas.
- Generación de una aplicación CRUD completa.

## Herramientas previstas

| Herramienta | Uso |
|---|---|
| Eclipse IDE | Entorno de trabajo para las herramientas de modelado. |
| Eclipse Papyrus | Creación del diagrama de clases UML. |
| EMF / Ecore | Representación de metamodelos y modelos. |
| ATL | Transformación UML a modelo relacional. |
| Acceleo | Generación de Java y SQL desde el modelo relacional. |
| Java | Lenguaje de las clases generadas. |
| SQLite | Ejecución y comprobación del esquema SQL generado. |

## Criterio de éxito de la primera versión

Dado un modelo UML pequeño y válido, el proyecto debe generar el modelo relacional, las clases Java y un script SQL que SQLite pueda ejecutar para crear las tablas y relaciones admitidas.

## Próximos pasos

1. Confirmar los tipos básicos y las reglas de nombres.
2. Diseñar el metamodelo y las reglas de validación.
3. Crear un modelo UML de ejemplo para probar el flujo completo.
4. Implementar la transformación ATL y las plantillas Acceleo.
