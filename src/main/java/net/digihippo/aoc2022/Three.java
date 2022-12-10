package net.digihippo.aoc2022;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Three extends SolutionTemplate<Integer, Integer> {

    private static int priorities(List<Character> items) {
        int sum = 0;
        for (Character character : items) {
            int priority = character < 'a' ? 27 + (character - 'A') : 1 + (character - 'a');
            sum += priority;
        }
        return sum;
    }

    @Override
    Solution<Integer> partOne() {
        return new RucksackExaminer();
    }

    @Override
    Solution<Integer> partTwo() {
        return new BadgeExaminer();
    }

    private static final class BadgeExaminer implements Solution<Integer>
    {
        int groupCount = 0;
        List<Character> badges = new ArrayList<>();
        List<String> group = new ArrayList<>();

        @Override
        public void accept(String rucksack) {
            group.add(rucksack);
            groupCount++;
            if (groupCount == 3)
            {
                processGroup();
                groupCount = 0;
                group.clear();
            }
        }

        private void processGroup() {
            Set<Character> one = toSet(group.get(0));
            Set<Character> two = toSet(group.get(1));
            Set<Character> three = toSet(group.get(2));
            Set<Character> intersectionOne = Sets.intersect(one, two);
            badges.addAll(Sets.intersect(intersectionOne, three));
        }

        private static Set<Character> toSet(String rucksack) {
            Set<Character> chars = new HashSet<>();
            for (char c : rucksack.toCharArray()) {
                chars.add(c);
            }
            return chars;
        }

        public Integer result() {
            return priorities(badges);
        }
    }

    private static final class RucksackExaminer implements Solution<Integer>
    {
        private final List<Character> common = new ArrayList<>();

        @Override
        public void accept(String rucksack) {
            final Set<Character> c1 = new HashSet<>();
            final Set<Character> c2 = new HashSet<>();
            int halfLength = rucksack.length() / 2;
            for (int i = 0; i < halfLength; i++) {
                 c1.add(rucksack.charAt(i));
                 c2.add(rucksack.charAt(i + halfLength));
            }

            common.addAll(Sets.intersect(c1, c2));
        }

        public Integer result() {

            return priorities(common);
        }
    }

}
