package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.Args.*;
import static com.kisscodesystems.MyDbConns.Connections.*;
import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Const.*;
import static com.kisscodesystems.MyDbConns.Messages.*;
import static com.kisscodesystems.MyDbConns.Print.*;
import static com.kisscodesystems.MyDbConns.QueryStore.*;
import static com.kisscodesystems.MyDbConns.Validate.*;

/** Command handler for echoing a finished query's result set to a chosen target and format. */
final class ResultCommands {
  /**
   * Echoes the result set of the query identified by {@code qid}. Validates the query id and its
   * state (reporting when it is still running, not yet run, or finished with errors), then prompts
   * for result targets and formats and, when needed, whether to include the header and query,
   * validates those choices, and delegates to {@code echoResult}.
   *
   * @param qid the query id as a string, parsed to an int
   */
  static final void executeCommandResultEcho(String qid) {
    if (isFileContentConnectionsOrigReady()) {
      int queryId = parseIntSafe(qid, -1);
      String queryState = null;
      String resultTargets = "";
      String resultFormats = "";
      boolean headerToInclude = true;
      boolean queryToInclude = false;
      String headerToIncludeString = null;
      String queryToIncludeString = null;
      if (isValidQueryId(queryId)) {
        queryState = getQueryState(queryId);
        if (queryState != null) {
          if (queryState.equals(QUERY_STATE_RUNNING)) {
            outprintln(MESSAGE_YOUR_QUERY_IS_RUNNING);
          } else if (queryState.equals(QUERY_STATE_NOT_STARTED)) {
            outprintln(MESSAGE_YOUR_QUERY_HAS_TO_BE_RUN);
          } else if (queryState.equals(QUERY_STATE_FINISHED_WITH_ERRORS)) {
            outprintln("");
            printErrorMessage(getQueryErrorMessage(queryId));
          } else {
            if (isResultSetUsable(getQueryResultSet(queryId))) {
              resultTargets = "";
              resultFormats = "";
              headerToInclude = true;
              queryToInclude = false;
              resultTargets = readline(MESSAGE_ENTER_RESULT_TARGETS, APP_MAX_LENGTH_OF_INPUT);
              resultTargets = resultTargets.replaceAll(SINGLE_SPACE, "").trim();
              if ("".equals(resultTargets)) {
                resultTargets = RESULT_TARGET_CONS_VALUE;
              }
              if (isValidResultTargets(resultTargets)) {
                if (!resultTargets.equals(RESULT_TARGET_CONS_VALUE)) {
                  resultFormats = readline(MESSAGE_ENTER_RESULT_FORMATS, APP_MAX_LENGTH_OF_INPUT);
                  resultFormats = resultFormats.replaceAll(SINGLE_SPACE, "").trim();
                }
                if ("".equals(resultFormats) || resultTargets.equals(RESULT_TARGET_CONS_VALUE)) {
                  resultFormats = RESULT_FORMAT_TXT_VALUE;
                }
                if (isValidResultFormats(resultFormats)) {
                  if (!resultTargets.equals(RESULT_TARGET_CONS_VALUE)) {
                    headerToIncludeString =
                        readline(MESSAGE_ENTER_HEADER_TO_INCLUDE, APP_MAX_LENGTH_OF_INPUT)
                            .toLowerCase()
                            .trim();
                    headerToInclude = "".equals(headerToIncludeString);
                    queryToIncludeString =
                        readline(MESSAGE_ENTER_QUERY_TO_INCLUDE, APP_MAX_LENGTH_OF_INPUT)
                            .toLowerCase()
                            .trim();
                    queryToInclude =
                        Y.equals(queryToIncludeString) || YES.equals(queryToIncludeString);
                  } else {
                    headerToInclude = true;
                    queryToInclude = false;
                  }
                  echoResult(
                      queryId, resultTargets, resultFormats, headerToInclude, queryToInclude);
                } else {
                  outprintln(MESSAGE_CORRECT_RESULT_FORMATS_ARE);
                }
              } else {
                outprintln(MESSAGE_CORRECT_RESULT_TARGETS_ARE);
              }
            } else {
              outprintln(MESSAGE_SORRY_BUT_THIS_RESULT_SET_CANNOT_BE_DISPAYED + queryId);
            }
          }
        } else {
          throw systemexit("Error - queryState is null, executeCommandResultEcho");
        }
      }
      queryId = 0;
      queryState = null;
      resultTargets = null;
      resultFormats = null;
      headerToInclude = false;
      queryToInclude = false;
      headerToIncludeString = null;
      queryToIncludeString = null;
    }
  }
}
