package TuringMachines.Engine;

public class State {
    private String name;
    private boolean isAccepting;
    private boolean readRaw;  

    public State(String name, boolean isAccepting) {
        this(name, isAccepting, false);
    }

    public State(String name, boolean isAccepting, boolean readRaw) {
        this.name = name;
        this.isAccepting = isAccepting;
        this.readRaw = readRaw;
    }

    public boolean isAccepting() {
        return isAccepting;
    }

    public boolean isReadRaw() {
        return readRaw;
    }

    @Override
    public String toString() {
        return name;
    }
}
