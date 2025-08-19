package DFA.Engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Transitioner<Symbol> {
    private Map<Integer, State> transitions;
    private Map<String, State> statesByName;
    
    public State get(State state, Symbol symbol) {
        return transitions.get(InputHash(state, symbol));
    }
    public State get(String name) {
        return statesByName.get(name);
    }

    public Transitioner(Map<Integer, State> transitions) {
        this.transitions = transitions;
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

        Map<Integer, State> map = new LinkedHashMap<>();
        statesByName = new LinkedHashMap<>();

        

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
            String name = parts[0];
            boolean accepting = parseIsAccepting(parts[1].toLowerCase().charAt(0));
            statesByName.putIfAbsent(name, new State(name, accepting));
        }

        

        // Now we parse the transitions for each state.
        // Each line contains exactly one transition of the form: 
        // stateName readSymbol -> nextState
        for (String line: lines.subList(1, lines.size())) {
            String[] parts = line.split("\\s+");
            if (parts.length != 4 || !parts[2].equals("->")) {
                throw new IllegalArgumentException("Invalid transition format at this line: " + line + "\nExpected format is: stateName readSymbol -> nextState writeSymbol direction");
            }

            String stateName = parts[0];
            String readSymbolRaw = parts[1];
            // " -> " is a fixed part of the notation so it carries no semantic meaning, so we can skip it
            String nextStateName = parts[3];

            State state = statesByName.get(stateName);
            if (state == null) throw new IllegalArgumentException("State '" + stateName + "' not declared in header");
            
            State nextState = statesByName.get(nextStateName);
            if (nextState == null) throw new IllegalArgumentException("State '" + nextStateName + "' not declared in header");
            
            Symbol readSymbol = symbolparser.apply(readSymbolRaw);

            if (map.put(InputHash(state, readSymbol), nextState) != null) {
                throw new IllegalArgumentException("Duplicate transition for (" + stateName + ", " + readSymbolRaw + ")");
            }
        }

        this.transitions = Collections.unmodifiableMap(map);
    }

    // ---------- Helpers ----------

    private static boolean parseIsAccepting(Character c) throws IllegalArgumentException {
        switch (c) {
            case 'y': return true;
            case 'n': return false;
            default:  throw new IllegalArgumentException("Unknown flag for a state found: " + c + " (use y/n)");
        }
    }

    private int InputHash(State state, Symbol readSymbol) {
        return state.hashCode() ^ readSymbol.hashCode();
    }
}