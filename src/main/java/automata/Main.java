package automata;

import java.util.List;

import automata.engines.turingmachine.TuringMachine;
import automata.util.IntArrayListView;
import automata.engines.Engine;
import automata.engines.dfa.DFA;
import automata.engines.nfa.NFA;


public class Main {
    private static void mainTM() throws Exception {
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
        long durationMS = duration / 1_000_000;
        System.out.println("Finished " + formatNumber(i) + " steps in " + formatNumber(durationMS) + " ms.");
        System.out.println("Stepping rate: " + formatNumber(i/durationMS) + " steps/ms");
        System.out.println("Reached accepting state after " + formatNumber(i) + " steps with a total of " + tm.countOccurrences(1) + " ones on the tape.");
    }

    private static void mainDFA () throws Exception {
        // Transition spec: accepts strings with a number of 0s that is a multiple of 3
        String spec = """
            {A y, B n, C n}
            A

            A 0 -> B
            A 1 -> A
            B 0 -> C
            B 1 -> B
            C 0 -> A
            C 1 -> C
            """;
        Engine<Integer> dfa = new DFA<>(spec, Integer::valueOf);

        int length = 100_000_000;
        int[] inputArr = new int[length];
        for(int i=0; i<length; i++) {
            // Generate a random input of 0s and 1s
            inputArr[i] = (int) (Math.random() * 2);
        }
        List<Integer> inputView = new IntArrayListView(inputArr); // we don't want to actually box the integers because it ruins performance, so we use this view 

        System.out.println("Running DFA");
        long startTime = System.nanoTime();
        dfa.run(inputView);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        long durationMS = duration / 1_000_000;
        System.out.println("Finished " + formatNumber(length) + " steps in " + durationMS + " ms.");
        System.out.println("Stepping rate: " + formatNumber(length/durationMS) + " steps/ms");
        System.out.println("The input string was "+ (dfa.isAccepting() ? "accepted ": "rejected ") + "by the DFA.");
        int zeroes = (int) inputView.stream().filter(c -> c == 0).count();
        System.out.println("Number of 0s in input: " + zeroes + ", which is " + (zeroes % 3 == 0 ? "a multiple of 3." : "not a multiple of 3."));
    }

    private static void mainNFA () throws Exception {
        // Accepts: strings that end on "01" OR have an even number of '1's
        String spec = """
            {S n, P0 y, P1 n, X n, U n, Y y}
            S

            # Epsilon split from the start (forces epsilon-closure work)
            S -> P0 X

            # Branch A: parity of '1's (P0 = even, P1 = odd)
            P0 0 -> P0
            P0 1 -> P1
            P1 0 -> P1
            P1 1 -> P0

            # Branch B: detect substring "01" at end
            # X: looking for a 0 that could start "01"
            # U: have seen a 0; a following 1 completes "01"
            # Y: once "01" seen, go back to X on next transition
            X 0 -> U
            X 1 -> X
            U 0 -> U
            U 1 -> Y
            Y 0 -> X
            Y 1 -> X
            """;


        Engine<Integer> nfa = new NFA<>(spec, Integer::valueOf);


        int length = 10_000_000;
        
        int[] inputArr = new int[length];
        for (int i = 0; i < length; i++) {
            inputArr[i] = (int) (Math.random() * 2); // random 0/1
        }
        List<Integer> inputView = new IntArrayListView(inputArr);

        System.out.println("Running NFA");
        long startTime = System.nanoTime();
        nfa.run(inputView);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        long durationMS = duration / 1_000_000;
        System.out.println("Finished " + formatNumber(length) + " steps in " + durationMS + " ms.");
        System.out.println("Stepping rate: " + formatNumber(length/durationMS) + " steps/ms");

        int ones = 0;
        boolean has01 = inputArr[inputArr.length - 2] == 0 && inputArr[inputArr.length - 1] == 1;
        for (int i = 0; i < inputArr.length; i++) {
            if (inputArr[i] == 1) {
                ones++;
            }
        }

        boolean expected = has01 || (ones % 2 == 0);

        System.out.println("The input string should have been " + (expected ? "accepted " : "rejected ") + "by the NFA");
        System.out.println("The input string was " + (nfa.isAccepting() ? "accepted " : "rejected ") + "by the NFA.");
    }
    
    private static String formatNumber(long n) {
        double num = n;
        if (num >= 1_000_000) {
            return String.format("%.2fM", num / 1_000_000);
        } else if (num >= 1_000) {
            return String.format("%.2fk", num / 1_000);
        } else {
            return String.format("%.2f", num);
        }
    }

    public static void main(String[] args) throws Exception {
        if(args.length !=1) {
            System.out.println("Expected one argument: <engine>");
            System.out.println("Where <engine> is either 'tm', 'dfa', or 'nfa");
            System.out.println("Pass engine using --args=\"...\"");
            return;
        }

        String engine = args[0].toLowerCase();
        if(engine.equals("tm")) {
            mainTM();
        } else if(engine.equals("dfa")) {
            mainDFA();
        } else if(engine.equals("nfa")) {
            mainNFA();
        } else {
            System.out.println("Unknown engine: " + engine);
            System.out.println("Expected one argument: <engine>");
            System.out.println("Where <engine> is either 'tm', 'dfa', or 'nfa");
            System.out.println("Pass engine using --args=\"...\"");

        }
        
    }
}