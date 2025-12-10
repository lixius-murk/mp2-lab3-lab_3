package org.lab_5v1.cpu_lib.handlers;

import org.lab_5v1.cpu_lib.cpu.CPU;
import org.lab_5v1.cpu_lib.instructions.InstructCode;
import org.lab_5v1.cpu_lib.instructions.Instructions;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;
import org.lab_5v1.cpu_lib.memory.FlagRegister;

public class ControlHandler extends Handler {
    @Override
    public boolean canHandle(Instructions instruction) {
        return (instruction.getInstructCode() == InstructCode.JMP ||
                instruction.getInstructCode() == InstructCode.JE ||
                instruction.getInstructCode() == InstructCode.JL ||
                instruction.getInstructCode() == InstructCode.JG);
    }

    @Override
    public void run(Instructions instruction, CPU cpu) throws InstructionsException {
        if (canHandle(instruction)) {
            switch(instruction.getInstructCode()){
                case JMP -> handleJMP(instruction, cpu);
                case JE -> handleJE(instruction, cpu);
                case JG -> handleJG(instruction, cpu);
                case JL -> handleJL(instruction, cpu);
                default -> throw new InstructionsException("Unknown instruction!");
            }
        }
        else{
            passToNext(instruction, cpu);
        }
    }



    public void handleJMP(Instructions instruction, CPU cpu){
        int address =  Integer.parseInt(instruction.getOperand1());
        cpu.setProgramCounter(address);
    }
    //ZF
    public void handleJE(Instructions instruction, CPU cpu){
        FlagRegister flagRegister = cpu.getFlags();
        int address =  Integer.parseInt(instruction.getOperand1());
        if (flagRegister.isZF()) {
            cpu.setProgramCounter(address);}
    }
    //SF == OF && !(ZF)
    public void handleJG(Instructions instruction, CPU cpu){
        FlagRegister flagRegister = cpu.getFlags();
        int address =  Integer.parseInt(instruction.getOperand1());
        if (flagRegister.isSF() == flagRegister.isOF() && !flagRegister.isZF()) {
            cpu.setProgramCounter(address );}
    }

    //SF!=OF
    public void handleJL(Instructions instruction, CPU cpu){
        FlagRegister flagRegister = cpu.getFlags();
        int address =  Integer.parseInt(instruction.getOperand1());
        if (flagRegister.isSF() != flagRegister.isOF()) {
            cpu.setProgramCounter(address );}
    }
}
