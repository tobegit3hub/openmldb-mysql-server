package com._4paradigm.openmldb;

import cn.paxos.mysql.MySqlListener;
import cn.paxos.mysql.ResultSetWriter;
import cn.paxos.mysql.engine.QueryResultColumn;
import cn.paxos.mysql.engine.SqlEngine;
import cn.paxos.mysql.util.SHAUtils;
import cn.paxos.mysql.util.Utils;
import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.jdbc.SQLResultSet;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.SdkOption;
import com._4paradigm.openmldb.sdk.SqlException;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import com.google.common.base.Strings;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenmldbMysqlServer {
  private static Pattern setExecuteModePattern =
      Pattern.compile("(?i)SET\\s+@@execute_mode\\s*=\\s*'(.*)'");
  private final Map<Integer, Map<String, SqlClusterExecutor>> sqlClusterExecutorMap =
      new ConcurrentHashMap<>();
  private final Map<Integer, Map<String, Boolean>> onlineExecuteModeEnabledMap =
      new ConcurrentHashMap<>();
  private final Map<Integer, Map<String, SdkOption>> sdkOptionMap = new ConcurrentHashMap<>();

  public OpenmldbMysqlServer(int port, String zkCluster, String zkPath) {
    new MySqlListener(
        port,
        100,
        new SqlEngine() {
          @Override
          public void authenticate(
              int connectionId,
              String database,
              String userName,
              byte[] scramble411,
              byte[] authSeed)
              throws IOException {
            // mocked username
            String validUser = "root";
            if (!userName.equals(validUser)) {
              throw new IOException(
                  new IllegalAccessException(
                      "Authentication failed: User " + userName + " is not allowed to connect"));
            }
            // mocked password
            String validPassword = "4pdadmin";

            String validPasswordSha1 = SHAUtils.SHA(validPassword, SHAUtils.SHA_1);
            String validScramble411WithSeed20 = Utils.scramble411(validPasswordSha1, authSeed);

            if (!Utils.compareDigest(
                validScramble411WithSeed20, Base64.getEncoder().encodeToString(scramble411))) {
              throw new IOException(
                  new IllegalAccessException("Authentication failed: Validation failed"));
            }

            try {
              if (!sqlClusterExecutorMap.containsKey(connectionId)) {
                synchronized (this) {
                  if (!sqlClusterExecutorMap.containsKey(connectionId)) {
                    SdkOption option = new SdkOption();
                    option.setZkCluster(zkCluster);
                    option.setZkPath(zkPath);
                    option.setSessionTimeout(10000);
                    option.setRequestTimeout(60000);
                    option.setUser(userName);
                    option.setPassword(validPassword);
                    Map<String, SdkOption> tmpMap = new ConcurrentHashMap<>();
                    tmpMap.put(userName, option);
                    sdkOptionMap.put(connectionId, tmpMap);

                    Map<String, Boolean> tmpMap2 = new ConcurrentHashMap<>();
                    onlineExecuteModeEnabledMap.put(connectionId, tmpMap2);

                    SqlClusterExecutor sqlExecutor = new SqlClusterExecutor(option);
                    Map<String, SqlClusterExecutor> tmpMap3 = new ConcurrentHashMap<>();
                    tmpMap3.put(userName, sqlExecutor);
                    sqlClusterExecutorMap.put(connectionId, tmpMap3);
                  }
                }
              }

              if (!sqlClusterExecutorMap.get(connectionId).containsKey(userName)) {
                synchronized (this) {
                  if (!sqlClusterExecutorMap.get(connectionId).containsKey(userName)) {
                    SdkOption option = new SdkOption();
                    option.setZkCluster(zkCluster);
                    option.setZkPath(zkPath);
                    option.setSessionTimeout(10000);
                    option.setRequestTimeout(60000);
                    option.setUser(userName);
                    option.setPassword(validPassword);
                    sdkOptionMap.get(connectionId).put(userName, option);

                    SqlClusterExecutor sqlExecutor = new SqlClusterExecutor(option);
                    sqlClusterExecutorMap.get(connectionId).put(userName, sqlExecutor);
                  }
                }
              }
            } catch (SqlException e) {
              throw new IOException(e);
            }
          }

          @Override
          public void query(
              int connectionId,
              ResultSetWriter resultSetWriter,
              String database,
              String userName,
              byte[] scramble411,
              byte[] authSeed,
              String sql)
              throws IOException {
            // Print useful information
            System.out.println(
                "Try to execute query, Database: "
                    + database
                    + ", User: "
                    + userName
                    + ", SQL: "
                    + sql);

            this.authenticate(connectionId, database, userName, scramble411, authSeed);

            try {
              if (MockResult.mockResults.containsKey(sql.toLowerCase())) {
                Pair<List<QueryResultColumn>, List<List<String>>> pair =
                    MockResult.mockResults.get(sql.toLowerCase());
                resultSetWriter.writeColumns(pair.getKey());
                for (List<String> row : pair.getValue()) {
                  resultSetWriter.writeRow(row);
                }
                resultSetWriter.finish();
              } else if (setExecuteModePattern.matcher(sql).matches()) {
                List<QueryResultColumn> columns = new ArrayList<>();
                columns.add(new QueryResultColumn("execute_mode", "VARCHAR(255)"));
                resultSetWriter.writeColumns(columns);

                Matcher matcher = setExecuteModePattern.matcher(sql);
                if (matcher.find()) {
                  String executeMode = matcher.group(1);
                  List<String> row = new ArrayList<>();
                  row.add(executeMode);
                  resultSetWriter.writeRow(row);

                  if (executeMode.trim().equalsIgnoreCase("online")) {
                    onlineExecuteModeEnabledMap.get(connectionId).put(userName, Boolean.TRUE);
                  }
                }
                resultSetWriter.finish();
              } else {
                for (String patternStr : MockResult.mockPatternResults.keySet()) {
                  Pattern pattern = Pattern.compile(patternStr);
                  if (pattern.matcher(sql).matches()) {
                    Pair<List<QueryResultColumn>, List<List<String>>> pair =
                        MockResult.mockPatternResults.get(patternStr);
                    resultSetWriter.writeColumns(pair.getKey());
                    for (List<String> row : pair.getValue()) {
                      resultSetWriter.writeRow(row);
                    }
                    resultSetWriter.finish();
                    return;
                  }
                }

                java.sql.Statement stmt =
                    sqlClusterExecutorMap.get(connectionId).get(userName).getStatement();

                if (!Strings.isNullOrEmpty(database)) {
                  stmt.execute("use " + database);
                }
                if (onlineExecuteModeEnabledMap
                    .get(connectionId)
                    .getOrDefault(userName, Boolean.FALSE)) {
                  stmt.execute("SET @@execute_mode='online'");
                } else {
                  stmt.execute("SET @@execute_mode='offline'");
                }
                if (sql.equalsIgnoreCase("SHOW FULL TABLES")) {
                  sql = "SHOW TABLES";
                }
                stmt.execute(sql);

                if (sql.toLowerCase().startsWith("select")
                    || sql.toLowerCase().startsWith("show")) {
                  SQLResultSet resultSet = (SQLResultSet) stmt.getResultSet();
                  outputResultSet(resultSetWriter, resultSet, sql);
                }

                System.out.println("Success to execute OpenMLDB SQL: " + sql);

                // Close resources
                stmt.close();
              }
            } catch (Exception e) {
              e.printStackTrace();
              throw new IOException(e.getMessage());
            }
          }

          @Override
          public void close(int connectionId) {
            sdkOptionMap.remove(connectionId);
            onlineExecuteModeEnabledMap.remove(connectionId);
            sqlClusterExecutorMap.remove(connectionId);
          }
        });
  }

  public static void main(String[] args) {
    int serverPort = 3307;
    String zkCluster = "127.0.0.1:2181";
    String zkPath = "/openmldb";

    try {
      OpenmldbMysqlServer server = new OpenmldbMysqlServer(serverPort, zkCluster, zkPath);
      server.start();
      // Conenct with mysql client, mysql -h127.0.0.1 -P3307
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void outputResultSet(ResultSetWriter resultSetWriter, SQLResultSet resultSet, String sql)
      throws SQLException {
    // Build and respond columns
    List<QueryResultColumn> columns = new ArrayList<>();

    Schema schema = resultSet.GetInternalSchema();
    // int columnCount = schema.GetColumnCnt();
    int columnCount = schema.getColumnList().size();

    // Add schema
    for (int i = 0; i < columnCount; i++) {
      String columnName = schema.getColumnName(i);
      int columnType = schema.getColumnType(i);
      columns.add(
          new QueryResultColumn(columnName, TypeUtil.openmldbTypeToMysqlTypeString(columnType)));
    }

    resultSetWriter.writeColumns(columns);

    // Add rows
    while (resultSet.next()) {
      // Build and respond rows
      List<String> row = new ArrayList<>();

      for (int i = 0; i < columnCount; i++) {
        // DataType type = schema.GetColumnType(i);
        int type = schema.getColumnType(i);
        String columnValue = TypeUtil.getResultSetStringColumn(resultSet, i + 1, type);
        row.add(columnValue);
      }

      resultSetWriter.writeRow(row);
    }

    // mysql workbench will check some variables
    if (sql.equalsIgnoreCase("show variables")) {
      List<String> row;
      for (String variable : MockResult.mockVariables.keySet()) {
        row = new ArrayList<>();
        row.add(variable);
        row.add(MockResult.mockVariables.get(variable));
        resultSetWriter.writeRow(row);
      }
    }

    // Finish the response
    resultSetWriter.finish();
  }

  public void start() {
    Thread currentThread = Thread.currentThread();

    try {
      currentThread.join(); // This will cause the current thread to wait indefinitely
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
