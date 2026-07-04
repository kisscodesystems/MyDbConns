package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.Crypto.*;
import static com.kisscodesystems.MyDbConns.FileStore.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.State.*;
import static com.kisscodesystems.MyDbConns.Utils.*;
import static com.kisscodesystems.MyDbConns.Validate.*;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Manages the encrypted connections file for MyDbConns: reading the password, encrypting and saving
 * the file (AES/GCM), decrypting and loading it into memory, and maintaining the in-memory content
 * model. The plaintext is a fixed-length char[] beginning with a random-length header line followed
 * by 6-line connection records (dbtype, connna, dbuser, dbpass, driver, connst); getters locate
 * fields by walking newlines past the header.
 */
final class Connections {
  /**
   * Repeatedly prompts for the connections password until it is a valid good password and, when
   * verification is requested, matches the re-entered value, storing the accepted password in the
   * shared password buffer. Sensitive input buffers are cleared between attempts.
   *
   * @param beVerified when true, the password must be entered a second time and both entries must
   *     match; a verification error message is printed on mismatch
   */
  static final void readPassword(boolean beVerified) {
    boolean isValidPassword = false;
    boolean isVerifiedPassword = false;
    while (!isValidPassword || !isVerifiedPassword) {
      isValidPassword = false;
      isVerifiedPassword = true;
      clearCharArray(passwordFromInputOriginal, ZERO_CHAR);
      passwordFromInputOriginal = readpassword(MESSAGE_ENTER_PASSWORD_FOR_CONNECTIONS);
      clearCharArray(passwordForConnections, ZERO_CHAR);
      passwordForConnections = new char[passwordFromInputOriginal.length];
      isValidPassword = isValidGoodPassword(passwordFromInputOriginal, beVerified);
      if (isValidPassword) {
        for (int i = 0; i < passwordFromInputOriginal.length; i++) {
          passwordForConnections[i] = passwordFromInputOriginal[i];
        }
        if (beVerified) {
          clearCharArray(passwordFromInputVerified, ZERO_CHAR);
          passwordFromInputVerified = readpassword(MESSAGE_ENTER_PASSWORD_VERIFY);
          if (passwordFromInputVerified.length != passwordFromInputOriginal.length) {
            isVerifiedPassword = false;
          } else {
            for (int i = 0; i < passwordFromInputVerified.length; i++) {
              if (passwordFromInputVerified[i] != passwordFromInputOriginal[i]) {
                isVerifiedPassword = false;
                break;
              }
            }
          }
          clearCharArray(passwordFromInputVerified, ZERO_CHAR);
          if (!isVerifiedPassword) {
            outprintln(MESSAGE_PASSWORD_VERIFICATION_ERROR);
          }
        }
      }
      clearCharArray(passwordFromInputOriginal, ZERO_CHAR);
    }
    isValidPassword = false;
    isVerifiedPassword = false;
  }

