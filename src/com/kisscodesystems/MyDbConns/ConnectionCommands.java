package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Connections.*;
import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.Crypto.*;
import static com.kisscodesystems.MyDbConns.FileStore.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.Print.*;
import static com.kisscodesystems.MyDbConns.State.*;
import static com.kisscodesystems.MyDbConns.Utils.*;
import static com.kisscodesystems.MyDbConns.Validate.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implements the connection CRUD commands (add, change, delete, deleteall, use, describe, test,
 * load) of the MyDbConns SQL client. Prompts and validates user input and inserts, updates or
 * removes 6-field connection records (dbtype, connna, dbuser, dbpass, driver, connst) in the
 * decrypted in-memory connections-file content.
 */
final class ConnectionCommands {
  /**
   * Adds a new connection or changes an existing one for the given connection name. Resolves the
   * database type (from state or by prompting), validates it, loads the current values as defaults
   * when changing, enforces the maximum connection count when adding, then delegates to {@link
   * #enterAndSaveConnection}.
   *
   * @param connnaIn the name of the connection to change, or an empty string to add a new one
   */
  static final void connectionAddOrChange(String connnaIn) {
    if (!isFileContentConnectionsOrigReady()) {
      return;
    }
    if (connnaIn == null) {
      throw systemexit("Error - connnaIn is null, connectionAddOrChange");
    }
    String dbtype;
    if (!"".equals(dbType)) {
      dbtype = dbType;
    } else {
      dbtype =
          readline(NEW_LINE_STRING + MESSAGE_DATABASE_TYPE, APP_MAX_LENGTH_OF_INPUT).toLowerCase();
    }
    if (dbtype == null) {
      throw systemexit("Error - dbtype is null, connectionAddOrChange");
    }
    if (!isValidDbType(dbtype, true)) {
      return;
    }
    int currPos = getConnnaPos(dbtype, connnaIn);
    String connnaDefault = null;
    String dbuserDefault = null;
    char[] dbpassDefault = null;
    String driverDefault = null;
    String connstDefault = null;
    if (!"".equals(connnaIn)) {
      if (isConnectionInUse(dbtype, connnaIn, false)) {
        return;
      }
      if (currPos == -1) {
        outprintln(MESSAGE_YOUR_CONNECTION_DOES_NOT_EXIST);
        return;
      }
      connnaDefault = connnaIn;
      dbuserDefault = getDbuser(dbtype, connnaIn);
      dbpassDefault = getDbpass(dbtype, connnaIn).toCharArray();
      driverDefault = getDriver(dbtype, connnaIn);
      connstDefault = getConnst(dbtype, connnaIn);
      outprintln(MESSAGE_CHANGE_CONN_PROPERTY_BEHAVIOUR);
    } else if (getNumOfAllConnections() >= APP_MAX_NUM_OF_CONNECTIONS) {
      outprintln(MESSAGE_YOU_HAVE_REACHED_THE_TOP_OF_THE_COUNT_OF_STORABLE_CONNECTIONS);
      return;
    }
    enterAndSaveConnection(
        dbtype,
        connnaIn,
        currPos,
        connnaDefault,
        dbuserDefault,
        dbpassDefault,
        driverDefault,
        connstDefault);
  }

