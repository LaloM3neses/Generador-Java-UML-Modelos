# Entorno de desarrollo y estado de herramientas

## Entorno localizado en E:

- Eclipse Modeling Tools 2025-06, en `E:\Proyectos eclipse\tools\eclipse`.
- Temurin JDK 17, en `E:\Proyectos eclipse\tools\jdk-17.0.21.1+2`.
- Proyecto: `E:\Proyectos eclipse\Generador-Java-UML-Modelos`.
- Eclipse incluye su propia JVM para ejecutar el IDE.

Se agregaron desde repositorios oficiales los complementos ATL 4.12, Acceleo 3.7.17 y Papyrus UML/diagramas 7.1.0. El perfil de paquetes los registró, pero al iniciar el IDE se encontró `Unable to acquire application service` asociado con `org.eclipse.core.runtime`, incluso después de iniciar con `-clean`. Por eso, la instalación de Eclipse queda pendiente de reparación y no se afirma que el flujo ATL/Acceleo se haya ejecutado desde el IDE.

## Ejecución comprobada del prototipo

Desde PowerShell:

```powershell
Set-Location 'E:\Proyectos eclipse\Generador-Java-UML-Modelos'
.\scripts\run.ps1
```

El script compila el generador con JDK 17, procesa `examples\biblioteca\biblioteca.uml`, escribe JSON, Java y SQL y compila automáticamente las clases Java resultantes. Para pasar otra entrada y una raíz de salida:

```powershell
.\scripts\run.ps1 .\ruta\modelo.uml .
```

## Herramientas oficiales de modelado

- Papyrus Desktop 7.1.0 para Eclipse 2025-06: <https://download.eclipse.org/modeling/mdt/papyrus/papyrus-desktop/updates/releases/2025-06/>
- ATL 4.12.0: <https://download.eclipse.org/mmt/atl/builds/release/4.12.0/>
- Acceleo 3.7: <https://download.eclipse.org/acceleo/updates/releases/3.7/R202405130857>

La compatibilidad publicada de Acceleo 3.7 incluye Eclipse 2025-06. Estos complementos son herramientas previstas para la evolución del proyecto; el ejecutable actualmente verificado es `ModelGenerator.java`, no una transformación ATL seguida de plantillas Acceleo.

## SQLite

El generador crea `generated\sql\schema.sql`. En esta computadora no se encontró Python ni el ejecutable SQLite, por lo que la aplicación del DDL a un motor SQLite real no quedó verificada. El esquema se inspeccionó y se comprobó que contiene claves primarias, las claves foráneas previstas y la tabla de unión del ejemplo.
