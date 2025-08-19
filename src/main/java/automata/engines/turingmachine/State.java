package automata.engines.turingmachine;


public record State(String name, boolean isAccepting) {
    @Override
    public String toString() {
        return name;
    }
}