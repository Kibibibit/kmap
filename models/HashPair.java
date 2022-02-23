package models;

public class HashPair<A,B> {

    private A a;
    private B b;


    public HashPair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return this.a;
    }

    public B getB() {
        return this.b;
    }

    @Override
    public int hashCode() {
        int aHash = a.hashCode();
        int bHash = b.hashCode();
        String hashString = "%d%d";

        if (aHash<bHash) {
            hashString = String.format(hashString, aHash,bHash);
        } else {
            hashString = String.format(hashString, bHash, aHash);
        }

        return hashString.hashCode();

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HashPair) {
            return this.hashCode() == obj.hashCode();
        }

        return false;
    }
    
}
