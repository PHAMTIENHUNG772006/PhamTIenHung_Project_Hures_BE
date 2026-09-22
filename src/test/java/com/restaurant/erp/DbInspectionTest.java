package com.restaurant.erp;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbInspectionTest {

    @Test
    void checkUserBranch() throws Exception {
        String url = "jdbc:postgresql://aws-0-ap-southeast-2.pooler.supabase.com:5432/postgres?sslmode=require";
        String user = "postgres.cqebkpudxybsalxfjbyi";
        String pass = "Admin77@xyz.com";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery("SELECT id, username, branch_id, role, status FROM users")) {
                while (rs.next()) {
                    System.out.println("USER: id=" + rs.getObject("id") + ", username=" + rs.getString("username") + ", branch=" + rs.getObject("branch_id"));
                }
            }
        }
    }
}
