package org.lab_3v1.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.lab_3v1.cpu_lib.instructions.InstructionsException;
import org.lab_3v1.model.BModel;
import org.lab_3v1.model.IObserver;
import org.lab_3v1.model.Model;

public class RegisterFrameController implements IObserver {
    @FXML 
    private Label LregA;
    @FXML 
    private Label LregB;
    @FXML 
    private Label LregC;
    @FXML 
    private Label LregD;
    private Model model;

    public void setModel(Model model) {
        this.model = model;
        this.model.addObserver(this);
        try {
            event();
        } catch (InstructionsException e) {
            e.printStackTrace();
        }    }
/*
    @FXML
    void initialize() throws InstructionsException {
        model.addObserver(this);
        event();
    }
*/

    @Override
    public void event() throws InstructionsException {
        updateRegisters();

    }

    private void updateRegisters() throws InstructionsException {
        int valueA = model.getCPU().getRegister().getValue("a", 0);
        int valueB = model.getCPU().getRegister().getValue("b", 0);
        int valueC = model.getCPU().getRegister().getValue("c", 0);
        int valueD = model.getCPU().getRegister().getValue("d", 0);

        LregA.setText(Integer.toString(valueA));
        LregB.setText(Integer.toString(valueB));
        LregC.setText(Integer.toString(valueC));
        LregD.setText(Integer.toString(valueD));
    }
}
