package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Args.*;
import static com.kisscodesystems.MyDbConns.Connections.*;
import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.FileStore.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.Print.*;
import static com.kisscodesystems.MyDbConns.State.*;
import static com.kisscodesystems.MyDbConns.Utils.*;
import static com.kisscodesystems.MyDbConns.Validate.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * In-memory query engine for MyDbConns. Holds per-query state in parallel HashMaps keyed by an
 * integer query id (database type, connection name, title, SQL string, output file, delimiter,
 * type, scrollable flag, batch parameters, JDBC Connection/Statement/ResultSet, start/end dates,
 * error message and worker thread) and provides getters, synchronized setters, id allocation and
 * the full query lifecycle (save, start, add, cancel, delete, free resources, thread join) plus
 * state, elapsed-time and result-metadata helpers.
 */
final class QueryStore {
  /**
   * Returns the stored database type for the given query id.
   *
   * @param queryId the query id
   * @return the database type mapped to the query id
   */
  static final String getQueryDbType(int queryId) {
    if (queryDbTypes != null) {
      return queryDbTypes.get(queryId);
    } else {
      throw systemexit("Error - queryDbTypes is null, getQueryDbType");
    }
  }

  /**
   * Returns the stored connection name for the given query id.
   *
   * @param queryId the query id
   * @return the connection name mapped to the query id
   */
  static final String getQueryConnna(int queryId) {
    if (queryConnnas != null) {
      return queryConnnas.get(queryId);
    } else {
      throw systemexit("Error - queryConnnas is null, getQueryConnna");
    }
  }

  /**
   * Returns the stored title for the given query id.
   *
   * @param queryId the query id
   * @return the title mapped to the query id
   */
  static final String getQueryTitle(int queryId) {
    if (queryTitles != null) {
      return queryTitles.get(queryId);
    } else {
      throw systemexit("Error - queryTitles is null, getQueryTitle");
    }
  }

  /**
   * Returns the stored SQL string for the given query id.
   *
   * @param queryId the query id
   * @return the SQL string mapped to the query id
   */
  static final String getQueryString(int queryId) {
    if (queryStrings != null) {
      return queryStrings.get(queryId);
    } else {
      throw systemexit("Error - queryStrings is null, getQueryString");
    }
  }

  /**
   * Returns the stored output file name for the given query id.
   *
   * @param queryId the query id
   * @return the file name mapped to the query id
   */
  static final String getQueryFile(int queryId) {
    if (queryFiles != null) {
      return queryFiles.get(queryId);
    } else {
      throw systemexit("Error - queryFiles is null, getQueryFile");
    }
  }

  /**
   * Returns the stored delimiter for the given query id.
   *
   * @param queryId the query id
   * @return the delimiter mapped to the query id
   */
  static final String getQueryDelimiter(int queryId) {
    if (queryDelimiters != null) {
      return queryDelimiters.get(queryId);
    } else {
      throw systemexit("Error - queryDelimiters is null, getQueryDelimiter");
    }
  }

  /**
   * Returns the stored query type for the given query id.
   *
   * @param queryId the query id
   * @return the query type mapped to the query id
   */
  static final String getQueryType(int queryId) {
    if (queryTypes != null) {
      return queryTypes.get(queryId);
    } else {
      throw systemexit("Error - queryTypes is null, getQueryType");
    }
  }

  /**
   * Returns the stored scrollable flag for the given query id.
   *
   * @param queryId the query id
   * @return the scrollable flag mapped to the query id
   */
  static final String getQueryIsScrollable(int queryId) {
    if (queryIsScrollables != null) {
      return queryIsScrollables.get(queryId);
    } else {
      throw systemexit("Error - queryIsScrollables is null, getQueryIsScrollable");
    }
  }

  /**
   * Returns the stored batch execution count for the given query id.
   *
   * @param queryId the query id
   * @return the batch execution count mapped to the query id
   */
  static final int getQueryBatchExecCount(int queryId) {
    if (queryBatchExecCounts != null) {
      return queryBatchExecCounts.get(queryId);
    } else {
      throw systemexit("Error - queryBatchExecCounts is null, getQueryBatchExecCount");
    }
  }

  /**
   * Returns the stored batch source file for the given query id.
   *
   * @param queryId the query id
   * @return the batch source file mapped to the query id
   */
  static final String getQueryBatchSourceFile(int queryId) {
    if (queryBatchSourceFiles != null) {
      return queryBatchSourceFiles.get(queryId);
    } else {
      throw systemexit("Error - queryBatchSourceFiles is null, getQueryBatchSourceFile");
    }
  }

  /**
   * Returns the stored batch source query id for the given query id.
   *
   * @param queryId the query id
   * @return the batch source query id mapped to the query id
   */
  static final int getQueryBatchSourceQueryId(int queryId) {
    if (queryBatchSourceQueryIds != null) {
      return queryBatchSourceQueryIds.get(queryId);
    } else {
      throw systemexit("Error - queryBatchSourceQueryIds is null, getQueryBatchSourceQueryId");
    }
  }

  /**
   * Returns the stored batch source field(s) for the given query id.
   *
   * @param queryId the query id
   * @return the batch source field(s) mapped to the query id
   */
  static final String getQueryBatchSourceField(int queryId) {
    if (queryBatchSourceFields != null) {
      return queryBatchSourceFields.get(queryId);
    } else {
      throw systemexit("Error - queryBatchSourceFields is null, getQueryBatchSourceField");
    }
  }

  /**
   * Returns the stored JDBC connection for the given query id.
   *
   * @param queryId the query id
   * @return the JDBC Connection mapped to the query id
   */
  static final Connection getQueryConnection(int queryId) {
    if (queryConnections != null) {
      return queryConnections.get(queryId);
    } else {
      throw systemexit("Error - queryConnections is null, getQueryConnection");
    }
  }

  /**
   * Returns the stored JDBC statement for the given query id.
   *
   * @param queryId the query id
   * @return the JDBC Statement mapped to the query id
   */
  static final Statement getQueryStatement(int queryId) {
    if (queryStatements != null) {
      return queryStatements.get(queryId);
    } else {
      throw systemexit("Error - queryStatements is null, getQueryStatement");
    }
  }

