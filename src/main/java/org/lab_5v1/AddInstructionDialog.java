package org.lab_5v1;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.lab_5v1.cpu_lib.instructions.InstructCode;
import org.lab_5v1.cpu_lib.instructions.Instructions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddInstructionDialog {

    private ComboBox<InstructCode> instructionComboBox;
    private TextField operand1Field;
    private TextField operand2Field;
    private ComboBox<String> registerComboBox1;
    private ComboBox<String> registerComboBox2;
    private Label operand1Label;
    private Label operand2Label;

    public Optional<Instructions> showAddInstructionDialog() {
        Dialog<Instructions> dialog = new Dialog<>();
        dialog.setTitle("Add New Instruction");
        dialog.setHeaderText("Select instruction and enter operands");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        instructionComboBox = new ComboBox<>();
        instructionComboBox.setItems(FXCollections.observableArrayList(InstructCode.values()));
        instructionComboBox.setPromptText("Select instruction");

        operand1Field = new TextField();
        operand2Field = new TextField();

        //в случае увеличения числа регистров изменить
        registerComboBox1 = new ComboBox<>(FXCollections.observableArrayList("a", "b", "c", "d"));
        registerComboBox1.setPromptText("Select register");
        registerComboBox1.setVisible(false);
        registerComboBox2 = new ComboBox<>(FXCollections.observableArrayList("a", "b", "c", "d"));
        registerComboBox2.setPromptText("Select register");
        registerComboBox2.setVisible(false);

        operand1Label = new Label("Operand 1:");
        operand2Label = new Label("Operand 2:");

        // HBox для операндов
        HBox operand1Box = new HBox(5);
        operand1Box.getChildren().addAll(operand1Field, registerComboBox1);

        HBox operand2Box = new HBox(5);
        operand2Box.getChildren().addAll(operand2Field, registerComboBox2);

        instructionComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateOperandLabels(newVal);
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Instruction:"), 0, 0);
        grid.add(instructionComboBox, 1, 0);
        grid.add(operand1Label, 0, 1);
        grid.add(operand1Box, 1, 1);
        grid.add(operand2Label, 0, 2);
        grid.add(operand2Box, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                InstructCode selectedInstruction = instructionComboBox.getValue();

                if (selectedInstruction == null) {
                    alert("Error", "Please select an instruction");
                    return null;
                }

                List<String> operands = parseOperandsForInstruction(selectedInstruction);
                if (operands == null) {
                    return null;
                }

                return new Instructions(selectedInstruction, operands.get(0), operands.get(1));
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void updateOperandLabels(InstructCode instruction) {
        if (instruction == null) return;

        // сбрасываем значения полей при смене инструкции
        operand1Field.setText("");
        operand2Field.setText("");
        registerComboBox1.setValue(null);
        registerComboBox2.setValue(null);
        operand1Field.setVisible(true);
        operand1Field.setDisable(false);
        operand2Field.setVisible(true);
        operand2Field.setDisable(false);
        registerComboBox1.setVisible(false);
        registerComboBox1.setDisable(false);
        registerComboBox2.setVisible(false);
        registerComboBox2.setDisable(false);

        switch (instruction) {
            case INIT:
                operand1Label.setText("Memory address:");
                operand2Label.setText("Initial value:");
                // Оба поля - числовые значения
                operand1Field.setVisible(true);
                operand2Field.setVisible(true);
                registerComboBox1.setVisible(false);
                registerComboBox2.setVisible(false);
                break;

            case LD:
                operand1Label.setText("Register (dest):");
                operand2Label.setText("Memory address:");
                // Первый операнд - регистр (выпадающий список)
                operand1Field.setVisible(false);
                registerComboBox1.setVisible(true);
                // Второй операнд - адрес памяти (поле ввода)
                operand2Field.setVisible(true);
                registerComboBox2.setVisible(false);
                break;

            case ST:
                operand1Label.setText("Register (src):");
                operand2Label.setText("Memory address:");
                // Первый операнд - регистр
                operand1Field.setVisible(false);
                registerComboBox1.setVisible(true);
                // Второй операнд - адрес памяти
                operand2Field.setVisible(true);
                registerComboBox2.setVisible(false);
                break;

            case DIV:
            case ADD:
            case SUB:
            case MULT:
            case MV:
                operand1Label.setText("Register (dest):");
                operand2Label.setText("Register or value:");
                // Первый операнд - регистр
                operand1Field.setVisible(false);
                registerComboBox1.setVisible(true);
                // Второй операнд - может быть регистром или значением
                operand2Field.setVisible(true);
                registerComboBox2.setVisible(false);
                break;

            case JMP:
            case JG:
            case JE:
            case JL:
                operand1Label.setText("Memory address:");
                operand2Label.setText(""); // Скрываем второй операнд
                operand1Field.setVisible(true);
                operand2Field.setVisible(false);
                registerComboBox1.setVisible(false);
                registerComboBox2.setVisible(false);
                break;

            case CMP:
                operand1Label.setText("Register1:");
                operand2Label.setText("Register2:");
                // Оба операнда - регистры
                operand1Field.setVisible(false);
                operand2Field.setVisible(false);
                registerComboBox1.setVisible(true);
                registerComboBox2.setVisible(true);
                break;

            case PRINT:
                operand1Label.setText("Register or value:");
                operand2Label.setText(""); // Скрываем второй операнд
                operand1Field.setVisible(true);
                operand2Field.setVisible(false);
                registerComboBox1.setVisible(false);
                registerComboBox2.setVisible(false);
                break;

            default:
                operand1Label.setText("Operand 1:");
                operand2Label.setText("Operand 2:");
                operand1Field.setVisible(true);
                operand2Field.setVisible(true);
                registerComboBox1.setVisible(false);
                registerComboBox2.setVisible(false);
                break;
        }
    }

    private List<String> parseOperandsForInstruction(InstructCode instruction) {
        List<String> operands = new ArrayList<>();

        switch (instruction) {
            case INIT:
                // INIT address, value
                String addrStr = operand1Field.getText().trim();
                if (addrStr.isEmpty()) {
                    alert("Error", "Please enter memory address for INIT");
                    return null;
                }
                try {
                    Integer.parseInt(addrStr);
                    operands.add(addrStr);
                } catch (NumberFormatException e) {
                    alert("Error", "INIT address must be a number");
                    return null;
                }

                String valueStr = operand2Field.getText().trim();
                if (valueStr.isEmpty()) {
                    alert("Error", "Please enter initial value for INIT");
                    return null;
                }
                try {
                    Integer.parseInt(valueStr);
                    operands.add(valueStr);
                } catch (NumberFormatException e) {
                    alert("Error", "INIT value must be a number");
                    return null;
                }
                break;

            case LD:
                // LD register, address
                String reg1 = registerComboBox1.getValue();
                if (reg1 == null || reg1.isEmpty()) {
                    alert("Error", "Please select a register for LD");
                    return null;
                }
                operands.add(reg1.toLowerCase());

                String addrStr2 = operand2Field.getText().trim();
                if (addrStr2.isEmpty()) {
                    alert("Error", "Please enter memory address for LD");
                    return null;
                }
                try {
                    Integer.parseInt(addrStr2);
                    operands.add(addrStr2);
                } catch (NumberFormatException e) {
                    alert("Error", "LD address must be a number");
                    return null;
                }
                break;

            case ST:
                // ST register, address
                String reg2 = registerComboBox1.getValue();
                if (reg2 == null || reg2.isEmpty()) {
                    alert("Error", "Please select a register for ST");
                    return null;
                }
                operands.add(reg2.toLowerCase());

                String addrStr3 = operand2Field.getText().trim();
                if (addrStr3.isEmpty()) {
                    alert("Error", "Please enter memory address for ST");
                    return null;
                }
                try {
                    Integer.parseInt(addrStr3);
                    operands.add(addrStr3);
                } catch (NumberFormatException e) {
                    alert("Error", "ST address must be a number");
                    return null;
                }
                break;

            case MV:
            case ADD:
            case SUB:
            case MULT:
            case DIV:
                // instr register, register/number
                String regArith = registerComboBox1.getValue();
                if (regArith == null || regArith.isEmpty()) {
                    alert("Error", "Please select a destination register for " + instruction);
                    return null;
                }
                operands.add(regArith.toLowerCase());

                String op2 = operand2Field.getText().trim();
                if (op2.isEmpty()) {
                    alert("Error", "Please enter value or register for " + instruction);
                    return null;
                }
                operands.add(op2);
                break;

            case JMP:
            case JG:
            case JE:
            case JL:
                // JMP address
                String jumpAddr = operand1Field.getText().trim();
                if (jumpAddr.isEmpty()) {
                    alert("Error", "Please enter memory address for " + instruction);
                    return null;
                }
                try {
                    Integer.parseInt(jumpAddr);
                    operands.add(jumpAddr);
                    // Для JMP-подобных инструкций второй операнд пустой
                    operands.add("");
                } catch (NumberFormatException e) {
                    alert("Error", instruction + " address must be a number");
                    return null;
                }
                break;

            case CMP:
                // CMP reg1, reg2
                String regA = registerComboBox1.getValue();
                String regB = registerComboBox2.getValue();
                if (regA == null || regA.isEmpty() || regB == null || regB.isEmpty()) {
                    alert("Error", "Please select both registers for CMP");
                    return null;
                }
                operands.add(regA.toLowerCase());
                operands.add(regB.toLowerCase());
                break;

            case PRINT:
                // PRINT register or value
                String printOperand = operand1Field.getText().trim();
                if (printOperand.isEmpty()) {
                    alert("Error", "Please enter register or value for PRINT");
                    return null;
                }
                operands.add(printOperand);
                operands.add("");
                break;

            default:
                alert("Error", "Instruction not supported: " + instruction);
                return null;
        }

        return operands;
    }

    private Object parseOperand(String operand) {
        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            return operand.toLowerCase();
        }
    }


    private static void alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}