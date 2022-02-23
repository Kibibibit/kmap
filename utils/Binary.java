package utils;

public class Binary {

    public static int parse(String s) {
        return Integer.parseInt(s,2);
    }

    public static int grayCode(int s) {
        return s ^ (s >> 1);
    }
    
}
