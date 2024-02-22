# openmldb-mysql-server

## Introduction

This is the server of OpenMLDB to be compatible with MySQL client. That means you can use `mysql` command to connect with OpenMLDB and complete any SQL operation.

## Installation

Make sure to use Java 11+ and setup `$JAVA_HOME`. Setup OpenMLDB cluster in advanced.

```
mvn clean package

mvn exec:java -Dexec.mainClass="com._4paradigm.openmldb.OpenmldbMysqlServer"
```

## Clent

Use `mysql` command to connect.

```
mysql -h127.0.0.1 -P3307
```

Run adhoc ANSI SQL or even OpenMLDB SQL.

```
show databases;

insert into db1.t1 values("user1", 10);

select age from db1.t1;

show jobs;

stop job 1;
```

## Related Projects

The code of analysing MySQL Protol are copied from [mysql-protocol](https://github.com/paxoscn/mysql-protocol) which is forked from [netty-mysql-codec](https://github.com/mheath/netty-mysql-codec) .

