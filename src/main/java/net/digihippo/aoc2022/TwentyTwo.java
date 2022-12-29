package net.digihippo.aoc2022;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwentyTwo extends SolutionTemplate<Integer, Integer> {
    record Row(int minInclusive, int maxInclusive, int[] walls) {

        public boolean isWall(int x) {
            return Arrays.stream(walls).anyMatch(w -> w == x);
        }

        public boolean inPlay(Position p) {
            return minInclusive <= p.x && p.x <= maxInclusive;
        }
    }

    record Position(int x, int y, int dx, int dy) {

        public Position next(int maxX, int maxY) {
            int newX = (x + dx) % maxX;
            if (newX < 0) {
                newX += maxX;
            }

            int newY = (y + dy) % maxY;
            if (newY < 0) {
                newY += maxY;
            }

            return new Position(newX, newY, dx, dy);
        }

        public int password() {
            int rowPart = (y + 1) * 1000;
            int columnPart = (x + 1) * 4;
            int directionPart = dx == 1 ? 0 : dx == -1 ? 2 : dy == 1 ? 1 : 3;
            return rowPart + columnPart + directionPart;
        }
    }

    sealed interface Instruction permits Move, Turn {

        Position apply(Position p, List<Row> rows);
    }

    record Move(int distance) implements Instruction {

        @Override
        public Position apply(Position p, List<Row> rows) {
            Position r = p;
            Position last = r;
            for (int i = 0; i < distance; i++) {
                Position q = r.next(rows.size(), rows.size());
                while (!rows.get(q.y).inPlay(q)) {
                    q = q.next(rows.size(), rows.size());
                }
                Row row = rows.get(q.y);
                if (row.isWall(q.x)) {
                    return last;
                }

                r = q;
                last = q;
            }

            return r;
        }
    }

    enum Turn implements Instruction {
        Left {
            @Override
            public Position apply(Position p, List<Row> rows) {
                if (p.dx == 1 && p.dy == 0) {
                    return new Position(p.x, p.y, 0, -1);
                } else if (p.dx == 0 && p.dy == -1) {
                    return new Position(p.x, p.y, -1, 0);
                } else if (p.dx == -1 && p.dy == 0) {
                    return new Position(p.x, p.y, 0, 1);
                } else if (p.dx == 0 && p.dy == 1) {
                    return new Position(p.x, p.y, 1, 0);
                }

                throw new UnsupportedOperationException();
            }
        },
        Right {
            @Override
            public Position apply(Position p, List<Row> rows) {
                if (p.dx == 1 && p.dy == 0) {
                    return new Position(p.x, p.y, 0, 1);
                } else if (p.dx == 0 && p.dy == 1) {
                    return new Position(p.x, p.y, -1, 0);
                } else if (p.dx == -1 && p.dy == 0) {
                    return new Position(p.x, p.y, 0, -1);
                } else if (p.dx == 0 && p.dy == -1) {
                    return new Position(p.x, p.y, 1, 0);
                }

                throw new UnsupportedOperationException();
            }
        };

        @Override
        public abstract Position apply(Position p, List<Row> rows);
    }

    @Override
    Solution<Integer> partOne() {
        return new Solution<Integer>() {
            private final List<Row> rows = new ArrayList<>();
            private final List<Instruction> instructions = new ArrayList<>();

            private boolean map = true;
            @Override
            public Integer result() {
                Position p = new Position(rows.get(0).minInclusive, 0, 1, 0);

                for (Instruction instruction : instructions) {
                    p = instruction.apply(p, rows);
                }
                return p.password();
            }

            @Override
            public void accept(String s) {
                if (s.isBlank()) {
                    map = false;
                    return;
                }

                if (map) {
                    int min = Math.min(indexOfOrDeath(s, '#'), indexOfOrDeath(s, '.'));
                    int max = Math.max(lastIndexOfOrDeath(s, '#'), lastIndexOfOrDeath(s, '.'));
                    final List<Integer> walls = new ArrayList<>();
                    for (int i = 0; i < s.length(); i++) {
                         char c = s.charAt(i);
                         if (c == '#') {
                             walls.add(i);
                         }
                    }

                    int[] w = new int[walls.size()];
                    for (int i = 0; i < w.length; i++) {
                        w[i] = walls.get(i);
                    }
                    rows.add(new Row(min, max, w));

                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < s.length(); i++) {
                        char c = s.charAt(i);
                        if ('0' <= c && c <= '9') {
                             sb.append(c);
                        }
                        else {
                            if (sb.length() > 0) {
                                instructions.add(new Move(Integer.parseInt(sb.toString())));
                                sb = new StringBuilder();
                            }
                            instructions.add(c == 'L' ? Turn.Left : Turn.Right);
                        }
                    }
                    if (sb.length() > 0) {
                        instructions.add(new Move(Integer.parseInt(sb.toString())));
                    }
                }
            }

            private int lastIndexOfOrDeath(String s, char ch) {
                int answer = s.lastIndexOf(ch);
                return answer == -1 ? Integer.MIN_VALUE : answer;
            }

            private int indexOfOrDeath(String s, char ch) {
                int answer = s.indexOf(ch);
                return answer == -1 ? Integer.MAX_VALUE : answer;
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
