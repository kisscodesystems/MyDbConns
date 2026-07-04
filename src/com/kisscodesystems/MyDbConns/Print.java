package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Args.*;
import static com.kisscodesystems.MyDbConns.Connections.*;
import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.FileStore.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.QueryStore.*;
import static com.kisscodesystems.MyDbConns.State.*;
import static com.kisscodesystems.MyDbConns.Utils.*;
import static com.kisscodesystems.MyDbConns.Validate.*;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * Rendering helpers for the CLI: lists connections and queries (by dbtype, all/active/inactive),
 * counts connections, builds and echoes query results (constructing text/csv/htm output, writing
 * large values to disk separately), and provides formatting utilities.
 */
final class Print {
  /**
   * Prints the connections of a single dbtype filtered by list type (all, active, or inactive),
   * including each connection's query ids where applicable, sorted, with a count of hits.
   *
   * @param dbtype the dbtype whose connections are listed
   * @param typeToList which connections to show: all, active, or inactive
   */
  static final void listConnectionsByDbtype(String dbtype, String typeToList) {
    if (typeToList != null && dbtype != null) {
      if (isFileContentConnectionsOrigReady()) {
        int countOfListableConnections = 0;
        String stringToDisplay = null;
        HashMap<String, ArrayList<Integer>> connections = new HashMap<String, ArrayList<Integer>>();
        ArrayList<String> sortedList = new ArrayList<String>();
        String listElementToSortedList = null;
        if (typeToList.equals(TYPE_TO_LIST_ALL)) {
          stringToDisplay = MESSAGE_ALL_CONNECTIONS;
        } else if (typeToList.equals(TYPE_TO_LIST_ACTIVE)) {
          stringToDisplay = MESSAGE_ACTIVE_CONNECTIONS;
        } else if (typeToList.equals(TYPE_TO_LIST_INACTIVE)) {
          stringToDisplay = MESSAGE_INACTIVE_CONNECTIONS;
        }
        if (stringToDisplay != null) {
          connections = getConnectionsByDbtype(dbtype, true);
          if (connections != null) {
            outprint(
                NEW_LINE_STRING
                    + FOLD
                    + pad(
                        dbtype + stringToDisplay,
                        APP_MAX_QUERY_ID_TITLE_CONNNA_WIDTH + (FOLD2.length() - FOLD.length()),
                        SPACE_CHAR));
            if (!typeToList.equals(TYPE_TO_LIST_INACTIVE)) {
              outprint(MESSAGE_QUERIES);
            }
            for (HashMap.Entry<String, ArrayList<Integer>> connection : connections.entrySet()) {
              if (connection != null) {
                if (connection.getKey() != null) {
                  if (connection.getValue() != null) {
                    if ((typeToList.equals(TYPE_TO_LIST_ALL))
                        || (typeToList.equals(TYPE_TO_LIST_ACTIVE)
                            && connection.getValue().size() > 0)
                        || (typeToList.equals(TYPE_TO_LIST_INACTIVE)
                            && connection.getValue().size() == 0)) {
                      listElementToSortedList =
                          NEW_LINE_STRING
                              + FOLD2
                              + pad(
                                  connection.getKey(),
                                  APP_MAX_QUERY_ID_TITLE_CONNNA_WIDTH,
                                  SPACE_CHAR);
                      if (!typeToList.equals(TYPE_TO_LIST_INACTIVE)) {
                        listElementToSortedList =
                            listElementToSortedList
                                + SEP9
                                + joinArrayListInteger(connection.getValue(), SEP1);
                      }
                      sortedList.add(listElementToSortedList);
                      countOfListableConnections++;
                    }
                  } else {
                    throw systemexit(
                        "Error - connectionValue is null ( 2 ), listConnectionsByDbtype");
                  }
                } else {
                  throw systemexit("Error - connectionKey is null ( 2 ), listConnectionsByDbtype");
                }
              } else {
                throw systemexit("Error - connection is null ( 2 ), listConnectionsByDbtype");
              }
            }
            Collections.sort(sortedList);
            for (String listElement : sortedList) {
              outprint(listElement);
            }
            outprintln(NEW_LINE_STRING + FOLD + countOfListableConnections + MESSAGE_HITS);
          } else {
            throw systemexit("Error - connections is null, listConnectionsByDbtype");
          }
        } else {
          throw systemexit("Error - stringToDisplay is null, listConnectionsByDbtype");
        }
        countOfListableConnections = 0;
        stringToDisplay = null;
        connections = null;
        sortedList = null;
        listElementToSortedList = null;
      }
    } else {
      throw systemexit(
          "Error - one of these is null: dbtype|us|typeToList, listConnectionsByDbtype");
    }
  }

  /**
   * Counts all configured connections across every supported dbtype (Mysql, Oracle, Mssql, Db2,
   * Postgresql).
   *
   * @return the total number of connections across all dbtypes
   */
  static final int getNumOfAllConnections() {
    return getConnectionsByDbtype(DB_TYPE_MYSQL, false).size()
        + getConnectionsByDbtype(DB_TYPE_ORACLE, false).size()
        + getConnectionsByDbtype(DB_TYPE_MSSQL, false).size()
        + getConnectionsByDbtype(DB_TYPE_DB2, false).size()
        + getConnectionsByDbtype(DB_TYPE_POSTGRESQL, false).size();
  }

