package models;

import java.util.HashMap;
import java.util.Map;

import utils.Binary;

public class TruthTable {

    private Map<Integer, Integer> table;

    private int inputCount;
    private int outputCount;
    private String[] inputLabels;
    private String[] outputLabels;

    public TruthTable(int inputCount, int outputCount, String inputLabels, String outputLabels) {
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        this.inputLabels = inputLabels.split(",");
        this.outputLabels = outputLabels.split(",");

        table = new HashMap<Integer, Integer>((int) Math.pow(2,inputCount));

        for (int i = 0; i < Math.pow(2,inputCount); i++) {
            table.put(i, 0);
        }
    }

    public void setRow(int input, int output) {
        table.replace(input, output);
    }

    public void setRow(String input, String output) {
        setRow(Binary.parse(input), Binary.parse(output));
    }

    public int result(int input) {
        return table.get(input);
    }

    public int result(String input) {
        return table.get(Binary.parse(input));
    }

    public boolean result(int input, int pos) {
        return (table.get(input) >> pos) % 2 == 1;
    }

    public int size() {
        return table.size();
    }

    public int getInputCount() {
        return this.inputCount;
    }

    public int getOutputCount() {
        return this.outputCount;
    }

    public String[] getInputLabels() {
        return this.inputLabels;
    }

    public String[] getOutputLabels() {
        return this.outputLabels;
    }
    
}
