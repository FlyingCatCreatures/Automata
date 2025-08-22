package automata.engines.nfa;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

public class Transitioner<Symbol>  {
    private IntObjectHashMap<String[]> transitions;
    private Map<String, String[]> epsilonTransitions;
    private Map<String, Boolean> isAcceptingByName;
    private Set<String> states;
    private String initialstateName;

    // Returns array of next states for given (state, symbol) pair
    public String[] get(String state, Symbol symbol) {
        int key = H(state, symbol);
        return transitions.containsKey(key) ? transitions.get(key) : new String[0];
    }

    // Returns array of next states for given state with epsilon transition
    public String[] get(String state){
        return epsilonTransitions.getOrDefault(state, new String[0]);
    }

    // Returns the initial state of the NFA
    public String get() {
        return initialstateName;
    }

    public boolean isAccepting(String state) {
        return isAcceptingByName.get(state);
    }



        /*
     # Specification format is made up of three parts:
     #  1. A header line with state names and their acceptance status:
        {state1 y, state2 y, state3 n, ...}
     # 2. A line with the initial state name:
        state2
     # 3. A list of allowed transitions for each (state, symbol) pair:
        state1 symbol1 -> state2 state1
        state1 symbol2 -> state3
        state2 symbol2 -> 
     # etc. etc.
     # we can also have epsilon transitions, which are represented as:
        state1 -> state2
        state2 -> state3 state4
    #etc. etc.
     */
    public Transitioner(String Specification, Function<String, Symbol> symbolparser) throws IllegalArgumentException {
        if (Specification == null) throw new IllegalArgumentException("spec is null");

        transitions = new IntObjectHashMap<>();
        epsilonTransitions = new HashMap<>();
        isAcceptingByName = new HashMap<>();
        states = new HashSet<>();

        // Normalize line endings and split
        List<String> lines = Arrays.stream(Specification.replace("\r\n", "\n").replace('\r', '\n').split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !s.startsWith("#")) // allow comments starting with #
                .toList();
        
        if (lines.isEmpty()) throw new IllegalArgumentException("Empty transition specification");

        parseHeader(lines);

        for (String line: lines.subList(2, lines.size())) {
            parseTransitions(line, symbolparser);
        }
    }

    // Format: 
    // {stateName*}
    // initStateName
    private void parseHeader(List<String> lines) throws IllegalArgumentException{
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
    }

    // Each line contains exactly one transition of the form: 
    // stateName readSymbol -> nextState*
    // Or an epsilon transition of the form:
    // stateName -> nextState*
    private void parseTransitions(String line, Function<String, Symbol> symbolparser) throws IllegalArgumentException {
        String[] parts = line.split("\\s+");
        if (parts.length < 3) throw new IllegalArgumentException("Invalid transition format at this line: " + line);
        
        String stateName = parts[0];
        if (!states.contains(stateName)) throw new IllegalArgumentException("String '" + stateName + "' not declared in header");

        // Case 1: epsilon transition (stateName -> nextState*)
        if (parts[1].equals("->")) {
            String[] nextStates = Arrays.asList(parts).subList(2, parts.length).toArray(new String[0]);
            for (String nextStateName : nextStates) if (!states.contains(nextStateName)) throw new IllegalArgumentException("State '" + nextStateName + "' not declared in header");
            
            if (epsilonTransitions.put(stateName, nextStates) != null) throw new IllegalArgumentException("Duplicate epsilon transition for state " + stateName);
            
            return;
        }

        // Case 2: regular transition (stateName readSymbol -> nextState*)
        if (parts.length <= 3 || !parts[2].equals("->")) throw new IllegalArgumentException("Invalid transition format at this line: " + line);

        String readSymbolRaw = parts[1];
        Symbol readSymbol = symbolparser.apply(readSymbolRaw);

        // " -> " is a fixed part of the notation so it carries no semantic meaning, so we can skip it

        // The rest of the parts array are next states
        String[] nextStates = Arrays.asList(parts).subList(3, parts.length).toArray(new String[0]);
        for (String nextStateName: nextStates) if (!states.contains(nextStateName)) throw new IllegalArgumentException("String '" + nextStateName + "' not declared in header");
        
        if (transitions.put(H(stateName, readSymbol), nextStates) != null) throw new IllegalArgumentException("Duplicate transition for (" + stateName + ", " + readSymbolRaw + ")");
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
