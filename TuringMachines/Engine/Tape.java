package TuringMachines.Engine;

import java.util.ArrayList;

public class Tape<Symbol> {
    private ArrayList<Symbol> positive = new ArrayList<>();
    private ArrayList<Symbol> negative = new ArrayList<>();
    private int head = 0;
    private Symbol defaultSymbol;

    // Tracks the leftmost and rightmost access positions
    // These are not strictly necessary for the tape's functionality, but useful for printing
    private int leftmostAccess=0, rightmostAccess=0;

    private void updateAccessBounds() {
        leftmostAccess = Math.min(leftmostAccess, head);
        rightmostAccess = Math.max(rightmostAccess, head);
    }

    public Tape(Symbol defaultSymbol) {
        this(defaultSymbol, 0);
    }

    public Tape(Symbol defaultSymbol, int headPosition) {
        head = headPosition;
        this.defaultSymbol = defaultSymbol;
        leftmostAccess = headPosition;
        rightmostAccess = headPosition;
    }

    public Symbol read() {
        return head>=0 ? getOrDefaultPositive() : getOrDefaultNegative();
    }

    public void writeAndMove(Symbol symbol, int direction) {
        if (head >= 0) {
            ensurePositiveCapacity(head);
            positive.set(head, symbol);
        } else {
            int index = -head - 1;
            ensureNegativeCapacity(index);
            negative.set(index, symbol);
        }
        head += direction;
        updateAccessBounds();
    }

    private void ensurePositiveCapacity(int index) {
        while (positive.size() <= index) {
            positive.add(defaultSymbol);
        }
    }

    private void ensureNegativeCapacity(int index) {
        while (negative.size() <= index) {
            negative.add(defaultSymbol);
        }
    }

    private Symbol getOrDefaultPositive() {
        return head < positive.size() ? positive.get(head) : defaultSymbol;
    }

    private Symbol getOrDefaultNegative() {
        int index = -head - 1;
        return index<negative.size() ? negative.get(index) : defaultSymbol;
    }

    public void setHead(int newHead) {
        this.head = newHead;
    }

    public int getHead() {
        return head;
    }

    public int getLeftmostAccess() {
        return leftmostAccess;
    }

    public int getRightmostAccess() {
        return rightmostAccess;
    }

    public String toString() {
        return stringify(leftmostAccess, rightmostAccess);
    }

    public String stringify(int start, int end) {
        StringBuilder sb = new StringBuilder(end - start + 1);
        for (int pos = start; pos <= end; pos++) {
            Symbol symbol;
            if (pos >= 0) {
                if (pos < positive.size()) {
                    symbol = positive.get(pos);
                } else {
                    symbol = defaultSymbol;
                }
            } else {
                int index = -pos - 1;
                if (index < negative.size()) {
                    symbol = negative.get(index);
                } else {
                    symbol = defaultSymbol;
                }
            }
            sb.append(symbol);
        }
        return sb.toString();
    }

    public int countOccurrences(Symbol symbol) {
        int count = 0;
        ensurePositiveCapacity(rightmostAccess);
        ensureNegativeCapacity(-leftmostAccess - 1);
        for (int pos = leftmostAccess; pos <= rightmostAccess; pos++) {
            Symbol currentSymbol;
            if (pos >= 0) {
                currentSymbol = positive.get(pos);
            } else {
                currentSymbol = negative.get(-pos - 1);
            }

            if (currentSymbol.equals(symbol)) count++;
            
        }
        return count;
    }
}