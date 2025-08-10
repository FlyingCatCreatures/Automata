package TuringMachines.Engine;

public class Transition {
    private final State nextState;
    private final Boolean writeSymbol; // null indicates no write
    private final Direction moveDirection;

    public enum Direction { LEFT, RIGHT }

    public Transition(State nextState, Boolean writeSymbol, Direction moveDirection) {
        this.nextState = nextState;
        this.writeSymbol = writeSymbol;
        this.moveDirection = moveDirection;
    }

    public State getNextState() {
        return nextState;
    }

    public Boolean getWriteSymbol() {
        return writeSymbol;
    }

    public Direction getMoveDirection() {
        return moveDirection;
    }
}
