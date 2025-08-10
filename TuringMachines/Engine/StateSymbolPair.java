package TuringMachines.Engine;

public class StateSymbolPair {
    private final State state;
    private final Boolean symbol;  

    public StateSymbolPair(State state, Boolean symbol) {
        this.state = state;
        this.symbol = symbol;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StateSymbolPair)) return false;
        StateSymbolPair that = (StateSymbolPair) o;
        return state.equals(that.state) && 
               (symbol == null ? that.symbol == null : symbol.equals(that.symbol));
    }

    @Override
    public int hashCode() {
        return 31 * state.hashCode() + (symbol == null ? 0 : symbol.hashCode());
    }
}


