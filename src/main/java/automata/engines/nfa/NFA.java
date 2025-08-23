package automata.engines.nfa;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import automata.engines.Engine;

public class NFA<Symbol> implements Engine<Symbol> {
    private Set<String> currentStates = new HashSet<>();
    private final Transitioner<Symbol> transitioner;

    public NFA(String transitionerSpecification, Function<String, Symbol> symbolparser) throws Exception {
        this.transitioner = new Transitioner<>(transitionerSpecification, symbolparser);
        this.currentStates = epsilonClosure(new HashSet<>(Arrays.asList(transitioner.get())));
    }
    
    public boolean step(Symbol symbol) {
        HashSet<String> next = new HashSet<>();

        for (String state : currentStates)
            for (String d : transitioner.get(state, symbol)) 
                next.add(d);
            
        
        this.currentStates = epsilonClosure(next);
        return this.currentStates.isEmpty();
    }

    // Only works if all symbols have been processed
    public boolean isAccepting() {
        return currentStates.stream().anyMatch(transitioner::isAccepting);
    }

    private Set<String> epsilonClosure(HashSet<String> states) {
        HashSet<String> closure = states; // All states passed are in their own epsilon closure
        Deque<String> worklist = new ArrayDeque<>(states);

        while (!worklist.isEmpty()) {
            for (String s : transitioner.get(worklist.pop())) {
                if (closure.add(s))
                    worklist.push(s);
            }
        }
        return closure;
    }
}
