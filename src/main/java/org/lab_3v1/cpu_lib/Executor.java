package org.lab_3v1.cpu_lib;

import org.lab_3v1.cpu_lib.cpu.CPU;
import org.lab_3v1.cpu_lib.instructions.Instructions;
import org.lab_3v1.cpu_lib.instructions.InstructionsException;

public class Executor {
    CPU cpu;
    public Executor(CPU cpu){
        this.cpu = cpu;
    }

    public void run(Instructions[] program) throws InstructionsException {
        cpu.setProgramCounter(0);

        while (cpu.getProgramCounter() >= 0 && cpu.getProgramCounter() < program.length) {
            int currentPC = cpu.getProgramCounter();
            Instructions instruction = program[currentPC];
            cpu.execute(instruction);
            if (cpu.getProgramCounter() == currentPC) {
                cpu.setProgramCounter(currentPC + 1);
            }
        }

    }
}

