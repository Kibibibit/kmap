import models.ExpressionTree;
import models.KarnaughMap;
import models.TruthTable;
import utils.Print;

public class KMap {

    public static final void main(String[] args) {

        TruthTable adderTruthTable = new TruthTable(3, 2, "A,B,C", "Q,C'");

        adderTruthTable.setRow("000", "00");
        adderTruthTable.setRow("001", "10");
        adderTruthTable.setRow("010", "10");
        adderTruthTable.setRow("011", "01");
        adderTruthTable.setRow("100", "10");
        adderTruthTable.setRow("101", "01");
        adderTruthTable.setRow("110", "01");
        adderTruthTable.setRow("111", "11");

        Print.out("Solving for adder");
        KarnaughMap adderKarnaughMap = new KarnaughMap(adderTruthTable);
        String[] adderFormulas = adderKarnaughMap.solve();
        String[] simpleAdderFormulas = new String[adderFormulas.length];

        int i = 0;
        for (String adderFormula : adderFormulas) {
            ExpressionTree adderTree = new ExpressionTree(adderFormula);
            Print.out(adderTree.toString());
            simpleAdderFormulas[i] = adderTree.simplify().toString();
            Print.out(simpleAdderFormulas[i]);
            i++;

        }

        // Print.out("--- TEST 1 ---");

        // ExpressionTree test = new ExpressionTree("F = (A&B) | (C&B)");

        // Print.out(test);

        // test = test.simplify();

        // Print.out(test);

        // Print.out("--- TEST 2 ---");

        // ExpressionTree test2 = new ExpressionTree("F = (A&C) | (A&C) | (C&B)");

        // Print.out(test2);

        // test2 = test2.simplify();

        // Print.out(test2);

        // Print.out("--- TEST 3 ---");

        // ExpressionTree test3 = new ExpressionTree("C' = (C&B) | ((A&B) | (A&C))");

        // Print.out(test3);

        // test3 = test3.simplify();

        // Print.out(test3);

        Print.out("\n\nSolving for 4 Bit BCD");

        TruthTable bcdTruthTable = new TruthTable(4,5,"A,B,C,D","V,W,X,Y,Z");

        bcdTruthTable.setRow("0000","0 0000");
        bcdTruthTable.setRow("0001","0 0001");
        bcdTruthTable.setRow("0010","0 0010");
        bcdTruthTable.setRow("0011","0 0011");
        bcdTruthTable.setRow(4,4);
        bcdTruthTable.setRow(5,5);
        bcdTruthTable.setRow(6,6);
        bcdTruthTable.setRow(7,7);
        bcdTruthTable.setRow(8,8);
        bcdTruthTable.setRow(9,9);
        bcdTruthTable.setRow(10,16);
        bcdTruthTable.setRow(11,17);
        bcdTruthTable.setRow(12,18);
        bcdTruthTable.setRow(13,19);
        bcdTruthTable.setRow(14,20);
        bcdTruthTable.setRow(15,21);

        KarnaughMap bcdKarnaughMap = new KarnaughMap(bcdTruthTable);

        String[] bcdFormulas = bcdKarnaughMap.solve();
        String[] simpleBcdFormulas = new String[bcdFormulas.length];

        i = 0;
        for (String bcdFormula : bcdFormulas) {
            ExpressionTree bcdTree = new ExpressionTree(bcdFormula);
            Print.out(bcdTree.toString());
            simpleBcdFormulas[i] = bcdTree.simplify().toString();
            Print.out(simpleBcdFormulas[i]);
            i++;

        }

        System.exit(0);

    }

}