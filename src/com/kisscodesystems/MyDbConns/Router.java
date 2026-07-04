package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.AppCommands.*;
import static com.kisscodesystems.MyDbConns.Args.*;
import static com.kisscodesystems.MyDbConns.ConnectionCommands.*;
import static com.kisscodesystems.MyDbConns.Connections.*;
import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.Crypto.*;
import static com.kisscodesystems.MyDbConns.DelimiterCommands.*;
import static com.kisscodesystems.MyDbConns.LineReader.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.Print.*;
import static com.kisscodesystems.MyDbConns.QueryCommands.*;
import static com.kisscodesystems.MyDbConns.QueryFactory.*;
import static com.kisscodesystems.MyDbConns.QueryStore.*;
import static com.kisscodesystems.MyDbConns.ResultCommands.*;
import static com.kisscodesystems.MyDbConns.State.*;
import static com.kisscodesystems.MyDbConns.Utils.*;
import static com.kisscodesystems.MyDbConns.Validate.*;

import java.io.File;
import java.util.Date;

/**
 * Orchestrator of the application. It bootstraps the connections directory and password (creating
 * or opening the connections file on first run), then either dispatches a one-shot command or
 * enters the interactive prompt loop, routing parsed commands to the appropriate command handlers.
 */
final class Router {
  /**
   * Entry point that bootstraps and runs the application. It resets the database type/connection
   * and prompt, verifies the console and args are valid, and depending on whether the command needs
   * the connections store it creates the connections file, opens it, or runs a connectionless
   * command directly. When the store is ready it enters the interactive loop.
   */
  static final void run() {
    dbType = "";
    dbConn = "";
    changePrompt(PROMPT_APP);
    if (CONSOLE == null) {
      outprintln("Error - one of these is null: us|console, main.");
      return;
    }
    if (!isGoodArgsObject(args)) {
      return;
    }
    connectionsDirFolder = new File(APP_CONNECTIONS_DIR);
    boolean connectionsStuffNeeded = !isConnectionlessCommand(args);
    boolean ready;
    if (!connectionsDirFolder.exists() && connectionsStuffNeeded) {
      ready = createConnectionsFile();
    } else if (connectionsStuffNeeded) {
      ready = openConnectionsFile();
    } else {
      letsWork(args);
      ready = false;
    }
    if (ready) {
      interactiveLoop();
    }
  }

  /**
   * Determines whether the given command can run without the connections store. Returns true for
   * the help/question-mark commands, application describe/story, welcome screen, and the
   * connections good-password command.
   *
   * @param args the parsed command tokens
   * @return true if the command does not require the connections file, false otherwise
   */
  private static final boolean isConnectionlessCommand(String[] args) {
    return (args.length == 1 && (cmd(args, ARG_QUESTION_MARK) || cmd(args, ARG_HELP)))
        || (args.length == 2
            && (cmd(args, ARG_APPLICATION, ARG_DESCRIBE)
                || cmd(args, ARG_APPLICATION, ARG_STORY)
                || cmd(args, ARG_WELCOME, ARG_SCREEN)))
        || (args.length == 3 && cmd(args, ARG_CONNECTIONS, ARG_GOOD, ARG_PASSWORD));
  }

  /**
   * Creates the connections file on first run. It shows the welcome screen, confirms the folder is
   * safe (exiting fatally otherwise), creates and verifies the empty connections directory, reads a
   * new password, builds the initial file content with a random header and an initialization log
   * line, and saves it.
   *
   * @return true if the connections file was created and saved successfully, false otherwise
   */
  private static final boolean createConnectionsFile() {
    outprintln(MESSAGE_WELCOME_SCREEN);
    if (!YES.equals(readline(MESSAGE_IS_FOLDER_SAFE, APP_MAX_LENGTH_OF_INPUT))) {
      throw systemexit("Error - Folder is not safe by answer, main");
    }
    connectionsDirFolder.mkdirs();
    File[] connectionsFiles = connectionsDirFolder.listFiles();
    if (connectionsFiles == null) {
      throw systemexit("Error - connectionsDirFolder is null, main");
    }
    if (connectionsFiles.length != 0) {
      throw systemexit("Error - Connections folder is not empty, main");
    }
    outprintln(MESSAGE_DO_NOT_FORGET_YOUR_CONNECTIONS_PASSWORD);
    readPassword(true);
    clearCharArray(fileContentConnectionsOrig, ZERO_CHAR);
    fileContentConnectionsOrig = new char[APP_FILE_CONTENT_MAX_LENGTH];
    clearCharArray(fileContentConnectionsOrig, ZERO_CHAR);
    String connectionsIniContent =
        ""
            + new String(generateRandomHeader())
            + SIMPLE_DATE_FORMAT_FOR_DISPLAYING.format(new Date())
            + SEP9
            + MESSAGE_LOG_APPLICATION_INSTANCE_INITIALIZE
            + NEW_LINE_CHAR;
    for (int i = 0;
        i < Math.min(connectionsIniContent.length(), APP_FILE_CONTENT_MAX_LENGTH);
        i++) {
      fileContentConnectionsOrig[i] = connectionsIniContent.charAt(i);
    }
    if (saveFile()) {
      outprintln(MESSAGE_CONNECTIONS_FILE_HAS_BEEN_CREATED);
      return true;
    }
    return false;
  }

