package net.digihippo.aoc2022;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sixteen extends SolutionTemplate<Integer, Integer> {
    private static final class Valve implements WorkItem {
        private final String name;
        private final int pressure;
        private boolean on = false;

        private Valve(String name, int pressure) {
            this.name = name;
            this.pressure = pressure;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Valve valve = (Valve) o;
            return Objects.equals(name, valve.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return name;
        }

        public void turn() {
            this.on = !this.on;
        }

        private boolean worthTurning() {
            return pressure > 0 && !on;
        }
    }

    // Valve HH has flow rate=22; tunnel leads to valve GG
    private static final Pattern PATTERN = Pattern.compile(
            "Valve ([A-Z]+) has flow rate=([0-9]+); tunnel(s?) lead(s)? to valve(s?) (.*)"
    );

    sealed interface Path permits Tail, Empty {

        Path add(Valve valve);

        boolean worthTurning();

        int pressure();

        String head();

        boolean isEmpty();
    }

    record Tail(Path h, Valve tail) implements Path {
        @Override
        public Path add(Valve valve) {
            return new Tail(this, valve);
        }

        @Override
        public boolean worthTurning() {
            return tail.worthTurning();
        }

        @Override
        public int pressure() {
            return tail.pressure + h.pressure();
        }

        @Override
        public String head() {
            return h.isEmpty() ? tail.name : h.head();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
    record Empty() implements Path {
        @Override
        public Path add(Valve valve) {
            return new Tail(this, valve);
        }

        @Override
        public boolean worthTurning() {
            return false;
        }

        @Override
        public int pressure() {
            return 0;
        }

        @Override
        public String head() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    record Connection(int cost, String to) {}

    sealed interface WorkItem permits Valve, Pending {}

    record Pending(Valve upstream, Valve lastSeen, int distance) implements WorkItem {}

    record ScoreItem(Valve v, double weight) {};

    record ShortestPath(int distance, List<Valve> path) {
        public ShortestPath add(int cost, Valve n) {
            final List<Valve> newPath = new ArrayList<>(this.path);
            newPath.add(n);
            return new ShortestPath(distance + cost, newPath);
        }

        public ShortestPath[] split(Valve valve, Map<String, Set<Connection>> connections) {
            boolean first = true;
            ShortestPath before = new ShortestPath(0, new ArrayList<>());
            ShortestPath after = new ShortestPath(0, new ArrayList<>());


            for (int i = path.size() - 2; i >= 0; i--) {
                Valve previous = this.path.get(i + 1);
                Valve now = this.path.get(i);

                Set<Connection> cs = connections.get(previous.name);
                int cost = cs
                        .stream()
                        .filter(c -> c.to.equals(now.name))
                        .findFirst()
                        .get()
                        .cost;

                if (first) {
                    before = before.add(cost, previous);
                } else {
                    after = after.add(cost, previous);
                }

                if (now.equals(valve)) {
                    before = before.add(0, now);
                    first = false;
                }
            }
            after = after.add(0, this.path.get(0));

            return new ShortestPath[] {before, after};
        }
    }

    record Best(Valve v, int score) {}

    @Override
    Solution<Integer> partOne() {
        return new Solution<Integer>() {
            private final Map<String, Valve> valves = new HashMap<>();
            private final Map<String, List<String>> adjacent = new HashMap<>();
            private final Map<String, Set<Connection>> connections = new HashMap<>();

            @Override
            public Integer result() {
                // first off, let's simplify the graph, getting rid of the 0 flow nodes.
                simplifyGraph();

                final List<Valve> visitOrder = visitWorthyValves();

                final Map<Valve, Map<Valve, ShortestPath>> allShortestPaths = new HashMap<>();
                Valve aa = valves.get("AA");
                Map<Valve, ShortestPath> paths = findShortestPaths(
                        new HashSet<>(visitWorthyValves()),
                        this.connections,
                        aa
                );
                allShortestPaths.put(aa, paths);
                for (Valve valve : visitOrder) {
                    allShortestPaths.put(
                            valve,
                            findShortestPaths(new HashSet<>(valves.values()), this.connections, valve));
                }

                visitOrder.sort((o1, o2) -> {
                    int o1Distance = paths.get(o1).distance;
                    int o2Distance = paths.get(o2).distance;
                    if (o1Distance < o2Distance) {
                        return o2.pressure - ((1 + (o2Distance - o1Distance)) * o1.pressure);
                    } else if (o2Distance < o1Distance) {
                        return -(o1.pressure - ((1 + (o1Distance - o2Distance)) * o2.pressure));
                    } else {
                        return o2.pressure - o1.pressure;
                    }
                });


                int time = 0;
                Valve currentValve = aa;

                while (time < 30) {
                    Best bestScore = findBestScore(currentValve, new HashSet<>(visitWorthyValves()), allShortestPaths, 30 - time);
                    Valve valve = bestScore.v;
                    if (valve != null) {
                        ShortestPath shortestPath = allShortestPaths.get(currentValve).get(valve);
                        int distance = shortestPath.distance;
                        time = moveTo(time, distance, valve);
                        time = turnValveOn(time, valve);
                        valve.turn();
                        currentValve = valve;
                    } else {
                        time = tick(time);
                    }
                }

                return pressure;
            }

            private ArrayList<Valve> visitWorthyValves() {
                return new ArrayList<>(valves.values().stream().filter(Valve::worthTurning).toList());
            }

            private Best findBestScore(
                    Valve to,
                    Set<Valve> all,
                    Map<Valve, Map<Valve, ShortestPath>> allShortestPaths,
                    int timeRemaining) {
                int best = 0;
                Valve bestValve = null;
                for (Valve valve : all) {
                    if (!valve.equals(to)) {
                        int score = 0;
                        if (timeRemaining > 0) {
                            int timeToOpenOneValve = 1;
                            final int timeValveSpendsOpen =
                                    timeRemaining - (timeToOpenOneValve + allShortestPaths.get(to).get(valve).distance);
                            final int pressureReleased = timeValveSpendsOpen * (valve.on ? 0 : valve.pressure);
                            valve.turn();
                            Best rest = findBestScore(valve, new HashSet<>(visitWorthyValves()), allShortestPaths, timeValveSpendsOpen);
                            valve.turn();
                            score = pressureReleased + rest.score;
                        }

                        if (score > best) {
                            best = score;
                            bestValve = valve;
                        }
                    }
                }
                return new Best(bestValve, best);
            }

            private Map<Valve, ShortestPath> findShortestPaths(
                    Set<Valve> valves,
                    Map<String, Set<Connection>> adjacent,
                    Valve start) {
                Map<Valve, ShortestPath> pathsSoFar = new HashMap<>();
                pathsSoFar.put(start, new ShortestPath(0, List.of(start)));

                Valve current = start;
                while (current != null) {
                    valves.remove(current);
                    ShortestPath path = pathsSoFar.get(current);
                    Set<Connection> adjacentToThis = adjacent.get(current.name);
                    for (Connection connection : adjacentToThis) {
                        Valve toValve = this.valves.get(connection.to);
                        if (valves.contains(toValve)) {
                            pathsSoFar.put(toValve, path.add(connection.cost, toValve));
                        }
                    }
                    current = findCheapest(valves, pathsSoFar);
                }

                return pathsSoFar;
            }

            private Valve findCheapest(Iterable<Valve> valves, Map<Valve, ShortestPath> distancesSoFar) {
                int bestDistance = Integer.MAX_VALUE;
                Valve best = null;
                for (Valve valve : valves) {
                    if (distancesSoFar.containsKey(valve)) {
                        int distance = distancesSoFar.get(valve).distance;
                        if (distance < bestDistance) {
                            best = valve;
                            bestDistance = distance;
                        }
                    }
                }

                return best;
            }

            private void simplifyGraph() {
                final Deque<WorkItem> queue = new ArrayDeque<>();
                final Set<Valve> visited = new HashSet<>();
                queue.add(valves.get("AA"));
                while (!queue.isEmpty()) {
                    WorkItem v = queue.poll();
                    if (v instanceof Valve parent) {
                        if (visited.add(parent)) {
                            List<String> connected = adjacent.get(parent.name);
                            for (String name : connected) {
                                Valve valve = valves.get(name);
                                if (valve.pressure > 0) {
                                    connect(parent, name, 1);
                                    queue.push(valve);
                                } else {
                                    queue.push(new Pending(parent, valve, 1));
                                }
                            }
                        }
                    } else if (v instanceof Pending p) {
                        if (visited.add(p.lastSeen)) {
                            List<String> connected = adjacent.get(p.lastSeen.name);
                            for (String name : connected) {
                                Valve valve = valves.get(name);
                                if (valve.pressure > 0) {
                                    connect(p.upstream, name, p.distance + 1);
                                    queue.push(valve);
                                } else {
                                    queue.push(new Pending(p.upstream, valve, p.distance + 1));
                                }
                            }
                        }
                    }
                }
            }

            private void connect(Valve parent, String name, int cost) {
                if (parent.name.equals(name)) {
                    return;
                }

                connections.putIfAbsent(parent.name, new HashSet<>());
                connections.get(parent.name).add(new Connection(cost, name));

                connections.putIfAbsent(name, new HashSet<>());
                connections.get(name).add(new Connection(cost, parent.name));
            }

            int pressure = 0;

            private int turnValveOn(int time, Valve valve) {
                System.out.println("== Minute " + (time + 1));
                releasePressure();
                System.out.println("\tTurned on valve " + valve.name);

                return time + 1;
            }

            private void releasePressure() {
                valves.values().stream().filter(v -> v.on).forEach(v -> {
                    System.out.println("\tValve " + v.name + " releases " + v.pressure);
                    pressure += v.pressure;
                });
                System.out.println("\tTotal pressure is now " + pressure);
            }

            private int tick(int time) {
                System.out.println("== Minute " + (time + 1));
                releasePressure();

                return time + 1;
            }

            private int moveTo(int time, int distance, Valve destination) {
                int resultTime = time;
                for (int i = 0; i < distance; i++) {
                    System.out.println("== Minute " + (resultTime + 1));
                    releasePressure();
                    resultTime++;
                }
                System.out.println("\tMoving to " + destination);

                return resultTime;
            }

            @Override
            public void accept(String s) {
                // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
                Matcher matcher = PATTERN.matcher(s);
                if (matcher.find()) {
                    final Valve valve = new Valve(matcher.group(1), Integer.parseInt(matcher.group(2)));
                    final String[] connections = matcher.group(6).split(", ");

                    final List<String> conns = new ArrayList<>();
                    Collections.addAll(conns, connections);
                    valves.put(valve.name, valve);
                    adjacent.put(valve.name, conns);
                }
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new Solution<>() {
            @Override
            public Integer result() {
                return 54;
            }

            @Override
            public void accept(String s) {

            }
        };
    }
}
