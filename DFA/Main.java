package DFA;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import DFA.Engine.DFA;

public class Main {
    public static void main(String[] args) throws Exception {
        // Transition spec: accepts strings with a number of 0s that is a multiple of 3
        String spec = """
            {A y, B n, C n}

            A 0 -> B
            A 1 -> A
            B 0 -> C
            B 1 -> B
            C 0 -> A
            C 1 -> C
            """;
        
        String input = IntStream.range(0,10000000).mapToObj(i -> Math.random() < 0.5 ? "0" : "1").collect(Collectors.joining());
        //String input = Collections.nCopies(10000000, "010011010011").stream().collect(Collectors.joining());
        // Our symbols are integers (0 or 1), so parser converts strings to Character
        DFA<Integer> dfa = new DFA<>(
            "A",
            spec,
            input,
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
        int zeroes = (int) input.chars().filter(c -> c == '0').count();
        System.out.println("Number of 0s in input: " + zeroes + ", which is " + (zeroes % 3 == 0 ? "a multiple of 3." : "not a multiple of 3."));
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
