import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day12 {
    private static final int SIZE = 10001;
    private static final long GENERATIONS = 20;

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input12.txt"));
        var config = new Config(lines);

        var state = new char[SIZE];
        var start = SIZE / 2;

        // Part 01
        config.applyInitial(state, start);
        for (var i = 0L; i < GENERATIONS; i++) {
            state = config.applyGeneration(state);
        }

        System.out.println(calcScore(state, start));

        // Part 02
        var score = calcScore(state, start);
        var prevDiff = 0;
        var matchCount = 0; // Count how many times the score is incremented by the same value
        var generation = 21; // 20 generations have already been applied, this starts at 21
        for (var i = 0; i < 1000; i++, generation++) { // Run 1000 generations, hopefully find a convergence
            state = config.applyGeneration(state);
            var newScore = calcScore(state, start);
            var diff = newScore - score;

            if (diff == prevDiff) {
                matchCount++;
            } else {
                matchCount = 0;
                prevDiff = diff;
            }
            score = newScore;

            if (matchCount == 20) break; // 10 is _not_ enough, if so we'd use 69 as the convergence ( ͡° ͜ʖ ͡°)
        }

        // Original score (first 20 gens) + (total_generations - convergence_gen) * diff
        var calculatedScore = (long) score + (50_000_000_000L - generation) * prevDiff;
        System.out.println(calculatedScore);
    }

    private static int calcScore(char[] state, int start) {
        var score = 0;
        for (var i = 0; i < state.length; i++) {
            if (state[i] == '#') score += i - start;
        }
        return score;
    }

    static class Config {
        char[] initialState;
        Set<String> mapper = new TreeSet<>();

        Config(List<String> input) {
            var initialStateString = input.get(0).replaceAll("[^#.]", "");
            initialState = new char[initialStateString.length()];

            for (var i = 0; i < initialStateString.length(); i++) {
                initialState[i] = initialStateString.charAt(i);
            }

            for (var i = 2; i < input.size(); i++) {
                var scanner = new Scanner(input.get(i));
                var pattern = scanner.next();
                scanner.next();
                var result = scanner.next();
                if ("#".equals(result)) {
                    mapper.add(pattern);
                }
            }
        }

        void applyInitial(char[] state, int start) {
            Arrays.fill(state, '.');
            System.arraycopy(initialState, 0, state, start, initialState.length);
        }

        char[] applyGeneration(char[] state) {
            var output = new char[state.length];
            Arrays.fill(output, '.');

            var pattern = new char[5];
            for (var i = 2; i < output.length - 2; i++) {
                System.arraycopy(state, i - 2, pattern, 0, 5);
                if (mapper.contains(new String(pattern))) {
                    output[i] = '#';
                } else {
                    output[i] = '.';
                }
            }

            return output;
        }
    }
}
