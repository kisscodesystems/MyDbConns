package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Const.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLXML;
import java.util.ArrayList;
import java.util.HashMap;
import oracle.jdbc.OracleBfile;

/**
 * Collection of stateless helper methods used across MyDbConns, including reading JDBC column
 * values, exporting large object (XML/BLOB/CLOB/RAW/BFILE) columns to files, formatting elapsed
 * time, joining and padding strings, splitting a request line into parameters, clearing sensitive
 * buffers and performing ASCII classification and conversion.
 */
public final class Utils {
  /**
   * Returns the length of the header portion of the given content, defined as the index just past
   * the first newline character.
   *
   * @param content the character array to inspect, may be null
   * @return the index immediately after the first newline character, or -1 if content is null or
   *     contains no newline
   */
  static final int getHeaderLength(char[] content) {
    if (content != null) {
      for (int i = 0; i < content.length; i++) {
        if (content[i] == NEW_LINE_CHAR) {
          return i + 1;
        }
      }
    }
    return -1;
  }

  /**
   * Reads the value of the named column from the current row of the result set. Any error or
   * exception thrown while reading is caught and its trimmed string representation is returned as
   * the value instead.
   *
   * @param rs the result set to read from, may be null
   * @param colname the name of the column to read
   * @return the column value as an Object, the trimmed error text on failure, or null if the result
   *     set is null
   */
  static final Object getVal(ResultSet rs, String colname) {
    Object val = null;
    if (rs != null) {
      try {
        val = rs.getObject(colname);
      } catch (Error e) {
        val = e.toString().trim();
      } catch (Exception e) {
        val = e.toString().trim();
      }
    }
    return val;
  }

  /**
   * Converts a value to its string representation, substituting a caller-supplied placeholder when
   * the value is null.
   *
   * @param val the value to convert, may be null
   * @param nullStr the string to return when val is null
   * @return the value's toString result, or nullStr if val is null
   */
  static final String getValStr(Object val, String NULL_STR) {
    if (val == null) {
      return NULL_STR;
    } else {
      return val.toString();
    }
  }

  /**
   * Reads the named SQLXML column from the result set and writes its string content to the given
   * file using the specified character encoding.
   *
   * @param rs the result set to read from
   * @param destfile the path of the file to write the XML content to
   * @param colname the name of the SQLXML column to read
   * @param utf8 the character encoding used to convert the XML string to bytes
   * @return true if the content was written successfully, false if any error occurred
   */
  static final boolean getXml(ResultSet rs, String destfile, String colname, String UTF8) {
    boolean separately = false;
    SQLXML sxml = null;
    FileOutputStream fout = null;
    String buff = null;
    try {
      sxml = rs.getSQLXML(colname);
      buff = sxml.getString();
      fout = new FileOutputStream(destfile);
      fout.write(buff.getBytes(UTF8));
      fout.flush();
      separately = true;
    } catch (Exception e) {
      separately = false;
    } finally {
      try {
        if (fout != null) {
          fout.close();
        }
      } catch (Exception e) {
        separately = false;
      }
    }
    sxml = null;
    fout = null;
    buff = null;
    return separately;
  }

  /**
   * Reads the named BLOB column from the result set and writes its raw bytes to the given file. The
   * file is only written when it does not already exist.
   *
   * @param rs the result set to read from
   * @param destfile the path of the file to write the BLOB content to
   * @param colname the name of the BLOB column to read
   * @return true if the content was written successfully, false if the file already exists or any
   *     error occurred
   */
  static final boolean getBlob(ResultSet rs, String destfile, String colname) {
    boolean separately = false;
    Blob BLOB = null;
    File file = null;
    FileOutputStream fout = null;
    byte[] buff = null;
    try {
      BLOB = rs.getBlob(colname);
      file = new File(destfile);
      if (!file.exists()) {
        fout = new FileOutputStream(destfile);
        buff = BLOB.getBytes(1, (int) BLOB.length());
        fout.write(buff);
        fout.flush();
        separately = true;
      }
    } catch (Exception e) {
      separately = false;
    } finally {
      try {
        if (fout != null) {
          fout.close();
        }
      } catch (Exception e) {
        separately = false;
      }
    }
    BLOB = null;
    file = null;
    fout = null;
    buff = null;
    return separately;
  }

