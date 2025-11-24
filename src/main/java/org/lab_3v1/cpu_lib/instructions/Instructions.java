package org.lab_3v1.cpu_lib.instructions;

import java.util.Arrays;

public class  Instructions {
    private final InstructCode type;
    private Object[] operands;
    public Instructions(InstructCode type, Object... operands){
        this.type = type;
        this.operands = operands != null ? operands : new Object[0];
        /*        for(Object op: operands) this.operands = new Object[0];*/
    }


    public InstructCode getInstructCode() { return type; }
    public Object[] getOperands() { return operands; }
    public Object getOperand(int index) {
        return (index >= 0 && index < operands.length) ? operands[index] : null;
    }

    @Override
    public String toString() {
        return type.name() + " " + Arrays.toString(operands);
    }
    //переопределим equals и hashcode, чтобы правильно велся подсчет иинструкций
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructions that = (Instructions) o;
        return (type == that.type);

    }
    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        return 31 * result;
    }

}

