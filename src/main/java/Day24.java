import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day24 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input24.txt"));

        var state = new State(lines);

        // Part 1
        System.out.println(state.fight());

        // Part 2
        var boost = 0;
        int result;
        do {
            boost++;
            state = new State(lines);
            result = state.fight(boost);
        } while (state.immune.size() == 0 || result == -1);

        System.out.println(boost);
        System.out.println(result);
    }

    static class State {
        List<Unit> immune = new ArrayList<>();
        List<Unit> infection = new ArrayList<>();

        State(List<String> lines) {
            var side = false;
            for (var line : lines) {
                if (line.isEmpty()) {
                    side = true;
                    continue;
                }

                if (line.contains(":")) continue;

                var unit = new Unit(line);
                unit.side = side;
                if (side) {
                    infection.add(unit);
                } else {
                    immune.add(unit);
                }
            }
        }

        int fight() {
            return fight(0);
        }

        int fight(int boost) {
            immune.forEach(unit -> unit.damage += boost);

            while (immune.size() > 0 && infection.size() > 0) {
                // Target selection
                var all = new ArrayList<>(infection);
                all.addAll(immune);
                all.sort(Unit::compareTo);
                all.forEach(unit -> {
                    unit.isSelected = false;
                    unit.target = null;
                });

                for (var unit : all) {
                    unit.selectOpponent(unit.side ? immune : infection);
                }

                // Attacking
                all.sort(Comparator.comparing(unit -> -unit.initiative));
                var killed = all.stream()
                        .sorted(Comparator.comparing(unit -> -unit.initiative))
                        .mapToInt(Unit::attack)
                        .sum();
                if (killed == 0) return -1;

                immune.removeIf(unit -> unit.count <= 0);
                infection.removeIf(unit -> unit.count <= 0);
            }

            return Stream.concat(immune.stream(), infection.stream())
                    .mapToInt(unit -> unit.count)
                    .sum();
        }

        @Override
        public String toString() {
            return "State{" +
                    "immune=" + immune +
                    ",\n infection=" + infection +
                    '}';
        }
    }

    static class Unit implements Comparable<Unit> {
        boolean side;
        int initiative;
        int damage;
        int count;
        int hpPerUnit;
        String damageType;
        List<String> immune = new ArrayList<>();
        List<String> weak = new ArrayList<>();
        boolean isSelected;
        Unit target;

        Unit(String line) {
            var scanner = new Scanner(line);

            count = scanner.nextInt();
            for (var i = 0; i < 3; i++) scanner.next(); // units each with
            hpPerUnit = scanner.nextInt();
            for (var i = 0; i < 2; i++) scanner.next(); // hit points

            if (line.contains("(")) parseWeakAndImmune(scanner);

            for (var i = 0; i < 5; i++) scanner.next(); // with an attack that does
            damage = scanner.nextInt();
            damageType = scanner.next();

            for (var i = 0; i < 3; i++) scanner.next(); // damage at initiative
            initiative = scanner.nextInt();
        }

        private void parseWeakAndImmune(Scanner scanner) {
            var type = scanner.next().replace("(", "");
            scanner.next(); // to
            var done = false;

            while (!done) {
                var word = scanner.next();
                var element = word.replaceAll("[^\\w]+", "");

                switch (type) {
                    case "weak":
                        weak.add(element);
                        break;
                    case "immune":
                        immune.add(element);
                        break;
                }

                switch (word.charAt(word.length() - 1)) {
                    case ';':
                        type = scanner.next();
                        scanner.next(); // to
                        break;
                    case ')':
                        done = true;
                        break;
                }
            }
        }

        int getEffectivePower() {
            return count * damage;
        }

        int getDamageDoneBy(Unit unit) {
            if (immune.contains(unit.damageType)) return 0;
            if (weak.contains(unit.damageType)) return unit.getEffectivePower() * 2;
            return unit.getEffectivePower();
        }

        @Override
        public int compareTo(Unit unit) {
            var apDiff = unit.getEffectivePower() - getEffectivePower();
            if (apDiff != 0) return apDiff;
            return unit.initiative - initiative;
        }

        @Override
        public String toString() {
            return "Unit{" +
                    "initiative=" + initiative +
                    ",\n damage=" + damage +
                    ",\n count=" + count +
                    ",\n hpPerUnit=" + hpPerUnit +
                    ",\n damageType='" + damageType + '\'' +
                    ",\n immune=" + immune +
                    ",\n weak=" + weak +
                    ",\n effectiveAP=" + getEffectivePower() +
                    '}';
        }

        void selectOpponent(List<Unit> units) {
            var targets = new ArrayList<>(units)
                    .stream()
                    .filter(unit -> unit.getDamageDoneBy(this) > 0) // only if it can deal damage
                    .filter(unit -> !unit.isSelected) // can only be selected once
                    .min(this::compareTargets);
            targets.ifPresent(unit -> {
                unit.isSelected = true; // select
                target = unit; // remember who to attack
            });
        }

        int compareTargets(Unit a, Unit b) {
            var damage = b.getDamageDoneBy(this) - a.getDamageDoneBy(this);
            if (damage != 0) return damage;

            var power = b.getEffectivePower() - a.getEffectivePower();
            if (power != 0) return power;

            return b.initiative - a.initiative;
        }

        int attack() {
            if (target == null || target.count <= 0 || count <= 0) return 0;

            var damage = target.getDamageDoneBy(this);
            var kills = damage / target.hpPerUnit;
            target.count -= kills;
            return kills;
        }
    }
}
