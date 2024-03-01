package com._4paradigm.openmldb;

import cn.paxos.mysql.MySqlListener;
import cn.paxos.mysql.engine.QueryResultColumn;
import com._4paradigm.openmldb.common.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockResult {
  public static Map<String, Pair<List<QueryResultColumn>, List<List<String>>>> mockResults =
      new HashMap<>();
  public static Map<String, Pair<List<QueryResultColumn>, List<List<String>>>> mockPatternResults =
      new HashMap<>();

  static {
    String query = "show character set where charset = 'utf8mb4'";
    List<QueryResultColumn> columns = new ArrayList<>();
    columns.add(new QueryResultColumn("Charset", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Description", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Default collation", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Maxlen", "VARCHAR(255)"));
    List<List<String>> rows = new ArrayList<>();
    List<String> row = new ArrayList<>();
    row.add("utf8mb4");
    row.add("UTF-8 Unicode");
    row.add("utf8mb4_0900_ai_ci");
    row.add("4");
    rows.add(row);
    mockResults.put(query, new Pair<>(columns, rows));

    query = "show global status";
    columns = new ArrayList<>();
    columns.add(new QueryResultColumn("Variable_name", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Value", "VARCHAR(255)"));
    rows = new ArrayList<>();
    mockResults.put(query, new Pair<>(columns, rows));

    //    query = "select connection_id()";
    //    columns = new ArrayList<>();
    //    columns.add(new QueryResultColumn("CONNECTION_ID()", "VARCHAR(255)"));
    //    rows = new ArrayList<>();
    //    row = new ArrayList<>();
    //    row.add("1");
    //    mockResults.put(query, new Pair<>(columns, rows));

    query = "show session status like 'ssl_cipher'";
    columns = new ArrayList<>();
    columns.add(new QueryResultColumn("Variable_name", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Value", "VARCHAR(255)"));
    rows = new ArrayList<>();
    row = new ArrayList<>();
    row.add("Ssl_cipher");
    row.add("TLS_AES_256_GCM_SHA384");
    mockResults.put(query, new Pair<>(columns, rows));

    query = "show session variables like 'sql_mode'";
    columns = new ArrayList<>();
    columns.add(new QueryResultColumn("Variable_name", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Value", "VARCHAR(255)"));
    rows = new ArrayList<>();
    row = new ArrayList<>();
    row.add("sql_mode");
    row.add(
        "ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION");
    mockResults.put(query, new Pair<>(columns, rows));

    query = "show session variables like 'version_comment'";
    columns = new ArrayList<>();
    columns.add(new QueryResultColumn("Variable_name", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Value", "VARCHAR(255)"));
    rows = new ArrayList<>();
    row = new ArrayList<>();
    row.add("version_comment");
    row.add("");
    mockResults.put(query, new Pair<>(columns, rows));

    query = "show session variables like 'version'";
    columns = new ArrayList<>();
    columns.add(new QueryResultColumn("Variable_name", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Value", "VARCHAR(255)"));
    rows = new ArrayList<>();
    row = new ArrayList<>();
    row.add("version");
    row.add(MySqlListener.VERSION);
    mockResults.put(query, new Pair<>(columns, rows));

    query = "show plugins";
    // # Name, Status, Type, Library, License
    // keyring_file, ACTIVE, KEYRING, keyring_file.so, GPL
    columns = new ArrayList<>();
    columns.add(new QueryResultColumn("Name", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Status", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Type", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Library", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("License", "VARCHAR(255)"));
    rows = new ArrayList<>();
    mockResults.put(query, new Pair<>(columns, rows));

    query = "show slave status";
    // Slave_IO_State, Master_Host, Master_User, Master_Port, Connect_Retry, Master_Log_File,
    // Read_Master_Log_Pos, Relay_Log_File, Relay_Log_Pos, Relay_Master_Log_File, Slave_IO_Running,
    // Slave_SQL_Running, Replicate_Do_DB, Replicate_Ignore_DB, Replicate_Do_Table,
    // Replicate_Ignore_Table, Replicate_Wild_Do_Table, Replicate_Wild_Ignore_Table, Last_Errno,
    // Last_Error, Skip_Counter, Exec_Master_Log_Pos, Relay_Log_Space, Until_Condition,
    // Until_Log_File, Until_Log_Pos, Master_SSL_Allowed, Master_SSL_CA_File, Master_SSL_CA_Path,
    // Master_SSL_Cert, Master_SSL_Cipher, Master_SSL_Key, Seconds_Behind_Master,
    // Master_SSL_Verify_Server_Cert, Last_IO_Errno, Last_IO_Error, Last_SQL_Errno, Last_SQL_Error,
    // Replicate_Ignore_Server_Ids, Master_Server_Id, Master_UUID, Master_Info_File, SQL_Delay,
    // SQL_Remaining_Delay, Slave_SQL_Running_State, Master_Retry_Count, Master_Bind,
    // Last_IO_Error_Timestamp, Last_SQL_Error_Timestamp, Master_SSL_Crl, Master_SSL_Crlpath,
    // Retrieved_Gtid_Set, Executed_Gtid_Set, Auto_Position, Replicate_Rewrite_DB, Channel_Name,
    // Master_TLS_Version, Master_public_key_path, Get_master_public_key, Network_Namespace
    columns = new ArrayList<>();
    String columnNameStr =
        "Slave_IO_State, Master_Host, Master_User, Master_Port, Connect_Retry, Master_Log_File, Read_Master_Log_Pos, Relay_Log_File, Relay_Log_Pos, Relay_Master_Log_File, Slave_IO_Running, Slave_SQL_Running, Replicate_Do_DB, Replicate_Ignore_DB, Replicate_Do_Table, Replicate_Ignore_Table, Replicate_Wild_Do_Table, Replicate_Wild_Ignore_Table, Last_Errno, Last_Error, Skip_Counter, Exec_Master_Log_Pos, Relay_Log_Space, Until_Condition, Until_Log_File, Until_Log_Pos, Master_SSL_Allowed, Master_SSL_CA_File, Master_SSL_CA_Path, Master_SSL_Cert, Master_SSL_Cipher, Master_SSL_Key, Seconds_Behind_Master, Master_SSL_Verify_Server_Cert, Last_IO_Errno, Last_IO_Error, Last_SQL_Errno, Last_SQL_Error, Replicate_Ignore_Server_Ids, Master_Server_Id, Master_UUID, Master_Info_File, SQL_Delay, SQL_Remaining_Delay, Slave_SQL_Running_State, Master_Retry_Count, Master_Bind, Last_IO_Error_Timestamp, Last_SQL_Error_Timestamp, Master_SSL_Crl, Master_SSL_Crlpath, Retrieved_Gtid_Set, Executed_Gtid_Set, Auto_Position, Replicate_Rewrite_DB, Channel_Name, Master_TLS_Version, Master_public_key_path, Get_master_public_key, Network_Namespace";
    for (String columnName : columnNameStr.split(", ")) {
      columns.add(new QueryResultColumn(columnName, "VARCHAR(255)"));
    }
    rows = new ArrayList<>();
    mockResults.put(query, new Pair<>(columns, rows));

    query = "show variables like 'skip_show_database'";
    columns = new ArrayList<>();
    columns.add(new QueryResultColumn("Variable_name", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Value", "VARCHAR(255)"));
    rows = new ArrayList<>();
    row = new ArrayList<>();
    row.add("skip_show_database");
    row.add("OFF");
    mockResults.put(query, new Pair<>(columns, rows));

    query = "show variables like 'character_set_database'";
    columns = new ArrayList<>();
    columns.add(new QueryResultColumn("Variable_name", "VARCHAR(255)"));
    columns.add(new QueryResultColumn("Value", "VARCHAR(255)"));
    rows = new ArrayList<>();
    row = new ArrayList<>();
    row.add("character_set_database");
    row.add("utf8mb4");
    mockResults.put(query, new Pair<>(columns, rows));

    String pattern = "(?i)SELECT .+ FROM .*information_schema.*\\..*routines.* WHERE .*routine_schema.* =.*";
    // SPECIFIC_NAME, ROUTINE_CATALOG, ROUTINE_SCHEMA, ROUTINE_NAME, ROUTINE_TYPE, DATA_TYPE,
    // CHARACTER_MAXIMUM_LENGTH, CHARACTER_OCTET_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE,
    // DATETIME_PRECISION, CHARACTER_SET_NAME, COLLATION_NAME, DTD_IDENTIFIER, ROUTINE_BODY,
    // ROUTINE_DEFINITION, EXTERNAL_NAME, EXTERNAL_LANGUAGE, PARAMETER_STYLE, IS_DETERMINISTIC,
    // SQL_DATA_ACCESS, SQL_PATH, SECURITY_TYPE, CREATED, LAST_ALTERED, SQL_MODE, ROUTINE_COMMENT,
    // DEFINER, CHARACTER_SET_CLIENT, COLLATION_CONNECTION, DATABASE_COLLATION
    columns = new ArrayList<>();
    columnNameStr =
        "SPECIFIC_NAME, ROUTINE_CATALOG, ROUTINE_SCHEMA, ROUTINE_NAME, ROUTINE_TYPE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, CHARACTER_OCTET_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE, DATETIME_PRECISION, CHARACTER_SET_NAME, COLLATION_NAME, DTD_IDENTIFIER, ROUTINE_BODY, ROUTINE_DEFINITION, EXTERNAL_NAME, EXTERNAL_LANGUAGE, PARAMETER_STYLE, IS_DETERMINISTIC, SQL_DATA_ACCESS, SQL_PATH, SECURITY_TYPE, CREATED, LAST_ALTERED, SQL_MODE, ROUTINE_COMMENT, DEFINER, CHARACTER_SET_CLIENT, COLLATION_CONNECTION, DATABASE_COLLATION";
    for (String columnName : columnNameStr.split(", ")) {
      columns.add(new QueryResultColumn(columnName, "VARCHAR(255)"));
    }
    rows = new ArrayList<>();
    mockPatternResults.put(pattern, new Pair<>(columns, rows));
  }
}
