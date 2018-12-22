import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Day22 {
    private static final int[] DIRECTIONS_X = {-1, 0, 1, 0};
    private static final int[] DIRECTIONS_Y = {0, 1, 0, -1};

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input22.txt"));
        var depth = parseDepth(lines.get(0));
        var target = parseTarget(lines.get(1));

        var map = new CaveMap(target, depth);
        var risk = map.calculateRisk(target.x + 1, target.y + 1);
        System.out.println(risk);

        map.saveTarget();
    }

    private static int parseDepth(String line) {
        return Integer.parseInt(line.replaceAll("[^\\d]+", ""));
    }

    private static Point parseTarget(String line) {
        var scanner = new Scanner(line.replaceAll("[^\\d]+", " ").trim());
        return new Point(scanner.nextInt(), scanner.nextInt());
    }

    static class CaveMap {
        byte[][] types;
        int[][] erosionLevel;
        int depth;
        Point target;

        CaveMap(Point target, int depth) {
            this.target = target;
            this.depth = depth;
            var width = target.x * 3;
            var height = target.y * 3;

            types = new byte[width][height];
            erosionLevel = new int[width][height];

            for (var j = 0; j < types[0].length; j++) {
                for (var i = 0; i < types.length; i++) {
                    var result = geologicIndex(i, j);
                    calcTypeFor(result, i, j);
                }
            }
        }

        private void calcTypeFor(int geoIndex, int x, int y) {
            var erosion = (geoIndex + depth) % 20183;
            erosionLevel[x][y] = erosion;
            types[x][y] = (byte) (erosion % 3);
        }

        int geologicIndex(int x, int y) {
            if (x == 0 && y == 0) return 0;
            if (x == target.x && y == target.y) return 0;
            if (y == 0) return x * 16807;
            if (x == 0) return y * 48271;
            return erosionLevel[x - 1][y] * erosionLevel[x][y - 1];
        }

        int calculateRisk(int x, int y) {
            var risk = 0;
            for (var i = 0; i < x; i++) {
                for (var j = 0; j < y; j++) {
                    risk += types[i][j];
                }
            }
            return risk;
        }

        void saveTarget() {
            var visited = new boolean[types.length][types[0].length][3];
            var solution = new int[types.length][types[0].length][3];

            var rescue = new Rescue(0, 0);

            var queue = new PriorityQueue<Rescue>();
            queue.add(rescue);

            while (queue.size() > 0 && !visited[target.x][target.y][1]) {
                var current = queue.poll();
                if (visited[current.x][current.y][current.equiped]) continue;

                visited[current.x][current.y][current.equiped] = true;
                solution[current.x][current.y][current.equiped] = current.distance;

                for (var i = 0; i < DIRECTIONS_X.length; i++) {
                    var temp = new Rescue(current.x + DIRECTIONS_X[i], current.y + DIRECTIONS_Y[i]);
                    temp.equiped = current.equiped;
                    temp.distance = current.distance + 1;

                    if (temp.x >= 0 && temp.y >= 0 && temp.x < types.length && temp.y < types[0].length
                            && types[temp.x][temp.y] != temp.equiped
                            && !visited[temp.x][temp.y][temp.equiped]) {
                        queue.add(temp);
                    }
                }

                for (var i = 0; i < 3; i++) {
                    var temp = new Rescue(current.x, current.y);
                    temp.equiped = i;
                    temp.distance = current.distance + 7;

                    if (types[temp.x][temp.y] != temp.equiped && !visited[temp.x][temp.y][temp.equiped]) {
                        queue.add(temp);
                    }
                }
            }
            System.out.println(solution[target.x][target.y][1]);
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

    static class Rescue extends Point implements Comparable<Rescue> {
        int equiped = 1;
        int distance;

        Rescue(int x, int y) {
            super(x, y);
        }

        @Override
        public int compareTo(Rescue rescue) {
            return distance - rescue.distance;
        }
    }
}
