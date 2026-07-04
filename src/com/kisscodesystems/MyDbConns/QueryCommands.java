package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Args.*;
import static com.kisscodesystems.MyDbConns.Connections.*;
import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.Print.*;
import static com.kisscodesystems.MyDbConns.QueryStore.*;

import java.sql.ResultSet;
import java.util.Date;

/**
 * Command handlers for queries: listing (active/inactive/all), adding (single/multiple/batch),
 * describing, running, cancelling (one or all), and deleting (one or all). These validate arguments
 * and delegate to {@code QueryStore}.
 */
final class QueryCommands {
  /** Lists the currently active queries. */
  static final void executeCommandQueryListActive() {
    listQueries(TYPE_TO_LIST_ACTIVE);
  }

  /** Lists the currently inactive queries. */
  static final void executeCommandQueryListInactive() {
    listQueries(TYPE_TO_LIST_INACTIVE);
  }

  /** Lists all queries regardless of state. */
  static final void executeCommandQueryListall() {
    listQueries(TYPE_TO_LIST_ALL);
  }

  /** Adds a new single query. */
  static final void executeCommandQueryAddSingle() {
    addQuery(ARG_SINGLE);
  }

  /** Adds a new multiple query. */
  static final void executeCommandQueryAddMultiple() {
    addQuery(ARG_MULTIPLE);
  }

  /** Adds a new batch query. */
  static final void executeCommandQueryAddBatch() {
    addQuery(ARG_BATCH);
  }

  /**
   * Describes the query identified by {@code qid}. Once the connections file is ready and the id is
   * valid, it reads all stored properties of the query (database type, connection, title,
   * delimiter, type, scrollability, batch info, timing, error, state, thread state, and result-set
   * columns and row count) and prints them to the console.
   *
   * @param qid the query id as a string, parsed to an int
   */
  static final void executeCommandQueryDescribe(String qid) {
    if (isFileContentConnectionsOrigReady()) {
      int queryId = parseIntSafe(qid, -1);
      String queryDbType = null;
      String queryConnna = null;
      String queryTitle = null;
      String queryString = null;
      String queryFile = null;
      String queryDelimiter = null;
      String queryType = null;
      String queryIsScrollable = null;
      int queryBatchExecCount = 0;
      String queryBatchSourceFile = null;
      int queryBatchSourceQueryId = -1;
      String queryBatchSourceField = null;
      ResultSet queryResultSet = null;
      Date queryStartDate = null;
      Date queryEndDate = null;
      String queryErrorMessage = null;
      Thread queryThread = null;
      String[] columns = null;
      if (isValidQueryId(queryId)) {
        queryDbType = getQueryDbType(queryId);
        if (queryDbType != null) {
          queryConnna = getQueryConnna(queryId);
          queryTitle = getQueryTitle(queryId);
          queryString = getQueryString(queryId);
          queryFile = getQueryFile(queryId);
          queryDelimiter = getQueryDelimiter(queryId);
          queryType = getQueryType(queryId);
          queryIsScrollable = getQueryIsScrollable(queryId);
          queryBatchExecCount = getQueryBatchExecCount(queryId);
          queryBatchSourceFile = getQueryBatchSourceFile(queryId);
          queryBatchSourceQueryId = getQueryBatchSourceQueryId(queryId);
          queryBatchSourceField = getQueryBatchSourceField(queryId);
          getQueryConnection(queryId);
          getQueryStatement(queryId);
          queryResultSet = getQueryResultSet(queryId);
          queryStartDate = getQueryStartDate(queryId);
          queryEndDate = getQueryEndDate(queryId);
          queryErrorMessage = getQueryErrorMessage(queryId);
          queryThread = getQueryThread(queryId);
          outprintln(MESSAGE_QUERY_DESC_DB_TYPE + queryDbType);
          outprintln(MESSAGE_QUERY_DESC_CONNNA + queryConnna);
          outprintln(MESSAGE_QUERY_DESC_TITLE + queryTitle);
          outprintln(
              MESSAGE_QUERY_DESC_DELIMITER
                  + ("".equals(queryDelimiter) ? MESSAGE_EMPTY : queryDelimiter));
          outprintln(MESSAGE_QUERY_DESC_TYPE + queryType);
          outprintln(MESSAGE_QUERY_DESC_IS_SCROLLABLE + queryIsScrollable);
          if (queryBatchExecCount > 0) {
            outprintln(MESSAGE_QUERY_DESC_BATCH_EXEC_COUNT + queryBatchExecCount);
            if (queryBatchSourceFile != null) {
              outprintln(MESSAGE_QUERY_DESC_BATCH_SOURCE_FILE + queryBatchSourceFile);
            } else if (queryBatchSourceQueryId > -1) {
              outprintln(MESSAGE_QUERY_DESC_BATCH_SOURCE_QUERY_ID + queryBatchSourceQueryId);
            }
            outprintln(MESSAGE_QUERY_DESC_BATCH_SOURCE_FIELDS + queryBatchSourceField);
          }
          outprintln(
              MESSAGE_QUERY_DESC_START_DATE
                  + (queryStartDate == null
                      ? ""
                      : SIMPLE_DATE_FORMAT_FOR_DISPLAYING.format(queryStartDate)));
          outprintln(
              MESSAGE_QUERY_DESC_END_DATE
                  + (queryEndDate == null
                      ? ""
                      : SIMPLE_DATE_FORMAT_FOR_DISPLAYING.format(queryEndDate)));
          outprintln(MESSAGE_QUERY_DESC_TOTAL_ELAPSED_TIME + getQueryElapsedFormatted(queryId));
          printErrorMessage(queryErrorMessage);
          outprintln(MESSAGE_QUERY_DESC_STATE + getQueryState(queryId));
          outprintln(MESSAGE_QUERY_DESC_THREAD_STATE + queryThread.getState());
          if (queryResultSet == null) {
            outprintln(MESSAGE_QUERY_DESC_NO_RESULT_SET);
          } else {
            outprintln(MESSAGE_QUERY_DESC_HAS_RESULT_SET);
            outprintln(MESSAGE_QUERY_DESC_COLUMNS);
            columns = getQueryResultSetColumns(queryId);
            for (int i = 0; i < columns.length; i++) {
              outprintln(FOLD + FOLD2 + columns[i]);
            }
            if (YES.equals(queryIsScrollable)) {
              outprintln(MESSAGE_QUERY_DESC_ROW_COUNT + getQueryResultSetRowCount(queryId));
            } else {
              outprintln(
                  MESSAGE_QUERY_DESC_ROW_COUNT_IS_NOT_DISPLAYABLE_BECAUSE_OF_NOT_SCROLLABLE_RESULT_SET);
            }
          }
          outprintln(
              MESSAGE_QUERY_DESC_STRING
                  + NEW_LINE_CHAR
                  + (queryString == null ? MESSAGE_EMPTY : queryString));
          if (queryFile != null) {
            outprintln(MESSAGE_QUERY_DESC_FILE + queryFile);
          }
        } else {
          outprintln(MESSAGE_QUERY_HAS_NOT_BEEN_FOUND);
        }
      }
      queryId = 0;
      queryDbType = null;
      queryConnna = null;
      queryTitle = null;
      queryString = null;
      queryFile = null;
      queryDelimiter = null;
      queryType = null;
      queryIsScrollable = null;
      queryBatchExecCount = 0;
      queryBatchSourceFile = null;
      queryBatchSourceQueryId = 0;
      queryBatchSourceField = null;
      queryResultSet = null;
      queryStartDate = null;
      queryEndDate = null;
      queryErrorMessage = null;
      queryThread = null;
      columns = null;
    }
  }

