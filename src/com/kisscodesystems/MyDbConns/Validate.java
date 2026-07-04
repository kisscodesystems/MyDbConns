package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Args.*;
import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.Print.*;
import static com.kisscodesystems.MyDbConns.Utils.*;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Collection of boolean validators for MyDbConns inputs and state: database types, good-password
 * strength, file and connections-file paths, result targets and formats, drivers,
 * query-type/delimiter compatibility, result set usability, and whether a connection is in use.
 * Several validators accept a "messageIfNot" flag that prints an explanatory message on failure.
 */
final class Validate {
  /**
   * Determines whether the given result set is usable by attempting to obtain its metadata.
   *
   * @param rs the result set to test
   * @return true if the result set is non-null and its metadata can be retrieved, false otherwise
   */
  static final boolean isResultSetUsable(ResultSet rs) {
    boolean usable = false;
    if (rs != null) {
      try {
        rs.getMetaData();
        usable = true;
      } catch (SQLException e) {
        usable = false;
      }
    } else {
      usable = false;
    }
    return usable;
  }

  /**
   * Checks that a batch query type is not paired with an empty delimiter.
   *
   * @param queryType the query type to check
   * @param queryDelimiter the delimiter to check
   * @return false only when the query type is batch and the delimiter is empty, true otherwise
   */
  static final boolean isQueryTypeAndDelimiterOk(String queryType, String queryDelimiter) {
    return (!(ARG_BATCH.equals(queryType) && "".equals(queryDelimiter)));
  }

  /**
   * Checks whether the given command-line arguments array is non-null.
   *
   * @param args the arguments array to check
   * @return true if the array is non-null, false otherwise
   */
  static final boolean isGoodArgsObject(String[] args) {
    return args != null;
  }

  /**
   * Checks that the given path lies within the connections directory and ends with one of the
   * recognized connections file postfixes (ciphertext, salt, IV, or new-file).
   *
   * @param filePath the path to validate
   * @return true if the path is a valid connections file path, false otherwise
   */
  static final boolean isValidConnectionsFilePath(String filePath) {
    boolean valid = false;
    if (filePath != null) {
      if (filePath.startsWith(APP_CONNECTIONS_DIR + SEP)
          && (filePath.endsWith(APP_CS_POSTFIX)
              || filePath.endsWith(APP_SL_POSTFIX)
              || filePath.endsWith(APP_IV_POSTFIX)
              || filePath.endsWith(APP_NW_POSTFIX))) {
        valid = true;
      }
    }
    return valid;
  }

  /**
   * Checks whether the given database type is one of the supported types (Mysql, Oracle, Mssql,
   * Db2, or Postgresql).
   *
   * @param dbtype the database type to validate
   * @param messageIfNot when true, prints an incorrect-database-type message on failure
   * @return true if the type is supported, false otherwise
   */
  static final boolean isValidDbType(String dbtype, boolean messageIfNot) {
    boolean valid = true;
    if (dbtype.equals(DB_TYPE_MYSQL)
        || dbtype.equals(DB_TYPE_ORACLE)
        || dbtype.equals(DB_TYPE_MSSQL)
        || dbtype.equals(DB_TYPE_DB2)
        || dbtype.equals(DB_TYPE_POSTGRESQL)) {
      valid = true;
    } else {
      valid = false;
      if (messageIfNot) {
        outprintln(MESSAGE_DATABASE_TYPE_HAS_NOT_BEEN_CORRECT);
      }
    }
    return valid;
  }

  /**
   * Checks whether the given path exists and is a regular file.
   *
   * @param filePath the path to check
   * @param messageIfNot when true, prints a "not a file" or "does not exist" message on failure
   * @return true if the path exists and is a file, false otherwise
   */
  static final boolean isExistingFile(String filePath, boolean messageIfNot) {
    boolean success = false;
    File file = new File(filePath);
    if (file.exists()) {
      if (file.isFile()) {
        success = true;
      } else {
        if (messageIfNot) {
          outprintln(MESSAGE_FILE_IS_NOT_FILE + filePath);
        }
      }
    } else {
      if (messageIfNot) {
        outprintln(MESSAGE_FILE_DOES_NOT_EXIST + filePath);
      }
    }
    file = null;
    return success;
  }