  /**
   * Reads the named CLOB column from the result set and writes its character content to the given
   * file using the specified encoding. The file is only written when it does not already exist.
   *
   * @param rs the result set to read from
   * @param destfile the path of the file to write the CLOB content to
   * @param colname the name of the CLOB column to read
   * @param utf8 the character encoding used to convert the CLOB string to bytes
   * @return true if the content was written successfully, false if the file already exists or any
   *     error occurred
   */
  static final boolean getClob(ResultSet rs, String destfile, String colname, String UTF8) {
    boolean separately = false;
    Clob CLOB = null;
    File file = null;
    FileOutputStream fout = null;
    String buff = null;
    try {
      CLOB = rs.getClob(colname);
      file = new File(destfile);
      if (!file.exists()) {
        fout = new FileOutputStream(destfile);
        buff = CLOB.getSubString(1, (int) CLOB.length());
        fout.write(buff.getBytes(UTF8));
        fout.flush();
        separately = true;
      }
    } catch (Exception e) {
      separately = false;
    } finally {
      try {
        if (fout != null) {
          fout.close();
        }
      } catch (Exception e) {
        separately = false;
      }
    }
    CLOB = null;
    file = null;
    fout = null;
    buff = null;
    return separately;
  }

  /**
   * Reads the named binary (RAW) column from the result set via its binary stream and writes it to
   * the given file in fixed-size chunks. The file is only written when it does not already exist.
   *
   * @param rs the result set to read from
   * @param destfile the path of the file to write the binary content to
   * @param colname the name of the binary column to read
   * @param bufflength the size in bytes of the buffer used while copying the stream
   * @return true if the content was written successfully, false if the file already exists or any
   *     error occurred
   */
  static final boolean getRaw(ResultSet rs, String destfile, String colname, int BUFFLENGTH) {
    boolean separately = false;
    InputStream inst = null;
    File file = null;
    FileOutputStream fout = null;
    byte[] buff = null;
    try {
      inst = rs.getBinaryStream(colname);
      file = new File(destfile);
      if (!file.exists()) {
        fout = new FileOutputStream(destfile);
        buff = new byte[BUFFLENGTH];
        while (inst.read(buff) != -1) {
          fout.write(buff);
        }
        fout.flush();
        separately = true;
      }
    } catch (Exception e) {
      separately = false;
    } finally {
      try {
        if (fout != null) {
          fout.close();
        }
      } catch (Exception e) {
        separately = false;
      }
      try {
        if (inst != null) {
          inst.close();
        }
      } catch (Exception e) {
        separately = false;
      }
    }
    inst = null;
    file = null;
    fout = null;
    buff = null;
    return separately;
  }

  /**
   * Reads the named Oracle BFILE column from the result set, opens it, and writes its binary
   * content to the given file in fixed-size chunks. The file is only written when it does not
   * already exist.
   *
   * @param rs the result set to read from
   * @param destfile the path of the file to write the BFILE content to
   * @param colname the name of the BFILE column to read
   * @param bufflength the size in bytes of the buffer used while copying the stream
   * @return true if the content was written successfully, false if the file already exists or any
   *     error occurred
   */
  static final boolean getBfile(ResultSet rs, String destfile, String colname, int BUFFLENGTH) {
    boolean separately = false;
    OracleBfile BFILE = null;
    InputStream inst = null;
    File file = null;
    FileOutputStream fout = null;
    byte[] buff = null;
    try {
      BFILE = (OracleBfile) rs.getObject(colname);
      BFILE.openFile();
      inst = BFILE.getBinaryStream();
      file = new File(destfile);
      if (!file.exists()) {
        fout = new FileOutputStream(destfile);
        buff = new byte[BUFFLENGTH];
        while (inst.read(buff) != -1) {
          fout.write(buff);
        }
        fout.flush();
        separately = true;
      }
    } catch (Exception e) {
      separately = false;
    } finally {
      try {
        if (fout != null) {
          fout.close();
        }
      } catch (Exception e) {
        separately = false;
      }
      try {
        if (inst != null) {
          inst.close();
        }
      } catch (Exception e) {
        separately = false;
      }
      try {
        if (BFILE != null) {
          BFILE.closeFile();
        }
      } catch (Exception e) {
        separately = false;
      }
    }
    BFILE = null;
    inst = null;
    file = null;
    fout = null;
    buff = null;
    return separately;
  }

