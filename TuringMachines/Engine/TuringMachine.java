package TuringMachines.Engine;
import java.util.Map;

public class TuringMachine {
    private Tape tape;
    private State currentState;
    private Map<StateSymbolPair, Transition> transitions;

    public TuringMachine(Tape tape, State initialState, Map<StateSymbolPair, Transition> transitions) {
        this.tape = tape;
        this.currentState = initialState;
        this.transitions = transitions;
    }

    public void setHead(int position){
        tape.setHead(position);
    }
    
    public void run() {
        System.out.println("running turing machine.");
        while (!currentState.isAccepting()) {
            System.out.println("Tape: " + tape.stringify(0, 16) + "  Head: " + tape.getHead()+ "  State: " + currentState);

            Boolean symbol;
            if (currentState.isReadRaw()) {
                symbol = tape.readRaw(); 
            } else {
                symbol = tape.read();    
            }

            Transition transition = transitions.get(new StateSymbolPair(currentState, symbol));

            if (transition == null) {
                System.out.println("Error: No transition found. Halting.");
                break;
            }

            Boolean writeSymbol = transition.getWriteSymbol();
            if (writeSymbol != null) {
                tape.write(writeSymbol);
            }

            if (transition.getMoveDirection() == Transition.Direction.LEFT)
                tape.moveLeft();
            else
                tape.moveRight();

            currentState = transition.getNextState();
        }
    }
}