  /**
   * Parses the loaded connections file content to build a map of connection names to their query
   * ids for the given dbtype, optionally resolving the query ids per connection.
   *
   * @param dbtype the dbtype whose connections are collected
   * @param queryIdsNeeded whether to populate each connection's query id list (null otherwise)
   * @return a map from connection name to its list of query ids (or null values when not needed)
   */
  static final HashMap<String, ArrayList<Integer>> getConnectionsByDbtype(
      String dbtype, boolean queryIdsNeeded) {
    HashMap<String, ArrayList<Integer>> connections = new HashMap<String, ArrayList<Integer>>();
    int newLines = 0;
    boolean innerBreak = false;
    String connna = null;
    ArrayList<Integer> queryIds = null;
    String toSearch = NEW_LINE_STRING + dbtype + NEW_LINE_CHAR;
    if (isFileContentConnectionsOrigReady()) {
      for (int i =
              getHeaderLength(fileContentConnectionsOrig)
                  + APP_DATE_FORMAT_FOR_DISPLAYING.length()
                  + SEP9.length()
                  + MESSAGE_LOG_APPLICATION_INSTANCE_INITIALIZE.length()
                  + 1
                  - 1;
          i < fileContentConnectionsOrig.length - toSearch.length();
          i++) {
        if (fileContentConnectionsOrig[i] == NEW_LINE_CHAR) {
          newLines++;
        } else if (fileContentConnectionsOrig[i] == ZERO_CHAR) {
          break;
        }
        innerBreak = false;
        for (int j = 0; j < toSearch.length(); j++) {
          if (fileContentConnectionsOrig[i + j] != toSearch.charAt(j)) {
            innerBreak = true;
            break;
          }
        }
        if (!innerBreak && newLines % 6 == 1) {
          connna = "";
          for (int j = i + toSearch.length(); j < APP_MAX_LENGTH_OF_INPUT; j++) {
            if (fileContentConnectionsOrig[j] == NEW_LINE_CHAR) {
              break;
            }
            connna += fileContentConnectionsOrig[j];
          }
          if (queryIdsNeeded) {
            queryIds = getQueriesByConnection(dbtype, connna);
          } else {
            queryIds = null;
          }
          connections.put(connna, queryIds);
        }
      }
    }
    newLines = 0;
    innerBreak = false;
    connna = null;
    queryIds = null;
    toSearch = null;
    return connections;
  }

  /**
   * Collects the ids of the queries that belong to the given dbtype and connection name, returned
   * in sorted order.
   *
   * @param dbtype the dbtype to match
   * @param connna the connection name to match
   * @return the sorted list of query ids for that dbtype and connection
   */
  static final ArrayList<Integer> getQueriesByConnection(String dbtype, String connna) {
    ArrayList<Integer> queryIds = new ArrayList<Integer>();
    String cn = null;
    if (queryDbTypes != null && queryConnnas != null) {
      for (HashMap.Entry<Integer, String> queryDbType : queryDbTypes.entrySet()) {
        if (queryDbType != null) {
          if (queryDbType.getKey() != null) {
            if (queryDbType.getValue() != null) {
              if (queryDbType.getValue().equals(dbtype)) {
                cn = queryConnnas.get(queryDbType.getKey());
                if (cn != null) {
                  if (cn.equals(connna)) {
                    queryIds.add(queryDbType.getKey());
                  }
                } else {
                  throw systemexit("Error - cn is null, getQueriesByConnection");
                }
              }
            } else {
              throw systemexit("Error - queryDbTypeValue is null, getQueriesByConnection");
            }
          } else {
            throw systemexit("Error - queryDbTypeKey is null, getQueriesByConnection");
          }
        } else {
          throw systemexit("Error - queryDbType is null, getQueriesByConnection");
        }
      }
    } else {
      throw systemexit(
          "Error - one of these is null: queryDbTypes|queryConnnas, getQueriesByConnection");
    }
    cn = null;
    Collections.sort(queryIds);
    return queryIds;
  }

  /**
   * Lists connections for the current dbType (or every supported dbtype when dbType is empty),
   * filtered by the given list type.
   *
   * @param typeToList which connections to show: all, active, or inactive
   */
  static final void listConnections(String typeToList) {
    if (isFileContentConnectionsOrigReady()) {
      if (dbType != null) {
        if ("".equals(dbType)) {
          listConnectionsByDbtype(DB_TYPE_MYSQL, typeToList);
          listConnectionsByDbtype(DB_TYPE_ORACLE, typeToList);
          listConnectionsByDbtype(DB_TYPE_MSSQL, typeToList);
          listConnectionsByDbtype(DB_TYPE_DB2, typeToList);
          listConnectionsByDbtype(DB_TYPE_POSTGRESQL, typeToList);
        } else {
          listConnectionsByDbtype(dbType, typeToList);
        }
      } else {
        throw systemexit("Error . dbType is null, listConnections");
      }
    }
  }

