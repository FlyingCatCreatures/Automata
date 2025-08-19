package TuringMachines.Engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        return head>=0 ? getOrDefault(positive, head) : getOrDefault(negative, -head - 1);
    }

    public void writeAndMove(Symbol symbol, int direction) {
        if (head >= 0) {
            ensureCapacity(positive, head);
            positive.set(head, symbol);
        } else {
            int index = -head - 1;
            ensureCapacity(negative, index);
            negative.set(index, symbol);
        }
        head += direction;
        updateAccessBounds();
    }

    private void ensureCapacity(List<Symbol> list, int index) {
        int amtToAdd = (index + 1 - list.size())+10; // Resize to it new index, plus a little extra to make resizing less frequent
        if(amtToAdd <= 10) return; // we added 10 above, and we don't want to resize unnecesarily. Otherwise we'd test against amt <= 0
        list.addAll(Collections.nCopies(amtToAdd, defaultSymbol));
    }

    private Symbol getOrDefault(List<Symbol> list, int index) {
        return index < list.size() ? list.get(index) : defaultSymbol;
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
        ensureCapacity(positive, rightmostAccess);
        ensureCapacity(negative, -leftmostAccess - 1);
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