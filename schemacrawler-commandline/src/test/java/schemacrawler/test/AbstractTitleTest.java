/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.ExecutableTestUtility.hasSameContentAndTypeAs;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.copyResourceToTempFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestAssertNoSystemErrOutput;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestUtility;
import schemacrawler.tools.options.OutputFormat;

@ExtendWith(TestAssertNoSystemErrOutput.class)
@ExtendWith(TestDatabaseConnectionParameterResolver.class)
public abstract class AbstractTitleTest
{

  private static final String TITLE_OUTPUT = "title_output/";

  @BeforeAll
  public static void clean()
    throws Exception
  {
    TestUtility.clean(TITLE_OUTPUT);
  }

  private Path hsqldbProperties;

  @Test
  public void commandLineWithTitle(final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    assertAll(outputFormats()
      .flatMap(outputFormat -> commands().map(command -> () -> {

        final String referenceFile = referenceFile(command, outputFormat);

        final Map<String, String> argsMap = new HashMap<>();
        argsMap.put("g", hsqldbProperties.toString());
        argsMap.put("schemas", ".*\\.(?!FOR_LINT).*");
        argsMap.put("infolevel", InfoLevel.maximum.name());
        argsMap.put("noinfo", Boolean.FALSE.toString());
        argsMap.put("title", "Database Design for Books and Publishers");

        assertThat(outputOf(commandlineExecution(connectionInfo,
                                                 command,
                                                 argsMap,
                                                 outputFormat)),
                   hasSameContentAndTypeAs(classpathResource(TITLE_OUTPUT
                                                             + referenceFile),
                                           outputFormat));

      })));
  }

  @BeforeEach
  public void copyResources()
    throws IOException
  {
    hsqldbProperties = copyResourceToTempFile("/hsqldb.INFORMATION_SCHEMA.config.properties");
  }

  protected Stream<String> commands()
  {
    return Arrays.asList("schema", "list").stream();
  }

  protected abstract Stream<OutputFormat> outputFormats();

  private String referenceFile(final String command,
                               final OutputFormat outputFormat)
  {
    final String referenceFile = String
      .format("commandLineWithTitle_%s.%s", command, outputFormat.getFormat());
    return referenceFile;
  }

}