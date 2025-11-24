package org.lab_3v1.cpu_lib;

import org.lab_3v1.cpu_lib.instructions.InstructCode;
import org.lab_3v1.cpu_lib.instructions.Instructions;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Program implements Iterable<Instructions> {
    private List<Instructions> instructionsList = new ArrayList<>();
    private Map<Instructions, Integer> instructionCountMap = new HashMap<>();

    public Instructions[] toArray() {
        return instructionsList.toArray(new Instructions[0]);
    }

    @Override
    public Iterator<Instructions> iterator() {
        return instructionsList.iterator();
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
    public void add(InstructCode code, Object ... operands){
        Instructions instr = new Instructions(code, operands);
        instructionsList.add(instr);
        instructionCountMap.merge(instr, 1, (c1, c2) -> c1+c2 );

    }
    public int length(){
        return instructionsList.size();
    }
    public Instructions get(int ind){
        return instructionsList.get(ind);
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
        Object[] operands = instructions.getOperands();
        switch (instructions.getInstructCode()) {
            case ST:
                addresses.add((Integer) operands[1]);
                break;
            case LD:
                addresses.add((Integer) operands[1]);
                break;
            case INIT:
                addresses.add((Integer) operands[0]);
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
