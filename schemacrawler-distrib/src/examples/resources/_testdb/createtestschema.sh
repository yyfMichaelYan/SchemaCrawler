#!/bin/sh
SC_DIR=`dirname $0`
java -cp $(echo $SC_DIR/lib/*.jar | tr ' ' ':'):. schemacrawler.testdb.TestSchemaCreatorMain $*