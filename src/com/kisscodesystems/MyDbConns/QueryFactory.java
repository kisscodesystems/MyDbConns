package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Args.*;
import static com.kisscodesystems.MyDbConns.ConnectionCommands.*;
import static com.kisscodesystems.MyDbConns.Connections.*;
import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.DelimiterCommands.*;
import static com.kisscodesystems.MyDbConns.FileStore.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.Print.*;
import static com.kisscodesystems.MyDbConns.QueryStore.*;
import static com.kisscodesystems.MyDbConns.State.*;
import static com.kisscodesystems.MyDbConns.Utils.*;
import static com.kisscodesystems.MyDbConns.Validate.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

/**
 * Interactive per-connection SQL REPL ("query factory"). Resolves a dbtype and connection name,
 * opens a JDBC connection, and loops reading multi-line queries, dispatching sub-commands
 * (delimiter show/change, connection describe, help) and executing SELECTs versus other statements.
 */
final class QueryFactory {
  /**
   * Starts an interactive query factory session: validates that connections are loaded, resolves
   * the dbtype and connection name, checks that the connection exists, and then runs the session.
   *
   * @param d the requested dbtype, or empty to resolve from state or prompt
   * @param c the requested connection name, or empty to resolve from state or prompt
   * @param q an initial query to execute first, or empty to start by reading from the console
   */
  static final void factoryQuery(String d, String c, String q) {
    if (!isFileContentConnectionsOrigReady()) {
      return;
    }
    if (d == null || c == null || q == null) {
      throw systemexit("Error - one of these is null: d|c|q|us, factoryQuery");
    }
    String dbtype = resolveFactoryDbtype(d);
    if (!isValidDbType(dbtype, true)) {
      return;
    }
    String dbconn = resolveFactoryConnna(c);
    if (getConnnaPos(dbtype, dbconn) == -1) {
      outprintln(MESSAGE_YOUR_CONNECTION_DOES_NOT_EXIST);
      return;
    }
    runFactorySession(dbtype, dbconn, q);
  }

  /**
   * Resolves the dbtype to use for a factory session, preferring the explicit argument, then the
   * current dbType state, and finally prompting the user (lower-cased).
   *
   * @param d the explicitly requested dbtype, or empty to fall back
   * @return the resolved dbtype
   */
  private static final String resolveFactoryDbtype(String d) {
    if (!"".equals(d)) {
      return d;
    }
    if (!"".equals(dbType)) {
      return dbType;
    }
    return readline(NEW_LINE_STRING + MESSAGE_DATABASE_TYPE, APP_MAX_LENGTH_OF_INPUT).toLowerCase();
  }

  /**
   * Resolves the connection name to use for a factory session, preferring the explicit argument,
   * then the current dbConn state, and finally prompting the user.
   *
   * @param c the explicitly requested connection name, or empty to fall back
   * @return the resolved connection name
   */
  private static final String resolveFactoryConnna(String c) {
    if (!"".equals(c)) {
      return c;
    }
    if (!"".equals(dbConn)) {
      return dbConn;
    }
    return readline(NEW_LINE_STRING + MESSAGE_CONNECTION_NAME, APP_MAX_LENGTH_OF_INPUT);
  }

