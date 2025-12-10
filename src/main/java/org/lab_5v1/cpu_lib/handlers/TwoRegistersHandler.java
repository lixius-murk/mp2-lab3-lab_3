package org.lab_5v1.cpu_lib.handlers;

import org.lab_5v1.cpu_lib.cpu.CPU;
import org.lab_5v1.cpu_lib.instructions.InstructCode;
import org.lab_5v1.cpu_lib.instructions.Instructions;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;
import org.lab_5v1.cpu_lib.memory.FlagRegister;

public class TwoRegistersHandler extends Handler {

    @Override
    public boolean canHandle(Instructions instruction) {
        InstructCode code = instruction.getInstructCode();
        return code == InstructCode.ADD || code == InstructCode.SUB|| code == InstructCode.MULT || code == InstructCode.MV || code == InstructCode.DIV || code == InstructCode.CMP;
    }

    @Override
    public void run(Instructions instruction, CPU cpu) throws InstructionsException {
        if(canHandle(instruction)){
            switch (instruction.getInstructCode()){
                case MV -> handleMv(instruction, cpu);
                case CMP -> handleCMP(instruction, cpu);
                default -> handleArythm(instruction, cpu);

            }}
        else{passToNext(instruction, cpu);}

    }

    private void handleCMP(Instructions instruction, CPU cpu) throws InstructionsException {
        Object[] operands = instruction.getOperands();
        String source1 = (String) operands[0];
        String source2 = (String) operands[1];

        int val1 = cpu.getRegister().getValue(source1, 0);
        int val2 = cpu.getRegister().getValue(source2, 0);

        int result = val1 - val2;
        FlagRegister flags = cpu.getFlags();
        flags.reset();
        //результат равен 0
        flags.setZF(result == 0);
        //результат отрицательный
        flags.setSF(result < 0);

        // Overflow Flag: проверяем переполнение для 32-битных целых
        boolean overflow = false;
        if (val1 > 0 && val2 < 0 && result < 0) {
            overflow = true; // Положительное - отрицательное = отрицательное
        } else if (val1 < 0 && val2 > 0 && result > 0) {
            overflow = true; // Отрицательное - положительное = положительное
        }
        flags.setOF(overflow);

        cpu.setSourseReg1(source1);
        cpu.setSourseReg2(source2);
    }


    private void handleArythm(Instructions instruction, CPU cpu) throws InstructionsException {
        Object[] operands = instruction.getOperands();

        String dest = (String) operands[0];
        String source = (String) operands[1];
        int val1 = cpu.getRegister().getValue(dest, 0);
        int val2 = cpu.getRegister().getValue(source, 0);

        int res;
        switch (instruction.getInstructCode()) {
            case ADD -> res =  val1 + val2;
            case SUB -> res = val1 - val2;
            case MULT -> res = val1 * val2;
            case DIV -> {
                if (val2 == 0) throw new InstructionsException("Divition by 0!");
                res = val1/val2;
            }
            default -> throw new InstructionsException("Unknown instruction!");
        }

        cpu.getRegister().setValue(dest, 0, res);
        cpu.setDestReg(dest);
        cpu.setSourseReg1(dest);
        cpu.setSourseReg2(source);
    }

    private void handleMv(Instructions instruction, CPU cpu) throws InstructionsException {
        String dest = (String) instruction.getOperand(0);
        String source = (String) instruction.getOperand(1);

        int value = cpu.getRegister().getValue(source, 0);
        cpu.getRegister().setValue(dest, 0, value);

        cpu.setDestReg(dest);
        cpu.setSourseReg1(source);

    }

}

