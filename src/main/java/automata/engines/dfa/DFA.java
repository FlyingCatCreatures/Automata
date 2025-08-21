package automata.engines.dfa;

import java.util.function.Function;
import automata.engines.Engine;

public class DFA<Symbol> implements Engine<Symbol>{
    private String currentState;
    private final Transitioner<Symbol> transitioner;

    public DFA(String transitionerSpecification, Function<String, Symbol> symbolparser) throws Exception {
        this.transitioner = new Transitioner<>(transitionerSpecification, symbolparser);
        this.currentState = transitioner.get();
    }
    
    public boolean step(Symbol symbol) {
        currentState = transitioner.get(currentState, symbol);
        return false; // DFA can always take another step, since all states must have a transition for every symbol.
    }

    public boolean isAccepting() {
        return transitioner.isAccepting(currentState);
    }
}