  /**
   * Runs the interactive session for a resolved connection: opens the JDBC connection, then loops
   * reading queries (or using the seeded initial query), handling exit, help, delimiter and
   * connection sub-commands, and executing statements, until the user exits. Always closes the
   * result set, statement, and connection on completion.
   *
   * @param dbtype the resolved dbtype
   * @param dbconn the resolved connection name
   * @param q an initial query to execute first, or empty to start by reading from the console
   */
  private static final void runFactorySession(String dbtype, String dbconn, String q) {
    dbType = dbtype;
    dbConn = dbconn;
    changePromptToTheActual();
    String prompt = PROMPT_ENDING;
    boolean firstQueryExecuted = "".equals(q);
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    String dbuser = getDbuser(dbtype, dbconn);
    char[] dbpass = getDbpass(dbtype, dbconn).toCharArray();
    String driver = getDriver(dbtype, dbconn);
    String connst = getConnst(dbtype, dbconn);
    try {
      Class.forName(driver);
      connection = DriverManager.getConnection(connst, dbuser, new String(dbpass));
      statement =
          connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      outprintln(MESSAGE_YOU_ARE_NOW_CONNECTED + dbType + SEP9 + dbConn);
      outprintln(MESSAGE_WELCOME_TO_QUERY_FACTORY);
      while (true) {
        String query;
        if (!firstQueryExecuted) {
          firstQueryExecuted = true;
          query = q;
        } else {
          query = readMultilineQuery(dbtype, prompt);
        }
        query = trimQueryString(query, getDelimiter(dbtype));
        if (query.startsWith(APP_QUERY_FILE_PREFIX)) {
          String fileName = query.substring(APP_QUERY_FILE_PREFIX.length(), query.length()).trim();
          if (isValidFilePath(fileName, false) && isExistingFile(fileName, false)) {
            query = readFileContent(fileName, false).trim();
          } else {
            query = "";
          }
        }
        if (query.startsWith("!")) {
          query = "select * from mycolischemarole . " + query.substring(1);
        }
        query = trimQueryString(query, getDelimiter(dbtype));
        String queryWithoutSpaces = query.replaceAll(SINGLE_SPACE, "");
        if (PROMPT_TO_UPPER_OR_EXIT.toLowerCase().equals(query.toLowerCase())) {
          break;
        } else if (query.equals(ARG_QUESTION_MARK) || query.equals(ARG_HELP)) {
          outprintln(MESSAGE_WELCOME_TO_QUERY_FACTORY);
          outprintln(MESSAGE_YOU_ARE_NOW_CONNECTED + dbType + SEP9 + dbConn);
        } else if (queryWithoutSpaces.equals(ARG_DELIMITER + ARG_SHOW)) {
          executeCommandDelimiterShow();
        } else if (queryWithoutSpaces.startsWith(ARG_DELIMITER + ARG_CHANGE)) {
          executeCommandDelimiterChange(
              queryWithoutSpaces.substring(
                  (ARG_DELIMITER + ARG_CHANGE).length(), queryWithoutSpaces.length()));
        } else if (queryWithoutSpaces.startsWith(ARG_CONNECTION + ARG_DESCRIBE)) {
          executeCommandConnectionDescribe();
        } else if (!"".equals(query)) {
          resultSet = executeFactoryStatement(statement, query, dbtype, resultSet);
        }
      }
      outprintln(MESSAGE_SUCCESSFULLY_EXITED_FROM_QUERY_FACTORY);
    } catch (Exception e) {
      outprintln(MESSAGE_UNABLE_TO_CONNECT);
      outprintln(FOLD + e.toString().trim());
    } finally {
      if (resultSet != null) {
        try {
          resultSet.close();
        } catch (Exception e) {
          outprintln(MESSAGE_UNABLE_TO_FREE_QUERY_FACTORY + e.toString().trim());
        }
      }
      if (statement != null) {
        try {
          statement.close();
        } catch (Exception e) {
          outprintln(MESSAGE_UNABLE_TO_FREE_QUERY_FACTORY + e.toString().trim());
        }
      }
      if (connection != null) {
        try {
          connection.close();
        } catch (Exception e) {
          outprintln(MESSAGE_UNABLE_TO_FREE_QUERY_FACTORY + e.toString().trim());
        }
      }
    }
  }

  /**
   * Reads a multi-line query from the console, continuing to read lines until the trimmed input
   * ends with the dbtype's delimiter, or reading a single line when the delimiter is empty.
   *
   * @param dbtype the dbtype whose delimiter terminates the query
   * @param prompt the prompt shown before the first line
   * @return the full multi-line query text entered by the user
   */
  private static final String readMultilineQuery(String dbtype, String prompt) {
    String query = "";
    boolean aReq = false;
    while (true) {
      if (!aReq) {
        aReq = true;
        query += CONSOLE.readLine(NEW_LINE_STRING + prompt);
      } else {
        query += NEW_LINE_STRING + CONSOLE.readLine("");
      }
      if ((!"".equals(getDelimiter(dbtype)) && query.trim().endsWith(getDelimiter(dbtype)))
          || "".equals(getDelimiter(dbtype))) {
        break;
      }
    }
    return query;
  }

