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
    private int termCount;

    public ExpressionTree(String kMapExpression) {

        depth = 0;
        parent = null;

        String[] removeEquals = kMapExpression
                .replaceAll(" ", "")
                .replaceAll("[\\(]", "")
                .replaceAll("[\\)]", "")
                .split("=");
        fOf = removeEquals[0];

        build(removeEquals[1]);

    }

    public ExpressionTree(String fOf, String expression, ExpressionTree parent) {

        this.depth = 0;

        this.fOf = fOf;
        this.parent = parent;
        this.setParentDepths();
        build(expression);

    }

    private ExpressionTree() {
    }

    public void build(String expression) {

        // Print.out(expression);

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
        } else {
            this.termCount = minterms.length;
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

    private void setParentDepths() {
        int i = 0;
        ExpressionTree tree = this.parent;
        while (tree != null) {
            i++;
            tree.setDepth(i);
            tree = tree.parent;
        }
    }

    private void setDepth(int depth) {
        this.depth = depth;
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

        if (!isLeaf()) {
            if (this.left.equals(this.right)) {
                this.left = this.left.clone();
                this.left.setChildDepths();
                return simplify(this.left);
            }
        }

        // Print.out(this + " " + this.depth);

        Map<ExpressionTree, Integer> counts = this.getCounts();

        int highestCount = 0;
        ExpressionTree highest = null;
        int operatorHighestCount = 0;
        String operatorHighest = null;

        ExpressionTree newParent = null;

        for (Entry<ExpressionTree, Integer> entry : counts.entrySet()) {
            // Print.out(entry.getKey() + " = " + entry.getValue());
            if (entry.getValue() > highestCount) {
                highest = entry.getKey();
                highestCount = entry.getValue();
            }
        }

        if (highestCount > 1) {

            Map<String, Integer> operatorCounts = getOperatorCount(highest);
            for (Entry<String, Integer> entry : operatorCounts.entrySet()) {
                // Print.out(entry.getKey() + " = " + entry.getValue());
                if (entry.getValue() > operatorHighestCount) {
                    operatorHighest = entry.getKey();
                    operatorHighestCount = entry.getValue();
                }
            }

            if (operatorHighestCount > 1) {

                newParent = new ExpressionTree();
                newParent.fOf = this.fOf;
                newParent.value = operatorHighest;
                newParent.left = highest.clone();
                newParent.left.parent = newParent;

                shunt(highest, operatorHighest);

                this.parent = newParent;
                newParent.right = this;
                newParent.setChildDepths();

                newParent.right = simplify(newParent.right);
                newParent.right.parent = newParent;
                newParent.setChildDepths();

                return newParent;
            }

        }

        // if (operatorHighestCount > 1 && highestCount > 1) {
        // return newParent.simplify();
        // }
        this.setChildDepths();
        return this;

    }

    private void shunt(ExpressionTree highestTerm, String highestOperator) {

        if (!isLeaf()) {
            this.left.shunt(highestTerm, highestOperator);
            this.right.shunt(highestTerm, highestOperator);
        }

        if (this.value.equals(highestOperator)) {

            ExpressionTree branch = null;

            if (this.left.equals(highestTerm)) {
                branch = this.right;
            } else if (this.right.equals(highestTerm)) {
                branch = this.left;
            }

            if (branch != null) {
                this.value = branch.value;
                this.left = branch.left;
                this.right = branch.right;
                if (!branch.isLeaf()) {
                    this.left.parent = this;
                    this.right.parent = this;
                    this.left.setChildDepths();
                    this.right.setChildDepths();
                }
            }

        }

    }

    private Map<String, Integer> getOperatorCount(ExpressionTree term) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        return getOperatorCount(map, term);

    }

    private Map<String, Integer> getOperatorCount(Map<String, Integer> map, ExpressionTree term) {
        if (this.equals(term)) {
            if (this.parent != null) {
                if (map.containsKey(this.parent.value)) {
                    map.replace(this.parent.value, map.get(this.parent.value) + 1);
                } else {
                    map.put(this.parent.value, 1);
                }
            }
        } else {
            if (!isLeaf()) {
                map = this.left.getOperatorCount(map, term);
                map = this.right.getOperatorCount(map, term);
            }
        }

        return map;
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
                out = this.fOf + " = %s";
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

    private void setChildDepths() {

        if (isLeaf()) {
            this.setParentDepths();
        } else {
            this.left.setChildDepths();
            this.right.setChildDepths();
        }

    }

    public ExpressionTree clone() {

        ExpressionTree out = new ExpressionTree();

        out.fOf = this.fOf;
        out.value = this.value;
        out.depth = 0;
        out.termCount = this.termCount;
        out.parent = null;

        if (!isLeaf()) {
            out.left = this.left.clone();
            out.left.setParentDepths();
            out.left.parent = out;
            out.right = this.right.clone();
            out.right.setParentDepths();
        }

        return out;

    }

    @Override
    public int hashCode() {

        Set<Object> set = new HashSet<Object>();

        int i = 1;
        int hashSum = 0;

        for (char c : this.value.toCharArray()) {
            int c_ = c << i; // What the hell is this method I feel like John Carmack but dumber
            hashSum += c_;
            i++;
        }

        set.add(hashSum);

        if (!isLeaf()) {
            set.add(this.right.hashCode());
            set.add(this.left.hashCode());
        }

        return set.hashCode() << depth;
    }

    @Override
    public boolean equals(Object obj) {

        if (this.getClass().equals(obj.getClass())) {

            return this.hashCode() == obj.hashCode();
        }
        return false;
    }

}
