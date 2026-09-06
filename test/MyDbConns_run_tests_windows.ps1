# Runs the MyDbConns regression tests.
#
# > powershell -ExecutionPolicy Bypass -File .\MyDbConns_run_tests_windows.ps1
#
# Compiles the current sources, then compiles and runs the JUnit tests that
# check the validators and the pure data/array helpers.
#
# The path of the oracle jdbc driver can be given in the DRIVER environment variable.

$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath (Join-Path $PSScriptRoot '..')
$root = (Get-Location).Path

$src = Join-Path $root 'src\com\kisscodesystems\MyDbConns'
$build = Join-Path $root 'build\testrun'
$jars = Join-Path $root 'lib'
$junit = Join-Path $jars 'junit-4.12.jar'
$hamcrest = Join-Path $jars 'hamcrest-core-1.3.jar'
# The oracle jdbc driver, the one dependency of these sources. It can be given
# in the DRIVER environment variable, otherwise it is the one in lib.
$driver = if ($env:DRIVER) { $env:DRIVER } else { Join-Path $jars 'OracleJdbc.jar' }

if (Test-Path -LiteralPath $build)
{
  Remove-Item -LiteralPath $build -Recurse -Force
}
New-Item -ItemType Directory -Path (Join-Path $build 'main_out') -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $build 'test_out') -Force | Out-Null

$sources = Get-ChildItem -LiteralPath $src -Filter '*.java' | ForEach-Object { $_.FullName }

# 1. Compile the current sources.
javac -cp $driver -d (Join-Path $build 'main_out') $sources
if ($LASTEXITCODE -ne 0)
{
  Write-Output "The sources could not be compiled."
  exit 1
}

# 2. Compile and run the tests. The classpath is separated by ; on windows.
$cp = ((Join-Path $build 'main_out'), $driver, $junit, $hamcrest) -join ';'
javac -cp $cp -d (Join-Path $build 'test_out') (Join-Path $root 'test\com\kisscodesystems\MyDbConns\MyDbConnsTest.java')
if ($LASTEXITCODE -ne 0)
{
  Write-Output "The tests could not be compiled."
  exit 1
}
java -cp ($cp + ';' + (Join-Path $build 'test_out')) org.junit.runner.JUnitCore com.kisscodesystems.MyDbConns.MyDbConnsTest
exit $LASTEXITCODE