  /**
   * Prompts the user for each connection property (name, user, password, driver, connection
   * string), applying the supplied defaults when the input is left empty, validates them, tests the
   * connection, and on confirmation delegates to {@link #saveConnection}.
   *
   * @param dbtype the database type of the connection
   * @param connnaIn the original connection name being changed, or an empty string when adding
   * @param currPos the position of the existing connection block in the content, or -1 when adding
   * @param connnaDefault the current connection name to use as default, or null when adding
   * @param dbuserDefault the current database user to use as default, or null when adding
   * @param dbpassDefault the current database password to use as default, or null when adding
   * @param driverDefault the current driver to use as default, or null when adding
   * @param connstDefault the current connection string to use as default, or null when adding
   */
  private static final void enterAndSaveConnection(
      String dbtype,
      String connnaIn,
      int currPos,
      String connnaDefault,
      String dbuserDefault,
      char[] dbpassDefault,
      String driverDefault,
      String connstDefault) {
    outprintln(MESSAGE_ENTER_THE_PROPERTIES_OF_THE_CONNECTION);
    String connna =
        readline(
            MESSAGE_CONNECTION_NAME
                + (connnaDefault != null ? ("[" + connnaDefault + "]" + SPACE_CHAR) : ""),
            APP_MAX_LENGTH_OF_INPUT);
    if (connna == null) {
      throw systemexit("Error - connna is null, connectionAddOrChange");
    }
    if (connna.lastIndexOf(SPACE_CHAR) != -1) {
      outprintln(MESSAGE_THE_NAME_OF_THE_CONNECTION_CANNOT_CONTAIN_SPACE_CHAR);
      return;
    }
    if (connnaDefault != null && connna.equals("")) {
      connna = connnaDefault;
    }
    if (connna.length() == 0) {
      outprintln(MESSAGE_THE_NAME_OF_THE_CONNECTION_HAS_TO_BE_AT_LEAST_ONE_CHAR);
      return;
    }
    if (getConnnaPos(dbtype, connna) != -1 && !connnaIn.equals(connna)) {
      outprintln(MESSAGE_THIS_CONNECTION_NAME_IN_DATABASE_TYPE_ALREADY_EXISTS);
      return;
    }
    String dbuser =
        readline(
            MESSAGE_DATABASE_USER
                + (dbuserDefault != null
                    ? ("[now "
                        + dbuserDefault
                        + SEP9
                        + MESSAGE_CHANGE_CONN_USER_OR_PASSWORD_BEHAVIOUR
                        + "]"
                        + SPACE_CHAR)
                    : ""),
            APP_MAX_LENGTH_OF_INPUT);
    if (dbuser == null) {
      throw systemexit("Error - dbuser is null, connectionAddOrChange");
    }
    if (dbuser.length() == 0 && dbpassDefault != null) {
      dbuser = dbuserDefault;
    } else {
      dbuser = collapseEmptySentinel(dbuser);
    }
    if (dbuser == null) {
      throw systemexit("Error - dbuser is null, connectionAddOrChange");
    }
    char[] dbpass =
        readpassword(
            MESSAGE_DATABASE_PASSWORD
                + (dbpassDefault != null
                    ? ("[" + MESSAGE_CHANGE_CONN_USER_OR_PASSWORD_BEHAVIOUR + "]" + SPACE_CHAR)
                    : ""));
    if (dbpass == null) {
      throw systemexit("Error - dbpass is null, connectionAddOrChange");
    }
    if (dbpass.length == 0 && dbpassDefault != null) {
      dbpass = dbpassDefault;
    } else {
      dbpass = collapseEmptySentinel(dbpass);
    }
    String driver =
        readline(
            MESSAGE_DATABASE_DRIVER
                + (driverDefault != null ? ("[" + driverDefault + "]" + SPACE_CHAR) : ""),
            APP_MAX_LENGTH_OF_INPUT);
    if (driver == null) {
      throw systemexit("Error - driver is null, connectionAddOrChange");
    }
    if (driverDefault != null && driver.equals("")) {
      driver = driverDefault;
    }
    if (driver.length() == 0) {
      outprintln(MESSAGE_THE_DRIVER_CANNOT_BE_EMPTY);
      return;
    }
    if (!isValidDriver(driver, dbtype)) {
      return;
    }
    String connst =
        readline(
            MESSAGE_CONNECTION_STRING
                + (connstDefault != null ? ("[" + connstDefault + "]" + SPACE_CHAR) : ""),
            APP_MAX_LENGTH_OF_INPUT);
    if (connst == null) {
      throw systemexit("Error - connst is null, connectionAddOrChange");
    }
    if (connstDefault != null && connst.equals("")) {
      connst = connstDefault;
    }
    if (connst.length() == 0) {
      outprintln(MESSAGE_THE_CONNECTION_STRING_CANNOT_BE_EMPTY);
      return;
    }
    testConnection(dbuser, new String(dbpass), driver, connst);
    if (!readYesElseAnything(MESSAGE_SAVE_CONNECTION, MESSAGE_CONNECTION_HAS_NOT_BEEN_SAVED)) {
      return;
    }
    saveConnection(
        dbtype,
        connnaIn,
        currPos,
        connna,
        dbuser,
        dbpass,
        driver,
        connst,
        connnaDefault,
        dbuserDefault,
        dbpassDefault,
        driverDefault,
        connstDefault);
  }

