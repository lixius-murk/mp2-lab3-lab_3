package org.lab_5v1.cpu_lib.instructions;

import jakarta.persistence.*;
import java.util.Arrays;

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

    private Object operands;

    // Конструкторы
    public Instructions() {
        this.type = InstructCode.INIT;
        this.operand1 = new String(" ");
        this.operand2 = new String(" ");

    }

    public Instructions(InstructCode type, Object... operands) {
        this.type = type;
        this.operands = operands != null ? operands : new Object[0];
        // Автоматически преобразуем операнды в строки
        if (operands != null && operands.length > 0) {
            this.operand1 = operands[0] != null ? operands[0].toString() : null;
            if (operands.length > 1) {
                this.operand2 = operands[1] != null ? operands[1].toString() : null;
            }
        }
    }

    // Геттер для операндов (ленивая загрузка)
    public Object[] getOperands() {
        if (operands == null) {
            // Если operands не инициализированы, создаем из строк
            int count = 0;
            if (operand1 != null) count++;
            if (operand2 != null) count++;

            operands = new Object[count];
            if (operand1 != null) {
                operands[0] = parseOperand(operand1);
            }
            if (operand2 != null) {
                operands[count > 1 ? 1 : 0] = parseOperand(operand2);
            }
        }
        return operands != null ? operands : new Object[0];
    }

    // Простой парсер операнда
    private Object parseOperand(String str) {
        if (str == null || str.equals("null")) return null;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            // Проверяем, является ли регистром
            if (str.matches("[abcd]")) {
                return str;
            }
            return str;
        }
    }

    // Остальные геттеры и сеттеры
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public InstructCode getType() { return type; }
    public void setType(InstructCode type) { this.type = type; }

    public String getOperand1() { return operand1; }
    public void setOperand1(String operand1) {
        this.operand1 = operand1;
        this.operands = null; // Сбрасываем кэш при изменении
    }

    public String getOperand2() { return operand2; }
    public void setOperand2(String operand2) {
        this.operand2 = operand2;
        this.operands = null; // Сбрасываем кэш при изменении
    }

    public InstructCode getInstructCode() { return type; }

    public void setOperands(Object[] operands) {
        this.operands = operands;
        if (operands != null) {
            this.operand1 = operands.length > 0 && operands[0] != null ? operands[0].toString() : null;
            this.operand2 = operands.length > 1 && operands[1] != null ? operands[1].toString() : null;
        }
    }

    public Object getOperand(int index) throws InstructionsException {
        Object[] ops = getOperands();
        if (index < 0 || index >= ops.length) {
            throw new InstructionsException("Error getting operands");
        }
        return ops[index];
    }

    @Override
    public String toString() {
        return "Instructions{" +
                "id=" + id +
                ", type=" + type +
                ", operands=" + Arrays.toString(getOperands()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructions that = (Instructions) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}