  /**
   * Builds a space-prefixed string of the ids of all queries currently in the running state.
   *
   * @return a string listing the ids of running queries, empty if none are running
   */
  static final String getActiveQueries() {
    String activeQueries = "";
    if (isFileContentConnectionsOrigReady()) {
      if (queryDbTypes != null) {
        for (HashMap.Entry<Integer, String> queryDbType : queryDbTypes.entrySet()) {
          if (queryDbType != null) {
            if (queryDbType.getKey() != null) {
              if (queryDbType.getValue() != null) {
                if (QUERY_STATE_RUNNING.equals(getQueryState(queryDbType.getKey()))) {
                  activeQueries = activeQueries + SPACE_CHAR + String.valueOf(queryDbType.getKey());
                }
              } else {
                throw systemexit("Error - queryDbTypeValue is null, getActiveQueries");
              }
            } else {
              throw systemexit("Error - queryDbTypeKey is null, getActiveQueries");
            }
          } else {
            throw systemexit("Error - queryDbType is null, getActiveQueries");
          }
        }
      } else {
        throw systemexit("Error - queryDbTypes is null, getActiveQueries");
      }
    }
    return activeQueries;
  }

  /**
   * Builds a map of query id to its state for all queries of the given dbtype; running queries map
   * to a running-elapsed message instead of the plain state.
   *
   * @param dbtype the dbtype whose queries are collected
   * @return a map from query id (as string) to its state or running-elapsed description
   */
  static final HashMap<String, String> getQueriesByDbtype(String dbtype) {
    HashMap<String, String> queries = new HashMap<String, String>();
    String queryState = null;
    if (isFileContentConnectionsOrigReady()) {
      if (queryDbTypes != null) {
        for (HashMap.Entry<Integer, String> queryDbType : queryDbTypes.entrySet()) {
          if (queryDbType != null) {
            if (queryDbType.getKey() != null) {
              if (queryDbType.getValue() != null) {
                if (queryDbType.getValue().equals(dbtype)) {
                  queryState = getQueryState(queryDbType.getKey());
                  if (QUERY_STATE_RUNNING.equals(queryState)) {
                    queries.put(
                        String.valueOf(queryDbType.getKey()),
                        MESSAGE_RUNNING_ELAPSED + getQueryElapsedFormatted(queryDbType.getKey()));
                  } else {
                    queries.put(String.valueOf(queryDbType.getKey()), queryState);
                  }
                }
              } else {
                throw systemexit("Error - queryDbTypeValue is null, getQueriesByDbtype");
              }
            } else {
              throw systemexit("Error - queryDbTypeKey is null, getQueriesByDbtype");
            }
          } else {
            throw systemexit("Error - queryDbType is null, getQueriesByDbtype");
          }
        }
      } else {
        throw systemexit("Error - queryDbTypes is null, getQueriesByDbtype");
      }
    }
    queryState = null;
    return queries;
  }

  /**
   * Lists queries for the current dbType (or every supported dbtype when dbType is empty), filtered
   * by the given list type.
   *
   * @param typeToList which queries to show: all, active, or inactive
   */
  static final void listQueries(String typeToList) {
    if (isFileContentConnectionsOrigReady()) {
      if (dbType != null) {
        if ("".equals(dbType)) {
          listQueriesByDbtype(DB_TYPE_MYSQL, typeToList);
          listQueriesByDbtype(DB_TYPE_ORACLE, typeToList);
          listQueriesByDbtype(DB_TYPE_MSSQL, typeToList);
          listQueriesByDbtype(DB_TYPE_DB2, typeToList);
          listQueriesByDbtype(DB_TYPE_POSTGRESQL, typeToList);
        } else {
          listQueriesByDbtype(dbType, typeToList);
        }
      } else {
        throw systemexit("Error . dbType is null, listQueries");
      }
    }
  }

