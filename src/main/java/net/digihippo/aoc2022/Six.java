package net.digihippo.aoc2022;

import java.util.HashSet;
import java.util.Set;

public class Six extends SolutionTemplate<Integer, Integer> {
    @Override
    Solution<Integer> partOne() {
        return new BufferSolution(4);
    }

    @Override
    Solution<Integer> partTwo() {
        return new BufferSolution(14);
    }

    private static class BufferSolution implements Solution<Integer> {
        private final int bufferSize;
        private int solution;

        private BufferSolution(int bufferSize) {
            this.bufferSize = bufferSize;
        }

        @Override
        public Integer result() {
            return solution;
        }

        @Override
        public void accept(String s) {
            final Set<Character> set = new HashSet<>();
            for (int i = 0; i < s.length() - bufferSize; i++) {
                set.clear();
                String fragment = s.substring(i, i + bufferSize);
                char[] chars = fragment.toCharArray();
                for (char aChar : chars) {
                    set.add(aChar);
                }
                if (set.size() == bufferSize)
                {
                    solution = i + bufferSize;
                    break;
                }
            }

        }
    }
}
