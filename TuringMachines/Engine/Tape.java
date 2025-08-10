package TuringMachines.Engine;

import java.util.HashMap;
import java.util.Map;

public class Tape {
    private Map<Integer, Boolean> cells = new HashMap<>();
    private int head = 0;
    private boolean defaultSymbol = false;

    // Constructor for tape with an inputstring containing only 1s and 0s
    public Tape(String initialState, int start) {
        for (int i = 0; i < initialState.length(); i++) {
            cells.put(i+start, initialState.charAt(i) == '1');
        }
    }

    // Constructor for empty tape
    public Tape() {}

    public Boolean readRaw() {
        return cells.get(head); // returns null if not written to
    }

    public boolean read() {
        return cells.getOrDefault(head, defaultSymbol);
    }

    public void write(boolean symbol) {
        cells.put(head, symbol);
    }

    public void moveLeft() {
        head--;
    }

    public void moveRight() {
        head++;
    }

    public int getHead() {
        return head;
    }
    public void setHead(int position){
        head = position;
    }

    public void setDefaultSymbol(boolean defaultSymbol) {
        this.defaultSymbol = defaultSymbol;
    }

    public String toString(){
        return stringify(1,10);
    }

    public String stringify(int start, int end) {
        int savedHead = getHead();
        StringBuilder sb = new StringBuilder(end - start);

        for (int pos = start; pos < end; pos++) {
            Boolean val = cells.get(pos);
            sb.append(val==null? "_":val? "1": "0");
        }

        setHead(savedHead);
        return sb.toString();
    }
}