  /**
   * Prints the queries of a single dbtype filtered by list type (all, active, or inactive), showing
   * each query's id, title, and state or elapsed time, sorted, with a count of hits.
   *
   * @param dbtype the dbtype whose queries are listed
   * @param typeToList which queries to show: all, active, or inactive
   */
  static final void listQueriesByDbtype(String dbtype, String typeToList) {
    if (typeToList != null && dbtype != null) {
      if (isFileContentConnectionsOrigReady()) {
        int countOfListableQueries = 0;
        String stringToDisplay = null;
        HashMap<String, String> queries = new HashMap<String, String>();
        ArrayList<String> sortedList = new ArrayList<String>();
        if (typeToList.equals(TYPE_TO_LIST_ALL)) {
          stringToDisplay = MESSAGE_ALL_QUERIES;
        } else if (typeToList.equals(TYPE_TO_LIST_ACTIVE)) {
          stringToDisplay = MESSAGE_ACTIVE_QUERIES;
        } else if (typeToList.equals(TYPE_TO_LIST_INACTIVE)) {
          stringToDisplay = MESSAGE_INACTIVE_QUERIES;
        }
        if (stringToDisplay != null) {
          outprint(
              NEW_LINE_STRING
                  + FOLD
                  + pad(
                      dbtype + stringToDisplay,
                      APP_MAX_QUERY_ID_TITLE_CONNNA_WIDTH + (FOLD2.length() - FOLD.length()),
                      SPACE_CHAR));
          if (typeToList.equals(TYPE_TO_LIST_ACTIVE)) {
            outprint(SEP9 + MESSAGE_QUERY_ELAPSEDS);
          } else {
            outprint(SEP9 + QUERY_STATES);
          }
          queries = getQueriesByDbtype(dbtype);
          if (queries != null) {
            for (HashMap.Entry<String, String> query : queries.entrySet()) {
              if (query != null) {
                if (query.getKey() != null) {
                  if (query.getValue() != null) {
                    if ((typeToList.equals(TYPE_TO_LIST_ALL))
                        || (typeToList.equals(TYPE_TO_LIST_ACTIVE)
                            && query.getValue().startsWith(MESSAGE_RUNNING_ELAPSED))
                        || (typeToList.equals(TYPE_TO_LIST_INACTIVE)
                            && (query.getValue().equals(QUERY_STATE_NOT_STARTED)
                                || query.getValue().equals(QUERY_STATE_FINISHED_SUCCESSFULLY)
                                || query.getValue().equals(QUERY_STATE_FINISHED_WITH_ERRORS)))) {
                      countOfListableQueries++;
                      sortedList.add(
                          NEW_LINE_STRING
                              + FOLD2
                              + pad(
                                  query.getKey()
                                      + " ("
                                      + getQueryTitle(Integer.parseInt(query.getKey()))
                                      + ")",
                                  APP_MAX_QUERY_ID_TITLE_CONNNA_WIDTH,
                                  SPACE_CHAR)
                              + SEP9
                              + query.getValue());
                    }
                  } else {
                    throw systemexit("Error - queryValue is null, listQueriesByDbtype");
                  }
                } else {
                  throw systemexit("Error - queryKey is null, listQueriesByDbtype");
                }
              }
            }
            Collections.sort(sortedList);
            for (String listElement : sortedList) {
              outprint(listElement);
            }
            outprintln(NEW_LINE_STRING + FOLD + countOfListableQueries + MESSAGE_HITS);
          } else {
            throw systemexit("Error - queries is null, listQueriesByDbtype");
          }
        } else {
          throw systemexit("Error - stringToDisplay is null, listQueriesByDbtype");
        }
        countOfListableQueries = 0;
        stringToDisplay = null;
        queries = null;
        sortedList = null;
      }
    } else {
      throw systemexit("Error - one of these is null: dbtype|us|typeToList, listQueriesByDbtype");
    }
  }

  /**
   * Waits a short fixed interval for a just-started query to finish, then echoes its result to the
   * console if it has ended (respecting scrollability for single queries), or reports that it is
   * still running.
   *
   * @param queryId the id of the query whose result to display
   */
  static final void displayResultImmediately(int queryId) {
    if (isValidQueryId(queryId)) {
      try {
        Thread.sleep(APP_MAX_NUM_OF_MILLISECONDS_TO_WAIT_FOR_THE_RESULT);
      } catch (InterruptedException e) {
        throw systemexit("Exception - InterruptedException, displayResultImmediately");
      }
      if (getQueryEndDate(queryId) != null) {
        if (ARG_SINGLE.equals(getQueryType(queryId))) {
          if (YES.equals(getQueryIsScrollable(queryId))) {
            echoResult(queryId, RESULT_TARGET_CONS_VALUE, RESULT_FORMAT_TXT_VALUE, true, false);
          } else {
            outprintln(MESSAGE_YOUR_QUERY_RESULT_SET_IS_NOT_SCROLLABLE + queryId);
          }
        } else {
          echoResult(queryId, RESULT_TARGET_CONS_VALUE, RESULT_FORMAT_TXT_VALUE, true, false);
        }
      } else {
        outprintln(MESSAGE_YOUR_QUERY_IS_RUNNING);
      }
    }
  }

