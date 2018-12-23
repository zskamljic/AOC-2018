import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Day23 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input23.txt"));
        //var lines = mockLines();
        var points = lines.stream()
                .map(Point3D::new)
                .collect(Collectors.toList());

        // Part 1
        var inRadius = 0;
        var strongest = points.stream().max(Comparator.comparingLong(point -> point.radius)).orElseThrow();
        for (Point3D point : points) {
            if (strongest.contains(point)) {
                inRadius++;
            }
        }

        System.out.println(inRadius);

        // Part 2
        part2(points);
    }

    private static void part2(List<Point3D> points) {
        var startPosition = new Point3D(0, 0, 0);

        var currentRadius = findRadius(points);

        startPosition.radius = currentRadius;
        var currentNodes = Set.of(startPosition);

        while (currentRadius > 0) {
            currentRadius = currentRadius / 2 + (currentRadius > 2 ? 1 : 0);

            long finalCurrentRadius = currentRadius;
            var nextNodes = currentNodes.stream().flatMap(point -> {
                var neighbours = point.neighbours(finalCurrentRadius);
                neighbours.forEach(n -> n.radius = finalCurrentRadius);

                return neighbours.stream().map(p -> matchIntersecting(p, points));
            }).collect(Collectors.toList());

            var maxDistance = nextNodes.stream()
                    .mapToLong(entry -> entry.second)
                    .max()
                    .orElse(0);
            currentNodes = nextNodes.stream()
                    .filter(n -> n.second == maxDistance)
                    .map(n -> n.first)
                    .collect(Collectors.toSet());
        }

        currentNodes.stream()
                .min(Comparator.comparing(startPosition::distanceTo))
                .map(point -> point.distanceTo(new Point3D(0, 0, 0)))
                .ifPresent(System.out::println);
    }

    private static Pair<Point3D, Long> matchIntersecting(Point3D point, List<Point3D> points) {
        return new Pair<>(point, points.stream().filter(point::intersects).count());
    }

    private static long findRadius(List<Point3D> points) {
        var x = range(points, point -> point.x);
        var y = range(points, point -> point.y);
        var z = range(points, point -> point.z);

        return LongStream.of(x, y, z).max().orElse(0);
    }

    private static long range(List<Point3D> points, ToLongFunction<Point3D> mapper) {
        var max = points.stream().mapToLong(mapper).max().orElse(0);
        var min = points.stream().mapToLong(mapper).min().orElse(0);
        return Math.abs(max - min);
    }

    private static List<String> mockLines() {
        return List.of(
                "pos=<10,12,12>, r=2",
                "pos=<12,14,12>, r=2",
                "pos=<16,12,12>, r=4",
                "pos=<14,14,14>, r=6",
                "pos=<50,50,50>, r=200",
                "pos=<10,10,10>, r=5"
        );
    }

    static class Pair<L, R> {
        L first;
        R second;

        Pair(L first, R second) {
            this.first = first;
            this.second = second;
        }
    }

    static class Point3D {
        long x;
        long y;
        long z;
        long radius;

        Point3D(String line) {
            var scanner = new Scanner(line.replaceAll("[^\\d-]+", " ").trim());
            x = scanner.nextLong();
            y = scanner.nextLong();
            z = scanner.nextLong();
            radius = scanner.nextLong();
        }

        Point3D(long x, long y, long z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        long distanceTo(Point3D point) {
            return Math.abs(x - point.x) + Math.abs(y - point.y) + Math.abs(z - point.z);
        }

        boolean contains(Point3D point) {
            return distanceTo(point) <= radius;
        }

        boolean intersects(Point3D point) {
            return distanceTo(point) <= radius + point.radius;
        }

        Set<Point3D> neighbours(long radius) {
            var items = new HashSet<Point3D>();
            for (var x = -1; x <= 1; x++) {
                for (var y = -1; y <= 1; y++) {
                    for (var z = -1; z <= 1; z++) {
                        items.add(new Point3D(this.x + x * radius, this.y + y * radius, this.z + z * radius));
                    }
                }
            }
            return items;
        }
    }
}
