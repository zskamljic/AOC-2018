import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Day03 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input03.txt"));

        var infos = new ArrayList<LineInfo>();

        var field = new byte[1000][1000];
        for (var line : lines) {
            var info = new LineInfo(line);
            infos.add(info);
            apply(field, info);
        }

        part01(field);
        part02(infos, field);
    }

    private static void part01(byte[][] field) {
        var overlap = 0;
        for (byte[] bytes : field) {
            for (byte aByte : bytes) {
                if (aByte > 1) overlap++;
            }
        }
        System.out.println(overlap);
    }

    private static void part02(ArrayList<LineInfo> infos, byte[][] field) {
        for (var info : infos) {
            if (intact(field, info)) {
                System.out.println(info.id.replace("#", ""));
                return;
            }
        }
    }

    private static void apply(byte[][] field, LineInfo info) {
        for (int i = 0; i < info.width; i++) {
            for (int j = 0; j < info.height; j++) {
                field[info.x + i][info.y + j]++;
            }
        }
    }

    private static boolean intact(byte[][] field, LineInfo info) {
        for (int i = 0; i < info.width; i++) {
            for (int j = 0; j < info.height; j++) {
                if (field[info.x + i][info.y + j] != 1) return false;
            }
        }
        return true;
    }

    static class LineInfo {
        String id;
        int x;
        int y;
        int width;
        int height;

        // #id @ x,y: _width_x_height
        LineInfo(String line) {
            var scanner = new Scanner(line);
            id = scanner.next();
            scanner.next(); // @ character
            var origin = scanner.next()
                    .replaceAll("[,:]", " ")
                    .trim()
                    .split(" ");
            x = Integer.parseInt(origin[0]);
            y = Integer.parseInt(origin[1]);

            var size = scanner.next()
                    .split("x");
            width = Integer.parseInt(size[0]);
            height = Integer.parseInt(size[1]);
        }
    }
}
