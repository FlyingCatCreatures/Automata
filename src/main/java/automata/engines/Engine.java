package automata.engines;

import java.util.List;

public interface Engine<Symbol> {
    public boolean step(Symbol symbol);
    public boolean isAccepting();
    public default boolean run(List<Symbol> input){
        for(Symbol symbol: input) {
            if(step(symbol)) return isAccepting();
        }
        return isAccepting();
    }
}
