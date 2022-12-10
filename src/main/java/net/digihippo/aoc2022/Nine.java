package net.digihippo.aoc2022;

import java.util.HashSet;
import java.util.Set;

public class Nine extends SolutionTemplate<Integer, Integer> {
    record Point(int x, int y) {
        public Point plus(Point direction) {
            return new Point(x + direction.x, y + direction.y);
        }

        public Point chase(Point head) {
            Point diff = head.minus(this);
            // one-over-other, do nothing
            if (diff.size() < 2.0D) {
                return this;
            }

            if (diff.size() == 2.0D)
            {
                return new Point(this.x + (diff.x/2), this.y + (diff.y/2));
            }

            return new Point(this.x + Integer.signum(diff.x), this.y + Integer.signum(diff.y));
        }

        private double size() {
            return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        }

        private Point minus(Point point) {
            return new Point(this.x - point.x, this.y - point.y);
        }
    }

    private static final Point UP = new Point(0, 1);
    private static final Point DOWN = new Point(0, -1);
    private static final Point LEFT = new Point(-1, 0);
    private static final Point RIGHT = new Point(1, 0);
    @Override
    Solution<Integer> partOne() {
        return new RopeSolution() {
            Point head = new Point(0, 0);
            Point tail = new Point(0, 0);

            @Override
            void moveRopeHead(int count, Point direction) {
                for (int i = 0; i < count; i++) {
                    head = head.plus(direction);
                    tail = tail.chase(head);
                    visited.add(tail);
                }
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new RopeSolution() {
            Point[] rope = new Point[] {
                    new Point(0, 0),
                    new Point(0, 0),
                    new Point(0, 0),
                    new Point(0, 0),
                    new Point(0, 0),
                    new Point(0, 0),
                    new Point(0, 0),
                    new Point(0, 0),
                    new Point(0, 0),
                    new Point(0, 0),
            };

            @Override
            void moveRopeHead(int count, Point direction) {
                for (int i = 0; i < count; i++) {
                    rope[0] = rope[0].plus(direction);
                    for (int j = 1; j < rope.length; j++) {
                        Point moving = rope[j];
                        rope[j] = moving.chase(rope[j - 1]);
                    }
                    visited.add(rope[rope.length - 1]);
                }
            }
        };
    }

    private static abstract class RopeSolution implements Solution<Integer> {
        final Set<Point> visited = new HashSet<>();

        @Override
        public Integer result() {
            return visited.size();
        }

        @Override
        public void accept(String s) {
            String[] parts = s.split(" ");
            int count = Integer.parseInt(parts[1]);
            final Point direction = switch (parts[0]) {
                case "U" -> UP;
                case "D" -> DOWN;
                case "L" -> LEFT;
                case "R" -> RIGHT;
                default -> throw new IllegalStateException("Cannot parse direction: " + parts[0]);
            };

            moveRopeHead(count, direction);
        }

        abstract void moveRopeHead(int count, Point direction);
    }
}
