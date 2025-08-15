package TuringMachines;
import TuringMachines.Engine.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        // Transition spec: increments a unary number (e.g. 111 becomes 1111)
        String spec = """
            {A, B, C, D, E}

            A 0 -> 1 R B
            A 1 -> 1 L C
            B 0 -> 1 R C
            B 1 -> 1 R B
            C 0 -> 1 R D
            C 1 -> 0 L E
            D 0 -> 1 L A
            D 1 -> 1 L D
            E 0 -> 1 R HALT
            E 1 -> 0 L A
            """;

        // Our symbols are integers (0 or 1), so parser converts strings to Integer
        TuringMachine<Integer> tm = new TuringMachine<>(
            "A",                  // initial state
            0,                        // default symbol on the tape
            spec,                                   // transition specification 
            Integer::parseInt                       // symbol parser
        );

        // Manually preload some tape data (111 and startposition at 1)
        //tm.initializeTape(List.of(1, 1, 1), 1);

        // Run until accepting state (HALT)
        System.out.println("Running 5-state busy beaver");
        long startTime = System.nanoTime();
        int i=1;
        while (!tm.step()) {
            i++;
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Finished after " + i + " steps in " + (duration / 1_000_000) + " ms.");
        System.out.println("Reached accepting state after " + i + " steps with a total of " + tm.countOccurrences(1) + " ones on the tape.");
    }
}