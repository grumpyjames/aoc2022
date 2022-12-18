package net.digihippo.aoc2022;

import java.util.*;
import java.util.function.Function;

public class Eighteen extends SolutionTemplate<Integer, Integer> {
    record Cube(int x, int y, int z) {
        Set<Cube> neighbours() {
            final Set<Cube> result = new HashSet<>();

            result.add(new Cube(x + 1, y, z));
            result.add(new Cube(x - 1, y, z));
            result.add(new Cube(x, y + 1, z));
            result.add(new Cube(x, y - 1, z));
            result.add(new Cube(x, y, z + 1));
            result.add(new Cube(x, y, z - 1));

            return result;
        }
    }
    @Override
    Solution<Integer> partOne() {
        return new CubeSolution(cubes -> {
            int total = 0;

            for (Cube cube : cubes) {
                total += 6;
                if (cubes.contains(new Cube(cube.x + 1, cube.y, cube.z))) {
                    total--;
                }
                if (cubes.contains(new Cube(cube.x - 1, cube.y, cube.z))) {
                    total--;
                }
                if (cubes.contains(new Cube(cube.x, cube.y + 1, cube.z))) {
                    total--;
                }
                if (cubes.contains(new Cube(cube.x, cube.y - 1, cube.z))) {
                    total--;
                }
                if (cubes.contains(new Cube(cube.x, cube.y, cube.z + 1))) {
                    total--;
                }
                if (cubes.contains(new Cube(cube.x, cube.y, cube.z - 1))) {
                    total--;
                }
            }

            return total;
        });
    }

    record Bound(int low, int high) {}

    record Bounds(Bound x, Bound y, Bound z) {
        public boolean matchAny(Cube c) {
            return
                    c.x == x.low ||
                    c.x == x.high ||
                    c.y == y.low ||
                    c.y == y.high ||
                    c.z == z.low ||
                    c.z == z.high;
        }
    }

    @Override
    Solution<Integer> partTwo() {
        return new CubeSolution(cubes -> {
            int total = 0;
            final Map<Cube, Set<Cube>> trapped = new HashMap<>();

            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            int maxZ = Integer.MIN_VALUE;

            for (Cube cube : cubes) {
                minX = Math.min(cube.x, minX);
                minY = Math.min(cube.y, minY);
                minZ = Math.min(cube.z, minZ);
                maxX = Math.max(cube.x, maxX);
                maxY = Math.max(cube.y, maxY);
                maxZ = Math.max(cube.z, maxZ);

                total += 6;
                for (Cube neighbour : cube.neighbours()) {
                    total = examine(cubes, total, trapped, cube, neighbour);
                }
            }

            Bounds b = new Bounds(new Bound(minX - 1, maxX + 1), new Bound(minY - 1, maxY + 1), new Bound(minZ - 1, maxZ + 1));
            final Set<Cube> internal = new HashSet<>();
            final Set<Cube> external = new HashSet<>();
            for (int x = b.x.low; x < b.x.high + 1; x++) {
                for (int y = b.y.low; y < b.y.high + 1; y++) {
                    for (int z = b.z.low; z < b.z.high + 1; z++) {
                        assessTrapped(b, new Cube(x, y, z), cubes, internal, external);
                    }
                }
            }

            for (Cube cube : internal) {
                for (Cube neighbour : cube.neighbours()) {
                    if (cubes.contains(neighbour)) {
                        --total;
                    }
                }
            }

            return total;
        });
    }

    private void assessTrapped(
            Bounds bounds,
            Cube cube,
            Set<Cube> lava,
            Set<Cube> internal,
            Set<Cube> external) {
        if (external.contains(cube) || internal.contains(cube) || lava.contains(cube)) {
            return;
        }

        final Deque<Cube> toExamine = new ArrayDeque<>();
        final Set<Cube> examined = new HashSet<>();
        examined.add(cube);
        toExamine.addAll(cube.neighbours());
        while (!toExamine.isEmpty()) {
            Cube c = toExamine.pop();
            if (lava.contains(c)) {
                continue;
            }

            if (examined.add(c)) {
                if (external.contains(c) || bounds.matchAny(c)) {
                    external.addAll(examined);
                    return;
                } else if (internal.contains(c)) {
                    internal.addAll(examined);
                    return;
                } else {
                    toExamine.addAll(c.neighbours());
                }
            }
        }
        internal.addAll(examined);
    }

    private static int examine(Set<Cube> cubes, int total, Map<Cube, Set<Cube>> trapped, Cube cube, Cube facing) {
        if (cubes.contains(facing)) {
            total--;
        } else {
            if (trapped.containsKey(facing)) {
                trapped.get(facing).add(cube);
            } else {
                HashSet<Cube> set = new HashSet<>();
                set.add(cube);
                trapped.put(facing, set);
            }
        }
        return total;
    }

    private static class CubeSolution implements Solution<Integer> {
        private final Set<Cube> cubes = new HashSet<>();
        private final Function<Set<Cube>, Integer> resultComputer;

        private CubeSolution(Function<Set<Cube>, Integer> resultComputer) {
            this.resultComputer = resultComputer;
        }

        @Override
        public Integer result() {
            return resultComputer.apply(cubes);
        }

        @Override
        public void accept(String s) {
            String[] coords = s.split(",");
            cubes.add(new Cube(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])));
        }
    }
}
