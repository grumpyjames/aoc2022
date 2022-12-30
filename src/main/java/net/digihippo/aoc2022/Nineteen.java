package net.digihippo.aoc2022;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Nineteen extends SolutionTemplate<Integer, Integer> {
    public enum ResourceType {
        Ore,
        Clay,
        Obsidian,
        Geode
    }

    public static Blueprint newBlueprint(int blueprintNumber, EnumMap<ResourceType, Map<ResourceType, Integer>> costs) {
        final Map<ResourceType, Integer> maxCosts = new EnumMap<>(ResourceType.class);

        for (ResourceType value : ResourceType.values()) {
            int max = 0;
            for (Map<ResourceType, Integer> costMap : costs.values()) {
                max = Math.max(costMap.getOrDefault(value, 0), max);
            }
            maxCosts.put(value, max);
        }
        maxCosts.put(ResourceType.Geode, Integer.MAX_VALUE);

        return new Blueprint(blueprintNumber, costs, maxCosts);
    }

    record Blueprint(
            int number,
            Map<ResourceType, Map<ResourceType, Integer>> robotCosts,
            Map<ResourceType, Integer> maxCosts) {

        public int maxCost(ResourceType resource) {
            return maxCosts.get(resource);
        }
    }

    private static final Pattern PATTERN = Pattern.compile(
            "Blueprint ([0-9]+): Each ore robot costs ([0-9]+) ore. Each clay robot costs ([0-9]+) ore. Each obsidian robot costs ([0-9]+) ore and ([0-9]+) clay. Each geode robot costs ([0-9]+) ore and ([0-9]+) obsidian."
    );

    static final class BuildingSite {
        private final Blueprint blueprint;

        private final EnumMap<ResourceType, Integer> robots;
        private final EnumMap<ResourceType, Integer> resources;
        private final EnumSet<ResourceType> cannotBuy;

        BuildingSite(
                Blueprint blueprint,
                EnumMap<ResourceType, Integer> robots,
                EnumMap<ResourceType, Integer> resources,
                EnumSet<ResourceType> cannotBuy) {
            this.blueprint = blueprint;
            this.robots = robots;
            this.resources = resources;
            this.cannotBuy = cannotBuy;
        }

        EnumSet<ResourceType> buildOptions(int timeLeft) {
            final EnumSet<ResourceType> possible = EnumSet.noneOf(ResourceType.class);

            for (ResourceType value : ResourceType.values()) {
                if (!cannotBuy.contains(value) && canPay(blueprint.robotCosts.get(value), resources) && (robots.getOrDefault(value, 0) + 1) <= blueprint.maxCost(value)) {
                    possible.add(value);
                }
            }

            return possible;
        }

        List<Supplier<BuildingSite>> tickOptions(int timeLeft) {
            EnumSet<ResourceType> availableBuilds = buildOptions(timeLeft);
            // want: an option to build each available type right now
            //   or: an option to not build that type, but prevent that type being built next
            final List<Supplier<BuildingSite>> sites = new ArrayList<>();

            for (ResourceType availableBuild : availableBuilds) {
                sites.add(() -> this.withAdditionalRobot(availableBuild));

                EnumSet<ResourceType> newCannotBuy = EnumSet.copyOf(cannotBuy);
                newCannotBuy.add(availableBuild);
                if (newCannotBuy.size() <= robots.keySet().size()) {
                    sites.add(() -> this.skippingRobotType(availableBuild));
                }
            }

            if (sites.isEmpty()) {
                return List.of(() -> this);
            } else {
                return sites;
            }
        }

        void accrueResources() {
            robots.forEach((r, c) -> {
                resources.putIfAbsent(r, 0);
                resources.put(r, c + resources.get(r));
            });
        }

        BuildingSite skippingRobotType(ResourceType availableBuild) {
            EnumSet<ResourceType> newCannotBuy = EnumSet.copyOf(cannotBuy);
            newCannotBuy.add(availableBuild);

            return new BuildingSite(
                    blueprint,
                    new EnumMap<>(robots),
                    new EnumMap<>(resources),
                    newCannotBuy
            );
        }

        BuildingSite withAdditionalRobot(ResourceType availableBuild) {
            EnumMap<ResourceType, Integer> remainingResources = new EnumMap<>(resources);
            subtract(blueprint.robotCosts.get(availableBuild), remainingResources);
            EnumMap<ResourceType, Integer> moreRobots = new EnumMap<>(robots);
            moreRobots.put(availableBuild, moreRobots.getOrDefault(availableBuild, 0) + 1);
            return new BuildingSite(
                    blueprint,
                    moreRobots,
                    remainingResources,
                    EnumSet.noneOf(ResourceType.class)
            );
        }

        private boolean canPay(Map<ResourceType, Integer> costs, EnumMap<ResourceType, Integer> resources) {
            for (Map.Entry<ResourceType, Integer> costEntry : costs.entrySet()) {
                if (resources.getOrDefault(costEntry.getKey(), 0) < costEntry.getValue()) {
                    return false;
                }
            }

            return true;
        }

        private void subtract(Map<ResourceType, Integer> costs, EnumMap<ResourceType, Integer> resources) {
            for (Map.Entry<ResourceType, Integer> costEntry : costs.entrySet()) {
                resources.put(costEntry.getKey(), resources.get(costEntry.getKey()) - costEntry.getValue());
            }
        }

        @Override
        public String toString() {
            return "robots=" + robots + ", resources=" + resources + ", cannotBuy=" + cannotBuy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BuildingSite that = (BuildingSite) o;
            return Objects.equals(blueprint, that.blueprint) && Objects.equals(robots, that.robots) && Objects.equals(resources, that.resources) && Objects.equals(cannotBuy, that.cannotBuy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(blueprint, robots, resources, cannotBuy);
        }

        public boolean hasObsidianRobot() {
            return robots.getOrDefault(ResourceType.Obsidian, 0) > 0;
        }

        public boolean hasGeodeRobot() {
            return robots.getOrDefault(ResourceType.Geode, 0) > 0;
        }
    }

    private static <T> List<T> concat(List<T> l1, List<T> l2) {
        final List<T> result = new ArrayList<>(l1.size() + l2.size());
        result.addAll(l1);
        result.addAll(l2);
        return result;
    }

    @Override
    Solution<Integer> partOne() {
        return new Solution<>() {
            private final List<Blueprint> blueprints = new ArrayList<>();

            @Override
            public Integer result() {
                int result = 0;

                for (Blueprint blueprint : blueprints) {
                    BuildingSite site = newBuildingSite(blueprint);
                    List<BuildingSite> buildingSites = List.of(site);

                    boolean firstObsidian = true;
                    boolean firstGeode = true;
                    int trimNoObsidian = 0;
                    int trimNoGeode = 0;

                    for (int i = 0; i < 24; i++) {
                        final int timeLeft = 24 - i;
                        List<Supplier<BuildingSite>> bse = buildingSites
                                .stream()
                                .flatMap(bs -> bs.tickOptions(timeLeft).stream())
                                .toList();
                        buildingSites.forEach(BuildingSite::accrueResources);
                        buildingSites = bse.stream().map(Supplier::get).collect(Collectors.toList());

                        Map<EnumMap<ResourceType, Integer>, List<BuildingSite>> hmm = buildingSites
                                .stream()
                                .collect(Collectors.toMap(
                                        b -> b.resources,
                                        List::of,
                                        Nineteen::concat
                                ));

                        if (trimNoObsidian > 0 && trimNoObsidian == i) {
                            trimNoObsidian = 0;
                            buildingSites = buildingSites.stream().filter(BuildingSite::hasObsidianRobot).collect(Collectors.toList());
                        }
                        else if (trimNoGeode > 0 && trimNoGeode == i) {
                            trimNoGeode = 0;
                            buildingSites = buildingSites.stream().filter(BuildingSite::hasGeodeRobot).collect(Collectors.toList());
                        }
                        else if (firstObsidian && buildingSites.stream().anyMatch(BuildingSite::hasObsidianRobot)) {
                            firstObsidian = false;
                            trimNoObsidian = i + 2;
                        }
                        else if (firstGeode && buildingSites.stream().anyMatch(BuildingSite::hasGeodeRobot)) {
                            firstGeode = false;
                            trimNoGeode = i + 2;
                        }
                        else {
                            buildingSites = refilter(hmm);
                        }
                    }

                    int geodude = buildingSites
                            .stream()
                            .mapToInt(bs -> bs.resources.getOrDefault(ResourceType.Geode, 0))
                            .max()
                            .getAsInt();
                    System.out.println("Blueprint " + blueprint.number + ": " + geodude);
                    result += (blueprint.number * geodude);
                }

                return result;
            }

            @Override
            public void accept(String s) {
                blueprints.add(parse(s));
            }
        };
    }

    private List<BuildingSite> refilter(Map<EnumMap<ResourceType, Integer>, List<BuildingSite>> hmm) {
        final List<BuildingSite> results = new ArrayList<>();
        for (List<BuildingSite> value : hmm.values()) {
            Map<ResourceType, Integer> best = new EnumMap<>(ResourceType.class);
            boolean better = true;

            for (BuildingSite buildingSite : value) {
                for (ResourceType resourceType : ResourceType.values()) {
                    if (buildingSite.robots.getOrDefault(resourceType, 0) < best.getOrDefault(resourceType, 0)) {
                        better = false;
                    }
                }
                if (better) {
                    best = buildingSite.robots;
                }
            }

            for (BuildingSite buildingSite : value) {
                boolean anyBetter = false;
                boolean anyWorse = false;
                for (ResourceType resourceType : ResourceType.values()) {
                    anyBetter |= (buildingSite.robots.getOrDefault(resourceType, 0) > best.getOrDefault(resourceType, 0));
                    anyWorse |= (buildingSite.robots.getOrDefault(resourceType, 0) < best.getOrDefault(resourceType, 0));
                }
                if (anyBetter || !anyWorse) {
                    results.add(buildingSite);
                }
            }
        }


        return results;
    }


    public static BuildingSite newBuildingSite(Blueprint blueprint) {
        EnumMap<ResourceType, Integer> robots = new EnumMap<>(ResourceType.class);
        robots.put(ResourceType.Ore, 1);
        return new BuildingSite(
                blueprint,
                robots,
                new EnumMap<>(ResourceType.class),
                EnumSet.noneOf(ResourceType.class));
    }

    static Blueprint parse(String s) {
        final Matcher matcher = PATTERN.matcher(s);
        if (matcher.find()) {
            final int blueprintNumber = Integer.parseInt(matcher.group(1));
            final int oreRobotOreCost = Integer.parseInt(matcher.group(2));
            final int clayRobotOreCost = Integer.parseInt(matcher.group(3));
            final int obsidianRobotOreCost = Integer.parseInt(matcher.group(4));
            final int obsidianRobotClayCost = Integer.parseInt(matcher.group(5));
            final int geodeRobotOreCost = Integer.parseInt(matcher.group(6));
            final int geodeRobotObsidianCost = Integer.parseInt(matcher.group(7));
            final EnumMap<ResourceType, Map<ResourceType, Integer>> costs = new EnumMap<>(ResourceType.class);

            final EnumMap<ResourceType, Integer> oreRobotCosts = new EnumMap<>(ResourceType.class);
            oreRobotCosts.put(ResourceType.Ore, oreRobotOreCost);

            final EnumMap<ResourceType, Integer> clayRobotCosts = new EnumMap<>(ResourceType.class);
            clayRobotCosts.put(ResourceType.Ore, clayRobotOreCost);

            final EnumMap<ResourceType, Integer> obsidianRobotCosts = new EnumMap<>(ResourceType.class);
            obsidianRobotCosts.put(ResourceType.Ore, obsidianRobotOreCost);
            obsidianRobotCosts.put(ResourceType.Clay, obsidianRobotClayCost);

            final EnumMap<ResourceType, Integer> geodeRobotCosts = new EnumMap<>(ResourceType.class);
            geodeRobotCosts.put(ResourceType.Ore, geodeRobotOreCost);
            geodeRobotCosts.put(ResourceType.Obsidian, geodeRobotObsidianCost);

            costs.put(ResourceType.Ore, oreRobotCosts);
            costs.put(ResourceType.Clay, clayRobotCosts);
            costs.put(ResourceType.Obsidian, obsidianRobotCosts);
            costs.put(ResourceType.Geode, geodeRobotCosts);

            return newBlueprint(blueprintNumber, costs);
        }
        throw new IllegalStateException("Unparseable: " + s);
    }

    @Override
    Solution<Integer> partTwo() {
        return new Solution<>() {
            @Override
            public Integer result() {
                return 22;
            }

            @Override
            public void accept(String s) {

            }
        };
    }
}
