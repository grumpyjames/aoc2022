package net.digihippo.aoc2022;

import java.util.*;

public class Seventeen extends SolutionTemplate<Integer, Long> {
    record FullBlock(int rockNo, int height) {}

    private static final class BlockTracker {
        private final char[][] blocks;
        private final String hmm;
        private int maxY = 0;
        private final Map<FullBlock, Integer> fullBlocks = new HashMap<>();
        private final Map<Integer, Integer> maxHeights = new HashMap<>();
        private int rockIndex;
        private boolean hasSolution;
        private long height;

        BlockTracker(char[][] blocks, String hmm) {
            this.blocks = blocks;
            this.hmm = hmm;
        }

        void rockSettling(int rockIndex) {
            this.rockIndex = rockIndex;
        }

        boolean isOccupied(int x, int y) {
            return blocks[y][x] == '#';
        }

        void occupy(int x, int y) {
            this.maxY = Math.max(y, this.maxY);
            if (isOccupied(x, y)) {
                throw new IllegalStateException("hmm");
            }
            blocks[y][x] = '#';
            if (ofInterest(y)) {
                final Map<List<String>, List<FullBlock>> hmm = new HashMap<>();

                fullBlocks.forEach((fb, mh) -> {
                    final List<String> al = new ArrayList<>();
                    if (fb.height < maxY - 10) {
                        for (int y2 = fb.height; y2 < fb.height + 5; y2++) {
                            al.add(new String(blocks[y2]));
                        }
                        hmm.putIfAbsent(al, new ArrayList<>());
                        hmm.get(al).add(fb);
                    }
                });
                int max = Integer.MIN_VALUE;
                List<FullBlock> bestChance = new ArrayList<>();
                for (Map.Entry<List<String>, List<FullBlock>> listListEntry : hmm.entrySet()) {
                    List<FullBlock> blocks = listListEntry.getValue();
                    if (blocks.size() > max) {
                        max = blocks.size();
                        bestChance = blocks;
                    }
                }

                bestChance.sort(Comparator.comparing(b -> b.rockNo));

                if (bestChance.size() > 3) {
                    hasSolution = true;
                    for (FullBlock fullBlock : bestChance) {
                        System.out.println("After: " + fullBlock + ", height was " + fullBlocks.get(fullBlock));
                    }
                    FullBlock first = bestChance.get(0);
                    FullBlock second = bestChance.get(1);
                    int periodSize = second.rockNo - first.rockNo;
                    int growthOverPeriod = fullBlocks.get(second) - fullBlocks.get(first);
                    long large = 1_000_000_000_000L;
                    long distanceFromOrigin = large - first.rockNo;
                    long multiplicand = distanceFromOrigin / periodSize;
                    int offset = (int) (distanceFromOrigin % periodSize);
                    int offsetHeight = maxHeights.get(first.rockNo + offset);
                    this.height = (multiplicand * growthOverPeriod) + offsetHeight;
                }

                fullBlocks.put(new FullBlock(rockIndex, y), maxY);
            }
        }

        private boolean ofInterest(int y) {
            return hmm.equals(new String(blocks[y]));
        }

        void print(int yLow, int yHigh) {
            for (int i = yHigh; i >= yLow; i--) {
                System.out.print('|');
                System.out.print(blocks[i]);
                System.out.println('|');
            }
            System.out.println("_________");
        }

        public boolean hasSolution() {
            return hasSolution;
        }

        public Long result() {
            return height;
        }

        public void rockSettled(int rockIndex) {
            maxHeights.put(rockIndex, maxY);
        }
    }

