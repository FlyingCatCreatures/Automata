package automata.engines.turingmachine;

import java.util.List;
import java.util.function.Function;

public class TuringMachine<Symbol> {
    private Tape<Symbol>tape;
    private String currentState;
    private Transitioner<Symbol> transitioner;

    public TuringMachine(String initialStateName, Symbol defaultSymbol, Transitioner<Symbol> transitioner) {
        this.tape = new Tape<>(defaultSymbol);
        this.currentState = initialStateName;
        this.transitioner = transitioner;
    }

    public TuringMachine(String initialStateName, Symbol defaultSymbol, String transitionerSpecification, Function<String, Symbol> symbolparser) throws Exception {
        this.tape = new Tape<>(defaultSymbol);
        this.currentState = initialStateName;
        this.transitioner = new Transitioner<>(transitionerSpecification, symbolparser);
    }
    
    public void initializeTape(List<Symbol> symbols, int startHeadPosition) {
        for (Symbol symbol : symbols) {
            tape.writeAndMove(symbol, Direction.RIGHT);
        }
        tape.setHead(startHeadPosition);
    }
    public boolean step() {
        Symbol readVal = tape.read();
        Transitioner.Output<Symbol> transition = transitioner.get(currentState, readVal);
        tape.writeAndMove(transition.writeSymbol(), transition.moveDirection());
        currentState = transition.state();
        return isAccepting();
    }

    public String stringify(int tapeStart, int tapeEnd) {
        return "Current State: " + currentState + ", " + tape.stringify(tapeStart, tapeEnd) + ", Head Position: " + tape.getHead();    
    }
    public String toString(){
        return "Current State: " + currentState + ", " + tape.toString()                    + ", Head Position: " + tape.getHead();    
    }

    public int countOccurrences(Symbol symbol) {
        return tape.countOccurrences(symbol);
    }

    public boolean isAccepting() {
        return currentState.equals("H") || currentState.equals("HALT");
    }
}