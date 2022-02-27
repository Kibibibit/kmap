package models;

import java.util.HashSet;
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

        if (expression.contains("=")) {
            Print.out("ALARM! + " + expression);
        }

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

        String currentValue = this.value;

        boolean threeProng = false;
        boolean doubleParent = false;
        boolean doubleChild = false;

        ExpressionTree singleTerm = null;
        ExpressionTree dualTerm = null;
        boolean left = false;

        if (!isLeaf()) {
            // Four prong case eg. (A&B)|(B&C)
            if (this.left.value.equals(this.right.value) && !this.left.isLeaf() && !this.right.isLeaf()) {

                threeProng = this.left.left.equals(this.right.left) || this.left.left.equals(this.right.right)
                        || this.left.right.equals(this.right.left) || this.left.right.equals(this.right.right);
                doubleParent = false;
                doubleChild = false;

            }

            // Cases such as (A | (A | B))
            if ((this.value.equals(this.left.value) || this.value.equals(this.right.value)) &&
                    (!this.left.isLeaf() || !this.right.isLeaf()) &&
                    !(this.value.equals(this.left.value) && this.value.equals(this.right.value))) {

                left = !this.value.equals(this.left.value);
                if (left) {
                    singleTerm = this.left.clone();
                    dualTerm = this.right.clone();
                } else {
                    singleTerm = this.right.clone();
                    dualTerm = this.left.clone();
                }

                if (dualTerm.left.equals(singleTerm) || dualTerm.right.equals(singleTerm)) {

                    doubleParent = true;
                    threeProng = false;
                    doubleChild = false;

                }

            }

            if (this.left.equals(this.right)) {
                doubleChild = true;
                doubleParent = false;
                threeProng = false;
            }

            if (threeProng) {
                boolean match = true;

                ExpressionTree matchTree = null;
                ExpressionTree newLeft = null;
                ExpressionTree newRight = null;

                if (this.left.left.equals(this.right.left)) {
                    newLeft = this.left.right.clone();
                    newRight = this.right.right.clone();
                    matchTree = this.left.left.clone();
                } else if (this.left.left.equals(this.right.right)) {
                    newLeft = this.left.right.clone();
                    newRight = this.right.left.clone();
                    matchTree = this.left.left.clone();
                } else if (this.left.right.equals(this.right.left)) {
                    newLeft = this.left.left.clone();
                    newRight = this.right.right.clone();
                    matchTree = this.right.left.clone();
                } else if (this.left.right.equals(this.right.right)) {
                    matchTree = this.right.right.clone();
                    newLeft = this.left.left.clone();
                    newRight = this.right.left.clone();
                } else {
                    match = false;
                }

                if (match) {

                    this.value = this.left.value;

                    this.left = matchTree;
                    

                    this.right.value = currentValue;

                    this.right.left = newLeft;
                    this.right.left.parent = this.right;
                    
                    this.right.right = newRight;
                    this.right.right.parent = this.right;
                }
            }

            if (doubleParent) {
                if (dualTerm.left.equals(singleTerm) || dualTerm.right.equals(singleTerm)) {

                    doubleParent = true;

                    this.left = singleTerm.clone();

                    if (dualTerm.left.equals(singleTerm)) {
                        this.right = dualTerm.right.clone();
                    } else {
                        this.right = dualTerm.left.clone();
                    }

                }
            }

            if (doubleChild) {
                this.value = this.left.value;
                this.right = this.left.right;
                this.left = this.left.left;
                
            }

            if (threeProng || doubleParent || doubleChild) {
                this.left.parent = this;
                this.right.parent = this;
                this.setChildDepths();
            } else {
                int oldLeftHash = this.left.hashCode();
                int oldRightHash = this.right.hashCode();
                this.left.simplify();
                this.right.simplify();
                

                if (this.left.hashCode() != oldLeftHash || this.right.hashCode() != oldRightHash) {
                    this.simplify();
                }
            }

        }

        return this;

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
            out.right.parent = out;
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
