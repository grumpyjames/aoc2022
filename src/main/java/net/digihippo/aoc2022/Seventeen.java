package net.digihippo.aoc2022;

import java.util.Arrays;

public class Seventeen extends SolutionTemplate<Integer, Integer> {
    enum Block {
        Minus {
            @Override
            public boolean canMoveX(int newX, int[] heights, int height) {
                // |.......|
                // |####...| <- here x is 0
                if (newX < 0) {
                    return false;
                }

                // |...####| <- here x is 3
                if (newX > 3) {
                    return false;
                }

                for (int i = newX; i < newX + 4; i++) {
                    if (heights[i] >= height) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public boolean canMoveY(int newY, int[] heights, int x) {
                for (int i = x; i < x + 4; i++) {
                    if (heights[i] >= newY) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void updateHeights(int x, int height, int[] heights) {
                for (int i = x; i < x + 4; i++) {
                    heights[i] = Math.max(heights[i], height);
                }
            }
        },
        Plus {
            @Override
            public boolean canMoveX(int newX, int[] heights, int height) {
                /*
                |...@...|
                |..@@@..|
                |...@...|

                this is x = 2
                 */

                // |.#.....|
                // |###....|
                // |.#.....|
                if (newX < 0) {
                    return false;
                }

                // this is x = 4
                // |.....#.|
                // |....###|
                // |.....#.|
                if (newX > 4) {
                    return false;
                }


                boolean left = heights[newX] < height + 1;
                if (!left) {
                    return false;
                }

                boolean bottom = heights[newX + 1] < height;
                if (!bottom) {
                    return false;
                }

                boolean right = heights[newX + 2] < height + 1;
                if (!right) {
                    return false;
                }

                return true;
            }

            @Override
            public boolean canMoveY(int newY, int[] heights, int x) {
                return heights[x] < newY + 1 && heights[x + 1] < newY && heights[x + 2] < newY + 1;
            }

            @Override
            public void updateHeights(int x, int height, int[] heights) {
                heights[x] = Math.max(heights[x], height + 1);
                heights[x + 1] = Math.max(heights[x + 1], height + 2);
                heights[x + 2] = Math.max(heights[x + 2], height + 1);
            }
        },
        L {
            @Override
            public boolean canMoveX(int newX, int[] heights, int height) {
                // |..#....|
                // |..#....|
                // |###....|
                if (newX < 0) {
                    return false;
                }

                if (newX > 4) {
                    return false;
                }

                for (int i = newX; i < newX + 3; i++) {
                    int max = heights[i];
                    if (max >= height) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public boolean canMoveY(int newY, int[] heights, int x) {
                for (int i = x; i < x + 3; i++) {
                    if (heights[i] >= newY) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void updateHeights(int x, int height, int[] heights) {
                heights[x] = Math.max(heights[x], height);
                heights[x + 1] = Math.max(heights[x + 1], height);
                heights[x + 2] = Math.max(heights[x + 2], height + 2);
            }
        },
        Column {
            @Override
            public boolean canMoveX(int newX, int[] heights, int height) {
                if (newX < 0) {
                    return false;
                }

                if (newX > 6) {
                    return false;
                }

                return heights[newX] < height;
            }

            @Override
            public boolean canMoveY(int newY, int[] heights, int x) {
                return heights[x] < newY;
            }

            @Override
            public void updateHeights(int x, int height, int[] heights) {
                heights[x] = Math.max(height + 3, heights[x]);
            }
        },
        Square {
            @Override
            public boolean canMoveX(int newX, int[] heights, int height) {
                if (newX < 0) {
                    return false;
                }

                if (newX > 5) {
                    return false;
                }

                return heights[newX] < height && heights[newX + 1] < height;
            }

            @Override
            public boolean canMoveY(int newY, int[] heights, int x) {
                return heights[x] < newY && heights[x + 1] < newY;
            }

            @Override
            public void updateHeights(int x, int height, int[] heights) {
                heights[x] = Math.max(heights[x], height + 1);
                heights[x + 1] = Math.max(heights[x + 1], height + 1);
            }
        };

        public abstract boolean canMoveX(int newX, int[] heights, int height);
        public abstract boolean canMoveY(int newY, int[] heights, int x);

        public abstract void updateHeights(int x, int height, int[] heights);
    }

    enum MoveType {
        Down,
        Jet;

        public MoveType next() {
            return this == Down ? Jet : Down;
        }
    }

    @Override
    Solution<Integer> partOne() {

        return new Solution<>() {
            private final int[] heights = new int[7];
            private String jets;

            @Override
            public Integer result() {
                Block[] blockTypes = Block.values();
                int jetIndex = 0;
                for (int i = 0; i < 2022; i++) {
                    int height = max(heights) + 4;
                    int x = 2;
                    boolean stopped = false;
                    Block b = blockTypes[i % blockTypes.length];
                    MoveType m = MoveType.Jet;

                    while (!stopped) {
                        switch (m) {
                            case Down -> {
                                int newY = height - 1;
                                if (b.canMoveY(newY, heights, x)) {
                                    height = newY;
                                } else {
                                    //System.out.println("Stopping " + b.name() + " at " + x + ", " + height);
                                    b.updateHeights(x, height, heights);
                                    stopped = true;
                                }
                            }
                            case Jet -> {
                                int dx = jets.charAt(jetIndex++ % jets.length()) == '<' ? -1 : 1;
                                int newX = x + dx;
                                if (b.canMoveX(newX, heights, height)) {
                                    x = newX;
                                }
                            }
                        }

                        m = m.next();
                    }
                }

                return max(heights);
            }

            private int max(int[] distanceToBlock) {
                int max = Integer.MIN_VALUE;
                for (int i : distanceToBlock) {
                    max = Math.max(i, max);
                }
                return max;
            }

            @Override
            public void accept(String s) {
                jets = s;
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new Solution<Integer>() {
            @Override
            public Integer result() {
                return 22;
            }

            @Override
            public void accept(String s) {

            }
        };
    }
}
