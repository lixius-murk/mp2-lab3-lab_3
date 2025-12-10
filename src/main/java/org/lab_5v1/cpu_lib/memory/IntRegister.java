package org.lab_5v1.cpu_lib.memory;

import org.lab_5v1.cpu_lib.instructions.InstructionsException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IntRegister {
    private final Map<String, int[]> registers;// 4 регистра по 1024 int

    public IntRegister() {
        this.registers = new HashMap<>();
        initRegister("a");
        initRegister("b");
        initRegister("c");
        initRegister("d");
    }
    private void initRegister(String name) {
        registers.put(name, new int[1024]);
    }
    public int[] getRegister(String name) {
        return registers.get(name);
    }

    public int getValue(String register, int element) throws InstructionsException {
        validateElement(register, element);
        return getRegister(register)[element];
    }

    public void setValue(String register, int element, int value) throws InstructionsException {
        validateElement(register, element);
        getRegister(register)[element] = value;
    }

    public void validateElement(String register, int element) throws InstructionsException {
        if ((!Objects.equals(register, "a") &&!Objects.equals(register, "b")&&
                !Objects.equals(register, "c")&&!Objects.equals(register, "d"))|| element > 1024  ){
            throw new InstructionsException("Обращение к несуществующему регистру!");
        }
    }


}