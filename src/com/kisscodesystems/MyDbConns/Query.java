package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Args.*;
import static com.kisscodesystems.MyDbConns.Connections.*;
import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.FileStore.*;
import static com.kisscodesystems.MyDbConns.QueryStore.*;
import static com.kisscodesystems.MyDbConns.Validate.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Date;

/**
 * Per-query background worker. This {@link Runnable} is started on its own {@link Thread} whose
 * name is the queryId; {@link #run()} reads that id from the thread name and executes the query in
 * single, multiple, or batch mode (batch data sourced from a file or another query's result set).
 */
public final class Query implements Runnable {
  private String errorString = "";

  /**
   * Entry point run on the worker thread. Reads the queryId from the current thread name, resolves
   * the query string (optionally loading it from a file), validates the query type and delimiter
   * and the sql length, then dispatches to {@link #executeQuery} and records any error message.
   */
  public final void run() {
    int queryId = Integer.parseInt(Thread.currentThread().getName());
    if (!isValidQueryId(queryId)) {
      return;
    }
    String queryString = getQueryString(queryId);
    String queryFile = getQueryFile(queryId);
    String queryDelimiter = getQueryDelimiter(queryId);
    String queryType = getQueryType(queryId);
    if (queryFile != null) {
      if (isValidFilePath(queryFile, true) && isExistingFile(queryFile, true)) {
        queryString = readFileContent(queryFile, false).trim();
        queryString = trimQueryString(queryString, queryDelimiter);
        setQueryString(queryId, queryString);
      } else {
        throw systemexit("Error - invalid queryFile, run");
      }
    }
    if (!isQueryTypeAndDelimiterOk(queryType, queryDelimiter)) {
      throw systemexit("Error - batch sql and empty delimiter have been given, run");
    }
    if (queryString == null) {
      throw systemexit("Error - queryString is null, run");
    }
    if (queryString.length() == 0) {
      appendErrorString("Empty sql statement has been found!");
    } else if (queryString.length() > APP_MAX_LENGTH_OF_SQL) {
      appendErrorString("Too long sql statement has been found!");
    } else {
      executeQuery(queryId, queryString, queryType, queryDelimiter);
    }
    if (!isCancelled(queryId)) {
      setQueryErrorMessage(queryId, "".equals(errorString) ? null : errorString.trim());
    }
  }

  /**
   * Opens a JDBC connection for the query's dbtype/connna, stores it, and dispatches execution to
   * the single, multiple, or batch runner based on the query type. Records the end date and result
   * set on completion unless the query was cancelled.
   *
   * @param queryId the id of the query being executed
   * @param queryString the sql statement(s) to run
   * @param queryType the execution mode (single, multiple, or batch)
   * @param queryDelimiter the statement delimiter used for multiple/batch modes
   */
  private final void executeQuery(
      int queryId, String queryString, String queryType, String queryDelimiter) {
    String connna = getQueryConnna(queryId);
    String dbtype = getQueryDbType(queryId);
    String dbuser = getDbuser(dbtype, connna);
    String dbpass = getDbpass(dbtype, connna);
    String driver = getDriver(dbtype, connna);
    String connst = getConnst(dbtype, connna);
    setQueryStartDate(queryId, new Date());
    ResultSet resultSet = null;
    try {
      Class.forName(driver);
      Connection connection = DriverManager.getConnection(connst, dbuser, dbpass);
      setQueryConnection(queryId, connection);
      if (ARG_SINGLE.equals(queryType)) {
        resultSet = runSingle(queryId, connection, queryString);
      } else if (ARG_MULTIPLE.equals(queryType)) {
        runMultiple(queryId, connection, queryString, queryDelimiter);
      } else if (ARG_BATCH.equals(queryType)) {
        runBatch(queryId, connection, queryString, queryDelimiter);
      } else {
        throw systemexit("Error - wrong queryType, run");
      }
    } catch (Exception e) {
      resultSet = null;
      appendErrorString(e.toString());
    } finally {
      if (!isCancelled(queryId)) {
        setQueryEndDate(queryId, new Date());
        setQueryResultSet(queryId, resultSet);
      }
    }
  }

