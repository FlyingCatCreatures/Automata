package automata.engines;

public interface Engine {
    public boolean step();
    public String toString();
    public boolean isAccepting();
    public default boolean run(){
        while(!step()){
            // Do nothing, just keep stepping
        }
        return isAccepting();
    }
}