  /**
   * Renders a finished query's result to the requested targets: prints running/not-run/error status
   * as appropriate, or builds the result in the requested formats (txt/csv/htm) writing file
   * targets to disk and printing console targets, reporting empty or non-displayable result sets.
   *
   * @param queryId the id of the query whose result is echoed
   * @param resultTargets the output targets to produce (console and/or file)
   * @param resultFormats the file formats to produce (txt, csv, htm)
   * @param headerToInclude whether to include the column header in the output
   * @param queryToInclude whether to include the query string in the output
   */
  static final void echoResult(
      int queryId,
      String resultTargets,
      String resultFormats,
      boolean headerToInclude,
      boolean queryToInclude) {
    if (isValidQueryId(queryId)) {
      String queryState = getQueryState(queryId);
      String queryIsScrollable = getQueryIsScrollable(queryId);
      String queryDbType = getQueryDbType(queryId);
      if (queryState != null && queryIsScrollable != null && queryDbType != null) {
        if (queryState.equals(QUERY_STATE_RUNNING)) {
          outprintln(MESSAGE_YOUR_QUERY_IS_RUNNING);
        } else if (queryState.equals(QUERY_STATE_NOT_STARTED)) {
          outprintln(MESSAGE_YOUR_QUERY_HAS_TO_BE_RUN);
        } else if (queryState.equals(QUERY_STATE_FINISHED_WITH_ERRORS)) {
          outprint(NEW_LINE_STRING + FOLD2);
          printErrorMessage(getQueryErrorMessage(queryId));
        } else {
          ResultSet resultSet = getQueryResultSet(queryId);
          String elapsedFormatted = getQueryElapsedFormatted(queryId);
          String queryDelimiter = getQueryDelimiter(queryId);
          if (resultSet != null) {
            String resultTxt = null;
            String resultCsv = null;
            String resultHtm = null;
            String queryString = "";
            boolean emptyResult = false;
            Date dateForFilenames = new Date();
            if (queryToInclude) {
              queryString = getQueryString(queryId);
            }
            if (resultTargets.contains(RESULT_TARGET_CONS_VALUE)) {
              resultTxt =
                  constructResult(
                      queryIsScrollable,
                      resultSet,
                      headerToInclude,
                      queryString,
                      RESULT_FORMAT_TXT_VALUE,
                      elapsedFormatted,
                      null,
                      queryDbType,
                      queryDelimiter);
              if (!"".equals(resultTxt)) {
                outprint(resultTxt);
              } else {
                emptyResult = true;
                outprintln(MESSAGE_SORRY_BUT_THIS_RESULT_SET_CANNOT_BE_DISPAYED + queryId);
              }
            }
            if (resultTargets.contains(RESULT_TARGET_FILE_VALUE)) {
              if (!emptyResult) {
                outprintln("");
              }
              if (resultFormats.contains(RESULT_FORMAT_TXT_VALUE) && !emptyResult) {
                outprint(MESSAGE_PRODUCING_TXT_RESULT);
                if (resultTxt == null) {
                  resultTxt =
                      constructResult(
                          queryIsScrollable,
                          resultSet,
                          headerToInclude,
                          queryString,
                          RESULT_FORMAT_TXT_VALUE,
                          elapsedFormatted,
                          null,
                          queryDbType,
                          queryDelimiter);
                }
                if (!"".equals(resultTxt)) {
                  String filename =
                      FILE_NAME_RESULT
                          + SIMPLE_DATE_FORMAT_FOR_FILENAMES.format(dateForFilenames)
                          + FILE_POSTFIX_TXT;
                  writeFileContent(resultTxt, filename);
                  outprintln(MESSAGE_DONE);
                  outprintln(FILE_FILE_PREFIX + new File(filename).getAbsolutePath());
                } else {
                  emptyResult = true;
                  outprintln(MESSAGE_SORRY_BUT_THIS_RESULT_SET_CANNOT_BE_DISPAYED + queryId);
                }
              }
              if (resultFormats.contains(RESULT_FORMAT_CSV_VALUE) && !emptyResult) {
                outprint(MESSAGE_PRODUCING_CSV_RESULT);
                // Use the current delimiter for the query's database type (which the user may have
                // changed since the query was added) as the csv field separator, not the delimiter
                // that was frozen into the query at creation time.
                String csvDelimiter = getDelimiter(queryDbType);
                if (!"".equals(csvDelimiter)) {
                  resultCsv =
                      constructResult(
                          queryIsScrollable,
                          resultSet,
                          headerToInclude,
                          queryString,
                          RESULT_FORMAT_CSV_VALUE,
                          elapsedFormatted,
                          null,
                          queryDbType,
                          csvDelimiter);
                  if (!"".equals(resultCsv)) {
                    String filename =
                        FILE_NAME_RESULT
                            + SIMPLE_DATE_FORMAT_FOR_FILENAMES.format(dateForFilenames)
                            + FILE_POSTFIX_CSV;
                    writeFileContent(resultCsv, filename);
                    outprintln(MESSAGE_DONE);
                    outprintln(FILE_FILE_PREFIX + new File(filename).getAbsolutePath());
                  } else {
                    emptyResult = true;
                    outprintln(MESSAGE_SORRY_BUT_THIS_RESULT_SET_CANNOT_BE_DISPAYED + queryId);
                  }
                } else {
                  outprintln(MESSAGE_DELIMITER_IS_EMPTY);
                }
              }
              if (resultFormats.contains(RESULT_FORMAT_HTM_VALUE) && !emptyResult) {
                outprint(MESSAGE_PRODUCING_HTM_RESULT);
                resultHtm =
                    constructResult(
                        queryIsScrollable,
                        resultSet,
                        headerToInclude,
                        queryString,
                        RESULT_FORMAT_HTM_VALUE,
                        elapsedFormatted,
                        FILE_NAME_RESULT
                            + SIMPLE_DATE_FORMAT_FOR_FILENAMES.format(dateForFilenames)
                            + SEP,
                        queryDbType,
                        queryDelimiter);
                if (!"".equals(resultHtm)) {
                  String filename =
                      FILE_NAME_RESULT
                          + SIMPLE_DATE_FORMAT_FOR_FILENAMES.format(dateForFilenames)
                          + FILE_POSTFIX_HTM;
                  writeFileContent(resultHtm, filename);
                  outprintln(MESSAGE_DONE);
                  outprintln(FILE_FILE_PREFIX + new File(filename).getAbsolutePath());
                } else {
                  emptyResult = true;
                  outprintln(MESSAGE_SORRY_BUT_THIS_RESULT_SET_CANNOT_BE_DISPAYED + queryId);
                }
              }
            }
            resultTxt = null;
            resultCsv = null;
            resultHtm = null;
            queryString = "";
            emptyResult = false;
            dateForFilenames = null;
          } else {
            outprintln(MESSAGE_YOUR_QUERY_HAS_BEEN_EXECUTED_SUCCESSFULLY);
            outprintln(FOLD + elapsedFormatted);
          }
          resultSet = null;
          elapsedFormatted = null;
          queryDelimiter = null;
        }
      } else {
        throw systemexit(
            "Error - One of these is null: queryState|queryIsScrollable|queryDbType, echoResult");
      }
      queryState = null;
      queryIsScrollable = null;
    }
  }

