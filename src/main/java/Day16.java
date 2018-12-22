import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day16 {
    private static final List<String> OP_CODES = List.of(
            "addr", "addi",
            "mulr", "muli",
            "banr", "bani",
            "borr", "bori",
            "setr", "seti",
            "gtir", "gtri", "gtrr",
            "eqir", "eqri", "eqrr"
    );
    private static final int REGISTER_COUNT = 4;

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input16.txt")).split("\n\n\n");
        var observationStates = parseObservations(Arrays.asList(input[0].split("\n")));
        var multiCodeOps = observationStates.stream()
                .filter(state -> state.validOpCodes.size() > 3)
                .count();

        // Part 1
        System.out.println(multiCodeOps);

        // Part 2
        var opCodes = new HashMap<Integer, List<String>>();
        for (var i = 0; i < OP_CODES.size(); i++) opCodes.put(i, new ArrayList<>(OP_CODES));

        // Find single codes
        for (var operation : observationStates) {
            var code = operation.call[0];
            var candidates = opCodes.get(code);
            candidates.removeIf(c -> !operation.validOpCodes.contains(c));
        }
        System.out.println(opCodes);

        // Remove determined codes from lists
        while (opCodes.values().stream().anyMatch(item -> item.size() > 1)) {
            var determined = opCodes.values().stream()
                    .filter(item -> item.size() == 1)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            for (var entry : opCodes.entrySet()) {
                if (entry.getValue().size() == 1) continue;
                entry.getValue().removeAll(determined);
            }
        }

        // Map instructions to words
        var instructions = new String[OP_CODES.size()];
        for (var i = 0; i < opCodes.size(); i++) {
            instructions[i] = opCodes.get(i).get(0);
        }

        // Parse program and split to array of ints
        var program = Stream.of(input[1].trim().split("\n"))
                .map(line -> Stream.of(line.split(" ")).mapToInt(Integer::parseInt).toArray())
                .collect(Collectors.toList());

        var result = execute(program, instructions);
        System.out.println(result[0]);
    }

    private static int[] execute(List<int[]> program, String[] instructions) {
        var registers = new int[4];

        for (var line : program) {
            // Get instruction and parameters
            var instruction = instructions[line[0]];
            var a = line[1];
            var b = line[2];
            var c = line[3];

            Day19.Instruction.evaluate(registers, instruction, c, a, b);
        }
        return registers;
    }

    private static List<ObservationState> parseObservations(List<String> observations) {
        int[] before = null;
        int[] call = null;
        int[] after;

        var list = new ArrayList<ObservationState>();
        for (var line : observations) {
            if (line.isBlank()) continue;

            var numbers = Stream.of(line.replaceAll("[^\\d ]", "").trim().split(" "))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            if (line.startsWith("Before:")) {
                before = numbers;
            } else if (line.startsWith("After:")) {
                after = numbers;
                list.add(new ObservationState(before, call, after));
            } else {
                call = numbers;
            }
        }
        return list;
    }

    static class ObservationState {
        List<String> validOpCodes = new ArrayList<>();
        int[] before;
        int[] call;
        int[] after;

        ObservationState(int[] before, int[] call, int[] after) {
            this.before = before;
            this.call = call;
            this.after = after;
            findValidOpCodes();
        }

        void findValidOpCodes() {
            var a = call[1];
            var b = call[2];
            var c = call[3];

            // If both are valid registers
            if (call[1] < REGISTER_COUNT && call[2] < REGISTER_COUNT) {
                if (after[c] == before[a] + before[b]) validOpCodes.add("addr");
                if (after[c] == before[a] * before[b]) validOpCodes.add("mulr");
                if (after[c] == (before[a] & before[b])) validOpCodes.add("banr");
                if (after[c] == (before[a] | before[b])) validOpCodes.add("borr");
                if (after[c] == (before[a] > before[b] ? 1 : 0)) validOpCodes.add("gtrr");
                if (after[c] == (before[a] == before[b] ? 1 : 0)) validOpCodes.add("eqrr");
            }
            // If A is valid register
            if (call[1] < REGISTER_COUNT) {
                if (after[c] == before[a] + b) validOpCodes.add("addi");
                if (after[c] == before[a] * b) validOpCodes.add("muli");
                if (after[c] == (before[a] & b)) validOpCodes.add("bani");
                if (after[c] == (before[a] | b)) validOpCodes.add("bori");
                if (after[c] == before[a]) validOpCodes.add("setr");
                if (after[c] == (before[a] > b ? 1 : 0)) validOpCodes.add("gtri");
                if (after[c] == (before[a] == b ? 1 : 0)) validOpCodes.add("eqri");
            }
            if (after[c] == a) validOpCodes.add("seti");
            if (after[c] == (a > before[b] ? 1 : 0)) validOpCodes.add("gtir");
            if (after[c] == (a == before[b] ? 1 : 0)) validOpCodes.add("eqir");
        }
    }
}
