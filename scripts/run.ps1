$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$model = if ($args.Count -ge 1) { [System.IO.Path]::GetFullPath($args[0]) } else { Join-Path $projectRoot 'examples\biblioteca\biblioteca.uml' }
$outputRoot = if ($args.Count -ge 2) { [System.IO.Path]::GetFullPath($args[1]) } else { $projectRoot }
$jdk = 'E:\Proyectos eclipse\tools\jdk-17.0.21.1+2'
$javac = if (Test-Path (Join-Path $jdk 'bin\javac.exe')) { Join-Path $jdk 'bin\javac.exe' } else { (Get-Command javac -ErrorAction Stop).Source }
$java = if (Test-Path (Join-Path $jdk 'bin\java.exe')) { Join-Path $jdk 'bin\java.exe' } else { (Get-Command java -ErrorAction Stop).Source }
$source = Join-Path $projectRoot 'src\main\java\edu\modelos\generador\ModelGenerator.java'
$classes = Join-Path $projectRoot 'build\classes'
$generatedJava = Join-Path $outputRoot 'generated\java'
$generatedClasses = Join-Path $projectRoot 'build\generated-classes'
New-Item -ItemType Directory -Force -Path $classes | Out-Null
& $javac -encoding UTF-8 -d $classes $source
if ($LASTEXITCODE -ne 0) { throw 'La compilacion del generador fallo.' }
& $java -cp $classes edu.modelos.generador.ModelGenerator $model $outputRoot
if ($LASTEXITCODE -ne 0) { throw 'La ejecucion del generador fallo.' }
$javaFiles = @(Get-ChildItem -LiteralPath $generatedJava -Filter '*.java' -File -Recurse)
if ($javaFiles.Count -eq 0) { throw 'El generador no produjo archivos Java.' }
New-Item -ItemType Directory -Force -Path $generatedClasses | Out-Null
$sourceFiles = @($javaFiles | ForEach-Object { $_.FullName })
& $javac -encoding UTF-8 -d $generatedClasses $sourceFiles
if ($LASTEXITCODE -ne 0) { throw 'La compilacion del codigo Java generado fallo.' }
Write-Host "Validacion Java completada: $($javaFiles.Count) clases generadas y compiladas."
