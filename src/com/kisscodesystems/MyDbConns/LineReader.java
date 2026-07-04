package com.kisscodesystems.MyDbConns;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Provides an interactive single-line reader with up/down-arrow command history and in-place line
 * editing. It uses raw terminal mode via {@code stty}, ANSI escape sequences, backspace handling
 * and Up/Down history navigation, and is only functional on non-Windows systems.
 */
final class LineReader {
  private static final ArrayList<String> HISTORY = new ArrayList<String>();
  private static String savedSttyState = null;
  private static boolean shutdownHookRegistered = false;

  /**
   * Determines whether this line reader is supported on the current operating system.
   *
   * @return {@code true} on non-Windows systems where the raw-mode reader can be used, {@code
   *     false} otherwise
   */
  static final boolean isSupported() {
    String os = System.getProperty("os.name");
    return os != null && !os.toLowerCase().contains("win");
  }

  /** Clears all previously stored command-line history entries. */
  static final void clearHistory() {
    HISTORY.clear();
  }

  /**
   * Reads a single line of input while displaying the given prompt, supporting in-place editing,
   * backspace, and Up/Down arrow navigation through the command history. Non-empty lines that
   * differ from the most recent history entry are appended to the history. The terminal is put into
   * raw mode for the duration of the read and restored afterwards.
   *
   * @param prompt the prompt text to display before reading input
   * @return the line entered by the user, or {@code null} if raw mode could not be entered
   */
  static final String readLineWithHistory(String prompt) {
    if (!enterRawMode()) {
      return null;
    }
    StringBuilder buf = new StringBuilder();
    int histIndex = HISTORY.size();
    String stash = "";
    try {
      System.out.print(prompt);
      System.out.flush();
      int c;
      while ((c = System.in.read()) != -1) {
        if (c == '\r' || c == '\n') {
          System.out.print('\n');
          System.out.flush();
          break;
        } else if (c == 127 || c == 8) {
          if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
            System.out.print("\b \b");
            System.out.flush();
          }
        } else if (c == 27) {
          int c1 = System.in.read();
          if (c1 == '[' || c1 == 'O') {
            int c2 = System.in.read();
            if (c2 == 'A') {
              if (histIndex > 0) {
                if (histIndex == HISTORY.size()) {
                  stash = buf.toString();
                }
                histIndex--;
                setLine(prompt, buf, HISTORY.get(histIndex));
              }
            } else if (c2 == 'B') {
              if (histIndex < HISTORY.size()) {
                histIndex++;
                setLine(prompt, buf, histIndex == HISTORY.size() ? stash : HISTORY.get(histIndex));
              }
            } else if (c2 >= '0' && c2 <= '9') {
              int x = c2;
              while (x != '~' && x != -1) {
                x = System.in.read();
              }
            }
          }
        } else if (c >= 32 && c <= 126) {
          buf.append((char) c);
          System.out.print((char) c);
          System.out.flush();
        }
      }
    } catch (Exception e) {
      // fall through, restore the terminal and return what was read so far
    } finally {
      restoreMode();
    }
    String line = buf.toString();
    if (line.length() > 0 && (HISTORY.isEmpty() || !HISTORY.get(HISTORY.size() - 1).equals(line))) {
      HISTORY.add(line);
    }
    return line;
  }

  /**
   * Replaces the current input buffer and redraws the line, showing the prompt followed by the new
   * text and clearing the remainder of the line. Used when navigating the history.
   *
   * @param prompt the prompt text to redraw
   * @param buf the editing buffer to replace with the new text
   * @param text the new line content to display
   */
  private static final void setLine(String prompt, StringBuilder buf, String text) {
    buf.setLength(0);
    buf.append(text);
    System.out.print('\r');
    System.out.print(prompt);
    System.out.print(text);
    System.out.print("[K");
    System.out.flush();
  }

  /**
   * Switches the terminal into raw mode by saving the current {@code stty} settings and disabling
   * canonical input and echo, and registers a shutdown hook to restore the terminal.
   *
   * @return {@code true} if raw mode was successfully entered, {@code false} otherwise
   */
  private static final boolean enterRawMode() {
    try {
      String saved = stty("-g");
      if (saved == null || saved.trim().length() == 0) {
        return false;
      }
      savedSttyState = saved.trim();
      stty("-icanon -echo min 1 time 0");
      registerRestoreHook();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Restores the terminal to its previously saved {@code stty} state, or falls back to {@code sane}
   * if no saved state is available. Failures are ignored on a best-effort basis.
   */
  private static final void restoreMode() {
    try {
      if (savedSttyState != null) {
        stty(savedSttyState);
        savedSttyState = null;
      } else {
        stty("sane");
      }
    } catch (Exception e) {
      // best effort
    }
  }

  /**
   * Registers, at most once, a JVM shutdown hook that resets the terminal to {@code sane} mode so
   * the terminal is not left in raw mode if the application exits unexpectedly.
   */
  private static final void registerRestoreHook() {
    if (!shutdownHookRegistered) {
      Runtime.getRuntime()
          .addShutdownHook(
              new Thread() {
                public void run() {
                  try {
                    stty("sane");
                  } catch (Exception e) {
                    // best effort
                  }
                }
              });
      shutdownHookRegistered = true;
    }
  }

  /**
   * Runs the {@code stty} command with the given arguments against {@code /dev/tty} and returns its
   * output. Used both to query and to change the terminal settings.
   *
   * @param args the arguments to pass to {@code stty}
   * @return the standard output produced by the {@code stty} command
   * @throws Exception if the command cannot be executed or is interrupted
   */
  private static final String stty(String args) throws Exception {
    String[] cmd = {"sh", "-c", "stty " + args + " < /dev/tty"};
    Process p = Runtime.getRuntime().exec(cmd);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    InputStream is = p.getInputStream();
    byte[] b = new byte[256];
    int N;
    while ((N = is.read(b)) != -1) {
      out.write(b, 0, N);
    }
    p.waitFor();
    return out.toString();
  }
}
