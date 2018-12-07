import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day07 {
    private static final int WORKER_COUNT = 5;
    private static final int MIN_TIME = 60;

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input07.txt"));

        part01(createNodes(lines));
        System.out.println();
        part02(createNodes(lines));
    }

    private static List<Node> createNodes(List<String> lines) {
        var nodes = new ArrayList<Node>();
        for (var line : lines) {
            var scanner = new Scanner(line);
            scanner.next(); // Step
            var nodeName = scanner.next();
            for (int i = 0; i < 5; i++) scanner.next(); // must be finished before step
            var blocked = scanner.next();

            var blockedNode = getOrDefault(nodes, blocked);

            blockedNode.blockedBy++;
            if (!nodes.contains(blockedNode)) {
                nodes.add(blockedNode);
            }

            var blockingNode = getOrDefault(nodes, nodeName);
            blockingNode.blocks.add(blockedNode);
        }
        return nodes;
    }

    private static Node getOrDefault(List<Node> nodes, String id) {
        var result = nodes.stream()
                .filter(node -> id.equals(node.id))
                .findFirst()
                .orElse(new Node(id));

        if (!nodes.contains(result)) {
            nodes.add(result);
        }
        return result;
    }

    private static void part01(List<Node> nodes) {
        while (nodes.size() > 0) {
            var nextNode = nodes.stream()
                    .filter(node -> node.blockedBy == 0)
                    .min(Node::compareTo)
                    .orElseThrow();

            System.out.print(nextNode.id);
            nextNode.unblockChildren();
            nodes.remove(nextNode);
        }
    }

    private static void part02(List<Node> nodes) {
        var timer = 0;
        var workers = new TreeSet<Node>();
        while (nodes.size() > 0 || workers.size() > 0) {
            // Find all unblocked parts and add them to free workers
            nodes.stream()
                    .filter(node -> node.blockedBy == 0)
                    .sorted(Node::compareTo)
                    .limit(WORKER_COUNT - workers.size())
                    .forEach(workers::add);
            nodes.removeAll(workers); // Remove them from pending queue

            // Work on items
            var iterator = workers.iterator();
            while (iterator.hasNext()) {
                var node = iterator.next();
                node.timeRemaining--;
                if (node.timeRemaining == 0) {
                    node.unblockChildren();
                    iterator.remove();
                    nodes.remove(node);
                    System.out.print(node.id); // Print finished part
                }
            }

            // Second passed
            timer++;
        }
        System.out.println();
        System.out.println(timer);
    }

    static class Node implements Comparable<Node> {
        String id;
        int blockedBy = 0;
        int timeRemaining;
        Set<Node> blocks = new HashSet<>();

        Node(String id) {
            this.id = id;
            timeRemaining = MIN_TIME + 1 + (id.charAt(0) - 'A'); // 61 seconds for A, 62 for B etc.
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Node) {
                return id.equals(((Node) obj).id);
            } else if (obj instanceof String) {
                return id.equals(obj);
            }
            return super.equals(obj);
        }

        void unblockChildren() {
            blocks.forEach(node -> node.blockedBy--);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "id='" + id + '\'' +
                    ", blockedBy=" + blockedBy +
                    ", timeRemaining=" + timeRemaining +
                    '}';
        }

        @Override
        public int compareTo(Node other) {
            return id.compareTo(other.id);
        }
    }
}
