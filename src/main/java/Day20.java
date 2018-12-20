import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Day20 {
    private static int MAP_SIZE = 1000;
    private static int[] OFFSET_X = {-1, 0, 1, 0}; // Left, down, right, up
    private static int[] OFFSET_Y = {0, 1, 0, -1};
    private static int[][] distances = new int[MAP_SIZE][MAP_SIZE];

    public static void main(String[] args) throws IOException {
        var regex = Files.readString(Paths.get("input20.txt"));

        // Remove start & end indicators
        regex = regex.substring(1, regex.length() - 2);

        // Init array
        for (int i = 0; i < MAP_SIZE; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
        }

        // Set center point
        var center = MAP_SIZE / 2;
        distances[center][center] = 0;
        shortestPath(regex, center, center);

        // Part 01
        var solution = 0;
        for (var i = 0; i < MAP_SIZE; i++) {
            for (var j = 0; j < MAP_SIZE; j++) {
                if (distances[i][j] == Integer.MAX_VALUE) continue;

                solution = Math.max(solution, distances[i][j]);
            }
        }

        System.out.println(solution);

        // Part 02
        solution = 0;
        for (var i = 0; i < MAP_SIZE; i++) {
            for (var j = 0; j < MAP_SIZE; j++) {
                if (distances[i][j] == Integer.MAX_VALUE) continue;

                if (distances[i][j] >= 1000) solution++;
            }
        }

        System.out.println(solution);
    }

    private static void shortestPath(String s, int x, int y) {
        var prefixLen = 0;
        // Handle prefix
        while (prefixLen < s.length()) {
            var next = s.charAt(prefixLen);
            if (next == '(') break;

            var nextX = x + OFFSET_X[toIndex(next)];
            var nextY = y + OFFSET_Y[toIndex(next)];

            distances[nextX][nextY] = Math.min(distances[x][y] + 1, distances[nextX][nextY]);

            x = nextX;
            y = nextY;

            prefixLen++;
        }

        // Save coordinates
        var previousX = x;
        var previousY = y;

        if (prefixLen == s.length()) return;

        // find all branches between prefix and suffix
        var branchEnd = prefixLen + 1;
        var branches = 1;
        while (true) {
            if (s.charAt(branchEnd) == '(') branches++;
            else if (s.charAt(branchEnd) == ')') branches--;

            if (branches == 0) break;

            branchEnd++;
        }

        // String between first ( and last )
        var subRegex = s.substring(prefixLen + 1, branchEnd);

        while (true) {
            var index = 0;
            var branchPart = 0;

            // Same as before, except with multiple options
            while (index < subRegex.length()) {
                var next = subRegex.charAt(index);
                if (next == '(') branchPart++;
                else if (next == ')') branchPart--;
                else if (branchPart == 0 && next == '|') break;

                index++;
            }

            // handle the selected part
            shortestPath(subRegex.substring(0, index), x, y);

            // if done with current string
            if (index == subRegex.length()) break;

            // continue with next part
            subRegex = subRegex.substring(index + 1);
        }

        // If anything remains, handle that
        if (branchEnd < s.length() - 1) shortestPath(s.substring(branchEnd + 1), previousX, previousY);
    }

    private static int toIndex(char c) {
        switch (c) {
            case 'N':
                return 0;
            case 'E':
                return 1;
            case 'S':
                return 2;
            case 'W':
                return 3;
            default:
                return -1;
        }
    }
}
