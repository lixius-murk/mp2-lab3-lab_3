package org.lab_5v1.cpu_lib.handlers;

import org.lab_5v1.cpu_lib.cpu.CPU;
import org.lab_5v1.cpu_lib.instructions.Instructions;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;

public abstract class Handler {
    protected Handler nextHandler;

    public void setNext(Handler handler) {
        this.nextHandler = handler;
    }

    public abstract boolean canHandle(Instructions instruction);
    public abstract void run(Instructions instruction, CPU cpu) throws InstructionsException;

    protected void passToNext(Instructions instruction, CPU cpu) throws InstructionsException {
        if (nextHandler != null) {
            nextHandler.run(instruction, cpu);
        } else {
            throw new InstructionsException("Unknown instruction: " + instruction);
        }
    }

    public void add(Handler h) {
        nextHandler = h;
    }
}

