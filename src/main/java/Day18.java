import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day18 {
    private static final int CONVERGE_STEPS = 1_000;
    private static final long TOTAL_STEPS = 1_000_000_000L;

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input18.txt"));
        var forest = new Forest(lines);

        // Part 01
        for (var i = 0; i < 10; i++) {
            forest.nextMinute();
        }

        System.out.println(forest.resourceValue());

        System.out.println();

        // Part 02
        var scores = new ArrayList<Integer>();
        forest = new Forest(lines);
        for (var i = 0; i < CONVERGE_STEPS; i++) {
            forest.nextMinute();
            scores.add(forest.resourceValue());
        }

        var lastValue = scores.get(scores.size() - 1);
        var withoutLast = scores.subList(0, scores.size() - 1);
        var previousIndex = withoutLast.lastIndexOf(lastValue);

        var period = (CONVERGE_STEPS - 1) - previousIndex;
        System.out.println("Period is: " + period);

        var repeatedValues = withoutLast.subList(previousIndex, withoutLast.size());
        var periodStep = (TOTAL_STEPS - previousIndex - 1) % period;

        System.out.println(repeatedValues.get((int) periodStep));
    }

    static class Forest {
        char[][] grid;

        Forest(List<String> lines) {
            grid = new char[lines.get(0).length()][lines.size()];

            for (var j = 0; j < lines.size(); j++) {
                for (var i = 0; i < lines.get(j).length(); i++) {
                    grid[i][j] = lines.get(j).charAt(i);
                }
            }
        }

        void nextMinute() {
            var nextStep = new char[grid[0].length][grid.length];
            for (var j = 0; j < grid.length; j++) {
                for (var i = 0; i < grid[0].length; i++) {
                    process(nextStep, i, j);
                }
            }
            grid = nextStep;
        }

        void process(char[][] target, int x, int y) {
            var trees = 0;
            var lumber = 0;

            for (var i = Math.max(x - 1, 0); i <= Math.min(x + 1, grid[0].length - 1); i++) {
                for (var j = Math.max(y - 1, 0); j <= Math.min(y + 1, grid[0].length - 1); j++) {
                    if (i == x && j == y) continue;
                    switch (grid[i][j]) {
                        case '|':
                            trees++;
                            break;
                        case '#':
                            lumber++;
                            break;
                    }
                }
            }

            switch (grid[x][y]) {
                case '.':
                    if (trees >= 3) target[x][y] = '|';
                    else target[x][y] = '.';
                    break;
                case '|':
                    if (lumber >= 3) target[x][y] = '#';
                    else target[x][y] = '|';
                    break;
                case '#':
                    if (lumber == 0 || trees == 0) target[x][y] = '.';
                    else target[x][y] = '#';
                    break;
            }
        }

        void print() {
            for (var j = 0; j < grid[0].length; j++) {
                for (char[] chars : grid) {
                    System.out.print(chars[j]);
                }
                System.out.println();
            }
        }

        int resourceValue() {
            var wooded = 0;
            var lumberyards = 0;

            for (char[] chars : grid) {
                for (var j = 0; j < grid[0].length; j++) {
                    if (chars[j] == '|') wooded++;
                    else if (chars[j] == '#') lumberyards++;
                }
            }

            return wooded * lumberyards;
        }
    }
}
