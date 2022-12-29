package net.digihippo.aoc2022;

public class TwentyFive extends SolutionTemplate<String, String> {
    static long fromSnafu(String snafu) {
        int offset = snafu.length() - 1;
        long result = 0;
        for (int i = offset; i >= 0; i--) {
            int power = offset - i;
            long exped = (long) Math.pow(5, power);
            char c = snafu.charAt(i);
            switch (c) {
                case '=' -> result += (-2 * exped);
                case '-' -> result += (-1 * exped);
                case '0' -> result += (0);
                case '1' -> result += exped;
                case '2' -> result += (2 * exped);
            }
        }

        return result;
    }

    static String toSnafu(long decimal) {
        int power = 0;
        while (biggestPossible(power) < decimal) {
            power++;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = power; i >= 0; i--) {
            long exp = (long) Math.pow(5, i);
            long diff = Long.MAX_VALUE;
            long signedDiff = Long.MAX_VALUE;
            char best = 'p';
            for (int j = -2; j < 3; j++) {
                long mmmSignedDiff = decimal - (j * exp);
                long distance = Math.abs(mmmSignedDiff);
                if (distance < diff) {
                    diff = distance;
                    best = asChar(j);
                    signedDiff = mmmSignedDiff;
                }
            }
            stringBuilder.append(best);
            decimal = signedDiff;
        }

        return stringBuilder.toString();
    }

    private static char asChar(int j) {
        switch (j) {
            case -2 -> {
                return '=';
            }
            case -1 -> {
                return '-';
            }
            case 0 -> {
                return '0';
            }
            case 1 -> {
                return '1';
            }
            case 2 -> {
                return '2';
            }
        }

        throw new IllegalStateException();
    }

    private static long biggestPossible(int power) {
        long result = 0;
        while (power >= 0) {
            result += 2 * (Math.pow(5, power));
            power--;
        }

        return result;
    }


    @Override
    Solution<String> partOne() {
        return new Solution<String>() {
            long total = 0;
            @Override
            public String result() {
                return toSnafu(total);
            }

            @Override
            public void accept(String s) {
                total += fromSnafu(s);
            }
        };
    }

    @Override
    Solution<String> partTwo() {
        return new Solution<>() {
            @Override
            public String result() {
                return null;
            }

            @Override
            public void accept(String s) {

            }
        };
    }
}
