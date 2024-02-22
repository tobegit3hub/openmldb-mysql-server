# openmldb-mysql-server

## Introduction

The server for OpenMLDB with mysql client.

## Setup

Make sure to use Java 11 and set `$JAVA_HOME`. Setup OpenMLDB cluster in advance.

```
mvn clean package

mvn exec:java -Dexec.mainClass="com._4paradigm.openmldb.OpenmldbMysqlServer"
```

## Clent

Use `mysql` command to connect.

```
mysql -h127.0.0.1 -P3307
```

Run ANSI SQL or OpenMLDB SQL.

```
show databases;

show jobs;

insert into db1.t1 values("user1", 10);

select age from db1.t1;
```