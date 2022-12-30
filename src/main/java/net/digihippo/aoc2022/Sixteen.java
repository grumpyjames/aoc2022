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

    record Connection(int cost, Valve to) {}

    sealed interface WorkItem permits Valve, Pending {}

    record Pending(Valve upstream, List<Valve> seen, int distance) implements WorkItem {}

    record Best(Valve v, int score) {}

    @Override
    Solution<Integer> partOne() {
        return new Elephant() {
            void doYourThing(Map<Valve, Map<Valve, Integer>> allShortestPaths) {
                int time = 0;
                Valve currentValve = valves.get("AA");

                while (time < 30) {
                    Best bestScore = findBestScore(currentValve, new HashSet<>(visitWorthyValves()), allShortestPaths, 30 - time);
                    Valve valve = bestScore.v;
                    if (valve != null) {
                        int distance = allShortestPaths.get(currentValve).get(valve);
                        time = moveTo(time, distance, valve);
                        time = turnValveOn(time, valve);
                        valve.turn();
                        currentValve = valve;
                    } else {
                        time = tick(time);
                    }
                }
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new Elephant() {

            @Override
            void doYourThing(Map<Valve, Map<Valve, Integer>> allShortestPaths) {
                int time = 0;
                Valve myValve = valves.get("AA");
                Valve eleValve = valves.get("AA");

                while (time < 26) {
                    Best myScore = findBestScore(myValve, new HashSet<>(visitWorthyValves()), allShortestPaths, 26 - time);

                    myScore.v.turn();
                    Best eleScore = findBestScore(eleValve, new HashSet<>(visitWorthyValves()), allShortestPaths, 26 - time);
                    myScore.v.turn();

                    Valve myNextValve = myScore.v;
                    Valve eleNextValve = eleScore.v;

                    Integer myNextDistance = allShortestPaths.get(myValve).get(myNextValve);
                    Integer eleNextDistance = allShortestPaths.get(eleValve).get(eleNextValve);

                    if (myNextDistance <= eleNextDistance) {
                        time = moveTo(time, myNextDistance, myNextValve);
                    }

//                    if (valve != null) {
//                        int distance = allShortestPaths.get(myValve).get(valve);
//                        time = moveTo(time, distance, valve);
//                        time = turnValveOn(time, valve);
//                        valve.turn();
//                        myValve = valve;
//                    } else {
//                        time = tick(time);
//                    }
                }
            }
        };
    }

    private static abstract class Elephant implements Solution<Integer> {
        protected final Map<String, Valve> valves = new HashMap<>();
        private final Map<String, List<String>> adjacent = new HashMap<>();

        @Override
        public Integer result() {
            // first off, let's simplify the graph, getting rid of the 0 flow nodes.
            final Map<Valve, Map<Valve, Integer>> allShortestPaths = new HashMap<>();
            final Map<Valve, Set<Connection>> altConns = connecto(valves, adjacent);
            for (Valve valve : valves.values()) {
                Map<Valve, Integer> shortestPaths =
                        findShortestPaths(new HashSet<>(valves.values()), altConns, valve);
                allShortestPaths.put(valve, shortestPaths);
            }

            Iterator<Map.Entry<Valve, Map<Valve, Integer>>> iterator = allShortestPaths.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Valve, Map<Valve, Integer>> e = iterator.next();
                if (e.getKey().worthTurning() || e.getKey().name.equals("AA")) {
                    Map<Valve, Integer> value = e.getValue();
                    value.entrySet().removeIf(f -> !f.getKey().worthTurning() && !f.getKey().name.equals("AA"));
                } else {
                    iterator.remove();
                }
            }


            doYourThing(allShortestPaths);

            return pressure;
        }

        abstract void doYourThing(Map<Valve, Map<Valve, Integer>> allShortestPaths);

        protected Best findBestScore(
                Valve from,
                Set<Valve> all,
                Map<Valve, Map<Valve, Integer>> allShortestPaths,
                int timeRemaining) {
            int best = 0;
            Valve bestValve = null;
            for (Valve valve : all) {
                if (!valve.equals(from)) {
                    int score = 0;
                    if (timeRemaining > 0) {
                        int timeToOpenOneValve = 1;
                        int distance = allShortestPaths.get(from).get(valve);
                        final int timeRemainingAfterOpeningThisValve =
                                timeRemaining - (timeToOpenOneValve + distance);
                        assert !valve.on;
                        final int pressureReleased =
                                timeRemainingAfterOpeningThisValve * valve.pressure;
                        valve.turn();
                        Best rest = findBestScore(
                                valve,
                                new HashSet<>(visitWorthyValves()),
                                allShortestPaths,
                                timeRemainingAfterOpeningThisValve);
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

        private Map<Valve, Set<Connection>> connecto(
                Map<String, Valve> valves,
                Map<String, List<String>> adjacent) {
            final Map<Valve, Set<Connection>> result = new HashMap<>();

            for (Map.Entry<String, List<String>> entry : adjacent.entrySet()) {
                String fromName = entry.getKey();
                Valve from = valves.get(fromName);

                HashSet<Connection> cs = new HashSet<>();
                for (String to : entry.getValue()) {
                    cs.add(new Connection(1, valves.get(to)));
                }

                result.put(from, cs);
            }

            return result;
        }

        protected List<Valve> visitWorthyValves() {
            return valves.values().stream().filter(Valve::worthTurning).toList();
        }

        private Map<Valve, Integer> findShortestPaths(
                Set<Valve> valves,
                Map<Valve, Set<Connection>> adjacent,
                Valve start) {
            final Map<Valve, Integer> distances = new HashMap<>();
            final Map<Valve, Valve> previous = new HashMap<>();

            valves.forEach(v -> distances.put(v, Integer.MAX_VALUE));

            distances.put(start, 0);

            while (!valves.isEmpty()) {
                Valve v = nearest(distances, valves);
                int soFar = distances.get(v);
                valves.remove(v);

                Set<Connection> conns = adjacent.get(v);
                for (Connection conn : conns) {
                    Valve u = conn.to;
                    if (valves.contains(u)) {
                        int alt = soFar + conn.cost;
                        if (alt < distances.get(u)) {
                            distances.put(u, alt);
                            previous.put(v, u);
                        }
                    }
                }
            }

            return distances;
        }

        private Valve nearest(Map<Valve, Integer> distances, Set<Valve> valves) {
            Valve best = null;
            int near = Integer.MAX_VALUE;
            for (Valve valve : valves) {
                Integer d = distances.get(valve);
                if (d != null) {
                    if (d < near) {
                        best = valve;
                        near = d;
                    }
                }
            }

            return best;
        }

        int pressure = 0;

        protected int turnValveOn(int time, Valve valve) {
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

        protected int moveTo(int time, int distance, Valve destination) {
            int resultTime = time;
            for (int i = 0; i < distance; i++) {
                System.out.println("== Minute " + (resultTime + 1));
                releasePressure();
                resultTime++;
            }
            System.out.println("\tMoving to " + destination);

            return resultTime;
        }

        protected int tick(int time) {
            System.out.println("== Minute " + (time + 1));
            releasePressure();

            return time + 1;
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
    }
}
