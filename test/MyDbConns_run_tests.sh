#!/bin/bash
#
# Runs the MyDbConns regression tests.
#
# Compiles the current sources, then compiles and runs the JUnit tests that
# check the validators and the pure data/array helpers.
#
set -e
cd "$(dirname "$0")/.."
ROOT="$(pwd)"

SRC="src/com/kisscodesystems/MyDbConns"
BUILD="$ROOT/build/testrun"
JARS="$ROOT/lib"
JUNIT="$JARS/junit-4.12.jar"
HAMCREST="$JARS/hamcrest-core-1.3.jar"
DRIVER=/opt/dkcs/helper/jars/dbdrivers/OracleJdbc.jar

rm -rf "$BUILD"
mkdir -p "$BUILD/main_out" "$BUILD/test_out"

# 1. Compile the current sources.
javac -cp "$DRIVER" -d "$BUILD/main_out" "$SRC"/*.java

# 2. Compile and run the tests.
CP="$BUILD/main_out:$DRIVER:$JUNIT:$HAMCREST"
javac -cp "$CP" -d "$BUILD/test_out" test/com/kisscodesystems/MyDbConns/MyDbConnsTest.java
java  -cp "$CP:$BUILD/test_out" org.junit.runner.JUnitCore com.kisscodesystems.MyDbConns.MyDbConnsTest
