package net.digihippo.aoc2022;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ten extends SolutionTemplate<Integer, String> {
    @Override
    Solution<Integer> partOne() {
        return new CpuSolution<>() {
            private final List<Integer> measurements = new ArrayList<>();

            @Override
            void onNewCycle(int cycle, int x) {
                if ((cycle - 20) % 40 == 0)
                {
                    measurements.add(x * cycle);
                }
            }

            @Override
            public Integer result() {
                return measurements.stream().mapToInt(e -> e).sum();
            }
        };
    }

    @Override
    Solution<String> partTwo() {
        final char[][] crt = new char[6][40];
        for (char[] chars : crt) {
            Arrays.fill(chars, '.');
        }

        return new CpuSolution<>() {

            @Override
            public String result() {
                final char[] result = new char[41 * 6];
                for (int i = 0; i < crt.length; i++) {
                    char[] chars = crt[i];
                    System.arraycopy(chars, 0, result, 41 * i, 40);
                    result[(41 * (i + 1)) - 1] = '\n';
                }
                return new String(result).strip();
            }

            @Override
            void onNewCycle(int cycle, int x) {
                int row = (cycle - 1) / 40;
                int column = (cycle - 1) % 40;
                int xColumn = x % 40;

                if (column - 1 <= xColumn && xColumn <= column + 1) {
                    crt[row][column] = '#';
                }
            }
        };
    }

    private abstract static class CpuSolution<T> implements Solution<T> {
        private int x = 1;
        private int counter = 1;

        @Override
        public void accept(String line) {
            newCycle();

            if (line.equals("noop"))
            {
                return;
            }

            newCycle();
            String[] parts = line.split(" ");
            int diff = Integer.parseInt(parts[1]);
            x += diff;
        }

        private void newCycle() {
            onNewCycle(counter, x);
            ++counter;
        }

        abstract void onNewCycle(int cycle, int x);
    }
}
