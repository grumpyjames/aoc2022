package net.digihippo.aoc2022;

import java.util.HashSet;
import java.util.Set;

public class Sets {
    public static <T> Set<T> intersect(Set<T> first, Set<T> second) {
        final Set<T> result = new HashSet<>();
        for (final T t : first) {
            if (second.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }
}