    enum Block {
        Minus {
            @Override
            public boolean canMoveX(int newX, int height, BlockTracker blocks) {
                // |.......|
                // |####...| <- here x is 0
                if (newX < 0) {
                    return false;
                }

                // |...####| <- here x is 3
                if (newX > 3) {
                    return false;
                }

                for (int x = newX; x < newX + 4; x++) {
                    if (blocks.isOccupied(x, height)) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public boolean canMoveY(int newX, int newY, BlockTracker blocks) {
                for (int x = newX; x < newX + 4; x++) {
                    if (blocks.isOccupied(x, newY)) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void update(int x, int height, BlockTracker blocks) {
                for (int i = x; i < x + 4; i++) {
                    blocks.occupy(i, height);
                }
            }
        },
        Plus {
            @Override
            public boolean canMoveX(int newX, int y, BlockTracker blocks) {
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

                return canMoveTo(newX, y, blocks);
            }

            private boolean canMoveTo(int x, int y, BlockTracker blocks) {
                return !(
                        blocks.isOccupied(x + 1, y) ||
                                blocks.isOccupied(x + 1, y + 1) ||
                                blocks.isOccupied(x + 1, y + 2) ||
                                blocks.isOccupied(x + 2, y + 1) ||
                                blocks.isOccupied(x, y + 1));
            }

            @Override
            public boolean canMoveY(int x, int newY, BlockTracker blocks) {
                return canMoveTo(x, newY, blocks);
            }

            @Override
            public void update(int x, int y, BlockTracker blocks) {
                blocks.occupy(x + 1, y);
                blocks.occupy(x + 1, y + 1);
                blocks.occupy(x + 1, y + 2);
                blocks.occupy(x, y + 1);
                blocks.occupy(x + 2, y + 1);
            }
        },
        L {
            @Override
            public boolean canMoveX(int newX, int y, BlockTracker blocks) {
                // |..#....|
                // |..#....|
                // |###....|
                if (newX < 0) {
                    return false;
                }

                if (newX > 4) {
                    return false;
                }

                return !cannotMove(newX, y, blocks);
            }

            @Override
            public boolean canMoveY(int x, int newY, BlockTracker blocks) {
                return !cannotMove(x, newY, blocks);
            }

            private boolean cannotMove(int x, int newY, BlockTracker blocks) {
                for (int i = x; i < x + 3; i++) {
                    if (blocks.isOccupied(i, newY)) {
                        return true;
                    }
                }

                if (blocks.isOccupied(x + 2, newY + 1)) {
                    return true;
                }

                if (blocks.isOccupied(x + 2, newY + 2)) {
                    return true;
                }
                return false;
            }

            @Override
            public void update(int x, int y, BlockTracker blocks) {
                for (int i = x; i < x + 3; i++) {
                    blocks.occupy(i, y);
                }
                blocks.occupy(x + 2, y + 1);
                blocks.occupy(x + 2, y + 2);
            }
        },
        Column {
            @Override
            public boolean canMoveX(int newX, int height, BlockTracker blocks) {
                if (newX < 0) {
                    return false;
                }

                if (newX > 6) {
                    return false;
                }

                for (int y = height; y < height + 4; y++) {
                    if (blocks.isOccupied(newX, y)) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public boolean canMoveY(int x, int height, BlockTracker blocks) {
                for (int y = height; y < height + 4; y++) {
                    if (blocks.isOccupied(x, y)) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void update(int x, int height, BlockTracker blocks) {
                for (int y = height; y < height + 4; y++) {
                    blocks.occupy(x, y);
                }
            }
        },
        Square {
            @Override
            public boolean canMoveX(int newX, int height, BlockTracker blocks) {
                if (newX < 0) {
                    return false;
                }

                if (newX > 5) {
                    return false;
                }

                return canMoveTo(newX, height, blocks);
            }

            private boolean canMoveTo(int newX, int height, BlockTracker heights) {
                return !(
                        heights.isOccupied(newX, height) ||
                        heights.isOccupied(newX + 1, height) ||
                        heights.isOccupied(newX, height + 1) ||
                        heights.isOccupied(newX + 1, height + 1)
                );
            }

            @Override
            public boolean canMoveY(int x, int newY, BlockTracker blocks) {
                return canMoveTo(x, newY, blocks);
            }

            @Override
            public void update(int x, int height, BlockTracker blocks) {
                blocks.occupy(x, height);
                blocks.occupy(x + 1, height);
                blocks.occupy(x, height + 1);
                blocks.occupy(x + 1, height + 1);
            }
        };

        public abstract boolean canMoveX(int newX, int height, BlockTracker blocks);
        public abstract boolean canMoveY(int x, int newY, BlockTracker blocks);

        public abstract void update(int x, int height, BlockTracker blocks);
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
        final BlockTracker blockTracker = newBlockTracker(5000, "#######");

        return new Tetris(blockTracker);
    }

    @Override
    Solution<Long> partTwo() {
        return new TetrisPeriodSpotter(newBlockTracker(50000, "#######"));
    }

    public Solution<Long> partTwo(String s) {
        return new TetrisPeriodSpotter(newBlockTracker(50000, s));
    }

    private static BlockTracker newBlockTracker(int size, String hmm) {
        final char[][] blocks = new char[size][7];
        for (char[] chars : blocks) {
            Arrays.fill(chars, '.');
        }
        Arrays.fill(blocks[0], '#');
        return new BlockTracker(blocks, hmm);
    }

    private static class TetrisPeriodSpotter implements Solution<Long> {
        private final BlockTracker blockTracker;
        private String jets;

        public TetrisPeriodSpotter(BlockTracker blockTracker) {
            this.blockTracker = blockTracker;
        }

        @Override
        public Long result() {
            Block[] blockTypes = Block.values();
            int jetIndex = 0;
            int rockIndex = 0;
            while (!blockTracker.hasSolution()) {
                int height = blockTracker.maxY + 4;
                int x = 2;
                boolean stopped = false;
                Block b = blockTypes[rockIndex % blockTypes.length];
                MoveType m = MoveType.Jet;

                while (!stopped) {
                    switch (m) {
                        case Down -> {
                            int newY = height - 1;
                            if (b.canMoveY(x, newY, blockTracker)) {
                                height = newY;
                            } else {
                                blockTracker.rockSettling(rockIndex + 1);
                                b.update(x, height, blockTracker);
                                blockTracker.rockSettled(rockIndex + 1);
                                stopped = true;
                            }
                        }
                        case Jet -> {
                            int dx = jets.charAt(jetIndex++ % jets.length()) == '<' ? -1 : 1;
                            int newX = x + dx;
                            if (b.canMoveX(newX, height, blockTracker)) {
                                x = newX;
                            }
                        }
                    }

                    m = m.next();
                }
                ++rockIndex;
            }

//            blockTracker.print(0, blockTracker.maxY);

            return blockTracker.result();
        }

        @Override
        public void accept(String s) {
            jets = s;
        }
    }

    private static class Tetris implements Solution<Integer> {
        private final BlockTracker blockTracker;
        private String jets;

        public Tetris(BlockTracker blockTracker) {
            this.blockTracker = blockTracker;
        }

        @Override
        public Integer result() {
            Block[] blockTypes = Block.values();
            int jetIndex = 0;
            for (int i = 0; i < 2022; i++) {
                int height = blockTracker.maxY + 4;
                int x = 2;
                boolean stopped = false;
                Block b = blockTypes[i % blockTypes.length];
                MoveType m = MoveType.Jet;

                while (!stopped) {
                    switch (m) {
                        case Down -> {
                            int newY = height - 1;
                            if (b.canMoveY(x, newY, blockTracker)) {
                                height = newY;
                            } else {
                                blockTracker.rockSettling(i + 1);
                                b.update(x, height, blockTracker);
                                stopped = true;
                            }
                        }
                        case Jet -> {
                            int dx = jets.charAt(jetIndex++ % jets.length()) == '<' ? -1 : 1;
                            int newX = x + dx;
                            if (b.canMoveX(newX, height, blockTracker)) {
                                x = newX;
                            }
                        }
                    }

                    m = m.next();
                }
            }

            blockTracker.print(0, blockTracker.maxY);

            return blockTracker.maxY;
        }

        @Override
        public void accept(String s) {
            jets = s;
        }
    }
}
