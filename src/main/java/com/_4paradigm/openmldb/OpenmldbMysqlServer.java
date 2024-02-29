package com._4paradigm.openmldb;

import cn.paxos.mysql.MySqlListener;
import cn.paxos.mysql.ResultSetWriter;
import cn.paxos.mysql.engine.QueryResultColumn;
import cn.paxos.mysql.engine.SqlEngine;
import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.jdbc.SQLResultSet;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.SdkOption;
import com._4paradigm.openmldb.sdk.SqlException;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OpenmldbMysqlServer {
  public SqlClusterExecutor sqlExecutor;

  public OpenmldbMysqlServer(int port, String zkCluster, String zkPath) throws SqlException {
    SdkOption option = new SdkOption();
    option.setZkCluster(zkCluster);
    option.setZkPath(zkPath);
    option.setSessionTimeout(10000);
    option.setRequestTimeout(60000);

    sqlExecutor = new SqlClusterExecutor(option);

    new MySqlListener(
        port,
        100,
        new SqlEngine() {
          @Override
          public void authenticate(
              String database, String userName, byte[] scramble411, byte[] authSeed)
              throws IOException {}

          @Override
          public void query(
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

            try {
              if (MockResult.mockResults.containsKey(sql.toLowerCase())) {
                Pair<List<QueryResultColumn>, List<List<String>>> pair =
                    MockResult.mockResults.get(sql.toLowerCase());
                resultSetWriter.writeColumns(pair.getKey());
                for (List<String> row : pair.getValue()) {
                  resultSetWriter.writeRow(row);
                }
                resultSetWriter.finish();
              } else {
                java.sql.Statement stmt = sqlExecutor.getStatement();

                stmt.execute("SET @@execute_mode='online'");
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
      // version, 8.0.29
      // version_comment, MySQL Community Server - GPL
      List<String> row = new ArrayList<>();
      row.add("version");
      // mocked version
      row.add("8.0.29");
      resultSetWriter.writeRow(row);

      row = new ArrayList<>();
      row.add("version_comment");
      row.add("OpenMLDB");
      resultSetWriter.writeRow(row);
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
