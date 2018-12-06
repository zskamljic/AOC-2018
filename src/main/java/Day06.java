import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Day06 {
    private static final int MAX_DIST = 10000;

    public static void main(String[] args) throws IOException {
        var points = Files.readAllLines(Paths.get("input06.txt"))
                .stream()
                .map(Point::new)
                .collect(Collectors.toList());

        // Find bounds of rect
        var minX = Integer.MAX_VALUE;
        var maxX = Integer.MIN_VALUE;
        var minY = Integer.MAX_VALUE;
        var maxY = Integer.MIN_VALUE;
        for (var point : points) {
            if (point.x < minX) minX = point.x;
            if (point.x > maxX) maxX = point.x;
            if (point.y < minY) minY = point.y;
            if (point.y > maxY) maxY = point.y;
        }

        part01(minX, maxX, minY, maxY, points);
        part02(minX, maxX, minY, maxY, points);
    }

    private static void part01(int minX, int maxX, int minY, int maxY, List<Point> points) {
        var counters = new HashMap<Point, Integer>();

        // Loop over points in rect
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                var minDistance = Integer.MAX_VALUE;
                var isShared = false;
                var nearestPoint = points.get(0);

                // Calculate distances
                for (var point : points) {
                    var distance = point.manhattan(i, j);
                    if (distance < minDistance) { // update minimum
                        minDistance = distance;
                        isShared = false;
                        nearestPoint = point;
                    } else if (distance == minDistance) { // not the only point there
                        isShared = true;
                    }
                }

                // More than one point at the distance
                if (isShared) continue;

                // Increment count for point
                var count = counters.getOrDefault(nearestPoint, 0);
                count++;
                counters.put(nearestPoint, count);
            }
        }

        // Ignore border points
        var maxCovered = 0;
        for (var entry : counters.entrySet()) {
            var point = entry.getKey();
            if (point.x == minX || point.x == maxX || point.y == minY || point.y == maxY) continue;
            if (entry.getValue() > maxCovered) {
                maxCovered = entry.getValue();
            }
        }

        System.out.println(maxCovered);
    }

    private static void part02(int minX, int maxX, int minY, int maxY, List<Point> points) {
        var count = 0;

        // Loop over points in rect
        for (int i = minX; i <= maxX; i++) {
            yLoop:
            for (int j = minY; j <= maxY; j++) {
                var sum = 0;

                for (var point : points) {
                    sum += point.manhattan(i, j);
                    if (sum > MAX_DIST) continue yLoop; // We've exceeded the limit, don't need to check other points
                }

                count++;
            }
        }

        System.out.println(count);
    }

    static class Point {
        int x;
        int y;

        Point(String string) {
            var parts = string.split(", ");
            x = Integer.parseInt(parts[0]);
            y = Integer.parseInt(parts[1]);
        }

        int manhattan(int x, int y) {
            return Math.abs(this.x - x) + Math.abs(this.y - y);
        }
    }
}
