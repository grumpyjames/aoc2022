package net.digihippo.aoc2022;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sixteen extends SolutionTemplate<Integer, Integer> {
    private static final class Valve {
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

        public void turn() {
            this.on = true;
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

    @Override
    Solution<Integer> partOne() {
        return new Solution<>() {
            private final Map<String, Valve> valves = new HashMap<>();
            private final Map<String, List<String>> adjacent = new HashMap<>();

            @Override
            public Integer result() {
                String currentRoom = "AA";

                int time = 0;
                while (time < 30) {
                    Valve valve = valves.get(currentRoom);
                    if (valve.worthTurning()) {
                        time = turnValveOn(time, valve);
                        valve.turn();
                    }

                    // BFS - move to nearest 'off' valve that has some pressure
                    Path chosen = mad(currentRoom);
                    if (chosen instanceof Tail) {
                        currentRoom = chosen.head();
                    }

                    time = moveTo(time, currentRoom);
                }

                return 42;
            }

            private int turnValveOn(int time, Valve valve) {
                System.out.println("== Minute " + (time + 1));
                valves.values().stream().filter(v -> v.on).forEach(v -> {
                    System.out.println("\tValve " + v.name + " releases " + v.pressure);
                });
                System.out.println("\tTurned on valve " + valve.name);

                return time + 1;
            }

            private int moveTo(int time, String room) {
                System.out.println("== Minute " + (time + 1));
                valves.values().stream().filter(v -> v.on).forEach(v -> {
                    System.out.println("\tValve " + v.name + " releases " + v.pressure + " pressure");
                });
                System.out.println("\tMoving to " + room);

                return time + 1;
            }

            private Path mad(String currentRoom) {
                boolean done = false;
                Map<Path, List<String>> searchSpace = new HashMap<>();
                searchSpace.put(new Empty(), adjacent.get(currentRoom));
                Map<Path, List<String>> nextLayer = new HashMap<>();

                final Set<Valve> visited = new HashSet<>();

                Path choice = new Empty();
                while (!done) {
                    final List<Valve> visitedThisTime = new ArrayList<>();
                    Iterator<Map.Entry<Path, List<String>>> it = searchSpace.entrySet().iterator();
                    while (it.hasNext()) {
                        final Map.Entry<Path, List<String>> entry = it.next();
                        final Path path = entry.getKey();
                        final List<String> options = entry.getValue();
                        for (String room : options) {
                            final Valve v = valves.get(room);
                            if (v == null) {
                                throw new IllegalStateException("wtf");
                            }
                            if (!visited.contains(v)) {
                                visitedThisTime.add(v);
                                Path candidate = path.add(v);
                                if (candidate.worthTurning()) {
                                    choice = candidate.pressure() > choice.pressure() ? candidate : choice;
                                } else {
                                    nextLayer.put(candidate, adjacent.get(v.name));
                                }
                            }
                        }

                        it.remove();
                    }

                    if (!choice.isEmpty()) {
                        done = true;
                    } else {
                        visited.addAll(visitedThisTime);
                        searchSpace = nextLayer;
                        nextLayer = new HashMap<>();
                    }
                }

                return choice;
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
