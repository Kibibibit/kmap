package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.Binary;
import utils.Print;

public class KarnaughMap {

    private List<HashTable<Integer, Integer, Boolean>> maps;

    private int inputCount;
    private int outputCount;
    private int topCount;
    private int sideCount;

    public KarnaughMap(TruthTable truthTable) {

        maps = new ArrayList<HashTable<Integer, Integer, Boolean>>();

        inputCount = truthTable.getInputCount();
        outputCount = truthTable.getOutputCount();

        topCount = (int) Math.floor((double)inputCount / 2);
        sideCount = (int) Math.ceil((double)inputCount / 2);

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


    public void solve() {

        for (int output = 0; output < maps.size(); output++) {

            Set<HashSet<String>> boxes = new HashSet<HashSet<String>>();

            HashTable<Integer, Integer, Boolean> map = maps.get(output);
     
            for (int t = 0; t < Math.pow(2,topCount); t++) {

                int top = Binary.grayCode(t);

                for (int s = 0; s < Math.pow(2,sideCount); s ++) {

                    int side = Binary.grayCode(s);                    

                    if (!map.get(top,side)) {
                        continue;
                    }

                    HashSet<String> box = new HashSet<String>();

                    int width = 0;
                    int height = 0;

                    for (int sOffset = 0; sOffset < Math.pow(2,sideCount); sOffset++) {
                        int cellS = wrap(0, (int) Math.pow(2,sideCount), s + sOffset);
                        int grayCellS = Binary.grayCode(cellS);

                        if (maps.get(output).get(top,grayCellS)) {
                            box.add(String.format("%d-%d",top,grayCellS));
                            height++;
                        } else {
                            break;
                        }
                    }

                    for (int tOffset = 0; tOffset < Math.pow(2, topCount); tOffset++) {
                        boolean columnGood = true;
                        Set<String> columnSet = new HashSet<String>();
                        for (int sOffset = 0; sOffset < height; sOffset++) {
                            int cellT = wrap(0, (int) Math.pow(2,topCount), t+tOffset);
                            int grayCellT = Binary.grayCode(cellT);
                            int cellS = wrap(0, (int) Math.pow(2, sideCount), s+sOffset);
                            int grayCellS = Binary.grayCode(cellS);
                            columnSet.add(String.format("%d-%d",grayCellT,grayCellS));
                            columnGood = maps.get(output).get(grayCellT, grayCellS);

                        }

                        if (columnGood) {
                            box.addAll(columnSet);
                            width++;
                        } else {
                            break;
                        }
                    }

                    boolean unique = true;


                    for (HashSet<String> existingBox : boxes) {
                        if (existingBox.containsAll(box)) {
                            unique = false;
                            break;
                        }
                    }

                    if (unique) {
                        Print.out("("+output+","+top+","+side+") Adding new box of width: " + width+ ", height: "+height +" | "+ box);
                        boxes.add(box);
                    } else {
                        Print.out("("+output+","+top+","+side+") Box of width: " + width+ ", height: "+height + " is not unique!");
                    }
                    

                    

                    
            
                   
                }

            }
        }

    }

    private int wrap(int min, int max, int value) {

        int diff = Math.abs(max-min);

        while (value <= min) {
            value += diff;
        }

        while (value >= max) {
            value -= diff;
        }

        return value;

    }

}
