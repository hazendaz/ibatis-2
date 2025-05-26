/*
 * Copyright 2004-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibatis.common.jdbc;

import com.ibatis.common.resources.Resources;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Tool to run database scripts.
 */
public class ScriptRunner {

  /** The Constant DEFAULT_DELIMITER. */
  private static final String DEFAULT_DELIMITER = ";";

  /** The connection. */
  private Connection connection;

  /** The driver. */
  private String driver;

  /** The url. */
  private String url;

  /** The username. */
  private String username;

  /** The password. */
  private String password;

  /** The stop on error. */
  private boolean stopOnError;

  /** The auto commit. */
  private boolean autoCommit;

  /** The log writer. */
  private PrintWriter logWriter = new PrintWriter(System.out);

  /** The error log writer. */
  private PrintWriter errorLogWriter = new PrintWriter(System.err);

  /** The delimiter. */
  private String delimiter = DEFAULT_DELIMITER;

  /** The full line delimiter. */
  private boolean fullLineDelimiter;

  /**
   * Default constructor.
   *
   * @param connection
   *          the connection
   * @param autoCommit
   *          the auto commit
   * @param stopOnError
   *          the stop on error
   */
  public ScriptRunner(Connection connection, boolean autoCommit, boolean stopOnError) {
    this.connection = connection;
    this.autoCommit = autoCommit;
    this.stopOnError = stopOnError;
  }

  /**
   * Sets the delimiter.
   *
   * @param delimiter
   *          the delimiter
   * @param fullLineDelimiter
   *          the full line delimiter
   */
  public void setDelimiter(String delimiter, boolean fullLineDelimiter) {
    this.delimiter = delimiter;
    this.fullLineDelimiter = fullLineDelimiter;
  }

  /**
   * Instantiates a new script runner.
   *
   * @param driver
   *          the driver
   * @param url
   *          the url
   * @param username
   *          the username
   * @param password
   *          the password
   * @param autoCommit
   *          the auto commit
   * @param stopOnError
   *          the stop on error
   */
  public ScriptRunner(String driver, String url, String username, String password, boolean autoCommit,
      boolean stopOnError) {
    this.driver = driver;
    this.url = url;
    this.username = username;
    this.password = password;
    this.autoCommit = autoCommit;
    this.stopOnError = stopOnError;
  }

  /**
   * Setter for logWriter property.
   *
   * @param logWriter
   *          - the new value of the logWriter property
   */
  public void setLogWriter(PrintWriter logWriter) {
    this.logWriter = logWriter;
  }

  /**
   * Setter for errorLogWriter property.
   *
   * @param errorLogWriter
   *          - the new value of the errorLogWriter property
   */
  public void setErrorLogWriter(PrintWriter errorLogWriter) {
    this.errorLogWriter = errorLogWriter;
  }

  /**
   * Runs an SQL script (read in using the Reader parameter).
   *
   * @param reader
   *          - the source of the script
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws SQLException
   *           the SQL exception
   */
  public void runScript(Reader reader) throws IOException, SQLException {
    try {
      if (connection == null) {
        DriverManager.registerDriver((Driver) Resources.instantiate(driver));
        Connection conn = DriverManager.getConnection(url, username, password);
        try {
          if (conn.getAutoCommit() != autoCommit) {
            conn.setAutoCommit(autoCommit);
          }
          runScript(conn, reader);
        } finally {
          conn.close();
        }
      } else {
        boolean originalAutoCommit = connection.getAutoCommit();
        try {
          if (originalAutoCommit != this.autoCommit) {
            connection.setAutoCommit(this.autoCommit);
          }
          runScript(connection, reader);
        } finally {
          connection.setAutoCommit(originalAutoCommit);
        }
      }
    } catch (IOException | SQLException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error running script.  Cause: " + e, e);
    }
  }

  /**
   * Runs an SQL script (read in using the Reader parameter) using the connection passed in.
   *
   * @param conn
   *          - the connection to use for the script
   * @param reader
   *          - the source of the script
   *
   * @throws IOException
   *           if there is an error reading from the Reader
   * @throws SQLException
   *           if any SQL errors occur
   */
  private void runScript(Connection conn, Reader reader) throws IOException, SQLException {
    StringBuilder command = null;
    try {
      LineNumberReader lineReader = new LineNumberReader(reader);
      String line = null;
      while ((line = lineReader.readLine()) != null) {
        if (command == null) {
          command = new StringBuilder();
        }
        String trimmedLine = line.trim();
        if (trimmedLine.startsWith("--")) {
          println(trimmedLine);
        } else if (trimmedLine.isEmpty() || trimmedLine.startsWith("//")) {
          // Do nothing
        } else if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
          // Do nothing
        } else if (!fullLineDelimiter && trimmedLine.endsWith(getDelimiter())
            || fullLineDelimiter && trimmedLine.equals(getDelimiter())) {
          command.append(line.substring(0, line.lastIndexOf(getDelimiter())));
          command.append(" ");
          Statement statement = conn.createStatement();

          println(command);

          boolean hasResults = false;
          if (stopOnError) {
            hasResults = statement.execute(command.toString());
          } else {
            try {
              statement.execute(command.toString());
            } catch (SQLException e) {
              e.fillInStackTrace();
              printlnError("Error executing: " + command);
              printlnError(e);
            }
          }

          if (autoCommit && !conn.getAutoCommit()) {
            conn.commit();
          }

          ResultSet rs = statement.getResultSet();
          if (hasResults && rs != null) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            for (int i = 0; i < cols; i++) {
              String name = md.getColumnLabel(i + 1);
              print(name + "\t");
            }
            println("");
            while (rs.next()) {
              for (int i = 0; i < cols; i++) {
                String value = rs.getString(i + 1);
                print(value + "\t");
              }
              println("");
            }
          }

          command = null;
          try {
            statement.close();
          } catch (Exception e) {
            // Ignore to workaround a bug in Jakarta DBCP
          }
          Thread.yield();
        } else {
          command.append(line);
          command.append(" ");
        }
      }
      if (!autoCommit) {
        conn.commit();
      }
    } catch (IOException | SQLException e) {
      e.fillInStackTrace();
      printlnError("Error executing: " + command);
      printlnError(e);
      throw e;
    } finally {
      conn.rollback();
      flush();
    }
  }

  /**
   * Gets the delimiter.
   *
   * @return the delimiter
   */
  private String getDelimiter() {
    return delimiter;
  }

  /**
   * Prints the.
   *
   * @param o
   *          the o
   */
  private void print(Object o) {
    if (logWriter != null) {
      System.out.print(o);
    }
  }

  /**
   * Println.
   *
   * @param o
   *          the o
   */
  private void println(Object o) {
    if (logWriter != null) {
      logWriter.println(o);
    }
  }

  /**
   * Println error.
   *
   * @param o
   *          the o
   */
  private void printlnError(Object o) {
    if (errorLogWriter != null) {
      errorLogWriter.println(o);
    }
  }

  /**
   * Flush.
   */
  private void flush() {
    if (logWriter != null) {
      logWriter.flush();
    }
    if (errorLogWriter != null) {
      errorLogWriter.flush();
    }
  }

}
