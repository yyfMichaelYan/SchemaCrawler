/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.test.utility;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.testdb.SqlScript;
import schemacrawler.testdb.TestSchemaCreator;

@ExtendWith(TestLoggingExtension.class)
public abstract class BaseAdditionalDatabaseTest {

  private DataSource dataSource;

  protected void createDatabase(final String scriptsResource)
      throws SchemaCrawlerException, SQLException {
    try (final Connection connection = getConnection()) {
      final TestSchemaCreator schemaCreator = new TestSchemaCreator(connection, scriptsResource);
      schemaCreator.run();
    }
  }

  protected void createDataSource(
      final String connectionUrl, final String user, final String password) {
    createDataSource(connectionUrl, user, password, null);
  }

  protected void createDataSource(
      final String connectionUrl,
      final String user,
      final String password,
      final String connectionProperties) {

    dataSource = createDataSourceObject(connectionUrl, user, password, connectionProperties);
  }

  protected DataSource createDataSourceObject(
      final String connectionUrl,
      final String user,
      final String password,
      final String connectionProperties) {

    final BasicDataSource ds = new BasicDataSource();
    ds.setUrl(connectionUrl);
    ds.setUsername(user);
    ds.setPassword(password);
    if (connectionProperties != null) {
      ds.setConnectionProperties(connectionProperties);
    }
    ds.setDefaultAutoCommit(false);

    return ds;
  }

  protected final Connection getConnection() throws SchemaCrawlerException {
    try {
      return dataSource.getConnection();
    } catch (final SQLException e) {
      throw new SchemaCrawlerException(e.getMessage(), e);
    }
  }

  protected void runScript(final String databaseSqlResource) throws Exception {
    try (final Connection connection = getConnection()) {
      connection.setAutoCommit(false);

      final SqlScript sqlScript = new SqlScript(databaseSqlResource, connection);
      sqlScript.run();
    }
  }
}