  /**
   * Writes the entered connection into the in-memory content and persists it. When changing an
   * existing connection it shifts the surrounding content to make room for the size difference;
   * when adding it inserts at the start of the connection list. Inserts the connection block, saves
   * the file, and updates the active connection name when the changed connection is the one in use.
   *
   * @param dbtype the database type of the connection
   * @param connnaIn the original connection name being changed, or an empty string when adding
   * @param currPos the position of the existing connection block in the content, or -1 when adding
   * @param connna the new connection name
   * @param dbuser the new database user
   * @param dbpass the new database password
   * @param driver the new driver
   * @param connst the new connection string
   * @param connnaDefault the previous connection name, or null when adding
   * @param dbuserDefault the previous database user, or null when adding
   * @param dbpassDefault the previous database password, or null when adding
   * @param driverDefault the previous driver, or null when adding
   * @param connstDefault the previous connection string, or null when adding
   */
  private static final void saveConnection(
      String dbtype,
      String connnaIn,
      int currPos,
      String connna,
      String dbuser,
      char[] dbpass,
      String driver,
      String connst,
      String connnaDefault,
      String dbuserDefault,
      char[] dbpassDefault,
      String driverDefault,
      String connstDefault) {
    int posToInsert;
    if (currPos != -1) {
      posToInsert = currPos;
      if (connnaDefault == null
          || dbuserDefault == null
          || dbpassDefault == null
          || driverDefault == null
          || connstDefault == null) {
        throw systemexit("Error - a default connection attribute is null, connectionAddOrChange");
      }
      int currentContentLength =
          dbtype.length()
              + 1
              + connnaDefault.length()
              + 1
              + dbuserDefault.length()
              + 1
              + dbpassDefault.length
              + 1
              + driverDefault.length()
              + 1
              + connstDefault.length()
              + 1;
      int newContentLength =
          dbtype.length()
              + 1
              + connna.length()
              + 1
              + dbuser.length()
              + 1
              + dbpass.length
              + 1
              + driver.length()
              + 1
              + connst.length()
              + 1;
      int toMoveDiff = newContentLength - currentContentLength;
      if (toMoveDiff != 0) {
        shiftFileContent(posToInsert + currentContentLength, toMoveDiff);
      }
    } else {
      posToInsert = getFirstNewLineAndZeroCharIndex() + 1;
    }
    if (posToInsert <= 0) {
      throw systemexit("Error - posToInsert is not greater than 0, connectionAddOrChange");
    }
    insertConnectionBlock(dbtype, connna, dbuser, dbpass, driver, connst, posToInsert);
    if (saveFile()) {
      outprintln(MESSAGE_CONNECTION_HAS_BEEN_SAVED_SUCCESSFULLY);
      if (dbtype.equals(dbType) && connnaIn.equals(dbConn)) {
        setDbConn(connna);
      }
    }
  }

  /**
   * Inserts the six attributes of a single connection (dbtype, connna, dbuser, dbpass, driver,
   * connst) into the in-memory connections content at the given position, each followed by its
   * separator, advancing the insert position after every attribute.
   *
   * @param dbtype the database type
   * @param connna the connection name
   * @param dbuser the database user
   * @param dbpass the database password
   * @param driver the driver
   * @param connst the connection string
   * @param posToInsert the position in the content at which to start inserting
   */
  private static final void insertConnectionBlock(
      String dbtype,
      String connna,
      String dbuser,
      char[] dbpass,
      String driver,
      String connst,
      int posToInsert) {
    insertAnAttributeIntoFileContentConnectionsOrig(dbtype, posToInsert);
    posToInsert = posToInsert + dbtype.length() + 1;
    insertAnAttributeIntoFileContentConnectionsOrig(connna, posToInsert);
    posToInsert = posToInsert + connna.length() + 1;
    insertAnAttributeIntoFileContentConnectionsOrig(dbuser, posToInsert);
    posToInsert = posToInsert + dbuser.length() + 1;
    insertAnAttributeIntoFileContentConnectionsOrig(dbpass, posToInsert);
    posToInsert = posToInsert + dbpass.length + 1;
    insertAnAttributeIntoFileContentConnectionsOrig(driver, posToInsert);
    posToInsert = posToInsert + driver.length() + 1;
    insertAnAttributeIntoFileContentConnectionsOrig(connst, posToInsert);
  }

  /**
   * Returns an empty string when the given value equals the leading characters of the "empty"
   * sentinel message, otherwise returns the value unchanged. Used to let the user explicitly clear
   * a field by typing the sentinel text.
   *
   * @param value the entered value to check against the empty sentinel
   * @return an empty string if the value matches the sentinel prefix, otherwise the original value
   */
  private static final String collapseEmptySentinel(String value) {
    for (int i = 0; i < value.length(); i++) {
      if (i >= MESSAGE_EMPTY.length() || value.charAt(i) != MESSAGE_EMPTY.charAt(i)) {
        return value;
      }
    }
    return "";
  }

  /**
   * Character-array overload of {@link #collapseEmptySentinel(String)}. Returns an empty char array
   * when the given value equals the leading characters of the "empty" sentinel message, otherwise
   * returns the value unchanged.
   *
   * @param value the entered value to check against the empty sentinel
   * @return an empty char array if the value matches the sentinel prefix, otherwise the original
   *     value
   */
  private static final char[] collapseEmptySentinel(char[] value) {
    for (int i = 0; i < value.length; i++) {
      if (i >= MESSAGE_EMPTY.length() || value[i] != MESSAGE_EMPTY.charAt(i)) {
        return value;
      }
    }
    return new char[0];
  }