  /**
   * Heuristically determines whether the query is a SELECT statement by checking
   * (case-insensitively) for the select keyword at the start or surrounded by spaces or newlines.
   *
   * @param query the query text to inspect
   * @return true if the query appears to be a SELECT statement, false otherwise
   */
  private static final boolean isSelectQuery(String query) {
    String q = query.toLowerCase();
    return q.startsWith(SELECT + SINGLE_SPACE)
        || q.contains(SINGLE_SPACE + SELECT + SINGLE_SPACE)
        || q.contains(NEW_LINE_STRING + SELECT + NEW_LINE_STRING)
        || q.contains(NEW_LINE_STRING + SELECT + SINGLE_SPACE)
        || q.contains(SINGLE_SPACE + SELECT + NEW_LINE_STRING);
  }

  /**
   * Executes a single query on the session statement: for SELECTs it closes any prior result set,
   * runs the query, and prints the formatted text result; for other statements it executes them and
   * prints the elapsed time. Exceptions are printed to the console.
   *
   * @param statement the session statement to execute on
   * @param query the query text to run
   * @param dbtype the dbtype (used for delimiter and result formatting)
   * @param resultSet the previous result set, closed before running a new SELECT
   * @return the new result set for a SELECT, or the passed-in result set for other statements
   */
  private static final ResultSet executeFactoryStatement(
      Statement statement, String query, String dbtype, ResultSet resultSet) {
    if (isSelectQuery(query)) {
      if (resultSet != null) {
        try {
          resultSet.close();
          resultSet = null;
        } catch (Exception e) {
          resultSet = null;
        }
      }
      try {
        Date startDate = new Date();
        resultSet = statement.executeQuery(query);
        String elapsedFormatted = calculateElapsed(new Date().getTime() - startDate.getTime());
        String resultTxt =
            constructResult(
                YES,
                resultSet,
                true,
                "",
                RESULT_FORMAT_TXT_VALUE,
                elapsedFormatted,
                null,
                dbtype,
                getDelimiter(dbtype));
        outprint(resultTxt);
      } catch (Exception e) {
        outprintln(NEW_LINE_STRING + e.toString().trim());
      }
    } else {
      try {
        Date startDate = new Date();
        statement.execute(query);
        String elapsedFormatted = calculateElapsed(new Date().getTime() - startDate.getTime());
        outprintln(FOLD + elapsedFormatted);
      } catch (Exception e) {
        outprintln(NEW_LINE_STRING + e.toString().trim());
      }
    }
    return resultSet;
  }

  /**
   * Starts a query factory session with no preset dbtype, connection, or initial query, resolving
   * everything interactively.
   */
  static final void executeCommandQueryFactory() {
    factoryQuery("", "", "");
  }

  /**
   * Starts a query factory session with no preset dbtype or connection, seeding it with an initial
   * query to execute first.
   *
   * @param query the initial query to execute at session start
   */
  static final void executeCommandQueryFactory(String query) {
    factoryQuery("", "", query);
  }

  /**
   * Starts a query factory session for the given dbtype and connection with no initial query.
   *
   * @param dbtype the dbtype to connect with
   * @param connna the connection name to connect with
   */
  static final void executeCommandQueryFactory(String dbtype, String connna) {
    factoryQuery(dbtype, connna, "");
  }

  /**
   * Starts a query factory session for the given dbtype and connection, seeding it with an initial
   * query to execute first.
   *
   * @param dbtype the dbtype to connect with
   * @param connna the connection name to connect with
   * @param query the initial query to execute at session start
   */
  static final void executeCommandQueryFactory(String dbtype, String connna, String query) {
    factoryQuery(dbtype, connna, query);
  }
}
