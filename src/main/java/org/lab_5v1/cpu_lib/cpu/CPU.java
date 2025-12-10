package org.lab_5v1.cpu_lib.cpu;

import org.lab_5v1.cpu_lib.handlers.*;
import org.lab_5v1.cpu_lib.instructions.Instructions;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;
import org.lab_5v1.cpu_lib.memory.FlagRegister;
import org.lab_5v1.cpu_lib.memory.IntRegister;
import org.lab_5v1.cpu_lib.memory.Memory;

public class CPU implements ICPU {
    IntRegister r;
    String destReg;
    String sourseReg1;
    String sourseReg2;
    FlagRegister flags;

    Handler handler;
    Memory m;

    private int programCounter;

    public CPU() {
        m = new Memory(1024);
        r = new IntRegister();
        flags = new FlagRegister();
        programCounter = 0;
        Handler memoryHandler = new MemoryHandler();
        Handler oneRegisterHandler = new OneRegisterHandler();
        Handler twoRegistersHandler = new TwoRegistersHandler();
        Handler controlHandler = new ControlHandler();

        memoryHandler.setNext(oneRegisterHandler);
        oneRegisterHandler.setNext(twoRegistersHandler);
        twoRegistersHandler.setNext(controlHandler);

        this.handler = memoryHandler;

    };

    public Handler getHandler() {
        return handler;
    }

    @Override
    public void execute(Instructions instruction) {
        System.out.println(programCounter + " > " + instruction);
        try {
            handler.run(instruction, this);
        } catch (InstructionsException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }


    public int getCount() {
        return programCounter;
    }

    public void setCount(int count) {
        this.programCounter = count;
    }

    public Memory getMemory(){return m;}
    public IntRegister getRegister(){return r;}

    public String getDestReg() { return destReg; }
    public String getSourseReg1() { return sourseReg1; }
    public String getSourseReg2() { return sourseReg2; }
    public void setDestReg(String reg) { this.destReg = reg; }
    public void setSourseReg1(String reg) { this.sourseReg1 = reg; }
    public void setSourseReg2(String reg) { this.sourseReg2 = reg; }


    public FlagRegister getFlags() {
        return flags;
    }

}
