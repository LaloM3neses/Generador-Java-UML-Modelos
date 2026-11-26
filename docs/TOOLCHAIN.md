# Entorno de desarrollo y estado de herramientas

## Entorno localizado en E:

- Eclipse Modeling Tools 2025-06, en `E:\Proyectos eclipse\tools\eclipse`.
- Temurin JDK 17, en `E:\Proyectos eclipse\tools\jdk-17.0.21.1+2`.
- SQLite 3.54.1 x64, en `E:\Proyectos eclipse\tools\sqlite\sqlite3.exe`.
- Proyecto: `E:\Proyectos eclipse\Generador-Java-UML-Modelos`.

Se corrigió en `eclipse.ini` la ruta del perfil de configuración para que use la configuración de Eclipse que contiene los complementos instalados. Eclipse abre el espacio de trabajo `Proyectos Eclipse` y el proyecto actualizado muestra cero errores y una advertencia en la vista Problems. Se conservó la protección de Microsoft Defender; no se agregaron exclusiones.

## Ejecución comprobada del prototipo

Desde PowerShell:

```powershell
Set-Location 'E:\Proyectos eclipse\Generador-Java-UML-Modelos'
.\scripts\run.ps1
```

El script compila el generador con JDK 17, procesa `examples\biblioteca\biblioteca.uml`, escribe el modelo relacional JSON, tres clases Java y el esquema SQL, y compila automáticamente las clases generadas. Al encontrar SQLite en `E:\Proyectos eclipse\tools\sqlite` (o en el PATH), también ejecuta el DDL en una base temporal, verifica que se creen cuatro tablas, inserta datos con relaciones válidas y comprueba que una clave foránea inválida sea rechazada. La base temporal se elimina al terminar.

Para pasar otra entrada y una raíz de salida:

```powershell
.\scripts\run.ps1 .\ruta\modelo.uml .
```

## Herramientas de modelado

- Papyrus Desktop 7.1.0 para Eclipse 2025-06: <https://download.eclipse.org/modeling/mdt/papyrus/papyrus-desktop/updates/releases/2025-06/>
- ATL 4.12.0: <https://download.eclipse.org/mmt/atl/builds/release/4.12.0/>
- Acceleo 3.7: <https://download.eclipse.org/acceleo/updates/releases/3.7/R202405130857>

Papyrus, ATL y Acceleo se instalaron desde repositorios oficiales. El ejecutable verificado actualmente es `ModelGenerator.java`; no se afirma que la transformación ATL seguida de plantillas Acceleo ya esté integrada ni ejecutada.

## SQLite

El ejecutable x64 de SQLite 3.54.1 se descargó del sitio oficial y su SHA3-256 se contrastó con el valor publicado por SQLite. No se modificó el PATH global. El script usa el binario local y comprueba el DDL real, las cuatro tablas, la inserción de asociaciones válidas y el rechazo de una clave foránea inválida. La validación genera y borra una base desechable dentro de `build`.

## Evidencias visuales

La captura de Eclipse y la captura de una ejecución de `scripts/run.ps1` quedan pendientes porque la superposición de la Herramienta Recortes no aceptó acciones de captura/guardado durante esta sesión. La memoria no incluye imágenes simuladas: conserva los espacios identificados para insertar capturas auténticas.
