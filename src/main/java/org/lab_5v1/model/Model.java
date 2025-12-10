
package org.lab_5v1.model;

import org.lab_5v1.InstructionListDAO_JDBC;
import org.lab_5v1.InstructionsDAO_Hibernate;
import org.lab_5v1.InstructionsListDAO;
import org.lab_5v1.cpu_lib.Executor;
import org.lab_5v1.cpu_lib.Program;
import org.lab_5v1.cpu_lib.cpu.CPU;
import org.lab_5v1.cpu_lib.instructions.InstructCode;
import org.lab_5v1.cpu_lib.instructions.Instructions;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;



public class  Model implements Iterable<Instructions> {
    private CPU cpu = new CPU();
    private Executor executor = new Executor(cpu);
    //private List<Instructions> instructionsList = new ArrayList<>();
   // private InstructionsListDAO instructionsList = new InstructionListDAO_JDBC();
    private InstructionsListDAO instructionsList = new InstructionsDAO_Hibernate();

    private Map<Instructions, Integer> instructionCountMap = new HashMap<>();

    private List<IObserver> observers = new ArrayList<>();

    public Model() throws InstructionsException {
        this.instructionsList = new InstructionsDAO_Hibernate();
        loadFromDB();
    }



    public void addObserver(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    public void switchInstructins(int ind1, int ind2) throws InstructionsException {
        if (ind1 == ind2) {
            return;
        }
        if (ind1 < 0 || ind1 >= instructionsList.size() ||
                ind2 < 0 || ind2 >= instructionsList.size()) {
            throw new InstructionsException("Invalid instruction indices for swapping");
        }

        Instructions instr1 = instructionsList.get(ind1);
        Instructions instr2 = instructionsList.get(ind2);

        instructionsList.set(ind1, instr2);
        instructionsList.set(ind2, instr1);


        notifyObservers();

    }

    private void loadFromDB() throws InstructionsException {
        if (instructionsList instanceof InstructionsDAO_Hibernate) {
            ((InstructionsDAO_Hibernate) instructionsList).syncWithDatabase();
        }
        instructionCountMap.clear();
        for (Instructions instr : instructionsList) {
            updateInstructionCount(instr);
        }
        notifyObservers();
    }
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() throws InstructionsException {
        for (IObserver observer : observers) {
            observer.event();
        }
    }
    private void updateInstructionCount(Instructions instr) {
        instructionCountMap.merge(instr, 1, Integer::sum);
    }
    public List<Map.Entry<Instructions, Integer>> getMaxInstr(int amount) {
        instructionCountMap.clear();

        for (Instructions instr : instructionsList) {
            instructionCountMap.merge(instr, 1, Integer::sum);
        }

        return instructionCountMap.entrySet().stream()
                .sorted(Map.Entry.<Instructions, Integer>comparingByValue().reversed())
                .limit(amount)
                .collect(Collectors.toList());
    }

    public void addListener(IObserver observer) {
        addObserver(observer);
    }

    public void removeListener(IObserver observer) {
        removeObserver(observer);
    }


    public int getProgramCounter() {
        return cpu.getProgramCounter();
    }

    public void setProgramCounter(int pc) throws InstructionsException {
        cpu.setProgramCounter(pc);
        notifyObservers();
    }

    public CPU getCPU() {
        return cpu;
    }

    public int getRegisterValue(String registerName) throws InstructionsException {
        return cpu.getRegister().getValue(registerName, 0);
    }

    public Map<String, Integer> getAllRegisterValues() throws InstructionsException {
        Map<String, Integer> registers = new HashMap<>();
        registers.put("a", getRegisterValue("a"));
        registers.put("b", getRegisterValue("b"));
        registers.put("c", getRegisterValue("c"));
        registers.put("d", getRegisterValue("d"));
        return registers;
    }

    public void addInstruction(InstructCode code, String... operands) throws InstructionsException {
        validateInstruction(code, operands);

        Instructions instr = new Instructions(code, operands);
        instructionsList.add(instr);
        instructionCountMap.merge(instr, 1, Integer::sum);

        //cpu.setProgramCounter(0);

        notifyObservers();
    }

    public void removeInstruction(int index) throws InstructionsException {
        if (index >= 0 && index < instructionsList.size()) {
            instructionsList.remove(index);
            notifyObservers();
        }
    }

    private void validateInstruction(InstructCode code, String... operands) throws InstructionsException {
        switch (code) {
            case INIT:
                // INIT address, val
                if (operands.length < 2) {
                    throw new InstructionsException(code + " requires 2 operands: address and value");
                }

                try {
                    int address = Integer.parseInt(operands[0]);
                    if (address < 0 || address >= 1024) {
                        throw new InstructionsException("Address out of range: " + address);
                    }
                    Integer.parseInt(operands[1]);
                } catch (NumberFormatException e) {
                    throw new InstructionsException(code + " operands must be numbers");
                }
                break;

            case LD, ST:
                // LD register, address
                if (operands.length < 2) {
                    throw new InstructionsException(code + "  requires 2 operands: register and address");
                }

                String loadReg = operands[0];
                if (!loadReg.matches("[abcd]")) {
                    throw new InstructionsException("Register must be a,b,c,d, got: " + loadReg);
                }

                try {
                    int loadAddr = Integer.parseInt(operands[1]);
                    if (loadAddr < 0 || loadAddr >= 1024) {
                        throw new InstructionsException(code + " address out of range: " + loadAddr);
                    }
                } catch (NumberFormatException e) {
                    throw new InstructionsException(code + " address must be a number");
                }
                break;
            case DIV, MULT, ADD, SUB, MV:
                if (operands.length != 2) {
                    throw new InstructionsException(code + " Requires 2 operands: register and register/value");
                }

                // оба операнда - регистры
                if (!(operands[0] instanceof String)) {
                    throw new InstructionsException(code + " first operand must be a register (a,b,c,d)");
                }

                String reg1 = (String) operands[0];
                if (!reg1.matches("[abcd]")) {
                    throw new InstructionsException(code + " register must be a,b,c,d, got: " + reg1);
                }
                break;
            case JMP, JG, JE, JL:
                //JMP address
                if (operands.length != 1) {
                    throw new InstructionsException(code + " Requires 1 operand: address");
                }
                if (Integer.parseInt(operands[0]) < 0 || Integer.parseInt(operands[0]) >= 1024) {
                    throw new InstructionsException(code + " address out of range: " + operands[0]);
                }
                break;
            case CMP:
                //CMP reg1, reg2
                if (operands.length != 2) {
                    throw new InstructionsException(code + " Requires 2 operand: reg1, reg2");
                }
                String reg_1 = operands[0];
                if (!reg_1.matches("[abcd]")) {
                    throw new InstructionsException("Register must be a,b,c,d, got: " + reg_1);
                }
                String reg_2 = operands[1];
                if (!reg_2.matches("[abcd]")) {
                    throw new InstructionsException("Register must be a,b,c,d, got: " + reg_2);
                }
                break;
        }
    }
    public Instructions getInstruction(int index) throws InstructionsException {
        if (index >= 0 && index < instructionsList.size()) {
            return instructionsList.get(index);
        }
        return null;
    }

    public void executeNextInstruction() throws InstructionsException {
        int currentPC = cpu.getProgramCounter();

        if (currentPC >= instructionsList.size()) {
            throw new InstructionsException("Program completed");
        }

        Instructions instruction = instructionsList.get(currentPC);

        int savedPC = cpu.getProgramCounter();

        cpu.execute(instruction);

        //если не JMP, увеличиваем pc
        if (cpu.getProgramCounter() == savedPC) {
            cpu.setProgramCounter(currentPC + 1);
        }

        notifyObservers();
    }
    public void executeInstruction(int index) throws InstructionsException {
        int savedPC = cpu.getProgramCounter();
        cpu.setProgramCounter(index);

        if (index >= instructionsList.size()) {
            cpu.setProgramCounter(savedPC);
            throw new InstructionsException("PC out of range");
        }

        Instructions instruction = instructionsList.get(index);

        cpu.execute(instruction);
        if (cpu.getProgramCounter() == index) {
            cpu.setProgramCounter(index + 1);
        }

        notifyObservers();
    }

    public void runProgram() throws InstructionsException {
        executor.run(this.toArray());
        notifyObservers();
    }

    private Instructions[] toArray() {
        return instructionsList.toArray();
    }

    public void resetProgram() throws InstructionsException {
        clearInstructions();
        cpu.setProgramCounter(0);
        notifyObservers();
    }
    public void clearInstructions() throws InstructionsException {
        instructionsList.clear();
        instructionCountMap.clear();
        notifyObservers();
    }

    public void resetCPU() throws InstructionsException {
        this.cpu = new CPU();
        this.executor = new Executor(cpu);
        initializeMemory();
        notifyObservers();
    }



    public int getInstructionCount() {
        return instructionsList.size();
    }

    public boolean isEmpty() {
        return instructionsList.isEmpty();
    }

    @Override
    public Iterator<Instructions> iterator() {
        return instructionsList.iterator();
    }

    public int[] getMemory100() {
        int[] memoryCells = new int[100];
        for (int i = 0; i < 100; i++) {
            memoryCells[i] = getMemoryValue(i);
        }
        return memoryCells;
    }
    private void initializeMemory() {
        for (int i = 0; i < 100; i++) {
            cpu.getMemory().write(i, 0);
        }
    }

    private int getMemoryValue(int i) {
        return cpu.getMemory().read(i);
    }


}