  /**
   * Removes the connection block at the given position from the in-memory content by locating the
   * end of its sixth field and shifting the following content over it. Does nothing if the
   * connection is currently in use.
   *
   * @param dbtype the database type of the connection
   * @param connna the connection name
   * @param connPos the position of the connection block in the content
   * @return true if the connection block was removed, false otherwise (e.g. when it is in use)
   */
  static final boolean deleteConnection(String dbtype, String connna, int connPos) {
    boolean success = false;
    if (isFileContentConnectionsOrigReady()) {
      if (connPos > -1) {
        int newLineCounter = 0;
        int nextConnPos = -1;
        if (!isConnectionInUse(dbtype, connna, false)) {
          for (int i = connPos; i < fileContentConnectionsOrig.length; i++) {
            if (fileContentConnectionsOrig[i] == NEW_LINE_CHAR) {
              newLineCounter++;
            }
            if (newLineCounter == 6) {
              nextConnPos = i + 1;
              break;
            }
          }
          if (nextConnPos - connPos > -1) {
            int toMoveFromPos = nextConnPos;
            int toMoveDiff = connPos - nextConnPos;
            if (toMoveDiff != 0) {
              shiftFileContent(toMoveFromPos, toMoveDiff);
            }
            toMoveFromPos = 0;
            toMoveDiff = 0;
            success = true;
          } else {
            throw systemexit("Error - nextConnPos - connPos is negative, deleteConnection");
          }
        }
        newLineCounter = 0;
        nextConnPos = 0;
      } else {
        throw systemexit("Error - connPos is negative, deleteConnection");
      }
    }
    return success;
  }

  /**
   * Interactive delete command. Resolves the database type and connection name (from state or by
   * prompting when not supplied), asks the user for confirmation, then removes the connection via
   * {@link #deleteConnection(String, String, int)}, saves the file and clears the active connection
   * when the deleted one was in use.
   *
   * @param connna the connection name to delete, or an empty string to prompt for it
   */
  static final void deleteConnection(String connna) {
    if (isFileContentConnectionsOrigReady()) {
      if (connna != null) {
        String dbtype = null;
        String dbconn = null;
        int connPos = -1;
        if (!"".equals(dbType)) {
          dbtype = dbType;
        } else {
          dbtype =
              readline(NEW_LINE_STRING + MESSAGE_DATABASE_TYPE, APP_MAX_LENGTH_OF_INPUT)
                  .toLowerCase();
        }
        if (isValidDbType(dbtype, true)) {
          if ("".equals(connna)) {
            if (!"".equals(dbConn)) {
              dbconn = dbConn;
            } else {
              dbconn = readline(MESSAGE_CONNECTION_NAME, APP_MAX_LENGTH_OF_INPUT);
            }
          } else {
            dbconn = connna;
          }
          connPos = getConnnaPos(dbtype, dbconn);
          if (connPos != -1) {
            if (readYesElseAnything(
                MESSAGE_ARE_YOU_SURE_DELETE_CONNECTION, MESSAGE_YOUR_CONNECTION_WONT_BE_DELETED)) {
              if (deleteConnection(dbtype, dbconn, connPos)) {
                if (saveFile()) {
                  outprintln(MESSAGE_YOUR_CONNECTION_HAS_BEEN_DELETED_SUCCESSFULLY);
                  if (dbconn.equals(dbConn) && dbtype.equals(dbType)) {
                    setDbConn("");
                  }
                }
              }
            }
          } else {
            outprintln(MESSAGE_YOUR_CONNECTION_DOES_NOT_EXIST);
          }
        }
        dbtype = null;
        dbconn = null;
        connPos = 0;
      } else {
        throw systemexit("Error - connna is null, deleteConnection");
      }
    }
  }