  /**
   * Returns the stored JDBC result set for the given query id.
   *
   * @param queryId the query id
   * @return the JDBC ResultSet mapped to the query id
   */
  static final ResultSet getQueryResultSet(int queryId) {
    if (queryResultSets != null) {
      return queryResultSets.get(queryId);
    } else {
      throw systemexit("Error - queryResultSets is null, getQueryResultSet");
    }
  }

  /**
   * Returns the stored start date for the given query id.
   *
   * @param queryId the query id
   * @return the start date mapped to the query id
   */
  static final Date getQueryStartDate(int queryId) {
    if (queryStartDates != null) {
      return queryStartDates.get(queryId);
    } else {
      throw systemexit("Error - queryStartDates is null, getQueryStartDate");
    }
  }

  /**
   * Returns the stored end date for the given query id.
   *
   * @param queryId the query id
   * @return the end date mapped to the query id
   */
  static final Date getQueryEndDate(int queryId) {
    if (queryEndDates != null) {
      return queryEndDates.get(queryId);
    } else {
      throw systemexit("Error - queryEndDates is null, getQueryEndDate");
    }
  }

  /**
   * Returns the stored error message for the given query id.
   *
   * @param queryId the query id
   * @return the error message mapped to the query id
   */
  static final String getQueryErrorMessage(int queryId) {
    if (queryErrorMessages != null) {
      return queryErrorMessages.get(queryId);
    } else {
      throw systemexit("Error - queryErrorMessages is null, getQueryErrorMessage");
    }
  }

  /**
   * Returns the stored worker thread for the given query id.
   *
   * @param queryId the query id
   * @return the worker Thread mapped to the query id
   */
  static final Thread getQueryThread(int queryId) {
    if (queryThreads != null) {
      return queryThreads.get(queryId);
    } else {
      throw systemexit("Error - queryThreads is null, getQueryThread");
    }
  }

  /**
   * Stores the database type for the given query id.
   *
   * @param queryId the query id
   * @param queryDbType the database type to store
   */
  static final synchronized void setQueryDbType(int queryId, String queryDbType) {
    if (queryDbTypes != null) {
      queryDbTypes.put(queryId, queryDbType);
    } else {
      throw systemexit("Error - queryDbTypes is null, setQueryDbType");
    }
  }

  /**
   * Stores the connection name for the given query id.
   *
   * @param queryId the query id
   * @param queryConnna the connection name to store
   */
  static final synchronized void setQueryConnna(int queryId, String queryConnna) {
    if (queryConnnas != null) {
      queryConnnas.put(queryId, queryConnna);
    } else {
      throw systemexit("Error - queryConnnas is null, setQueryConnna");
    }
  }

  /**
   * Stores the title for the given query id.
   *
   * @param queryId the query id
   * @param queryTitle the title to store
   */
  static final synchronized void setQueryTitle(int queryId, String queryTitle) {
    if (queryTitles != null) {
      queryTitles.put(queryId, queryTitle);
    } else {
      throw systemexit("Error - queryTitles is null, setQueryTitle");
    }
  }

  /**
   * Stores the SQL string for the given query id.
   *
   * @param queryId the query id
   * @param queryString the SQL string to store
   */
  static final synchronized void setQueryString(int queryId, String queryString) {
    if (queryStrings != null) {
      queryStrings.put(queryId, queryString);
    } else {
      throw systemexit("Error - queryStrings is null, setQueryString");
    }
  }

  /**
   * Stores the output file name for the given query id.
   *
   * @param queryId the query id
   * @param queryFile the file name to store
   */
  static final synchronized void setQueryFile(int queryId, String queryFile) {
    if (queryFiles != null) {
      queryFiles.put(queryId, queryFile);
    } else {
      throw systemexit("Error - queryFiles is null, setQueryFile");
    }
  }

  /**
   * Stores the delimiter for the given query id.
   *
   * @param queryId the query id
   * @param queryDelimiter the delimiter to store
   */
  static final synchronized void setQueryDelimiter(int queryId, String queryDelimiter) {
    if (queryDelimiters != null) {
      queryDelimiters.put(queryId, queryDelimiter);
    } else {
      throw systemexit("Error - queryDelimiters is null, setQueryDelimiter");
    }
  }

  /**
   * Stores the query type for the given query id.
   *
   * @param queryId the query id
   * @param queryType the query type to store
   */
  static final synchronized void setQueryType(int queryId, String queryType) {
    if (queryTypes != null) {
      queryTypes.put(queryId, queryType);
    } else {
      throw systemexit("Error - queryTypes is null, setQueryType");
    }
  }

  /**
   * Stores the scrollable flag for the given query id.
   *
   * @param queryId the query id
   * @param queryIsScrollable the scrollable flag to store
   */
  static final synchronized void setQueryIsScrollable(int queryId, String queryIsScrollable) {
    if (queryIsScrollables != null) {
      queryIsScrollables.put(queryId, queryIsScrollable);
    } else {
      throw systemexit("Error - queryIsScrollables is null, setQueryIsScrollable");
    }
  }

  /**
   * Stores the batch execution count for the given query id.
   *
   * @param queryId the query id
   * @param queryBatchExecCount the batch execution count to store
   */
  static final synchronized void setQueryBatchExecCount(int queryId, int queryBatchExecCount) {
    if (queryBatchExecCounts != null) {
      queryBatchExecCounts.put(queryId, queryBatchExecCount);
    } else {
      throw systemexit("Error - queryBatchExecCounts is null, setQueryBatchExecCount");
    }
  }

  /**
   * Stores the batch source file for the given query id.
   *
   * @param queryId the query id
   * @param queryBatchSourceFile the batch source file to store
   */
  static final synchronized void setQueryBatchSourceFile(int queryId, String queryBatchSourceFile) {
    if (queryBatchSourceFiles != null) {
      queryBatchSourceFiles.put(queryId, queryBatchSourceFile);
    } else {
      throw systemexit("Error - queryBatchSourceFiles is null, setQueryBatchSourceFile");
    }
  }

