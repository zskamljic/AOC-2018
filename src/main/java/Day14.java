public class Day14 {
    private static final int STEPS = 306281;
    private static final int VALIDATION_SIZE = 10;

    public static void main(String[] args) {
        var state = new State();

        while (state.recipes.length() < STEPS + VALIDATION_SIZE) {
            state.doStep();
        }

        // Part 01:
        System.out.println(state.getPart1Result());

        // Part 02:
        System.out.println(state.getPart2Result(STEPS));
    }

    static class State {
        StringBuilder recipes = new StringBuilder("37");
        int elf1 = 0;
        int elf2 = 1;
        boolean didAddTwo = false;

        void doStep() {
            var elf1Num = recipes.charAt(elf1) - '0';
            var elf2Num = recipes.charAt(elf2) - '0';

            var newSum = elf1Num + elf2Num;
            if (newSum >= 10) {
                var tens = newSum / 10 % 10;
                recipes.append(tens);
                didAddTwo = true;
            } else {
                didAddTwo = false;
            }
            recipes.append(newSum % 10);

            elf1 = (elf1 + elf1Num + 1) % recipes.length();
            elf2 = (elf2 + elf2Num + 1) % recipes.length();
        }

        String getPart1Result() {
            var output = recipes.toString();
            if (didAddTwo) output = output.substring(0, recipes.length() - 1);
            return output.substring(output.length() - VALIDATION_SIZE);
        }

        int getPart2Result(int steps) {
            var toFind = Integer.toString(STEPS);
            for (var i = 0; i < 50_000_000; i++) {
                doStep();
            }
            return recipes.indexOf(toFind);
        }
    }
}
