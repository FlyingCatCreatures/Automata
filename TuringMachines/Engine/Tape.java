package TuringMachines.Engine;

import java.util.HashMap;
import java.util.Map;

public class Tape<Symbol> {
    private Map<Integer, Symbol> cells = new HashMap<>();
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
    }

    public Symbol read() {
        updateAccessBounds();
        return cells.getOrDefault(head, defaultSymbol);
    }

    public void writeAndMove(Symbol symbol, Direction direction) {
        cells.put(head, symbol);
        updateAccessBounds();
        switch(direction){
            case LEFT:  head--; break;
            case RIGHT: head++; break;
        }
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

    public String toString(){
        //return stringify(Math.min(leftmostAccess,1),Math.max(rightmostAccess,10));
        return stringify(leftmostAccess, rightmostAccess);
    }

    public String stringify(int start, int end) {
        StringBuilder sb = new StringBuilder(end - start);
        /*sb.append("Tape from position ")
          .append(start)
          .append(" to ")
          .append(end)
          .append(": ");*/
        for (int pos = start; pos <= end; pos++) {
            sb.append(cells.getOrDefault(pos, defaultSymbol));
        }
        return sb.toString();
    }

    public int countOccurrences(Symbol symbol) {
        int count = 0;
        for (int pos = leftmostAccess; pos <= rightmostAccess; pos++) {
            if (cells.getOrDefault(pos, defaultSymbol).equals(symbol)) {
                count++;
            }
        }
        return count;
    }
}
