import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    private static List<String> mockLines() {
        return List.of("#ip 0",
                "seti 5 0 1",
                "seti 6 0 2",
                "addi 0 1 0",
                "addr 1 2 3",
                "setr 1 0 0",
                "seti 8 0 4",
                "seti 9 0 5");
    }

    static class Program {
        int ipRegister = 0;
        int[] registers = new int[REGISTER_COUNT];
        List<Instruction> instructions = new ArrayList<>();

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

        int[] execute() {
            while (registers[ipRegister] >= 0 && registers[ipRegister] < instructions.size()) {
                //System.out.printf("ip=%d %s ", registers[ipRegister], Arrays.toString(registers));

                var instruction = instructions.get(registers[ipRegister]);
                //System.out.printf("%s %d %d %d ", instruction.mnemonic, instruction.a, instruction.b, instruction.c);

                instruction.execute(registers);
                //System.out.println(Arrays.toString(registers));
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
