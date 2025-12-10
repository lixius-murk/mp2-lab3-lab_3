package org.lab_5v1.model;

import org.lab_5v1.cpu_lib.instructions.InstructionsException;

public interface IObserver {
    void event() throws InstructionsException;
}
