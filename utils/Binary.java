package utils;

public class Binary {

    public static int parse(String s) {
        return Integer.parseInt(s.strip().replaceAll(" ", ""),2);
    }

    public static int findBits(int s) {

        int bits = 1;

        while (Math.pow(2,bits) < s) {
            bits++;
        }
        
        return bits;

    }

    public static int grayCode(int s) {
        return s ^ (s >> 1);
    }

    public static int inverseGrayCode(int s) {
        int inv = 0;

        for (; s != 0; s = s >> 1) {
            inv ^= s;
        }

        return inv;
    }
    
}
