import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day10 {
    public static void main(String[] args) throws IOException {
        var points = Files.readAllLines(Paths.get("input10.txt"))
                .stream()
                .map(Day10::removeNonNumeric)
                .map(Point::new)
                .collect(Collectors.toList());

        var duration = 1;
        movement:
        while (true) {
            points.forEach(Point::move);

            var yPosition = points.get(0).y;
            for (var point : points) {
                if (Math.abs(yPosition - point.y) > 8) {
                    duration++;
                    continue movement;
                }
            }
            break;
        }

        // Part 01
        printMap(points);

        // Part 02
        System.out.println(duration);
    }

    private static String removeNonNumeric(String s) {
        return s.replaceAll("[^\\d\\- ]+", "");
    }

    private static Map<Integer, Map<Integer, Object>> toMap(List<Point> points) {
        var map = new HashMap<Integer, Map<Integer, Object>>();
        for (var point : points) {
            if (!map.containsKey(point.y)) map.put(point.y, new HashMap<>());

            map.get(point.y).put(point.x, 0);
        }

        return map;
    }

    private static void printMap(List<Point> points) {
        var minX = points.stream().min(Comparator.comparing(point -> point.x)).orElseThrow().x;
        var minY = points.stream().min(Comparator.comparing(point -> point.y)).orElseThrow().y;
        var maxX = points.stream().max(Comparator.comparing(point -> point.x)).orElseThrow().x;
        var maxY = points.stream().max(Comparator.comparing(point -> point.y)).orElseThrow().y;

        var map = toMap(points);
        for (var i = minY; i <= maxY; i++) {
            for (var j = minX; j <= maxX; j++) {
                if (!map.containsKey(i) || !map.get(i).containsKey(j)) System.out.print(".");
                else System.out.print("#");
            }
            System.out.println();
        }
    }

    static class Point {
        int x;
        int y;
        int speedX;
        int speedY;

        Point(String line) {
            var scanner = new Scanner(line);
            x = scanner.nextInt();
            y = scanner.nextInt();
            speedX = scanner.nextInt();
            speedY = scanner.nextInt();
        }

        void move() {
            x += speedX;
            y += speedY;
        }
    }
}