  /**
   * Deletes all connections of the given database type after user confirmation. Iterates over every
   * connection of that type, skips and reports the ones currently in use, deletes the rest, and
   * saves the file when at least one connection was deleted.
   *
   * @param dbtype the database type whose connections should be deleted
   */
  static final void deleteConnectionsByDbType(String dbtype) {
    if (isValidDbType(dbtype, true)) {
      int counter = 0;
      int counterDeleted = 0;
      int connPos = -1;
      outprintln(NEW_LINE_STRING + FOLD + dbtype);
      if (readYesElseAnything(
          MESSAGE_ARE_YOU_SURE_DELETE_CONNECTIONS_BY_DB_TYPE,
          MESSAGE_YOUR_CONNECTIONS_WONT_BE_DELETED)) {
        HashMap<String, ArrayList<Integer>> connections = getConnectionsByDbtype(dbtype, true);
        if (connections != null) {
          for (HashMap.Entry<String, ArrayList<Integer>> connection : connections.entrySet()) {
            if (connection != null) {
              if (connection.getKey() != null) {
                if (connection.getValue() != null) {
                  if (counter == 0) {
                    outprintln("");
                  }
                  counter++;
                  if (connection.getValue().size() > 0) {
                    outprintln(
                        FOLD
                            + connection.getKey()
                            + SPACE_CHAR
                            + MESSAGE_THIS_CONNECTION_IS_IN_USE
                            + joinArrayListInteger(connection.getValue(), SEP1));
                  } else {
                    connPos = getConnnaPos(dbtype, connection.getKey());
                    if (connPos != -1) {
                      if (deleteConnection(dbtype, connection.getKey(), connPos)) {
                        counterDeleted++;
                        outprintln(
                            FOLD + connection.getKey() + MESSAGE_CONNECTION_HAS_BEEN_DELETED);
                      } else {
                        throw systemexit(
                            "Error - deleteConnection is not successful,"
                                + " deleteConnectionsByDbType");
                      }
                    } else {
                      throw systemexit("Error - connPos is -1, deleteConnectionsByDbType");
                    }
                  }
                } else {
                  throw systemexit("Error - connectionValue is null, deleteConnectionsByDbType");
                }
              } else {
                throw systemexit("Error - connectionKey is null, deleteConnectionsByDbType");
              }
            } else {
              throw systemexit("Error - connection is null, deleteConnectionsByDbType");
            }
          }
          if (counterDeleted > 0) {
            if (saveFile()) {
              outprintln(MESSAGE_ALL_CONNECTIONS_HAVE_BEEN_HANDELED);
            }
          } else {
            outprintln(MESSAGE_NO_CONNECTIONS_HAVE_BEEN_HANDELED);
          }
        } else {
          throw systemexit("Error - connections is null, deleteConnectionsByDbType");
        }
        connections = null;
      }
      counter = 0;
      counterDeleted = 0;
      connPos = 0;
    }
  }

  /**
   * Selects the given connection as the active one. Resolves the database type (from the argument,
   * state, or by prompting), validates it, and if the connection exists sets it as the current
   * database type and connection and updates the prompt; otherwise reports that it does not exist.
   *
   * @param d the database type, or an empty string to resolve it from state or by prompting
   * @param c the connection name to use
   */
  static final void useConnection(String d, String c) {
    if (isFileContentConnectionsOrigReady()) {
      if (d != null && c != null) {
        String dbtype = null;
        String connna = null;
        int connPos = -1;
        if ("".equals(d)) {
          if (!"".equals(dbType)) {
            dbtype = dbType;
          } else {
            dbtype =
                readline(NEW_LINE_STRING + MESSAGE_DATABASE_TYPE, APP_MAX_LENGTH_OF_INPUT)
                    .toLowerCase();
          }
        } else {
          dbtype = d;
        }
        if (isValidDbType(dbtype, true)) {
          connna = c;
          connPos = getConnnaPos(dbtype, connna);
          if (connPos != -1) {
            dbType = dbtype;
            dbConn = connna;
            changePromptToTheActual();
          } else {
            outprintln(MESSAGE_YOUR_CONNECTION_DOES_NOT_EXIST);
          }
        }
      } else {
        throw systemexit("Error - one of these is null: d|c, useConnection");
      }
    }
  }

  /**
   * Prints the properties of a connection (database type, name, user, hidden password, driver and
   * connection string) and whether it is in use. Resolves the database type and connection name
   * from state or by prompting when not supplied, and reports if the connection does not exist.
   *
   * @param connna the connection name to describe, or an empty string to prompt for it
   */
  static final void describeConnection(String connna) {
    if (isFileContentConnectionsOrigReady()) {
      if (connna != null) {
        String dbtype = null;
        String dbconn = null;
        if (!"".equals(dbType)) {
          dbtype = dbType;
        } else {
          dbtype =
              readline(NEW_LINE_STRING + MESSAGE_DATABASE_TYPE, APP_MAX_LENGTH_OF_INPUT)
                  .toLowerCase();
        }
        if (isValidDbType(dbtype, true)) {
          if ("".equals(connna)) {
            if (!"".equals(dbConn)) {
              dbconn = dbConn;
            } else {
              dbconn = readline(NEW_LINE_STRING + MESSAGE_CONNECTION_NAME, APP_MAX_LENGTH_OF_INPUT);
            }
          } else {
            dbconn = connna;
          }
          if (getConnnaPos(dbtype, dbconn) != -1) {
            outprintln(MESSAGE_THE_PROPERTIES_OF_YOUR_CONNECTION);
            outprintln(MESSAGE_DATABASE_TYPE + dbtype);
            outprintln(MESSAGE_CONNECTION_NAME + dbconn);
            outprintln(MESSAGE_DATABASE_USER + getDbuser(dbtype, dbconn));
            outprintln(MESSAGE_DATABASE_PASSWORD + MESSAGE_HIDDEN);
            outprintln(MESSAGE_DATABASE_DRIVER + getDriver(dbtype, dbconn));
            outprintln(MESSAGE_CONNECTION_STRING + getConnst(dbtype, dbconn));
            isConnectionInUse(dbtype, dbconn, true);
          } else {
            outprintln(MESSAGE_YOUR_CONNECTION_DOES_NOT_EXIST);
          }
        }
        dbtype = null;
        dbconn = null;
      } else {
        throw systemexit("Error - connna is null, describeConnection");
      }
    }
  }

