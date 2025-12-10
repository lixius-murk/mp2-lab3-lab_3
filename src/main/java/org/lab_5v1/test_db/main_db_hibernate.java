package org.lab_5v1.test_db;


import org.lab_5v1.InstructionsDAO_Hibernate;
import org.lab_5v1.cpu_lib.instructions.InstructCode;
import org.lab_5v1.cpu_lib.instructions.Instructions;

public class main_db_hibernate {
    public static void main(String[] args) {
        InstructionsDAO_Hibernate dao = new InstructionsDAO_Hibernate();

        Instructions testInstr = new Instructions(InstructCode.INIT, 50, 888);
        dao.add(testInstr);

        for (Instructions instr : dao) {
            System.out.println(instr);
        }

        dao.close();

    }
}