  /**
   * Executes a single SELECT statement, creating a scrollable or plain statement depending on the
   * query's scrollable flag, and returns the resulting result set.
   *
   * @param queryId the id of the query being executed
   * @param connection the open JDBC connection
   * @param queryString the sql statement to run
   * @return the result set produced by executing the query
   * @throws Exception if creating the statement or executing the query fails
   */
  private final ResultSet runSingle(int queryId, Connection connection, String queryString)
      throws Exception {
    Statement statement;
    if (YES.equals(getQueryIsScrollable(queryId))) {
      statement =
          connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    } else {
      statement = connection.createStatement();
    }
    setQueryStatement(queryId, statement);
    setQueryStartDate(queryId, new Date());
    return statement.executeQuery(queryString);
  }

  /**
   * Splits the query string into individual statements by the delimiter (or newline when the
   * delimiter is empty) and executes each one, appending an error string for empty or failing
   * statements.
   *
   * @param queryId the id of the query being executed
   * @param connection the open JDBC connection
   * @param queryString the sql statements to run
   * @param queryDelimiter the delimiter separating the statements
   * @throws Exception if creating the statement fails
   */
  private final void runMultiple(
      int queryId, Connection connection, String queryString, String queryDelimiter)
      throws Exception {
    Statement statement = connection.createStatement();
    setQueryStatement(queryId, statement);
    String[] queries =
        queryString.split("".equals(queryDelimiter) ? NEW_LINE_STRING : queryDelimiter);
    setQueryStartDate(queryId, new Date());
    for (int i = 0; i < queries.length; i++) {
      try {
        if (!queries[i].trim().equals("")) {
          statement.execute(queries[i].trim());
        } else {
          appendErrorString("(" + i + ") " + " query is empty.");
        }
      } catch (Exception e) {
        appendErrorString("(" + i + ") " + e.toString());
      }
    }
  }

  /**
   * Prepares the query as a batch statement and feeds it parameter data drawn from either a source
   * file or another query's result set, resolving the requested source fields to their positions
   * before delegating to {@link #batchFromFile} or {@link #batchFromResultSet}.
   *
   * @param queryId the id of the query being executed
   * @param connection the open JDBC connection
   * @param queryString the parameterized sql statement to run in batches
   * @param queryDelimiter the delimiter separating source field names and file columns
   * @throws Exception if reading the source or preparing the statement fails
   */
  private final void runBatch(
      int queryId, Connection connection, String queryString, String queryDelimiter)
      throws Exception {
    int queryBatchExecCount = getQueryBatchExecCount(queryId);
    String queryBatchSourceFile = getQueryBatchSourceFile(queryId);
    int queryBatchSourceQueryId = getQueryBatchSourceQueryId(queryId);
    String queryBatchSourceField = getQueryBatchSourceField(queryId);
    if (queryBatchSourceFile == null && queryBatchSourceQueryId <= -1) {
      throw systemexit(
          "Error - queryBatchSourceFile is null and queryBatchSourceQueryId is negative, run");
    }
    PreparedStatement preparedStatement = connection.prepareStatement(queryString);
    setQueryStatement(queryId, preparedStatement);
    setQueryStartDate(queryId, new Date());
    String[] sourceHeaderFieldsArr = queryBatchSourceField.trim().split(queryDelimiter);
    int[] sourceHeaderPositionsArr = new int[sourceHeaderFieldsArr.length];
    String[] data = null;
    ResultSet sourceResultSet = null;
    String sourceResultSetIsScrollable = null;
    String[] sourceAllHeaderFieldsArr;
    if (queryBatchSourceFile != null) {
      if (isValidFilePath(queryBatchSourceFile, true)
          && isExistingFile(queryBatchSourceFile, true)) {
        data = readFileContent(queryBatchSourceFile, false).split(NEW_LINE_STRING);
        sourceAllHeaderFieldsArr = data[0].trim().split(queryDelimiter);
      } else {
        throw systemexit("Error - invalid queryBatchSourceFile, run");
      }
    } else {
      sourceResultSet = getQueryResultSet(queryBatchSourceQueryId);
      sourceResultSetIsScrollable = getQueryIsScrollable(queryBatchSourceQueryId);
      if (sourceResultSet == null || sourceResultSetIsScrollable == null) {
        throw systemexit(
            "Error - one of these is null:" + " sourceResultSet|sourceResultSetIsScrollable, run");
      }
      ResultSetMetaData resultSetMetaData = sourceResultSet.getMetaData();
      if (resultSetMetaData == null) {
        throw systemexit("Error - resultSetMetaData is null, run");
      }
      int colscount = resultSetMetaData.getColumnCount();
      sourceAllHeaderFieldsArr = new String[colscount];
      for (int i = 1; i <= colscount; i++) {
        sourceAllHeaderFieldsArr[i - 1] = resultSetMetaData.getColumnName(i);
      }
    }
    boolean fieldsOk = true;
    for (int i = 0; i < sourceHeaderFieldsArr.length; i++) {
      sourceHeaderPositionsArr[i] =
          getFieldPosition(sourceAllHeaderFieldsArr, sourceHeaderFieldsArr[i]);
      if (sourceHeaderPositionsArr[i] == -1) {
        fieldsOk = false;
        appendErrorString("Field not found in source: " + sourceHeaderFieldsArr[i]);
      }
    }
    if (!fieldsOk) {
      return;
    }
    if (queryBatchSourceFile != null && data != null) {
      batchFromFile(
          queryId,
          preparedStatement,
          data,
          sourceHeaderFieldsArr,
          sourceHeaderPositionsArr,
          queryDelimiter,
          queryBatchExecCount);
    } else {
      if (sourceResultSet == null) {
        throw systemexit("Error - sourceResultSet is null, run");
      }
      batchFromResultSet(
          queryId,
          preparedStatement,
          sourceResultSet,
          sourceResultSetIsScrollable,
          sourceHeaderFieldsArr,
          queryBatchExecCount);
    }
  }