  /**
   * Stores the batch source query id for the given query id.
   *
   * @param queryId the query id
   * @param queryBatchSourceQueryId the batch source query id to store
   */
  static final synchronized void setQueryBatchSourceQueryId(
      int queryId, int queryBatchSourceQueryId) {
    if (queryBatchSourceQueryIds != null) {
      queryBatchSourceQueryIds.put(queryId, queryBatchSourceQueryId);
    } else {
      throw systemexit("Error - queryBatchSourceQueryIds is null, setQueryBatchSourceQueryId");
    }
  }

  /**
   * Stores the batch source field(s) for the given query id.
   *
   * @param queryId the query id
   * @param queryBatchSourceField the batch source field(s) to store
   */
  static final synchronized void setQueryBatchSourceField(
      int queryId, String queryBatchSourceField) {
    if (queryBatchSourceFields != null) {
      queryBatchSourceFields.put(queryId, queryBatchSourceField);
    } else {
      throw systemexit("Error - queryBatchSourceFields is null, setQueryBatchSourceField");
    }
  }

  /**
   * Stores the JDBC connection for the given query id.
   *
   * @param queryId the query id
   * @param queryConnection the JDBC Connection to store
   */
  static final synchronized void setQueryConnection(int queryId, Connection queryConnection) {
    if (queryConnections != null) {
      queryConnections.put(queryId, queryConnection);
    } else {
      throw systemexit("Error - queryConnections is null, setQueryConnection");
    }
  }

  /**
   * Stores the JDBC statement for the given query id.
   *
   * @param queryId the query id
   * @param queryStatement the JDBC Statement to store
   */
  static final synchronized void setQueryStatement(int queryId, Statement queryStatement) {
    if (queryStatements != null) {
      queryStatements.put(queryId, queryStatement);
    } else {
      throw systemexit("Error - queryStatements is null, setQueryStatement");
    }
  }

  /**
   * Stores the JDBC result set for the given query id.
   *
   * @param queryId the query id
   * @param queryResultSet the JDBC ResultSet to store
   */
  static final synchronized void setQueryResultSet(int queryId, ResultSet queryResultSet) {
    if (queryResultSets != null) {
      queryResultSets.put(queryId, queryResultSet);
    } else {
      throw systemexit("Error - queryResultSets is null, setQueryResultSet");
    }
  }

  /**
   * Stores the start date for the given query id.
   *
   * @param queryId the query id
   * @param queryStartDate the start date to store
   */
  static final synchronized void setQueryStartDate(int queryId, Date queryStartDate) {
    if (queryStartDates != null) {
      queryStartDates.put(queryId, queryStartDate);
    } else {
      throw systemexit("Error - queryStartDates is null, setQueryStartDate");
    }
  }

  /**
   * Stores the end date for the given query id.
   *
   * @param queryId the query id
   * @param queryEndDate the end date to store
   */
  static final synchronized void setQueryEndDate(int queryId, Date queryEndDate) {
    if (queryEndDates != null) {
      queryEndDates.put(queryId, queryEndDate);
    } else {
      throw systemexit("Error - queryEndDates is null, setQueryEndDate");
    }
  }

  /**
   * Stores the error message for the given query id.
   *
   * @param queryId the query id
   * @param queryErrorMessage the error message to store
   */
  static final synchronized void setQueryErrorMessage(int queryId, String queryErrorMessage) {
    if (queryErrorMessages != null) {
      queryErrorMessages.put(queryId, queryErrorMessage);
    } else {
      throw systemexit("Error - queryErrorMessages is null, setQueryErrorMessage");
    }
  }

  /**
   * Stores the worker thread for the given query id.
   *
   * @param queryId the query id
   * @param queryThread the worker Thread to store
   */
  static final synchronized void setQueryThread(int queryId, Thread queryThread) {
    if (queryThreads != null) {
      queryThreads.put(queryId, queryThread);
    } else {
      throw systemexit("Error - queryThreads is null, setQueryThread");
    }
  }

  /**
   * Increments and returns the next available query id.
   *
   * @return the newly allocated query id
   */
  static final synchronized int getNextQueryId() {
    return ++nextQueryId;
  }

