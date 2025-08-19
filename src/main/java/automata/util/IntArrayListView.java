package automata.util;

import java.util.AbstractList;

public class IntArrayListView extends AbstractList<Integer> {
    private final int[] data;
    public IntArrayListView(int[] data) { this.data = data; }

    @Override
    public Integer get(int index) {
        return data[index]; // autobox only when accessed
    }

    @Override
    public int size() {
        return data.length;
    }
}