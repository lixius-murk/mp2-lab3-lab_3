package org.lab_5v1;


import org.lab_5v1.cpu_lib.instructions.InstructCode;
import org.lab_5v1.cpu_lib.instructions.Instructions;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InstructionListDAO_JDBC extends InstructionsListDAO {
    private Connection connection = null;
    private List<Instructions> instructionsList = new ArrayList<>();
    

    public InstructionListDAO_JDBC() {
        connect();
        /*createTableIfNotExists()*/;
        loadFromDatabase();
        
    }

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + "C:\\Users\\felyl\\DB\\instructions_lab");
            System.out.println("Opened database successfully");
            
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver not found: " + ex.getMessage());
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("Couldn't connect to database: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

/*    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS instructions_lab (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "INSTRUCTIONCODE TEXT NOT NULL," +
                "OPERAND1 TEXT," +
                "OPERAND2 TEXT)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("[DAO] Table 'instructions_lab' checked/created successfully");

        } catch (SQLException e) {
            System.out.println("[DAO] Error creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }*/

    private void loadFromDatabase() {
        instructionsList.clear();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM instructions_lab ORDER BY ID")) {

            int loadedCount = 0;
            while (rs.next()) {
                try {
                    String typeStr = rs.getString("INSTRUCTIONCODE");
                    InstructCode type = InstructCode.valueOf(typeStr);

                    String op1 = rs.getString("OPERAND1");
                    String op2 = rs.getString("OPERAND2");

                    Object[] operands;
                    if (op1 != null && op2 != null) {
                        operands = new Object[]{parseOperand(op1), parseOperand(op2)};
                    } else if (op1 != null) {
                        operands = new Object[]{parseOperand(op1)};
                    } else if (op2 != null) {
                        operands = new Object[]{null, parseOperand(op2)};
                    } else {
                        operands = new Object[0];
                    }

                    Instructions instr = new Instructions(type, operands);
                    instructionsList.add(instr);

                    loadedCount++;

                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid instruction code in DB: " + e.getMessage());
                }
            }

            System.out.println("Loaded " + loadedCount + " instructions from DB");

        } catch (SQLException e) {
            System.out.println("Error loading from DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Object parseOperand(String operand) {
        if (operand == null || operand.equals("null")) return null;

        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            return operand;
        }
    }

    @Override
    public void addInstr(Instructions instr) {
        String sql = "INSERT INTO instructions_lab(INSTRUCTIONCODE, OPERAND1, OPERAND2) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, instr.getInstructCode().name());

            Object[] operands = instr.getOperands();
            if (operands.length > 0 && operands[0] != null) {
                pstmt.setString(2, operands[0].toString());
            };

            if (operands.length > 1 && operands[1] != null) {
                pstmt.setString(3, operands[1].toString());
            };

            //добавляем в локальный список (кэш??)
            Instructions newInstr = new Instructions(instr.getInstructCode(), operands);
            instructionsList.add(newInstr);


        } catch (SQLException e) {
            System.out.println("Error adding instruction: " + e.getMessage());
            e.printStackTrace();

        }
    }

    @Override
    public void add(Instructions instruction) {
        try {
            PreparedStatement pst = connection.prepareStatement(
                    "INSERT INTO instructions_lab(INSTRUCTIONCODE, OPERAND1, OPERAND2) VALUES (?, ?, ?)");
            pst.setString(1, instruction.getInstructCode().toString());
            Object[] operands = instruction.getOperands();
            pst.setString(2, operands.length > 0 && operands[0] != null ? operands[0].toString() : null);
            pst.setString(3, operands.length > 1 && operands[1] != null ? operands[1].toString() : null);
            pst.executeUpdate();

            instructionsList.add(instruction);

        } catch (SQLException e) {
            System.out.println("Error adding instruction: " + e.getMessage());
            e.printStackTrace();
        }

    }


    private int getByInd(int index) {
        if (index < 0 || index >= instructionsList.size()) {
            return -1;
        }
        //получаем только(!) одну строку данных, пропуская id строк
        String sql = "SELECT ID FROM instructions_lab ORDER BY ID LIMIT 1 OFFSET ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, index);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            System.out.println("Error getting record ID: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void setInstructionsList(List<Instructions> instructionsList) {
        clearDatabase();
        for (Instructions instr : instructionsList) {
            addInstr(instr);
        }
        loadFromDatabase();
    }

    @Override
    public void remove(int index) {
        if (index >= 0 && index < instructionsList.size()) {
            int recordId = getByInd(index);
            if (recordId > 0) {
                deleteFromDatabase(recordId);
                instructionsList.remove(index);
                System.out.println("Removed by index: " + index);
            }
        } else {
            System.out.println("Couldn't delete index: " + index);
        }
    }

    private void deleteFromDatabase(int id) {
        String sql = "DELETE FROM instructions_lab WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            System.out.println("Deleted instruction with id: " + id);
        } catch (SQLException e) {
            System.out.println("Error deleting instruction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        clearDatabase();
        instructionsList.clear();
    }

    private void clearDatabase() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM instructions_lab");
        } catch (SQLException e) {
            System.out.println("Error cleaning DB: " + e.getMessage());
        }
    }


    @Override
    public List<Instructions> getInstructionsList() {
        return instructionsList;
    }

    @Override
    public int size() {
        return instructionsList.size();
    }

    @Override
    public Instructions get(int index) {
        if (index >= 0 && index < instructionsList.size()) {
            return instructionsList.get(index);
        }
        else {
            throw new RuntimeException("Error getting instr from DB");
        }
    }

    @Override
    public void set(int index, Instructions instruction) {
        if (index >= 0 && index < instructionsList.size()) {
            int recordId = getByInd(index);
            if (recordId > 0) {
                updateInDatabase(recordId, instruction);
                instructionsList.set(index, instruction);
            }
        } else {
            throw new RuntimeException("Error setting instr from DB");        }
    }

    private void updateInDatabase(int id, Instructions instruction) {
        String sql = "UPDATE instructions_lab SET INSTRUCTIONCODE = ?, OPERAND1 = ?, OPERAND2 = ? WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, instruction.getInstructCode().name());

            Object[] operands = instruction.getOperands();
            if (operands.length > 0 && operands[0] != null) {
                pstmt.setString(2, operands[0].toString());
            }

            if (operands.length > 1 && operands[1] != null) {
                pstmt.setString(3, operands[1].toString());
            }

            pstmt.setInt(4, id);

        } catch (SQLException e) {
            System.out.println("Error updating instruction: " + e.getMessage());
        }
    }

    @Override
    public void removenstr(Instructions instr) {
        int index = instructionsList.indexOf(instr);
        if (index != -1) {
            remove(index);
        }
    }

    public Instructions[] toArray(Instructions[] instructions) {
        return instructionsList.toArray(instructions);
    }

    @Override
    public List<Instructions> getInternalList() {
        return instructionsList;
    }

    @Override
    public Iterator<Instructions> iterator() {
        return instructionsList.iterator();
    }

    @Override
    public boolean isEmpty() {
        return instructionsList.isEmpty();
    }

    public Instructions[] toArray() {
        return instructionsList.toArray(new Instructions[0]);
    }

}