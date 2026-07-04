package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.LineReader.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.State.*;
import static com.kisscodesystems.MyDbConns.Utils.*;

import java.util.Date;

/**
 * Wraps all terminal interaction for MyDbConns, providing output printing and debug helpers, a
 * fatal-exit helper, input reading with length and timing limits (including up-arrow history for
 * requests and masked password entry), and management of the interactive prompt.
 */
final class ConsoleIo {
  /**
   * Stores the command-line arguments into the shared application state.
   *
   * @param args the command-line arguments to store
   */
  static final void setArgs(String args[]) {
    State.args = args;
  }

  /**
   * Prints the given string to standard output followed by a line terminator.
   *
   * @param s the string to print
   */
  static final void outprintln(String s) {
    System.out.println(s);
  }

  /**
   * Prints the given character to standard output without a line terminator.
   *
   * @param c the character to print
   */
  static final void outprint(char c) {
    System.out.print(c);
  }

  /**
   * Prints the given string to standard output without a line terminator.
   *
   * @param s the string to print
   */
  static final void outprint(String s) {
    System.out.print(s);
  }

  /**
   * Prints the given string as a debug line, prefixed with "# " and followed by a line terminator.
   *
   * @param s the debug string to print
   */
  static final void debugln(String s) {
    outprintln("# " + s);
  }

  /**
   * Prints the given character as a debug line, prefixed with "# " and followed by a line
   * terminator.
   *
   * @param c the debug character to print
   */
  static final void debugln(char c) {
    outprintln("# " + c);
  }

  /**
   * Prints the given string to standard output without a prefix or line terminator.
   *
   * @param s the string to print
   */
  static final void debug(String s) {
    outprint(s);
  }

  /**
   * Prints the given character to standard output without a prefix or line terminator.
   *
   * @param c the character to print
   */
  static final void debug(char c) {
    outprint(c);
  }

  /**
   * Prints an exit message with the given detail and terminates the JVM with status code 1. This
   * method never actually returns; it declares a RuntimeException return type so callers can write
   * {@code throw systemexit(...)} to signal a fatal, non-returning error.
   *
   * @param s the detail appended to the exit message
   * @return nominally a RuntimeException, but the JVM exits before any value is returned
   */
  static final RuntimeException systemexit(String s) {
    outprintln(MESSAGE_EXITING + s);
    System.exit(1);
    return new RuntimeException();
  }

  /**
   * Displays the given prompt and reads a single line from the console. A fatal exit is triggered
   * if the prompt is null, the console is unavailable, no input is read, or the input exceeds the
   * maximum allowed length.
   *
   * @param s the prompt to display
   * @param maxLength the maximum permitted length of the read input
   * @return the line read from the console
   */
  static final String readline(String s, int maxLength) {
    String read = "";
    if (s != null) {
      if (CONSOLE != null) {
        read = CONSOLE.readLine(s);
        if (read != null) {
          if (read.length() > maxLength) {
            throw systemexit("Error - Too long input has been read, readline");
          }
        } else {
          throw systemexit("Error - read is null, readline");
        }
      } else {
        throw systemexit("Error - console is null, readline");
      }
    } else {
      throw systemexit("Error - s is null, readline");
    }
    return read;
  }

  /**
   * Displays the given prompt and reads a line of SQL input, using up-arrow history editing when
   * supported and falling back to a plain console read otherwise. The result is trimmed. A fatal
   * exit is triggered if the prompt is null, the console is unavailable, no input is read, or the
   * input exceeds the maximum SQL length.
   *
   * @param s the prompt to display
   * @return the trimmed line read from the console
   */
  static final String readiline(String s) {
    String read = "";
    if (s == null) {
      throw systemexit("Error - s is null, readiline");
    }
    if (CONSOLE == null) {
      throw systemexit("Error - console is null, readiline");
    }
    read = null;
    if (isSupported()) {
      read = readLineWithHistory(s);
    }
    if (read == null) {
      read = CONSOLE.readLine(s);
    }
    if (read == null) {
      throw systemexit("Error - read is null, readiline");
    }
    read = read.trim();
    if (read.length() > APP_MAX_LENGTH_OF_SQL) {
      throw systemexit("Error - Too long input has been read, readiline");
    }
    return read;
  }