  /**
   * Checks that the password consists of ASCII non-space characters within the allowed length
   * bounds and meets the configured minimum counts of uppercase letters, lowercase letters, digits,
   * and special characters.
   *
   * @param password the password characters to validate
   * @param messageIfNot when true, prints an invalid-good-password message on failure
   * @return true if the password satisfies all strength requirements, false otherwise
   */
  static final boolean isValidGoodPassword(char[] password, boolean messageIfNot) {
    boolean valid = false;
    if (isASCIIandNONSPACE(password)) {
      if (password.length <= APP_MAX_LENGTH_OF_INPUT) {
        if (password.length >= APP_GOOD_PASSWORD_MIN_LENGTH_OF_GOOD_PASSWORDS) {
          int countUCLetters = 0;
          int countLCLetters = 0;
          int countDigits = 0;
          int countSpecChars = 0;
          for (int i = 0; i < password.length; i++) {
            if (password[i] >= 33 && password[i] <= 47) {
              countSpecChars++;
            } else if (password[i] >= 48 && password[i] <= 57) {
              countDigits++;
            } else if (password[i] >= 58 && password[i] <= 64) {
              countSpecChars++;
            } else if (password[i] >= 65 && password[i] <= 90) {
              countUCLetters++;
            } else if (password[i] >= 91 && password[i] <= 96) {
              countSpecChars++;
            } else if (password[i] >= 97 && password[i] <= 122) {
              countLCLetters++;
            } else if (password[i] >= 123 && password[i] <= 126) {
              countSpecChars++;
            }
          }
          if (countUCLetters >= APP_GOOD_PASSWORD_MIN_COUNT_OF_UC_LETTERS
              && countLCLetters >= APP_GOOD_PASSWORD_MIN_COUNT_OF_LC_LETTERS
              && countDigits >= APP_GOOD_PASSWORD_MIN_COUNT_OF_DIGITS
              && countSpecChars >= APP_GOOD_PASSWORD_MIN_COUNT_OF_SPEC_CHARS) {
            valid = true;
          }
          countUCLetters = 0;
          countLCLetters = 0;
          countDigits = 0;
          countSpecChars = 0;
        }
      }
    }
    if (!valid && messageIfNot) {
      outprintln(MESSAGE_CONNECTIONS_GOOD_PASSWORD_IS_NOT_VALID);
    }
    return valid;
  }

  /**
   * Checks that the given file path is either free of any path separator or contains the
   * application name, restricting files to those next to the application.
   *
   * @param filePath the path to validate
   * @param messageIfNot when true, prints a message on failure that files are only allowed next to
   *     the application
   * @return true if the path is allowed, false otherwise
   */
  static final boolean isValidFilePath(String filePath, boolean messageIfNot) {
    boolean success = false;
    if (filePath != null) {
      if (!filePath.contains(SEP) || filePath.contains(APP_NAME)) {
        success = true;
      }
    }
    if (!success && messageIfNot) {
      outprintln(MESSAGE_FILES_ARE_ALLOWED_FROM_NEXT_TO_THE_APPLICATION);
    }
    return success;
  }

  /**
   * Checks whether the given result targets value is one of the accepted single targets (console or
   * file) or one of their two-target combinations in either order.
   *
   * @param resultTargets the result targets value to validate
   * @return true if the value is a recognized result targets combination, false otherwise
   */
  static final boolean isValidResultTargets(String resultTargets) {
    return RESULT_TARGET_CONS_VALUE.equals(resultTargets)
        || RESULT_TARGET_FILE_VALUE.equals(resultTargets)
        || (RESULT_TARGET_CONS_VALUE + RESULT_TARGET_FILE_VALUE).equals(resultTargets)
        || (RESULT_TARGET_FILE_VALUE + RESULT_TARGET_CONS_VALUE).equals(resultTargets);
  }

  /**
   * Checks whether the given result formats value is one of the accepted single formats (txt, csv,
   * or htm) or any of their two- or three-format combinations in any order.
   *
   * @param resultTargets the result formats value to validate
   * @return true if the value is a recognized result formats combination, false otherwise
   */
  static final boolean isValidResultFormats(String resultTargets) {
    return RESULT_FORMAT_TXT_VALUE.equals(resultTargets)
        || RESULT_FORMAT_CSV_VALUE.equals(resultTargets)
        || RESULT_FORMAT_HTM_VALUE.equals(resultTargets)
        || (RESULT_FORMAT_TXT_VALUE + RESULT_FORMAT_CSV_VALUE).equals(resultTargets)
        || (RESULT_FORMAT_CSV_VALUE + RESULT_FORMAT_TXT_VALUE).equals(resultTargets)
        || (RESULT_FORMAT_TXT_VALUE + RESULT_FORMAT_HTM_VALUE).equals(resultTargets)
        || (RESULT_FORMAT_HTM_VALUE + RESULT_FORMAT_TXT_VALUE).equals(resultTargets)
        || (RESULT_FORMAT_HTM_VALUE + RESULT_FORMAT_CSV_VALUE).equals(resultTargets)
        || (RESULT_FORMAT_CSV_VALUE + RESULT_FORMAT_HTM_VALUE).equals(resultTargets)
        || (RESULT_FORMAT_TXT_VALUE + RESULT_FORMAT_CSV_VALUE + RESULT_FORMAT_HTM_VALUE)
            .equals(resultTargets)
        || (RESULT_FORMAT_TXT_VALUE + RESULT_FORMAT_HTM_VALUE + RESULT_FORMAT_CSV_VALUE)
            .equals(resultTargets)
        || (RESULT_FORMAT_CSV_VALUE + RESULT_FORMAT_TXT_VALUE + RESULT_FORMAT_HTM_VALUE)
            .equals(resultTargets)
        || (RESULT_FORMAT_CSV_VALUE + RESULT_FORMAT_HTM_VALUE + RESULT_FORMAT_TXT_VALUE)
            .equals(resultTargets)
        || (RESULT_FORMAT_HTM_VALUE + RESULT_FORMAT_TXT_VALUE + RESULT_FORMAT_CSV_VALUE)
            .equals(resultTargets)
        || (RESULT_FORMAT_HTM_VALUE + RESULT_FORMAT_CSV_VALUE + RESULT_FORMAT_TXT_VALUE)
            .equals(resultTargets);
  }