  /** Lists the connections that are currently active (in use). */
  static final void executeCommandConnectionListActive() {
    listConnections(TYPE_TO_LIST_ACTIVE);
  }

  /** Lists the connections that are currently inactive (not in use). */
  static final void executeCommandConnectionListInactive() {
    listConnections(TYPE_TO_LIST_INACTIVE);
  }

  /** Lists all connections regardless of whether they are active or inactive. */
  static final void executeCommandConnectionListall() {
    listConnections(TYPE_TO_LIST_ALL);
  }

  /** Handles the add command by invoking {@link #connectionAddOrChange} with an empty name. */
  static final void executeCommandConnectionAdd() {
    connectionAddOrChange("");
  }

  /**
   * Loads (imports) connections from an external file. Validates the file path and existence, asks
   * the user to confirm the format and the append, checks that the line count is a multiple of six,
   * then loads each 6-line connection block via {@link #loadOneConnection}, stopping when the
   * maximum connection count is reached, and saves the file with a summary message.
   *
   * @param fileName the path of the file to import connections from
   */
  static final void executeCommandConnectionLoad(String fileName) {
    if (!isFileContentConnectionsOrigReady()) {
      return;
    }
    int connectionsInitialCount = getNumOfAllConnections();
    if (connectionsInitialCount >= APP_MAX_NUM_OF_CONNECTIONS) {
      outprintln(MESSAGE_YOU_HAVE_REACHED_THE_TOP_OF_THE_COUNT_OF_STORABLE_CONNECTIONS);
      return;
    }
    if (!(isValidFilePath(fileName, true) && isExistingFile(fileName, true))) {
      return;
    }
    if (!readYesElseAnything(MESSAGE_IS_FILE_GOOD_FORMATTED, MESSAGE_FILE_WONT_BE_USED)) {
      return;
    }
    String contentString = readFileContent(fileName, false);
    if (contentString == null) {
      throw systemexit("Error - contentString is null, executeCommandConnectionLoad");
    }
    String[] contentArray = contentString.split(NEW_LINE_STRING);
    if (contentArray == null) {
      throw systemexit("Error - contentArray is null, executeCommandConnectionLoad");
    }
    if (contentArray.length % 6 != 0) {
      outprintln(MESSAGE_COUNT_OF_LINES_OF_FILE_IS_NOT_THE_EXPECTED);
      return;
    }
    if (!readYesElseAnything(
        MESSAGE_APPEND_THIS_FILE_INTO_THE_END_OF_CURRENT_CONNECTIONS,
        MESSAGE_CONNECTIONS_WILL_NOT_BE_IMPORTED)) {
      return;
    }
    int counter = 0;
    int counterLoaded = 0;
    for (int i = 0; i < contentArray.length; i += 6) {
      if (counter == 0) {
        outprintln("");
      }
      counter++;
      if (loadOneConnection(
          contentArray[i],
          contentArray[i + 1],
          contentArray[i + 2],
          contentArray[i + 3],
          contentArray[i + 4],
          contentArray[i + 5])) {
        counterLoaded++;
        if (connectionsInitialCount + counterLoaded >= APP_MAX_NUM_OF_CONNECTIONS) {
          outprintln(MESSAGE_YOU_HAVE_REACHED_THE_TOP_OF_THE_COUNT_OF_STORABLE_CONNECTIONS);
          break;
        }
      }
    }
    if (counterLoaded == 0) {
      outprintln(MESSAGE_NO_CONNECTIONS_HAVE_BEEN_LOADED);
      return;
    }
    if (saveFile()) {
      if (counter == counterLoaded) {
        outprintln(MESSAGE_ALL_OF_THE_CONNECTIONS_HAVE_BEEN_LOADED + counterLoaded);
      } else if (counterLoaded == 1) {
        outprintln(MESSAGE_CONNECTION_HAVE_BEEN_LOADED);
      } else {
        outprintln(NEW_LINE_STRING + FOLD + counterLoaded + MESSAGE_CONNECTIONS_HAVE_BEEN_LOADED);
      }
    }
  }

