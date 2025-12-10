package org.lab_5v1.cpu_lib.instructions;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "instructions_table")
public class Instructions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "instruction_code", nullable = false)
    private InstructCode type;

    @Column(name = "operand1")
    private String operand1;

    @Column(name = "operand2")
    private String operand2;


    public Instructions() {
        this.type = InstructCode.INIT;
        this.operand1 = new String(" ");
        this.operand2 = new String(" ");

    }

    public Instructions(InstructCode type, String... operands) {
        this.type = type;

        if (operands.length > 0 && operands[0] != null) {
            this.operand1 = operands[0];
        } else {
            this.operand1 = "";
        }

        if (operands.length > 1 && operands[1] != null) {
            this.operand2 = operands[1];
        } else {
            this.operand2 = "";
        }
    }

    /*private Object parseOperand(String str) {
        if (str == null || str.equals("null")) return null;
        try {
            //проверяем числа
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            //проверяем региср
            if (str.matches("[abcd]")) {
                return str;
            }
            return str;
        }
    }*/

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public InstructCode getInstructCode() { return type; }
    public void setInstructCode(InstructCode type) { this.type = type; }

    public String getOperand1() { return operand1; }
    public void setOperand1(String operand1) {
        this.operand1 = operand1;
    }

    public String getOperand2() { return operand2; }
    public void setOperand2(String operand2) {
        this.operand2 = operand2;
    }


    @Override
    public String toString() {
        return /*"Instruction: " +
                "id=" + id +*/
                "{instr=" + type +
                ", op1=" + operand1 +
                ", op2=" + operand2 + "}";
    }

    //иначе не распознаётся сравнение инструкций
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Instructions that = (Instructions) o;
        return  type == that.type && Objects.equals(operand1, that.operand1) && Objects.equals(operand2, that.operand2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, operand1, operand2);
    }
}