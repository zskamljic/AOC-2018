import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day15 {
    private static final int DEFAULT_AP = 3;

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input15.txt"));

        // Part 01
        var field = new Field(lines);
        var didLose = field.doCombat(false);
        if (!didLose) {
            System.out.println("Didn't lose, no action needed");
        }

        // Part 02
        var power = DEFAULT_AP;
        do {
            power++;
            field = new Field(lines, power);
            didLose = field.doCombat(true);
        } while (didLose);
        System.out.println("Final AP was " + power);
    }

    private static int sortedByReading(int[][] graph, Point first, Point second) {
        var distance = graph[first.x][first.y] - graph[second.x][second.y];
        if (distance != 0) return distance;

        return first.compareTo(second);
    }

    private static int[][] dijkstra(char[][] map, Point start) {
        var distances = new int[map.length][map[0].length];
        for (var i = 0; i < distances.length; i++) {
            for (var j = 0; j < distances.length; j++) {
                distances[i][j] = Integer.MAX_VALUE;
            }
        }
        var neighborsX = new int[]{-1, 1, 0, 0};
        var neighborsY = new int[]{0, 0, -1, 1};

        distances[start.x][start.y] = 0;

        var unsettled = new PriorityQueue<Point>(Comparator.comparing(point -> distances[point.x][point.y]));
        unsettled.add(start);

        while (unsettled.size() > 0) {
            var evaluated = unsettled.poll();

            var pendingDistance = distances[evaluated.x][evaluated.y] + 1;
            for (var i = 0; i < neighborsX.length; i++) {
                if (map[evaluated.x + neighborsX[i]][evaluated.y + neighborsY[i]] != '.') continue;
                if (distances[evaluated.x + neighborsX[i]][evaluated.y + neighborsY[i]] <= pendingDistance) continue;
                distances[evaluated.x + neighborsX[i]][evaluated.y + neighborsY[i]] = pendingDistance;
                unsettled.add(new Point(evaluated.x + neighborsX[i], evaluated.y + neighborsY[i]));
            }
        }
        return distances;
    }

    static class Field {
        char[][] map;
        List<Unit> units = new ArrayList<>();

        Field(List<String> lines) { this(lines, DEFAULT_AP); }

        Field(List<String> lines, int power) {
            map = new char[lines.get(0).length()][lines.size()];
            for (var j = 0; j < lines.size(); j++) {
                for (var i = 0; i < lines.get(j).length(); i++) {
                    var value = lines.get(j).charAt(i);
                    map[i][j] = value;
                    if (value == 'G') {
                        units.add(new Unit(value, i, j));
                    } else if (value == 'E') {
                        units.add(new Unit(value, i, j, power));
                    }
                }
            }
        }

        int getScore() {
            return units.stream()
                    .filter(unit -> unit.hp > 0)
                    .mapToInt(unit -> unit.hp).sum();
        }

        boolean doCombat(boolean stopOnDeath) {
            var round = 0;
            var didLoseElf = false;

            combat:
            while (true) {
                if (units.isEmpty()) break;
                // Unit turns
                for (var unit : units) {
                    if (unit.hp <= 0) continue;
                    var targets = identifyTargets(unit);
                    if (targets.isEmpty()) break combat;

                    // Move
                    if (!unit.isInRange(targets)) {
                        // Find enemies
                        var inRange = unit.findInRange(targets, map);
                        // Remove unreachable
                        inRange.removeIf(point -> unreachableFrom(unit, point));

                        // Find closest point
                        var graph = dijkstra(map, unit);
                        var min = inRange.stream().min((l, r) -> sortedByReading(graph, l, r));
                        if (min.isEmpty()) {
                            continue;
                        }

                        var target = min.get();
                        unit.moveTo(target, this);
                    }

                    // Attack
                    var selected = targets.stream()
                            .filter(target -> unit.distance(target) == 1)
                            .min(this::selectAttackTarget);

                    // If none are adjacent finish
                    if (selected.isEmpty()) continue;

                    var target = selected.get();
                    target.damage(unit.attackPower);
                    if (target.hp < 0) {
                        map[target.x][target.y] = '.';
                        if (target.side == 'E') {
                            if (stopOnDeath) return true;
                            else didLoseElf = true;
                        }
                    }
                }

                units.removeIf(units -> units.hp <= 0);
                units.sort(Point::compareTo);
                round++;
            }

            units.removeIf(units -> units.hp <= 0);
            System.out.println("Combat ends after " + round + " rounds.");
            System.out.println("Outcome: " + round + " * " + getScore() + " = " + (round * getScore()));
            return didLoseElf;
        }

        boolean unreachableFrom(Point source, Point point) {
            var searched = new boolean[map.length][map[0].length];
            searched[point.x][point.y] = true;

            var queue = new LinkedList<Point>();
            queue.add(point);
            while (queue.size() > 0) {
                var current = queue.poll();
                if (map[current.x][current.y] != '.') continue;

                if (current.isAdjacentTo(source)) return false;

                if (map[current.x - 1][current.y] == '.' && !searched[current.x - 1][current.y]) {
                    searched[current.x - 1][current.y] = true;
                    queue.add(new Point(current.x - 1, current.y));
                }
                if (map[current.x + 1][current.y] == '.' && !searched[current.x + 1][current.y]) {
                    searched[current.x + 1][current.y] = true;
                    queue.add(new Point(current.x + 1, current.y));
                }
                if (map[current.x][current.y - 1] == '.' && !searched[current.x][current.y - 1]) {
                    searched[current.x][current.y - 1] = true;
                    queue.add(new Point(current.x, current.y - 1));
                }
                if (map[current.x][current.y + 1] == '.' && !searched[current.x][current.y + 1]) {
                    searched[current.x][current.y + 1] = true;
                    queue.add(new Point(current.x, current.y + 1));
                }
            }

            return true;
        }

        List<Unit> identifyTargets(Unit unit) {
            return units.stream()
                    .filter(u -> u.hp > 0)
                    .filter(u -> u.side != unit.side)
                    .collect(Collectors.toList());
        }

        int selectAttackTarget(Unit first, Unit second) {
            if (first.hp < second.hp) return -1;
            if (first.hp > second.hp) return 1;

            return first.compareTo(second);
        }
    }

    static class Unit extends Point {
        char side;
        int attackPower = 3;
        int hp = 200;

        Unit(char side, int x, int y) {
            super(x, y);
            this.side = side;
        }

        Unit(char side, int x, int y, int attackPower) {
            this(side, x, y);
            this.attackPower = attackPower;
        }

        void print() {
            System.out.printf("%c(%d) ", side, hp);
        }

        boolean isInRange(List<Unit> targets) {
            return targets.stream().anyMatch(unit -> distance(unit) == 1);
        }

        List<Point> findInRange(List<Unit> targets, char[][] map) {
            var points = new ArrayList<Point>();
            for (var unit : targets) {
                if (map[unit.x - 1][unit.y] == '.') points.add(new Point(unit.x - 1, unit.y));
                if (map[unit.x + 1][unit.y] == '.') points.add(new Point(unit.x + 1, unit.y));
                if (map[unit.x][unit.y - 1] == '.') points.add(new Point(unit.x, unit.y - 1));
                if (map[unit.x][unit.y + 1] == '.') points.add(new Point(unit.x, unit.y + 1));
            }
            return points;
        }

        void moveTo(Point target, Field field) {
            var map = field.map;
            var distanceGrid = dijkstra(map, target);

            var points = new ArrayList<Point>();
            points.add(new Point(x - 1, y));
            points.add(new Point(x + 1, y));
            points.add(new Point(x, y - 1));
            points.add(new Point(x, y + 1));

            var minDist = points.stream()
                    .mapToInt(point -> distanceGrid[point.x][point.y])
                    .min().orElse(Integer.MAX_VALUE);

            points.removeIf(point -> distanceGrid[point.x][point.y] > minDist);
            points.sort(Point::compareTo);

            map[x][y] = '.';

            x = points.get(0).x;
            y = points.get(0).y;
            map[x][y] = side;
        }

        void damage(int attackPower) {
            hp -= attackPower;
        }
    }

    static class Point implements Comparable<Point> {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        boolean isAdjacentTo(Point point) {
            return distance(point) == 1;
        }

        int distance(Point point) {
            return Math.abs(x - point.x) + Math.abs(y - point.y);
        }

        @Override
        public int compareTo(Point point) {
            if (y < point.y) return -1;
            if (y > point.y) return 1;
            if (x < point.x) return -1;
            return 1;
        }
    }
}