  /**
   * Displays the given prompt and reads a password from the console without echoing it. A fatal
   * exit is triggered if the prompt is null, the console is unavailable, no input is read, the user
   * takes longer than the allowed wait time, or the password exceeds the maximum input length. If
   * the password contains any non-ASCII or space character, an empty array is returned.
   *
   * @param s the prompt to display
   * @return the password characters read, or an empty array if the input is not valid non-space
   *     ASCII
   */
  static final char[] readpassword(String s) {
    char[] read = new char[0];
    if (s != null) {
      if (CONSOLE != null) {
        Date wait = new Date();
        read = CONSOLE.readPassword(s);
        if (read != null) {
          if ((int) ((new Date().getTime() - wait.getTime()) / 1000)
              > APP_MAX_NOT_READ_INPUTS_SECONDS) {
            throw systemexit("Error - Waited too long, readpassword");
          }
          if (read.length > APP_MAX_LENGTH_OF_INPUT) {
            throw systemexit("Error - Too long password has been read, readpassword");
          } else {
            if (!isASCIIandNONSPACE(read)) {
              read = new char[0];
            }
          }
          wait = null;
        } else {
          throw systemexit("Error - read is null, readpassword");
        }
      } else {
        throw systemexit("Error - console is null, readpassword");
      }
    } else {
      throw systemexit("Error - s is null, readpassword");
    }
    return read;
  }

  /**
   * Asks the user the given question and reads a single line. If the answer equals the affirmative
   * "yes" value the method returns true; otherwise it prints the supplied message and returns
   * false.
   *
   * @param questionMessage the prompt asking the yes/no question
   * @param notYesMessage the message printed when the answer is not "yes"
   * @return true if the user answered "yes", false otherwise
   */
  static final boolean readYesElseAnything(String questionMessage, String notYesMessage) {
    boolean success = false;
    if (YES.equals(readline(questionMessage, APP_MAX_LENGTH_OF_INPUT))) {
      success = true;
    } else {
      outprintln(notYesMessage);
    }
    return success;
  }

  /**
   * Updates the prompt to match the currently selected database type (Mysql, Oracle, Mssql, Db2, or
   * Postgresql as the default). A fatal exit is triggered if the database type is null.
   */
  static final void changePromptToTheActual() {
    if (dbType != null) {
      if (dbType.equals(DB_TYPE_MYSQL)) {
        changePrompt(PROMPT_MYSQL);
      } else if (dbType.equals(DB_TYPE_ORACLE)) {
        changePrompt(PROMPT_ORACLE);
      } else if (dbType.equals(DB_TYPE_MSSQL)) {
        changePrompt(PROMPT_MSSQL);
      } else if (dbType.equals(DB_TYPE_DB2)) {
        changePrompt(PROMPT_DB2);
      } else {
        changePrompt(PROMPT_POSTGRESQL);
      }
    } else {
      throw systemexit("Error - dbType is null, changePromptToTheActual");
    }
  }

  /**
   * Sets the active prompt from the given template, substituting the connection placeholder with
   * the current connection name (or an empty string when none is set) and updating the stored
   * prompt only when it differs. A fatal exit is triggered if the template or the computed prompt
   * is null.
   *
   * @param s the prompt template containing the connection placeholder
   */
  static final void changePrompt(String s) {
    if (s != null) {
      String tempPrompt =
          s.replace(DB_CONN_TO_CHANGE_IN_PROMPT, "".equals(dbConn) ? "" : "" + SPACE_CHAR + dbConn);
      if (tempPrompt != null) {
        if (!tempPrompt.equals(prompt)) {
          prompt = tempPrompt;
        }
      } else {
        throw systemexit("Error - thePrompt is null, changePrompt");
      }
      tempPrompt = null;
    } else {
      throw systemexit("Error - s is null, changePrompt");
    }
  }

  /**
   * Sets the current connection name and refreshes the prompt when the value changes. A fatal exit
   * is triggered if either the existing connection name or the supplied value is null.
   *
   * @param dbconn the new connection name
   */
  static final void setDbConn(String dbconn) {
    if (dbConn != null && dbconn != null) {
      if (!dbConn.equals(dbconn)) {
        dbConn = dbconn;
        changePromptToTheActual();
      }
    } else {
      throw systemexit("Error - one of these is null: dbConn|dbconn, setDbConn");
    }
  }
}
