package com.restaurant.erp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbSchemaChecker {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://aws-0-ap-southeast-2.pooler.supabase.com:5432/postgres?sslmode=require";
        String user = "postgres.cqebkpudxybsalxfjbyi";
        String pass = "Admin77@xyz.com";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            System.out.println("=== TABLES IN PUBLIC SCHEMA ===");
            ResultSet rs = stmt.executeQuery(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' ORDER BY table_name"
            );
            while (rs.next()) {
                System.out.println("Table: " + rs.getString("table_name"));
            }

            System.out.println("\n=== COLUMNS IN USERS TABLE ===");
            rs = stmt.executeQuery(
                "SELECT column_name, data_type, is_nullable FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'users' ORDER BY ordinal_position"
            );
            while (rs.next()) {
                System.out.printf("  %s (%s, nullable=%s)\n",
                    rs.getString("column_name"),
                    rs.getString("data_type"),
                    rs.getString("is_nullable")
                );
            }

            System.out.println("\n=== FLYWAY SCHEMA HISTORY ===");
            rs = stmt.executeQuery(
                "SELECT installed_rank, version, description, success FROM flyway_schema_history ORDER BY installed_rank"
            );
            while (rs.next()) {
                System.out.printf("  Rank %d: v%s - %s (success=%s)\n",
                    rs.getInt("installed_rank"),
                    rs.getString("version"),
                    rs.getString("description"),
                    rs.getBoolean("success")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
