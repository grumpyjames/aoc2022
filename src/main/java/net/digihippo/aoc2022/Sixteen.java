package net.digihippo.aoc2022;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sixteen extends SolutionTemplate<Integer, Integer> {
    private static final class Valve implements WorkItem {
        private final String name;
        private final int pressure;
        private boolean on = false;
        private final int hashcode;

        private Valve(String name, int pressure) {
            this.name = name;
            this.pressure = pressure;
            this.hashcode = Objects.hash(name);
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
            return hashcode;
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

    sealed interface ConsList<T> permits Head, EmptyC {
        int score();

        List<T> toList();
    }

    record CoOp(ConsList<Valve> me, ConsList<Valve> elephant) {
        public int score() {
            return me.score() + elephant().score();
        }
    }

    record Head<T>(T t, int score, ConsList<T> rest) implements ConsList<T> {

        @Override
        public List<T> toList() {
            final List<T> res = new ArrayList<>();

            res.add(t);
            res.addAll(rest.toList());

            return res;
        }

        @Override
        public String toString() {
            return t.toString() + " -> " + rest;
        }


    }
    record EmptyC<T>() implements ConsList<T> {
        @Override
        public int score() {
            return 0;
        }

        @Override
        public List<T> toList() {
            return Collections.emptyList();
        }

        @Override
        public String toString() {
            return "{}";
        }
    }

    @Override
    Solution<Integer> partOne() {
        return new Elephant() {
            void doYourThing(Map<Valve, Map<Valve, Integer>> allShortestPaths) {
                int time = 0;
                Valve currentValve = valves.get("AA");

                ConsList<Valve> bestRoute = findBestScore(
                        currentValve,
                        new HashSet<>(visitWorthyValves()),
                        new HashSet<>(),
                        allShortestPaths,
                        30 - time);
                List<Valve> path = bestRoute.toList();
                Iterator<Valve> iterator = path.iterator();
                while (time < 30) {
                    if (iterator.hasNext()) {
                        Valve valve = iterator.next();
                        int distance = allShortestPaths.get(currentValve).get(valve);
                        time = move(time, distance);
                        System.out.println("\tMoving to " + valve);
                        time = turnValvesOn(time, valve);
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
                Valve myCurrentValve = valves.get("AA");
                Valve eleCurrentValve = valves.get("AA");
                CoOp coOp = findCoOpScore(
                        true,
                        myCurrentValve,
                        eleCurrentValve,
                        new HashSet<>(visitWorthyValves()),
                        new HashSet<>(),
                        allShortestPaths,
                        26,
                        26);

                Deque<Valve> eleValves = new ArrayDeque<>(coOp.elephant.toList());
                Deque<Valve> myValves = new ArrayDeque<>(coOp.me.toList());

                Valve eleNextValve = eleValves.poll();
                Valve myNextValve = myValves.poll();
                int myDistance = allShortestPaths.get(myCurrentValve).get(myNextValve);
                int eleDistance = allShortestPaths.get(eleCurrentValve).get(eleNextValve);

                while (time < 26) {
                    assert eleDistance >= 0;
                    assert myDistance >= 0;
                    if (myNextValve == null && eleNextValve == null) {
                        time = tick(time);
                    } else if (myNextValve == null) {
                        time = turnValvesOn(time, eleNextValve);
                        eleCurrentValve = eleNextValve;
                        eleNextValve = eleValves.poll();
                        if (eleNextValve != null) {
                            eleDistance = allShortestPaths.get(eleCurrentValve).get(eleNextValve);
                        } else {
                            eleDistance = 0;
                        }
                    } else if (eleNextValve == null) {
                        time = move(time, myDistance);
                        time = turnValvesOn(time, myNextValve);
                        myCurrentValve = myNextValve;
                        myNextValve = myValves.poll();
                        if (myNextValve != null) {
                            myDistance = allShortestPaths.get(myCurrentValve).get(myNextValve);
                        } else {
                            myDistance = 0;
                        }
                    } else if (myDistance == eleDistance) {
                        time = move(time, myDistance);
                        System.out.println("\tI move to " + myNextValve + ", elephant moves to " + eleNextValve);
                        time = turnValvesOn(time, myNextValve, eleNextValve);

                        myCurrentValve = myNextValve;
                        myNextValve = myValves.poll();
                        if (myNextValve != null) {
                            myDistance = allShortestPaths.get(myCurrentValve).get(myNextValve);
                        } else {
                            myDistance = 0;
                        }

                        eleCurrentValve = eleNextValve;
                        eleNextValve = eleValves.poll();
                        if (eleNextValve != null) {
                            eleDistance = allShortestPaths.get(eleCurrentValve).get(eleNextValve);
                        } else {
                            eleDistance = 0;
                        }
                    } else if (eleDistance < myDistance) {
                        time = move(time, eleDistance);
                        System.out.println("\tElephant moves to " + eleNextValve);

                        myDistance -= eleDistance;
                        time = turnValvesOn(time, eleNextValve);
                        myDistance--; // valve turning on time

                        eleCurrentValve = eleNextValve;
                        eleNextValve = eleValves.poll();
                        if (eleNextValve != null) {
                            eleDistance = allShortestPaths.get(eleCurrentValve).get(eleNextValve);
                        } else {
                            eleDistance = 0;
                        }
                    } else {
                        // myDistance < eleDistance
                        time = move(time, myDistance);
                        System.out.println("\tI move to " + myNextValve);
                        eleDistance -= myDistance;
                        time = turnValvesOn(time, myNextValve);
                        eleDistance--; // valve turning on time

                        myCurrentValve = myNextValve;
                        myNextValve = myValves.poll();
                        if (myNextValve != null) {
                            myDistance = allShortestPaths.get(myCurrentValve).get(myNextValve);
                        } else {
                            myDistance = 0;
                        }
                    }
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

        protected CoOp findCoOpScore(
                boolean top,
                Valve myFrom,
                Valve eleFrom,
                Set<Valve> all,
                Set<Valve> visited,
                Map<Valve, Map<Valve, Integer>> allShortestPaths,
                int myTimeRemaining,
                int elephantTimeRemaining
        ) {
            if (myTimeRemaining <= 0 && elephantTimeRemaining <= 0) {
                return new CoOp(new EmptyC<>(), new EmptyC<>());
            }

            if (myTimeRemaining <= 0) {
                return new CoOp(new EmptyC<>(), findBestScore(eleFrom, all, visited, allShortestPaths, elephantTimeRemaining));
            }

            if (elephantTimeRemaining <= 0) {
                return new CoOp(findBestScore(myFrom, all, visited, allShortestPaths, myTimeRemaining), new EmptyC<>());
            }

            int best = 0;
            int myScore = 0;
            Valve myBest = null;
            int eleScore = 0;
            Valve eleBest = null;
            CoOp bestest = null;

            int timeToOpenOneValve = 1;
            for (Valve myValve : all) {
                if (visited.contains(myValve)) {
                    continue;
                }

                if (top) System.out.println(Instant.now() + ": Examining valve " + myValve);

                assert !myValve.equals(myFrom);
                final int distanceToMyValve = allShortestPaths.get(myFrom).get(myValve);
                final int myTimeRemainingAfterOpeningValve =
                        myTimeRemaining - (timeToOpenOneValve + distanceToMyValve);
                final int pressureReleasedByMe =
                        myTimeRemainingAfterOpeningValve * myValve.pressure;

                visited.add(myValve);

                for (Valve eleValve: all) {
                    if (visited.contains(eleValve)) {
                        continue;
                    }
                    assert !eleValve.equals(eleFrom);
                    final int distanceToEleValve = allShortestPaths.get(eleFrom).get(eleValve);
                    final int eleTimeRemainingAfterOpeningValve =
                            elephantTimeRemaining - (timeToOpenOneValve + distanceToEleValve);
                    final int pressureReleasedByEle =
                            eleTimeRemainingAfterOpeningValve * eleValve.pressure;
                    visited.add(eleValve);

                    CoOp combo = findCoOpScore(
                            false,
                            myValve,
                            eleValve,
                            all,
                            visited,
                            allShortestPaths,
                            myTimeRemainingAfterOpeningValve,
                            eleTimeRemainingAfterOpeningValve
                    );
                    int scoreThisCoop = pressureReleasedByMe + pressureReleasedByEle + combo.score();
                    if (scoreThisCoop > best) {
                        myScore = pressureReleasedByMe + combo.me.score();
                        eleScore = pressureReleasedByEle + combo.elephant.score();
                        best = scoreThisCoop;
                        bestest = combo;
                        myBest = myValve;
                        eleBest = eleValve;
                    }

                    visited.remove(eleValve);
                }

                visited.remove(myValve);
            }

            if (myBest == null) {
                return new CoOp(new EmptyC<>(), new EmptyC<>());
            }

            return new CoOp(new Head<>(myBest, myScore, bestest.me), new Head<>(eleBest, eleScore, bestest.elephant));
        }

        protected ConsList<Valve> findBestScore(
                Valve from,
                Set<Valve> all,
                Set<Valve> visited,
                Map<Valve, Map<Valve, Integer>> allShortestPaths,
                int timeRemaining) {
            if (timeRemaining <= 0) {
                return new EmptyC<>();
            }

            int best = 0;
            Valve bestValve = null;
            ConsList<Valve> bestest = new EmptyC<>();
            for (Valve valve : all) {
                if (visited.contains(valve)) {
                    continue;
                }
                assert !valve.equals(from);

                int timeToOpenOneValve = 1;
                int distance = allShortestPaths.get(from).get(valve);
                final int timeRemainingAfterOpeningThisValve =
                        timeRemaining - (timeToOpenOneValve + distance);
                final int pressureReleased =
                        timeRemainingAfterOpeningThisValve * valve.pressure;
                visited.add(valve);
                ConsList<Valve> rest = findBestScore(
                        valve,
                        all,
                        visited,
                        allShortestPaths,
                        timeRemainingAfterOpeningThisValve);
                visited.remove(valve);

                int score = pressureReleased + rest.score();

                if (score > best) {
                    best = score;
                    bestValve = valve;
                    bestest = rest;
                }
            }

            if (bestValve == null) {
                return new EmptyC<>();
            }

            return new Head<>(bestValve, best, bestest);
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
            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") final Map<Valve, Valve> previous = new HashMap<>();

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

        protected int turnValvesOn(int time, Valve... valves) {
            System.out.println("== Minute " + (time + 1));
            releasePressure();
            for (Valve valve : valves) {
                System.out.println("\tTurned on valve " + valve.name);
                valve.turn();
            }

            return time + 1;
        }

        private void releasePressure() {
            valves.values().stream().filter(v -> v.on).forEach(v -> {
                System.out.println("\tValve " + v.name + " releases " + v.pressure);
                pressure += v.pressure;
            });
            System.out.println("\tTotal pressure is now " + pressure);
        }

        protected int move(int time, int distance) {
            int resultTime = time;
            for (int i = 0; i < distance; i++) {
                System.out.println("== Minute " + (resultTime + 1));
                releasePressure();
                resultTime++;
            }

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