  /**
   * Opens an existing connections file. It reads the password and, if the file content is
   * successfully decrypted, shows the welcome screen and the ini message (adding a blank line in
   * interactive mode).
   *
   * @return true if the file content was read successfully, false otherwise
   */
  private static final boolean openConnectionsFile() {
    readPassword(false);
    if (getFileContent()) {
      outprintln(MESSAGE_WELCOME_SCREEN);
      printIniMessage();
      if (args.length == 0) {
        outprintln("");
      }
      return true;
    }
    return false;
  }

  /**
   * Runs the interactive prompt loop. On each iteration it reads and splits a request line (or uses
   * the pre-supplied args once), then either switches the database type, goes up a level or exits
   * on the up/exit command, or dispatches the parsed command via {@link #letsWork(String[])}. The
   * loop ends on exit, after which it prints the goodbye message and clears the history.
   */
  private static final void interactiveLoop() {
    while (true) {
      String requestString;
      if (args.length == 0) {
        requestString = readiline(prompt).trim();
      } else {
        requestString = "";
      }
      if (requestString == null) {
        usageWrongParameters();
        outprintln("");
        continue;
      }
      String[] requestParams;
      if (args.length == 0) {
        requestParams =
            requestStringSplit(
                requestString, DOUBLE_SPACE, DOUBLE_QUOTE, SINGLE_SPACE, BACKSLA, KEY_BEGIN_END);
      } else {
        requestParams = args;
        args = new String[0];
      }
      if (switchDbType(requestString)) {
        // database type selected; prompt already changed
      } else if (PROMPT_TO_UPPER_OR_EXIT.toLowerCase().equals(requestString.toLowerCase())) {
        if (goUpOrExit()) {
          break;
        }
      } else if (isGoodArgsObject(requestParams)) {
        letsWork(requestParams);
      }
      outprintln("");
    }
    outprintln(MESSAGE_BYE);
    clearHistory();
  }

  /**
   * Switches the active database type when the request matches one of the supported type names
   * (mysql, oracle, mssql, db2, postgresql), resetting the current connection and changing the
   * prompt accordingly.
   *
   * @param requestString the raw request line, matched case-insensitively
   * @return true if a database type was selected, false if the request matched no type
   */
  private static final boolean switchDbType(String requestString) {
    String request = requestString.toLowerCase();
    if (DB_TYPE_MYSQL.toLowerCase().equals(request)) {
      dbType = DB_TYPE_MYSQL;
      dbConn = "";
      changePrompt(PROMPT_MYSQL);
    } else if (DB_TYPE_ORACLE.toLowerCase().equals(request)) {
      dbType = DB_TYPE_ORACLE;
      dbConn = "";
      changePrompt(PROMPT_ORACLE);
    } else if (DB_TYPE_MSSQL.toLowerCase().equals(request)) {
      dbType = DB_TYPE_MSSQL;
      dbConn = "";
      changePrompt(PROMPT_MSSQL);
    } else if (DB_TYPE_DB2.toLowerCase().equals(request)) {
      dbType = DB_TYPE_DB2;
      dbConn = "";
      changePrompt(PROMPT_DB2);
    } else if (DB_TYPE_POSTGRESQL.toLowerCase().equals(request)) {
      dbType = DB_TYPE_POSTGRESQL;
      dbConn = "";
      changePrompt(PROMPT_POSTGRESQL);
    } else {
      return false;
    }
    return true;
  }

  /**
   * Handles the up/exit command depending on the current level. At the top level (no database type)
   * it checks for active queries, optionally confirming and cancelling them, and if it can proceed
   * deletes all queries and signals exit. When a database type is selected it goes up to the app
   * level; when a connection is selected it clears the connection.
   *
   * @return true if the interactive loop should break (exit), false otherwise
   */
  private static final boolean goUpOrExit() {
    if ("".equals(dbType)) {
      boolean canBreak = false;
      String activeQueries = getActiveQueries();
      if ("".equals(activeQueries)) {
        canBreak = true;
      } else {
        outprintln(MESSAGE_RUNNING_QUERIES + activeQueries);
        if (readYesElseAnything(
            MESSAGE_ARE_YOU_SURE_WANT_TO_CANCEL_ALL_ACTIVE_QUERIES_AND_EXIT, "")) {
          if (cancelAllQueries(false)) {
            canBreak = true;
          }
        }
      }
      if (canBreak) {
        deleteAllQueries(false);
        return true;
      }
    } else if ("".equals(dbConn)) {
      dbType = "";
      changePrompt(PROMPT_APP);
    } else {
      setDbConn("");
    }
    return false;
  }

