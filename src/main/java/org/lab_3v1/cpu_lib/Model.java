package org.lab_3v1.cpu_lib;

import org.lab_3v1.cpu_lib.cpu.CPU;
import org.lab_3v1.cpu_lib.instructions.InstructCode;
import org.lab_3v1.cpu_lib.instructions.Instructions;
import org.lab_3v1.cpu_lib.instructions.InstructionsException;

import java.util.*;

public class Model implements Iterable<Instructions> {
    private CPU cpu = new CPU();
    private Executor executor = new Executor(cpu);
    private List<Instructions> instructionsList = new ArrayList<>();
    private Map<Instructions, Integer> instructionCountMap = new HashMap<>();

    private List<IObserver> observers = new ArrayList<>();

    public Model() {}


    public void addObserver(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (IObserver observer : observers) {
            observer.event();
        }
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

    public void setProgramCounter(int pc) {
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

    public void addInstruction(InstructCode code, Object... operands) throws InstructionsException {
        Object[] processedOperands = processOperands(code, operands);
        validateRegisters(code, processedOperands);

        Instructions instr = new Instructions(code, processedOperands);
        instructionsList.add(instr);
        instructionCountMap.merge(instr, 1, Integer::sum);

        notifyObservers();
    }

    public void removeInstruction(int index) throws InstructionsException {
        if (index >= 0 && index < instructionsList.size()) {
            instructionsList.remove(index);
            notifyObservers();
        }
    }

    public void clearInstructions() throws InstructionsException {
        instructionsList.clear();
        instructionCountMap.clear();
        notifyObservers();
    }

    public Instructions getInstruction(int index) {
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
        cpu.execute(instruction);

        cpu.setProgramCounter(currentPC + 1);
        notifyObservers();
    }

    public void runProgram() throws InstructionsException {
        executor.run(this.toArray());
        notifyObservers();
    }

    public void resetProgram() {
        cpu.setProgramCounter(0);
        notifyObservers();
    }

    public void resetCPU() {
        this.cpu = new CPU();
        this.executor = new Executor(cpu);
        notifyObservers();
    }


    private Object[] processOperands(InstructCode code, Object[] operands) {
        Object[] res = new Object[operands.length];

        for (int i = 0; i < operands.length; i++) {
            Object operand = operands[i];

            if (operand instanceof Optional) {
                Optional<?> optional = (Optional<?>) operand;
                operand = optional.orElse(null);
            }

            if (operand instanceof String) {
                String str = (String) operand;
                try {
                    res[i] = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    res[i] = str.toLowerCase();
                }
            } else {
                res[i] = operand;
            }
        }
        return res;
    }

    private void validateRegisters(InstructCode code, Object[] operands) throws InstructionsException {
        String[] validRegisters = {"a", "b", "c", "d"};
        Set<String> validSet = Set.of(validRegisters);

        for (Object operand : operands) {
            if (operand instanceof String) {
                String register = (String) operand;
                if (!validSet.contains(register)) {
                    throw new InstructionsException("Invalid register: " + register);
                }
            }
        }
    }


    public Instructions[] toArray() {
        return instructionsList.toArray(new Instructions[0]);
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
}