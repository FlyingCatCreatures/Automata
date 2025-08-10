package TuringMachines;
import TuringMachines.Engine.*;
import TuringMachines.Engine.Transition.Direction;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Define states
        State moveRightUntilEnd = new State("MOVE_RIGHT", false, true);  
        State add1 = new State("add1", false, true);

        State halt  = new State("HALT", true);

        // Setup initial tape value
        Tape tape = new Tape("00000000000000000", -1); 
        tape.setDefaultSymbol(false);

        // Define transitions
        Map<StateSymbolPair, Transition> transitions = new HashMap<>();
         
        transitions.put(new StateSymbolPair(moveRightUntilEnd, Boolean.FALSE), new Transition(moveRightUntilEnd, Boolean.FALSE, Direction.RIGHT));
        transitions.put(new StateSymbolPair(moveRightUntilEnd, Boolean.TRUE), new Transition(moveRightUntilEnd, Boolean.TRUE, Direction.RIGHT));
        transitions.put(new StateSymbolPair(moveRightUntilEnd, null), new Transition(add1, null, Direction.LEFT));

        transitions.put(new StateSymbolPair(add1, false), new Transition(moveRightUntilEnd, true, Direction.LEFT));
        transitions.put(new StateSymbolPair(add1, true), new Transition(add1, false, Direction.LEFT));
        transitions.put(new StateSymbolPair(add1, null), new Transition(halt, null, Direction.RIGHT));

        TuringMachine tm = new TuringMachine(tape, moveRightUntilEnd, transitions);
        tm.run();
    }
}
