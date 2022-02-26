package models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import utils.Print;

public class ExpressionTree {

    public static final String OR = "|";
    public static final String AND = "&";

    // Not sure I cant use this one
    public static final String NOT = "!";

    private String fOf;
    private String value;
    private ExpressionTree left;
    private ExpressionTree right;
    private ExpressionTree parent;
    private int depth;

    public ExpressionTree(String kMapExpression) {

        depth = 1;
        parent = null;

        String[] removeEquals = kMapExpression
                .replaceAll(" ", "")
                .replaceAll("[\\+]", OR)
                .replaceAll("[\\*]", AND)
                .split("=");
        fOf = removeEquals[0];

        build(removeEquals[1]);

    }

    public ExpressionTree(String fOf, String expression, ExpressionTree parent) {

        this.depth = 0;

        this.fOf = fOf;
        this.parent = parent;
        this.parent.addDepth();
        build(expression);

    }

    public void build(String expression) {

        Print.out(expression);

        String[] minterms = expression.split("[\\" + OR + "]");

        String operation = OR;

        if (minterms.length == 1) {
            minterms = expression.split("[\\" + AND + "]");
            operation = AND;
        }

        if (minterms.length == 1) {
            // Leaf node
            left = null;
            right = null;
            value = expression;
            return;
        }

        value = operation;

        String leftFormula = "";
        String rightFormula = "";

        int i = 0;
        for (String minterm : minterms) {

            if (i < minterms.length / 2) {
                leftFormula += minterm + operation;
            } else {
                rightFormula += minterm + operation;
            }
            i++;
        }

        leftFormula = leftFormula.substring(0, leftFormula.length() - 1);
        rightFormula = rightFormula.substring(0, rightFormula.length() - 1);

        this.left = new ExpressionTree(this.fOf, leftFormula, this);
        this.right = new ExpressionTree(this.fOf, rightFormula, this);

    }

    private void addDepth() {
        if (parent != null) {
            this.parent.addDepth();
        }
        this.depth++;
    }

    public int getDepth() {
        return this.depth;
    }

    public ExpressionTree simplify() {
        return simplify(this);
    }

    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

    private ExpressionTree simplify(ExpressionTree tree) {

        if (this.left.equals(this.right)) {
            return simplify(this.left);
        }

        Map<ExpressionTree, Integer> counts = this.getCounts();

        for (Entry<ExpressionTree, Integer> entry : counts.entrySet()) {
            Print.out(entry.getKey() + " = " + entry.getValue());
        }

        return null;

    }

    private Map<ExpressionTree, Integer> getCounts() {
        Map<ExpressionTree, Integer> map = new HashMap<ExpressionTree, Integer>();
        return this.getCounts(map);
    }

    private Map<ExpressionTree, Integer> getCounts(Map<ExpressionTree, Integer> map) {

        if (map.containsKey(this)) {
            map.replace(this, map.get(this) + 1);
        } else {
            map.put(this, 1);
        }
        if (!isLeaf()) {
            map = this.left.getCounts(map);
            map = this.right.getCounts(map);
        }

        return map;

    }

    public String toString() {

        if (isLeaf()) {
            return this.value;
        } else {

            String out = "(%s)";

            if (this.parent == null) {
                out = "%s";
            }

            String valueString = this.value;
            if (value.equals(OR)) {
                valueString = " | ";
            }

            String expression = String.format("%s%s%s", this.left.toString(), valueString, this.right.toString());
            out = String.format(out, expression);

            return out;
        }

    }

    @Override
    public int hashCode() {

        Set<Object> set = new HashSet<Object>();
        
        int i = 1;
        int hashSum = 0;

        for (char c : this.value.toCharArray()) {
            int c_ =  (int) Math.pow((c + i*31),2); // What the hell is this method I feel like John Carmack but dumber
            hashSum += c_;
            i++;
        }

        set.add(hashSum);

        if (!isLeaf()) {
            set.add(this.right.hashCode());
            set.add(this.left.hashCode());
        }

        return set.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this.getClass().equals(obj.getClass())) {

            return this.hashCode() == obj.hashCode();
        }
        return false;
    }

}
