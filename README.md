# MyDbConns

MyDbConns is a tiny command-line SQL client. It connects to **Mysql, Oracle,
Mssql, Db2 and Postgresql** databases, and stores the connection parameters — the
database user, password, driver and connection string — in a strongly encrypted
file on the local filesystem. Nothing is ever written to disk in clear text, and
queries can be run in the foreground or as background jobs.

- **Developed by:** Jozsef Kiss — KissCode Systems Kft
- **License:** GNU General Public License, version 3
- **Current version:** 2.0

## Concept

- Database **connections** are grouped by **database type** (`mysql`, `oracle`,
  `mssql`, `db2`, `postgresql`). Each connection is a named record holding a
  database user, password, JDBC driver class and connection string.
- All connections live inside a single **encrypted connections file**, protected
  by one strong **connections password** (chosen on first run). Every save
  re-encrypts the whole file.
- **Queries** are created against a connection and run on their own background
  thread, so long-running statements do not block the prompt. A query is one of
  three types: **single** (one SELECT with an optional scrollable result set),
  **multiple** (several statements split by the delimiter), or **batch** (a
  prepared statement fed row-by-row from a source file or another query's result
  set). Results can be echoed to the console or written to disk as txt / csv / htm.
- The **query factory** is an interactive per-connection SQL shell: it opens one
  connection and lets you type statements directly, showing each result inline.
- The application can run as a one-shot command (`MyDbConns <args>`) or in
  **interactive mode**, which keeps a session open and adds terminal-style Up/Down
  command history and optional connections-password caching.

## Directory layout

On first run MyDbConns initializes a single working directory next to the jar:

| Directory | Purpose                                                        |
| --------- | -------------------------------------------------------------- |
| `cs`      | The encrypted connections file (encrypted payload + IV + salt) |

The connections file is accompanied by its own per-file initialization vector
(`.iv`) and salt (`.sl`); a fresh salt and IV are generated on every save
(`.nw` files are the transient new copies written during a save).

## Security model (short)

- **Cipher:** AES-GCM (`AES/GCM/NoPadding`), 256-bit key, 128-bit auth tag — the
  authenticated tag both protects integrity and acts as the "correct password"
  check (a wrong password fails the tag and refuses to decrypt).
- **Key derivation:** PBKDF2WithHmacSHA512, 600,000 iterations, 256-bit output,
  256-byte random salt.
- **Random header:** the decrypted content begins with a header line of a random
  number of random lowercase letters, so there is no fixed/known plaintext for an
  offline attacker to verify guesses against.
- Passwords and file content are held in `char[]`/`byte[]` and wiped after use.

## Build

MyDbConns is a single-package Java program. The only build/run dependency is the
**Oracle JDBC driver** (`OracleJdbc.jar`), which is required on the classpath
because the code references `oracle.jdbc.OracleBfile` for the Oracle `BFILE` data
type. Building is a `javac` compile plus packaging the runnable jar (these commands
assume the repository root as the working directory):

```bash
# Run this:
./MyDbConns_build.sh
```

This produces `MyDbConns.jar` in the repository root. To also connect to the other
databases at runtime, put the matching JDBC driver jars on the classpath as well
(Mysql / Mssql / Postgresql / Db2). A JDK 8 or newer is sufficient.

## Run

Launch the jar with the Oracle driver (and any other needed drivers) on the
classpath:

```bash
DRIVER=/path/to/OracleJdbc.jar
java -cp "$DRIVER:MyDbConns.jar" com.kisscodesystems.MyDbConns.MyDbConnsMain help
java -cp "$DRIVER:MyDbConns.jar" com.kisscodesystems.MyDbConns.MyDbConnsMain application describe
java -cp "$DRIVER:MyDbConns.jar" com.kisscodesystems.MyDbConns.MyDbConnsMain interactive mode
```

> **Note:** the application reads the connections password from the terminal via
> the system console, so any command that touches the connections file must be run
> in a real interactive terminal. Running it with its output piped or redirected
> (no attached TTY) exits with `Error - one of these is null: us|console, main.`.
> The information-only commands below (`help`, `application describe`, etc.) do not
> need the connections file.

### Tests

The regression suite is driven by `test/MyDbConns_run_tests.sh`. It compiles the
current sources, then runs the JUnit tests that check the validators, the pure
helpers and the encrypted connections-file round-trip:

```bash
bash test/MyDbConns_run_tests.sh
```