  /**
   * Formats a duration given in milliseconds into a human-readable string composed of hours,
   * minutes, seconds and milliseconds (for example "1h 2m 3s 4ms"), omitting leading units that are
   * zero and returning "0ms" for a zero duration.
   *
   * @param elapsedMs the elapsed time in milliseconds
   * @return the formatted elapsed-time string
   */
  static final String calculateElapsed(long elapsedMs) {
    String totalElapsedTime = "";
    long milliseconds = 0;
    long seconds = 0;
    long minutes = 0;
    long hours = 0;
    boolean zeroIsNeeded = false;
    if (elapsedMs >= 3600 * 1000) {
      hours = (long) Math.floor(elapsedMs / (3600 * 1000));
      elapsedMs -= hours * (3600 * 1000);
    }
    if (elapsedMs >= 60 * 1000) {
      minutes = (long) Math.floor(elapsedMs / (60 * 1000));
      elapsedMs -= minutes * (60 * 1000);
    }
    if (elapsedMs >= 1000) {
      seconds = (long) Math.floor(elapsedMs / 1000);
      elapsedMs -= seconds * (1000);
    }
    milliseconds = elapsedMs;
    if (hours > 0) {
      zeroIsNeeded = true;
      totalElapsedTime += hours + "h ";
    }
    if (minutes > 0 || zeroIsNeeded) {
      zeroIsNeeded = true;
      totalElapsedTime += minutes + "m ";
    }
    if (seconds > 0 || zeroIsNeeded) {
      zeroIsNeeded = true;
      totalElapsedTime += seconds + "s ";
    }
    if (milliseconds > 0 || zeroIsNeeded) {
      totalElapsedTime += milliseconds + "ms";
    }
    if (hours == 0 && minutes == 0 && seconds == 0 && milliseconds == 0) {
      totalElapsedTime = "0ms";
    }
    milliseconds = 0;
    seconds = 0;
    minutes = 0;
    hours = 0;
    zeroIsNeeded = false;
    return totalElapsedTime;
  }

  /**
   * Joins the given list of integers into a single string, placing the separator between elements
   * and removing the trailing separator.
   *
   * @param elements the integers to join, may be null
   * @param sep1 the separator placed between elements, may be null
   * @return the joined string, or an empty string if either argument is null
   */
  static final String joinArrayListInteger(ArrayList<Integer> elements, String SEP1) {
    String s = "";
    if (elements != null && SEP1 != null) {
      for (int element : elements) {
        s += String.valueOf(element) + SEP1;
      }
      if (s.endsWith(SEP1)) {
        s = s.substring(0, s.length() - SEP1.length());
      }
    }
    return s;
  }

  /**
   * Pads or truncates the given string to an exact length. When i is non-negative the string is
   * padded on the right with the padding character until it reaches length i and then cut to
   * exactly i characters; a negative i leaves the string unchanged.
   *
   * @param s the source string
   * @param i the target length, or a negative value to leave the string unchanged
   * @param spaceChar the character used for right padding
   * @return the padded and truncated string
   */
  static final String pad(String s, int i, char SPACE_CHAR) {
    String b = new String(s);
    if (i > -1) {
      while (b.length() < i) {
        b = b + SPACE_CHAR;
      }
      b = b.substring(0, i);
    }
    return b;
  }

