package net.digihippo.aoc2022;

import java.util.*;

public class TwentyThree extends SolutionTemplate<Integer, Integer> {
    @Override
    Solution<Integer> partOne() {
        return new ElfGame() {
            @Override
            boolean shouldContinue(boolean anyMoved, int round) {
                return anyMoved && round < 10;
            }

            @Override
            Integer solution(Set<Elf> elves, int round) {
                int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
                for (Elf elf : elves) {
                    minX = Math.min(minX, elf.x);
                    minY = Math.min(minY, elf.y);
                    maxX = Math.max(maxX, elf.x);
                    maxY = Math.max(maxY, elf.y);
                }

                int total = 0;
                for (int y = minY; y <= maxY; y++) {
                    for (int x = minX; x <= maxX; x++) {
                        boolean elf = elves.contains(new Elf(x, y));
                        if (!elf) {
                            total++;
                        }
                    }
                }
                return total;
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new ElfGame() {
            @Override
            boolean shouldContinue(boolean anyMoved, int round) {
                return anyMoved;
            }

            @Override
            Integer solution(Set<Elf> elves, int round) {
                return round;
            }
        };
    }

    enum Direction {
        North {
            @Override
            boolean canMove(boolean nw, boolean n, boolean ne, boolean w, boolean e, boolean sw, boolean s, boolean se) {
                return !nw && !n && !ne;
            }

            @Override
            public Elf move(Elf elf) {
                return new Elf(elf.x, elf.y - 1);
            }
        },
        South {
            @Override
            boolean canMove(boolean nw, boolean n, boolean ne, boolean w, boolean e, boolean sw, boolean s, boolean se) {
                return !s && !se && !sw;
            }

            @Override
            public Elf move(Elf elf) {
                return new Elf(elf.x, elf.y + 1);
            }
        },
        East {
            @Override
            boolean canMove(boolean nw, boolean n, boolean ne, boolean w, boolean e, boolean sw, boolean s, boolean se) {
                return !e && !ne && !se;
            }

            @Override
            public Elf move(Elf elf) {
                return new Elf(elf.x + 1, elf.y);
            }
        },
        West {
            @Override
            boolean canMove(boolean nw, boolean n, boolean ne, boolean w, boolean e, boolean sw, boolean s, boolean se) {
                return !w && !nw && !sw;
            }

            @Override
            public Elf move(Elf elf) {
                return new Elf(elf.x - 1, elf.y);
            }
        };

        abstract boolean canMove(
                boolean nw,
                boolean n,
                boolean ne,
                boolean w,
                boolean e,
                boolean sw,
                boolean s,
                boolean se);

        public abstract Elf move(Elf elf);
    }

    record Elf(int x, int y) {
        public Elf choose(Set<Elf> elves, Iterable<Direction> directions) {
            boolean nw = elves.contains(new Elf(x - 1, y - 1));
            boolean n = elves.contains(new Elf(x, y - 1));
            boolean ne = elves.contains(new Elf(x + 1, y - 1));
            boolean w = elves.contains(new Elf(x - 1, y));
            boolean e = elves.contains(new Elf(x + 1, y));
            boolean sw = elves.contains(new Elf(x - 1, y + 1));
            boolean s = elves.contains(new Elf(x, y + 1));
            boolean se = elves.contains(new Elf(x + 1, y + 1));

            if (!nw && !n && !ne && !w && !e && !sw && !s && !se) {
                return this;
            }

            for (Direction direction : directions) {
                if (direction.canMove(nw, n, ne, w, e, sw, s, se)) {
                    return direction.move(this);
                }
            }

            return this;
        }
    }

    private static abstract class ElfGame implements Solution<Integer> {
        private int row = 0;
        private final Set<Elf> startElves = new HashSet<>();

        @Override
        public Integer result() {
            boolean anyMoved = true;
            Set<Elf> elves = new HashSet<>(startElves);
            List<Direction> directions = List.of(Direction.North, Direction.South, Direction.West, Direction.East);
            int round = 0;
            while (shouldContinue(anyMoved, round)) {
                printElves(round, elves);
                final Map<Elf, List<Elf>> more = new HashMap<>();
                for (Elf elf : elves) {
                    final Elf n = elf.choose(elves, directions);
                    assert n != null;
                    more.putIfAbsent(n, new ArrayList<>());
                    more.get(n).add(elf);
                }

                final Set<Elf> next = new HashSet<>();
                for (Map.Entry<Elf, List<Elf>> entry : more.entrySet()) {
                    if (entry.getValue().size() == 1) {
                        next.add(entry.getKey());
                    } else {
                        next.addAll(entry.getValue());
                    }
                }

                anyMoved = !next.equals(elves);
                elves = next;
                directions = rotate(directions);
                round++;
            }

            printElves(round, elves);
            return solution(elves, round);
        }

        abstract boolean shouldContinue(boolean anyMoved, int round);

        private List<Direction> rotate(List<Direction> directions) {
            final List<Direction> result = new ArrayList<>();
            for (int i = 1; i < directions.size(); i++) {
                 result.add(directions.get(i));
            }
            result.add(directions.get(0));
            return result;
        }


        abstract Integer solution(Set<Elf> elves, int round);

        public void printElves(int round, Set<Elf> elves) {
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
            for (Elf elf : elves) {
                minX = Math.min(minX, elf.x);
                minY = Math.min(minY, elf.y);
                maxX = Math.max(maxX, elf.x);
                maxY = Math.max(maxY, elf.y);
            }

            System.out.println("Round " + round);
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                     System.out.print(elves.contains(new Elf(x, y)) ? '#' : '.');
                }
                System.out.println();
            }
        }

        @Override
        public void accept(String s) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '#') {
                    startElves.add(new Elf(i, row));
                }
            }
            row++;
        }
    }
}
