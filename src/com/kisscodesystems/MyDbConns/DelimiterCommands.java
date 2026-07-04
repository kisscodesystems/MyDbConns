package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Connections.*;
import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.State.*;
import static com.kisscodesystems.MyDbConns.Validate.*;

/**
 * Command handlers for the per-database-type SQL delimiter: showing the current delimiter and
 * changing it. When no database type is currently selected, these prompt the user for one.
 */
final class DelimiterCommands {
  /**
   * Shows the delimiter for the active database type. If no database type is currently selected it
   * prompts for one from the console, validates it, and switches to it, then prints the delimiter
   * configured for that type (or an empty marker when unset).
   */
  static final void showDelimiter() {
    String dbtype = null;
    boolean fromConsole = false;
    if (!"".equals(dbType)) {
      fromConsole = false;
      dbtype = dbType;
    } else {
      fromConsole = true;
      dbtype =
          readline(NEW_LINE_STRING + MESSAGE_DATABASE_TYPE, APP_MAX_LENGTH_OF_INPUT).toLowerCase();
    }
    if (dbtype != null) {
      if (isValidDbType(dbtype, true)) {
        if (fromConsole) {
          dbType = dbtype;
          changePromptToTheActual();
        }
        if (DB_TYPE_MYSQL.equals(dbType)) {
          outprintln(
              NEW_LINE_STRING
                  + FOLD
                  + DB_TYPE_MYSQL
                  + MESSAGE_DELIMITER_HAS_BEEN_CHANGED_TO
                  + ("".equals(delimiterMysql) ? MESSAGE_EMPTY : delimiterMysql));
        } else if (DB_TYPE_ORACLE.equals(dbType)) {
          outprintln(
              NEW_LINE_STRING
                  + FOLD
                  + DB_TYPE_ORACLE
                  + MESSAGE_DELIMITER_HAS_BEEN_CHANGED_TO
                  + ("".equals(delimiterOracle) ? MESSAGE_EMPTY : delimiterOracle));
        } else if (DB_TYPE_MSSQL.equals(dbType)) {
          outprintln(
              NEW_LINE_STRING
                  + FOLD
                  + DB_TYPE_MSSQL
                  + MESSAGE_DELIMITER_HAS_BEEN_CHANGED_TO
                  + ("".equals(delimiterMssql) ? MESSAGE_EMPTY : delimiterMssql));
        } else if (DB_TYPE_DB2.equals(dbType)) {
          outprintln(
              NEW_LINE_STRING
                  + FOLD
                  + DB_TYPE_DB2
                  + MESSAGE_DELIMITER_HAS_BEEN_CHANGED_TO
                  + ("".equals(delimiterDb2) ? MESSAGE_EMPTY : delimiterDb2));
        } else {
          outprintln(
              NEW_LINE_STRING
                  + FOLD
                  + DB_TYPE_POSTGRESQL
                  + MESSAGE_DELIMITER_HAS_BEEN_CHANGED_TO
                  + ("".equals(delimiterPostgresql) ? MESSAGE_EMPTY : delimiterPostgresql));
        }
      }
    } else {
      throw systemexit("Error - dbtype is null, showDelimiter");
    }
    dbtype = null;
    fromConsole = false;
  }

  /** Shows the current delimiter once the connections file content is ready. */
  static final void executeCommandDelimiterShow() {
    if (isFileContentConnectionsOrigReady()) {
      showDelimiter();
    }
  }

  /**
   * Changes the delimiter for the active database type to the given value. If no database type is
   * currently selected it prompts for one from the console, validates it, and switches to it,
   * assigns the delimiter to that type, then shows the resulting delimiter.
   *
   * @param delimiter the new delimiter string to set for the database type
   */
  static final void executeCommandDelimiterChange(String delimiter) {
    if (isFileContentConnectionsOrigReady()) {
      String dbtype = null;
      boolean fromConsole = false;
      if (!"".equals(dbType)) {
        fromConsole = false;
        dbtype = dbType;
      } else {
        fromConsole = true;
        dbtype =
            readline(NEW_LINE_STRING + MESSAGE_DATABASE_TYPE, APP_MAX_LENGTH_OF_INPUT)
                .toLowerCase();
      }
      if (dbtype != null) {
        if (isValidDbType(dbtype, true)) {
          if (fromConsole) {
            dbType = dbtype;
            changePromptToTheActual();
          }
          if (DB_TYPE_MYSQL.equals(dbType)) {
            delimiterMysql = delimiter;
          } else if (DB_TYPE_ORACLE.equals(dbType)) {
            delimiterOracle = delimiter;
          } else if (DB_TYPE_MSSQL.equals(dbType)) {
            delimiterMssql = delimiter;
          } else if (DB_TYPE_DB2.equals(dbType)) {
            delimiterDb2 = delimiter;
          } else {
            delimiterPostgresql = delimiter;
          }
          showDelimiter();
        }
      } else {
        throw systemexit("Error - dbtype is null, executeCommandDelimiterChange");
      }
    }
  }
}
