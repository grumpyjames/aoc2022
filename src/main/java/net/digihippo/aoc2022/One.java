package net.digihippo.aoc2022;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class One {
    public static int maximumCalories(String input) {
        final String[] parts = input.split("\n");
        final MaxObserver maxObserver = new MaxObserver();
        for (String part : parts) {
            maxObserver.accept(part);
        }
        return maxObserver.result();
    }

    public static int topThreeElves(String input) {
        final String[] parts = input.split("\n");
        final TopThreeValueObserver obs = new TopThreeValueObserver();
        for (String part : parts) {
            obs.accept(part);
        }
        return obs.result();
    }

    public static int maximumCaloriesStream(InputStream puzzleInput) throws IOException {
        MaxObserver callback = new MaxObserver();
        Lines.processLines(puzzleInput, callback);
        return callback.result();
    }

    public static int topThreeElvesStream(InputStream puzzleInput) throws IOException {
        final TopThreeValueObserver obs = new TopThreeValueObserver();
        Lines.processLines(puzzleInput, obs);
        return obs.result();
    }

    private static class MaxObserver implements Consumer<String> {
        int sum = 0;
        int max = 0;

        @Override
        public void accept(String part) {
            if (part.isEmpty()) {
                max = Math.max(sum, max);
                sum = 0;
            }
            else
            {
                sum += Integer.parseInt(part);
            }
        }

        public int result() {
            return Math.max(sum, max);
        }
    }

    private static class TopThreeValueObserver implements Consumer<String> {
        private final int[] topThree = new int[] {0, 0, 0};
        private int sum = 0;

        @Override
        public void accept(String part) {
            if (part.isEmpty()) {
                recalcTopThree();
                sum = 0;
            }
            else
            {
                sum += Integer.parseInt(part);
            }
        }

        private void recalcTopThree() {
            for (int i = 0; i < topThree.length; i++) {
                int best = topThree[i];
                if (best < sum)
                {
                    if (i == 0) {
                        topThree[2] = topThree[1];
                        topThree[1] = topThree[0];
                    }
                    else if (i == 1) {
                        topThree[2] = topThree[1];
                    }
                    topThree[i] = sum;
                    break;
                }
            }
        }

        public int result() {
            recalcTopThree();
            return topThree[0] + topThree[1] + topThree[2];
        }
    }
}
