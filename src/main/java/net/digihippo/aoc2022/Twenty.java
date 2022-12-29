package net.digihippo.aoc2022;

import java.util.*;

public class Twenty extends SolutionTemplate<Long, Long> {
    private static final long DECRYPTION_KEY = 811589153L;

    private static final class Ffs
    {
        private final long entry;

        private Ffs(long entry) {
            this.entry = entry;
        }

        // deliberately no hc/eq, want object equality.
    }

    @Override
    Solution<Long> partOne() {
        return new Solution<Long>() {
            private final List<Ffs> entries = new ArrayList<>();
            private Ffs zero;

            @Override
            public Long result() {
                final List<Ffs> mixedItUp = new ArrayList<>(entries);

                for (Ffs entry : entries) {
                    int idx = mixedItUp.indexOf(entry);
                    long newIdx = idx + entry.entry;
                    newIdx %= entries.size() - 1; // -1 because we removed an item!
                    if (newIdx < 0)
                        newIdx += entries.size() - 1;

                    mixedItUp.remove(idx);
                    mixedItUp.add((int) newIdx, entry);
                }

                final int positionOfZero = mixedItUp.indexOf(zero);

                long a = mixedItUp.get((positionOfZero + 1000) % mixedItUp.size()).entry;
                long b = mixedItUp.get((positionOfZero + 2000) % mixedItUp.size()).entry;
                long c = mixedItUp.get((positionOfZero + 3000) % mixedItUp.size()).entry;
                return (a + b + c);
            }

            @Override
            public void accept(String s) {
                Ffs ffs = new Ffs(Integer.parseInt(s));
                entries.add(ffs);
                if (ffs.entry == 0) {
                    zero = ffs;
                }
            }
        };
    }

    @Override
    Solution<Long> partTwo() {
        return new Solution<Long>() {
            private final List<Ffs> entries = new ArrayList<>();
            private Ffs zero;

            @Override
            public Long result() {
                final List<Ffs> mixedItUp = new ArrayList<>(entries);

                for (int i = 0; i < 10; i++) {
                    for (Ffs entry : entries) {
                        int idx = mixedItUp.indexOf(entry);
                        long newIdx = idx + entry.entry;
                        newIdx %= entries.size() - 1; // -1 because we removed an item!
                        if (newIdx < 0)
                            newIdx += entries.size() - 1;

                        mixedItUp.remove(idx);
                        mixedItUp.add((int) newIdx, entry);
                    }
                }

                final int positionOfZero = mixedItUp.indexOf(zero);

                long a = mixedItUp.get((positionOfZero + 1000) % mixedItUp.size()).entry;
                long b = mixedItUp.get((positionOfZero + 2000) % mixedItUp.size()).entry;
                long c = mixedItUp.get((positionOfZero + 3000) % mixedItUp.size()).entry;
                return (a + b + c);
            }

            @Override
            public void accept(String s) {
                Ffs ffs = new Ffs(Long.parseLong(s) * DECRYPTION_KEY);
                entries.add(ffs);
                if (ffs.entry == 0) {
                    zero = ffs;
                }
            }
        };
    }
}
