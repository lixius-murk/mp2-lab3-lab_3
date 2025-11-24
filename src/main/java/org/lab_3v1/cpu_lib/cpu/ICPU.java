package org.lab_3v1.cpu_lib.cpu;

import org.lab_3v1.cpu_lib.instructions.Instructions;

public interface ICPU {
    void execute(Instructions instruction);
}
