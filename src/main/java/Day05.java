import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day05 {
    public static void main(String[] args) throws IOException {
        var data = Files.readAllLines(Paths.get("input05.txt"));
        var string = String.join("", data);

        // Part 1
        var output = collapsePolymer(string);
        System.out.println(output.length());

        System.out.println();

        // Part 2
        int minLength = Integer.MAX_VALUE;
        for (int i = 'a'; i <= 'z'; i++) {
            var regex = "[" + Character.toString(i) + Character.toString(i - 32) + "]+";
            var cleaned = string.replaceAll(regex, "");

            var length = collapsePolymer(cleaned).length();
            if (length < minLength) {
                minLength = length;
            }
        }

        System.out.println(minLength);
    }

    private static String collapsePolymer(String string) {
        var input = new StringBuilder(string);
        var output = new StringBuilder();

        while (input.length() > 0) {
            if (output.length() == 0) {
                output.append(input.charAt(0));
                input.deleteCharAt(0);
                continue;
            }

            var lastChar = output.charAt(output.length() - 1);
            var nextChar = input.charAt(0);

            if (Math.abs(lastChar - nextChar) == 32) {
                output.deleteCharAt(output.length() - 1);
            } else {
                output.append(input.charAt(0));
            }
            input.deleteCharAt(0);
        }
        return output.toString();
    }
}
