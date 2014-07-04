package ua.samosfator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

class DataBase {
    public void add(int year, String number) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:vstup.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();

            String tableName = "parsed" + year + "_" + number.replace(".", "");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName +
                    " ( name TEXT NOT NULL, city TEXT NOT NULL, " +
                    "passing_score DOUBLE NOT NULL, places INT NOT NULL )");

            for (int i = 0; i < Parser.names.size(); i++) {
                StringBuilder insert = new StringBuilder();
                double pl = Parser.places.get(i);
                if (pl > 0.0D) {
                    insert.append("INSERT INTO ").append(tableName)
                            .append(" (name, city, passing_score, places) ")
                            .append("VALUES (\"").append(Parser.names.get(i)).append("\", \"")
                            .append(Parser.city.get(i)).append("\", ")
                            .append(Parser.passingScore.get(i)).append(", ")
                            .append(pl).append(")");
                    stmt.executeUpdate(insert.toString());
                }
            }
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}