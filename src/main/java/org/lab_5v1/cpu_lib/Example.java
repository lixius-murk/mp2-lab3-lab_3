package org.lab_5v1.cpu_lib;

import org.lab_5v1.cpu_lib.cpu.CPU;
import org.lab_5v1.cpu_lib.instructions.InstructCode;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;

public class Example {
    public static void main(String[] args) throws InstructionsException {
        CPU cpu = new CPU();
        Executor executor = new Executor(cpu);
        Program program = new Program();
        program.add(InstructCode.INIT, 0, 10);
        program.add(InstructCode.INIT, 1, 20);
        program.add(InstructCode.LD, "a", 0);
        program.add(InstructCode.LD, "b", 1);
        program.add(InstructCode.CMP, "a", "b");
        program.add(InstructCode.JL, 3);

        program.add(InstructCode.ADD, "a","b");
        program.add(InstructCode.ADD, "a","b");
        program.add(InstructCode.ADD, "a","b");
        program.add(InstructCode.PRINT, "a", "b");


        System.out.println(program.getStatistics());
        System.out.println(program.getMaxInstr(2));
        System.out.println(program.getMemoryRange());

        executor.run(program.toArray());
    }
}
