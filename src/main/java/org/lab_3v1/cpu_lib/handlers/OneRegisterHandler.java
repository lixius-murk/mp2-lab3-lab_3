package org.lab_3v1.cpu_lib.handlers;

import org.lab_3v1.cpu_lib.cpu.CPU;
import org.lab_3v1.cpu_lib.instructions.InstructCode;
import org.lab_3v1.cpu_lib.instructions.Instructions;
import org.lab_3v1.cpu_lib.instructions.InstructionsException;

public class OneRegisterHandler extends Handler {

    @Override
    public boolean canHandle(Instructions instruction) {
        return instruction.getInstructCode()== InstructCode.PRINT;
    }

    @Override
    public void run(Instructions instruction, CPU cpu) throws InstructionsException {
        if (canHandle(instruction)) {
            for (Object operand : instruction.getOperands()) {
                String register = (String) operand;
                int value = cpu.getRegister().getValue(register, 0);
                System.out.print(register + "=" + value + " ");
            }
            System.out.println();
        } else {
            passToNext(instruction, cpu);
        }
    }
}
