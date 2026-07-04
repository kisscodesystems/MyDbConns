package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.Validate.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Provides low-level file input/output for MyDbConns: reading and writing raw bytes (used for the
 * encrypted connections files) and reading and writing text content (used for query files and
 * result output).
 */
final class FileStore {
  /**
   * Reads the entire contents of the given connections file into a byte array, after validating the
   * path as a connections file path. Exits on a missing file or an I/O error.
   *
   * @param filePath the path of the connections file to read
   * @return the file bytes, or an empty array if the path is not a valid connections file path
   */
  static final byte[] readFileBytes(String filePath) {
    byte[] bytes = new byte[0];
    if (isValidConnectionsFilePath(filePath)) {
      File file = new File(filePath);
      if (file.exists() && file.isFile()) {
        bytes = new byte[(int) file.length()];
        FileInputStream fis = null;
        try {
          fis = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
          throw systemexit("Exception - FileNotFoundException, readFileBytes");
        }
        try {
          fis.read(bytes);
        } catch (IOException e) {
          throw systemexit("Exception - IOException, readFileBytes");
        } finally {
          try {
            fis.close();
          } catch (Exception e) {
            throw systemexit("Exception - Exception, readFileBytes");
          }
        }
        fis = null;
      } else {
        throw systemexit("Error - File does not exist or it is not a file, readFileBytes");
      }
    }
    return bytes;
  }

  /**
   * Writes the given byte array to the given connections file, after validating the path as a
   * connections file path. Exits if the bytes are null or on an I/O error.
   *
   * @param filePath the path of the connections file to write
   * @param bytes the bytes to write
   */
  static final void writeFileBytes(String filePath, byte[] bytes) {
    if (isValidConnectionsFilePath(filePath)) {
      if (bytes != null) {
        FileOutputStream fos = null;
        try {
          fos = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
          throw systemexit("Exception - FileNotFoundException, writeFileBytes");
        }
        try {
          fos.write(bytes);
        } catch (IOException e) {
          throw systemexit("Exception - Exception, writeFileBytes");
        } finally {
          try {
            fos.close();
          } catch (Exception e) {
            throw systemexit("Exception - Exception, writeFileBytes");
          }
        }
        fos = null;
      } else {
        throw systemexit("Error - bytes is null, writeFileBytes");
      }
    }
  }

  /**
   * Reads text from the given file, either only its first line or the whole file with newline
   * separators, after validating the file path. Prints a message if the file is missing or the path
   * is not allowed, and exits if the file name is null.
   *
   * @param fileName the path of the text file to read
   * @param firstLineHeaderOnly when true, only the first line is read; when false, the entire file
   *     is read
   * @return the file content as a string, or an empty string if it could not be read
   */
  static final String readFileContent(String fileName, boolean firstLineHeaderOnly) {
    String contentString = "";
    if (fileName != null) {
      if (isValidFilePath(fileName, true)) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
          BufferedReader br = null;
          try {
            br = new BufferedReader(new FileReader(fileName));
          } catch (FileNotFoundException e) {
            throw systemexit("Exception - FileNotFoundException, readFileContent");
          }
          StringBuilder sb = new StringBuilder();
          String line = null;
          try {
            line = br.readLine();
            while (line != null && !firstLineHeaderOnly) {
              sb.append(line);
              sb.append(NEW_LINE_CHAR);
              line = br.readLine();
            }
            if (line != null) {
              sb.append(line);
            }
            contentString = sb.toString();
            br.close();
          } catch (IOException e) {
            throw systemexit("Exception - IOException, readFileContent");
          }
          sb = null;
          line = null;
          br = null;
        } else {
          outprintln(MESSAGE_YOUR_FILE_HAS_NOT_BEEN_FOUND_OR_NOT_BEEN_FILE);
        }
        file = null;
      } else {
        outprintln(MESSAGE_FILES_ALLOWED_BEING_NEXT_TO_THE_APPLICATION);
      }
    } else {
      throw systemexit("Error - filePath is null, readFileContent");
    }
    if (contentString == null) {
      contentString = "";
    }
    return contentString;
  }

  /**
   * Writes the given text to a newly created file, after validating the file path. Exits if the
   * file already exists or on an I/O error while creating or writing the file.
   *
   * @param result the text content to write
   * @param fileName the path of the file to create and write
   */
  static final void writeFileContent(String result, String fileName) {
    if (isValidFilePath(fileName, true)) {
      File file = new File(fileName);
      if (!file.exists()) {
        try {
          file.createNewFile();
        } catch (IOException e) {
          throw systemexit("Exception - IOException (1), writeFileContent");
        }
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
          fileWriter = new FileWriter(file.getAbsoluteFile());
          bufferedWriter = new BufferedWriter(fileWriter);
          try {
            bufferedWriter.write(result);
          } catch (IOException e) {
            throw systemexit("Exception - IOException (2), writeFileContent");
          }
        } catch (IOException e) {
          throw systemexit("Exception - IOException (3), writeFileContent");
        } finally {
          if (bufferedWriter != null) {
            try {
              bufferedWriter.close();
            } catch (IOException e) {
              throw systemexit("Exception - IOException (4), writeFileContent");
            }
            bufferedWriter = null;
          }
          if (fileWriter != null) {
            try {
              fileWriter.close();
            } catch (IOException e) {
              throw systemexit("Exception - IOException (5), writeFileContent");
            }
            fileWriter = null;
          }
        }
      } else {
        throw systemexit(MESSAGE_RESULT_FILE_ALREADY_EXISTS + fileName);
      }
    }
  }
}
