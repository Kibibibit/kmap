package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.Binary;
import utils.Print;

public class KarnaughMap {

    private List<HashMap<String, Boolean>> maps;

    private int inputCount;
    private int outputCount;
    private int topCount;
    private int sideCount;
    private TruthTable truthTable;

    private String makePair(int t, int s) {
        return String.format("%d-%d", t, s);
    }

    public KarnaughMap(TruthTable truthTable) {

        this.truthTable = truthTable;

        maps = new ArrayList<HashMap<String, Boolean>>();

        inputCount = truthTable.getInputCount();
        outputCount = truthTable.getOutputCount();

        topCount = (int) Math.floor((double) inputCount / 2);
        sideCount = (int) Math.ceil((double) inputCount / 2);

        Print.out(topCount + ", " + sideCount);

        for (int output = 0; output < outputCount; output++) {
            maps.add(new HashMap<String, Boolean>());

            for (int topSet = 0; topSet < Math.pow(2, topCount); topSet++) {
                int topCode = Binary.grayCode(topSet);
                for (int sideSet = 0; sideSet < Math.pow(2, sideCount); sideSet++) {
                    int sideCode = Binary.grayCode(sideSet);

                    int value = (topCode << Binary.findBits((int) Math.pow(2, sideCount) - 1)) + sideCode;

                    boolean result = truthTable.result(value, output);

                    maps.get(output).put(makePair(topCode, sideCode), result);

                }
            }

            draw(output);

        }

    }

    public void draw(int output) {
        boolean topDone = false;

        for (int s = 0; s < Math.pow(2, sideCount); s++) {
            int side = Binary.grayCode(s);

            String topRow = " \t";
            String row = side + "\t";

            for (int t = 0; t < Math.pow(2, topCount); t++) {
                int top = Binary.grayCode(t);
                boolean res = maps.get(output).get(makePair(top, side));

                if (res) {
                    row += "1  ";
                } else {
                    row += "0  ";
                }

                if (!topDone) {
                    topRow += top + "  ";
                }

            }
            if (!topDone) {
                Print.out(topRow);
                Print.out("------------------------------------");
            }
            Print.out(row);
            topDone = true;

        }

        Print.out("\n");
    }

    public String[] solve() {

        String[] outputs = new String[maps.size()];

        for (int output = maps.size() - 1; output >= 0; output--) {

            Set<HashSet<String>> boxes = new HashSet<HashSet<String>>();

            HashMap<String, Boolean> map = maps.get(output);

            for (int t = 0; t < Math.pow(2, topCount); t++) {

                int top = Binary.grayCode(t);

                for (int s = 0; s < Math.pow(2, sideCount); s++) {

                    int side = Binary.grayCode(s);

                    if (!map.get(makePair(top, side))) {
                        continue;
                    }

                    int cellS = wrap(0, (int) Math.pow(2, sideCount), s + 1);
                    int grayCellS = Binary.grayCode(cellS);
                    int cellT = wrap(0, (int) Math.pow(2, topCount), t + 1);
                    int grayCellT = Binary.grayCode(cellT);

                    boolean sideFound = false;
                    boolean topFound = false;

                    for (int i = 0; i < 3; i++) {
                        HashSet<String> box = new HashSet<String>();

                        if (map.get(makePair(top, grayCellS)) && i == 0) {
                            box.add(String.format("%d-%d", top, side));
                            box.add(String.format("%d-%d", top, grayCellS));
                            sideFound = true;
                        } else if (map.get(makePair(grayCellT, side)) && i == 1) {
                            box.add(String.format("%d-%d", top, side));
                            box.add(String.format("%d-%d", grayCellT, side));
                            topFound = true;
                        } else if (!sideFound && !topFound && i == 2) {
                            box.add(String.format("%d-%d", top, side));
                        }

                        boolean unique = true;

                        for (HashSet<String> existingBox : boxes) {
                            if (existingBox.containsAll(box)) {
                                unique = false;
                                break;
                            }
                        }

                        if (unique && box.size() > 0) {
                            boxes.add(box);
                        }

                    }

                }

            }

            // FIND FORMULAS NOW IN DISTRIBUTED FORM

            String formula = String.format("%s = ",
                    this.truthTable.getOutputLabels()[this.truthTable.getOutputCount() - output - 1]);

            int boxCount = 0;
            for (HashSet<String> box : boxes) {
                Integer[] tops = new Integer[box.size()];
                Integer[] sides = new Integer[box.size()];
                int i = 0;
                for (String value : box) {
                    String[] strValues = value.split("-");
                    tops[i] = Integer.parseInt(strValues[0]);
                    sides[i] = Integer.parseInt(strValues[1]);
                    i++;
                }

                int pos = 0;
                boolean top = true;

                ArrayList<String> inputsSorted = new ArrayList<String>();
                for (int tInput = 0; tInput < topCount; tInput++) {
                    int tIndex = topCount - tInput - 1;
                    inputsSorted.add(this.truthTable.getInputLabels()[tIndex]);
                }
                for (int sInput = 0; sInput < sideCount; sInput++) {
                    int sIndex = topCount + (sideCount - sInput - 1);
                    inputsSorted.add(this.truthTable.getInputLabels()[sIndex]);
                }

                String statement = "";
                for (String input : inputsSorted) {
                    if (pos >= topCount && top) {
                        pos = 0;
                        top = false;
                    }
                    Integer[] checkArr = top ? tops : sides;
                    boolean value = (checkArr[0] >> pos) % 2 == 1;
                    boolean changing = false;

                    for (Integer check : checkArr) {
                        changing = value != ((check >> pos) % 2 == 1);
                        if (changing) {
                            break;
                        }
                    }

                    if (!changing) {

                        if (!value) {
                            statement += "!";
                        }
                        statement += input + "*";
                    }

                    pos++;
                }

                boxCount++;
                statement = statement.substring(0, statement.length() - 1);
                formula += statement;
                if (boxCount < boxes.size()) {
                    formula += " + ";
                }

            }
            Print.out("Formula: " + formula);
            outputs[output] = formula;

        }

        return outputs;

    }

    private int wrap(int min, int max, int value) {

        int diff = Math.abs(max - min);

        while (value <= min) {
            value += diff;
        }

        while (value >= max) {
            value -= diff;
        }

        return value;

    }

}
