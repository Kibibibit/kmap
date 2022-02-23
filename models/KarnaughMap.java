package models;

import java.util.ArrayList;
import java.util.List;

import utils.Binary;

public class KarnaughMap {

    private List<HashTable<Integer, Integer, Boolean>> maps;

    public KarnaughMap(TruthTable truthTable) {

        maps = new ArrayList<HashTable<Integer, Integer, Boolean>>();

        int inputCount = truthTable.getInputCount();
        int outputCount = truthTable.getOutputCount();

        int topCount = (int) Math.floor((double)inputCount / 2);
        int sideCount = (int) Math.ceil((double)inputCount / 2);

        for (int output = 0; output < outputCount; output++) {
            maps.add(new HashTable<Integer, Integer, Boolean>());

            for (int topSet = 0; topSet < Math.pow(2, topCount); topSet++) {
                int topCode = Binary.grayCode(topSet);
                for (int sideSet = 0; sideSet < Math.pow(2, sideCount); sideSet++) {
                    int sideCode = Binary.grayCode(sideSet);
                    
                    int value = (topCode << sideCount) + sideCode;
                    boolean result = truthTable.result(value,output);

                    maps.get(output).put(topCode, sideCode, result);

                }
            }

        }

    }

}
