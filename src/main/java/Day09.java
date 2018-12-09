import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.LongStream;

public class Day09 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input09.txt")).get(0);
        var scanner = new Scanner(input);
        var players = scanner.nextInt();
        for (var i = 0; i < 5; i++) scanner.next();
        var lastMarble = scanner.nextInt();

        // Part 01
        var state = new State(players);
        state.play(lastMarble);
        System.out.println(state.getMaxScore());

        // Part 02
        state = new State(players);
        state.play(lastMarble * 100);
        System.out.println(state.getMaxScore());
    }

    static class State {
        int playerCount;
        long[] scores;
        int marbleNumber = 0;
        int currentPlayer = -1;
        Entry first;
        Entry current;

        State(int playerCount) {
            this.playerCount = playerCount;
            scores = new long[playerCount];
            current = new Entry(0);
            current.next = current;
            current.previous = current;
            first = current;
        }

        void play(int lastMarble) {
            for (var i = 0; i < lastMarble; i++) {
                insertNext();
            }
        }

        void insertNext() {
            currentPlayer++;
            currentPlayer %= playerCount;

            marbleNumber++;

            if (marbleNumber % 23 == 0) {
                scores[currentPlayer] += marbleNumber;
                for (int i = 0; i < 7; i++) {
                    current = current.previous;
                }
                scores[currentPlayer] += current.value;

                var previous = current.previous;
                var next = current.next;
                previous.next = next;
                next.previous = previous;
                current = next;
                return;
            }

            var marble = new Entry(marbleNumber);
            var next = current.next;
            marble.previous = next;
            marble.next = next.next;
            next.next.previous = marble;
            next.next = marble;
            current = marble;
        }

        long getMaxScore() {
            return LongStream.of(scores).max().orElse(0);
        }

        void print() {
            System.out.print("[" + (currentPlayer == -1 ? "-" : currentPlayer + 1) + "] ");

            var current = first;
            do {
                if (this.current == current) {
                    System.out.print("(" + current.value + ") ");
                } else {
                    System.out.print(current.value + " ");
                }
                current = current.next;
            } while (current != first);
            System.out.println();
        }
    }

    static class Entry {
        Entry previous;
        Entry next;
        int value;

        Entry(int value) {
            this.value = value;
        }
    }
}
