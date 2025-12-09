package org.lab_3v1.cpu_lib.instructions;

import java.util.Arrays;

public class  Instructions {
    final InstructCode type;
    Object[] operands;
    int id = -1;

    public Instructions(InstructCode type, Object... operands){
        this.type = type;
        this.operands = operands != null ? operands : new Object[0];
        /*        for(Object op: operands) this.operands = new Object[0];*/
    }
    public Instructions(int id, InstructCode type, Object... operands){
        this.type = type;
        this.operands = operands != null ? operands : new Object[0];
        this.id = id;
    }
    public Instructions(int id, String type, Object... operands){
        this.type = toType(type);
        this.operands = operands != null ? operands : new Object[0];
        this.id = id;
    }

    private InstructCode toType(String type) {
        InstructCode c;
        switch(type){
            //TODO: дописать все инструкции
            case "INIT": c = InstructCode.INIT; break;
            case "LD": c = InstructCode.LD; break;
            case "ST": c = InstructCode.ST; break;
            case "MULT": c = InstructCode.MULT; break;
            case "PRINT": c = InstructCode.PRINT; break;
            case "MV": c = InstructCode.MV; break;
            case "ADD": c = InstructCode.ADD; break;
            case "SUB": c = InstructCode.SUB; break;
            case "DIV": c = InstructCode.DIV; break;
            case "JMP": c = InstructCode.JMP; break;
            case "JE": c = InstructCode.JE; break;
            case "JG": c = InstructCode.JG; break;
            case "JL": c = InstructCode.JL; break;
            case "CMP": c = InstructCode.CMP; break;

            default: c = InstructCode.INIT;
        }
        return c;
    }


    public InstructCode getInstructCode() { return type; }
    public Object[] getOperands() { return operands; }
    public Object getOperand(int index) {
        return (index >= 0 && index < operands.length) ? operands[index] : null;
    }

    @Override
    public String toString() {
        return "Instructions{" +
                "type=" + type +
                ", operands=" + Arrays.toString(operands) +
                ", id=" + id +
                '}';
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

