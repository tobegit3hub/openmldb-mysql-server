package com.tobe.examples;

import cn.paxos.mysql.MySqlListener;
import cn.paxos.mysql.ResultSetWriter;
import cn.paxos.mysql.engine.QueryResultColumn;
import cn.paxos.mysql.engine.SqlEngine;
import cn.paxos.mysql.util.SHAUtils;
import cn.paxos.mysql.util.Utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.List;

public class OpenmldbMysqlServer {

    // mysql -h127.0.0.1 -P3307 -ugithub -p123456 dummy_db

    public static void main( String[] args ) {

        System.out.println("Hello World!");

        try {
            test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test() throws Exception {
        int port = 3307;
        // Preset database name
        String database = "dummy_db";
        // Preset user name
        String user = "github";
        // Preset password
        String password = "123456";

        // Start the server
        new MySqlListener(port, 100, new SqlEngine() {

            // Implement the authentication
            @Override
            public void authenticate(String database, String userName, byte[] scramble411, byte[] authSeed) throws IOException {
                // Print useful information
                System.out.println("Database: " + database + ", User: " + userName);

                // Check if the password is valid
                authenticateSimply(database, userName, scramble411, authSeed);
            }

            @Override
            public void query(ResultSetWriter resultSetWriter, String database, String userName, byte[] scramble411, byte[] authSeed, String sql) throws IOException {
                // Print useful information
                System.out.println("Database: " + database + ", User: " + userName + ", SQL: " + sql);

                // Check if the password is valid
                authenticateSimply(database, userName, scramble411, authSeed);

                // Build and respond columns
                List<QueryResultColumn> columns = List.of(
                        new QueryResultColumn("col1", "varchar(255)"));
                resultSetWriter.writeColumns(columns);

                // Build and respond rows
                List<String> row = List.of("Hello World !");
                resultSetWriter.writeRow(row);

                // Finish the response
                resultSetWriter.finish();
            }

            // Just check if the password equal to the preset
            private void authenticateSimply(String database, String userName, byte[] scramble411, byte[] authSeed) throws IOException {
                // SHA1 and encode the password
                String validPasswordSha1 = SHAUtils.SHA(password, SHAUtils.SHA_1);
                String validScramble411WithSeed20 = Utils.scramble411(validPasswordSha1, authSeed);

                // Use utils to compare the password
                if (!Utils.compareDigest(validScramble411WithSeed20, Base64.getEncoder().encodeToString(scramble411))) {
                    // Throw an exception if the checking failed
                    throw new IOException(new IllegalAccessException("Authentication failed: Digest validation failed"));
                }
            }
        });


        Thread.sleep(1000L * 60 * 10);


        /*
        // Raise a connection to the server
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:" + port + "/" + database, user,  password);
             // Query an arbitrary SQL
             PreparedStatement ps = conn.prepareStatement("select * from dummy_table");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Check the result
                //assertEquals("Hello World !", rs.getString(1));
                System.out.println(rs.getString(1));
            }
        }

         */



    }
}
