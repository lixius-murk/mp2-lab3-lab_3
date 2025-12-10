package org.lab_5v1.cpu_lib.handlers;

import org.lab_5v1.cpu_lib.cpu.CPU;
import org.lab_5v1.cpu_lib.instructions.InstructCode;
import org.lab_5v1.cpu_lib.instructions.Instructions;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;

public class MemoryHandler extends Handler {

    @Override
    public boolean canHandle(Instructions instruction) {
        InstructCode code = instruction.getInstructCode();
        return code == InstructCode.LD || code == InstructCode.INIT || code == InstructCode.ST;
    }

    @Override
    public void run(Instructions instruction, CPU cpu) throws InstructionsException {
        if(canHandle(instruction)){
            switch (instruction.getInstructCode()){
                case LD -> handleLoad(instruction, cpu);
                case ST -> handleStore(instruction, cpu);
                case INIT -> handleInit(instruction, cpu);
            }}
        else{passToNext(instruction, cpu);}

    }

    private void handleInit(Instructions instruction, CPU cpu) throws InstructionsException {

        int address = (Integer) instruction.getOperand(0);
        int value = (Integer) instruction.getOperand(1);

        cpu.getMemory().write(address, value);
    }

    private void handleStore(Instructions instruction, CPU cpu) throws InstructionsException {
        String register = (String) instruction.getOperand(0);
        int address = (Integer) instruction.getOperand(1);
        int value = cpu.getRegister().getValue(register, 0);
        cpu.getMemory().write(address, value);
    }

    private void handleLoad(Instructions instruction, CPU cpu) throws InstructionsException {
        String register = (String) instruction.getOperand(0);
        int address = (Integer) instruction.getOperand(1);
        int value = cpu.getMemory().read(address);
        cpu.getRegister().setValue(register, 0, value);
        cpu.setSourseReg1(register);
    }
}