  /**
   * Fills the prepared statement from the lines of a source file (skipping the header row), binding
   * the mapped source columns as parameters, adding each row to the batch and executing the batch
   * every queryBatchExecCount rows and once more at the end.
   *
   * @param queryId the id of the query being executed
   * @param preparedStatement the prepared statement to populate and execute in batches
   * @param data the source file lines, the first of which is the header row
   * @param sourceHeaderFieldsArr the source field names to bind, in parameter order
   * @param sourceHeaderPositionsArr the column positions of those fields within each data row
   * @param queryDelimiter the delimiter separating columns in each data row
   * @param queryBatchExecCount how many rows to accumulate before executing the batch
   * @throws Exception if binding or batch execution fails
   */
  private final void batchFromFile(
      int queryId,
      PreparedStatement preparedStatement,
      String[] data,
      String[] sourceHeaderFieldsArr,
      int[] sourceHeaderPositionsArr,
      String queryDelimiter,
      int queryBatchExecCount)
      throws Exception {
    boolean queryIsCancelled = false;
    for (int i = 1; i < data.length; i++) {
      if (queryIsCancelled) {
        break;
      }
      String[] currentData = data[i].split(queryDelimiter);
      for (int j = 0; j < sourceHeaderFieldsArr.length; j++) {
        try {
          preparedStatement.setString(j + 1, currentData[sourceHeaderPositionsArr[j]]);
        } catch (Exception e) {
          appendErrorString(e.toString());
        }
      }
      try {
        preparedStatement.addBatch();
      } catch (Exception e) {
        appendErrorString(e.toString());
      }
      if (i % queryBatchExecCount == 0) {
        queryIsCancelled = executeBatchUnlessCancelled(queryId, preparedStatement);
      }
    }
    executeBatchUnlessCancelled(queryId, preparedStatement);
  }

