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
        Bdelete.setOnAction(event -> {
            try {
                model.removeInstruction(index);
            } catch (InstructionsException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Issue removing instruction", ButtonType.OK);
                alert.setHeaderText("Issue removing instruction");
                alert.showAndWait();            }
        });
        Bnext.setOnAction(event -> {
            try {
                model.setProgramCounter(index++);
            } catch (InstructionsException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Issue removing instruction", ButtonType.OK);
                alert.setHeaderText("Issue removing instruction");
                alert.showAndWait();            }
        });
        Bprev.setOnAction(event -> {
            try {
                model.setProgramCounter(index--);
            } catch (InstructionsException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Issue removing instruction", ButtonType.OK);
                alert.setHeaderText("Issue removing instruction");
                alert.showAndWait();            }
        });
        Bexecute.setOnAction(event -> {
            try {
                model.executeInstruction(index);
            } catch (InstructionsException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Issue removing instruction", ButtonType.OK);
                alert.setHeaderText("Issue removing instruction");
                alert.showAndWait();            }
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
