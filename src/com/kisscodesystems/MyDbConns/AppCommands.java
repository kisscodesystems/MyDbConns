package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Connections.*;
import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Messages.*;

/**
 * Command handlers for application-level actions such as describing the application, showing its
 * story, the welcome screen, the good-password confirmation, the connections password change, and
 * the hints/help and wrong-parameters usage messages.
 */
final class AppCommands {
  /** Prints the application description message to the console. */
  static final void executeCommandApplicationDescribe() {
    outprintln(MESSAGE_APPLICATION_DESCRIBE);
  }

  /** Prints the application story message to the console. */
  static final void executeCommandApplicationStory() {
    outprintln(MESSAGE_APPLICATION_STORY);
  }

  /** Prints the welcome screen message to the console. */
  static final void executeCommandWelcomeScreen() {
    outprintln(MESSAGE_WELCOME_SCREEN);
  }

  /** Prints the message shown when the connections password was entered correctly. */
  static final void executeCommandConnectionsGoodPassword() {
    outprintln(MESSAGE_CONNECTIONS_GOOD_PASSWORD);
  }

  /**
   * Changes the connections file password. After confirming with the user and verifying the
   * connections file is ready, it reads a new password and saves the file, then reports success.
   */
  static final void executeCommandConnectionsPasswordChange() {
    if (isExistingConnectionsFiles(true)) {
      if (isFileContentConnectionsOrigReady()) {
        if (readYesElseAnything(
            MESSAGE_SURE_CHANGE_CONNECTIONS_PASSWORD,
            MESSAGE_CONNECTIONS_PASSWORD_WONT_BE_CHANGED)) {
          outprintln(MESSAGE_DO_NOT_FORGET_YOUR_CONNECTIONS_PASSWORD);
          readPassword(true);
          if (saveFile()) {
            outprintln(MESSAGE_CONNECTIONS_PASSWORD_HAS_BEEN_CHANGED);
          }
        }
      }
    }
  }

  /** Prints the hints message to the console. */
  static final void executeCommandHints() {
    outprintln(MESSAGE_HINTS);
  }

  /** Prints the help message to the console. */
  static final void executeCommandHelp() {
    outprintln(MESSAGE_HELP);
  }

  /** Prints the wrong-parameters usage message to the console. */
  static final void usageWrongParameters() {
    outprintln(MESSAGE_WRONG_PARAMETERS);
  }
}
