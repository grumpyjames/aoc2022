package net.digihippo.aoc2022;

import java.util.HashSet;
import java.util.Set;

public class Eighteen extends SolutionTemplate<Integer, Integer> {
    record Cube(int x, int y, int z) {}
    @Override
    Solution<Integer> partOne() {
        return new Solution<Integer>() {
            private final Set<Cube> cubes = new HashSet<>();
            @Override
            public Integer result() {
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
            }

            @Override
            public void accept(String s) {
                String[] coords = s.split(",");
                cubes.add(new Cube(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])));
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new Solution<Integer>() {
            @Override
            public Integer result() {
                return 43;
            }

            @Override
            public void accept(String s) {

            }
        };
    }
}