  /**
   * Validates and inserts a single connection read from an import file. Reports progress, rejects
   * invalid database types, names containing spaces, empty names, already existing connections, and
   * empty drivers or connection strings, then inserts the connection block into the in-memory
   * content.
   *
   * @param dbtype the database type
   * @param connna the connection name
   * @param dbuser the database user
   * @param dbpass the database password
   * @param driver the driver
   * @param connst the connection string
   * @return true if the connection was successfully loaded, false if it was rejected
   */
  private static final boolean loadOneConnection(
      String dbtype, String connna, String dbuser, String dbpass, String driver, String connst) {
    outprint(
        MESSAGE_LOADING_CONNECTION
            + dbtype
            + SPACE_CHAR
            + "-"
            + SPACE_CHAR
            + connna
            + SPACE_CHAR
            + DOUBLE_DOT
            + SPACE_CHAR);
    if (!isValidDbType(dbtype, false)) {
      outprintln(MESSAGE_CONNECTION_LOADING_FAILED_WRONG_DATABASE_TYPE);
      return false;
    }
    if (connna.lastIndexOf(SPACE_CHAR) != -1) {
      outprintln(MESSAGE_CONNECTION_LOADING_FAILED_SPACE_IN_CONNECTION_NAME);
      return false;
    }
    if (connna.length() == 0) {
      outprintln(MESSAGE_CONNECTION_LOADING_FAILED_CONNECTION_NAME_CANNOT_BE_EMPTY);
      return false;
    }
    if (getConnnaPos(dbtype, connna) != -1) {
      outprintln(MESSAGE_CONNECTION_LOADING_FAILED_EXISTING_CONNECTION);
      return false;
    }
    if (driver.length() == 0) {
      outprintln(MESSAGE_CONNECTION_LOADING_FAILED_DRIVER_CANNOT_BE_EMPTY);
      return false;
    }
    if (connst.length() == 0) {
      outprintln(MESSAGE_CONNECTION_LOADING_FAILED_CONNECTION_STRING_CANNOT_BE_EMPTY);
      return false;
    }
    int posToInsert = getFirstNewLineAndZeroCharIndex() + 1;
    if (posToInsert == -1) {
      throw systemexit("Error - posToInsert is -1, executeCommandConnectionLoad");
    }
    insertConnectionBlock(
        dbtype, connna, dbuser, dbpass.toCharArray(), driver, connst, posToInsert);
    outprintln(MESSAGE_DONE);
    return true;
  }

  /**
   * Handles the describe command for a named connection by delegating to {@link
   * #describeConnection}.
   *
   * @param connna the connection name to describe
   */
  static final void executeCommandConnectionDescribe(String connna) {
    describeConnection(connna);
  }

  /**
   * Handles the describe command without an argument by calling {@link #describeConnection} with an
   * empty name, so the connection is resolved from state or by prompting.
   */
  static final void executeCommandConnectionDescribe() {
    describeConnection("");
  }

  /**
   * Handles the change command for a named connection by delegating to {@link
   * #connectionAddOrChange}.
   *
   * @param connna the connection name to change
   */
  static final void executeCommandConnectionChange(String connna) {
    connectionAddOrChange(connna);
  }

  /**
   * Handles the change command without an argument by changing the currently used connection, or
   * printing a hint when no connection is in use.
   */
  static final void executeCommandConnectionChange() {
    if (!"".equals(dbConn)) {
      connectionAddOrChange(dbConn);
    } else {
      outprintln(MESSAGE_YOU_HAVE_TO_USE_A_CONNECTION_OR_GIVE_THE_CONNECTION_NAME_TO_CHANGE);
    }
  }

  /**
   * Handles the delete command for a named connection by delegating to {@link
   * #deleteConnection(String)}.
   *
   * @param connna the connection name to delete
   */
  static final void executeCommandConnectionDelete(String connna) {
    deleteConnection(connna);
  }

  /**
   * Handles the delete command without an argument by calling {@link #deleteConnection(String)}
   * with an empty name, so the connection is resolved from state or by prompting.
   */
  static final void executeCommandConnectionDelete() {
    deleteConnection("");
  }

