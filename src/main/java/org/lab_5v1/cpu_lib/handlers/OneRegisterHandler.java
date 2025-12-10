package org.lab_5v1.cpu_lib.handlers;

import org.lab_5v1.cpu_lib.cpu.CPU;
import org.lab_5v1.cpu_lib.instructions.InstructCode;
import org.lab_5v1.cpu_lib.instructions.Instructions;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;

public class OneRegisterHandler extends Handler {

    @Override
    public boolean canHandle(Instructions instruction) {
        return instruction.getInstructCode()== InstructCode.PRINT;
    }

    @Override
    public void run(Instructions instruction, CPU cpu) throws InstructionsException {
        if (canHandle(instruction)) {
            String register = instruction.getOperand1();
            int value = cpu.getRegister().getValue(register, 0);
            System.out.print(register + "=" + value + " ");


            passToNext(instruction, cpu);
        }
    }
}