  /**
   * Builds the formatted string representation of a result set in the requested format (txt, csv,
   * or htm), computing column widths for aligned text output, formatting Oracle timestamps, writing
   * large or binary values to disk separately for htm output, and appending the row count and
   * elapsed time. Returns an empty string on error or unusable result set.
   *
   * @param scr the scrollable flag of the result set (compared against yes)
   * @param rs the result set to render
   * @param headerToInclude whether to include the column header row
   * @param queryString the query text to prepend, or empty to omit
   * @param resultFormatValue the output format (txt, csv, or htm)
   * @param elapsedFormatted the formatted elapsed time appended to the output
   * @param htmFolderName the folder for separately stored htm values, required for htm output
   * @param dbtype the dbtype (used for type-specific value handling)
   * @param queryDelimiter the field separator used for csv output
   * @return the formatted result string, or an empty string if it cannot be produced
   */
  static final String constructResult(
      String scr,
      ResultSet rs,
      boolean headerToInclude,
      String queryString,
      String resultFormatValue,
      String elapsedFormatted,
      String htmFolderName,
      String dbtype,
      String queryDelimiter) {
    String resultString = "";
    String fieldsep = null;
    boolean scrollable = YES.equals(scr);
    File htmFolder = null;
    if (isResultSetUsable(rs)) {
      if (resultFormatValue.equals(RESULT_FORMAT_TXT_VALUE)) {
        fieldsep = FIELDSEP_TXT;
      } else if (resultFormatValue.equals(RESULT_FORMAT_CSV_VALUE)) {
        fieldsep = queryDelimiter;
      } else if (resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
        fieldsep = FIELDSEP_HTM;
        if (htmFolderName != null) {
          htmFolder = new File(htmFolderName);
          htmFolder.mkdirs();
          if (!(htmFolder.exists() && htmFolder.isDirectory())) {
            throw systemexit("Error - htmFolder is not existing or not a folder, constructResult");
          }
        } else {
          throw systemexit("Error - htmFolderName is null (htm result), constructResult");
        }
      }
      if (fieldsep != null) {
        ResultSetMetaData rsmd = null;
        int colscount = 0;
        ArrayList<String> cols = new ArrayList<String>();
        int[] colw = new int[0];
        String[] colt = new String[0];
        boolean onTheDiskSeparately = false;
        try {
          rsmd = rs.getMetaData();
          if (rsmd != null) {
            try {
              colscount = rsmd.getColumnCount();
              colw = new int[colscount];
              colt = new String[colscount];
              resultString += NEW_LINE_CHAR;
              for (int i = 1; i <= colscount; i++) {
                cols.add(rsmd.getColumnName(i));
                colw[i - 1] = rsmd.getColumnName(i).length();
                colt[i - 1] = rsmd.getColumnTypeName(i).toLowerCase().trim();
              }
            } catch (SQLException e) {
              throw systemexit("Exception - SQLException (2), constructResult");
            }
            if (!"".equals(queryString)) {
              if (resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
                resultString +=
                    "<p><b><i>"
                        + queryString.replaceAll(NEW_LINE_STRING, "<br/>\n")
                        + "</i></b></p>"
                        + NEW_LINE_CHAR
                        + NEW_LINE_CHAR;
              } else {
                resultString += queryString + NEW_LINE_CHAR + NEW_LINE_CHAR;
              }
            }
            int j = 0;
            int lines = 0;
            Object val = null;
            String valStr = null;
            String line = null;
            String plusMinusLine = null;
            try {
              if (scrollable) {
                rs.beforeFirst();
                while (rs.next()) {
                  lines++;
                  j = 1;
                  for (String colname : cols) {
                    val = getVal(rs, colname);
                    valStr = getValStr(val, NULL_STR);
                    if (colw[j - 1] < valStr.length()
                        && (valStr.length() <= APP_MAX_COL_LENGTH_TXT
                            || !resultFormatValue.equals(RESULT_FORMAT_TXT_VALUE))) {
                      colw[j - 1] = valStr.length();
                    }
                    j++;
                  }
                }
              }
              if (resultFormatValue.equals(RESULT_FORMAT_TXT_VALUE)) {
                line = fieldsep;
              } else if (resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
                line = "<tr><th>";
              } else {
                line = "";
              }
              for (int i = 1; i <= colscount; i++) {
                line =
                    line
                        + ((resultFormatValue.equals(RESULT_FORMAT_TXT_VALUE) && scrollable)
                            ? pad(rsmd.getColumnName(i).toLowerCase(), colw[i - 1], SPACE_CHAR)
                            : rsmd.getColumnName(i).toLowerCase());
                if (resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
                  line += fieldsep.replaceAll("td", "th");
                } else {
                  line += fieldsep;
                }
              }
              if (resultFormatValue.equals(RESULT_FORMAT_CSV_VALUE)
                  || resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
                line = line.substring(0, line.length() - fieldsep.length());
              }
              if (resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
                line += "</th></tr>";
              }
              if (resultFormatValue.equals(RESULT_FORMAT_TXT_VALUE) && scrollable) {
                plusMinusLine = getPlusMinusLine(colw, fieldsep);
              } else {
                plusMinusLine = "";
              }
            } catch (SQLException e) {
              throw systemexit("Exception - SQLException (3), constructResult");
            }
            if (resultFormatValue.equals(RESULT_FORMAT_TXT_VALUE)) {
              resultString += plusMinusLine + NEW_LINE_CHAR;
            } else if (resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
              resultString +=
                  "<table width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"1\">"
                      + NEW_LINE_CHAR;
            }
            if (headerToInclude) {
              resultString += line + NEW_LINE_CHAR;
              if (resultFormatValue.equals(RESULT_FORMAT_TXT_VALUE)) {
                resultString += plusMinusLine + NEW_LINE_CHAR;
              }
            }
            if (lines > 0 || !scrollable) {
              try {
                if (scrollable) {
                  rs.beforeFirst();
                }
                lines = 0;
                while (rs.next()) {
                  lines++;
                  if (resultFormatValue.equals(RESULT_FORMAT_TXT_VALUE)) {
                    line = fieldsep;
                  } else {
                    line = "";
                  }
                  j = 1;
                  for (String colname : cols) {
                    if (dbType.equals(DB_TYPE_ORACLE)
                        && (colt[j - 1].equals(TIMESTAMPLTZ)
                            || colt[j - 1].equals(TIMESTAMPTZ)
                            || colt[j - 1].equals(TIMESTAMP))) {
                      val = null;
                      valStr = SIMPLE_DATE_FORMAT_FOR_TIMESTAMPS.format(rs.getTimestamp(colname));
                    } else {
                      val = getVal(rs, colname);
                      valStr = getValStr(val, NULL_STR);
                    }
                    if (resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
                      onTheDiskSeparately =
                          valToTheDiskSeparately(
                              rs, htmFolderName + valStr, colname, colt[j - 1], dbtype);
                      if (onTheDiskSeparately) {
                        valStr =
                            "<a href=\""
                                + htmFolderName
                                + valStr
                                + "\" target=\"_blank\">"
                                + valStr
                                + "</a>";
                      } else if (valStr.length() > APP_MAX_COL_LENGTH_TXT) {
                        writeFileContent(valStr, htmFolderName + colname + lines);
                        valStr =
                            "<a href=\""
                                + htmFolderName
                                + colname
                                + lines
                                + "\" target=\"_blank\">"
                                + colname
                                + lines
                                + "</a>";
                      }
                    }
                    line =
                        line
                            + ((resultFormatValue.equals(RESULT_FORMAT_TXT_VALUE) && scrollable)
                                ? pad(valStr.replaceAll("\n", "\\n"), colw[j - 1], SPACE_CHAR)
                                : valStr)
                            + fieldsep;
                    j++;
                  }
                  if (resultFormatValue.equals(RESULT_FORMAT_CSV_VALUE)
                      || resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
                    line = line.substring(0, line.length() - fieldsep.length());
                  }
                  if (resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
                    resultString += "<tr><td>";
                  }
                  resultString += line;
                  if (resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
                    resultString += "</td></tr>";
                  }
                  resultString += NEW_LINE_CHAR;
                }
                if (resultFormatValue.equals(RESULT_FORMAT_TXT_VALUE)) {
                  resultString += plusMinusLine + NEW_LINE_CHAR;
                }
              } catch (SQLException e) {
                throw systemexit("Exception - SQLException (4), constructResult");
              }
            }
            if (resultFormatValue.equals(RESULT_FORMAT_TXT_VALUE)) {
              resultString += "" + SPACE_CHAR + getSelectedRowsDisplaying(lines) + NEW_LINE_CHAR;
              resultString += "" + SPACE_CHAR + elapsedFormatted + NEW_LINE_CHAR;
            } else if (resultFormatValue.equals(RESULT_FORMAT_CSV_VALUE)) {
              resultString += getSelectedRowsDisplaying(lines) + NEW_LINE_CHAR;
              resultString += elapsedFormatted + NEW_LINE_CHAR;
            } else if (resultFormatValue.equals(RESULT_FORMAT_HTM_VALUE)) {
              resultString +=
                  "<tr><td colspan=\""
                      + colw.length
                      + "\">"
                      + getSelectedRowsDisplaying(lines)
                      + "</td></tr>"
                      + NEW_LINE_CHAR;
              resultString +=
                  "<tr><td colspan=\""
                      + colw.length
                      + "\">"
                      + elapsedFormatted
                      + "</td></tr>"
                      + NEW_LINE_CHAR;
              resultString += "</table>" + NEW_LINE_CHAR;
            }
            j = 0;
            lines = 0;
            val = null;
            valStr = null;
            line = null;
            plusMinusLine = null;
          } else {
            throw systemexit("Error - rsmd is null, constructResult");
          }
        } catch (Exception e) {
          resultString = "";
        }
        rsmd = null;
        colscount = 0;
        cols = null;
        colw = null;
        colt = null;
        onTheDiskSeparately = false;
      } else {
        throw systemexit("Error - fieldsep is null, constructResult");
      }
    } else {
      resultString = "";
    }
    fieldsep = null;
    scrollable = false;
    htmFolder = null;
    return resultString;
  }