  /**
   * Dispatches a parsed command to the correct per-arity handler. It optionally clears sensitive
   * char/byte buffers before and after (when password caching is off), exits fatally if args is
   * null, and switches on the number of tokens to call {@code dispatch1}..{@code dispatch5},
   * reporting wrong parameters for unsupported arities.
   *
   * @param args the parsed command tokens
   */
  private static final void letsWork(String[] args) {
    if (!APP_CONNECTION_PASSWORD_CACHE) {
      clearCharArrays();
      clearByteArrays();
    }
    if (args == null) {
      throw systemexit("Error - args is null, letsWork");
    }
    switch (args.length) {
      case 0:
        break;
      case 1:
        dispatch1(args);
        break;
      case 2:
        dispatch2(args);
        break;
      case 3:
        dispatch3(args);
        break;
      case 4:
        dispatch4(args);
        break;
      case 5:
        dispatch5(args);
        break;
      default:
        usageWrongParameters();
    }
    if (!APP_CONNECTION_PASSWORD_CACHE) {
      clearCharArrays();
      clearByteArrays();
    }
  }

  /**
   * Prefix-token matcher for commands. Returns true when the leading tokens of {@code args} match,
   * case-insensitively, every token in {@code tokens}; the args may be longer than the tokens (the
   * extra args are treated as parameters), but a shorter args never matches.
   *
   * @param args the parsed command tokens
   * @param tokens the expected leading command tokens to match
   * @return true if args begins with the given tokens, false otherwise
   */
  private static final boolean cmd(String[] args, String... tokens) {
    if (args.length < tokens.length) {
      return false;
    }
    for (int i = 0; i < tokens.length; i++) {
      if (!tokens[i].equals(args[i].toLowerCase())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Dispatches a single-token command: the question-mark shows hints and help shows the help
   * screen. A non-empty unrecognized token reports wrong parameters.
   *
   * @param args the parsed command tokens (length 1)
   */
  private static final void dispatch1(String[] args) {
    if (cmd(args, ARG_QUESTION_MARK)) {
      executeCommandHints();
    } else if (cmd(args, ARG_HELP)) {
      executeCommandHelp();
    } else if (!"".equals(args[0])) {
      usageWrongParameters();
    }
  }

  /**
   * Dispatches a two-token command by prefix matching to the matching handler: application
   * describe/story, welcome screen, connection listall/add/describe/change/delete/deleteall/test,
   * query listall/factory/cancelall/deleteall, and delimiter show/change. Unrecognized commands
   * report wrong parameters.
   *
   * @param args the parsed command tokens (length 2)
   */
  private static final void dispatch2(String[] args) {
    if (cmd(args, ARG_APPLICATION, ARG_DESCRIBE)) {
      executeCommandApplicationDescribe();
    } else if (cmd(args, ARG_APPLICATION, ARG_STORY)) {
      executeCommandApplicationStory();
    } else if (cmd(args, ARG_WELCOME, ARG_SCREEN)) {
      executeCommandWelcomeScreen();
    } else if (cmd(args, ARG_CONNECTION, ARG_LISTALL)) {
      executeCommandConnectionListall();
    } else if (cmd(args, ARG_CONNECTION, ARG_ADD)) {
      executeCommandConnectionAdd();
    } else if (cmd(args, ARG_CONNECTION, ARG_DESCRIBE)) {
      executeCommandConnectionDescribe();
    } else if (cmd(args, ARG_CONNECTION, ARG_CHANGE)) {
      executeCommandConnectionChange();
    } else if (cmd(args, ARG_CONNECTION, ARG_DELETE)) {
      executeCommandConnectionDelete();
    } else if (cmd(args, ARG_CONNECTION, ARG_DELETEALL)) {
      executeCommandConnectionDeleteall();
    } else if (cmd(args, ARG_CONNECTION, ARG_TEST)) {
      executeCommandConnectionTest();
    } else if (cmd(args, ARG_QUERY, ARG_LISTALL)) {
      executeCommandQueryListall();
    } else if (cmd(args, ARG_QUERY, ARG_FACTORY)) {
      executeCommandQueryFactory();
    } else if (cmd(args, ARG_QUERY, ARG_CANCELALL)) {
      executeCommandQueryCancelall();
    } else if (cmd(args, ARG_QUERY, ARG_DELETEALL)) {
      executeCommandQueryDeleteall();
    } else if (cmd(args, ARG_DELIMITER, ARG_SHOW)) {
      executeCommandDelimiterShow();
    } else if (cmd(args, ARG_DELIMITER, ARG_CHANGE)) {
      executeCommandDelimiterChange("");
    } else {
      usageWrongParameters();
    }
  }

  /**
   * Dispatches a three-token command by prefix matching, passing the third token as a parameter
   * where applicable: connection list active/inactive and load/describe/change/delete/use/test,
   * query list active/inactive, query add single/multiple/batch, query
   * factory/describe/run/cancel/delete, result echo, delimiter change, and connections
   * good-password/password-change. Unrecognized commands report wrong parameters.
   *
   * @param args the parsed command tokens (length 3)
   */
  private static final void dispatch3(String[] args) {
    if (cmd(args, ARG_CONNECTION, ARG_LIST, ARG_ACTIVE)) {
      executeCommandConnectionListActive();
    } else if (cmd(args, ARG_CONNECTION, ARG_LIST, ARG_INACTIVE)) {
      executeCommandConnectionListInactive();
    } else if (cmd(args, ARG_CONNECTION, ARG_LOAD)) {
      executeCommandConnectionLoad(args[2]);
    } else if (cmd(args, ARG_CONNECTION, ARG_DESCRIBE)) {
      executeCommandConnectionDescribe(args[2]);
    } else if (cmd(args, ARG_CONNECTION, ARG_CHANGE)) {
      executeCommandConnectionChange(args[2]);
    } else if (cmd(args, ARG_CONNECTION, ARG_DELETE)) {
      executeCommandConnectionDelete(args[2]);
    } else if (cmd(args, ARG_CONNECTION, ARG_USE)) {
      executeCommandConnectionUse(args[2]);
    } else if (cmd(args, ARG_CONNECTION, ARG_TEST)) {
      executeCommandConnectionTest(args[2]);
    } else if (cmd(args, ARG_QUERY, ARG_LIST, ARG_ACTIVE)) {
      executeCommandQueryListActive();
    } else if (cmd(args, ARG_QUERY, ARG_LIST, ARG_INACTIVE)) {
      executeCommandQueryListInactive();
    } else if (cmd(args, ARG_QUERY, ARG_ADD, ARG_SINGLE)) {
      executeCommandQueryAddSingle();
    } else if (cmd(args, ARG_QUERY, ARG_ADD, ARG_MULTIPLE)) {
      executeCommandQueryAddMultiple();
    } else if (cmd(args, ARG_QUERY, ARG_ADD, ARG_BATCH)) {
      executeCommandQueryAddBatch();
    } else if (cmd(args, ARG_QUERY, ARG_FACTORY)) {
      executeCommandQueryFactory(args[2]);
    } else if (cmd(args, ARG_QUERY, ARG_DESCRIBE)) {
      executeCommandQueryDescribe(args[2]);
    } else if (cmd(args, ARG_QUERY, ARG_RUN)) {
      executeCommandQueryRun(args[2]);
    } else if (cmd(args, ARG_QUERY, ARG_CANCEL)) {
      executeCommandQueryCancel(args[2]);
    } else if (cmd(args, ARG_QUERY, ARG_DELETE)) {
      executeCommandQueryDelete(args[2]);
    } else if (cmd(args, ARG_RESULT, ARG_ECHO)) {
      executeCommandResultEcho(args[2]);
    } else if (cmd(args, ARG_DELIMITER, ARG_CHANGE)) {
      executeCommandDelimiterChange(args[2]);
    } else if (cmd(args, ARG_CONNECTIONS, ARG_GOOD, ARG_PASSWORD)) {
      executeCommandConnectionsGoodPassword();
    } else if (cmd(args, ARG_CONNECTIONS, ARG_PASSWORD, ARG_CHANGE)) {
      executeCommandConnectionsPasswordChange();
    } else {
      usageWrongParameters();
    }
  }

  /**
   * Dispatches a four-token command by prefix matching, passing the third and fourth tokens as
   * parameters: connection use and query factory. Unrecognized commands report wrong parameters.
   *
   * @param args the parsed command tokens (length 4)
   */
  private static final void dispatch4(String[] args) {
    if (cmd(args, ARG_CONNECTION, ARG_USE)) {
      executeCommandConnectionUse(args[2], args[3]);
    } else if (cmd(args, ARG_QUERY, ARG_FACTORY)) {
      executeCommandQueryFactory(args[2], args[3]);
    } else {
      usageWrongParameters();
    }
  }

  /**
   * Dispatches a five-token command by prefix matching, passing the third, fourth, and fifth tokens
   * as parameters: query factory. Unrecognized commands report wrong parameters.
   *
   * @param args the parsed command tokens (length 5)
   */
  private static final void dispatch5(String[] args) {
    if (cmd(args, ARG_QUERY, ARG_FACTORY)) {
      executeCommandQueryFactory(args[2], args[3], args[4]);
    } else {
      usageWrongParameters();
    }
  }
}