  /**
   * Fills the prepared statement from the rows of another query's result set, binding the named
   * source columns as parameters, adding each row to the batch and executing the batch every
   * queryBatchExecCount rows and once more at the end.
   *
   * @param queryId the id of the query being executed
   * @param preparedStatement the prepared statement to populate and execute in batches
   * @param sourceResultSet the result set supplying the parameter data
   * @param sourceResultSetIsScrollable flag indicating whether the source result set is scrollable
   * @param sourceHeaderFieldsArr the source column names to bind, in parameter order
   * @param queryBatchExecCount how many rows to accumulate before executing the batch
   * @throws Exception if reading the result set, binding, or batch execution fails
   */
  private final void batchFromResultSet(
      int queryId,
      PreparedStatement preparedStatement,
      ResultSet sourceResultSet,
      String sourceResultSetIsScrollable,
      String[] sourceHeaderFieldsArr,
      int queryBatchExecCount)
      throws Exception {
    boolean queryIsCancelled = false;
    int queryCounter = 1;
    if (YES.equals(sourceResultSetIsScrollable)) {
      sourceResultSet.beforeFirst();
    }
    while (sourceResultSet.next()) {
      if (queryIsCancelled) {
        break;
      }
      int fieldIndex = 1;
      for (String colname : sourceHeaderFieldsArr) {
        try {
          preparedStatement.setObject(fieldIndex, sourceResultSet.getObject(colname));
        } catch (Exception e) {
          appendErrorString(e.toString());
        }
        fieldIndex++;
      }
      try {
        preparedStatement.addBatch();
      } catch (Exception e) {
        appendErrorString(e.toString());
      }
      if (queryCounter % queryBatchExecCount == 0) {
        queryIsCancelled = executeBatchUnlessCancelled(queryId, preparedStatement);
      }
      queryCounter++;
    }
    executeBatchUnlessCancelled(queryId, preparedStatement);
  }

  /**
   * Executes the accumulated batch unless the query has been cancelled, appending an error string
   * on failure.
   *
   * @param queryId the id of the query being executed
   * @param preparedStatement the prepared statement whose batch is executed
   * @return true if the query was cancelled (and the batch was not executed), false otherwise
   */
  private final boolean executeBatchUnlessCancelled(
      int queryId, PreparedStatement preparedStatement) {
    boolean queryIsCancelled = isCancelled(queryId);
    try {
      if (!queryIsCancelled) {
        preparedStatement.executeBatch();
      }
    } catch (Exception e) {
      appendErrorString(e.toString());
    }
    return queryIsCancelled;
  }

  /**
   * Reports whether the query has been cancelled, determined by its start date having been cleared
   * to null.
   *
   * @param queryId the id of the query to check
   * @return true if the query's start date is null (cancelled), false otherwise
   */
  private final boolean isCancelled(int queryId) {
    return null == getQueryStartDate(queryId);
  }

  /**
   * Trims the given string and converts it to lower case, returning an empty string when the input
   * is null.
   *
   * @param string the string to normalize
   * @return the trimmed, lower-cased string, or an empty string if the input is null
   */
  private final String stringTrimAndToLowerCase(String string) {
    if (string != null) {
      return string.trim().toLowerCase();
    } else {
      return "";
    }
  }

  /**
   * Finds the index of the given field within the array of field names, comparing
   * case-insensitively after trimming.
   *
   * @param fields the array of field names to search
   * @param field the field name to locate
   * @return the zero-based index of the matching field, or -1 if not found
   */
  private final int getFieldPosition(String[] fields, String field) {
    int position = -1;
    if (fields != null && field != null) {
      String trimmedLowerCasedField = stringTrimAndToLowerCase(field);
      for (int i = 0; i < fields.length; i++) {
        if (trimmedLowerCasedField.equals(stringTrimAndToLowerCase(fields[i]))) {
          position = i;
          break;
        }
      }
      trimmedLowerCasedField = null;
    }
    return position;
  }

  /**
   * Appends the given error message to the accumulated error string on a new line, keeping the
   * result trimmed.
   *
   * @param error the error message to append
   */
  private final void appendErrorString(String error) {
    errorString = errorString.trim() + NEW_LINE_STRING + error.trim();
    errorString = errorString.trim();
  }

  /**
   * Deserialization guard that always throws to prevent this class from being deserialized.
   *
   * @param in the object input stream (unused)
   * @throws IOException always thrown to block deserialization
   */
  private final void readObject(ObjectInputStream in) throws IOException {
    throw new IOException("");
  }

  /**
   * Serialization guard that always throws to prevent this class from being serialized.
   *
   * @param out the object output stream (unused)
   * @throws IOException always thrown to block serialization
   */
  private final void writeObject(ObjectOutputStream out) throws IOException {
    throw new IOException("");
  }
}
