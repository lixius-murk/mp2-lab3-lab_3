package org.lab_5v1;

import org.lab_5v1.cpu_lib.instructions.Instructions;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InstructionsListDAO implements Iterable<Instructions> {
    private List<Instructions> instructionsList = new ArrayList<>();


    public void setInstructionsList(List<Instructions> instructionsList) {
        this.instructionsList = instructionsList;
    }
    public void addInstr(Instructions instr){
        instructionsList.add(instr);
    }
    public void removenstr(Instructions instr){
        instructionsList.remove(instr);
    }
    public int size(){
        return instructionsList.size();
    };
    public Instructions get(int ind) throws InstructionsException {
        return instructionsList.get(ind);
    };
    public void set(int ind, Instructions instruction){
        instructionsList.add(ind, instruction);
    };
    public void add(Instructions instruction){
        instructionsList.add(instruction);
    };
    public void remove(int ind){
        instructionsList.remove(ind);
    };
    public void clear() throws InstructionsException {
        instructionsList.clear();
    };
    public Instructions[] toArray() {
        return instructionsList.toArray(new Instructions[0]);
    }
    public List<Instructions> getInstructionsList(){
        return instructionsList.stream().toList();
    }
    //для iterator
    public List<Instructions> getInternalList(){
        return instructionsList.stream().toList();
    };

    @Override
    public Iterator<Instructions> iterator() {
        return getInternalList().iterator();
    }
    public void close() {
    }


        public boolean isEmpty() {
        return instructionsList.isEmpty();
    }
}
