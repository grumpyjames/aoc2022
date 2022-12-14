package net.digihippo.aoc2022;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Fourteen extends SolutionTemplate<Integer, Integer> {
    record Block(int x, int y) {
        public static Block parse(String s) {
            String[] parts = s.split(",");
            return new Block(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }

    @Override
    Solution<Integer> partOne() {
        return new SandPit();
    }

    @Override
    Solution<Integer> partTwo() {
        return new SandPit() {
            @Override
            public boolean blocked(Block below, Set<Block> blocks, Set<Block> atRest) {
                return blocks.contains(below) || atRest.contains(below) || (below.y == maxY + 2);
            }

            @Override
            public boolean isAbyss(Block newSand) {
                return newSand.x == 500 && newSand.y == 0;
            }

            @Override
            public boolean keepGoing(Block newSand) {
                return true;
            }
        };
    }

    private static class SandPit implements Solution<Integer> {
        private final Set<Block> blocks = new HashSet<>();
        private final Set<Block> sandAtRest = new HashSet<>();
        protected int maxY;

        @Override
        public Integer result() {
            this.maxY = blocks.stream().mapToInt(b -> b.y).max().getAsInt();

            boolean abyss = false;
            while (!abyss) {
                Block newSand = new Block(500, 0);

                while (keepGoing(newSand)) {
                    Block below = new Block(newSand.x, newSand.y + 1);
                    if (blocked(below, blocks, sandAtRest)) {
                        Block left = new Block(below.x - 1, below.y);
                        if (blocked(left, blocks, sandAtRest)) {
                            Block right = new Block(below.x + 1, below.y);
                            if (blocked(right, blocks, sandAtRest)) {
                                sandAtRest.add(newSand);
                                break;
                            } else {
                                newSand = right;
                            }
                        } else {
                            newSand = left;
                        }
                    } else {
                        newSand = below;
                    }
                }
                abyss = isAbyss(newSand);
            }

            return sandAtRest.size();
        }

        public boolean keepGoing(Block newSand) {
            return newSand.y < maxY;
        }

        public boolean isAbyss(Block newSand) {
            return newSand.y >= maxY;
        }

        public boolean blocked(Block below, Set<Block> blocks, Set<Block> atRest) {
            return blocks.contains(below) || atRest.contains(below);
        }

        @Override
        public void accept(String s) {
            String[] split = s.split(" -> ");
            for (int i = 0; i < split.length - 1; i++) {
                Block start = Block.parse(split[i]);
                Block end = Block.parse(split[i + 1]);

                blockRange(start, end).forEach(blocks::add);
            }
        }

        private Iterable<Block> blockRange(Block start, Block end) {
            if (start.x == end.x) {
                final List<Block> column = new ArrayList<>();
                for (int i = Math.min(start.y, end.y); i <= Math.max(start.y, end.y); i++) {
                    column.add(new Block(start.x, i));
                }
                return column;
            } else if (start.y == end.y) {
                final List<Block> column = new ArrayList<>();
                for (int i = Math.min(start.x, end.x); i <= Math.max(start.x, end.x); i++) {
                    column.add(new Block(i, start.y));
                }
                return column;
            }

            throw new UnsupportedOperationException();
        }
    }
}