  /**
   * Runs the query identified by {@code qid}. Once the connections file is ready and the id is
   * valid, it starts the query.
   *
   * @param qid the query id as a string, parsed to an int
   */
  static final void executeCommandQueryRun(String qid) {
    if (isFileContentConnectionsOrigReady()) {
      int queryId = parseIntSafe(qid, -1);
      if (isValidQueryId(queryId)) {
        startQuery(queryId);
      }
      queryId = 0;
    }
  }

  /**
   * Cancels the query identified by {@code qid}. Once the connections file is ready and the id is
   * valid, it asks the user for confirmation before cancelling.
   *
   * @param qid the query id as a string, parsed to an int
   */
  static final void executeCommandQueryCancel(String qid) {
    if (isFileContentConnectionsOrigReady()) {
      int queryId = parseIntSafe(qid, -1);
      if (isValidQueryId(queryId)) {
        if (readYesElseAnything(
            MESSAGE_DO_YOU_WANT_TO_CANCEL_THIS_QUERY, MESSAGE_QUERY_WONT_BE_CANCELLED)) {
          outprintln("");
          cancelQuery(queryId);
        }
      }
      queryId = 0;
    }
  }

  /** Cancels all queries. */
  static final void executeCommandQueryCancelall() {
    cancelAllQueries(true);
  }

  /**
   * Deletes the query identified by {@code qid}. Once the connections file is ready and the id is
   * valid, it reads the query state; if the query is running it first offers to cancel it, and then
   * asks for confirmation before deleting. Throws a fatal error if the query state is null.
   *
   * @param qid the query id as a string, parsed to an int
   */
  static final void executeCommandQueryDelete(String qid) {
    if (isFileContentConnectionsOrigReady()) {
      int queryId = parseIntSafe(qid, -1);
      String queryState = null;
      boolean go = true;
      if (isValidQueryId(queryId)) {
        queryState = getQueryState(queryId);
        if (queryState != null) {
          go = true;
          if (queryState.equals(QUERY_STATE_RUNNING)) {
            if (readYesElseAnything(
                MESSAGE_DO_YOU_WANT_TO_CANCEL_THIS_RUNNING_QUERY_FIRST,
                MESSAGE_QUERY_WONT_BE_CANCELLED)) {
              go = true;
              cancelQuery(queryId);
            } else {
              go = false;
            }
          }
          if (go) {
            if (readYesElseAnything(
                MESSAGE_DO_YOU_WANT_TO_DELETE_THIS_QUERY, MESSAGE_QUERY_WONT_BE_DELETED)) {
              outprintln("");
              deleteQuery(queryId, true);
            }
          }
        } else {
          throw systemexit("Error - queryState is null, executeCommandQueryDelete");
        }
      }
      queryId = 0;
      queryState = null;
      go = false;
    }
  }

  /** Deletes all queries. */
  static final void executeCommandQueryDeleteall() {
    deleteAllQueries(true);
  }
}
