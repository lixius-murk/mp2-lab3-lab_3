package org.lab_5v1.test_db;

import java.sql.*;

public class main_db_jdbc {
    public static void main(String[] args) throws SQLException {
        try{
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:C:\\Users\\felyl\\DB\\instructions_lab";

            System.out.println("Testing database connection...");
            Connection conn = DriverManager.getConnection(url);

            // Проверяем существование таблицы
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "instructions_list", null);

            if (tables.next()) {
                System.out.println("Table 'instructions_list' exists");

                // Проверяем содержимое
                Statement stmt = conn.createStatement();
                ResultSet r= stmt.executeQuery("select * from instructions_list ");
                while(r.next()){
                    System.out.println("Instruction: " + r.getString("INSTRUCTIONCODE") + ", Operands: " +
                            r.getString("OPERAND1") + " " + r.getString("OPERAND2"));
                }

            } else {
                System.out.println("Table 'instructions_list' does not exist");
            }

            conn.close();
            System.out.println("Database test completed successfully");

    } catch (Exception e) {
        System.err.println("Database test failed: " + e.getMessage());
        e.printStackTrace();
    }
        /*Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\felyl\\git\\lab_5v1\\all_instr.db");
            System.out.println("Opened database successfully");
            Statement st = c.createStatement();
            ResultSet r= st.executeQuery("select * from instructions ");
            while(r.next()){
                System.out.println("Instruction: " + r.getString("instrType") + ", Operands: " +
                        r.getString("instrOperand1") + " " + r.getString("instrOperand2"));
            }
        }
        catch (ClassNotFoundException ex) {
            System.out.println("Diver not found!");
        } catch (SQLException ex) {
            System.out.println("Couldn't connect to SUBD!");
        }*/
    }
}
