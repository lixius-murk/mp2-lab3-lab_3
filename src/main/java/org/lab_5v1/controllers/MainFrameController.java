package org.lab_5v1.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.lab_5v1.AddInstructionDialog;
import org.lab_5v1.cpu_lib.instructions.InstructCode;
import org.lab_5v1.cpu_lib.instructions.Instructions;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;
import org.lab_5v1.model.BModel;
import org.lab_5v1.model.IObserver;
import org.lab_5v1.model.Model;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

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
    @FXML
    private Label Lstatistic;


    private Model model;
    private GridPane allInstructions;
    private MemoryFrameController memoryController;

    @FXML
    void initialize() throws InstructionsException {
        model = BModel.build();
        model.addObserver(this);
        allInstructions = new GridPane();

        allInstructions.setVgap(5);
        allInstructions.setHgap(10);
        setRegisterState();
        initializeMem();
        Lstatistic.setText("-");
        ScrollpaneInstructions.setContent(allInstructions);

        BaddInstruction.setOnAction(e -> {
            try {
                addNewInstruction();
            } catch (InstructionsException ex) {
                alert("Add Instruction Error", ex.getMessage());
            }
        });
        BexecuteInstruction.setOnAction(e -> {
            try {
                executeNextInstruction();
            } catch (InstructionsException ex) {
                throw new RuntimeException(ex);
            }
        });
        BresetProgram.setOnAction(e -> resetProgram());
        //example();
        event();
        Bdebug.setOnAction(e -> {
            try {
                showDebugInfo();
            } catch (InstructionsException ex) {
                throw new RuntimeException(ex);
            }
        });

    }

    private void showDebugInfo() throws InstructionsException {
        StringBuilder debugInfo = new StringBuilder();
        debugInfo.append("Model: ").append(model != null ? "OK" : "NULL").append("\n");
        debugInfo.append("CPU: ").append(model != null && model.getCPU() != null ? "OK" : "NULL").append("\n");
        debugInfo.append("PC: ").append(model != null ? model.getProgramCounter() : "N/A").append("\n");
        debugInfo.append("Instructions: ").append(model != null ? model.getInstructionCount() : "N/A").append("\n");

        if (model != null) {
            debugInfo.append("\nInstructions list:\n");
            int i = 0;
            for (Instructions instr : model) {
                debugInfo.append(i++).append(": ").append(instr).append("\n");
            }
        }
        if(model!=null){
            debugInfo.append("\nRegisters\n");
            debugInfo.append("a: " + Integer.toString(model.getRegisterValue("a")) + "\n");
            debugInfo.append("b: " + Integer.toString(model.getRegisterValue("b"))+ "\n");
            debugInfo.append("c: " + Integer.toString(model.getRegisterValue("c"))+ "\n");
            debugInfo.append("d: " + Integer.toString(model.getRegisterValue("d"))+ "\n");
            debugInfo.append("flags: " + (model.getCPU().getFlags()).toString()+ "\n");

        }

        alert("Debug Info", debugInfo.toString());
    }

    private void example() throws InstructionsException {
        try {
            model.addInstruction(InstructCode.INIT, "10", "100");
            model.addInstruction(InstructCode.INIT, "11", "200");
            model.addInstruction(InstructCode.LD, "a", "10");
            model.addInstruction(InstructCode.LD, "b", "11");
            model.addInstruction(InstructCode.CMP, "a", "b");
            model.addInstruction(InstructCode.JL, "7");
            model.addInstruction(InstructCode.PRINT, "a");
            model.addInstruction(InstructCode.PRINT, "b");


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
            java.net.URL url = getClass().getResource("/org/lab_5v1/MemoryFrame.fxml");

            if (url == null) {
                System.err.println("[MainFrameController] Cannot find MemoryFrame.fxml");
                return;
            }

            FXMLLoader memoryLoader = new FXMLLoader(url);
            Pane memoryPane = memoryLoader.load();

            this.memoryController = memoryLoader.getController();
            this.memoryController.setModel(model);

            FlowPaneMem.getChildren().clear();
            FlowPaneMem.getChildren().add(memoryPane);
            memoryPane.getProperties().put("controller", memoryController);

        } catch (IOException e) {
            alert("Memory Frame Error", e.getMessage());
        } catch (InstructionsException e) {
            System.err.println("[MainFrameController] Error setting model to memory controller: " + e.getMessage());
        }
    }

    private void setRegisterState() {
        try {
            java.net.URL url = getClass().getResource("/org/lab_5v1/RegisterFrame.fxml");
            if (url == null) {
                throw new IOException("Cannot find RegisterFrame.fxml at /org/lab_5v1/RegisterFrame.fxml");
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
            event();
        } catch (InstructionsException e) {
            alert("Execution Error", e.getMessage());
        }

    }

    private void executeNextInstruction() throws InstructionsException {
        try {
            int currentPC = model.getCPU().getProgramCounter();


            if (model.getInstructionCount() == 0) {
                alert("Program is empty", "nothing to execute");
            }

            if (currentPC >= model.getInstructionCount()) {
                alert("Program is completed", "nothing to execute");
            }

            Instructions instruction = model.getInstruction(currentPC);


            int savedPC = model.getCPU().getProgramCounter();

            model.getCPU().execute(instruction);


            //если не jmp,  увеличиваем pc
            if (model.getCPU().getProgramCounter() == savedPC) {
                model.getCPU().setProgramCounter(currentPC + 1);
            }
            event();

        } catch (Exception e) {
            //не увеличиваем pc при ошибке
            throw new InstructionsException("Failed to execute instruction: " + e.getMessage());
        }

    }

    private void addNewInstruction() throws InstructionsException {
        AddInstructionDialog dialog = new AddInstructionDialog();
        Optional<Instructions> result = dialog.showAddInstructionDialog();

        if (result.isPresent()) {
            Instructions instruction = result.get();
            model.addInstruction(instruction.getInstructCode(), instruction.getOperand1(), instruction.getOperand2());
        }
        event();
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
                java.net.URL url = getClass().getResource("/org/lab_5v1/InstructionFrame.fxml");

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

//    private void getStatistics() {
//         Lstatistic.setText(model.getMaxInstr(3).toString());
//    }
    private void getStatistics() {
        List<Map.Entry<Instructions, Integer>> topInstructions = model.getMaxInstr(3);

        if (topInstructions.isEmpty()) {
            Lstatistic.setText("No instructions");
            return;
        }

        StringJoiner sj = new StringJoiner("\n");

        for (Map.Entry<Instructions, Integer> entry : topInstructions) {
            Instructions instr = entry.getKey();
            sj.add(String.format("%s %s %s: %d",
                    instr.getInstructCode(),
                    instr.getOperand1(),
                    instr.getOperand2(),
                    entry.getValue()));
        }

        Lstatistic.setText(sj.toString());
    }

    @Override
    public void event() {
        upgradeInstructions();
        highlightCurInstruction();

        if (memoryController != null) {
            try {
                memoryController.event();
            } catch (Exception e) {
                System.err.println("Error updating memory: " + e.getMessage());
            }
        } else {
            //если начало программы - загружаем контроллер
            setMemoryState();
        }
        getStatistics();
    }

    //память и регистры обновляются через своих наблюдателей
};
