package net.digihippo.aoc2022;

import java.util.ArrayList;
import java.util.List;

public class Thirteen extends SolutionTemplate<Integer, Integer> {

    record Pair(String left, String right) {}

    static class MutableIndex {
        private int i = 0;
        private final int max;

        MutableIndex(int max) {
            this.max = max;
        }

        boolean hasNext() {
            return i < max;
        }

        int next() {
            return i++;
        }
    }

    static int ordered(Object l, Object r) {
        if (listy(l) && listy(r)) {
            List l1 = (List) l;
            List r1 = (List) r;
            if (l1.isEmpty() && !r1.isEmpty()) {
                return -1;
            }
            int rightIndex = 0;
            int ordered = 0;
            for (int leftIndex = 0; leftIndex < l1.size();) {
                Object left = l1.get(leftIndex);
                if (rightIndex >= r1.size()) {
                    return 1;
                }
                Object right = r1.get(rightIndex);

                ordered = ordered(left, right);
                if (ordered > 0) {
                    return 1;
                } else if (ordered < 0) {
                    return -1;
                } else {
                    ++rightIndex;
                    ++leftIndex;
                }
            }
            return rightIndex < r1.size() ? -1 : ordered;

        } else if (l instanceof Integer && r instanceof Integer) {
            return (Integer) l > (Integer) r ? 1 : l == r ? 0 : -1;
        } else if (l instanceof List && r instanceof Integer) {
            return ordered(l, List.of(r));
        } else if (l instanceof Integer && r instanceof List) {
            return ordered(List.of(l), r);
        }

        throw new UnsupportedOperationException();
    }

    private static boolean listy(Object l) {
        return l instanceof List;
    }

    private static List<Object> parse(String left, MutableIndex index) {
        final List<Object> result = new ArrayList<>();
        int charIndex = 0;
        char[] itemBuffer = new char[2];
        while (index.hasNext()) {
            int i = index.next();
            char c = left.charAt(i);
            if (c == '[') {
                result.add(parse(left, index));
            } else if (c == ']') {
                if (charIndex > 0) {
                    result.add(Integer.parseInt(new String(itemBuffer, 0, charIndex)));
                }
                return result;
            } else if (c == ',') {
                if (charIndex > 0) {
                    result.add(Integer.parseInt(new String(itemBuffer, 0, charIndex)));
                }
                charIndex = 0;
            } else {
                itemBuffer[charIndex++] = c;
            }
        }

        return result;
    }

    @Override
    Solution<Integer> partOne() {
        return new PacketBollocks();
    }

    @Override
    Solution<Integer> partTwo() {
        return new Solution<Integer>() {
            private final List<Object> packets = new ArrayList<>();

            @Override
            public Integer result() {
                List<List<Integer>> divider = List.of(List.of(2));
                List<List<Integer>> dividerTwo = List.of(List.of(6));
                packets.add(divider);
                packets.add(dividerTwo);

                packets.sort(Thirteen::ordered);

                return (packets.indexOf(divider) + 1) * (packets.indexOf(dividerTwo) + 1);
            }

            @Override
            public void accept(String s) {
                if (!s.isBlank()) {
                    String l = s.strip();
                    packets.add(parse(l, new MutableIndex(l.length())).get(0));
                }
            }
        };
    }

    private static class PacketBollocks implements Solution<Integer> {
        private String left = null;
        private final List<Pair> pairs = new ArrayList<>();

        @Override
        public Integer result() {
            int orderCount = 0;
            for (int i = 0; i < pairs.size(); i++) {
                Pair pair = pairs.get(i);
                List<Object> lefties = (List<Object>) parse(pair.left, new MutableIndex(pair.left.length())).get(0);
                List<Object> righties = (List<Object>) parse(pair.right, new MutableIndex(pair.right.length())).get(0);

                int ordered = ordered(lefties, righties);
                if (ordered < 0) {
                    System.out.println("Pair " + i + " is ordered");
                    orderCount += (i + 1);
                } else {
                    System.out.println("Pair " + i + " is unordered");
                }
            }

            return orderCount;
        }

        @Override
        public void accept(String s) {
            if (left == null && !s.isEmpty()) {
                left = s;
            } else if (!s.isEmpty()) {
                pairs.add(new Pair(left, s));
                left = null;
            }
        }
    }
}