  /**
   * Handles the deleteall command. When no database type is in use it deletes the connections of
   * every supported database type; otherwise it deletes only those of the current type. Clears the
   * active connection if it no longer exists after deletion.
   */
  static final void executeCommandConnectionDeleteall() {
    if (isFileContentConnectionsOrigReady()) {
      if (dbType != null) {
        if ("".equals(dbType)) {
          deleteConnectionsByDbType(DB_TYPE_MYSQL);
          deleteConnectionsByDbType(DB_TYPE_ORACLE);
          deleteConnectionsByDbType(DB_TYPE_MSSQL);
          deleteConnectionsByDbType(DB_TYPE_DB2);
          deleteConnectionsByDbType(DB_TYPE_POSTGRESQL);
        } else {
          deleteConnectionsByDbType(dbType);
        }
        if (!"".equals(dbType) && !"".equals(dbConn)) {
          if (getConnnaPos(dbType, dbConn) == -1) {
            setDbConn("");
          }
        }
      } else {
        throw systemexit("Error . dbType is null, executeCommandConnectionDeleteall");
      }
    }
  }

  /**
   * Handles the use command with only a connection name by delegating to {@link #useConnection}
   * with an empty database type, so the type is resolved from state or by prompting.
   *
   * @param connna the connection name to use
   */
  static final void executeCommandConnectionUse(String connna) {
    useConnection("", connna);
  }

  /**
   * Handles the use command with both a database type and a connection name by delegating directly
   * to {@link #useConnection}.
   *
   * @param dbtype the database type of the connection to use
   * @param connna the connection name to use
   */
  static final void executeCommandConnectionUse(String dbtype, String connna) {
    useConnection(dbtype, connna);
  }

  /**
   * Tests a stored connection identified by name. Resolves and validates the database type, looks
   * up the connection's stored user, password, driver and connection string, and attempts to open a
   * connection via {@link Crypto#testConnection}; reports if the connection does not exist.
   *
   * @param connna the name of the stored connection to test
   */
  static final void executeCommandConnectionTest(String connna) {
    if (isFileContentConnectionsOrigReady()) {
      if (connna != null) {
        String dbtype = null;
        String dbuser = null;
        char[] dbpass = null;
        String driver = null;
        String connst = null;
        int currPos = -1;
        if (!"".equals(dbType)) {
          dbtype = dbType;
        } else {
          dbtype =
              readline(NEW_LINE_STRING + MESSAGE_DATABASE_TYPE, APP_MAX_LENGTH_OF_INPUT)
                  .toLowerCase();
        }
        if (dbtype != null) {
          if (isValidDbType(dbtype, true)) {
            currPos = getConnnaPos(dbtype, connna);
            if (currPos != -1) {
              dbuser = getDbuser(dbtype, connna);
              dbpass = getDbpass(dbtype, connna).toCharArray();
              driver = getDriver(dbtype, connna);
              connst = getConnst(dbtype, connna);
              testConnection(dbuser, new String(dbpass), driver, connst);
            } else {
              outprintln(MESSAGE_YOUR_CONNECTION_DOES_NOT_EXIST);
            }
          }
        } else {
          throw systemexit("Error - dbtype is null, executeCommandConnectionTest");
        }
        dbtype = null;
        dbuser = null;
        dbpass = null;
        driver = null;
        connst = null;
        currPos = 0;
      } else {
        throw systemexit("Error - connna is null, executeCommandConnectionTest");
      }
    }
  }

  /**
   * Tests an ad-hoc connection whose properties are entered interactively. Prompts for the database
   * user, password, driver and connection string, then attempts to open a connection via {@link
   * Crypto#testConnection}.
   */
  static final void executeCommandConnectionTest() {
    if (isFileContentConnectionsOrigReady()) {
      String dbuser = null;
      char[] dbpass = null;
      String driver = null;
      String connst = null;
      outprintln(MESSAGE_ENTER_THE_PROPERTIES_OF_THE_CONNECTION);
      dbuser = readline(MESSAGE_DATABASE_USER, APP_MAX_LENGTH_OF_INPUT);
      if (dbuser != null) {
        dbpass = readpassword(MESSAGE_DATABASE_PASSWORD);
        if (dbpass != null) {
          driver = readline(MESSAGE_DATABASE_DRIVER, APP_MAX_LENGTH_OF_INPUT);
          if (driver != null) {
            connst = readline(MESSAGE_CONNECTION_STRING, APP_MAX_LENGTH_OF_INPUT);
            if (connst != null) {
              testConnection(dbuser, new String(dbpass), driver, connst);
            } else {
              throw systemexit("Error - connst is null, executeCommandConnectionTest");
            }
          } else {
            throw systemexit("Error - driver is null, executeCommandConnectionTest");
          }
        } else {
          throw systemexit("Error - dbpass is null, executeCommandConnectionTest");
        }
      } else {
        throw systemexit("Error - dbuser is null, executeCommandConnectionTest");
      }
    }
  }
}
