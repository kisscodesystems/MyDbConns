package com.kisscodesystems.MyDbConns;

/**
 * Holds the command-keyword strings recognized by the application's interactive command dispatch,
 * together with the query-state label strings. These constants define the vocabulary of commands
 * and arguments a user can type.
 */
final class Args {
  static final String ARG_APPLICATION = "application";
  static final String ARG_DESCRIBE = "describe";
  static final String ARG_STORY = "story";
  static final String ARG_QUESTION_MARK = "?";
  static final String ARG_HELP = "help";
  static final String ARG_WELCOME = "welcome";
  static final String ARG_SCREEN = "screen";
  static final String ARG_GOOD = "good";
  static final String ARG_PASSWORD = "password";
  static final String ARG_EXIT = "exit";
  static final String ARG_CHANGE = "change";
  static final String ARG_DELIMITER = "delimiter";
  static final String ARG_SHOW = "show";
  static final String ARG_CONNECTION = "connection";
  static final String ARG_CONNECTIONS = "connections";
  static final String ARG_ADD = "add";
  static final String ARG_LIST = "list";
  static final String ARG_ACTIVE = "active";
  static final String ARG_INACTIVE = "inactive";
  static final String ARG_LISTALL = "listall";
  static final String ARG_DELETE = "delete";
  static final String ARG_DELETEALL = "deleteall";
  static final String ARG_LOAD = "load";
  static final String ARG_QUERY = "query";
  static final String ARG_SINGLE = "single";
  static final String ARG_MULTIPLE = "multiple";
  static final String ARG_BATCH = "batch";
  static final String ARG_RUN = "run";
  static final String ARG_CANCEL = "cancel";
  static final String ARG_CANCELALL = "cancelall";
  static final String ARG_RESULT = "result";
  static final String ARG_ECHO = "echo";
  static final String ARG_USE = "use";
  static final String ARG_TEST = "test";
  static final String ARG_FACTORY = "factory";
  static final String QUERY_STATES = "query states: ";
  static final String QUERY_STATE_NOT_STARTED = "not started";
  static final String QUERY_STATE_RUNNING = "running";
  static final String QUERY_STATE_FINISHED_SUCCESSFULLY = "finished successfully";
  static final String QUERY_STATE_FINISHED_WITH_ERRORS = "finished with errors";
}
