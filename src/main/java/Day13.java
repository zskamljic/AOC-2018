import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day13 {
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input13.txt"));

        var width = lines.stream().max(Comparator.comparing(String::length)).orElseThrow().length();
        var height = lines.size();

        var carts = new TreeSet<Cart>();

        var tracks = new char[width][height];
        var cartPosition = new Cart[width][height];

        for (var j = 0; j < lines.size(); j++) {
            var line = lines.get(j).toCharArray();
            for (var i = 0; i < line.length; i++) {
                tracks[i][j] = line[i];
                Cart cart = null;
                switch (line[i]) {
                    case '^':
                        tracks[i][j] = '|';
                        cart = new Cart(i, j, UP);
                        break;
                    case 'v':
                        tracks[i][j] = '|';
                        cart = new Cart(i, j, DOWN);
                        break;
                    case '>':
                        tracks[i][j] = '-';
                        cart = new Cart(i, j, RIGHT);
                        break;
                    case '<':
                        tracks[i][j] = '-';
                        cart = new Cart(i, j, LEFT);
                        break;
                }
                if (cart != null) {
                    carts.add(cart);
                    cartPosition[i][j] = cart;
                }
            }
        }

        var crashless = false;

        while (carts.size() > 1) {
            for (var cart : carts) {
                var collided = cart.move(tracks, cartPosition);
                if (collided != null) {
                    if (!crashless) {
                        crashless = true;
                        System.out.println(cart.x + "," + cart.y);
                    }
                }
            }
            carts.stream().filter(cart -> cart.crashed)
                    .forEach(cart -> cartPosition[cart.x][cart.y] = null);
            carts.removeIf(cart -> cart.crashed);
            var tmp = new HashSet<>(carts);
            carts.clear();
            tmp.forEach(carts::add);
        }

        if (carts.size() == 0) return;

        var cart = carts.first();
        System.out.println(cart.x + "," + cart.y);
    }

    static class Cart implements Comparable<Cart> {
        int x;
        int y;
        int direction;
        int nextTurn = LEFT;
        boolean crashed = false;

        Cart(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }

        @Override
        public int compareTo(Cart cart) {
            var yDiff = y - cart.y;
            if (yDiff != 0) return yDiff;

            return x - cart.x;
        }

        Cart move(char[][] tracks, Cart[][] cartPosition) {
            if (crashed) return this;
            var oldX = x;
            var oldY = y;

            switch (direction) {
                case UP:
                    y--;
                    break;
                case RIGHT:
                    x++;
                    break;
                case DOWN:
                    y++;
                    break;
                case LEFT:
                    x--;
                    break;
            }

            cartPosition[oldX][oldY] = null;
            if (cartPosition[x][y] != null) {
                crashed = true;
                cartPosition[x][y].crashed = true;
                return cartPosition[x][y];
            }

            cartPosition[x][y] = this;

            if (tracks[x][y] == '+') {
                turn();
            }
            turnCorner(tracks);
            return null;
        }

        private void turnCorner(char[][] tracks) {
            if (!isCorner(tracks[x][y])) return;

            var current = tracks[x][y];
            if (direction == RIGHT) {
                if (current == '\\') direction = DOWN;
                else direction = UP;
            } else if (direction == LEFT) {
                if (current == '\\') direction = UP;
                else direction = DOWN;
            } else if (direction == UP) {
                if (current == '\\') direction = LEFT;
                else direction = RIGHT;
            } else if (direction == DOWN) {
                if (current == '\\') direction = RIGHT;
                else direction = LEFT;
            }
        }

        void turn() {
            if (nextTurn == LEFT) direction--;
            if (nextTurn == RIGHT) direction++;
            direction %= 4;
            if (direction < 0) direction += 4;

            nextTurn++;
            nextTurn %= 4;
            if (nextTurn == DOWN) nextTurn = LEFT;
        }

        boolean isCorner(char rail) {
            return rail == '/' || rail == '\\';
        }
    }
}