  /**
   * Closes and releases the JDBC result set, statement and connection associated with the given
   * query id, printing a message for each resource that fails to close.
   *
   * @param queryId the query id whose JDBC resources should be freed
   */
  static final void freeQueryResources(int queryId) {
    ResultSet resultSet = getQueryResultSet(queryId);
    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (Exception e) {
        outprintln(MESSAGE_FAILED_TO_CLOSE_RESULT_SET + queryId + SPACE_CHAR + e.toString());
      }
    }
    Statement statement = getQueryStatement(queryId);
    if (statement != null) {
      try {
        statement.close();
      } catch (Exception e) {
        outprintln(MESSAGE_FAILED_TO_CLOSE_STATEMENT + queryId + SPACE_CHAR + e.toString());
      }
    }
    Connection connection = getQueryConnection(queryId);
    if (connection != null) {
      try {
        connection.close();
      } catch (Exception e) {
        outprintln(MESSAGE_FAILED_TO_CLOSE_CONNECTION + queryId + SPACE_CHAR + e.toString());
      }
    }
  }

  /**
   * Deletes a query if its id is valid and it is not currently running: frees its JDBC resources,
   * joins its worker thread and removes all per-query state from every map. Prints an appropriate
   * message; when the query is running it cannot be deleted.
   *
   * @param queryId the query id to delete
   * @param messageIfSuccess whether to print a confirmation message on successful deletion
   */
  static final void deleteQuery(int queryId, boolean messageIfSuccess) {
    if (isValidQueryId(queryId)) {
      String queryState = getQueryState(queryId);
      if (!queryState.equals(QUERY_STATE_RUNNING)) {
        freeQueryResources(queryId);
        joinQueryThread(queryId);
        queryDbTypes.remove(queryId);
        queryConnnas.remove(queryId);
        queryTitles.remove(queryId);
        queryStrings.remove(queryId);
        queryFiles.remove(queryId);
        queryDelimiters.remove(queryId);
        queryTypes.remove(queryId);
        queryIsScrollables.remove(queryId);
        queryBatchExecCounts.remove(queryId);
        queryBatchSourceFiles.remove(queryId);
        queryBatchSourceQueryIds.remove(queryId);
        queryBatchSourceFields.remove(queryId);
        queryConnections.remove(queryId);
        queryStatements.remove(queryId);
        queryResultSets.remove(queryId);
        queryStartDates.remove(queryId);
        queryEndDates.remove(queryId);
        queryErrorMessages.remove(queryId);
        queryThreads.remove(queryId);
        if (messageIfSuccess) {
          outprintln(MESSAGE_QUERY_IS_DELETED + " (" + queryId + ")");
        }
      } else {
        outprintln(MESSAGE_QUERY_CANNOT_BE_DELETED + " (" + queryId + ") " + queryState);
      }
      queryState = null;
    }
  }

  /**
   * Joins the worker thread of the given query id if it exists, waiting for it to finish and
   * printing progress; prints an exception message if the join is interrupted.
   *
   * @param queryId the query id whose worker thread should be joined
   */
  static final void joinQueryThread(int queryId) {
    if (isValidQueryId(queryId)) {
      Thread thread = getQueryThread(queryId);
      if (thread != null) {
        outprint(
            MESSAGE_JOINING_QUERY_THREAD
                + thread.getName()
                + SINGLE_SPACE
                + "("
                + getQueryTitle(queryId)
                + ")"
                + DOUBLE_DOT
                + SPACE_CHAR);
        try {
          thread.join();
          outprintln(SINGLE_SPACE + MESSAGE_DONE);
        } catch (InterruptedException e) {
          System.out.println(
              FOLD2
                  + "Exception while joining single query thread (id: "
                  + queryId
                  + "): "
                  + e.toString());
        }
      }
      thread = null;
    }
  }

  /**
   * Cancels every running query (optionally after user confirmation), restricted to the current
   * database type when one is set, and prints how many were cancelled. Throws systemexit if any
   * queryDbTypes entry, key or value is null.
   *
   * @param toConfirm whether to prompt the user for confirmation before cancelling
   * @return true if the cancellation flow ran to completion, false otherwise
   */
  static final boolean cancelAllQueries(boolean toConfirm) {
    boolean success = false;
    int counter = 0;
    boolean toContinue = false;
    if (isFileContentConnectionsOrigReady()) {
      if (!"".equals(dbType)) {
        outprint(NEW_LINE_STRING + FOLD + dbType + ":");
      }
      if (toConfirm) {
        if (readYesElseAnything(
            MESSAGE_DO_YOU_WANT_TO_CANCEL_ALL_QUERIES, MESSAGE_QUERIES_WONT_BE_CANCELLED)) {
          toContinue = true;
        }
      } else {
        toContinue = true;
      }
      if (toContinue) {
        for (HashMap.Entry<Integer, String> queryDbType : queryDbTypes.entrySet()) {
          if (queryDbType != null) {
            if (queryDbType.getKey() != null) {
              if (queryDbType.getValue() != null) {
                if (QUERY_STATE_RUNNING.equals(getQueryState(queryDbType.getKey()))) {
                  if ("".equals(dbType) || queryDbType.getValue().equals(dbType)) {
                    if (counter == 0) {
                      outprintln("");
                    }
                    cancelQuery(queryDbType.getKey());
                    counter++;
                  }
                }
              } else {
                throw systemexit("Error - queryDbTypeValue is null, cancelAllQueries");
              }
            } else {
              throw systemexit("Error - queryDbTypeKey is null, cancelAllQueries");
            }
          } else {
            throw systemexit("Error - queryDbType is null, cancelAllQueries");
          }
        }
        if (counter == 0) {
          outprintln(MESSAGE_NO_RUNNING_QUERIES_HAVE_BEEN_FOUND_TO_CANCEL);
        } else if (counter == 1) {
          outprintln(MESSAGE_ONE_RUNNING_QUERY_HAS_BEEN_CANCELLED);
        } else {
          outprintln(MESSAGE_RUNNING_QUERIES_HAVE_BEEN_CANCELLED + counter);
        }
        success = true;
      }
    }
    return success;
  }

  /**
   * Cancels a single running query: clears its start/end/error state, cancels its JDBC statement,
   * frees its resources and prints the outcome. Does nothing if the query is not running; throws
   * systemexit if the statement is null.
   *
   * @param queryId the query id to cancel
   */
  static final void cancelQuery(int queryId) {
    if (isValidQueryId(queryId)) {
      String queryState = getQueryState(queryId);
      if (queryState.equals(QUERY_STATE_RUNNING)) {
        Statement statement = getQueryStatement(queryId);
        try {
          setQueryStartDate(queryId, null);
          if (statement != null) {
            statement.cancel();
          }
          setQueryEndDate(queryId, null);
          setQueryErrorMessage(queryId, null);
          freeQueryResources(queryId);
          outprintln(MESSAGE_QUERY_IS_CANCELLED + " (" + queryId + ")");
        } catch (Exception e) {
          outprintln(MESSAGE_QUERY_CANNOT_BE_CANCELLED + " (" + queryId + ") " + e.getMessage());
        }
        statement = null;
      } else {
        outprintln(MESSAGE_QUERY_CANNOT_BE_CANCELLED + " (" + queryId + ") " + queryState);
      }
      queryState = null;
    }
  }

  /**
   * Starts a query for the given id: if it has never started, starts its worker thread and displays
   * the result immediately. If it has already started, offers to cancel a still-running query or to
   * re-run a finished one, resetting its state and thread before starting over. Throws systemexit
   * if the worker thread is null on first start.
   *
   * @param queryId the query id to start (or re-run)
   */
  static final void startQuery(int queryId) {
    if (isValidQueryId(queryId)) {
      if (getQueryStartDate(queryId) == null) {
        Thread queryThread = getQueryThread(queryId);
        if (queryThread != null) {
          queryThread.start();
          displayResultImmediately(queryId);
        } else {
          throw systemexit("Error - queryThread is null, startQuery");
        }
        queryThread = null;
      } else {
        boolean startOver = false;
        if (getQueryEndDate(queryId) == null) {
          outprintln(MESSAGE_QUERY_HAS_NOT_BEEN_FINISHED);
          if (readYesElseAnything(
              MESSAGE_DO_YOU_WANT_TO_CANCEL_THIS_QUERY, MESSAGE_QUERY_WONT_BE_CANCELLED)) {
            cancelQuery(queryId);
            startOver = true;
          }
        } else {
          if (readYesElseAnything(
              MESSAGE_SURE_RE_RUN_QUERY_AND_DROP_EXISTING_RESULT, MESSAGE_QUERY_WONT_BE_RE_RUN)) {
            startOver = true;
          }
        }
        if (startOver) {
          setQueryConnection(queryId, null);
          setQueryStatement(queryId, null);
          setQueryResultSet(queryId, null);
          setQueryStartDate(queryId, null);
          setQueryEndDate(queryId, null);
          setQueryErrorMessage(queryId, null);
          setQueryThread(queryId, null);
          Query query = new Query();
          Thread queryThreadNew = new Thread(query);
          queryThreadNew.setName(String.valueOf(queryId));
          setQueryThread(queryId, queryThreadNew);
          queryThreadNew.start();
          displayResultImmediately(queryId);
          queryThreadNew = null;
          query = null;
        }
        startOver = false;
      }
    }
  }

  /**
   * Allocates a new query id, stores all supplied per-query state, creates the worker thread and
   * prints the saved query id.
   *
   * @param dbtype the database type
   * @param connna the connection name
   * @param qtitle the query title
   * @param qstrin the SQL string
   * @param qfile the output file name
   * @param qdelim the delimiter
   * @param qtype the query type
   * @param qscroll the scrollable flag
   * @param qbatchcount the batch execution count
   * @param qbatchsfile the batch source file
   * @param qbatchsqueryid the batch source query id
   * @param qbatchsfields the batch source field(s)
   * @return the newly allocated query id
   */
  static final int saveQuery(
      String dbtype,
      String connna,
      String qtitle,
      String qstrin,
      String qfile,
      String qdelim,
      String qtype,
      String qscroll,
      int qbatchcount,
      String qbatchsfile,
      int qbatchsqueryid,
      String qbatchsfields) {
    int queryId = getNextQueryId();
    setQueryDbType(queryId, dbtype);
    setQueryConnna(queryId, connna);
    setQueryTitle(queryId, qtitle);
    setQueryString(queryId, qstrin);
    setQueryFile(queryId, qfile);
    setQueryDelimiter(queryId, qdelim);
    setQueryType(queryId, qtype);
    setQueryIsScrollable(queryId, qscroll);
    setQueryBatchExecCount(queryId, qbatchcount);
    setQueryBatchSourceFile(queryId, qbatchsfile);
    setQueryBatchSourceQueryId(queryId, qbatchsqueryid);
    setQueryBatchSourceField(queryId, qbatchsfields);
    Query query = new Query();
    Thread queryThread = new Thread(query);
    queryThread.setName(String.valueOf(queryId));
    setQueryThread(queryId, queryThread);
    outprintln(MESSAGE_SAVED_QUERY_ID + queryId);
    query = null;
    queryThread = null;
    return queryId;
  }

  /**
   * Interactively builds and saves a query of the given type by prompting for database type,
   * connection, title and SQL (from console or file), validating each input, dispatching to the
   * batch or single/multiple save path, and optionally running it immediately. Returns early if
   * connections are not ready or validation fails; throws systemexit if the console/file choice is
   * null.
   *
   * @param queryType the type of query to add (e.g. single, multiple or batch)
   */
  static final void addQuery(String queryType) {
    if (!isFileContentConnectionsOrigReady()) {
      return;
    }
    String dbtype;
    if (!"".equals(dbType)) {
      dbtype = dbType;
    } else {
      dbtype =
          readline(NEW_LINE_STRING + MESSAGE_DATABASE_TYPE, APP_MAX_LENGTH_OF_INPUT).toLowerCase();
    }
    if (!isValidDbType(dbtype, true)) {
      return;
    }
    String qdelim = getDelimiter(dbtype);
    if (!isQueryTypeAndDelimiterOk(queryType, qdelim)) {
      outprintln(MESSAGE_DELIMITER_IS_EMPTY_WHILE_ADDING_BATCH_SQL);
      return;
    }
    String connna;
    if (!"".equals(dbConn)) {
      connna = dbConn;
    } else {
      connna = readline(NEW_LINE_STRING + MESSAGE_CONNECTION_NAME, APP_MAX_LENGTH_OF_INPUT);
    }
    if (getConnnaPos(dbtype, connna) == -1) {
      outprintln(MESSAGE_YOUR_CONNECTION_DOES_NOT_EXIST);
      return;
    }
    String qtitle = readline(NEW_LINE_STRING + MESSAGE_QUERY_TITLE, APP_MAX_LENGTH_OF_INPUT);
    String qfromf =
        readline(MESSAGE_QUERY_FROM_CONSOLE_OR_FILE, APP_MAX_LENGTH_OF_INPUT).trim().toLowerCase();
    if (qfromf == null) {
      throw systemexit("Error - qfromf is null, addQuery");
    }
    String qstrin = null;
    String qfile = null;
    if (qfromf.equals("")) {
      qstrin = readQueryStringFromConsole(queryType, qdelim);
    } else {
      qfile = qfromf.trim();
    }
    if (!(qfile == null || (isExistingFile(qfile, true)) && isValidFilePath(qfile, true))) {
      return;
    }
    int queryId;
    if (ARG_BATCH.equals(queryType)) {
      queryId = addBatchQuery(dbtype, connna, qtitle, qstrin, qfile, qdelim, queryType);
    } else if (ARG_SINGLE.equals(queryType)) {
      queryId =
          saveQuery(
              dbtype,
              connna,
              qtitle,
              qstrin,
              qfile,
              qdelim,
              queryType,
              promptScrollable(),
              0,
              null,
              -1,
              null);
    } else {
      queryId =
          saveQuery(
              dbtype, connna, qtitle, qstrin, qfile, qdelim, queryType, NO, 0, null, -1, null);
    }
    if (queryId > -1) {
      maybeRunNow(queryId);
    }
  }

  /**
   * Reads a query string from the console, repeatedly prompting until the end condition for the
   * query type and delimiter is met, then returns the delimiter-trimmed string.
   *
   * @param queryType the query type governing the end-of-input condition
   * @param qdelim the delimiter marking the end of the query (may be empty)
   * @return the collected query string with trailing delimiters trimmed
   */
  private static final String readQueryStringFromConsole(String queryType, String qdelim) {
    String delimToShow;
    if ("".equals(qdelim)) {
      delimToShow = MESSAGE_ENTER;
    } else {
      delimToShow = qdelim;
    }
    if (ARG_MULTIPLE.equals(queryType)) {
      delimToShow = delimToShow + delimToShow;
    }
    boolean aReq = false;
    String qstrin = "";
    while (true) {
      if (!aReq) {
        aReq = true;
        if (!"".equals(qdelim)) {
          qstrin +=
              readline(
                  MESSAGE_ENTER_QUERY_STRING + delimToShow + MESSAGE_QUERY_STRING_END_NOT_EMPTY,
                  APP_MAX_LENGTH_OF_SQL);
        } else {
          qstrin +=
              readline(
                  MESSAGE_ENTER_QUERY_STRING + delimToShow + MESSAGE_QUERY_STRING_END_EMPTY,
                  APP_MAX_LENGTH_OF_SQL);
        }
      } else {
        qstrin += NEW_LINE_STRING + readline("", APP_MAX_LENGTH_OF_SQL);
      }
      if ((!"".equals(qdelim)
              && (((ARG_SINGLE.equals(queryType) || ARG_BATCH.equals(queryType))
                      && qstrin.trim().endsWith(qdelim))
                  || (ARG_MULTIPLE.equals(queryType)
                      && qstrin.trim().endsWith(qdelim + NEW_LINE_STRING + qdelim))
                  || (ARG_MULTIPLE.equals(queryType) && qstrin.trim().endsWith(qdelim + qdelim))))
          || ("".equals(qdelim)
              && (ARG_SINGLE.equals(queryType)
                  || ARG_BATCH.equals(queryType)
                  || (ARG_MULTIPLE.equals(queryType) && qstrin.endsWith(NEW_LINE_STRING))))) {
        break;
      }
    }
    return trimQueryString(qstrin, qdelim);
  }

  /**
   * Prompts for the batch-specific parameters (execution count and source, either a file or a
   * previous result set plus the source fields) and saves the batch query. Returns -1 if the source
   * is invalid or unusable.
   *
   * @param dbtype the database type
   * @param connna the connection name
   * @param qtitle the query title
   * @param qstrin the SQL string
   * @param qfile the output file name
   * @param qdelim the delimiter
   * @param queryType the query type
   * @return the saved query id, or -1 if the batch source could not be used
   */
  private static final int addBatchQuery(
      String dbtype,
      String connna,
      String qtitle,
      String qstrin,
      String qfile,
      String qdelim,
      String queryType) {
    int qbatchc =
        parseIntSafe(readline(MESSAGE_QUERY_BATCH_EXEC_COUNT, APP_MAX_LENGTH_OF_INPUT), 1);
    if (qbatchc < 1) {
      qbatchc = 1;
    }
    String batchSourceFrom =
        readline(MESSAGE_QUERY_BATCH_SOURCE_FROM, APP_MAX_LENGTH_OF_INPUT).trim().toLowerCase();
    if (batchSourceFrom.equals(BATCH_SOURCE_FROM_FILE_VALUE)) {
      String batchSourceFromFile =
          readline(MESSAGE_QUERY_BATCH_SOURCE_FROM_FILE, APP_MAX_LENGTH_OF_INPUT).trim();
      if (isExistingFile(batchSourceFromFile, true) && isValidFilePath(batchSourceFromFile, true)) {
        String sourceFields =
            readline(
                    MESSAGE_QUERY_BATCH_SOURCE_FIELDS_FIELDS.replace(
                        FIELDS_REPLACE, getSourceHeaderFromFile(batchSourceFromFile)),
                    APP_MAX_LENGTH_OF_INPUT)
                .trim();
        return saveQuery(
            dbtype,
            connna,
            qtitle,
            qstrin,
            qfile,
            qdelim,
            queryType,
            NO,
            qbatchc,
            batchSourceFromFile,
            -1,
            sourceFields);
      }
    } else if (batchSourceFrom.equals(BATCH_SOURCE_FROM_RESULT_SET_VALUE)) {
      int batchSourceFromQueryId =
          parseIntSafe(
              readline(MESSAGE_QUERY_BATCH_SOURCE_FROM_QURERY_ID, APP_MAX_LENGTH_OF_INPUT), -1);
      if (isValidQueryId(batchSourceFromQueryId)) {
        if (isResultSetUsable(getQueryResultSet(batchSourceFromQueryId))) {
          String sourceFields =
              readline(
                      MESSAGE_QUERY_BATCH_SOURCE_FIELDS_FIELDS.replace(
                          FIELDS_REPLACE,
                          getSourceHeaderFromResultSet(batchSourceFromQueryId, qdelim)),
                      APP_MAX_LENGTH_OF_INPUT)
                  .trim();
          return saveQuery(
              dbtype,
              connna,
              qtitle,
              qstrin,
              qfile,
              qdelim,
              queryType,
              NO,
              qbatchc,
              null,
              batchSourceFromQueryId,
              sourceFields);
        } else {
          outprintln(MESSAGE_THIS_RESULT_SET_CANNOT_BE_USED_AS_THE_SOURCE);
        }
      }
    } else {
      outprintln(MESSAGE_WRONG_QUERY_BATCH_SOURCE_FROM);
    }
    return -1;
  }

  /**
   * Prompts the user whether the query should be scrollable, treating y/yes/empty as yes.
   *
   * @return yes if the user chose scrollable, otherwise no
   */
  private static final String promptScrollable() {
    String queryIsScrollable = readline(MESSAGE_QUERY_IS_SCROLLABLE, APP_MAX_LENGTH_OF_INPUT);
    if (Y.equals(queryIsScrollable)
        || YES.equals(queryIsScrollable)
        || "".equals(queryIsScrollable)) {
      return YES;
    }
    return NO;
  }

  /**
   * Prompts whether to run the query now, starting it on yes/y/empty and otherwise printing how to
   * run it later. Throws systemexit if the input is null.
   *
   * @param queryId the query id that may be started
   */
  private static final void maybeRunNow(int queryId) {
    String runnow = readline(MESSAGE_QUERY_RUN_NOW, APP_MAX_LENGTH_OF_INPUT);
    if (runnow == null) {
      throw systemexit("Error - runnow is null, addQuery");
    }
    if (runnow.toLowerCase().equals(YES) || runnow.toLowerCase().equals(Y) || runnow.equals("")) {
      startQuery(queryId);
    } else {
      outprintln(MESSAGE_YOU_CAN_RUN_THIS_QUERY_BY_TYPING + queryId);
    }
  }

  /**
   * Returns the query's elapsed time as a human-readable formatted string.
   *
   * @param queryId the query id
   * @return the formatted elapsed time
   */
  static final String getQueryElapsedFormatted(int queryId) {
    return calculateElapsed(getQueryElapsedMs(queryId));
  }

  /**
   * Computes the query's elapsed time in milliseconds based on its state: zero when not started,
   * now minus the start date while running, and end minus start once finished.
   *
   * @param queryId the query id
   * @return the elapsed time in milliseconds
   */
  static final long getQueryElapsedMs(int queryId) {
    long elapsedMs = 0;
    if (isValidQueryId(queryId)) {
      String queryState = getQueryState(queryId);
      if (QUERY_STATE_NOT_STARTED.equals(queryState)) {
        elapsedMs = 0;
      } else if (QUERY_STATE_RUNNING.equals(queryState)) {
        elapsedMs = new Date().getTime() - getQueryStartDate(queryId).getTime();
      } else if (QUERY_STATE_FINISHED_SUCCESSFULLY.equals(queryState)
          || QUERY_STATE_FINISHED_WITH_ERRORS.equals(queryState)) {
        elapsedMs = getQueryEndDate(queryId).getTime() - getQueryStartDate(queryId).getTime();
      }
      queryState = null;
    }
    return elapsedMs;
  }

  /**
   * Derives the query's state from its start date, end date and error message: not started,
   * running, finished successfully or finished with errors. Returns an empty string for an invalid
   * query id.
   *
   * @param queryId the query id
   * @return the query state constant, or an empty string if the id is invalid
   */
  static final String getQueryState(int queryId) {
    String queryState = "";
    if (isValidQueryId(queryId)) {
      if (getQueryStartDate(queryId) == null) {
        queryState = QUERY_STATE_NOT_STARTED;
      } else {
        if (getQueryEndDate(queryId) == null) {
          queryState = QUERY_STATE_RUNNING;
        } else {
          if (getQueryErrorMessage(queryId) == null) {
            queryState = QUERY_STATE_FINISHED_SUCCESSFULLY;
          } else {
            queryState = QUERY_STATE_FINISHED_WITH_ERRORS;
          }
        }
      }
    }
    return queryState;
  }

  /**
   * Returns the column descriptions of the query's result set, each formatted as the column name
   * padded to a common width followed by its type name. Returns an empty array if there is no
   * result set or metadata access fails.
   *
   * @param queryId the query id
   * @return an array of formatted column name/type strings, empty if unavailable
   */
  static final String[] getQueryResultSetColumns(int queryId) {
    String[] columns = new String[0];
    ResultSet rs = getQueryResultSet(queryId);
    if (rs != null) {
      try {
        ResultSetMetaData rsmd = rs.getMetaData();
        int colscount = rsmd.getColumnCount();
        columns = new String[colscount];
        int maxWidthColname = 0;
        for (int i = 1; i <= colscount; i++) {
          columns[i - 1] = rsmd.getColumnName(i);
          if (maxWidthColname < rsmd.getColumnName(i).length()) {
            maxWidthColname = rsmd.getColumnName(i).length();
          }
        }
        maxWidthColname += 1;
        for (int i = 1; i <= colscount; i++) {
          columns[i - 1] =
              pad(columns[i - 1], maxWidthColname, SPACE_CHAR) + rsmd.getColumnTypeName(i);
        }
        rsmd = null;
        colscount = 0;
        maxWidthColname = 0;
      } catch (Exception e) {
        columns = new String[0];
      }
    }
    rs = null;
    return columns;
  }

  /**
   * Returns the row count of the query's result set by moving to the last row (only when the result
   * set is scrollable) and restoring the cursor. Returns zero if the result set is missing, not
   * scrollable or an error occurs.
   *
   * @param queryId the query id
   * @return the number of rows, or zero if unavailable or not scrollable
   */
  static final int getQueryResultSetRowCount(int queryId) {
    int rowCount = 0;
    ResultSet rs = getQueryResultSet(queryId);
    if (rs != null) {
      try {
        String scrollable = getQueryIsScrollable(queryId);
        if (YES.equals(scrollable)) {
          rs.last();
          rowCount = rs.getRow();
          rs.beforeFirst();
        } else {
          rowCount = 0;
        }
        scrollable = null;
      } catch (Exception e) {
        rowCount = 0;
      }
    } else {
      rowCount = 0;
    }
    rs = null;
    return rowCount;
  }

  /**
   * Checks whether the given query id exists as a key in the query state maps, printing a message
   * if it does not.
   *
   * @param queryId the query id to validate
   * @return true if the query id exists, false otherwise
   */
  /**
   * Parses a user-supplied integer without throwing on malformed input, so that a bad command
   * argument (for example a non-numeric query id) reports the value as invalid instead of
   * terminating the application with a {@link NumberFormatException}.
   *
   * @param s the text to parse (may be null)
   * @param fallback the value to return when {@code s} is null or not a valid int
   * @return the parsed int, or {@code fallback} if parsing fails
   */
  static final int parseIntSafe(String s, int fallback) {
    if (s == null) {
      return fallback;
    }
    try {
      return Integer.parseInt(s.trim());
    } catch (NumberFormatException e) {
      return fallback;
    }
  }

  static final boolean isValidQueryId(int queryId) {
    boolean success = false;
    if (queryDbTypes != null) {
      if (queryDbTypes.containsKey(queryId)) {
        success = true;
      }
    }
    if (!success) {
      outprintln(MESSAGE_QUERY_ID_DOES_NOT_EXIST + queryId);
    }
    return success;
  }

  /**
   * Deletes all non-running queries (optionally after user confirmation), restricted to the current
   * database type when one is set, and prints how many were deleted when confirmation was required.
   * Throws systemexit if queryDbTypes or any of its entries, keys or values is null.
   *
   * @param confirmNeeded whether to prompt for confirmation and print summary messages
   */
  static final void deleteAllQueries(boolean confirmNeeded) {
    int counter = 0;
    boolean toContinue = true;
    if (isFileContentConnectionsOrigReady()) {
      if (!"".equals(dbType) && confirmNeeded) {
        outprint(NEW_LINE_STRING + FOLD + dbType + ":");
      }
      if (confirmNeeded) {
        if (!readYesElseAnything(
            MESSAGE_DO_YOU_WANT_TO_DELETE_ALL_QUERIES, MESSAGE_QUERIES_WONT_BE_DELETED)) {
          toContinue = false;
        }
      }
      if (toContinue) {
        ArrayList<Integer> queryIds = new ArrayList<Integer>();
        if (queryDbTypes != null) {
          for (HashMap.Entry<Integer, String> queryDbType : queryDbTypes.entrySet()) {
            if (queryDbType != null) {
              if (queryDbType.getKey() != null) {
                if (queryDbType.getValue() != null) {
                  if (!QUERY_STATE_RUNNING.equals(getQueryState(queryDbType.getKey()))) {
                    if ("".equals(dbType) || queryDbType.getValue().equals(dbType)) {
                      queryIds.add(queryDbType.getKey());
                    }
                  }
                } else {
                  throw systemexit("Error - queryDbTypeValue is null, deleteAllQueries");
                }
              } else {
                throw systemexit("Error - queryDbTypeKey is null, deleteAllQueries");
              }
            } else {
              throw systemexit("Error - queryDbType is null, deleteAllQueries");
            }
          }
          for (int queryId : queryIds) {
            if (counter == 0) {
              outprintln("");
            }
            counter++;
            deleteQuery(queryId, confirmNeeded);
          }
          if (confirmNeeded) {
            if (counter == 0) {
              outprintln(MESSAGE_NO_NOT_RUNNING_QUERIES_TO_DELETE);
            } else if (counter == 1) {
              outprintln(MESSAGE_ONE_NOT_RUNNING_QUERY_HAS_BEEN_DELETED);
            } else {
              outprintln(MESSAGE_NOT_RUNNING_QUERIES_HAVE_BEEN_DELETED + counter);
            }
          }
        } else {
          throw systemexit("Error - queryDbTypes is null, deleteAllQueries");
        }
        queryIds = null;
      }
    }
    counter = 0;
    toContinue = true;
  }

  /**
   * Builds a delimiter-separated header line from the column names of the given query's result set.
   * Returns an empty string if there is no result set or metadata access fails.
   *
   * @param queryId the query id whose result set columns are read
   * @param delimiter the delimiter placed between column names
   * @return the delimiter-separated header, empty if unavailable
   */
  static final String getSourceHeaderFromResultSet(int queryId, String delimiter) {
    String sourceHeader = "";
    ResultSet sourceResultSet = getQueryResultSet(queryId);
    if (sourceResultSet != null) {
      try {
        ResultSetMetaData resultSetMetaData = sourceResultSet.getMetaData();
        int colscount = resultSetMetaData.getColumnCount();
        for (int i = 1; i <= colscount; i++) {
          sourceHeader = sourceHeader + resultSetMetaData.getColumnName(i);
          if (i < colscount) {
            sourceHeader = sourceHeader + delimiter;
          }
        }
      } catch (SQLException e) {
        sourceHeader = "";
      }
    }
    return sourceHeader;
  }

  /**
   * Returns the header (first line) read from the given file, or an empty string if the file does
   * not exist or its path is invalid.
   *
   * @param fileName the file to read the header from
   * @return the header line, empty if the file is missing or invalid
   */
  static final String getSourceHeaderFromFile(String fileName) {
    String sourceHeader = "";
    if (isExistingFile(fileName, false) && isValidFilePath(fileName, false)) {
      sourceHeader = readFileContent(fileName, true);
    }
    return sourceHeader;
  }

  /**
   * Prints the query error message with its label, placing the message on a new line if it contains
   * a line break and printing nothing extra when it is null.
   *
   * @param queryErrorMessage the error message to print (may be null)
   */
  static final void printErrorMessage(String queryErrorMessage) {
    outprintln(
        MESSAGE_QUERY_DESC_ERROR_MESSAGE
            + (queryErrorMessage == null
                ? ""
                : (queryErrorMessage.contains(NEW_LINE_STRING) ? NEW_LINE_STRING : "")
                    + queryErrorMessage));
  }

  /**
   * Trims whitespace from the query string and repeatedly strips any trailing delimiter (when the
   * delimiter is non-empty), trimming again after each removal.
   *
   * @param q the query string to trim
   * @param d the delimiter to strip from the end
   * @return the trimmed query string with trailing delimiters removed
   */
  static final String trimQueryString(String q, String d) {
    String queryString = q;
    String queryDelimiter = d;
    queryString = queryString.trim();
    while (queryString.endsWith(queryDelimiter) && !"".equals(queryDelimiter)) {
      queryString = queryString.substring(0, queryString.length() - queryDelimiter.length());
      queryString = queryString.trim();
    }
    return queryString;
  }
}
