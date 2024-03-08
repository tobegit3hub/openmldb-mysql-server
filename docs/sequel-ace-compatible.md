# sql execute when connect

1. [PARTIALLY SUPPORTED] SHOW VARIABLES
   > (mock) append variable `character_set_database`, `collation_database`, `default_storage_engine`
   , `skip_show_database`, `version`, `version_comment`
2. [MOCKED] SELECT @@global.max_allowed_packet
3. [IGNORED] SET NAMES 'utf8mb4'
4. [MOCKED] SHOW VARIABLES LIKE 'skip_show_database'
5. [FULLY SUPPORTED] SHOW DATABASES

# sql execute when select db

1. [FULLY SUPPORTED] USE `demo_db`
2. [MOCKED] SHOW VARIABLES LIKE 'character_set_database'
3. [ADAPTED] SHOW FULL TABLES
   > Adapt `SHOW FULL TABLES` to `SHOW TABLES`
4. [MOCKED] SELECT * FROM information_schema.routines WHERE routine_schema = 'demo_db' ORDER BY routine_name
5. [MOCKED] SHOW FULL COLUMNS FROM `demo_table1` FROM `demo_db`
   ```
   # Field, Type, Collation, Null, Key, Default, Extra, Privileges, Comment
   name, varchar(255), utf8mb4_general_ci, YES, , , , select,insert,update,references,
   subject_id, int, , YES, , , , select,insert,update,references,
   ```
6. [MOCKED] SHOW INDEX FROM `demo_table1`
7. [MOCKED] SELECT SPECIFIC_NAME, ROUTINE_TYPE, DTD_IDENTIFIER, IS_DETERMINISTIC, SQL_DATA_ACCESS, SECURITY_TYPE,
   DEFINER FROM `information_schema`.`ROUTINES` WHERE `ROUTINE_SCHEMA` = 'demo_db'

# sql execute when create

1. [NOT SUPPORTED] CREATE TABLE `demo_table3` (id INT(11) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT)
   > OpenMLDB cannot resolve keyword `UNSIGNED`

# sql execute when show table info

1. [FULLY SUPPORTED] SHOW TABLE STATUS LIKE 'demo_table1'
2. [FULLY SUPPORTED] SHOW CREATE TABLE `demo_db`.`demo_table1`
3. [MOCKED] SHOW TABLES IN information_schema LIKE 'ENGINES'
4. [MOCKED] SELECT Engine, Support FROM `information_schema`.`engines` WHERE SUPPORT IN ('DEFAULT', 'YES') AND Engine !
   = 'PERFORMANCE_SCHEMA'
5. [MOCKED] SHOW VARIABLES LIKE 'default_storage_engine'
6. [MOCKED] SHOW VARIABLES LIKE 'character_set_database'
7. [MOCKED] SHOW VARIABLES LIKE 'collation_database'
8. [MOCKED] SELECT * FROM `information_schema`.`character_sets` ORDER BY `character_set_name` ASC
9. [MOCKED] SELECT * FROM `information_schema`.`collations` WHERE character_set_name = 'utf8mb4' ORDER
   BY `collation_name` ASC

# testcases

```sql
CREATE TABLE demo_table2(c1 string, c2 int, c3 bigint, c4 float, c5 double, c6 timestamp);
INSERT INTO demo_table2(c1, c2, c3, c4, c5, c6) VALUES('01', 1, 1, 1.0, 1.0, 1709534741),('01', 2, 2, 2.0, 2.0, 1709534751),('01', 3, 3, 3.0, 3.0, 1709534761),('01', 4, 4, 4.0, 4.0, 1709534771);
select * from demo_table2;
SELECT c1, c2, sum(c3) OVER w1 AS w1_c3_sum FROM demo_table2 WINDOW w1 AS (PARTITION BY demo_table2.c1 ORDER BY demo_table2.c6 ROWS BETWEEN 2 PRECEDING AND CURRENT ROW);
```

## type testcase
```sql
CREATE TABLE demo_table1(c1 int, c2 int32, c3 smallint, c4 int16, c5 bigint, c6 int64, c7 float, c8 double, c9 timestamp, c10 date, c11 bool, c12 string, c13 varchar);
select * from demo_table1;
```