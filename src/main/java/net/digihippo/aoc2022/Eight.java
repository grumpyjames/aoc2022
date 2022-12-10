package net.digihippo.aoc2022;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Eight extends SolutionTemplate<Integer, Integer> {
    record Tree(int x, int y) {}

    @Override
    Solution<Integer> partOne() {
        return new Solution<>() {
            final List<int[]> rows = new ArrayList<>();

            @Override
            public Integer result() {
                Set<Tree> invisibleHorizontally = new HashSet<>();
                Set<Tree> invisibleVertically = new HashSet<>();

                for (int i = 1; i < rows.size() - 1; i++) {
                    final Set<Tree> visibleRight = new HashSet<>();
                    final Set<Tree> visibleLeft = new HashSet<>();
                    int[] row = rows.get(i);
                    int maxSoFar = 0;
                    for (int j = 0; j < row.length; j++) {
                        int height = row[j];
                        if (height <= maxSoFar)
                        {
                            visibleLeft.add(new Tree(j, i));
                        }
                        maxSoFar = Math.max(height, maxSoFar);
                    }
                    maxSoFar = 0;
                    for (int j = row.length - 1; j >= 0; j--) {
                        int height = row[j];
                        if (height <= maxSoFar)
                        {
                            visibleRight.add(new Tree(j, i));
                        }
                        maxSoFar = Math.max(height, maxSoFar);
                    }
                    Set<Tree> intersect = Sets.intersect(visibleRight, visibleLeft);
                    invisibleHorizontally.addAll(intersect);
                }

                for (int column = 1; column < rows.get(0).length - 1; column++)
                {
                    final Set<Tree> visibleDown = new HashSet<>();
                    final Set<Tree> visibleUp = new HashSet<>();

                    int maxSoFar = 0;
                    for (int row = 0; row < rows.size(); row++)
                    {
                        int height = rows.get(row)[column];
                        if (height <= maxSoFar)
                        {
                            visibleDown.add(new Tree(column, row));
                        }
                        maxSoFar = Math.max(height, maxSoFar);
                    }

                    maxSoFar = 0;
                    for (int row = rows.size() - 1; row >= 0; row--)
                    {
                        int height = rows.get(row)[column];
                        if (height <= maxSoFar)
                        {
                            visibleUp.add(new Tree(column, row));
                        }
                        maxSoFar = Math.max(height, maxSoFar);
                    }

                    invisibleVertically.addAll(Sets.intersect(visibleDown, visibleUp));
                }

                final Set<Tree> invisible = Sets.intersect(invisibleHorizontally, invisibleVertically);
                return (rows.size() * rows.get(0).length) - invisible.size();
            }

            @Override
            public void accept(String s) {
                char[] chars = s.toCharArray();
                int[] heights = new int[chars.length];
                for (int i = 0; i < chars.length; i++) {
                    char aChar = chars[i];
                    heights[i] = aChar - '0';
                }
                rows.add(heights);
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new Solution<>() {
            final List<int[]> rows = new ArrayList<>();


            @Override
            public Integer result() {
                int columnCount = rows.get(0).length;

                int best = 0;

                for (int row = 0; row < rows.size(); row++) {
                    for (int column = 0; column < columnCount; column++) {
                        final int height = rows.get(row)[column];

                        int upScore = 0;
                        int k = row;
                        // look up
                        while (--k >= 0) {
                            ++upScore;
                            if (height <= rows.get(k)[column]) {
                                break;
                            }
                        }

                        // look down
                        k = row;
                        int downScore = 0;
                        while (++k < rows.size()) {
                            ++downScore;
                            if (height <= rows.get(k)[column]) {
                                break;
                            }
                        }

                        // look left
                        k = column;
                        int leftScore = 0;
                        while (--k >= 0) {
                            ++leftScore;
                            if (height <= rows.get(row)[k]) {
                                break;
                            }
                        }

                        // look right
                        int rightScore = 0;
                        k = column;
                        while (++k < columnCount) {
                            ++rightScore;
                            if (height <= rows.get(row)[k]) {
                                break;
                            }
                        }

                        int score = upScore * downScore * leftScore * rightScore;
                        best = Math.max(score, best);
                    }
                }


                return best;
            }

            @Override
            public void accept(String s) {
                char[] chars = s.toCharArray();
                int[] heights = new int[chars.length];
                for (int i = 0; i < chars.length; i++) {
                    char aChar = chars[i];
                    heights[i] = aChar - '0';
                }
                rows.add(heights);
            }
        };
    }
}