  /**
   * Encrypts the current in-memory connections content with AES/GCM using a freshly generated salt
   * and IV derived from the stored password, writes the salt, IV, and ciphertext to new files, and
   * atomically replaces the old files with them. Prints a success or error message and exits on any
   * cryptographic or file error.
   *
   * @return true if the encrypted files were written and the old files successfully replaced, false
   *     otherwise
   */
  static final boolean saveFile() {
    boolean success = false;
    String fileName = APP_CONNECTIONS_FILE_NAME;
    SecureRandom secureRandom = new SecureRandom();
    slConnections = new byte[APP_SALT_LENGTH];
    secureRandom.nextBytes(slConnections);
    writeFileBytes(
        APP_CONNECTIONS_DIR + SEP + fileName + APP_SL_POSTFIX + APP_NW_POSTFIX, slConnections);
    secureRandom = null;
    SecretKeyFactory skf = null;
    try {
      skf = SecretKeyFactory.getInstance(APP_SECRET_KEY_FACTORY_INSTANCE);
    } catch (NoSuchAlgorithmException e) {
      throw systemexit("Exception - NoSuchAlgorithmException0, saveFile");
    }
    if (skf == null) {
      throw systemexit("Error - skf is null, saveFile");
    }
    PBEKeySpec pbeks =
        new PBEKeySpec(
            passwordForConnections,
            slConnections,
            APP_PBE_KEY_SPEC_ITERATIONS,
            APP_PBE_KEY_SPEC_KEY_LENGTH);
    SecretKey sk = null;
    try {
      sk = skf.generateSecret(pbeks);
    } catch (InvalidKeySpecException e) {
      throw systemexit("Exception - InvalidKeyException, saveFile");
    }
    if (sk == null) {
      throw systemexit("Error - sk is null, saveFile");
    }
    SecretKeySpec sks = new SecretKeySpec(sk.getEncoded(), APP_SECRET_KEY_SPEC_ALGORYTHM);
    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance(APP_CIPHER_INSTANCE);
    } catch (NoSuchAlgorithmException e) {
      throw systemexit("Exception - NoSuchAlgorithmException1, saveFile");
    } catch (NoSuchPaddingException e) {
      throw systemexit("Exception - NoSuchPaddingException, saveFile");
    }
    if (cipher != null) {
      ivConnections = new byte[APP_GCM_IV_LENGTH];
      new SecureRandom().nextBytes(ivConnections);
      try {
        cipher.init(
            Cipher.ENCRYPT_MODE, sks, new GCMParameterSpec(APP_GCM_TAG_LENGTH_BITS, ivConnections));
      } catch (InvalidAlgorithmParameterException e) {
        throw systemexit("Exception - InvalidAlgorithmParameterException, saveFile");
      } catch (InvalidKeyException e) {
        throw systemexit("Exception - InvalidKeyException, saveFile");
      }
      writeFileBytes(
          APP_CONNECTIONS_DIR + SEP + fileName + APP_IV_POSTFIX + APP_NW_POSTFIX, ivConnections);
      byte[] encryptedBytes = null;
      byte[] bytes = null;
      int endIndex = getFirstNewLineAndZeroCharIndex();
      clearCharArray(fileContentConnectionsTrim, ZERO_CHAR);
      fileContentConnectionsTrim = new char[endIndex + 1];
      clearCharArray(fileContentConnectionsTrim, ZERO_CHAR);
      for (int i = 0; i < fileContentConnectionsTrim.length; i++) {
        fileContentConnectionsTrim[i] = fileContentConnectionsOrig[i];
      }
      bytes = toBytesASCII(fileContentConnectionsTrim);
      try {
        encryptedBytes = cipher.doFinal(bytes);
      } catch (IllegalBlockSizeException e) {
        throw systemexit("Exception - IllegalBlockSizeException, saveFile");
      } catch (BadPaddingException e) {
        throw systemexit("Exception - BadPaddingException, saveFile");
      }
      writeFileBytes(
          APP_CONNECTIONS_DIR + SEP + fileName + APP_CS_POSTFIX + APP_NW_POSTFIX, encryptedBytes);
      clearByteArray(encryptedBytes, NULL_BYTE);
      encryptedBytes = null;
      clearByteArray(bytes, NULL_BYTE);
      bytes = null;
    } else {
      throw systemexit("Error - cipher is null, saveFile");
    }
    cipher = null;
    sks = null;
    sk = null;
    pbeks = null;
    skf = null;
    File fileNew = null;
    File slFileNew = null;
    File ivFileNew = null;
    fileNew = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_CS_POSTFIX + APP_NW_POSTFIX);
    slFileNew = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_SL_POSTFIX + APP_NW_POSTFIX);
    ivFileNew = new File(APP_CONNECTIONS_DIR + SEP + fileName + APP_IV_POSTFIX + APP_NW_POSTFIX);
    if (fileNew.exists() && slFileNew.exists() && ivFileNew.exists()) {
      if (removeOldFilesAndRenameNewFiles()) {
        success = true;
        outprintln(MESSAGE_FILE_HAS_BEEN_SAVED + fileName);
      }
    } else {
      outprintln(MESSAGE_MISSING_NEW_CS_OR_SL_OR_IV_FILE);
      if (fileNew.exists()) {
        if (!fileNew.delete()) {
          outprintln(MESSAGE_ERROR_DELETING_NEW_AN_FILE);
        }
      }
      if (slFileNew.exists()) {
        if (!slFileNew.delete()) {
          outprintln(MESSAGE_ERROR_DELETING_NEW_SL_FILE);
        }
      }
      if (ivFileNew.exists()) {
        if (!ivFileNew.delete()) {
          outprintln(MESSAGE_ERROR_DELETING_NEW_IV_FILE);
        }
      }
    }
    fileNew = null;
    slFileNew = null;
    ivFileNew = null;
    return success;
  }

  /**
   * Reads the salt, IV, and ciphertext files, derives the key from the stored password, decrypts
   * the content with AES/GCM, and loads it into the fixed-length in-memory buffer, validating that
   * a proper header is present. Prints a message for a wrong password, missing files, or missing
   * content, and exits on cryptographic errors.
   *
   * @return true if the content was decrypted and a valid header was found, false otherwise
   */
  static final boolean getFileContent() {
    boolean success = false;
    String fileName = APP_CONNECTIONS_FILE_NAME;
    if (isExistingConnectionsFiles(true)) {
      clearCharArray(fileContentConnectionsOrig, ZERO_CHAR);
      fileContentConnectionsOrig = new char[APP_FILE_CONTENT_MAX_LENGTH];
      clearCharArray(fileContentConnectionsOrig, ZERO_CHAR);
      slConnections = readFileBytes(APP_CONNECTIONS_DIR + SEP + fileName + APP_SL_POSTFIX);
      if (slConnections == null) {
        throw systemexit("Error - slConnections is null, getFileContent");
      } else if (slConnections.length == 0) {
        throw systemexit("Error - slConnections is empty, getFileContent");
      }
      ivConnections = readFileBytes(APP_CONNECTIONS_DIR + SEP + fileName + APP_IV_POSTFIX);
      SecretKeyFactory skf = null;
      try {
        skf = SecretKeyFactory.getInstance(APP_SECRET_KEY_FACTORY_INSTANCE);
      } catch (NoSuchAlgorithmException e) {
        throw systemexit("Exception - NoSuchAlgorithmException0, getFileContent");
      }
      if (skf == null) {
        throw systemexit("Error - skf is null, getFileContent");
      }
      PBEKeySpec pbeks =
          new PBEKeySpec(
              passwordForConnections,
              slConnections,
              APP_PBE_KEY_SPEC_ITERATIONS,
              APP_PBE_KEY_SPEC_KEY_LENGTH);
      SecretKey sk = null;
      try {
        sk = skf.generateSecret(pbeks);
      } catch (InvalidKeySpecException e) {
        throw systemexit("Exception - InvalidKeySpecException, getFileContent");
      }
      if (sk == null) {
        throw systemexit("Error - sk is null, getFileContent");
      }
      SecretKeySpec sks = new SecretKeySpec(sk.getEncoded(), APP_SECRET_KEY_SPEC_ALGORYTHM);
      Cipher cipher = null;
      try {
        cipher = Cipher.getInstance(APP_CIPHER_INSTANCE);
      } catch (NoSuchAlgorithmException e) {
        throw systemexit("Exception - NoSuchAlgorithmException1, getFileContent");
      } catch (NoSuchPaddingException e) {
        throw systemexit("Exception - NoSuchPaddingException, getFileContent");
      }
      if (cipher != null) {
        try {
          cipher.init(
              Cipher.DECRYPT_MODE,
              sks,
              new GCMParameterSpec(APP_GCM_TAG_LENGTH_BITS, ivConnections));
        } catch (InvalidAlgorithmParameterException e) {
          throw systemexit("Exception - InvalidAlgorithmParameterException, getFileContent");
        } catch (InvalidKeyException e) {
          throw systemexit("Exception - InvalidKeyException, getFileContent");
        }
        byte[] decryptedBytes = null;
        byte[] bytes = null;
        bytes = readFileBytes(APP_CONNECTIONS_DIR + SEP + fileName + APP_CS_POSTFIX);
        try {
          decryptedBytes = cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException e) {
          throw systemexit("Exception - IllegalBlockSizeException, getFileContent");
        } catch (BadPaddingException e) {
          outprintln(MESSAGE_INCORRECT_FILE_PASSWORD);
        }
        cipher = null;
        sks = null;
        sk = null;
        pbeks = null;
        skf = null;
        boolean headerFound = false;
        if (decryptedBytes != null) {
          char[] tempChar = toCharsASCII(decryptedBytes);
          for (int i = 0; i < Math.min(tempChar.length, APP_FILE_CONTENT_MAX_LENGTH); i++) {
            fileContentConnectionsOrig[i] = tempChar[i];
          }
          headerFound = isContentDecrypted(true);
          clearCharArray(tempChar, ZERO_CHAR);
          tempChar = null;
          clearByteArray(decryptedBytes, NULL_BYTE);
          decryptedBytes = null;
        } else {
          headerFound = false;
        }
        clearByteArray(bytes, NULL_BYTE);
        bytes = null;
        if (headerFound) {
          success = true;
        } else {
          outprintln(MESSAGE_FILE_CONTENT_HAS_NOT_BEEN_FOUND + fileName);
        }
        headerFound = false;
      } else {
        throw systemexit("Error - cipher is null, getFileContent");
      }
    } else {
      outprintln(MESSAGE_MISSING_CS_OR_SL_OR_IV_FILE + fileName);
    }
    return success;
  }

  /**
   * Performs a structural check that the in-memory content buffer is non-null, of the expected
   * fixed length, and begins with a header line (terminated by a newline) whose length falls within
   * the allowed minimum and maximum header letters.
   *
   * @param messageIfNot when true, prints a "content is not decrypted" message if the check fails
   * @return true if the content appears to be decrypted with a valid header, false otherwise
   */
  static final boolean isContentDecrypted(boolean messageIfNot) {
    boolean headerFound = false;
    if (fileContentConnectionsOrig != null
        && fileContentConnectionsOrig.length == APP_FILE_CONTENT_MAX_LENGTH) {
      int headerLen = -1;
      for (int i = 0;
          i < Math.min(fileContentConnectionsOrig.length, APP_HEADER_MAX_LETTERS + 1);
          i++) {
        if (fileContentConnectionsOrig[i] == NEW_LINE_CHAR) {
          headerLen = i + 1;
          break;
        }
      }
      if (headerLen >= APP_HEADER_MIN_LETTERS + 1) {
        headerFound = true;
      }
    }
    if (!headerFound && messageIfNot) {
      outprintln(MESSAGE_CONTENT_IS_NOT_DECRYPTED);
    }
    return headerFound;
  }

  /**
   * Checks that all three connections files (ciphertext, IV, and salt) exist as files.
   *
   * @param messageIfNot when true, a message is printed for each missing file (via the underlying
   *     file existence check)
   * @return true if all three files exist, false otherwise
   */
  static final boolean isExistingConnectionsFiles(boolean messageIfNot) {
    return (isExistingFile(
            APP_CONNECTIONS_DIR + SEP + APP_CONNECTIONS_FILE_NAME + APP_CS_POSTFIX, messageIfNot)
        && isExistingFile(
            APP_CONNECTIONS_DIR + SEP + APP_CONNECTIONS_FILE_NAME + APP_IV_POSTFIX, messageIfNot)
        && isExistingFile(
            APP_CONNECTIONS_DIR + SEP + APP_CONNECTIONS_FILE_NAME + APP_SL_POSTFIX, messageIfNot));
  }

  /**
   * Ensures the in-memory connections content is loaded and ready: if it is not already decrypted,
   * it prompts for the password and loads the file; otherwise it verifies the buffer is present and
   * of the expected fixed length, exiting on an inconsistent state.
   *
   * @return true if the content is decrypted and ready in memory, false otherwise
   */
  static final boolean isFileContentConnectionsOrigReady() {
    boolean success = false;
    if (!isContentDecrypted(false)) {
      readPassword(false);
      if (getFileContent()) {
        success = true;
      }
    } else {
      if (fileContentConnectionsOrig != null) {
        if (fileContentConnectionsOrig.length == APP_FILE_CONTENT_MAX_LENGTH) {
          success = true;
        } else {
          throw systemexit(
              "Error - fileContentConnectionsOrig is not at length appFileContentMaxLength,"
                  + " isFileContentConnectionsOrigReady");
        }
      } else {
        throw systemexit(
            "Error - fileContentConnectionsOrig is null, isFileContentConnectionsOrigReady");
      }
    }
    return success;
  }

  /**
   * Finds the index of the first newline character that is immediately followed by a zero (padding)
   * character in the in-memory content buffer, marking the end of the meaningful content. Exits if
   * the buffer is null.
   *
   * @return the index of that newline, or -1 if no such position is found
   */
  static final int getFirstNewLineAndZeroCharIndex() {
    int index = -1;
    if (fileContentConnectionsOrig != null) {
      for (int i = 0; i < fileContentConnectionsOrig.length - 1; i++) {
        if (fileContentConnectionsOrig[i] == NEW_LINE_CHAR
            && fileContentConnectionsOrig[i + 1] == ZERO_CHAR) {
          index = i;
          break;
        }
      }
    } else {
      throw systemexit(
          "Error - fileContentConnectionsOrig is null, getFirstNewLineAndZeroCharIndex");
    }
    return index;
  }

  /**
   * Writes the characters of the given string into the in-memory content buffer starting at the
   * specified position, followed by a terminating newline character. Exits if the string is null.
   *
   * @param string the attribute value to insert
   * @param posToInsert the buffer index at which to begin writing
   */
  static final void insertAnAttributeIntoFileContentConnectionsOrig(
      String string, int posToInsert) {
    if (isFileContentConnectionsOrigReady()) {
      if (string != null) {
        for (int i = 0; i < string.length(); i++) {
          fileContentConnectionsOrig[posToInsert + i] = string.charAt(i);
        }
        fileContentConnectionsOrig[posToInsert + string.length()] = NEW_LINE_CHAR;
      } else {
        throw systemexit("Error - string is null, insertAnAttributeIntoFileContentConnectionsOrig");
      }
    }
  }

  /**
   * Writes the given char array into the in-memory content buffer starting at the specified
   * position, followed by a terminating newline character. Exits if the array is null.
   *
   * @param chars the attribute characters to insert
   * @param posToInsert the buffer index at which to begin writing
   */
  static final void insertAnAttributeIntoFileContentConnectionsOrig(char[] chars, int posToInsert) {
    if (isFileContentConnectionsOrigReady()) {
      if (chars != null) {
        for (int i = 0; i < chars.length; i++) {
          fileContentConnectionsOrig[posToInsert + i] = chars[i];
        }
        fileContentConnectionsOrig[posToInsert + chars.length] = NEW_LINE_CHAR;
      } else {
        throw systemexit("Error - chars is null, insertAnAttributeIntoFileContentConnectionsOrig");
      }
    }
  }

  /**
   * Shifts the block of content from the given start position up to the end of the meaningful
   * content by the given offset, zero-filling the vacated region so records can be resized in
   * place. Exits if the shift would write before the start of the buffer or beyond its end.
   *
   * @param startPos the index at which the moved block begins
   * @param diff the number of positions to shift by (negative shifts left, positive shifts right; a
   *     value of 0 is a no-op)
   */
  static final void shiftFileContent(int startPos, int diff) {
    if (diff != 0) {
      int firstNewLineAndZeroCharIndexOrig = 0;
      int movedPartCount = 0;
      if (isFileContentConnectionsOrigReady()) {
        firstNewLineAndZeroCharIndexOrig = getFirstNewLineAndZeroCharIndex();
        movedPartCount = firstNewLineAndZeroCharIndexOrig - startPos + 1;
        char[] movedPart = new char[movedPartCount];
        clearCharArray(movedPart, ZERO_CHAR);
        for (int i = startPos; i <= firstNewLineAndZeroCharIndexOrig; i++) {
          movedPart[i - startPos] = fileContentConnectionsOrig[i];
        }
        if (startPos + diff >= 0) {
          if (startPos + diff + (movedPart.length - 1) < fileContentConnectionsOrig.length) {
            for (int i = 0; i < movedPart.length; i++) {
              fileContentConnectionsOrig[startPos + diff + i] = movedPart[i];
            }
            if (diff < 0) {
              for (int i = startPos + diff + movedPartCount;
                  i <= firstNewLineAndZeroCharIndexOrig;
                  i++) {
                fileContentConnectionsOrig[i] = ZERO_CHAR;
              }
            } else if (diff > 0) {
              for (int i = startPos; i < startPos + Math.min(movedPartCount, diff); i++) {
                fileContentConnectionsOrig[i] = ZERO_CHAR;
              }
            }
          } else {
            throw systemexit("Error - Writing after content, shiftFileContent");
          }
        } else {
          throw systemexit("Error - Write before content, shiftFileContent");
        }
        clearCharArray(movedPart, ZERO_CHAR);
        movedPart = null;
      }
      firstNewLineAndZeroCharIndexOrig = 0;
      movedPartCount = 0;
    }
  }

  /**
   * Prints the second line of the in-memory content (the line following the header) after a newline
   * and fold prefix, provided the content is loaded and ready.
   */
  static final void printIniMessage() {
    if (isFileContentConnectionsOrigReady()) {
      int newLineCounter = 0;
      outprint(NEW_LINE_CHAR + FOLD);
      for (int i = 0; i < fileContentConnectionsOrig.length; i++) {
        if (newLineCounter == 1) {
          outprint(fileContentConnectionsOrig[i]);
        }
        if (fileContentConnectionsOrig[i] == NEW_LINE_CHAR) {
          newLineCounter++;
          if (newLineCounter == 2) {
            break;
          }
        }
      }
      newLineCounter = 0;
    }
  }

  /**
   * Locates the connection record identified by dbtype and connna and returns the field that is
   * newlineDelta lines past the record's connna line (0 = dbuser, 1 = dbpass, 2 = driver, 3 =
   * connst) as a char array.
   *
   * @param dbtype the database type of the record
   * @param connna the connection name of the record
   * @param newlineDelta the number of newlines (0 to 3) past the connna line identifying the field
   * @return the field characters, or an empty array if the record or field is not found or the
   *     delta is out of range
   */
  static final char[] getContentData(String dbtype, String connna, int newlineDelta) {
    char[] content = new char[0];
    if (newlineDelta >= 0 && newlineDelta <= 3) {
      int pos = -1;
      int contentPos = -1;
      int charArrayCount = 0;
      int newLines = 0;
      String toSearch = NEW_LINE_STRING + dbtype + NEW_LINE_CHAR + connna + NEW_LINE_CHAR;
      if (isFileContentConnectionsOrigReady()) {
        pos = getConnnaPos(dbtype, connna);
        if (pos != -1) {
          pos = pos + toSearch.length() - 1;
          for (int i = pos; i < fileContentConnectionsOrig.length; i++) {
            if (fileContentConnectionsOrig[i] == ZERO_CHAR) {
              break;
            }
            if (newLines == newlineDelta) {
              contentPos = i;
              break;
            }
            if (fileContentConnectionsOrig[i] == NEW_LINE_CHAR) {
              newLines++;
            }
          }
        }
        if (contentPos != -1) {
          for (int i = contentPos; i < fileContentConnectionsOrig.length; i++) {
            if (fileContentConnectionsOrig[i] == NEW_LINE_CHAR) {
              charArrayCount = i - contentPos;
              break;
            }
            if (fileContentConnectionsOrig[i] == ZERO_CHAR) {
              charArrayCount = 0;
              break;
            }
          }
          content = new char[charArrayCount];
          for (int i = contentPos; i < contentPos + charArrayCount; i++) {
            content[i - contentPos] = fileContentConnectionsOrig[i];
          }
        }
      }
      pos = 0;
      contentPos = 0;
      charArrayCount = 0;
      newLines = 0;
      toSearch = null;
    }
    return content;
  }

  /**
   * Returns the database user of the connection record identified by dbtype and connna.
   *
   * @param dbtype the database type of the record
   * @param connna the connection name of the record
   * @return the database user string, or an empty string if not found
   */
  static final String getDbuser(String dbtype, String connna) {
    return new String(getContentData(dbtype, connna, 0));
  }

  /**
   * Returns the database password of the connection record identified by dbtype and connna.
   *
   * @param dbtype the database type of the record
   * @param connna the connection name of the record
   * @return the database password string, or an empty string if not found
   */
  static final String getDbpass(String dbtype, String connna) {
    return new String(getContentData(dbtype, connna, 1));
  }

  /**
   * Returns the JDBC driver of the connection record identified by dbtype and connna.
   *
   * @param dbtype the database type of the record
   * @param connna the connection name of the record
   * @return the driver string, or an empty string if not found
   */
  static final String getDriver(String dbtype, String connna) {
    return new String(getContentData(dbtype, connna, 2));
  }

  /**
   * Returns the connection string of the connection record identified by dbtype and connna.
   *
   * @param dbtype the database type of the record
   * @param connna the connection name of the record
   * @return the connection string, or an empty string if not found
   */
  static final String getConnst(String dbtype, String connna) {
    return new String(getContentData(dbtype, connna, 3));
  }

  /**
   * Searches the in-memory content, starting past the header and initialization log line, for the
   * record matching the given dbtype and connna, and returns the buffer index just after its connna
   * line. The match is accepted only when it begins a proper 6-line record boundary.
   *
   * @param dbtype the database type to match
   * @param connna the connection name to match
   * @return the index immediately following the matched connna line, or -1 if not found
   */
  static final int getConnnaPos(String dbtype, String connna) {
    int pos = -1;
    int newLines = 0;
    boolean innerBreak = false;
    String toSearch = NEW_LINE_STRING + dbtype + NEW_LINE_CHAR + connna + NEW_LINE_CHAR;
    if (isFileContentConnectionsOrigReady()) {
      for (int i =
              getHeaderLength(fileContentConnectionsOrig)
                  + APP_DATE_FORMAT_FOR_DISPLAYING.length()
                  + SEP9.length()
                  + MESSAGE_LOG_APPLICATION_INSTANCE_INITIALIZE.length()
                  + 1
                  - 1;
          i < fileContentConnectionsOrig.length - toSearch.length();
          i++) {
        if (fileContentConnectionsOrig[i] == NEW_LINE_CHAR) {
          newLines++;
        }
        innerBreak = false;
        for (int j = 0; j < toSearch.length(); j++) {
          if (fileContentConnectionsOrig[i + j] != toSearch.charAt(j)) {
            innerBreak = true;
            break;
          }
        }
        if (!innerBreak && newLines % 6 == 1) {
          pos = i + 1;
          break;
        }
      }
    }
    return pos;
  }

  /**
   * Returns the statement delimiter associated with the given database type.
   *
   * @param dbtype the database type
   * @return the delimiter for that type, or an empty string if the type is unrecognized
   */
  static final String getDelimiter(String dbtype) {
    if (dbtype.equals(DB_TYPE_MYSQL)) {
      return delimiterMysql;
    } else if (dbtype.equals(DB_TYPE_ORACLE)) {
      return delimiterOracle;
    } else if (dbtype.equals(DB_TYPE_MSSQL)) {
      return delimiterMssql;
    } else if (dbtype.equals(DB_TYPE_DB2)) {
      return delimiterDb2;
    } else if (dbtype.equals(DB_TYPE_POSTGRESQL)) {
      return delimiterPostgresql;
    } else {
      return "";
    }
  }

  /**
   * Returns the statement delimiter associated with the current database type held in the shared
   * state. Exits if that database type is null.
   *
   * @return the delimiter for the current database type, or an empty string if it is unrecognized
   */
  static final String getDelimiter() {
    if (dbType != null) {
      if (dbType.equals(DB_TYPE_MYSQL)) {
        return delimiterMysql;
      } else if (dbType.equals(DB_TYPE_ORACLE)) {
        return delimiterOracle;
      } else if (dbType.equals(DB_TYPE_MSSQL)) {
        return delimiterMssql;
      } else if (dbType.equals(DB_TYPE_DB2)) {
        return delimiterDb2;
      } else if (dbType.equals(DB_TYPE_POSTGRESQL)) {
        return delimiterPostgresql;
      } else {
        return "";
      }
    } else {
      throw systemexit("Error - dbType is null, getDelimiter");
    }
  }
}
