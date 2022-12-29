package net.digihippo.aoc2022;

import java.util.*;

public class TwentyFour extends SolutionTemplate<Integer, Integer> {
    enum Blizzard {
        Up,
        Down,
        Left,
        Right;

        public static Blizzard find(char charAt) {
            switch (charAt) {
                case '^' -> {
                    return Up;
                }
                case '<' -> {
                    return Left;
                }
                case '>' -> {
                    return Right;
                }
                case 'v' -> {
                    return Down;
                }
            }
            throw new IllegalStateException("Unrecognised: " + charAt);
        }

        public Coordinate applyTo(Coordinate c, int maxX, int maxY) {
            int yMod = maxY + 1;
            int xMod = maxX + 1;
            switch (this) {
                case Up -> {
                    int newY = c.y - 1;
                    if (newY < 0) {
                        newY += yMod;
                    }

                    return new Coordinate(c.x, newY);
                }
                case Down -> {
                    int newY = (c.y + 1) % yMod;
                    return new Coordinate(c.x, newY);
                }
                case Left -> {
                    int newX = c.x - 1;
                    if (newX < 0) {
                        newX += xMod;
                    }
                    return new Coordinate(newX, c.y);
                }
                case Right -> {
                    int newX = (c.x + 1) % xMod;
                    return new Coordinate(newX, c.y);
                }
            }
            throw new IllegalStateException(this.toString());
        }

        public char charValue() {
            switch (this) {
                case Up -> {
                    return '^';
                }
                case Down -> {
                    return 'v';
                }
                case Left -> {
                    return '<';
                }
                case Right -> {
                    return '>';
                }
            }
            throw new IllegalStateException(this.toString());
        }
    }

    record Coordinate(int x, int y) {
        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    sealed interface Path permits Head, Empty {
        boolean startsWith(Coordinate c);
    }

    record Head(Coordinate h, Path rest) implements Path {
        @Override
        public boolean startsWith(Coordinate c) {
            return c.equals(h);
        }

        @Override
        public String toString() {
            return h.toString() + " -> " + rest.toString();
        }
    }
    record Empty() implements Path {
        @Override
        public boolean startsWith(Coordinate c) {
            return false;
        }

        @Override
        public String toString() {
            return "{}";
        }
    }

    @Override
    Solution<Integer> partOne() {
        return new Solution<Integer>() {
            private Coordinate start = new Coordinate(0, -1);
            private Coordinate end;
            private int row = 0;
            private Map<Coordinate, List<Blizzard>> blizzards = new HashMap<>();

            @Override
            public Integer result() {
                int maxY = end.y - 1;
                int maxX = end.x;


                Map<Coordinate, Path> toTry = new HashMap<>();
                toTry.put(start, new Head(start, new Empty()));

                int length = 0;
                while (toTry.keySet().stream().noneMatch(p -> p.equals(end))) {
//                    System.out.println("After " + length);
//                    printBlizzards(blizzards, maxX, maxY);
                    final Map<Coordinate, List<Blizzard>> next = move(blizzards, maxX, maxY);

                    final Map<Coordinate, Path> nextPaths = new HashMap<>();
                    for (Map.Entry<Coordinate, Path> c: toTry.entrySet()) {
                        assert !blizzards.containsKey(c.getKey());

                        final List<Coordinate> cs = options(c.getKey(), next, maxX, maxY);
                        for (Coordinate c1 : cs) {
                            nextPaths.put(c1, new Head(c1, c.getValue()));
                        }
                    }
                    toTry = nextPaths;
                    blizzards = next;
                    length++;
                }

                return length;
            }

            private void printBlizzards(Map<Coordinate, List<Blizzard>> blizzards, int maxX, int maxY) {
                for (int y = 0; y <= maxY; y++) {
                    for (int x = 0; x <= maxX; x++) {
                        List<Blizzard> bs = blizzards.getOrDefault(new Coordinate(x, y), Collections.emptyList());
                        if (bs.isEmpty()) {
                            System.out.print('.');
                        } else if (bs.size() == 1) {
                            System.out.print(bs.get(0).charValue());
                        } else {
                            System.out.print(bs.size());
                        }
                    }
                    System.out.println();
                }
            }


            private static void consider(
                    Map<Coordinate, List<Blizzard>> next,
                    int maxX,
                    int maxY,
                    Coordinate coordinate,
                    Coordinate end,
                    List<Coordinate> result) {
                if (coordinate.equals(end)) {
                    result.add(coordinate);
                }

                if (
                        0 <= coordinate.y && coordinate.y <= maxY &&
                                0 <= coordinate.x && coordinate.x <= maxX &&
                                !next.containsKey(coordinate)) {
                    result.add(coordinate);
                }
            }


            private List<Coordinate> options(
                    Coordinate h,
                    Map<Coordinate, List<Blizzard>> next,
                    int maxX,
                    int maxY) {
                final List<Coordinate> result = new ArrayList<>();

                consider(next, maxX, maxY, new Coordinate(h.x, h.y + 1), end, result);
                consider(next, maxX, maxY, new Coordinate(h.x + 1, h.y), end, result);
                if (!next.containsKey(h)) {
                    result.add(h);
                }
                consider(next, maxX, maxY, new Coordinate(h.x, h.y - 1), end, result);
                consider(next, maxX, maxY, new Coordinate(h.x - 1, h.y), end, result);

                return result;
            }

            private Map<Coordinate, List<Blizzard>> move(Map<Coordinate, List<Blizzard>> blizzards, int maxX, int maxY) {
                final Map<Coordinate, List<Blizzard>> result = new HashMap<>();

                for (Map.Entry<Coordinate, List<Blizzard>> entry : blizzards.entrySet()) {
                    for (Blizzard blizzard : entry.getValue()) {
                        Coordinate c = blizzard.applyTo(entry.getKey(), maxX, maxY);
                        result.putIfAbsent(c, new ArrayList<>());
                        result.get(c).add(blizzard);
                    }
                }
                return result;
            }

            @Override
            public void accept(String s) {
                if (s.contains("##")) {
                    end = new Coordinate(s.length() - 3, row);
                    return;
                }

                for (int i = 1; i < s.length() - 1; i++) {
                     if (s.charAt(i) != '.') {
                         blizzards.put(new Coordinate(i - 1, row), List.of(Blizzard.find(s.charAt(i))));
                     }
                }
                ++row;
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new Solution<Integer>() {
            @Override
            public Integer result() {
                return null;
            }

            @Override
            public void accept(String s) {

            }
        };
    }
}
