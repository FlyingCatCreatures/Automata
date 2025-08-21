package automata.engines.dfa;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

public class Transitioner<Symbol> {
    private IntObjectHashMap<String> transitions;
    private Map<String, Boolean> isAcceptingByName;
    private Set<String> states;
    private String initialstateName;

    public String get(String state, Symbol symbol) {
        return transitions.get(H(state, symbol));
    }

    // Returns the initial state of the DFA
    public String get() {
        return initialstateName;
    }

    public boolean isAccepting(String state) {
        return isAcceptingByName.get(state);
    }

    /*
     * Format of transitions is a declaration of states used, along with if they are accepting states, like this:
     * 'y' means accepting, 'n' means not accepting.
     * {state1 y, state2 y, state3 n, ...}
     * 
     * Followed by a list of transitions, like:
     * state1 symbol1 -> state2
     * state1 symbol2 -> state3
     * etc. etc.
     * 
     */
    public Transitioner(String Specification, Function<String, Symbol> symbolparser) throws IllegalArgumentException {
        if (Specification == null) throw new IllegalArgumentException("spec is null");

        // Normalize line endings and split
        List<String> lines = Arrays.stream(Specification.replace("\r\n", "\n").replace('\r', '\n').split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !s.startsWith("#")) // allow comments starting with #
                .toList();

        transitions = new IntObjectHashMap<>();
        isAcceptingByName = new HashMap<>();
        states = new HashSet<>();

        if (lines.isEmpty()) throw new IllegalArgumentException("Empty transition specification");


        // First we parse the header line with state names
        // Format: {stateName*}
        // Semantics: Declare the existence of all states used
        String stateHeader = lines.get(0);
        if(!stateHeader.startsWith("{") || !stateHeader.endsWith("}")) throw new IllegalArgumentException("Expected a state header at the start of the specification; got: " + lines.get(0) + "\nExpected format is '{stateName1, stateName2, ...}'");
        String inside = stateHeader.substring(1, stateHeader.length() - 1);
        for (String raw : inside.split(",")) {
            String token = raw.trim();
            String[] parts = token.split("\\s+");
            if (parts.length != 2) throw new IllegalArgumentException("Each state must be followed by 'y' or 'n': " + token);
            states.add(parts[0]);
            isAcceptingByName.put(parts[0], parseIsAccepting(parts[1].toLowerCase().charAt(0)));
        }
        
        // The second line is the initial state name
        initialstateName = lines.get(1).trim();

        

        // Now we parse the transitions for each state.
        // Each line contains exactly one transition of the form: 
        // stateName readSymbol -> nextState
        for (String line: lines.subList(2, lines.size())) {
            String[] parts = line.split("\\s+");
            if (parts.length != 4 || !parts[2].equals("->")) {
                throw new IllegalArgumentException("Invalid transition format at this line: " + line + "\nExpected format is: stateName readSymbol -> nextState writeSymbol direction");
            }

            String stateName = parts[0];
            String readSymbolRaw = parts[1];
            // " -> " is a fixed part of the notation so it carries no semantic meaning, so we can skip it
            String nextStateName = parts[3];

            if (!states.contains(stateName)) throw new IllegalArgumentException("String '" + stateName + "' not declared in header");
            
            if (!states.contains(nextStateName)) throw new IllegalArgumentException("String '" + nextStateName + "' not declared in header");
            
            Symbol readSymbol = symbolparser.apply(readSymbolRaw);

            if (transitions.put(H(stateName, readSymbol), nextStateName) != null) {
                throw new IllegalArgumentException("Duplicate transition for (" + stateName + ", " + readSymbolRaw + ")");
            }
        }
    }
    // ---------- Helpers ----------

    private static boolean parseIsAccepting(Character c) throws IllegalArgumentException {
        switch (c) {
            case 'y': return true;
            case 'n': return false;
            default:  throw new IllegalArgumentException("Unknown flag for a state found: " + c + " (use y/n)");
        }
    }
    
    private int H(String s1, Symbol s2){
        return 31 * (31 + s1.hashCode()) + s2.hashCode();
    }
}