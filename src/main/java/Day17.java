import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day17 {
    private static final int SOURCE_X = 500;
    private static final int SOURCE_Y = 0;

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input17.txt"));

        var grid = new Grid(lines);
        grid.settle();
        System.out.println(grid.countWater());
        System.out.println(grid.retainedWater());
    }

    static class Grid {
        private final int maxX;
        private final int maxY;
        private final int minX;
        private final int minY;
        char[][] grid;

        Grid(List<String> lines) {
            var marks = lines.stream().map(Vein::new).collect(Collectors.toList());

            maxX = marks.stream().mapToInt(vein -> vein.xEnd).max().orElseThrow() + 1; // +1 for index
            maxY = marks.stream().mapToInt(vein -> vein.yEnd).max().orElseThrow() + 1; // +1 for index

            minX = marks.stream().mapToInt(vein -> vein.xStart).min().orElseThrow() - 1; // No need to handle anything to the left of leftmost bowl
            minY = marks.stream().mapToInt(vein -> vein.yStart).min().orElseThrow(); // For minimum counted

            grid = new char[maxX + 1][maxY]; // +1 so water can flow past rightmost wall
            grid[SOURCE_X][SOURCE_Y] = '+';

            marks.forEach(vein -> vein.apply(grid));
        }

        void settle() {
            var flow = new LinkedList<Point>();
            var settle = new LinkedList<Point>();

            flow.add(new Point(SOURCE_X, SOURCE_Y));
            while (flow.size() > 0) {
                var source = flow.poll();
                for (var i = source.y; i < maxY; i++) {
                    // Can flow?
                    if (grid[source.x][i] == 0) {
                        grid[source.x][i] = '|';
                    } else if (grid[source.x][i] == '#' || grid[source.x][i] == '~') { // Settled water behaves like clay
                        settle.add(new Point(source.x, i - 1));
                        break;
                    }
                }

                // Settle all pending, find new "sources"
                while (settle.size() > 0) {
                    var stuck = settle.poll();
                    flow.addAll(resolveHorizontal(stuck)); // Add all holes to flow resolution

                    // Water settled
                    if (grid[stuck.x][stuck.y] == '~') {
                        // We're at the source, no search needed
                        if (grid[stuck.x][stuck.y - 1] == '|') {
                            settle.add(new Point(stuck.x, stuck.y - 1));
                        } else {
                            // Where did we fill from?
                            for (var i = stuck.x; i > 0 && grid[i][stuck.y] == '~'; i--) {
                                if (grid[i][stuck.y - 1] == '|') {
                                    settle.add(new Point(i, stuck.y - 1));
                                }
                            }
                            for (var i = stuck.x; i < maxX && grid[i][stuck.y] == '~'; i++) {
                                if (grid[i][stuck.y - 1] == '|') {
                                    settle.add(new Point(i, stuck.y - 1));
                                }
                            }
                        }
                    }
                }
            }
        }

        private Set<Point> resolveHorizontal(Point point) {
            var canSettle = point.y != maxY;
            var left = point.x;
            var right = point.x;

            var verticalHoles = new HashSet<Point>();

            // Find holes to the left
            while (left > 0 && grid[left][point.y] != '#') {
                grid[left][point.y] = '|';
                if (checkHole(left, point.y, verticalHoles)) {
                    canSettle = false;
                    break;
                }
                left--;
            }

            // Find holes to the right
            while (right < maxX && grid[right][point.y] != '#') {
                grid[right][point.y] = '|';
                if (checkHole(right, point.y, verticalHoles)) {
                    canSettle = false;
                    break;
                }
                right++;
            }

            // No holes found, fill her up boys!
            if (canSettle) {
                for (var i = left + 1; i <= right - 1; i++) {
                    grid[i][point.y] = '~';
                }
            }
            return verticalHoles;
        }

        boolean checkHole(int x, int y, Set<Point> holes) {
            if (grid[x][y + 1] == 0) {
                holes.add(new Point(x, y)); // Found a hole!
                return true;
            } else return grid[x][y + 1] == '|'; // | is a hole, but we're not going to resolve it, probably already pending
        }

        int countWater() {
            var count = 0;
            for (char[] chars : grid) {
                for (var j = minY; j < grid[0].length; j++) {
                    if (chars[j] == '|' || chars[j] == '~') count++;
                }
            }
            return count;
        }

        int retainedWater() {
            var count = 0;
            for (char[] chars : grid) {
                for (var j = 0; j < grid[0].length; j++) {
                    if (chars[j] == '~') count++;
                }
            }
            return count;
        }
    }

    static class Vein {
        int xStart;
        int xEnd;
        int yStart;
        int yEnd;

        Vein(String line) {
            var numbersOnly = line.replaceAll("[^\\d]+", " ").trim();
            var scanner = new Scanner(numbersOnly);

            if (line.startsWith("x")) {
                xStart = scanner.nextInt();
                xEnd = xStart;
                yStart = scanner.nextInt();
                yEnd = scanner.nextInt();
            } else {
                yStart = scanner.nextInt();
                yEnd = yStart;
                xStart = scanner.nextInt();
                xEnd = scanner.nextInt();
            }
        }

        void apply(char[][] grid) {
            for (var i = xStart; i <= xEnd; i++) {
                for (var j = yStart; j <= yEnd; j++) {
                    grid[i][j] = '#';
                }
            }
        }
    }

    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
