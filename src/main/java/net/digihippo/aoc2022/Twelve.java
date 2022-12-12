package net.digihippo.aoc2022;

import java.util.*;

public class Twelve extends SolutionTemplate<Integer, Integer> {
    record Node(int x, int y, char rightHeight) {}


    @Override
    Solution<Integer> partOne() {
        return new Solution<Integer>() {
            final List<String> heightMap = new ArrayList<>();

            @Override
            public Integer result() {
                final List<Node> nodes = new ArrayList<>();
                final Map<Node, Set<Node>> adjacent = new HashMap<>();
                for (int y = 0; y < heightMap.size(); y++) {
                    String line = heightMap.get(y);
                    for (int x = 0; x < line.length(); x++) {
                        char height = line.charAt(x);
                        Node node = new Node(x, y, height);
                        nodes.add(node);
                        final Set<Node> connected = new HashSet<>();
                        if (x > 0) {
                            char leftHeight = line.charAt(x - 1);
                            if (leftHeight - 1 == height || leftHeight == height) {
                                connected.add(new Node(x - 1, y, leftHeight));
                            }
                        }

                        if (x != line.length() - 1) {
                            char rightHeight = line.charAt(x + 1);
                            if (rightHeight - 1 == height || rightHeight == height) {
                                connected.add(new Node(x + 1, y, rightHeight));
                            }
                        }

                        if (y > 0) {
                            char upHeight = heightMap.get(y - 1).charAt(x);
                            if (upHeight - 1 == height || upHeight == height) {
                                connected.add(new Node(x, y - 1, upHeight));
                            }
                        }

                        if (y != heightMap.size() - 1) {
                            char downHeight = heightMap.get(y + 1).charAt(x);
                            if (downHeight - 1 == height || downHeight == height) {
                                connected.add(new Node(x, y + 1, downHeight));
                            }
                        }

                        adjacent.put(node, connected);
                    }
                }

                return 32;
            }

            @Override
            public void accept(String s) {
                heightMap.add(s);
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new Solution<Integer>() {
            @Override
            public Integer result() {
                return 33;
            }

            @Override
            public void accept(String s) {

            }
        };
    }
}
