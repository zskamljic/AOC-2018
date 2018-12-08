import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Day08 {
    public static void main(String[] args) throws IOException {
        var numbers = Files.readAllLines(Paths.get("input08.txt")).get(0);

        System.out.println(part01(new Scanner(numbers)));
        part02(numbers);
    }

    private static int part01(Scanner scanner) {
        var sum = 0;

        var childCount = scanner.nextInt();
        var metadataCount = scanner.nextInt();

        for (var i = 0; i < childCount; i++) {
            sum += part01(scanner);
        }

        for (var i = 0; i < metadataCount; i++) {
            sum += scanner.nextInt();
        }

        return sum;
    }

    private static void part02(String numbers) {
        var scanner = new Scanner(numbers);

        var root = new Node(scanner);
        System.out.println(root.sum());
    }

    static class Node {
        Node[] nodes;
        int[] metadata;

        Node(Scanner scanner) {
            var nodeCount = scanner.nextInt();
            var metadataCount = scanner.nextInt();

            nodes = new Node[nodeCount];
            metadata = new int[metadataCount];

            for (var i = 0; i < nodeCount; i++) {
                nodes[i] = new Node(scanner);
            }

            for (var i = 0; i < metadataCount; i++) {
                metadata[i] = scanner.nextInt();
            }
        }

        int sum() {
            if (nodes.length == 0) return IntStream.of(metadata).sum();

            var sum = 0;
            for (var entry : metadata) {
                if (entry == 0 || entry > nodes.length) continue;
                sum += nodes[entry - 1].sum();
            }

            return sum;
        }
    }
}