  /**
   * Splits a request line into its individual parameters. Consecutive spaces are collapsed to a
   * single space, and quoted substrings (honoring backslash escaping of the quote character) are
   * treated as single parameters with their surrounding quotes removed. Each resulting parameter is
   * trimmed.
   *
   * @param args the request line to split, may be null
   * @param doubleSpace the two-space sequence collapsed into a single space
   * @param doubleQuote the quote character used to delimit strings
   * @param singleSpace the single-space string used as the split delimiter
   * @param backsla the backslash string used to detect escaped quotes
   * @param keyBeginEnd the marker wrapping placeholder keys for extracted quoted strings
   * @return the array of parsed parameters, or an empty array if any argument is null or the quotes
   *     are unbalanced
   */
  static final String[] requestStringSplit(
      String args,
      String DOUBLE_SPACE,
      String DOUBLE_QUOTE,
      String SINGLE_SPACE,
      String BACKSLA,
      String KEY_BEGIN_END) {
    String[] requestParams = null;
    String requestString = null;
    HashMap<String, String> strings = new HashMap<String, String>();
    String tempArgs = "";
    int charPos = 0;
    boolean inString = false;
    String tempString = "";
    String tempStringKey = "";
    int count = 0;
    if (args != null
        && DOUBLE_SPACE != null
        && DOUBLE_QUOTE != null
        && SINGLE_SPACE != null
        && BACKSLA != null
        && KEY_BEGIN_END != null) {
      requestString = new String(args);
      while (requestString.contains(DOUBLE_SPACE)) {
        requestString = requestString.replace(DOUBLE_SPACE, SINGLE_SPACE);
      }
      if (requestString.contains(DOUBLE_QUOTE)) {
        count = 0;
        for (int i = 0; i < requestString.length(); i++) {
          if (requestString.charAt(i) == (char) 34) {
            count++;
          }
        }
        if (count % 2 == 0) {
          if (requestString.startsWith(DOUBLE_QUOTE)
              || requestString.startsWith(SINGLE_SPACE + DOUBLE_QUOTE)) {
            requestString = DOUBLE_SPACE + requestString;
          }
          requestString =
              requestString.replace(
                  DOUBLE_QUOTE + DOUBLE_QUOTE, DOUBLE_QUOTE + SINGLE_SPACE + DOUBLE_QUOTE);
          charPos = 0;
          inString = false;
          while (charPos < requestString.length()) {
            if (requestString.substring(charPos, charPos + 1).equals(DOUBLE_QUOTE)) {
              if (charPos == 0 || !requestString.substring(charPos - 1, charPos).equals(BACKSLA)) {
                inString = !inString;
              } else {
                if (charPos >= 2
                    && requestString.substring(charPos - 2, charPos - 1).equals(BACKSLA)) {
                  inString = !inString;
                }
              }
            }
            if (inString) {
              tempString += requestString.substring(charPos, charPos + 1);
            } else {
              if (!"".equals(tempString)) {
                tempString += requestString.substring(charPos, charPos + 1);
                tempStringKey = KEY_BEGIN_END + strings.size() + KEY_BEGIN_END;
                strings.put(tempStringKey, tempString);
                tempArgs += tempStringKey;
                tempString = "";
              } else {
                tempArgs += requestString.substring(charPos, charPos + 1);
              }
            }
            charPos++;
          }
          requestParams = tempArgs.trim().split(SINGLE_SPACE);
          for (int i = 0; i < requestParams.length; i++) {
            if (requestParams[i].startsWith(KEY_BEGIN_END)
                && requestParams[i].endsWith(KEY_BEGIN_END)) {
              requestParams[i] = strings.get(requestParams[i]);
              requestParams[i] = requestParams[i].substring(1, requestParams[i].length() - 1);
            }
          }
        } else {
          requestParams = new String[0];
        }
      } else {
        requestParams = requestString.split(SINGLE_SPACE);
      }
      for (int i = 0; i < requestParams.length; i++) {
        requestParams[i] = requestParams[i].trim();
      }
    } else {
      requestParams = new String[0];
    }
    requestString = null;
    strings = null;
    tempArgs = null;
    charPos = 0;
    inString = false;
    tempString = null;
    tempStringKey = null;
    count = 0;
    return requestParams;
  }

