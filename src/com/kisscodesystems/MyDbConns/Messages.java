package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Args.*;
import static com.kisscodesystems.MyDbConns.Const.*;

/**
 * Holds the user-facing message strings displayed by the application, including prompts,
 * confirmations, informational notices, error messages, help and describe screens, and the
 * placeholder tokens used within them.
 */
final class Messages {
  static final String MESSAGE_YOUR_CONNECTION_NAME = "<your_connection_name>";
  static final String MESSAGE_YOUR_FILE_NAME = "<your_file_name>";
  static final String MESSAGE_YOUR_QUERY_ID = "<your_query_id>";
  static final String MESSAGE_YOUR_DATABASE_TYPE = "<your_database_type>";
  static final String MESSAGE_YOUR_NEW_DELIMITER = "<your_new_delimiter>";
  static final String MESSAGE_YOUR_QUERY = "\"<your_query>\"";
  static final String MESSAGE_EMPTY = "<empty>";
  static final String MESSAGE_HIDDEN = "<hidden>";
  static final String MESSAGE_ENTER = "<enter>";
  static final String MESSAGE_ACTIVE_CONNECTIONS = " active connections: ";
  static final String MESSAGE_INACTIVE_CONNECTIONS = " inactive connections: ";
  static final String MESSAGE_ALL_CONNECTIONS = " all connections: ";
  static final String MESSAGE_ACTIVE_QUERIES = " active queries: ";
  static final String MESSAGE_INACTIVE_QUERIES = " inactive queries: ";
  static final String MESSAGE_ALL_QUERIES = " all queries: ";
  static final String MESSAGE_QUERY_ELAPSEDS = "elapsed times: ";
  static final String MESSAGE_NO_ROWS_SELECTED = "No rows selected.";
  static final String MESSAGE1_ROW_SELECTED = "1 row selected.";
  static final String MESSAGE_ROWS_SELECTED = " rows selected.";
  static final String MESSAGE_RUNNING_ELAPSED = "running, elapsed: ";
  static final String MESSAGE_QUERIES = SEP9 + "queries: ";
  static final String MESSAGE_HITS = " hit(s).";
  static final String MESSAGE_DELIMITER_IS_EMPTY = "delimiter is empty!";
  static final String MESSAGE_CONNECTION_HAS_BEEN_DELETED = " connection has been deleted.";
  static final String MESSAGE_YOU_ARE_NOW_CONNECTED =
      NEW_LINE_STRING + FOLD + "You are now connected: ";
  static final String MESSAGE_WELCOME_TO_QUERY_FACTORY =
      NEW_LINE_STRING
          + FOLD
          + "Welcome to query factory."
          + NEW_LINE_CHAR
          + FOLD
          + "You can now type your sql queries one-by-one."
          + NEW_LINE_CHAR
          + FOLD
          + "Type "
          + PROMPT_TO_UPPER_OR_EXIT
          + " to have the application prompt!"
          + NEW_LINE_CHAR
          + FOLD
          + "Other commands work in query factory:"
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + ARG_DELIMITER
          + SPACE_CHAR
          + ARG_SHOW
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + ARG_DELIMITER
          + SPACE_CHAR
          + ARG_CHANGE
          + SPACE_CHAR
          + MESSAGE_YOUR_NEW_DELIMITER
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_DESCRIBE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + APP_QUERY_FILE_PREFIX
          + MESSAGE_YOUR_FILE_NAME
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + ARG_QUESTION_MARK
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + ARG_HELP;
  static final String MESSAGE_UNABLE_TO_FREE_QUERY_FACTORY =
      NEW_LINE_STRING + FOLD + "Unable to free query factory: ";
  static final String MESSAGE_SUCCESSFULLY_EXITED_FROM_QUERY_FACTORY =
      NEW_LINE_STRING + FOLD + "Successfully exited from query factory.";
  static final String MESSAGE_THE_PROPERTIES_OF_YOUR_CONNECTION =
      NEW_LINE_STRING + FOLD + "The properties of your connection.";
  static final String MESSAGE_THIS_CONNECTION_IS_NOT_IN_USE_BY_ADDED_QUERY =
      "This connection is not in use by an added query.";
  static final String MESSAGE_THIS_CONNECTION_IS_IN_USE = "This connection is in use: ";
  static final String MESSAGE_DELIMITER_IS_EMPTY_WHILE_ADDING_BATCH_SQL =
      NEW_LINE_STRING + FOLD + "Delimiter is empty while adding a batch sql!";
  static final String MESSAGE_YOUR_CONNECTION_DOES_NOT_EXIST =
      NEW_LINE_STRING + FOLD + "Your connection does not exist!";
  static final String MESSAGE_ENTER_THE_PROPERTIES_OF_THE_CONNECTION =
      NEW_LINE_STRING + FOLD + "Please enter the properties of the connection!" + NEW_LINE_CHAR;
  static final String MESSAGE_THE_DRIVER_CANNOT_BE_EMPTY =
      NEW_LINE_STRING
          + FOLD
          + "The driver cannot be empty!"
          + NEW_LINE_CHAR
          + FOLD
          + "Please type a non-empty driver (for example: 'oracle.jdbc.driver.OracleDriver')";
  static final String MESSAGE_THE_CONNECTION_STRING_CANNOT_BE_EMPTY =
      NEW_LINE_STRING
          + FOLD
          + "The connection string cannot be empty!"
          + NEW_LINE_CHAR
          + FOLD
          + "Please type a non-empty connection string (for example:"
          + " 'jdbc:oracle:thin:@localhost:1521:orcl')";
  static final String MESSAGE_SORRY_BUT_THIS_RESULT_SET_CANNOT_BE_DISPAYED =
      NEW_LINE_STRING
          + FOLD
          + "Sorry, but this resultSet cannot be displayed."
          + NEW_LINE_CHAR
          + FOLD
          + "Re-run this query by typing: query run ";
  static final String MESSAGE_DATABASE_TYPE = FOLD + FOLD2 + "Database type     : ";
  static final String MESSAGE_CONNECTION_NAME = FOLD + FOLD2 + "Connection name   : ";
  static final String MESSAGE_DATABASE_USER = FOLD + FOLD2 + "Database user     : ";
  static final String MESSAGE_DATABASE_PASSWORD = FOLD + FOLD2 + "Database password : ";
  static final String MESSAGE_DATABASE_DRIVER = FOLD + FOLD2 + "Connection driver : ";
  static final String MESSAGE_CONNECTION_STRING = FOLD + FOLD2 + "Connection string : ";
  static final String MESSAGE_QUERY_TITLE = FOLD + FOLD2 + "Query title       : ";
  static final String MESSAGE_ENTER_QUERY_STRING =
      NEW_LINE_STRING + "Enter your query:" + NEW_LINE_CHAR + "(type \"";
  static final String MESSAGE_ENTER_RESULT_TARGETS =
      NEW_LINE_STRING
          + FOLD
          + "Enter the result targets (can be multiple)."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + RESULT_TARGET_CONS_VALUE
          + " ("
          + RESULT_TARGET_CONS_STRING
          + ")"
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + RESULT_TARGET_FILE_VALUE
          + " ("
          + RESULT_TARGET_FILE_STRING
          + ")"
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + MESSAGE_EMPTY
          + " ("
          + RESULT_TARGET_CONS_STRING
          + ")"
          + NEW_LINE_CHAR
          + FOLD
          + ": ";
  static final String MESSAGE_ENTER_RESULT_FORMATS =
      FOLD
          + "Enter the result formats (can be multiple)."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + RESULT_FORMAT_TXT_VALUE
          + " ("
          + RESULT_FORMAT_TXT_STRING
          + ")"
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + RESULT_FORMAT_CSV_VALUE
          + " ("
          + RESULT_FORMAT_CSV_STRING
          + ")"
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + RESULT_FORMAT_HTM_VALUE
          + " ("
          + RESULT_FORMAT_HTM_STRING
          + ")"
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + MESSAGE_EMPTY
          + " ("
          + RESULT_FORMAT_TXT_STRING
          + ")"
          + NEW_LINE_CHAR
          + FOLD
          + ": ";
  static final String MESSAGE_CORRECT_RESULT_TARGETS_ARE =
      NEW_LINE_STRING
          + FOLD
          + "Correct result targets are "
          + RESULT_TARGET_CONS_VALUE
          + " and/or "
          + RESULT_TARGET_FILE_VALUE
          + "!";
  static final String MESSAGE_CORRECT_RESULT_FORMATS_ARE =
      NEW_LINE_STRING
          + FOLD
          + "Correct result formats are "
          + RESULT_FORMAT_TXT_VALUE
          + " and/or "
          + RESULT_FORMAT_CSV_VALUE
          + " and/or "
          + RESULT_FORMAT_HTM_VALUE
          + "!";
  static final String MESSAGE_ENTER_HEADER_TO_INCLUDE = FOLD + "Header included? [yes]: ";
  static final String MESSAGE_ENTER_QUERY_TO_INCLUDE = FOLD + "Query included? [no]: ";
  static final String MESSAGE_PRODUCING_TXT_RESULT =
      FOLD + "Producing txt result" + DOUBLE_DOT + SPACE_CHAR;
  static final String MESSAGE_PRODUCING_CSV_RESULT =
      FOLD + "Producing csv result" + DOUBLE_DOT + SPACE_CHAR;
  static final String MESSAGE_PRODUCING_HTM_RESULT =
      FOLD + "Producing htm result" + DOUBLE_DOT + SPACE_CHAR;
  static final String MESSAGE_QUERY_STRING_END_NOT_EMPTY =
      "\" and press " + MESSAGE_ENTER + " to save)" + NEW_LINE_CHAR + PROMPT_ENDING;
  static final String MESSAGE_QUERY_STRING_END_EMPTY =
      "\" to save)" + NEW_LINE_CHAR + PROMPT_ENDING;
  static final String MESSAGE_QUERY_IS_SCROLLABLE =
      NEW_LINE_STRING + FOLD + FOLD2 + "Scrollable resultSet? [yes]: ";
  static final String MESSAGE_QUERY_RUN_NOW =
      NEW_LINE_STRING + FOLD + FOLD2 + "Run it now? [" + YES + "]: ";
  static final String MESSAGE_CONNECTIONS_GOOD_PASSWORD =
      NEW_LINE_STRING
          + FOLD
          + "THE GOOD CONNECTIONS PASSWORD IS:"
          + NEW_LINE_CHAR
          + FOLD
          + "+------------------------------------------------------+"
          + NEW_LINE_CHAR
          + FOLD
          + "| - ASCII 33-126 characters are acceptable, no spaces! |"
          + NEW_LINE_CHAR
          + FOLD
          + "| - min. "
          + APP_GOOD_PASSWORD_MIN_COUNT_OF_UC_LETTERS
          + " uppercase letters                     "
          + LETTERS_UCAZ
          + " |"
          + NEW_LINE_CHAR
          + FOLD
          + "| - min. "
          + APP_GOOD_PASSWORD_MIN_COUNT_OF_LC_LETTERS
          + " lowercase letters                     "
          + LETTERS_LCAZ
          + " |"
          + NEW_LINE_CHAR
          + FOLD
          + "| - min. "
          + APP_GOOD_PASSWORD_MIN_COUNT_OF_DIGITS
          + " digits                                "
          + LETTERS09
          + " |"
          + NEW_LINE_CHAR
          + FOLD
          + "| - min. "
          + APP_GOOD_PASSWORD_MIN_COUNT_OF_SPEC_CHARS
          + " special chars, for example "
          + LETTERS_SPEC_CHARS
          + " |"
          + NEW_LINE_CHAR
          + FOLD
          + "| - min. _"
          + APP_GOOD_PASSWORD_MIN_LENGTH_OF_GOOD_PASSWORDS
          + "_ and max. _"
          + APP_MAX_LENGTH_OF_INPUT
          + "_ characters length password |"
          + NEW_LINE_CHAR
          + FOLD
          + "+------------------------------------------------------+";
  static final String MESSAGE_LOG_APPLICATION_INSTANCE_INITIALIZE =
      "Application instance was initialized.";
  static final String MESSAGE_YOU_HAVE_REACHED_THE_TOP_OF_THE_COUNT_OF_STORABLE_CONNECTIONS =
      NEW_LINE_STRING + FOLD + "You have reached the top of the count of storable connections.";
  static final String MESSAGE_CONNECTIONS_FILE_HAS_BEEN_CREATED =
      NEW_LINE_STRING
          + FOLD
          + "Your connections file has been created successfully."
          + NEW_LINE_CHAR
          + FOLD;
  static final String MESSAGE_SAVED_QUERY_ID =
      NEW_LINE_STRING + FOLD + "Your query has been added successfully with ID: ";
  static final String MESSAGE_THIS_CONNECTION_NAME_IN_DATABASE_TYPE_ALREADY_EXISTS =
      NEW_LINE_STRING + FOLD + "This connection name already exists in this database type!";
  static final String MESSAGE_CONNECTION_HAS_BEEN_TESTED_SUCCESSFULLY =
      NEW_LINE_STRING + FOLD + "Connection has been tested successfully.";
  static final String MESSAGE_UNABLE_TO_CONNECT = NEW_LINE_STRING + FOLD + "Unable to connect:";
  static final String MESSAGE_FAILED_TO_CLOSE_RESULT_SET = FOLD + "Unable to close result set ";
  static final String MESSAGE_FAILED_TO_CLOSE_CONNECTION = FOLD + "Unable to close connection ";
  static final String MESSAGE_FAILED_TO_CLOSE_STATEMENT = FOLD + "Unable to close statement ";
  static final String MESSAGE_UNABLE_TO_CLOSE_TESTER_CONNECTION =
      FOLD + "Unable to close tester connection!";
  static final String MESSAGE_DATABASE_TYPE_HAS_NOT_BEEN_CORRECT =
      NEW_LINE_STRING + FOLD + "The database type is not correct!";
  static final String MESSAGE_FILES_ARE_ALLOWED_FROM_NEXT_TO_THE_APPLICATION =
      NEW_LINE_STRING + FOLD + "Only files next to the application are allowed.";
  static final String MESSAGE_FILE_DOES_NOT_EXIST =
      NEW_LINE_STRING + FOLD + "The file does not exist: ";
  static final String MESSAGE_FILE_IS_NOT_FILE =
      NEW_LINE_STRING + FOLD + "The file is not a file: ";
  static final String MESSAGE_DO_NOT_FORGET_YOUR_CONNECTIONS_PASSWORD =
      NEW_LINE_STRING
          + FOLD
          + "The password of the application instance will be requested."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "Please do not forget your connections password"
          + NEW_LINE_CHAR
          + FOLD
          + "otherwise you won't be able to use your "
          + APP_NAME
          + " instance! ";
  static final String MESSAGE_QUERY_ID_DOES_NOT_EXIST =
      NEW_LINE_STRING + FOLD + "Query ID does not exist: ";
  static final String MESSAGE_TYPE_YES_ELSE_ANYTHING = "[type " + YES + " else anything]: ";
  static final String MESSAGE_EXITING =
      NEW_LINE_STRING + FOLD + "The " + APP_NAME + " is exiting with error:" + NEW_LINE_CHAR + FOLD;
  static final String MESSAGE_ERROR_DELETING_OLD_FILES_OR_RENAME_NEW_FILES =
      NEW_LINE_STRING
          + FOLD
          + "An error has occurred while deleting old files or renaming the new files back! "
          + NEW_LINE_CHAR
          + FOLD
          + "Please fix it manually:"
          + NEW_LINE_CHAR
          + FOLD
          + "The files have to be deleted ("
          + APP_CS_POSTFIX
          + SEP1
          + APP_SL_POSTFIX
          + " and "
          + APP_IV_POSTFIX
          + ")"
          + NEW_LINE_CHAR
          + FOLD
          + "and the "
          + APP_NW_POSTFIX
          + " files have to be renamed back without "
          + APP_NW_POSTFIX
          + " extension! "
          + NEW_LINE_CHAR
          + FOLD
          + "The filename is: ";
  static final String MESSAGE_PASSWORD_VERIFICATION_ERROR =
      NEW_LINE_STRING + FOLD + "Sorry, but the password and its verification are not the same.";
  static final String MESSAGE_WELCOME_SCREEN =
      NEW_LINE_STRING
          + FOLD
          + "Welcome to "
          + APP_NAME
          + "!"
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "Type "
          + DB_TYPE_MYSQL
          + " or "
          + DB_TYPE_ORACLE
          + " or "
          + DB_TYPE_MSSQL
          + " or "
          + DB_TYPE_DB2
          + " or "
          + DB_TYPE_POSTGRESQL
          + " to use the specific database!"
          + NEW_LINE_CHAR
          + FOLD
          + "Type "
          + PROMPT_TO_UPPER_OR_EXIT
          + " to return to upper levels or exit!"
          + NEW_LINE_CHAR
          + FOLD
          + "Type "
          + ARG_QUESTION_MARK
          + " or "
          + ARG_HELP
          + " for more information about using this application!"
          + NEW_LINE_CHAR
          + FOLD
          + "If you want to use this app for example with postgresql, then you can start the app with:"
          + NEW_LINE_CHAR
          + FOLD
          + "java -cp lib/PostgresqlJdbc.jar:./MyDbConns.jar com.kisscodesystems.MyDbConns.MyDbConnsMain";
  static final String MESSAGE_ERROR_DELETING_NEW_AN_FILE =
      FOLD + "Error while deleting newly created " + APP_CS_POSTFIX + " file! ";
  static final String MESSAGE_SURE_CHANGE_CONNECTIONS_PASSWORD =
      NEW_LINE_STRING
          + FOLD
          + "Are you sure you want to change the connections password?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_DRIVER_FOR_MYSQL_HAS_TO_CONTAIN =
      NEW_LINE_STRING + FOLD + "The driver for Mysql has to contain: ";
  static final String MESSAGE_DRIVER_FOR_ORACLE_HAS_TO_CONTAIN =
      NEW_LINE_STRING + FOLD + "The driver for Oracle has to contain: ";
  static final String MESSAGE_DRIVER_FOR_MSSQL_HAS_TO_CONTAIN =
      NEW_LINE_STRING + FOLD + "The driver for Mssql has to contain: ";
  static final String MESSAGE_DRIVER_FOR_DB2_HAS_TO_CONTAIN =
      NEW_LINE_STRING + FOLD + "The driver for Db2 has to contain: ";
  static final String MESSAGE_DRIVER_FOR_POSTGRESQL_HAS_TO_CONTAIN =
      NEW_LINE_STRING + FOLD + "The driver for Postgresql has to contain: ";
  static final String MESSAGE_SAVE_CONNECTION =
      NEW_LINE_STRING
          + FOLD
          + "Save connection?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_CONNECTIONS_PASSWORD_HAS_BEEN_CHANGED =
      NEW_LINE_STRING + FOLD + "The connections password has been changed successfully.";
  static final String MESSAGE_CONNECTIONS_PASSWORD_WONT_BE_CHANGED =
      NEW_LINE_STRING + FOLD + "The connections password is the same.";
  static final String MESSAGE_RUNNING_QUERIES =
      NEW_LINE_STRING + FOLD + "The ID-s of your currently running queries are: ";
  static final String MESSAGE_FILES_ALLOWED_BEING_NEXT_TO_THE_APPLICATION =
      NEW_LINE_STRING + FOLD + "Only files next to the application are allowed.";
  static final String MESSAGE_NO_RUNNING_QUERIES_HAVE_BEEN_FOUND_TO_CANCEL =
      NEW_LINE_STRING + FOLD + "No running queries have been found to cancel.";
  static final String MESSAGE_ONE_RUNNING_QUERY_HAS_BEEN_CANCELLED =
      NEW_LINE_STRING + FOLD + "1 running query has been cancelled.";
  static final String MESSAGE_RUNNING_QUERIES_HAVE_BEEN_CANCELLED =
      NEW_LINE_STRING + FOLD + "Running queries have been cancelled: ";
  static final String MESSAGE_NO_NOT_RUNNING_QUERIES_TO_DELETE =
      NEW_LINE_STRING + FOLD + "No non-running queries to delete.";
  static final String MESSAGE_ONE_NOT_RUNNING_QUERY_HAS_BEEN_DELETED =
      NEW_LINE_STRING + FOLD + "1 non-running query has been deleted.";
  static final String MESSAGE_NOT_RUNNING_QUERIES_HAVE_BEEN_DELETED =
      NEW_LINE_STRING + FOLD + "Non-running queries have been deleted: ";
  static final String MESSAGE_ARE_YOU_SURE_WANT_TO_CANCEL_ALL_ACTIVE_QUERIES_AND_EXIT =
      NEW_LINE_STRING
          + FOLD
          + "Are you sure want to cancel all active queries and exit?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_CONNECTION_HAS_NOT_BEEN_SAVED =
      NEW_LINE_STRING + FOLD + "Connection has not been saved.";
  static final String MESSAGE_QUERY_IS_CANCELLED = FOLD + "Query is cancelled.";
  static final String MESSAGE_QUERY_IS_DELETED = FOLD + "Query is deleted.";
  static final String MESSAGE_QUERY_HAS_NOT_BEEN_FOUND =
      NEW_LINE_STRING + FOLD + "Query has not been found.";
  static final String MESSAGE_ALL_CONNECTIONS_HAVE_BEEN_HANDELED =
      NEW_LINE_STRING + FOLD + "All connections have been handled.";
  static final String MESSAGE_NO_CONNECTIONS_HAVE_BEEN_HANDELED =
      NEW_LINE_STRING + FOLD + "No connections have been handled.";
  static final String MESSAGE_FILE_CONTENT_HAS_NOT_BEEN_FOUND =
      NEW_LINE_STRING + FOLD + "The content of the file has not been found: ";
  static final String MESSAGE_ERROR_DELETING_NEW_SL_FILE =
      FOLD + "Error while deleting newly created " + APP_SL_POSTFIX + " file! ";
  static final String MESSAGE_CONTENT_IS_NOT_DECRYPTED =
      NEW_LINE_STRING + FOLD + "The content is not decrypted.";
  static final String MESSAGE_YOUR_QUERY_IS_RUNNING =
      NEW_LINE_STRING + FOLD + "Your query is running.";
  static final String MESSAGE_YOUR_QUERY_RESULT_SET_IS_NOT_SCROLLABLE =
      NEW_LINE_STRING
          + FOLD
          + "Your query resultSet is not scrollable."
          + NEW_LINE_CHAR
          + FOLD
          + "Type this to echo the result: "
          + ARG_RESULT
          + SPACE_CHAR
          + ARG_ECHO
          + SPACE_CHAR;
  static final String MESSAGE_YOUR_QUERY_HAS_TO_BE_RUN =
      NEW_LINE_STRING + FOLD + "Your query has to be run.";
  static final String MESSAGE_QUERY_HAS_NOT_BEEN_FINISHED =
      NEW_LINE_STRING + FOLD + "Query has not been finished!";
  static final String MESSAGE_QUERY_WONT_BE_CANCELLED =
      NEW_LINE_STRING + FOLD + "Query won't be cancelled.";
  static final String MESSAGE_QUERIES_WONT_BE_CANCELLED =
      NEW_LINE_STRING + FOLD + "Queries won't be cancelled.";
  static final String MESSAGE_QUERY_WONT_BE_DELETED =
      NEW_LINE_STRING + FOLD + "Query won't be deleted.";
  static final String MESSAGE_QUERIES_WONT_BE_DELETED =
      NEW_LINE_STRING + FOLD + "Queries won't be deleted.";
  static final String MESSAGE_RESULT_FILE_ALREADY_EXISTS =
      NEW_LINE_STRING + FOLD + "Result file already exists: ";
  static final String MESSAGE_QUERY_WONT_BE_RE_RUN =
      NEW_LINE_STRING + FOLD + "Query won't be re-run.";
  static final String MESSAGE_DO_YOU_WANT_TO_CANCEL_THIS_QUERY =
      NEW_LINE_STRING
          + FOLD
          + "Do you want to cancel this query?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_DO_YOU_WANT_TO_DELETE_THIS_QUERY =
      NEW_LINE_STRING
          + FOLD
          + "Do you want to delete this query?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_DO_YOU_WANT_TO_CANCEL_ALL_QUERIES =
      NEW_LINE_STRING
          + FOLD
          + "Do you want to cancel all queries?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_DO_YOU_WANT_TO_DELETE_ALL_QUERIES =
      NEW_LINE_STRING
          + FOLD
          + "Do you want to delete all queries?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_DO_YOU_WANT_TO_CANCEL_THIS_RUNNING_QUERY_FIRST =
      NEW_LINE_STRING
          + FOLD
          + "Do you want to cancel this running query first?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_SURE_RE_RUN_QUERY_AND_DROP_EXISTING_RESULT =
      NEW_LINE_STRING
          + FOLD
          + "Sure re-run query and drop existing result?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_YOU_CAN_RUN_THIS_QUERY_BY_TYPING =
      NEW_LINE_STRING
          + FOLD
          + "You can run this query by typing: "
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_RUN
          + SPACE_CHAR;
  static final String MESSAGE_CONNECTION_HAS_BEEN_SAVED_SUCCESSFULLY =
      NEW_LINE_STRING + FOLD + "The connection has been saved successfully.";
  static final String MESSAGE_ERROR_DELETING_NEW_IV_FILE =
      FOLD + "Error while deleting newly created " + APP_IV_POSTFIX + " file! ";
  static final String MESSAGE_MISSING_CS_OR_SL_OR_IV_FILE =
      NEW_LINE_STRING
          + FOLD
          + "Sorry but you have no original "
          + APP_CS_POSTFIX
          + " file or "
          + APP_SL_POSTFIX
          + " file or "
          + APP_IV_POSTFIX
          + " file for ";
  static final String MESSAGE_ENTER_PASSWORD_VERIFY = FOLD + "Please verify it: ";
  static final String MESSAGE_WRONG_PARAMETERS =
      NEW_LINE_STRING + FOLD + "You have used wrong parameters!";
  static final String MESSAGE_THE_PASSWORD_IS_NOT_VALID = "The password is not valid.";
  static final String MESSAGE_CONNECTIONS_GOOD_PASSWORD_IS_NOT_VALID =
      NEW_LINE_STRING
          + FOLD
          + MESSAGE_THE_PASSWORD_IS_NOT_VALID
          + NEW_LINE_CHAR
          + MESSAGE_CONNECTIONS_GOOD_PASSWORD;
  static final String MESSAGE_INCORRECT_FILE_PASSWORD =
      NEW_LINE_STRING + FOLD + "The password you have entered is incorrect.";
  static final String MESSAGE_ENTER_PASSWORD_FOR_CONNECTIONS =
      NEW_LINE_STRING + FOLD + "Enter your connections password: ";
  static final String MESSAGE_YOU_HAVE_TO_USE_A_CONNECTION_OR_GIVE_THE_CONNECTION_NAME_TO_CHANGE =
      NEW_LINE_STRING
          + FOLD
          + "You have to use a connection or give the connection name to change!";
  static final String MESSAGE_MISSING_NEW_CS_OR_SL_OR_IV_FILE =
      NEW_LINE_STRING
          + FOLD
          + "Sorry but one or more new file is missing after the saving operation"
          + NEW_LINE_CHAR
          + FOLD
          + "( "
          + APP_CS_POSTFIX
          + " file or "
          + APP_SL_POSTFIX
          + " file or "
          + APP_IV_POSTFIX
          + " ), your changes will be rolled back! ";
  static final String MESSAGE_FILE_HAS_BEEN_SAVED =
      NEW_LINE_STRING + FOLD + "File has been saved: ";
  static final String MESSAGE_THE_NAME_OF_THE_CONNECTION_HAS_TO_BE_AT_LEAST_ONE_CHAR =
      NEW_LINE_STRING + FOLD + "The name of the connection has to be at least one char!";
  static final String MESSAGE_THE_NAME_OF_THE_CONNECTION_CANNOT_CONTAIN_SPACE_CHAR =
      NEW_LINE_STRING + FOLD + "The name of the connection cannot contain space char!";
  static final String MESSAGE_IS_FOLDER_SAFE =
      NEW_LINE_STRING
          + FOLD
          + "As the first step, please move the classes of this "
          + APP_NAME
          + NEW_LINE_CHAR
          + FOLD
          + "into a safe (local personal) folder or into a removable device!"
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "Is the folder of this "
          + APP_NAME
          + ".jar safe enough?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_ARE_YOU_SURE_DELETE_CONNECTION =
      NEW_LINE_STRING
          + FOLD
          + "Are you sure delete connection?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_ARE_YOU_SURE_DELETE_CONNECTIONS_BY_DB_TYPE =
      NEW_LINE_STRING
          + FOLD
          + "Are you sure delete connections by the database type?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_YOUR_CONNECTION_WONT_BE_DELETED =
      NEW_LINE_STRING + FOLD + "OK, your connection won't be deleted.";
  static final String MESSAGE_YOUR_CONNECTIONS_WONT_BE_DELETED =
      NEW_LINE_STRING + FOLD + "OK, your connections won't be deleted.";
  static final String MESSAGE_YOUR_CONNECTION_HAS_BEEN_DELETED_SUCCESSFULLY =
      NEW_LINE_STRING + FOLD + "Your connection has been deleted successfully.";
  static final String MESSAGE_YOUR_CONNECTIONS_HAVE_BEEN_DELETED_SUCCESSFULLY =
      NEW_LINE_STRING + FOLD + "Your connections have been deleted successfully.";
  static final String MESSAGE_QUERY_CANNOT_BE_CANCELLED =
      NEW_LINE_STRING + FOLD + "Query cannot be cancelled : ";
  static final String MESSAGE_QUERY_CANNOT_BE_DELETED =
      NEW_LINE_STRING + FOLD + "Query cannot be deleted : ";
  static final String MESSAGE_JOINING_QUERY_THREAD = FOLD + "Joining query thread: ";
  static final String MESSAGE_DELIMITER_HAS_BEEN_CHANGED_TO = " delimiter has been changed to ";
  static final String MESSAGE_CHANGE_CONN_USER_OR_PASSWORD_BEHAVIOUR =
      "empty, " + MESSAGE_EMPTY + ", a new value";
  static final String MESSAGE_CHANGE_CONN_PROPERTY_BEHAVIOUR =
      NEW_LINE_STRING
          + FOLD
          + "For changing the username or password of this connection"
          + NEW_LINE_CHAR
          + FOLD
          + "- leave empty    (just hit the enter) : remains the same"
          + NEW_LINE_CHAR
          + FOLD
          + "- type '"
          + MESSAGE_EMPTY
          + "'     (without quotes) : will be an empty string: ''"
          + NEW_LINE_CHAR
          + FOLD
          + "- type 'a new value' (without quotes) : your new non-empty value will be used"
          + NEW_LINE_CHAR
          + FOLD
          + "For changing any other property of this connection"
          + NEW_LINE_CHAR
          + FOLD
          + "leave empty the property for further use of value between [] or type a new value"
          + NEW_LINE_CHAR;
  static final String MESSAGE_YOUR_FILE_HAS_NOT_BEEN_FOUND_OR_NOT_BEEN_FILE =
      NEW_LINE_STRING + FOLD + "Your file has not been found or not been file!";
  static final String MESSAGE_IS_FILE_GOOD_FORMATTED =
      NEW_LINE_STRING
          + FOLD
          + "The file you would like to use has to be in this format!"
          + NEW_LINE_CHAR
          + FOLD
          + "Line by line contains the attributes of the connections."
          + NEW_LINE_CHAR
          + NEW_LINE_CHAR
          + FOLD
          + "line 1: database_type     [mysql, oracle, mssql, db2, postgresql]"
          + NEW_LINE_CHAR
          + FOLD
          + "line 2: connection_name   (a name without space, at least 1 char)"
          + NEW_LINE_CHAR
          + FOLD
          + "line 3: database_user     (username, space is allowed ('sys as sysdba'), can be empty)"
          + NEW_LINE_CHAR
          + FOLD
          + "line 4: database_password (password, empty line means empty password)"
          + NEW_LINE_CHAR
          + FOLD
          + "line 5: database_driver   (a driver, for example org.postgresql.Driver, at least 1"
          + " char)"
          + NEW_LINE_CHAR
          + FOLD
          + "line 6: connection_string (for example jdbc:postgresql://localhost:5432/postgres, at"
          + " least 1 char)"
          + NEW_LINE_CHAR
          + FOLD
          + "line 7: database_type2"
          + NEW_LINE_CHAR
          + FOLD
          + "line 8: connection_name2, etc."
          + NEW_LINE_CHAR
          + NEW_LINE_CHAR
          + FOLD
          + "Whitespace chars or empty lines should not be placed at the end or at the beginning of"
          + " the"
          + " file!"
          + NEW_LINE_CHAR
          + NEW_LINE_CHAR
          + FOLD
          + "Is your file formatted like that?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_FILE_WONT_BE_USED = NEW_LINE_STRING + FOLD + "File won't be used.";
  static final String MESSAGE_QUERY_DESC_DB_TYPE =
      NEW_LINE_STRING + FOLD + "Database type       : ";
  static final String MESSAGE_QUERY_DESC_CONNNA = FOLD + "Connection name     : ";
  static final String MESSAGE_QUERY_DESC_TITLE = FOLD + "Title of query      : ";
  static final String MESSAGE_QUERY_DESC_STRING = NEW_LINE_STRING + "--Query string: ";
  static final String MESSAGE_QUERY_DESC_DELIMITER =
      NEW_LINE_STRING + FOLD + "Query delimiter     : ";
  static final String MESSAGE_QUERY_DESC_TYPE = FOLD + "Type of query       : ";
  static final String MESSAGE_QUERY_DESC_IS_SCROLLABLE = FOLD + "Query is scrollable : ";
  static final String MESSAGE_QUERY_DESC_BATCH_EXEC_COUNT =
      NEW_LINE_STRING + FOLD + "Batch exec count    : ";
  static final String MESSAGE_QUERY_DESC_BATCH_SOURCE_FILE = FOLD + "Batch source file   : ";
  static final String MESSAGE_QUERY_DESC_BATCH_SOURCE_QUERY_ID = FOLD + "Batch source queryId: ";
  static final String MESSAGE_QUERY_DESC_BATCH_SOURCE_FIELDS = FOLD + "Batch source fields : ";
  static final String MESSAGE_QUERY_DESC_START_DATE =
      NEW_LINE_STRING + FOLD + "Start date          : ";
  static final String MESSAGE_QUERY_DESC_END_DATE = FOLD + "End date            : ";
  static final String MESSAGE_QUERY_DESC_ERROR_MESSAGE = FOLD + "Error message(s)    : ";
  static final String MESSAGE_QUERY_DESC_STATE = NEW_LINE_STRING + FOLD + "Query state         : ";
  static final String MESSAGE_QUERY_DESC_HAS_RESULT_SET =
      NEW_LINE_STRING + FOLD + "This query has a result set.";
  static final String MESSAGE_QUERY_DESC_NO_RESULT_SET =
      NEW_LINE_STRING + FOLD + "This query has no result set.";
  static final String MESSAGE_QUERY_DESC_COLUMNS = FOLD + "Columns: ";
  static final String MESSAGE_QUERY_DESC_ROW_COUNT = FOLD + "Row count: ";
  static final String
      MESSAGE_QUERY_DESC_ROW_COUNT_IS_NOT_DISPLAYABLE_BECAUSE_OF_NOT_SCROLLABLE_RESULT_SET =
          FOLD + "Row count is not displayable because of not scrollable resultSet.";
  static final String MESSAGE_QUERY_DESC_TOTAL_ELAPSED_TIME = FOLD + "Total elapsed time  : ";
  static final String MESSAGE_QUERY_DESC_THREAD_STATE =
      NEW_LINE_STRING + FOLD + "Exec thread state   : ";
  static final String MESSAGE_QUERY_DESC_FILE = NEW_LINE_STRING + FOLD + "Query from file     : ";
  static final String MESSAGE_QUERY_FROM_CONSOLE_OR_FILE =
      NEW_LINE_STRING
          + FOLD
          + "Leave blank to type sql into the console"
          + NEW_LINE_CHAR
          + FOLD
          + "or type your filename containing your sql statement(s)"
          + NEW_LINE_CHAR
          + FOLD
          + ": ";
  static final String MESSAGE_QUERY_BATCH_EXEC_COUNT =
      NEW_LINE_STRING
          + FOLD
          + "The count of the executed sqls at a time is"
          + NEW_LINE_CHAR
          + FOLD
          + ": ";
  static final String MESSAGE_QUERY_BATCH_SOURCE_FROM =
      NEW_LINE_STRING
          + FOLD
          + "The source of your sql batch containing the data separated by your query delimiter is"
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + BATCH_SOURCE_FROM_FILE_VALUE
          + ": "
          + BATCH_SOURCE_FROM_FILE_STRING
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + BATCH_SOURCE_FROM_RESULT_SET_VALUE
          + ": "
          + BATCH_SOURCE_FROM_RESULT_SET_STRING
          + NEW_LINE_CHAR
          + FOLD
          + ": ";
  static final String MESSAGE_QUERY_BATCH_SOURCE_FROM_FILE =
      NEW_LINE_STRING + FOLD + "Enter the filename" + NEW_LINE_CHAR + FOLD + ": ";
  static final String MESSAGE_QUERY_BATCH_SOURCE_FROM_QURERY_ID =
      NEW_LINE_STRING + FOLD + "Enter the query ID" + NEW_LINE_CHAR + FOLD + ": ";
  static final String MESSAGE_QUERY_BATCH_SOURCE_FIELDS_FIELDS =
      NEW_LINE_STRING
          + FOLD
          + "Fields you are going to use (separated by the delimiter) are"
          + NEW_LINE_CHAR
          + FOLD
          + "[available: "
          + FIELDS_REPLACE
          + "]"
          + NEW_LINE_CHAR
          + FOLD
          + ": ";
  static final String MESSAGE_WRONG_QUERY_BATCH_SOURCE_FROM =
      NEW_LINE_STRING
          + FOLD
          + "The query batch source can be "
          + BATCH_SOURCE_FROM_FILE_STRING
          + " or "
          + BATCH_SOURCE_FROM_RESULT_SET_STRING
          + "!";
  static final String MESSAGE_COUNT_OF_LINES_OF_FILE_IS_NOT_THE_EXPECTED =
      NEW_LINE_STRING + FOLD + "Count of lines of file is not the expected.";
  static final String MESSAGE_YOUR_QUERY_HAS_BEEN_EXECUTED_SUCCESSFULLY =
      NEW_LINE_STRING + FOLD + "Your query has been executed successfully.";
  static final String MESSAGE_THIS_RESULT_SET_CANNOT_BE_USED_AS_THE_SOURCE =
      NEW_LINE_STRING + FOLD + "This result set cannot be used as the source.";
  static final String MESSAGE_APPEND_THIS_FILE_INTO_THE_END_OF_CURRENT_CONNECTIONS =
      NEW_LINE_STRING
          + FOLD
          + "Append this file into the end of current connections?"
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_TYPE_YES_ELSE_ANYTHING;
  static final String MESSAGE_CONNECTIONS_WILL_NOT_BE_IMPORTED =
      NEW_LINE_STRING + FOLD + "Connections will not be imported.";
  static final String MESSAGE_LOADING_CONNECTION = FOLD + "Loading connection ";
  static final String MESSAGE_CONNECTION_LOADING_FAILED_WRONG_DATABASE_TYPE =
      "wrong database type!";
  static final String MESSAGE_CONNECTION_LOADING_FAILED_SPACE_IN_CONNECTION_NAME =
      "space in connection name!";
  static final String MESSAGE_CONNECTION_LOADING_FAILED_CONNECTION_NAME_CANNOT_BE_EMPTY =
      "connection name cannot be empty!";
  static final String MESSAGE_CONNECTION_LOADING_FAILED_EXISTING_CONNECTION =
      "this connection has already been set!";
  static final String MESSAGE_CONNECTION_LOADING_FAILED_DRIVER_CANNOT_BE_EMPTY =
      "driver cannot be empty!";
  static final String MESSAGE_CONNECTION_LOADING_FAILED_CONNECTION_STRING_CANNOT_BE_EMPTY =
      "connection string cannot be empty!";
  static final String MESSAGE_DONE = "done.";
  static final String MESSAGE_CONNECTIONS_HAVE_BEEN_LOADED = " connections have been loaded.";
  static final String MESSAGE_CONNECTION_HAVE_BEEN_LOADED =
      NEW_LINE_STRING + FOLD + "One connection has been loaded.";
  static final String MESSAGE_ALL_OF_THE_CONNECTIONS_HAVE_BEEN_LOADED =
      NEW_LINE_STRING + FOLD + "All of the connections have been loaded: ";
  static final String MESSAGE_NO_CONNECTIONS_HAVE_BEEN_LOADED =
      NEW_LINE_STRING + FOLD + "No connections have been loaded.";
  static final String MESSAGE_APPLICATION_DESCRIBE =
      NEW_LINE_STRING
          + FOLD
          + APP_NAME
          + " information."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "Current version: "
          + APP_VERSION
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "Input information."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Maximum length of any input                     : "
          + APP_MAX_LENGTH_OF_INPUT
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Maximum length of an sql to be executed         : "
          + APP_MAX_LENGTH_OF_SQL
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "Connections file content specific information."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Directory name of the connections file          : "
          + APP_CONNECTIONS_DIR
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "File name of the connections file               : "
          + APP_CONNECTIONS_FILE_NAME
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Postfix of connection file                      : "
          + APP_CS_POSTFIX
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Postfix of salt file                            : "
          + APP_SL_POSTFIX
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Postfix of initialization vector file           : "
          + APP_IV_POSTFIX
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Postfix of newly created files of above         : "
          + APP_NW_POSTFIX
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "The caching of the connections password         : "
          + APP_CONNECTION_PASSWORD_CACHE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Maximum storable connections                    : "
          + APP_MAX_NUM_OF_CONNECTIONS
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Max length of the content of cs file (bytes)    : "
          + APP_FILE_CONTENT_MAX_LENGTH
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "Connections file password specific information."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Minimum count of uppercase letters              : "
          + APP_GOOD_PASSWORD_MIN_COUNT_OF_UC_LETTERS
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Minimum count of lowercase letters              : "
          + APP_GOOD_PASSWORD_MIN_COUNT_OF_LC_LETTERS
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Minimum count of digits                         : "
          + APP_GOOD_PASSWORD_MIN_COUNT_OF_DIGITS
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Minimum count of special chars                  : "
          + APP_GOOD_PASSWORD_MIN_COUNT_OF_SPEC_CHARS
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Minimum length of password                      : "
          + APP_GOOD_PASSWORD_MIN_LENGTH_OF_GOOD_PASSWORDS
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "Connections encrypt/decrypt information."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Salt length                                     : "
          + APP_SALT_LENGTH
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Pbe key spec iterations                         : "
          + APP_PBE_KEY_SPEC_ITERATIONS
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Pbe key spec key length                         : "
          + APP_PBE_KEY_SPEC_KEY_LENGTH
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Secret key factory instance                     : "
          + APP_SECRET_KEY_FACTORY_INSTANCE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Secret key spec algorithm                       : "
          + APP_SECRET_KEY_SPEC_ALGORYTHM
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Cipher instance                                 : "
          + APP_CIPHER_INSTANCE
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "Other information."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Query file prefix                               : "
          + APP_QUERY_FILE_PREFIX
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Date format for displaying                      : "
          + APP_DATE_FORMAT_FOR_DISPLAYING
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Date format for filenames                       : "
          + APP_DATE_FORMAT_FOR_FILENAMES
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Date format for timestamps                      : "
          + APP_DATE_FORMAT_FOR_TIMESTAMPS
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Max seconds to enter any input                  : "
          + APP_MAX_NOT_READ_INPUTS_SECONDS
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Max time (ms) to wait for the immediate result  : "
          + APP_MAX_NUM_OF_MILLISECONDS_TO_WAIT_FOR_THE_RESULT
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Max length of the column in txt results         : "
          + APP_MAX_COL_LENGTH_TXT
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Max length of the id + title                    : "
          + APP_MAX_QUERY_ID_TITLE_CONNNA_WIDTH;
  static final String MESSAGE_APPLICATION_STORY =
      NEW_LINE_STRING
          + FOLD
          + "The application story."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + "The situation."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "The most important goal was to construct a lightweight sql client that"
          + NEW_LINE_CHAR
          + FOLD
          + "supports our daily development of prdare. We thought that it would be nice to"
          + NEW_LINE_CHAR
          + FOLD
          + "connect all of the databases supported by prdare in the same application."
          + NEW_LINE_CHAR
          + FOLD
          + "This was the reason for the birth of this "
          + APP_NAME
          + " command line application."
          + NEW_LINE_CHAR
          + FOLD
          + "Its modified version is published."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + "The most important features of "
          + APP_NAME
          + "."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "- connection management, in encrypted files"
          + NEW_LINE_CHAR
          + FOLD
          + "  adds, edits and deletes connections"
          + NEW_LINE_CHAR
          + FOLD
          + "  loads connections from file"
          + NEW_LINE_CHAR
          + FOLD
          + "  the possibility to cache the connections password"
          + NEW_LINE_CHAR
          + FOLD
          + "  (compile time property, this app has to be recompiled to change this)"
          + NEW_LINE_CHAR
          + FOLD
          + "- Mysql, Oracle, Mssql, Db2 and Postgresql databases are supported"
          + NEW_LINE_CHAR
          + FOLD
          + "- multiple queries are available to run"
          + NEW_LINE_CHAR
          + FOLD
          + "  in multiple database types at the same time"
          + NEW_LINE_CHAR
          + FOLD
          + "- sql-s can come from the console or from file"
          + NEW_LINE_CHAR
          + FOLD
          + "  supporting multi line sql commands, sql command delimiter by database types"
          + NEW_LINE_CHAR
          + FOLD
          + "- added sql queries will be run in separate threads"
          + NEW_LINE_CHAR
          + FOLD
          + "  these can be watched and the results can be seen"
          + NEW_LINE_CHAR
          + FOLD
          + "  user can work while several sql-s are running on the separate threads"
          + NEW_LINE_CHAR
          + FOLD
          + "- sql queries can be cancelled if your driver also supports this"
          + NEW_LINE_CHAR
          + FOLD
          + "- query factory mode: the user can type their sql queries or commands"
          + NEW_LINE_CHAR
          + FOLD
          + "  continuously and see the results of these"
          + NEW_LINE_CHAR
          + FOLD
          + "  (user has to wait for these queries to finish, not in separate threads)"
          + NEW_LINE_CHAR
          + FOLD
          + "- sql results can go onto the console or into txt, csv and/or htm files"
          + NEW_LINE_CHAR
          + FOLD
          + "- single, multiple or batch type of queries can be added"
          + NEW_LINE_CHAR
          + FOLD
          + "  single: single sql query, selects mostly. It has a result set object"
          + NEW_LINE_CHAR
          + FOLD
          + "  multiple: executes multiple sql commands and it has no result set"
          + NEW_LINE_CHAR
          + FOLD
          + "  batch: one single sql query using a file or result set as datasource"
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + "When to use this application?"
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "- if the user likes the command line apps"
          + NEW_LINE_CHAR
          + FOLD
          + "- or the user can work in the command line environment like a unix server."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + "Warning!"
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + "- The passwords of the connections are stored as strings in many points"
          + NEW_LINE_CHAR
          + FOLD
          + "  in the memory area of jvm as it uses"
          + NEW_LINE_CHAR
          + FOLD
          + "  DriverManager.getConnection(connection_string,database_user,password)."
          + NEW_LINE_CHAR
          + FOLD
          + "  This getConnection method accepts only a String-typed password."
          + NEW_LINE_CHAR
          + FOLD
          + "- Use ssl connection when possible to avoid password leak on the network!"
          + NEW_LINE_CHAR
          + FOLD
          + "  (Your connection passwords are safe in your encrypted connections file.)";
  static final String MESSAGE_HINTS =
      NEW_LINE_STRING
          + FOLD
          + ARG_APPLICATION
          + SPACE_CHAR
          + ARG_DESCRIBE
          + NEW_LINE_CHAR
          + FOLD
          + ARG_APPLICATION
          + SPACE_CHAR
          + ARG_STORY
          + NEW_LINE_CHAR
          + FOLD
          + ARG_WELCOME
          + SPACE_CHAR
          + ARG_SCREEN
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_YOUR_DATABASE_TYPE
          + NEW_LINE_CHAR
          + FOLD
          + PROMPT_TO_UPPER_OR_EXIT
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_LIST
          + SPACE_CHAR
          + ARG_ACTIVE
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_LIST
          + SPACE_CHAR
          + ARG_INACTIVE
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_LISTALL
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_ADD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_LOAD
          + SPACE_CHAR
          + MESSAGE_YOUR_FILE_NAME
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_DESCRIBE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_DESCRIBE
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_CHANGE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_CHANGE
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_DELETE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_DELETE
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_DELETEALL
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_USE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_USE
          + SPACE_CHAR
          + MESSAGE_YOUR_DATABASE_TYPE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_TEST
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_TEST
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_LIST
          + SPACE_CHAR
          + ARG_ACTIVE
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_LIST
          + SPACE_CHAR
          + ARG_INACTIVE
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_LISTALL
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_ADD
          + SPACE_CHAR
          + ARG_SINGLE
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_ADD
          + SPACE_CHAR
          + ARG_MULTIPLE
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_ADD
          + SPACE_CHAR
          + ARG_BATCH
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_FACTORY
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_FACTORY
          + SPACE_CHAR
          + MESSAGE_YOUR_DATABASE_TYPE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_DESCRIBE
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY_ID
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_RUN
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY_ID
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_CANCEL
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY_ID
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_CANCELALL
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_DELETE
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY_ID
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_DELETEALL
          + NEW_LINE_CHAR
          + FOLD
          + ARG_RESULT
          + SPACE_CHAR
          + ARG_ECHO
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY_ID
          + NEW_LINE_CHAR
          + FOLD
          + ARG_DELIMITER
          + SPACE_CHAR
          + ARG_SHOW
          + NEW_LINE_CHAR
          + FOLD
          + ARG_DELIMITER
          + SPACE_CHAR
          + ARG_CHANGE
          + SPACE_CHAR
          + MESSAGE_YOUR_NEW_DELIMITER
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTIONS
          + SPACE_CHAR
          + ARG_GOOD
          + SPACE_CHAR
          + ARG_PASSWORD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTIONS
          + SPACE_CHAR
          + ARG_PASSWORD
          + SPACE_CHAR
          + ARG_CHANGE
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUESTION_MARK
          + NEW_LINE_CHAR
          + FOLD
          + ARG_HELP;
  static final String MESSAGE_HELP =
      NEW_LINE_STRING
          + FOLD
          + "Please type these arguments to use "
          + APP_NAME
          + " correctly:"
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_APPLICATION
          + SPACE_CHAR
          + ARG_DESCRIBE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Prints the information of this application for you."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_APPLICATION
          + SPACE_CHAR
          + ARG_STORY
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Prints the basic concept and the basic usage, please read it carefully."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_WELCOME
          + SPACE_CHAR
          + ARG_SCREEN
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Prints the first run screen."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + MESSAGE_YOUR_DATABASE_TYPE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + DB_TYPE_MYSQL
          + " or "
          + DB_TYPE_ORACLE
          + " or "
          + DB_TYPE_MSSQL
          + " or "
          + DB_TYPE_DB2
          + " or "
          + DB_TYPE_POSTGRESQL
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "These databases can be used. Changes the application prompt."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + PROMPT_TO_UPPER_OR_EXIT
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Steps to upper levels."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "If the application level is set then you can exit."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "If a database level is set then steps to application."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "If a connection level is set then steps to database."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_LIST
          + SPACE_CHAR
          + ARG_ACTIVE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Lists the connections used by at least one query."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_LIST
          + SPACE_CHAR
          + ARG_INACTIVE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Lists the connections not used by queries."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_LISTALL
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Lists all of your defined connections."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_ADD
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Adds a new connection into your set of database connections."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_LOAD
          + SPACE_CHAR
          + MESSAGE_YOUR_FILE_NAME
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Loads and appends database connections from a file."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Only files right next to the application are allowed."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_DESCRIBE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Describes a connection you specified before."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_DESCRIBE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Describes a connection you are using."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_CHANGE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Changes a not-in-use connection you specified before."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_CHANGE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Changes a not-in-use connection you are using."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_DELETE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Deletes a not-in-use connection you specified before."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_DELETE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Deletes the connection you are using."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_DELETEALL
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Deletes all not-in-use connections you specified before."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_USE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Use this to use a specific database connection continuously."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_USE
          + SPACE_CHAR
          + MESSAGE_YOUR_DATABASE_TYPE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Same as above, but you can use a different connection in a different database type."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_TEST
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Tests the database connection."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTION
          + SPACE_CHAR
          + ARG_TEST
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Tests a new and unsaved database connection."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_LIST
          + SPACE_CHAR
          + ARG_ACTIVE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Lists the active (running) queries."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_LIST
          + SPACE_CHAR
          + ARG_INACTIVE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Lists the inactive (not started, finished) queries."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_LISTALL
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Lists all of your queries."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_ADD
          + SPACE_CHAR
          + ARG_SINGLE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Adds a single query to run."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Will have a result set in case of running successfully."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_ADD
          + SPACE_CHAR
          + ARG_MULTIPLE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Adds multiple and separated queries to run."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Won't have any result set."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_ADD
          + SPACE_CHAR
          + ARG_BATCH
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Adds an sql and repeats it as specified."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_FACTORY
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Writes sql commands continuously and displays the results."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "The queries will be executed on the main application thread,"
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "so the prompt will return only when the currently running query has finished."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "The "
          + MESSAGE_YOUR_QUERY
          + " is optional and has to be double-quoted if it contains a space."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "It can start with \""
          + APP_QUERY_FILE_PREFIX
          + "\" to dynamically read the content of the file next to"
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "this application to use its content as the initial sql statement to execute."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "If you specify this query then this application will execute it immediately."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_FACTORY
          + SPACE_CHAR
          + MESSAGE_YOUR_DATABASE_TYPE
          + SPACE_CHAR
          + MESSAGE_YOUR_CONNECTION_NAME
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Same as above, but you can use a connection immediately."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_DESCRIBE
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY_ID
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Describes the current state of a query."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_RUN
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY_ID
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Runs your query if it is possible (not started or finished query)."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_CANCEL
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY_ID
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Cancels your query if it is possible (running query)."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_CANCELALL
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Cancels all of your queries if it is possible (running queries)."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_DELETE
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY_ID
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Deletes your query if it is possible (not started or finished query)."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUERY
          + SPACE_CHAR
          + ARG_DELETEALL
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Deletes all of your queries if it is possible (not started or finished queries)."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_RESULT
          + SPACE_CHAR
          + ARG_ECHO
          + SPACE_CHAR
          + MESSAGE_YOUR_QUERY_ID
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Echoes the last result of that query onto the console or into files."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_DELIMITER
          + SPACE_CHAR
          + ARG_SHOW
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Shows your delimiter in a specific database type."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_DELIMITER
          + SPACE_CHAR
          + ARG_CHANGE
          + SPACE_CHAR
          + MESSAGE_YOUR_NEW_DELIMITER
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Changes your delimiter in a specific database type."
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "If the "
          + MESSAGE_YOUR_NEW_DELIMITER
          + " is empty then the empty character"
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "(new line char) will be used as the delimiter."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTIONS
          + SPACE_CHAR
          + ARG_GOOD
          + SPACE_CHAR
          + ARG_PASSWORD
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Prints the expectations of the encrypted file containing the connections data."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_CONNECTIONS
          + SPACE_CHAR
          + ARG_PASSWORD
          + SPACE_CHAR
          + ARG_CHANGE
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Changes your connections password."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_QUESTION_MARK
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Prints the available commands only."
          + NEW_LINE_CHAR
          + FOLD
          + NEW_LINE_CHAR
          + FOLD
          + ARG_HELP
          + NEW_LINE_CHAR
          + FOLD
          + FOLD2
          + "Prints this page.";
  static final String MESSAGE_BYE = NEW_LINE_STRING + FOLD + "Bye!" + NEW_LINE_STRING;
}
