package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Connections.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.Crypto.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.QueryStore.*;
import static com.kisscodesystems.MyDbConns.State.*;
import static com.kisscodesystems.MyDbConns.Utils.*;
import static com.kisscodesystems.MyDbConns.Validate.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import org.junit.Test;

/**
 * Regression tests for the pure/static MyDbConns helpers after the flat-module refactor. Oracles
 * are derived from the current {@link Const} values (so the tests track policy changes), and
 * validators are called with messageIfNot=false to keep the console quiet.
 */
public final class MyDbConnsTest {

  @Test
  public void validDbTypesAreAccepted() {
    assertTrue(isValidDbType(DB_TYPE_MYSQL, false));
    assertTrue(isValidDbType(DB_TYPE_ORACLE, false));
    assertTrue(isValidDbType(DB_TYPE_MSSQL, false));
    assertTrue(isValidDbType(DB_TYPE_DB2, false));
    assertTrue(isValidDbType(DB_TYPE_POSTGRESQL, false));
  }

  @Test
  public void invalidDbTypesAreRejected() {
    assertFalse(isValidDbType("not-a-db", false));
    assertFalse(isValidDbType("", false));
    assertFalse(isValidDbType(DB_TYPE_MYSQL.toUpperCase() + "x", false));
  }

  @Test
  public void connectionsFilePathMustBeInDirWithKnownPostfix() {
    assertTrue(isValidConnectionsFilePath(APP_CONNECTIONS_DIR + SEP + "cs" + APP_CS_POSTFIX));
    assertTrue(isValidConnectionsFilePath(APP_CONNECTIONS_DIR + SEP + "cs" + APP_SL_POSTFIX));
    assertTrue(isValidConnectionsFilePath(APP_CONNECTIONS_DIR + SEP + "cs" + APP_IV_POSTFIX));
    assertTrue(isValidConnectionsFilePath(APP_CONNECTIONS_DIR + SEP + "cs" + APP_NW_POSTFIX));
    assertFalse(isValidConnectionsFilePath("cs" + APP_CS_POSTFIX)); // missing dir prefix
    assertFalse(
        isValidConnectionsFilePath(APP_CONNECTIONS_DIR + SEP + "cs.txt")); // unknown postfix
    assertFalse(isValidConnectionsFilePath(null));
  }

  @Test
  public void resultTargetsValidation() {
    assertTrue(isValidResultTargets(RESULT_TARGET_CONS_VALUE));
    assertTrue(isValidResultTargets(RESULT_TARGET_FILE_VALUE));
    assertTrue(isValidResultTargets(RESULT_TARGET_CONS_VALUE + RESULT_TARGET_FILE_VALUE));
    assertFalse(isValidResultTargets("zzz"));
    assertFalse(isValidResultTargets(""));
  }

  @Test
  public void resultFormatsValidation() {
    assertTrue(isValidResultFormats(RESULT_FORMAT_TXT_VALUE));
    assertTrue(isValidResultFormats(RESULT_FORMAT_CSV_VALUE));
    assertTrue(isValidResultFormats(RESULT_FORMAT_HTM_VALUE));
    assertTrue(
        isValidResultFormats(
            RESULT_FORMAT_TXT_VALUE + RESULT_FORMAT_CSV_VALUE + RESULT_FORMAT_HTM_VALUE));
    assertFalse(isValidResultFormats("qqq"));
  }

  @Test
  public void weakGoodPasswordsAreRejected() {
    assertFalse(isValidGoodPassword("short".toCharArray(), false));
    assertFalse(isValidGoodPassword("alllowercaseletters".toCharArray(), false));
    assertFalse(isValidGoodPassword(new char[0], false));
  }

