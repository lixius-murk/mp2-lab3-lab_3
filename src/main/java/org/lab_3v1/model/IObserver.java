package org.lab_3v1.model;

import org.lab_3v1.cpu_lib.instructions.InstructionsException;

public interface IObserver {
    void event() throws InstructionsException;
}
