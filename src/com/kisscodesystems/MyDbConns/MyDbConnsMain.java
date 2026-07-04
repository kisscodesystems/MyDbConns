/*
 ** MyDbConns application.
 **
 ** Description:    : This tiny application works for you as a command line
 **                   sql client.
 **                   Supports Mysql, Oracle, Mssql, Db2 and Postgresql.
 **
 ** Published       : 05.03.2017
 **
 ** Current version : 2.0
 **
 ** Developed by    : Jozsef Kiss
 **                   KissCode Systems Kft
 **                   <http://www.prdare.com>
 **
 ** Changelog       : 1.0 - 04.01.2017
 **                   Initial release.
 **                   1.1 - 04.05.2017
 **                   Htm results contain the binary and long-length contents
 **                     behind a htm link.
 **                   Data visualization has been improved.
 **                   Smaller improvements.
 **                   The oracle jdbc is needed for recompiling because of the
 **                     BFILE datatype!
 **                     (The jdbc driver is usually needed for building
 **                      the connections to the database.)
 **                   1.2 - 05.03.2017
 **                   Oracle BFILE is now deprecated, using OracleBfile instead.
 **                   1.3 - 08.19.2017
 **                   1.4 - 03.19.2018
 **                   Now supports Mysql.
 **                   2.0 - 02.07.2026
 **                   Major refactor.
 **
 ** Example command to start this application:
 **   "C:\Program Files\Java\jdk1.8.0_121\bin\java.exe" /
 **   -cp C:\drivers\OracleJdbc.jar;C:\opensourcejava\MyDbConns\MyDbConns.jar /
 **   com.kisscodesystems.MyDbConns.MyDbConnsMain
 **
 ** Structure (flat static modules, wired by import static):
 **   - Const, Args, Messages   : constants, argument keywords, user messages.
 **   - State                   : all mutable runtime state.
 **   - ConsoleIo, Validate     : console i/o + prompts, input validation.
 **   - Utils, FileStore        : data/array helpers, low-level file i/o.
 **   - Crypto, Connections     : encryption + the encrypted connections file.
 **   - QueryStore              : query metadata, lifecycle and worker threads.
 **   - Print                   : listings and result output (txt/csv/htm).
 **   - Router                  : the interactive loop and command dispatch.
 **   - ConnectionCommands, QueryCommands, ResultCommands,
 **     DelimiterCommands, AppCommands, QueryFactory : the command handlers.
 **   - Query                   : the thread of each separately started query.
 **   - MyDbConnsMain           : starts the application.
 **
 ** Query implements Runnable (one thread per running query); the modules are
 ** stateless static utilities operating on State.
 **
 ** MyDbConns is free software: you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation, version 3.
 **
 ** MyDbConns is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 ** GNU General Public License for more details.
 **
 ** You should have received a copy of the GNU General Public License
 ** along with MyDbConns. If not, see <http://www.gnu.org/licenses/>.
 */
package com.kisscodesystems.MyDbConns;

import static com.kisscodesystems.MyDbConns.ConsoleIo.*;
import static com.kisscodesystems.MyDbConns.Router.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class MyDbConnsMain {
  /**
   * Application entry point. Stores the command-line arguments in the shared state, runs the
   * interactive router loop, and then exits the JVM with status {@code 0}.
   *
   * @param args the command-line arguments passed to the application
   */
  public static void main(String[] args) {
    setArgs(args);
    run();
    System.exit(0);
  }

  /**
   * Serialization guard that always fails, preventing instances of this class from being
   * deserialized.
   *
   * @param in the object input stream (unused)
   * @throws IOException always thrown to forbid deserialization
   */
  private final void readObject(ObjectInputStream in) throws IOException {
    throw new IOException("");
  }

  /**
   * Serialization guard that always fails, preventing instances of this class from being
   * serialized.
   *
   * @param out the object output stream (unused)
   * @throws IOException always thrown to forbid serialization
   */
  private final void writeObject(ObjectOutputStream out) throws IOException {
    throw new IOException("");
  }
}
