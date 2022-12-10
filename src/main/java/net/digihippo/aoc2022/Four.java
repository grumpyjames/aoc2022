package net.digihippo.aoc2022;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Four extends SolutionTemplate<Integer, Integer> {
    private final String regex = "([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)";
    private final Pattern p = Pattern.compile(regex);

    private record Elf(int low, int high) {}

    private record Pair(Elf one, Elf two) {
        @SuppressWarnings("RedundantIfStatement")
        public boolean oneContainsOther() {
            if (one.low <= two.low && two.high <= one.high)
            {
                return true;
            }

            if (two.low <= one.low && one.high <= two.high)
            {
                return true;
            }

            return false;
        }

        public boolean hasOverlap() {
            if (one.low <= two.low)
            {
                return one.high >= two.low;
            }

            return two.high >= one.low;
        }
    }

    private Pair parse(final String line)
    {
        final Matcher matcher = p.matcher(line);
        if (matcher.find()) {
            final int elfOneLow = Integer.parseInt(matcher.group(1));
            final int elfOneHigh = Integer.parseInt(matcher.group(2));
            final int elfTwoLow = Integer.parseInt(matcher.group(3));
            final int elfTwoHigh = Integer.parseInt(matcher.group(4));
            return new Pair(
                    new Elf(elfOneLow, elfOneHigh),
                    new Elf(elfTwoLow, elfTwoHigh)
            );
        }
        throw new IllegalStateException("No match found in " + line);
    }

    @Override
    Solution<Integer> partOne() {
        return new Solution<>() {
            private int count = 0;

            @Override
            public Integer result() {
                return count;
            }

            @Override
            public void accept(String s) {
                Pair p = parse(s);
                if (p.oneContainsOther()) {
                    ++count;
                }
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new Solution<>() {
            int count = 0;

            @Override
            public Integer result() {
                return count;
            }

            @Override
            public void accept(String s) {
                Pair p = parse(s);
                if (p.hasOverlap()) {
                    ++count;
                }
            }
        };
    }
}
