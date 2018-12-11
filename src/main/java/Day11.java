import java.util.Comparator;
import java.util.stream.IntStream;

public class Day11 {
    private static final int SERIAL_NUMBER = 18;
    private static final int GRID_SIZE = 300;

    public static void main(String[] args) {
        var grid = new Integer[GRID_SIZE][GRID_SIZE];

        // Part 01
        calcSize(grid, 3).print();

        // Part 02
        var max = IntStream.range(1, 300)
                .parallel()
                .mapToObj(i -> calcSize(grid, i))
                .max(Comparator.comparing(info -> info.value));
        max.ifPresent(Info::print);
    }

    private static Info calcSize(Integer[][] grid, int gridSize) {
        var info = new Info();
        info.size = gridSize;
        for (var j = 0; j < GRID_SIZE - (gridSize - 1); j++) {
            for (var i = 0; i < GRID_SIZE - (gridSize - 1); i++) {
                var sum = calcSum(grid, i, j, gridSize);
                if (sum > info.value) {
                    info.value = sum;
                    info.x = i;
                    info.y = j;
                }
            }
        }
        return info;
    }

    private static int calcSum(Integer[][] grid, int x, int y, int size) {
        var sum = 0;

        for (var j = 0; j < size; j++) {
            for (var i = 0; i < size; i++) {
                if (grid[x + i][y + j] == null) {
                    grid[x + i][y + j] = calcCell(x + i, y + j);
                }
                sum += grid[x + i][y + j];
            }
        }
        return sum;
    }

    private static int calcCell(int x, int y) {
        x++;
        y++;
        var rackId = x + 10;
        var initialLevel = rackId * y;
        var product = (initialLevel + SERIAL_NUMBER) * rackId / 100 % 10;
        return product - 5;
    }

    static class Info {
        int x, y, value, size;

        @Override
        public String toString() {
            return "Info{" +
                    "x=" + x +
                    ", y=" + y +
                    ", value=" + value +
                    ", size=" + size +
                    '}';
        }

        void print() {
            System.out.println(value);
            System.out.println((x + 1) + "," + (y + 1) + "," + size);
        }
    }
}