  /**
   * Determines whether the connection identified by dbtype and connna is referenced by any added
   * query, printing either a not-in-use message or the list of referencing query IDs. Exits if
   * either argument is null or the query lookup returns null.
   *
   * @param dbtype the database type of the connection
   * @param connna the connection name
   * @param messageIfNot when true, prints the not-in-use message when the connection is unused;
   *     also affects the leading newline of the in-use message
   * @return true if the connection is in use by at least one query, false otherwise
   */
  static final boolean isConnectionInUse(String dbtype, String connna, boolean messageIfNot) {
    boolean inUse = true;
    if (dbtype != null && connna != null) {
      ArrayList<Integer> queryIds = getQueriesByConnection(dbtype, connna);
      if (queryIds != null) {
        if (queryIds.size() == 0) {
          inUse = false;
          if (messageIfNot) {
            outprintln(FOLD + MESSAGE_THIS_CONNECTION_IS_NOT_IN_USE_BY_ADDED_QUERY);
          }
        } else {
          inUse = true;
          outprintln(
              ""
                  + (messageIfNot ? "" : NEW_LINE_CHAR)
                  + FOLD
                  + MESSAGE_THIS_CONNECTION_IS_IN_USE
                  + joinArrayListInteger(queryIds, SEP1));
        }
      } else {
        throw systemexit("Error - queryIds is null, isConnectionInUse");
      }
      queryIds = null;
    } else {
      throw systemexit("Error - one of these is null: dbtype|connna|us, isConnectionInUse");
    }
    return inUse;
  }

  /**
   * Checks that the given driver string contains the substring expected for the given database
   * type, printing a type-specific message when it does not. Exits if either argument is null.
   *
   * @param driver the driver class name to validate
   * @param dbtype the database type the driver must match
   * @return true if the driver contains the expected substring for the type, false otherwise
   */
  static final boolean isValidDriver(String driver, String dbtype) {
    boolean success = false;
    if (dbtype != null) {
      if (driver != null) {
        if (dbtype.equals(DB_TYPE_MYSQL)) {
          if (driver.contains(DB_TYPE_DRIVER_SEARCH_MYSQL)) {
            success = true;
          } else {
            outprintln(MESSAGE_DRIVER_FOR_MYSQL_HAS_TO_CONTAIN + DB_TYPE_DRIVER_SEARCH_MYSQL);
          }
        } else if (dbtype.equals(DB_TYPE_ORACLE)) {
          if (driver.contains(DB_TYPE_DRIVER_SEARCH_ORACLE)) {
            success = true;
          } else {
            outprintln(MESSAGE_DRIVER_FOR_ORACLE_HAS_TO_CONTAIN + DB_TYPE_DRIVER_SEARCH_ORACLE);
          }
        } else if (dbtype.equals(DB_TYPE_MSSQL)) {
          if (driver.contains(DB_TYPE_DRIVER_SEARCH_MSSQL)) {
            success = true;
          } else {
            outprintln(MESSAGE_DRIVER_FOR_MSSQL_HAS_TO_CONTAIN + DB_TYPE_DRIVER_SEARCH_MSSQL);
          }
        } else if (dbtype.equals(DB_TYPE_DB2)) {
          if (driver.contains(DB_TYPE_DRIVER_SEARCH_DB2)) {
            success = true;
          } else {
            outprintln(MESSAGE_DRIVER_FOR_DB2_HAS_TO_CONTAIN + DB_TYPE_DRIVER_SEARCH_DB2);
          }
        } else {
          if (driver.contains(DB_TYPE_DRIVER_SEARCH_POSTGRESQL)) {
            success = true;
          } else {
            outprintln(
                MESSAGE_DRIVER_FOR_POSTGRESQL_HAS_TO_CONTAIN + DB_TYPE_DRIVER_SEARCH_POSTGRESQL);
          }
        }
      } else {
        throw systemexit("Error - driver is null, isValidDriver");
      }
    } else {
      throw systemexit("Error - dbtype is null, isValidDriver");
    }
    return success;
  }
}
