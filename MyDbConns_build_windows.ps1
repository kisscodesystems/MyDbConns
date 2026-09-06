# Builds the MyDbConns application.
#
# > powershell -ExecutionPolicy Bypass -File .\MyDbConns_build_windows.ps1

$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

# The classpath is separated by ; on windows.
$drivers = 'lib\Db2Jdbc.jar;lib\MssqlJdbc.jar;lib\MysqlJdbc.jar;lib\OracleJdbc.jar;lib\PostgresqlJdbc.jar'

# 1. Compile the sources into a fresh output directory.
#    The Oracle JDBC driver is required on the classpath (oracle.jdbc.OracleBfile).
#    javac and jar do not open wildcards on windows, the shell has to give them the
#    file list.
$sources = Get-ChildItem -LiteralPath 'src\com\kisscodesystems\MyDbConns' -Filter '*.java' | ForEach-Object { $_.FullName }
javac -cp $drivers -d bin $sources
if ($LASTEXITCODE -ne 0)
{
  Write-Output "The sources could not be compiled."
  exit 1
}

# 2. Package a runnable jar using the bundled manifest (it sets Main-Class).
#    The jar entries have to stay relative to bin, so the packaging runs from there.
Push-Location 'bin'
$classes = Get-ChildItem -LiteralPath 'com\kisscodesystems\MyDbConns' -Filter '*.class' -Name | ForEach-Object { "com/kisscodesystems/MyDbConns/$_" }
jar cvfm 'MyDbConns.jar' '..\src\com\kisscodesystems\MyDbConns\manifest.txt' $classes
if ($LASTEXITCODE -ne 0)
{
  Pop-Location
  Write-Output "The jar could not be created."
  exit 1
}
Copy-Item -LiteralPath 'MyDbConns.jar' -Destination '..' -Force
Pop-Location

Write-Output ""
Write-Output "You can now start your application by"
Write-Output "java -cp "$drivers;MyDbConns.jar" com.kisscodesystems.MyDbConns.MyDbConnsMain interactive mode"
