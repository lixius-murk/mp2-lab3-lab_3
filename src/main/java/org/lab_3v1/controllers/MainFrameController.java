package org.lab_3v1.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.lab_3v1.AddInstructionDialog;
import org.lab_3v1.cpu_lib.instructions.InstructCode;
import org.lab_3v1.cpu_lib.instructions.Instructions;
import org.lab_3v1.cpu_lib.instructions.InstructionsException;
import org.lab_3v1.model.BModel;
import org.lab_3v1.model.IObserver;
import org.lab_3v1.model.Model;

import java.io.IOException;
import java.util.Optional;

public class MainFrameController implements IObserver {
    @FXML
    private ScrollPane ScrollpaneInstructions;
    @FXML
    private Pane PaneRegisterState;
    @FXML
    private FlowPane FlowPaneMem;
    @FXML
    private Button BaddInstruction;
    @FXML
    private Button BexecuteInstruction;
    @FXML
    private Button BresetProgram;
    @FXML
    private Button Bdebug;



    private Model model;
    private GridPane allInstructions;
    private AddInstructionDialog Dadd;


    @FXML
    void initialize() throws InstructionsException {
        model = BModel.build();
        model.addObserver(this);
        allInstructions = new GridPane();

        allInstructions.setVgap(5);
        allInstructions.setHgap(10);
        setRegisterState();
        initializeMem(); // Initialize memory display directly
        ScrollpaneInstructions.setContent(allInstructions);

        BaddInstruction.setOnAction(e -> {
            try {
                addNewInstruction();
            } catch (InstructionsException ex) {
                alert("Add Instruction Error", ex.getMessage());
            }
        });
        BexecuteInstruction.setOnAction(e -> executeNextInstruction());
        BresetProgram.setOnAction(e -> resetProgram());
        example();
        event();
        Bdebug.setOnAction(e -> showDebugInfo());

    }

    private void showDebugInfo() {
        StringBuilder debugInfo = new StringBuilder();
        debugInfo.append("=== DEBUG INFO ===\n");
        debugInfo.append("Model: ").append(model != null ? "OK" : "NULL").append("\n");
        debugInfo.append("CPU: ").append(model != null && model.getCPU() != null ? "OK" : "NULL").append("\n");
        debugInfo.append("PC: ").append(model != null ? model.getProgramCounter() : "N/A").append("\n");
        debugInfo.append("Instructions: ").append(model != null ? model.getInstructionCount() : "N/A").append("\n");

        alert("Debug Info", debugInfo.toString());
    }
    private void example() throws InstructionsException {
        try {
            model.addInstruction(InstructCode.INIT, 10, 100);
            model.addInstruction(InstructCode.INIT, 11, 200);
            System.out.println("Added test instructions");
        } catch (InstructionsException e) {
            e.printStackTrace();
        }
    }

    private void initializeMem() {
        FlowPaneMem.setHgap(5);
        FlowPaneMem.setVgap(5);
        FlowPaneMem.setPadding(new javafx.geometry.Insets(5));

        setMemoryState();
    }

    private void setMemoryState() {
        try {
            java.net.URL url = getClass().getResource("/org/lab_3v1/MemoryFrame.fxml");

            if (url == null) {
                return;
            }
            FXMLLoader memoryLoader = new FXMLLoader(url);
            Pane memoryPane = memoryLoader.load();

            MemoryFrameController memoryController = memoryLoader.getController();
            memoryController.setModel(model);

            FlowPaneMem.getChildren().clear();
            FlowPaneMem.getChildren().add(memoryPane);

        } catch (IOException e) {
            alert("Memory Frame Error", e.getMessage());
        } catch (InstructionsException e) {
            throw new RuntimeException(e);
        }
    }

    private void setRegisterState() {
        try {
            java.net.URL url = getClass().getResource("/org/lab_3v1/RegisterFrame.fxml");
            if (url == null) {
                throw new IOException("Cannot find RegisterFrame.fxml at /org/lab_3v1/RegisterFrame.fxml");
            }

            FXMLLoader registerLoader = new FXMLLoader(url);
            Pane registerPane = registerLoader.load();

            RegisterFrameController registerController = registerLoader.getController();
            registerController.setModel(model);

            PaneRegisterState.getChildren().clear();
            PaneRegisterState.getChildren().add(registerPane);

        } catch (IOException e) {
            alert("Register Frame Error", e.getMessage());
        }
    }

    private void resetProgram() {
        try {
            model.resetProgram();
        } catch (InstructionsException e) {
            alert("Execution Error", e.getMessage());
        }
    }

    private void executeNextInstruction() {
        try {
            model.executeNextInstruction();
        } catch (InstructionsException e) {
            alert("Execution Error", e.getMessage());
        }
    }

    private void addNewInstruction() throws InstructionsException {
        AddInstructionDialog dialog = new AddInstructionDialog();
        Optional<Instructions> result = dialog.showAddInstructionDialog();

        if (result.isPresent()) {
            Instructions instruction = result.get();
            model.addInstruction(instruction.getInstructCode(), instruction.getOperands());
        }
    }

    private void alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void highlightCurInstruction() {
        int pc = model.getProgramCounter();
        for (int i = 0; i < allInstructions.getChildren().size(); i++) {
            Pane pane = (Pane) allInstructions.getChildren().get(i);
            pane.setStyle("-fx-background-color: white;");
        }
        if (pc >= 0 && pc < allInstructions.getChildren().size()) {
            Pane pane = (Pane) allInstructions.getChildren().get(pc);
            pane.setStyle("-fx-background-color: OLIVEDRAB;");
        }
    }

    private void upgradeInstructions() {
        allInstructions.getChildren().clear();

        int row = 0;
        for (Instructions instruction : model) {
            try {
                java.net.URL url = getClass().getResource("/org/lab_3v1/InstructionFrame.fxml");

                if (url == null) {
                    throw new IOException("Cannot find InstructionFrame.fxml");
                }

                FXMLLoader loader = new FXMLLoader(url);
                Pane pane = loader.load();
                InstructionFrameController controller = loader.getController();
                controller.setModel(model);
                controller.setInstruction(instruction, row);
                allInstructions.addRow(row, pane);
                row++;

            } catch (IOException e) {
                alert("Instruction list issue", "try re-adding instructions");
                e.printStackTrace();
            } catch (InstructionsException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void event() {
        upgradeInstructions();
        highlightCurInstruction();
        //память и регистры обновляются через своих наблюдателей
    }
}