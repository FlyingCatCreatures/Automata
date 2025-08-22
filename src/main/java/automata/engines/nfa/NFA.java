package automata.engines.nfa;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import automata.engines.Engine;

public class NFA<Symbol> implements Engine<Symbol> {
    private Set<String> currentStates = new HashSet<>();
    private final Transitioner<Symbol> transitioner;
    private static final String[] EMPTY = new String[0];

    public NFA(String transitionerSpecification, Function<String, Symbol> symbolparser) throws Exception {
        this.transitioner = new Transitioner<>(transitionerSpecification, symbolparser);
        String initial = transitioner.get();
        this.currentStates.add(initial);
        this.currentStates.addAll(epsilonClosure(Set.of(initial)));
    }
    
    public boolean step(Symbol symbol) {
        HashSet<String> next = new HashSet<>();

        for (String state : currentStates) {
            String[] dests = transitioner.get(state, symbol);
            for (String d : dests) {
                next.add(d);
            }
        }

        this.currentStates = new HashSet<>(epsilonClosure(next));
        return this.currentStates.isEmpty();
    }

    // Only works if all symbols have been processed
    public boolean isAccepting() {
        return currentStates.stream().anyMatch(transitioner::isAccepting);
    }

    private Set<String> epsilonClosure(Collection<String> states) {
        Set<String> closure = new HashSet<>(states);
        Deque<String> worklist = new ArrayDeque<>(states);

        while (!worklist.isEmpty()) {
            String state = worklist.pop();
            String[] epsilons = transitioner.get(state);
            if (epsilons == null) epsilons = EMPTY;

            for (String e : epsilons) {
                if (closure.add(e)) {
                    worklist.push(e);
                }
            }
        }
        return closure;
    }
}
