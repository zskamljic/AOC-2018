import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Day21 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input21.txt"));
        var part1 = new Day19.Program(lines);

        part1.setBreakpoint(28, () -> {
            System.out.println(part1.registers[5]);
            return true;
        });
        part1.execute();

        var seen = new TreeSet<Integer>();
        var part2 = new Day19.Program(lines);
        var lastValue = new AtomicInteger();
        part2.setBreakpoint(28, () -> {
            var value = part2.registers[5];
            if (seen.contains(value)) {
                return true;
            }
            lastValue.set(value);
            seen.add(value);
            return false;
        });
        part2.execute();

        System.out.println(lastValue);
    }
}
