#!/bin/bash

# 1. Compile the sources into a fresh output directory.
#    The Oracle JDBC driver is required on the classpath (oracle.jdbc.OracleBfile).
DRIVER=lib/Db2Jdbc.jar:lib/MssqlJdbc.jar:lib/MysqlJdbc.jar:lib/OracleJdbc.jar:lib/PostgresqlJdbc.jar
javac -cp "$DRIVER" -d bin src/com/kisscodesystems/MyDbConns/*.java

# 2. Package a runnable jar using the bundled manifest (it sets Main-Class).
cd bin && jar cvfm MyDbConns.jar ../src/com/kisscodesystems/MyDbConns/manifest.txt com/kisscodesystems/MyDbConns/*.class

mv MyDbConns.jar ../

echo ""
echo "You can now start your application by"
echo "java -cp $DRIVER:MyDbConns.jar com.kisscodesystems.MyDbConns.MyDbConnsMain interactive mode"
