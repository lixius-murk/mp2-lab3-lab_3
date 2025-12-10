package org.lab_5v1.model;

import org.lab_5v1.cpu_lib.instructions.InstructionsException;

public class BModel {
    private static Model m;

    public  static Model build() throws InstructionsException {
        if (m == null) {
            m = new Model();
        }
        return m;
    }

    public  void reset() throws InstructionsException {
        m = new Model();
    }

}