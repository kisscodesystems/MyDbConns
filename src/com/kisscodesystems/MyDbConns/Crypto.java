package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.State.*;
import static com.kisscodesystems.MyDbConns.Utils.*;

import java.io.File;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Provides the cryptographic and file-lifecycle helpers of MyDbConns. Generates a random header,
 * tests JDBC connections, clears sensitive char[] and byte[] buffers, and removes the old encrypted
 * connection files while renaming the freshly written ones into place.
 */
final class Crypto {
  /**
   * Generates a random header of lowercase letters terminated by a newline character. The number of
   * letters is chosen randomly between the configured minimum and maximum using a {@link
   * SecureRandom}.
   *
   * @return a char array containing the random letters followed by a newline character
   */
  static final char[] generateRandomHeader() {
    SecureRandom secureRandom = new SecureRandom();
    int letters =
        secureRandom.nextInt(APP_HEADER_MAX_LETTERS - APP_HEADER_MIN_LETTERS + 1)
            + APP_HEADER_MIN_LETTERS;
    char[] header = new char[letters + 1];
    for (int i = 0; i < letters; i++) {
      header[i] = (char) ('a' + secureRandom.nextInt(26));
    }
    header[letters] = NEW_LINE_CHAR;
    secureRandom = null;
    return header;
  }

  /**
   * Overwrites all sensitive char arrays held in state (the original and verified input passwords,
   * the connections password, and the original and trimmed connections file content) with the zero
   * character.
   */
  static final void clearCharArrays() {
    clearCharArray(passwordFromInputOriginal, ZERO_CHAR);
    clearCharArray(passwordFromInputVerified, ZERO_CHAR);
    clearCharArray(passwordForConnections, ZERO_CHAR);
    clearCharArray(fileContentConnectionsOrig, ZERO_CHAR);
    clearCharArray(fileContentConnectionsTrim, ZERO_CHAR);
  }

  /**
   * Overwrites the sensitive byte arrays held in state (the connections salt and initialization
   * vector) with the null byte.
   */
  static final void clearByteArrays() {
    clearByteArray(slConnections, NULL_BYTE);
    clearByteArray(ivConnections, NULL_BYTE);
  }

  /**
   * Attempts to open a JDBC connection with the given credentials to validate them, printing a
   * success or failure message, and always closes the connection afterwards. Loads the driver
   * class, opens the connection, and reports any exception without propagating it.
   *
   * @param dbuser the database user
   * @param dbpass the database password
   * @param driver the fully qualified JDBC driver class name
   * @param connst the JDBC connection string
   */
  static final void testConnection(String dbuser, String dbpass, String driver, String connst) {
    Connection connection = null;
    try {
      Class.forName(driver);
      connection = DriverManager.getConnection(connst, dbuser, new String(dbpass));
      outprintln(MESSAGE_CONNECTION_HAS_BEEN_TESTED_SUCCESSFULLY);
    } catch (Exception e) {
      outprintln(MESSAGE_UNABLE_TO_CONNECT);
      outprintln(FOLD + e.toString().trim());
      ;
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (Exception e) {
          outprintln(MESSAGE_UNABLE_TO_CLOSE_TESTER_CONNECTION);
        }
      }
    }
    connection = null;
  }

  /**
   * Commits a freshly written set of connection files. Deletes the existing content, salt and IV
   * files, then renames the newly written files (with the "new" postfix) to their final names.
   * Prints an error message if any delete or rename fails.
   *
   * @return true if the old files were deleted and the new files renamed successfully, false
   *     otherwise
   */
  static final boolean removeOldFilesAndRenameNewFiles() {
    boolean success = false;
    String fileName = APP_CONNECTIONS_FILE_NAME;
    File file = null;
    File slFile = null;
    File ivFile = null;
    file = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_CS_POSTFIX);
    slFile = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_SL_POSTFIX);
    ivFile = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_IV_POSTFIX);
    if ((file.exists() && !file.delete())
        || (slFile.exists() && !slFile.delete())
        || (ivFile.exists() && !ivFile.delete())) {
      outprintln(MESSAGE_ERROR_DELETING_OLD_FILES_OR_RENAME_NEW_FILES + fileName);
    } else {
      File fileOld = null;
      File fileNew = null;
      File fileOldSl = null;
      File fileNewSl = null;
      File fileOldIv = null;
      File fileNewIv = null;
      fileOld = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_CS_POSTFIX + APP_NW_POSTFIX);
      fileNew = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_CS_POSTFIX);
      fileOldSl = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_SL_POSTFIX + APP_NW_POSTFIX);
      fileNewSl = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_SL_POSTFIX);
      fileOldIv = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_IV_POSTFIX + APP_NW_POSTFIX);
      fileNewIv = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_IV_POSTFIX);
      if (!fileOld.renameTo(fileNew)
          || !fileOldSl.renameTo(fileNewSl)
          || !fileOldIv.renameTo(fileNewIv)) {
        outprintln(MESSAGE_ERROR_DELETING_OLD_FILES_OR_RENAME_NEW_FILES + fileName);
      } else {
        success = true;
      }
      fileOld = null;
      fileNew = null;
      fileOldSl = null;
      fileNewSl = null;
      fileOldIv = null;
      fileNewIv = null;
    }
    file = null;
    slFile = null;
    ivFile = null;
    return success;
  }
}
