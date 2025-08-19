package DFA;

import java.util.Collections;
import java.util.stream.Collectors;

import DFA.Engine.DFA;

public class Main {
    public static void main(String[] args) throws Exception {
        // Transition spec: accepts strings with an even number of 0s
        String spec = """
            {A y, B n}

            A 0 -> B
            A 1 -> A
            B 0 -> A
            B 1 -> B
            """;

        // Our symbols are characters (0 or 1), so parser converts strings to Character
        DFA<Integer> dfa = new DFA<>(
            "A",
            spec,
            Collections.nCopies(10000000, "010011010011").stream().collect(Collectors.joining()), // Large input string
            Integer::valueOf
        );

        System.out.println("Running DFA");
        long startTime = System.nanoTime();
        int i=1;
        while (!dfa.step()) {
            i++;
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        long durationMS = duration / 1_000_000;
        System.out.println("Finished " + formatNumber(i) + " steps in " + durationMS + " ms.");
        System.out.println("Stepping rate: " + formatNumber(i/durationMS) + " steps/ms");
        System.out.println("The input string was "+ (dfa.isAccepting() ? "accepted ": "rejected ") + "by the DFA.");
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
}
