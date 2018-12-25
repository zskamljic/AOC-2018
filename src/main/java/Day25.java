import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day25 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input25.txt"));

        var points = lines.stream()
                .map(String::trim)
                .map(line -> line.split(","))
                .map(items -> Stream.of(items).mapToInt(Integer::parseInt).toArray())
                .collect(Collectors.toList());

        var constellations = points.stream().map(point -> {
            var set = new HashSet<int[]>();
            set.add(point);
            return set;
        }).collect(Collectors.toCollection(ArrayList::new));

        for (var i = 0; i < points.size(); i++) {
            var a = points.get(i);
            for (var j = 0; j < i; j++) {
                var b = points.get(j);
                if (distance(a, b) <= 3) {
                    merge(constellations, a, b);
                }
            }
        }
        System.out.println(constellations.size());
    }

    private static void merge(ArrayList<HashSet<int[]>> constellations, int[] a, int[] b) {
        Set<int[]> first = null;
        Set<int[]> second = null;

        for (var constellation : constellations) {
            if (constellation.contains(a)) first = constellation;
            if (constellation.contains(b)) second = constellation;
            if (first != null && second != null) break;
        }
        if (first == null || second == null) throw new RuntimeException("No such items!");
        if (first == second) return;

        first.addAll(second);
        constellations.remove(second);
    }

    private static int distance(int[] a, int[] b) {
        var dist = 0;
        for (var i = 0; i < a.length; i++) {
            dist += Math.abs(a[i] - b[i]);
        }
        return dist;
    }
}
