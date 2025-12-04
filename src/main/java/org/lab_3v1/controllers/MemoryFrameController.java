package org.lab_3v1.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.lab_3v1.cpu_lib.instructions.InstructionsException;
import org.lab_3v1.model.IObserver;
import org.lab_3v1.model.Model;

public class MemoryFrameController implements IObserver {
    @FXML
    private FlowPane FlowPaneMem;

    private Model model;

    public void setModel(Model model) throws InstructionsException {
        this.model = model;
        this.model.addObserver(this);
        event(); // Initial update
    }

    @FXML
    void initialize() {
        // Инициализация FlowPane
        FlowPaneMem.setHgap(5);
        FlowPaneMem.setVgap(5);
        FlowPaneMem.setPadding(new javafx.geometry.Insets(5));
    }

    @Override
    public void event() throws InstructionsException {
        updateMemory();
    }

    private void updateMemory() {
        FlowPaneMem.getChildren().clear();

        if (model == null || model.getCPU() == null || model.getCPU().getMemory() == null) {
            System.out.println("Memory display: model or CPU memory is null");
            return;
        }

        for (int i = 0; i < 100; i++) {
            int value = model.getCPU().getMemory().read(i);
            VBox memoryCell = createMemoryCell(i, value);
            FlowPaneMem.getChildren().add(memoryCell);
        }
    }

    private VBox createMemoryCell(int address, int value) {
        VBox cell = new VBox(2);
        cell.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 3; -fx-padding: 5;");
        cell.setPrefSize(50, 50);

        Label addressLabel = new Label(String.format("[%02d]", address));
        addressLabel.setFont(Font.font(10));
        addressLabel.setStyle("-fx-text-fill: #666666;");

        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.setFont(Font.font(12));
        valueLabel.setStyle("-fx-font-weight: bold;");

        cell.getChildren().addAll(addressLabel, valueLabel);
        return cell;
    }
}
