package automata.engines.turingmachine;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Transitioner<Symbol> {
    public record Output<Symbol>(State state, Symbol writeSymbol, int moveDirection) {}
    private Map<Integer, Output<Symbol>> transitions;

    public Output<Symbol> get(State state, Symbol readSymbol) {
        return transitions.get(InputHash(state, readSymbol));
    }

    public Transitioner(Map<Integer, Output<Symbol>> transitions) {
        this.transitions = transitions;
    }
    /*
     * Format of transitions is a declaration of states used, like this:
     * {state1, state2, state3, ...}
     * 
     * # Followed by a list of transitions, like:
     * state1 symbol1 -> symbol2 L state2
     * state1 symbol2 -> symbol3 R state3
     * etc. etc.
     * 
     * Special HALT and H state is also available, which halt the machine.
     */
    public Transitioner(String Specification, Function<String, Symbol> symbolparser) throws IllegalArgumentException {
        if (Specification == null) throw new IllegalArgumentException("spec is null");

        // Normalize line endings and split
        List<String> lines = Arrays.stream(Specification.replace("\r\n", "\n").replace('\r', '\n').split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !s.startsWith("#")) // allow comments starting with #
                .toList();

        Map<Integer, Output<Symbol>> map = new LinkedHashMap<>();
        Map<String, State> statesByName = new LinkedHashMap<>();

        

        if (lines.isEmpty()) throw new IllegalArgumentException("Empty transition specification");
        


        // First we parse the header line with state names
        // Format: {stateName*}
        // Semantics: Declare the existence of all states used except HALT.
        String stateHeader = lines.get(0);
        if(!stateHeader.startsWith("{") || !stateHeader.endsWith("}")) throw new IllegalArgumentException("Expected a state header at the start of the specification; got: " + lines.get(0) + "\nExpected format is '{stateName1, stateName2, ...}'");
        String inside = stateHeader.substring(1, stateHeader.length() - 1);
        for (String raw : inside.split(",")) {
            String name = raw.trim();
            if(name.isEmpty()) throw new IllegalArgumentException("Empty state name in header: " + stateHeader);
            statesByName.putIfAbsent(name, new State(name, false));
        }

        // HALT is not included in the header, so we have to add it explicitly
        statesByName.putIfAbsent("HALT", new State("HALT", true)); 
        statesByName.putIfAbsent("H", new State("H", true)); 

        

        // Now we parse the transitions for each state.
        // Each line contains exactly one transition of the form: 
        // stateName readSymbol -> nextState writeSymbol direction
        for (String line: lines.subList(1, lines.size())) {
            String[] parts = line.split("\\s+");
            if (parts.length != 6 || !parts[2].equals("->")) {
                throw new IllegalArgumentException("Invalid transition format at this line: " + line + "\nExpected format is: stateName readSymbol -> nextState writeSymbol direction");
            }

            String stateName = parts[0];
            String readSymbolRaw = parts[1];
            // " -> " is a fixed part of the notation so it carries no semantic meaning, so we can skip it
            String writeSymbolRaw = parts[3];
            String directionRaw = parts[4];
            String nextStateName = parts[5];

            State state = statesByName.get(stateName);
            if (state == null) throw new IllegalArgumentException("State '" + stateName + "' not declared in header");
            
            State nextState = statesByName.get(nextStateName);
            if (nextState == null) throw new IllegalArgumentException("Next state '" + nextStateName + "' not declared in header or HALT");
            
            Symbol readSymbol = symbolparser.apply(readSymbolRaw);
            Symbol writeSymbol = symbolparser.apply(writeSymbolRaw);
            int direction = parseDirection(directionRaw);

            Integer input = InputHash(state, readSymbol);
            Output<Symbol> output = new Output<>(nextState, writeSymbol, direction);

            if (map.put(input, output) != null) {
                throw new IllegalArgumentException("Duplicate transition for (" + stateName + ", " + readSymbolRaw + ")");
            }
        }

        this.transitions = Collections.unmodifiableMap(map);
    }

    // ---------- Helpers ----------

    private static int parseDirection(String token) throws IllegalArgumentException {
        String t = token.trim().toUpperCase();
        switch (t) {
            case "L": return Direction.LEFT;
            case "R": return Direction.RIGHT;
            default:  throw new IllegalArgumentException("Unknown direction: " + token + " (use L/R)");
        }
    }

    private int InputHash(State state, Symbol readSymbol) {
        return state.hashCode() ^ readSymbol.hashCode();
    }
}
