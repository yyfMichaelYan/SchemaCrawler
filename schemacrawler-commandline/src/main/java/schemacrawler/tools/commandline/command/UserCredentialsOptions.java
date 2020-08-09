/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline.command;


import static sf.util.Utility.isBlank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import schemacrawler.tools.commandline.state.MultiUseUserCredentials;
import schemacrawler.tools.databaseconnector.UserCredentials;
import schemacrawler.SchemaCrawlerLogger;

/**
 * Options for the command-line.
 *
 * @author sfatehi
 */
public final class UserCredentialsOptions
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(UserCredentialsOptions.class.getName());


  public static class PasswordOptions
  {
    @Option(names = "--password:env",
            description = "Database password, from an environmental variable value",
            paramLabel = "<environment variable name>")
    private String passwordEnvironmentVariable;
    @Option(names = "--password:file",
            description = "Database password, read from a file",
            paramLabel = "<path to password file>")
    private Path passwordFile;
    @Option(names = "--password:prompt",
            interactive = true,
            description = "Database password, prompted from the console")
    private String passwordPrompted;
    @Option(names = {
      "--password"
    }, description = "Database password", paramLabel = "<password>")
    private String passwordProvided;

    /**
     * Get password from various sources, in order of precedence. The password
     * cannot be specified in more than one way.
     *
     * @return Password, can be null
     */
    String getPassword()
    {
      String password = getPasswordProvided();

      if (password == null)
      {
        password = getPasswordPrompted();
      }
      if (password == null)
      {
        password = getPasswordFromFile();
      }
      if (password == null)
      {
        password = getPasswordFromEnvironment();
      }

      return password;
    }

    private String getPasswordProvided()
    {
      return passwordProvided;
    }

    private String getPasswordPrompted()
    {
      return passwordPrompted;
    }

    private String getPasswordFromEnvironment()
    {
      if (isBlank(passwordEnvironmentVariable))
      {
        return null;
      }

      String passwordEnvironment = null;
      try
      {
        passwordEnvironment = System.getenv(passwordEnvironmentVariable);
      }
      catch (final Exception e)
      {
        throw new IllegalArgumentException(String.format(
          "Password could not be read from environmental variable <%s>",
          passwordEnvironmentVariable), e);
      }

      return passwordEnvironment;
    }

    private String getPasswordFromFile()
    {
      if (passwordFile == null)
      {
        return null;
      }

      String password = null;
      try
      {
        final List<String> lines = Files.readAllLines(passwordFile);
        if (!lines.isEmpty()) {password = lines.get(0);}
      }
      catch (final IOException e)
      {
        throw new IllegalArgumentException(String.format(
          "Password could not be read from file <%s>",
          passwordFile), e);
      }

      return password;
    }

  }


  @ArgGroup
  private PasswordOptions passwordOptions;
  @Spec
  private Model.CommandSpec spec;
  @Option(names = {
    "--user"
  }, description = "Database user name")
  private String user;

  public UserCredentials getUserCredentials()
  {
    return new MultiUseUserCredentials(user, getPassword());
  }

  private String getPassword()
  {
    if (passwordOptions == null)
    {
      return null;
    }

    try
    {
      return passwordOptions.getPassword();
    }
    catch (final Exception e)
    {
      throw new ParameterException(spec.commandLine(), "No password provided");
    }
  }

}
