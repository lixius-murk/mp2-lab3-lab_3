package org.lab_5v1.cpu_lib.instructions;

public class InstructionsException extends Exception {
    public InstructionsException(String s) {
        super(s);
    }
    public String getMessage() {
        return "InstructionsException: " + super.getMessage();
    }
}