The script needs `junit-4.12.jar` and `hamcrest-core-1.3.jar` (bundled in the
repository's `lib/` directory) plus the Oracle driver, so no extra setup is
required.

## Getting started (how to initialize)

The application is invoked as
`java -cp "$DRIVER:MyDbConns.jar" com.kisscodesystems.MyDbConns.MyDbConnsMain <arguments>`.

1. **`... application describe`** and **`... help`** (or **`?`**)
   Read the application limits, directories and the full command list.

2. **`... interactive mode`** (or any command that needs the connections file)
   On the very first run, when no `cs` directory exists, MyDbConns confirms the
   location is safe, creates the `cs` directory, and prompts you to choose your
   **connections password**. Construct it carefully and do not forget it — it is
   the only key to your stored connections.

3. **`... connection add`**
   Add your first connection. You will be prompted for the database type, a
   connection name, the database user, password, JDBC driver class and connection
   string. The connection is tested before it is saved.

4. **`... connection listall`**
   Confirm your connection was stored.

5. **`... query factory <dbtype> <connname>`**
   Open an interactive SQL shell on that connection and start typing statements —
   the quickest way to run ad-hoc queries.

## Important functions

### Application info / help

| Command                        | What it does                                              |
| ------------------------------ | -------------------------------------------------------- |
| `help` · `?`                   | Print the full usage / hints.                            |
| `application describe`         | Print application limits, directories and parameters.    |
| `application story`            | Print the basic concept and recommended usage.           |
| `welcome screen`               | Reprint the first-run welcome screen.                    |
| `connections good password`    | Print the strength rules for the connections password.   |
| `connections password change`  | Re-encrypt the connections file under a new password.    |

### Connections

| Command                            | What it does                                                                 |
| ---------------------------------- | ---------------------------------------------------------------------------- |
| `connection add`                   | **Add a new connection.** Prompts for type, name, user, password, driver and connection string; tests it before saving. |
| `connection change [name]`         | Change the properties of an existing connection.                             |
| `connection describe [name]`       | Print the properties of a connection.                                        |
| `connection listall`               | List all stored connections.                                                 |
| `connection list active`           | List connections that currently have a live/used session.                    |
| `connection list inactive`         | List connections that are not currently in use.                              |
| `connection use <name>`            | Select a connection as the current one (optionally `use <dbtype> <name>`).   |
| `connection test [name]`           | Open a test connection to verify the parameters.                             |
| `connection load <file>`           | Bulk-import connections from a plain 6-field-per-record file.                |
| `connection delete [name]`         | Delete a single connection.                                                  |
| `connection deleteall`             | Delete all connections.                                                      |

### Queries

| Command                       | What it does                                                                        |
| ----------------------------- | ----------------------------------------------------------------------------------- |
| `query add single`            | **Create a single-statement query** (one SELECT, optionally scrollable).            |
| `query add multiple`          | Create a multi-statement query (statements split by the delimiter).                 |
| `query add batch`             | Create a batch query fed from a source file or another query's result set.          |
| `query listall`               | List all queries.                                                                   |
| `query list active`           | List queries that are currently running.                                            |
| `query list inactive`         | List queries that are not running.                                                  |
| `query describe <id>`         | Print the properties of a query.                                                    |
| `query run <id>`              | Run (or re-run) a saved query on its own background thread.                          |
| `query cancel <id>`           | Cancel a running query (best-effort, via JDBC `Statement.cancel()`).                |
| `query cancelall`             | Cancel all running queries.                                                         |
| `query delete <id>`           | Delete a single (non-running) query.                                                |
| `query deleteall`             | Delete all queries.                                                                 |

### Query factory (interactive SQL shell)

| Command                                   | What it does                                                              |
| ----------------------------------------- | ------------------------------------------------------------------------- |
| `query factory`                           | Open the SQL shell, prompting for db type and connection.                 |
| `query factory <query>`                   | Open on the current connection and run `<query>` first.                   |
| `query factory <dbtype> <connname>`       | Open directly on the given connection.                                    |
| `query factory <dbtype> <connname> <q>`   | Open on the given connection and run `<q>` first.                         |

Inside the factory you can type SQL directly, or use `?`/`help`, `delimiter show`,
`delimiter change`, and `connection describe`; type the exit keyword to leave.

### Results and delimiter

| Command                | What it does                                                                          |
| ---------------------- | ------------------------------------------------------------------------------------- |
| `result echo <id>`     | Re-print a finished query's result set (console / file, as txt / csv / htm).          |
| `delimiter show`       | Show the statement delimiter for the current database type.                           |
| `delimiter change [d]` | Change the statement delimiter (empty resets it).                                     |

### Interactive mode

| Command             | What it does                                                                    |
| ------------------- | ------------------------------------------------------------------------------- |
| `interactive mode`  | Start a persistent session (with Up/Down command history).                      |
| `mysql` · `oracle` · `mssql` · `db2` · `postgresql` | Switch the prompt to that database type.        |
| `exit`              | Step back up one level (connection → db type → app), or quit from the top.      |

When you exit from the top level while queries are still running, MyDbConns asks
whether to cancel them all before leaving.
