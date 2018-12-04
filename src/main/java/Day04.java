import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day04 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input04.txt"));

        var entries = lines.stream().map(LogEntry::new)
                .sorted()
                .collect(Collectors.toList());

        processLogs(entries);
    }

    private static void processLogs(List<LogEntry> entries) {
        var currentGuard = 0;
        var sleepStart = 0;

        var sleepEntries = new HashMap<Integer, SleepEntry>();

        for (var entry : entries) {
            if (entry.didStartSleeping()) {
                sleepStart = entry.minute;
            } else if (entry.didWakeUp()) {
                var sleepEntry = sleepEntries.getOrDefault(currentGuard, new SleepEntry());
                sleepEntry.updateSleep(sleepStart, entry.minute);
                sleepEntries.put(currentGuard, sleepEntry);
            } else {
                currentGuard = entry.getGuardId();
            }
        }

        // Strategy 1
        var max = sleepEntries.entrySet()
                .parallelStream()
                .max(Comparator.comparingInt(entry -> entry.getValue().duration));

        max.ifPresent(Day04::print);
        System.out.println();

        // Strategy 2
        max = sleepEntries.entrySet()
                .parallelStream()
                .max(Comparator.comparingInt(entry -> entry.getValue().mostSleepingCount()));

        max.ifPresent(Day04::print);
    }

    private static void print(Map.Entry<Integer, SleepEntry> result) {
        System.out.println("Laziest guard: " + result.getKey());
        System.out.println("Asleep for " + result.getValue().duration);
        System.out.println("Sleepiest minute: " + result.getValue().sleepiestMinute());
        System.out.println("Solution: " + result.getKey() * result.getValue().sleepiestMinute());
    }

    static class LogEntry implements Comparable<LogEntry> {
        int year;
        int month;
        int day;
        int hour;
        int minute;

        String event;

        LogEntry(String line) {
            var scanner = new Scanner(line);
            var date = scanner.next().replaceAll("\\[", "").split("-");

            year = Integer.parseInt(date[0]);
            month = Integer.parseInt(date[1]);
            day = Integer.parseInt(date[2]);

            var time = scanner.next().replaceAll("]", "").split(":");

            hour = Integer.parseInt(time[0]);
            minute = Integer.parseInt(time[1]);

            event = scanner.nextLine().trim();
        }

        int getGuardId() {
            return Integer.parseInt(event.replaceAll("[^\\d]+", ""));
        }

        boolean didStartSleeping() {
            return "falls asleep".equals(event);
        }

        boolean didWakeUp() {
            return "wakes up".equals(event);
        }

        @Override
        public int compareTo(LogEntry entry) {
            var yearDiff = year - entry.year;
            if (yearDiff != 0) return yearDiff;

            var monthDiff = month - entry.month;
            if (monthDiff != 0) return monthDiff;

            var dayDiff = day - entry.day;
            if (dayDiff != 0) return dayDiff;

            var hourDiff = hour - entry.hour;
            if (hourDiff != 0) return hourDiff;

            return minute - entry.minute;
        }
    }

    static class SleepEntry {
        int[] asleep = new int[60];
        int duration;

        void updateSleep(int start, int end) {
            duration += end - start;
            for (int i = start; i < end; i++) {
                asleep[i]++;
            }
        }

        int sleepiestMinute() {
            var max = 0;
            var imax = 0;
            for (int i = 0; i < asleep.length; i++) {
                if (asleep[i] > max) {
                    max = asleep[i];
                    imax = i;
                }
            }
            return imax;
        }

        int mostSleepingCount() {
            return IntStream.of(asleep).max().orElse(0);
        }
    }
}
