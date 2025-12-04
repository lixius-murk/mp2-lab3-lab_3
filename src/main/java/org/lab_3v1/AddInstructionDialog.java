package org.lab_3v1;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.lab_3v1.cpu_lib.instructions.InstructCode;
import org.lab_3v1.cpu_lib.instructions.InstructionsException;
import org.lab_3v1.model.BModel;
import org.lab_3v1.model.Model;
import org.lab_3v1.cpu_lib.instructions.Instructions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AddInstructionDialog {

    private InstructCode selectedInstruction;
    private Object[] operands;

    public static Optional<Instructions> showAddInstructionDialog() {
        Dialog<Instructions> dialog = new Dialog<>();
        dialog.setTitle("Add New Instruction");
        dialog.setHeaderText("Select instruction and enter operands");
        dialog.setTitle("Add New Instruction");
        dialog.setHeaderText("Select instruction and enter operands");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        ComboBox<InstructCode> instructionComboBox = new ComboBox<>();
        instructionComboBox.setItems(FXCollections.observableArrayList(InstructCode.values()));
        instructionComboBox.setPromptText("Select instruction");

        TextField operand1Field = new TextField();
        TextField operand2Field = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Instruction:"), 0, 0);
        grid.add(instructionComboBox, 1, 0);
        grid.add(new Label("Operand 1:"), 0, 1);
        grid.add(operand1Field, 1, 1);
        grid.add(new Label("Operand 2:"), 0, 2);
        grid.add(operand2Field, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                InstructCode selectedInstruction = instructionComboBox.getValue();

                //проверка, точно ли выбрана хоть какая-то инструкция
                if (selectedInstruction == null) {
                    alert("Error", "Please select an instruction");
                    return null;
                }

                Object[] operands = parseOperands(operand1Field.getText(), operand2Field.getText());
                return new Instructions(selectedInstruction, operands);
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private static Object[] parseOperands(String op1, String op2) {
        if (op1 == null || op1.trim().isEmpty()) {
            return new Object[0];
        }

        List<Object> operandList = new ArrayList<>();
        operandList.add(parseOperand(op1.trim()));

        if (op2 != null && !op2.trim().isEmpty()) {
            operandList.add(parseOperand(op2.trim()));
        }

        return operandList.toArray();
    }

    private static Object parseOperand(String operand) {
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