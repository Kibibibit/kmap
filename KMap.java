import models.KarnaughMap;
import models.TruthTable;
import utils.Print;

public class KMap {

    public static final void main(String[] args) {
        Print.out("Hello World");

        TruthTable truthTable = new TruthTable(3,2);

        truthTable.setRow("000", "00");
        truthTable.setRow("001", "01");
        truthTable.setRow("010", "01");
        truthTable.setRow("011", "10");
        truthTable.setRow("100", "01");
        truthTable.setRow("101", "10");
        truthTable.setRow("110", "10");
        truthTable.setRow("111", "11");

        Print.out(truthTable.result(7,0));

        KarnaughMap karnaughMap = new KarnaughMap(truthTable);


    }

}