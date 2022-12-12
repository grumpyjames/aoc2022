package net.digihippo.aoc2022;

import java.util.*;

public class Twelve extends SolutionTemplate<Integer, Integer> {
    record Node(int x, int y, char height) {}


    @Override
    Solution<Integer> partOne() {
        return new Dijkstra() {
            @Override
            public Integer result() {
                final Set<Node> nodes = new HashSet<>();
                final Map<Node, Set<Node>> adjacent = new HashMap<>();
                populateAdjacencySet(nodes, adjacent);

                return findShortestDistance(nodes, adjacent, startNode).distance;
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new Dijkstra() {
            @Override
            public Integer result() {
                final Set<Node> nodes = new HashSet<>();
                final Map<Node, Set<Node>> adjacent = new HashMap<>();
                populateAdjacencySet(nodes, adjacent);

                int bestDistance = Integer.MAX_VALUE;
                for (Node node : startNodes) {
                    Path p = findShortestDistance(new HashSet<>(nodes), adjacent, node);
                    if (p != null) {
                        int distance = 0;
                        for (int i = 0; i < p.path.size(); i++) {
                            Node n = p.path.get(i);
                            if (n.height > 'a') {
                                break;
                            }
                            distance = i;
                        }
                        int fromLastA = p.distance - distance;
                        if (fromLastA < bestDistance) {
                            bestDistance = fromLastA;
                        }
                    }
                }

                return bestDistance;
            }
        };
    }

    record Path(int distance, List<Node> path) {
        public Path add(Node n) {
            final List<Node> newPath = new ArrayList<>(this.path);
            newPath.add(n);
            return new Path(distance + 1, newPath);
        }
    }

    private static abstract class Dijkstra implements Solution<Integer> {
        protected Node startNode;
        protected List<Node> startNodes = new ArrayList<>();
        protected Node endNode;
        final List<String> heightMap = new ArrayList<>();

        @Override
        public abstract Integer result();

        protected Path findShortestDistance(Set<Node> nodes, Map<Node, Set<Node>> adjacent, Node start) {
            Map<Node, Path> distancesSoFar = new HashMap<>();
            distancesSoFar.put(start, new Path(0, List.of(startNode)));

            Node initialNode = start;
            while (initialNode != null) {
                nodes.remove(initialNode);
                Path path = distancesSoFar.get(initialNode);
                Set<Node> adjacentToThis = adjacent.get(initialNode);
                for (Node n : adjacentToThis) {
                    if (nodes.contains(n)) {
                        distancesSoFar.put(n, path.add(n));
                    }
                }
                initialNode = findCheapest(nodes, distancesSoFar);
            }

            return distancesSoFar.get(endNode);
        }

        protected void populateAdjacencySet(Set<Node> nodes, Map<Node, Set<Node>> adjacent) {
            for (int y = 0; y < heightMap.size(); y++) {
                String line = heightMap.get(y);
                for (int x = 0; x < line.length(); x++) {
                    char height = heightAt(heightMap, x, y);
                    Node node = new Node(x, y, height);
                    nodes.add(node);
                    final Set<Node> connected = new HashSet<>();

                    if (x > 0) {
                        char leftHeight = heightAt(heightMap, x - 1, y);
                        if (leftHeight <= height + 1) {
                            connected.add(new Node(x - 1, y, leftHeight));
                        }
                    }

                    if (x != line.length() - 1) {
                        char rightHeight = heightAt(heightMap, x + 1, y);
                        if (rightHeight <= height + 1) {
                            connected.add(new Node(x + 1, y, rightHeight));
                        }
                    }

                    if (y > 0) {
                        char upHeight = heightAt(heightMap, x, y - 1);
                        if (upHeight <= height + 1) {
                            connected.add(new Node(x, y - 1, upHeight));
                        }
                    }

                    if (y != heightMap.size() - 1) {
                        char downHeight = heightAt(heightMap, x, y + 1);
                        if (downHeight <= height + 1) {
                            connected.add(new Node(x, y + 1, downHeight));
                        }
                    }

                    adjacent.put(node, connected);
                }
            }
        }

        protected char heightAt(List<String> lines, int x, int y) {
            char c = lines.get(y).charAt(x);

            if (c == 'E') {
                char zPlusOne = 'z' + (char) 1;
                this.endNode = new Node(x, y, zPlusOne);
                return zPlusOne;
            }

            if (c == 'S') {
                this.startNode = new Node(x, y, 'a');
                this.startNodes.add(new Node(x, y, 'a'));
                return 'a';
            }

            if (c == 'a' && (x == 0 || x == lines.get(0).length() - 1 || y == 0 || y == lines.size() - 1)) {
                this.startNodes.add(new Node(x, y, 'a'));
            }

            return c;
        }

        protected Node findCheapest(Iterable<Node> nodes, Map<Node, Path> distancesSoFar) {
            int bestDistance = Integer.MAX_VALUE;
            Node best = null;
            for (Node node : nodes) {
                if (distancesSoFar.containsKey(node)) {
                    int distance = distancesSoFar.get(node).distance;
                    if (distance < bestDistance) {
                        best = node;
                        bestDistance = distance;
                    }
                }
            }

            return best;
        }

            /*
            while (initialNode != null && !initialNode.equals(new Point(risk[0].length - 1, risk.length - 1)))
        {
            unvisited.remove(initialNode);

            int distanceSoFar = distances[initialNode.y][initialNode.x];
            List<Point> neigh = initialNode.neighbours(risk);
            for (Point point : neigh) {
                if (unvisited.contains(point)) {
                    distances[point.y][point.x] =
                            Math.min(distances[point.y][point.x], distanceSoFar + risk[point.y][point.x]);
                }
            }
            // this is just silly.
            initialNode = findCheapest(unvisited, distances);
        }

        return distances[risk[0].length - 1][risk.length - 1];
    }
             */


        @Override
        public void accept(String s) {
            heightMap.add(s);
        }
    }
}
