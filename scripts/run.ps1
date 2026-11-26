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
$sqliteCandidate = Join-Path (Split-Path -Parent $projectRoot) 'tools\sqlite\sqlite3.exe'
$sqliteCommand = if (Test-Path $sqliteCandidate) { $sqliteCandidate } else { (Get-Command sqlite3 -ErrorAction SilentlyContinue).Source }
if ($sqliteCommand) {
    $database = Join-Path $projectRoot ('build\sqlite-validation-' + [Guid]::NewGuid().ToString('N') + '.db')
    try {
        Get-Content -LiteralPath (Join-Path $outputRoot 'generated\sql\schema.sql') -Raw | & $sqliteCommand -batch -bail $database
        if ($LASTEXITCODE -ne 0) { throw 'SQLite rechazo el DDL generado.' }
        $tableCount = (& $sqliteCommand -batch $database "SELECT COUNT(*) FROM sqlite_schema WHERE type='table' AND name NOT LIKE 'sqlite_%';" | Out-String).Trim()
        if ($LASTEXITCODE -ne 0 -or $tableCount -ne '4') { throw "Se esperaban 4 tablas en SQLite y se obtuvieron $tableCount." }
        $seedSql = "PRAGMA foreign_keys=ON; INSERT INTO Autor(id,nombre) VALUES (1,'Ada'); INSERT INTO Editorial(id,nombre) VALUES (1,'Editorial Demo'); INSERT INTO Libro(id,titulo,publicado,editorial_id) VALUES (1,'Modelo UML',1,1); INSERT INTO Autor_Libro(autor_id,libro_id) VALUES (1,1);"
        & $sqliteCommand -batch -bail $database $seedSql
        if ($LASTEXITCODE -ne 0) { throw 'SQLite rechazo las relaciones validas del ejemplo.' }
        $invalidSql = 'PRAGMA foreign_keys=ON; INSERT INTO Autor_Libro(autor_id,libro_id) VALUES (999,1);'
        $null = & $sqliteCommand -batch -bail $database $invalidSql 2>&1
        if ($LASTEXITCODE -eq 0) { throw 'SQLite no rechazo la clave foranea de prueba invalida.' }
        Write-Host "Validacion SQLite completada: $tableCount tablas, relaciones y clave foranea."
    } finally {
        if (Test-Path -LiteralPath $database) { Remove-Item -LiteralPath $database }
    }
} else {
    Write-Warning 'SQLite no esta instalado; se omitio la validacion del DDL.'
}
$global:LASTEXITCODE = 0
