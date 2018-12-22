import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;

public class Day19 {
    private static final int REGISTER_COUNT = 6;

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input19.txt"));
        var program = new Program(lines);
        var result = program.execute();

        // Part 1
        System.out.println(result[0]);

        // Part 2
        var sum = 0;
        // Value below is determined before loop starts
        // The value could be calculated via the program, but it's SLOOOOW.
        for (var i = 1; i <= 10551339; i++) {
            if (10551339 % i == 0) {
                sum += i;
            }
        }
        System.out.println(sum);
    }

    static class Program {
        int ipRegister = 0;
        int[] registers = new int[REGISTER_COUNT];
        List<Instruction> instructions = new ArrayList<>();
        int breakpoint;
        BooleanSupplier onBreakpoint;

        Program(List<String> lines) {
            for (var line : lines) {
                if (line.startsWith("#")) {
                    var scanner = new Scanner(line);
                    scanner.next();
                    ipRegister = scanner.nextInt();
                } else {
                    instructions.add(new Instruction(line));
                }
            }
        }

        void setBreakpoint(int breakpoint, BooleanSupplier onBreakpoint) {
            this.breakpoint = breakpoint;
            this.onBreakpoint = onBreakpoint;
        }

        int[] execute() {
            while (registers[ipRegister] >= 0 && registers[ipRegister] < instructions.size()) {
                var instruction = instructions.get(registers[ipRegister]);

                if (onBreakpoint != null && breakpoint == registers[ipRegister] && onBreakpoint.getAsBoolean()) {
                    return registers;
                }

                instruction.execute(registers);
                registers[ipRegister]++;
            }

            return registers;
        }
    }

    static class Instruction {
        String mnemonic;
        int a;
        int b;
        int c;

        Instruction(String instruction) {
            var scanner = new Scanner(instruction);
            mnemonic = scanner.next();
            a = scanner.nextInt();
            b = scanner.nextInt();
            c = scanner.nextInt();
        }

        void execute(int[] registers) {
            evaluate(registers, mnemonic, c, a, b);
        }

        static void evaluate(int[] registers, String mnemonic, int c, int a, int b) {
            switch (mnemonic) {
                case "addr":
                    registers[c] = registers[a] + registers[b];
                    break;
                case "addi":
                    registers[c] = registers[a] + b;
                    break;

                case "mulr":
                    registers[c] = registers[a] * registers[b];
                    break;
                case "muli":
                    registers[c] = registers[a] * b;
                    break;

                case "banr":
                    registers[c] = registers[a] & registers[b];
                    break;
                case "bani":
                    registers[c] = registers[a] & b;
                    break;

                case "borr":
                    registers[c] = registers[a] | registers[b];
                    break;
                case "bori":
                    registers[c] = registers[a] | b;
                    break;

                case "setr":
                    registers[c] = registers[a];
                    break;
                case "seti":
                    registers[c] = a;
                    break;

                case "gtir":
                    registers[c] = (a > registers[b]) ? 1 : 0;
                    break;
                case "gtri":
                    registers[c] = (registers[a] > b) ? 1 : 0;
                    break;
                case "gtrr":
                    registers[c] = (registers[a] > registers[b]) ? 1 : 0;
                    break;

                case "eqir":
                    registers[c] = (a == registers[b]) ? 1 : 0;
                    break;
                case "eqri":
                    registers[c] = (registers[a] == b) ? 1 : 0;
                    break;
                case "eqrr":
                    registers[c] = (registers[a] == registers[b]) ? 1 : 0;
                    break;
            }
        }
    }
}