  @Test
  public void strongGoodPasswordIsAccepted() {
    // Build a password that comfortably exceeds every current Const threshold.
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < APP_GOOD_PASSWORD_MIN_COUNT_OF_UC_LETTERS + 2; i++) sb.append('A');
    for (int i = 0; i < APP_GOOD_PASSWORD_MIN_COUNT_OF_LC_LETTERS + 2; i++) sb.append('a');
    for (int i = 0; i < APP_GOOD_PASSWORD_MIN_COUNT_OF_DIGITS + 2; i++) sb.append('7');
    for (int i = 0; i < APP_GOOD_PASSWORD_MIN_COUNT_OF_SPEC_CHARS + 2; i++) sb.append('!');
    while (sb.length() < APP_GOOD_PASSWORD_MIN_LENGTH_OF_GOOD_PASSWORDS + 2) sb.append('x');
    assertTrue(isValidGoodPassword(sb.toString().toCharArray(), false));
  }

  @Test
  public void padPadsAndTruncates() {
    assertEquals("ab   ", pad("ab", 5, ' '));
    assertEquals("abc", pad("abcdef", 3, ' '));
    assertEquals("x", pad("x", -1, ' ')); // negative length leaves it unchanged
  }

  @Test
  public void joinArrayListIntegerJoinsWithSeparator() {
    ArrayList<Integer> l = new ArrayList<Integer>();
    l.add(1);
    l.add(2);
    l.add(3);
    assertEquals("1-2-3", joinArrayListInteger(l, "-"));
    assertEquals("", joinArrayListInteger(new ArrayList<Integer>(), "-"));
  }

  @Test
  public void asciiConversionsRoundTrip() {
    char[] chars = "Hello, World! 123".toCharArray();
    byte[] bytes = toBytesASCII(chars);
    assertArrayEquals(chars, toCharsASCII(bytes));
  }

  @Test
  public void asciiClassifiers() {
    assertTrue(isASCIIandNONSPACE("abcDEF123".toCharArray()));
    assertFalse(isASCIIandNONSPACE("has space".toCharArray()));
    assertFalse(isASCIIandNONSPACE(new char[] {'a', (char) 200}));
    assertTrue(isASCIIorNEWLINE('A'));
    assertTrue(isASCIIorNEWLINE('\n'));
    assertFalse(isASCIIorNEWLINE((char) 200));
  }

  @Test
  public void getDelimiterDefaultsToEmpty() {
    assertEquals("", getDelimiter(DB_TYPE_MYSQL));
    assertEquals("", getDelimiter(DB_TYPE_ORACLE));
  }

  /**
   * Exercises the real AES/GCM encryption end to end: build a minimal connections-file content,
   * {@link Connections#saveFile()} it to a temporary {@code cs} folder, then decrypt it back with
   * {@link Connections#getFileContent()} and confirm the content survives. A wrong password must
   * fail the GCM tag check and refuse to decrypt.
   */
  @Test
  public void connectionsFileGcmRoundTrip() {
    File csDir = new File(APP_CONNECTIONS_DIR);
    deleteDir(csDir);
    assertTrue(csDir.mkdirs());
    try {
      String ini =
          new String(generateRandomHeader())
              + "12/31/2026 23:59:59"
              + SEP9
              + MESSAGE_LOG_APPLICATION_INSTANCE_INITIALIZE
              + NEW_LINE_CHAR;
      fileContentConnectionsOrig = new char[APP_FILE_CONTENT_MAX_LENGTH];
      for (int i = 0; i < ini.length(); i++) {
        fileContentConnectionsOrig[i] = ini.charAt(i);
      }
      passwordForConnections = "Round-Trip-Pass-9!".toCharArray();
      assertTrue(saveFile());

      // Wipe the in-memory copy, keep the same password, and decrypt from disk.
      clearCharArray(fileContentConnectionsOrig, ZERO_CHAR);
      fileContentConnectionsOrig = new char[0];
      assertTrue(getFileContent());
      assertEquals(ini, new String(fileContentConnectionsOrig, 0, ini.length()));

      // A wrong password must fail the GCM authentication tag and not decrypt.
      passwordForConnections = "Totally-Wrong-Pass-1!".toCharArray();
      fileContentConnectionsOrig = new char[0];
      assertFalse(getFileContent());
    } finally {
      deleteDir(csDir);
    }
  }

  @Test
  public void connectionNavigationWorksPastRandomHeader() {
    // Lay out decrypted content exactly as the app does: a random header line, the fixed-length
    // init log line, then one 6-field connection block. The navigation must locate it regardless
    // of the (variable) random header length, via getHeaderLength.
    char[] header = generateRandomHeader();
    StringBuilder date = new StringBuilder();
    for (int i = 0; i < APP_DATE_FORMAT_FOR_DISPLAYING.length(); i++) {
      date.append('X');
    }
    String line1 = date + SEP9 + MESSAGE_LOG_APPLICATION_INSTANCE_INITIALIZE + NEW_LINE_CHAR;
    String conn =
        DB_TYPE_MYSQL + "\n" + "myconn\n" + "user\n" + "pass\n" + "driver\n" + "connstr\n";
    String content = new String(header) + line1 + conn;
    fileContentConnectionsOrig = new char[APP_FILE_CONTENT_MAX_LENGTH];
    for (int i = 0; i < content.length(); i++) {
      fileContentConnectionsOrig[i] = content.charAt(i);
    }
    assertEquals(header.length + line1.length(), getConnnaPos(DB_TYPE_MYSQL, "myconn"));
    assertEquals("user", getDbuser(DB_TYPE_MYSQL, "myconn"));
    fileContentConnectionsOrig = new char[0];
  }

  @Test
  public void randomHeaderIsRandomAndBounded() {
    char[] h1 = generateRandomHeader();
    char[] h2 = generateRandomHeader();
    // Length: min..max letters plus the terminating newline.
    assertTrue(h1.length >= APP_HEADER_MIN_LETTERS + 1 && h1.length <= APP_HEADER_MAX_LETTERS + 1);
    assertEquals(NEW_LINE_CHAR, h1[h1.length - 1]);
    for (int i = 0; i < h1.length - 1; i++) {
      assertTrue(h1[i] >= 'a' && h1[i] <= 'z');
    }
    // Two headers must not be identical (random content and/or length).
    assertFalse(new String(h1).equals(new String(h2)));
  }

  @Test
  public void parseIntSafeReturnsFallbackInsteadOfThrowing() {
    // Non-numeric and out-of-range command arguments must not throw (which would terminate the
    // app);
    // they map to the fallback so the caller treats them as an invalid id.
    assertEquals(-1, parseIntSafe("asdf", -1));
    assertEquals(-1, parseIntSafe("", -1));
    assertEquals(-1, parseIntSafe(null, -1));
    assertEquals(-1, parseIntSafe("99999999999", -1)); // overflows int
    assertEquals(1, parseIntSafe("not-a-number", 1));
    // Valid integers (with surrounding whitespace) still parse.
    assertEquals(42, parseIntSafe("  42 ", -1));
    assertEquals(-5, parseIntSafe("-5", -1));
  }

  @Test
  public void requestStringSplitDoesNotThrowOnLeadingEscapedQuote() {
    // An interactive line beginning with a backslash-quote used to index substring(-1,..): must not
    // throw now, and the escaped quote is treated as a literal (not a string delimiter).
    String[] p =
        requestStringSplit(
            "\\\"x\\\"", DOUBLE_SPACE, DOUBLE_QUOTE, SINGLE_SPACE, BACKSLA, KEY_BEGIN_END);
    assertTrue(p != null);
    // A normal quoted argument still parses into a single token with the quotes stripped.
    String[] q =
        requestStringSplit(
            "query factory \"a b\"",
            DOUBLE_SPACE,
            DOUBLE_QUOTE,
            SINGLE_SPACE,
            BACKSLA,
            KEY_BEGIN_END);
    assertEquals("a b", q[q.length - 1]);
  }

  private static void deleteDir(File dir) {
    if (dir.exists()) {
      File[] files = dir.listFiles();
      if (files != null) {
        for (File f : files) {
          f.delete();
        }
      }
      dir.delete();
    }
  }
}
