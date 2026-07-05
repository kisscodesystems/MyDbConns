package com.kisscodesystems.MyDbConns;

import java.io.Console;
import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.text.SimpleDateFormat;

/**
 * Holds the application-wide constants and configuration values. These include character and byte
 * literals, numeric limits, cryptography parameters, date-format patterns, database-type and
 * driver-search strings, prompt strings, regular expressions, formatting strings, SQL type names
 * and result target/format values used throughout the application.
 */
final class Const {
  static final byte NULL_BYTE = '\0';
  static final char NEW_LINE_CHAR = '\n';
  static final char SPACE_CHAR = ' ';
  static final char MINUS_CHAR = "-".charAt(0);
  static final char PLUS_CHAR = "+".charAt(0);
  static final char ZERO_CHAR = '\0';
  static final String NEW_LINE_STRING = "\n";
  static final String BACKSLA = String.valueOf(Character.toChars(92));
  static final String DOUBLE_QUOTE = "\"";
  static final String DOUBLE_DOT = "..";
  static final String DOUBLE_SPACE = "" + SPACE_CHAR + SPACE_CHAR;
  static final String SINGLE_SPACE = "" + SPACE_CHAR;
  static final String APP_NAME = "MyDbConns";
  static final String APP_VERSION = "2.1";
  static final int APP_MAX_LENGTH_OF_INPUT = 999;
  static final int APP_MAX_LENGTH_OF_SQL = 1000000;
  static final String APP_DATA_BASE_DIR = resolveDataBaseDir();
  static final String APP_CONNECTIONS_DIR = APP_DATA_BASE_DIR + "cs";
  static final String APP_CONNECTIONS_FILE_NAME = "cs";
  static final String APP_CS_POSTFIX = ".cs";
  static final String APP_SL_POSTFIX = ".sl";
  static final String APP_IV_POSTFIX = ".iv";
  static final String APP_NW_POSTFIX = ".nw";
  static final boolean APP_CONNECTION_PASSWORD_CACHE = true;
  static final int APP_MAX_NUM_OF_CONNECTIONS = 999;
  static final int APP_HEADER_MIN_LETTERS = 8;
  static final int APP_HEADER_MAX_LETTERS = 19;
  static final int APP_FILE_CONTENT_MAX_LENGTH =
      APP_MAX_NUM_OF_CONNECTIONS * 6 * APP_MAX_LENGTH_OF_INPUT + APP_HEADER_MAX_LETTERS + 1;
  static final int APP_GOOD_PASSWORD_MIN_COUNT_OF_UC_LETTERS = 3;
  static final int APP_GOOD_PASSWORD_MIN_COUNT_OF_LC_LETTERS = 3;
  static final int APP_GOOD_PASSWORD_MIN_COUNT_OF_DIGITS = 2;
  static final int APP_GOOD_PASSWORD_MIN_COUNT_OF_SPEC_CHARS = 1;
  static final int APP_GOOD_PASSWORD_MIN_LENGTH_OF_GOOD_PASSWORDS =
      APP_GOOD_PASSWORD_MIN_COUNT_OF_UC_LETTERS
          + APP_GOOD_PASSWORD_MIN_COUNT_OF_LC_LETTERS
          + APP_GOOD_PASSWORD_MIN_COUNT_OF_DIGITS
          + APP_GOOD_PASSWORD_MIN_COUNT_OF_SPEC_CHARS;
  static final int APP_SALT_LENGTH = 256;
  static final int APP_PBE_KEY_SPEC_ITERATIONS = 600000;
  static final int APP_PBE_KEY_SPEC_KEY_LENGTH = 256;
  static final String APP_SECRET_KEY_FACTORY_INSTANCE = "PBKDF2WithHmacSHA512";
  static final String APP_SECRET_KEY_SPEC_ALGORYTHM = "AES";
  static final String APP_CIPHER_INSTANCE = "AES/GCM/NoPadding";
  static final int APP_GCM_TAG_LENGTH_BITS = 128;
  static final int APP_GCM_IV_LENGTH = 12;
  static final String APP_QUERY_FILE_PREFIX = "file://";
  static final String APP_DATE_FORMAT_FOR_DISPLAYING = "MM/dd/yyyy HH:mm:ss";
  static final String APP_DATE_FORMAT_FOR_FILENAMES = "yyyy-MM-dd-HHmmss";
  static final String APP_DATE_FORMAT_FOR_TIMESTAMPS = "MM/dd/yyyy HH:mm:ss.SSSSSS";
  static final int APP_MAX_NOT_READ_INPUTS_SECONDS = 60;
  static final int APP_MAX_NUM_OF_MILLISECONDS_TO_WAIT_FOR_THE_RESULT = 1234;
  static final int APP_MAX_COL_LENGTH_TXT = 150;
  static final int APP_MAX_QUERY_ID_TITLE_CONNNA_WIDTH = 42;
  static final Console CONSOLE = System.console();
  static final String FOLD = "" + SPACE_CHAR + SPACE_CHAR;
  static final String FOLD2 = "" + SPACE_CHAR + SPACE_CHAR + SPACE_CHAR + SPACE_CHAR + SPACE_CHAR;
  static final String SEP1 = "," + SPACE_CHAR;
  static final String SEP9 = "" + SPACE_CHAR + "|" + SPACE_CHAR;
  static final String YES = "yes";
  static final String NO = "no";
  static final String Y = "y";
  static final String N = "n";
  static final String NULL_STR = "null";
  static final String KEY_BEGIN_END = "$$$$$";
  static final String SELECT = "select";
  static final String SEP = File.separator;
  static final String FIELDS_REPLACE = "$$_fields_$$";
  static final String DB_CONN_TO_CHANGE_IN_PROMPT = "$dbConn$";
  static final String UTF8 = "UTF-8";
  static final String XML = "xml";
  static final String BYTEA = "bytea";
  static final String CLOB = "clob";
  static final String NCLOB = "nclob";
  static final String DBCLOB = "dbclob";
  static final String BLOB = "blob";
  static final String BINARY = "binary";
  static final String VARBINARY = "varbinary";
  static final String LONGVARBINARY = "longvarbinary";
  static final String IMAGE = "image";
  static final String BFILE = "bfile";
  static final String RAW = "raw";
  static final String LONGRAW = "long raw";
  static final String TIMESTAMP = "timestamp";
  static final String TIMESTAMPTZ = "timestamp with time zone";
  static final String TIMESTAMPLTZ = "timestamp with local time zone";
  static final String BIT = "bit";
  static final String TINYBLOB = "tinyblob";
  static final String MEDIUMBLOB = "mediumblob";
  static final String LONGBLOB = "longblob";
  static final int BUFFLENGTH = 4096;
  static final String FILE_FILE_PREFIX = "    file:///";
  static final String FIELDSEP_TXT = " | ";
  static final String FIELDSEP_HTM = "</td><td>";
  static final String FILE_NAME_RESULT = APP_NAME + "-result-";
  static final String FILE_POSTFIX_TXT = ".txt";
  static final String FILE_POSTFIX_CSV = ".csv";
  static final String FILE_POSTFIX_HTM = ".htm";
  static final String RESULT_TARGET_CONS_STRING = "console";
  static final String RESULT_TARGET_FILE_STRING = "file";
  static final String RESULT_TARGET_CONS_VALUE = "1";
  static final String RESULT_TARGET_FILE_VALUE = "2";
  static final String RESULT_FORMAT_TXT_STRING = "txt";
  static final String RESULT_FORMAT_CSV_STRING = "csv";
  static final String RESULT_FORMAT_HTM_STRING = "htm";
  static final String RESULT_FORMAT_TXT_VALUE = "1";
  static final String RESULT_FORMAT_CSV_VALUE = "2";
  static final String RESULT_FORMAT_HTM_VALUE = "3";
  static final String BATCH_SOURCE_FROM_FILE_VALUE = "1";
  static final String BATCH_SOURCE_FROM_RESULT_SET_VALUE = "2";
  static final String BATCH_SOURCE_FROM_FILE_STRING = "from file";
  static final String BATCH_SOURCE_FROM_RESULT_SET_STRING = "from query";
  static final String LETTERS_UCAZ = "[A-Z]";
  static final String LETTERS_LCAZ = "[a-z]";
  static final String LETTERS09 = "[0-9]";
  static final String LETTERS_SPEC_CHARS = "[.?!,;:-+_*@=<>]";
  static final String TYPE_TO_LIST_ALL = "all";
  static final String TYPE_TO_LIST_ACTIVE = "active";
  static final String TYPE_TO_LIST_INACTIVE = "inactive";
  static final String DB_TYPE_MYSQL = "mysql";
  static final String DB_TYPE_ORACLE = "oracle";
  static final String DB_TYPE_MSSQL = "mssql";
  static final String DB_TYPE_DB2 = "db2";
  static final String DB_TYPE_POSTGRESQL = "postgresql";
  static final String DB_TYPE_DRIVER_SEARCH_MYSQL = "mysql";
  static final String DB_TYPE_DRIVER_SEARCH_ORACLE = "oracle";
  static final String DB_TYPE_DRIVER_SEARCH_MSSQL = "sqlserver";
  static final String DB_TYPE_DRIVER_SEARCH_DB2 = "db2";
  static final String DB_TYPE_DRIVER_SEARCH_POSTGRESQL = "postgresql";
  static final String PROMPT_ENDING = ">" + SPACE_CHAR;
  static final String PROMPT_APP = APP_NAME + PROMPT_ENDING;
  static final String PROMPT_MYSQL =
      APP_NAME + SPACE_CHAR + DB_TYPE_MYSQL + DB_CONN_TO_CHANGE_IN_PROMPT + PROMPT_ENDING;
  static final String PROMPT_ORACLE =
      APP_NAME + SPACE_CHAR + DB_TYPE_ORACLE + DB_CONN_TO_CHANGE_IN_PROMPT + PROMPT_ENDING;
  static final String PROMPT_MSSQL =
      APP_NAME + SPACE_CHAR + DB_TYPE_MSSQL + DB_CONN_TO_CHANGE_IN_PROMPT + PROMPT_ENDING;
  static final String PROMPT_DB2 =
      APP_NAME + SPACE_CHAR + DB_TYPE_DB2 + DB_CONN_TO_CHANGE_IN_PROMPT + PROMPT_ENDING;
  static final String PROMPT_POSTGRESQL =
      APP_NAME + SPACE_CHAR + DB_TYPE_POSTGRESQL + DB_CONN_TO_CHANGE_IN_PROMPT + PROMPT_ENDING;
  static final String PROMPT_TO_UPPER_OR_EXIT = "<";
  static final SimpleDateFormat SIMPLE_DATE_FORMAT_FOR_DISPLAYING =
      new SimpleDateFormat(APP_DATE_FORMAT_FOR_DISPLAYING);
  static final SimpleDateFormat SIMPLE_DATE_FORMAT_FOR_FILENAMES =
      new SimpleDateFormat(APP_DATE_FORMAT_FOR_FILENAMES);
  static final SimpleDateFormat SIMPLE_DATE_FORMAT_FOR_TIMESTAMPS =
      new SimpleDateFormat(APP_DATE_FORMAT_FOR_TIMESTAMPS);

  /**
   * Resolves the base directory used for storing the application's data files. When the code is
   * running from a jar file it returns the absolute path of the directory containing that jar (with
   * a trailing separator); otherwise, or on any failure, it returns an empty string so that a
   * working-directory-relative default is used.
   *
   * @return the absolute base directory path with a trailing separator, or an empty string
   */
  private static final String resolveDataBaseDir() {
    try {
      CodeSource codeSource = Const.class.getProtectionDomain().getCodeSource();
      if (codeSource == null) {
        return "";
      }
      URL location = codeSource.getLocation();
      if (location == null) {
        return "";
      }
      File codeSourceFile = new File(location.toURI());
      if (codeSourceFile.isFile()) {
        File baseDir = codeSourceFile.getParentFile();
        if (baseDir != null) {
          return baseDir.getAbsolutePath() + File.separator;
        }
      }
    } catch (Exception e) {
      // Fall through to the working-directory-relative default below.
    }
    return "";
  }
}
