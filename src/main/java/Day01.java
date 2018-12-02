import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeSet;

public class Day01 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input01.txt"));
        part1(lines);
        part2(lines);
    }

    private static void part1(List<String> lines) {
        var frequency = lines.parallelStream()
                .mapToInt(Integer::parseInt).sum();
        System.out.println(frequency);
    }

    private static void part2(List<String> lines) {
        var present = new TreeSet<Integer>();
        var frequency = 0;

        var numbers = lines.stream().mapToInt(Integer::parseInt).toArray();
        for (int i = 0; ; i++, i %= numbers.length) {
            frequency += numbers[i];
            if (present.contains(frequency)) {
                System.out.println(frequency);
                return;
            }
            present.add(frequency);
        }
    }
}
