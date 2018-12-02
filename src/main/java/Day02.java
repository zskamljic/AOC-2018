import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class Day02 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input02.txt"));

        part1(lines);
        part2(lines);
    }

    private static void part1(List<String> lines) {
        var twos = 0;
        var threes = 0;
        for (var line : lines) {
            var result = processLine(line);
            if (result.two) twos++;
            if (result.three) threes++;
        }

        System.out.println(twos * threes);
    }

    private static Tracker processLine(String line) {
        var counters = new HashMap<Character, Integer>();
        for (var character : line.toCharArray()) {
            var repeats = counters.getOrDefault(character, 0);
            counters.put(character, repeats + 1);
        }
        return new Tracker(counters.values().stream().anyMatch(i -> i == 2),
                counters.values().stream().anyMatch(i -> i == 3));
    }

    private static class Tracker {
        boolean two;
        boolean three;

        Tracker(boolean two, boolean three) {
            this.two = two;
            this.three = three;
        }
    }

    private static void part2(List<String> lines) {
        for (int i = 0; i < lines.size() - 1; i++) {
            for (int j = i + 1; j < lines.size(); j++) {
                var lineA = lines.get(i);
                var lineB = lines.get(j);
                if (close(lineA, lineB)) {
                    System.out.println(trimmed(lineA, lineB));
                    return;
                }
            }
        }
    }

    private static boolean close(String a, String b) {
        var diff = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) diff++;
            if (diff > 1) return false;
        }
        return true;
    }

    private static String trimmed(String a, String b) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == b.charAt(i)) {
                result.append(a.charAt(i));
            }
        }
        return result.toString();
    }
}
