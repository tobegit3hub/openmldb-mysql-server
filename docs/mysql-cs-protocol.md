[MySQL Client/Server Protocol](https://dev.mysql.com/doc/dev/mysql-server/latest/PAGE_PROTOCOL.html)

# Overview

The MySQL protocol is used between MySQL Clients and a MySQL Server. It is implemented by:

* Connectors (Connector/C, Connector/J, and so forth)
* MySQL Proxy
* Communication between master and slave replication servers

The protocol supports these features:

* Transparent encryption using SSL
* Transparent compression
* A Connection Phase where capabilities and authentication data are exchanged
* A Command Phase which accepts commands from the client and executes them

# MySQL Packets

If a MySQL client or server wants to send data, it:

* Splits the data into packets of size 224 bytes
* Prepends to each chunk a packet header

## Protocol::Packet

Data between client and server is exchanged in packets of max 16MByte size.

### Payload

| Type          | Name            | Description                                                                                                         |
|---------------|-----------------|---------------------------------------------------------------------------------------------------------------------| 
| int<3>        | payload_length  | Length of the payload. The number of bytes in the packet beyond the initial 4 bytes that make up the packet header. |
| int<1>        | sequence_id     | Sequence ID                                                                                                         |
| string<var>   | payload         | payload of the packet                                                                                               |

### Example:

`01 00 00 00 01`

* length: 1
* sequence_id: 0x00
* payload: 0x01

# Connection Lifecycle

![Connection Lifecycle](/Users/leoyang/Workspace/github/openmldb-mysql-server/docs/connection-lifecycle.png)