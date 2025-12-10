package org.lab_5v1.cpu_lib;

import org.lab_5v1.InstructionsDAO_Hibernate;
import org.lab_5v1.InstructionsListDAO;
import org.lab_5v1.cpu_lib.instructions.InstructCode;
import org.lab_5v1.cpu_lib.instructions.Instructions;
import org.lab_5v1.cpu_lib.instructions.InstructionsException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Program implements Iterable<Instructions> {

    private List<Instructions> instructionsList;
    private Map<Instructions, Integer> instructionCountMap;
    private InstructionsListDAO dao;

    public Program() {
        this.instructionsList = new ArrayList<>();
        this.instructionCountMap = new HashMap<>();
        this.dao = new InstructionsListDAO();
        loadFromDatabase();
    }

    // Загрузить из БД
    private void loadFromDatabase() {
        instructionsList.clear();
        instructionCountMap.clear();

        List<Instructions> dbInstructions = dao.getInstructionsList();
        for (Instructions instr : dbInstructions) {
            instructionsList.add(instr);
            instructionCountMap.merge(instr, 1, Integer::sum);
        }
    }

    public void saveToDatabase() throws InstructionsException {
        dao.clear();
        dao.setInstructionsList(instructionsList);
    }

    public void add(InstructCode code, String... operands) {
        Instructions instr = new Instructions(code, operands);
        instructionsList.add(instr);
        instructionCountMap.merge(instr, 1, Integer::sum);
        dao.setInstructionsList(instructionsList);
    }

    // Очистить программу
    public void clear() throws InstructionsException {
        instructionsList.clear();
        instructionCountMap.clear();
        dao.clear();
    }

    public Instructions[] toArray() {
        return instructionsList.toArray(new Instructions[0]);
    }

    @Override
    public Iterator<Instructions> iterator() {
        return instructionsList.iterator();
    }

    public int length() {
        return instructionsList.size();
    }

    public Instructions get(int ind) {
        return instructionsList.get(ind);
    }


    public void close() {
        dao.close();
    }

    public ListIterator<Instructions> listIterator() {
        return instructionsList.listIterator();
    }

    public ListIterator<Instructions> listIterator(int index) {
        return instructionsList.listIterator(index);
    }

    public Spliterator<Instructions> spliterator() {
        return instructionsList.spliterator();
    }

    public static class MemoryRange {
        private final int minAddress;
        private final int maxAddress;
        public MemoryRange(int minAddress, int maxAddress) {
            this.minAddress = minAddress;
            this.maxAddress = maxAddress;
        }
        public int getMinAddress() {
            return minAddress;
        }
        public int getMaxAddress() {
            return maxAddress;
        }
        public int getRangeSize() {
            return maxAddress - minAddress + 1;
        }
        @Override
        public String toString() {
            return "[" + minAddress + " - " + maxAddress + "]";
        }
    }


    public List<Map.Entry<Instructions, Integer>> getMaxInstr(int amount){
        return instructionCountMap.entrySet().stream()
                .sorted(Map.Entry.<Instructions, Integer>comparingByValue().reversed())
                .limit(amount)
                .collect(Collectors.toList());
    }
    public List<Map.Entry<Instructions, Integer>> getStatistics(){
        return instructionCountMap.entrySet().stream()
                .sorted(Map.Entry.<Instructions, Integer>comparingByValue())
                .collect(Collectors.toList());
    }
    public Optional<MemoryRange> getMemoryRange() {
        Set<Integer> memoryAddresses = instructionsList.stream()
                .flatMap(this::extractMemoryAddressesFromInstruction)
                .collect(Collectors.toSet());

        if (memoryAddresses.isEmpty()) {
            return Optional.empty();
        }

        int minAddress = memoryAddresses.stream()
                .mapToInt(Integer::intValue)
                .min()
                .getAsInt();

        int maxAddress = memoryAddresses.stream()
                .mapToInt(Integer::intValue)
                .max()
                .getAsInt();

        return Optional.of(new MemoryRange(minAddress, maxAddress));
    }

    private Stream<Integer> extractMemoryAddressesFromInstruction(Instructions instructions) {
        List<Integer> addresses = new ArrayList<>();
        switch (instructions.getInstructCode()) {
            case ST:
                addresses.add(Integer.valueOf(instructions.getOperand2()));
                break;
            case LD:
                addresses.add(Integer.valueOf(instructions.getOperand2()));
                break;
            case INIT:
                addresses.add(Integer.valueOf(instructions.getOperand1()));
                break;

        }
        return  addresses.stream();
    }


    public void printMaxInstr(int amount){
        List<Map.Entry<Instructions, Integer>> list = instructionCountMap.entrySet().stream()
                .sorted(Map.Entry.<Instructions, Integer>comparingByValue().reversed())
                .limit(amount)
                .collect(Collectors.toList());
        System.out.println(list);
    }
    public void printStatistics(){
        List<Map.Entry<Instructions, Integer>> list = instructionCountMap.entrySet().stream()
                .sorted(Map.Entry.<Instructions, Integer>comparingByValue())
                .collect(Collectors.toList());
        System.out.println(list);
    }


}
