package com.kisscodesystems.MyDbConns;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

/**
 * Holds all mutable runtime state of the application in {@code static volatile} fields. This
 * includes the command-line arguments, the current database type, connection and prompt, the
 * per-database delimiters, password and connections-file buffers, and the maps that track each
 * query's metadata, JDBC objects, timing, error messages and worker threads.
 */
final class State {
  static volatile String[] args = null;
  static volatile String dbType = null;
  static volatile String dbConn = null;
  static volatile String prompt = null;
  static volatile String delimiterMysql = "";
  static volatile String delimiterOracle = "";
  static volatile String delimiterMssql = "";
  static volatile String delimiterDb2 = "";
  static volatile String delimiterPostgresql = "";
  static volatile char[] passwordFromInputOriginal = new char[0];
  static volatile char[] passwordFromInputVerified = new char[0];
  static volatile char[] passwordForConnections = new char[0];
  static volatile char[] fileContentConnectionsOrig = new char[0];
  static volatile char[] fileContentConnectionsTrim = new char[0];
  static volatile byte[] slConnections = new byte[0];
  static volatile byte[] ivConnections = new byte[0];
  static volatile File connectionsDirFolder = null;
  static volatile int nextQueryId = -1;
  static volatile HashMap<Integer, String> queryDbTypes = new HashMap<Integer, String>();
  static volatile HashMap<Integer, String> queryConnnas = new HashMap<Integer, String>();
  static volatile HashMap<Integer, String> queryTitles = new HashMap<Integer, String>();
  static volatile HashMap<Integer, String> queryStrings = new HashMap<Integer, String>();
  static volatile HashMap<Integer, String> queryFiles = new HashMap<Integer, String>();
  static volatile HashMap<Integer, String> queryDelimiters = new HashMap<Integer, String>();
  static volatile HashMap<Integer, String> queryTypes = new HashMap<Integer, String>();
  static volatile HashMap<Integer, String> queryIsScrollables = new HashMap<Integer, String>();
  static volatile HashMap<Integer, Integer> queryBatchExecCounts = new HashMap<Integer, Integer>();
  static volatile HashMap<Integer, String> queryBatchSourceFiles = new HashMap<Integer, String>();
  static volatile HashMap<Integer, Integer> queryBatchSourceQueryIds =
      new HashMap<Integer, Integer>();
  static volatile HashMap<Integer, String> queryBatchSourceFields = new HashMap<Integer, String>();
  static volatile HashMap<Integer, Connection> queryConnections =
      new HashMap<Integer, Connection>();
  static volatile HashMap<Integer, Statement> queryStatements = new HashMap<Integer, Statement>();
  static volatile HashMap<Integer, ResultSet> queryResultSets = new HashMap<Integer, ResultSet>();
  static volatile HashMap<Integer, Date> queryStartDates = new HashMap<Integer, Date>();
  static volatile HashMap<Integer, Date> queryEndDates = new HashMap<Integer, Date>();
  static volatile HashMap<Integer, String> queryErrorMessages = new HashMap<Integer, String>();
  static volatile HashMap<Integer, Thread> queryThreads = new HashMap<Integer, Thread>();
}
