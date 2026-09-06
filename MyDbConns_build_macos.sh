#!/bin/bash

# This is the macos version. Nothing of this build is platform specific: javac, jar
# and the : separator of the classpath work the same way on linux and on macos, so
# this script is the very same as MyDbConns_build_linux.sh.

# 1. Compile the sources into a fresh output directory.
#    The Oracle JDBC driver is required on the classpath (oracle.jdbc.OracleBfile).
DRIVERS=lib/Db2Jdbc.jar:lib/MssqlJdbc.jar:lib/MysqlJdbc.jar:lib/OracleJdbc.jar:lib/PostgresqlJdbc.jar
javac -cp "$DRIVERS" -d bin src/com/kisscodesystems/MyDbConns/*.java

# 2. Package a runnable jar using the bundled manifest (it sets Main-Class).
cd bin && jar cvfm MyDbConns.jar ../src/com/kisscodesystems/MyDbConns/manifest.txt com/kisscodesystems/MyDbConns/*.class

cp MyDbConns.jar ../

echo ""
echo "You can now start your application by"
echo "java -cp $DRIVERS:MyDbConns.jar com.kisscodesystems.MyDbConns.MyDbConnsMain interactive mode"
