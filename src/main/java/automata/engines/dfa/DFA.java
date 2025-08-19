package automata.engines.dfa;

import java.util.function.Function;

public class DFA<Symbol> {
    private State currentState;
    private final Transitioner<Symbol> transitioner;
    private final String digest;
    private int headPosition;
    private final Function<String, Symbol> symbolParser;

    public DFA(String initialStateName, String transitionerSpecification, String digest, Function<String, Symbol> symbolparser) throws Exception {
        this.transitioner = new Transitioner<>(transitionerSpecification, symbolparser);
        this.currentState = transitioner.get(initialStateName);
        this.digest = digest;
        this.symbolParser = symbolparser;
        this.headPosition = 0;
    }
    
    public boolean step() {
        currentState = transitioner.get(currentState, symbolParser.apply(String.valueOf(digest.charAt(headPosition))));
        return ++headPosition == digest.length();
    }

    public boolean isAccepting() {
        return currentState.isAccepting();
    }
}