  /**
   * Overwrites every element of the given character array with the specified fill character, used
   * to scrub sensitive data from memory.
   *
   * @param charArray the character array to clear, may be null
   * @param zeroChar the character to write into every position
   */
  static final void clearCharArray(char[] charArray, char ZERO_CHAR) {
    if (charArray != null) {
      for (int i = 0; i < charArray.length; i++) {
        charArray[i] = ZERO_CHAR;
      }
    }
  }

  /**
   * Overwrites every element of the given byte array with the specified fill byte, used to scrub
   * sensitive data from memory.
   *
   * @param byteArray the byte array to clear, may be null
   * @param nullByte the byte to write into every position
   */
  static final void clearByteArray(byte[] byteArray, byte NULL_BYTE) {
    if (byteArray != null) {
      for (int i = 0; i < byteArray.length; i++) {
        byteArray[i] = NULL_BYTE;
      }
    }
  }

  /**
   * Determines whether a single character is a printable ASCII character (codes 32 to 126) or a
   * newline.
   *
   * @param c the character to test
   * @return true if the character is printable ASCII or a newline, false otherwise
   */
  static final boolean isASCIIorNEWLINE(char c) {
    return ((c >= 32 && c <= 126) || c == 10);
  }

  /**
   * Determines whether every character in the array is a printable ASCII character (codes 32 to
   * 126) or a newline.
   *
   * @param cs the character array to test, may be null
   * @return true if all characters are printable ASCII or newlines, false if any character fails
   *     the test or the array is null
   */
  static final boolean isASCIIorNEWLINE(char[] cs) {
    boolean success = true;
    if (cs != null) {
      for (int i = 0; i < cs.length; i++) {
        if (!((cs[i] >= 32 && cs[i] <= 126) || cs[i] == 10)) {
          success = false;
          break;
        }
      }
    } else {
      success = false;
    }
    return success;
  }

  /**
   * Determines whether every character in the array is a non-space printable ASCII character (codes
   * 33 to 126).
   *
   * @param cs the character array to test, may be null
   * @return true if all characters are non-space printable ASCII, false if any character fails the
   *     test or the array is null
   */
  static final boolean isASCIIandNONSPACE(char[] cs) {
    boolean success = true;
    if (cs != null) {
      for (int i = 0; i < cs.length; i++) {
        if (!(cs[i] >= 33 && cs[i] <= 126)) {
          success = false;
          break;
        }
      }
    } else {
      success = false;
    }
    return success;
  }

  /**
   * Converts a character array to a byte array by casting each character to a byte.
   *
   * @param chars the character array to convert, may be null
   * @return a byte array with each character cast to a byte, or an empty array if chars is null
   */
  static final byte[] toBytesASCII(char[] chars) {
    byte[] bytes = new byte[0];
    if (chars != null) {
      bytes = new byte[chars.length];
      for (int i = 0; i < chars.length; i++) {
        bytes[i] = (byte) chars[i];
      }
    }
    return bytes;
  }

  /**
   * Converts a byte array to a character array by casting each byte to a char.
   *
   * @param bytes the byte array to convert, may be null
   * @return a character array with each byte cast to a char, or an empty array if bytes is null
   */
  static final char[] toCharsASCII(byte[] bytes) {
    char[] chars = new char[0];
    if (bytes != null) {
      chars = new char[bytes.length];
      for (int i = 0; i < bytes.length; i++) {
        chars[i] = (char) bytes[i];
      }
    }
    return chars;
  }
}