  /**
   * Produces the human-readable "rows selected" message matching the given row count (no rows, one
   * row, or a count of rows).
   *
   * @param lines the number of selected rows
   * @return the message describing how many rows were selected
   */
  private static final String getSelectedRowsDisplaying(int lines) {
    return (lines == 0
        ? MESSAGE_NO_ROWS_SELECTED
        : (lines == 1 ? MESSAGE1_ROW_SELECTED : "" + lines + MESSAGE_ROWS_SELECTED));
  }

  /**
   * Writes a large or binary column value (blob, clob, raw, bfile, xml, etc., depending on dbtype
   * and column type) to a separate file on disk, returning whether it was stored separately.
   *
   * @param rs the result set positioned at the current row
   * @param destfile the destination file path for the extracted value
   * @param colname the name of the column to read
   * @param coltype the (lower-cased) column type name
   * @param dbtype the dbtype determining which types are stored separately
   * @return true if the value was written to disk separately, false otherwise
   */
  static final boolean valToTheDiskSeparately(
      ResultSet rs, String destfile, String colname, String coltype, String dbtype) {
    boolean separately = false;
    if (coltype != null && dbtype != null) {
      if (dbtype.equals(DB_TYPE_MYSQL)) {
        if (coltype.equals(BIT)
            || coltype.equals(BINARY)
            || coltype.equals(VARBINARY)
            || coltype.equals(TINYBLOB)
            || coltype.equals(BLOB)
            || coltype.equals(MEDIUMBLOB)
            || coltype.equals(LONGBLOB)) {
          separately = getBlob(rs, destfile, colname);
        }
      } else if (dbtype.equals(DB_TYPE_ORACLE)) {
        if (coltype.equals(BLOB)) {
          separately = getBlob(rs, destfile, colname);
        } else if (coltype.equals(CLOB) || coltype.equals(NCLOB)) {
          separately = getClob(rs, destfile, colname, UTF8);
        } else if (coltype.equals(RAW) || coltype.equals(LONGRAW)) {
          separately = getRaw(rs, destfile, colname, BUFFLENGTH);
        } else if (coltype.equals(BFILE)) {
          separately = getBfile(rs, destfile, colname, BUFFLENGTH);
        }
      } else if (dbtype.equals(DB_TYPE_MSSQL)) {
        if (coltype.equals(IMAGE)) {
          separately = getBlob(rs, destfile, colname);
        } else if (coltype.equals(BINARY)
            || coltype.equals(VARBINARY)
            || coltype.equals(LONGVARBINARY)) {
          separately = getRaw(rs, destfile, colname, BUFFLENGTH);
        }
      } else if (dbtype.equals(DB_TYPE_DB2)) {
        if (coltype.equals(BLOB)) {
          separately = getBlob(rs, destfile, colname);
        } else if (coltype.equals(CLOB) || coltype.equals(DBCLOB)) {
          separately = getClob(rs, destfile, colname, UTF8);
        } else if (coltype.equals(XML)) {
          separately = getXml(rs, destfile, colname, UTF8);
        }
      } else if (dbtype.equals(DB_TYPE_POSTGRESQL)) {
        if (coltype.equals(BYTEA)) {
          separately = getRaw(rs, destfile, colname, BUFFLENGTH);
        } else if (coltype.equals(XML)) {
          separately = getXml(rs, destfile, colname, UTF8);
        }
      } else {
        throw systemexit("Error - dbtype has unexpected value, valToTheDiskSeparately");
      }
    }
    return separately;
  }

