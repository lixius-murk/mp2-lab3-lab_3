package org.lab_3v1.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import org.lab_3v1.cpu_lib.instructions.Instructions;
import org.lab_3v1.cpu_lib.instructions.InstructionsException;
import org.lab_3v1.model.BModel;
import org.lab_3v1.model.IObserver;
import org.lab_3v1.model.Model;

public class InstructionFrameController implements IObserver {
    @FXML
    Label Linstructions;
    @FXML
    Label Lop1;
    @FXML
    Label Lop2;
    @FXML
    Button Bdelete;
    @FXML
    private Button Bnext;
    @FXML
    private Button Bprev;
    @FXML
    private Button Bexecute;

    private Model model;
    private Instructions instruction;
    private int index;


    public void setInstruction(Instructions instruction, int row) throws InstructionsException {
        this.instruction = instruction;
        this.index = row;
        event();
    }

    @FXML
    void initialize() {
        Bnext.setOnAction(event -> {
            try {
                // Устанавливаем PC на следующий индекс
                model.setProgramCounter(model.getProgramCounter() + 1);
                System.out.println("[InstructionFrame] Next clicked, PC set to: " + model.getProgramCounter());
            } catch (InstructionsException e) {
                System.err.println("[ERROR] " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, e.getMessage(), ButtonType.OK);
                alert.setHeaderText("Issue setting PC");
                alert.showAndWait();
            }
        });

        Bprev.setOnAction(event -> {
            try {
                if (model.getProgramCounter() > 0) {
                    model.setProgramCounter(model.getProgramCounter() - 1);
                    System.out.println("[InstructionFrame] Prev clicked, PC set to: " + model.getProgramCounter());
                }
            } catch (InstructionsException e) {
                System.err.println("[ERROR] " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, e.getMessage(), ButtonType.OK);
                alert.setHeaderText("Issue setting PC");
                alert.showAndWait();
            }
        });

        Bexecute.setOnAction(event -> {
            try {
                System.out.println("[InstructionFrame] Execute clicked for index: " + index);
                model.executeInstruction(index);
                System.out.println("[InstructionFrame] After execution, PC = " + model.getProgramCounter());
            } catch (InstructionsException e) {
                System.err.println("[ERROR] " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, e.getMessage(), ButtonType.OK);
                alert.setHeaderText("Issue executing instruction");
                alert.showAndWait();
            }
        });

    }


    public Instructions getInstruction() {
        return instruction;
    }
    public int getIndex() {
        return index;
    }

    @Override
    public void event() throws InstructionsException {
        if (instruction != null) {

            Linstructions.setText(instruction.getInstructCode().name());
            Object[] operands = instruction.getOperands();
            if (operands.length > 0) {
                Lop1.setText(String.valueOf(operands[0]));
            } else {
                Lop1.setText("");
            }

            if (operands.length > 1) {
                Lop2.setText(String.valueOf(operands[1]));
            } else {
                Lop2.setText("");
            }

        }

    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
}