  /**
   * Builds the horizontal separator line for aligned text output, drawing dashes across the table
   * width with plus signs at the column boundaries derived from the column widths and field
   * separator.
   *
   * @param colw the array of column widths
   * @param fieldsep the field separator whose length sets the boundary spacing
   * @return the separator line string
   */
  static final String getPlusMinusLine(int[] colw, String fieldsep) {
    int len = fieldsep.length();
    if (colw.length > 0) {
      for (int i = 0; i < colw.length; i++) {
        len += colw[i] + fieldsep.length();
      }
    } else {
      len += fieldsep.length();
    }
    char[] plusMinusLine = new char[len];
    int B = fieldsep.length();
    int A = (int) Math.floor(B / 2);
    int index = 0;
    for (int i = 0; i < len; i++) {
      if (i == 0 || i == len - 1) {
        plusMinusLine[i] = SPACE_CHAR;
      } else {
        plusMinusLine[i] = MINUS_CHAR;
      }
    }
    for (int j = 0; j <= colw.length; j++) {
      if (j == 0) {
        index = A;
      } else {
        index = index + B + colw[j - 1];
      }
      plusMinusLine[index] = PLUS_CHAR;
    }
    len = 0;
    A = 0;
    B = 0;
    index = 0;
    return String.valueOf(plusMinusLine);
  }
}
