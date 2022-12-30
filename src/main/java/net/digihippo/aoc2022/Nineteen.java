package net.digihippo.aoc2022;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        List<BuildingSite> tickOptions(int timeLeft) {
            EnumSet<ResourceType> availableBuilds = buildOptions(timeLeft);
            // want: an option to build each available type right now
            //   or: an option to not build that type, but prevent that type being built next
            final List<BuildingSite> sites = new ArrayList<>();
            accrueResources();

            for (ResourceType availableBuild : availableBuilds) {
                sites.add(this.withAdditionalRobot(availableBuild));

                EnumSet<ResourceType> newCannotBuy = EnumSet.copyOf(cannotBuy);
                newCannotBuy.add(availableBuild);
                if (newCannotBuy.size() <= robots.keySet().size()) {
                    sites.add(this.skippingRobotType(availableBuild));
                }
            }

            if (sites.isEmpty()) {
                return List.of(this);
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
        return new RobotPlanOptimiser(24) {
            int result = 0;

            @Override
            protected Iterable<Blueprint> filtered(List<Blueprint> blueprints) {
                return blueprints;
            }

            @Override
            protected int resultPlease() {
                return result;
            }

            @Override
            void onResult(Blueprint blueprint, int geodes) {
                result += (blueprint.number * geodes);
            }
        };
    }

    @Override
    Solution<Integer> partTwo() {
        return new RobotPlanOptimiser(32) {
            int result = 1;
            @Override
            protected Iterable<Blueprint> filtered(List<Blueprint> blueprints) {
                return blueprints.subList(0, 3);
            }

            @Override
            protected int resultPlease() {
                return result;
            }

            @Override
            void onResult(Blueprint blueprint, int geodes) {
                result *= geodes;
            }
        };
    }


    private static void refilter(
            Map<EnumMap<ResourceType, Integer>, List<BuildingSite>> bsByResource,
            int bestGeodeCount) {
        for (List<BuildingSite> value : bsByResource.values()) {
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

            Iterator<BuildingSite> iterator = value.iterator();
            while (iterator.hasNext()) {
                BuildingSite buildingSite = iterator.next();

                boolean anyBetter = false;
                boolean anyWorse = false;
                for (ResourceType resourceType : ResourceType.values()) {
                    anyBetter |= (buildingSite.robots.getOrDefault(resourceType, 0) > best.getOrDefault(resourceType, 0));
                    anyWorse |= (buildingSite.robots.getOrDefault(resourceType, 0) < best.getOrDefault(resourceType, 0));
                }

                boolean notTheBest = !anyBetter && anyWorse;
                if (notTheBest) {
                    iterator.remove();
                } else if (buildingSite.resources.getOrDefault(ResourceType.Geode, 0) < bestGeodeCount - 2) {
                    iterator.remove();
                }
            }
        }
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

    private static abstract class RobotPlanOptimiser implements Solution<Integer> {
        private final List<Blueprint> blueprints = new ArrayList<>();
        private final int minutes;

        public RobotPlanOptimiser(int minutes) {
            this.minutes = minutes;
        }

        @Override
        public Integer result() {
            for (Blueprint blueprint : filtered(blueprints)) {
                Map<EnumMap<ResourceType, Integer>, List<BuildingSite>> byResources = new HashMap<>();
                BuildingSite site = newBuildingSite(blueprint);
                byResources.put(site.resources, List.of(site));

                boolean firstObsidian = true;
                boolean firstGeode = true;
                int trimNoObsidian = 0;
                int trimNoGeode = 0;

                for (int i = 0; i < minutes; i++) {
                    final int timeLeft = minutes - i;
                    final Map<EnumMap<ResourceType, Integer>, List<BuildingSite>> newResources = new HashMap<>();
                    final AtomicInteger maxGeodes = new AtomicInteger(0);
                    byResources
                            .values()
                            .stream()
                            .flatMap(bs -> bs.stream().flatMap(bss -> bss.tickOptions(timeLeft).stream()))
                            .forEach(buildingSite -> {
                                newResources.putIfAbsent(buildingSite.resources, new ArrayList<>());
                                newResources.get(buildingSite.resources).add(buildingSite);
                                maxGeodes.set(Math.max(maxGeodes.get(), buildingSite.resources.getOrDefault(ResourceType.Geode, 0)));
                            });

                    refilter(newResources, maxGeodes.get());
                    byResources = newResources;
//                    if (trimNoObsidian > 0 && trimNoObsidian == i) {
//                        trimNoObsidian = 0;
//                        buildingSites = buildingSites.stream().filter(BuildingSite::hasObsidianRobot).collect(Collectors.toList());
//                    }
//                    else if (trimNoGeode > 0 && trimNoGeode == i) {
//                        trimNoGeode = 0;
//                        buildingSites = buildingSites.stream().filter(BuildingSite::hasGeodeRobot).collect(Collectors.toList());
//                    }
//                    else if (firstObsidian && buildingSites.stream().anyMatch(BuildingSite::hasObsidianRobot)) {
//                        firstObsidian = false;
//                        trimNoObsidian = i + 2;
//                    }
//                    else if (firstGeode && buildingSites.stream().anyMatch(BuildingSite::hasGeodeRobot)) {
//                        firstGeode = false;
//                        trimNoGeode = i + 2;
//                    }
//                    else {
//                        refilter(byResources);
//                    }
                    System.out.println("At " + i + " have " + byResources.values().stream().mapToInt(List::size).sum() + " options");
                }

                int geodude = byResources.values().stream().flatMap(List::stream)
                        .mapToInt(bs -> bs.resources.getOrDefault(ResourceType.Geode, 0))
                        .max()
                        .getAsInt();
                System.out.println("Blueprint " + blueprint.number + ": " + geodude);
                onResult(blueprint, geodude);
            }

            return resultPlease();
        }

        protected abstract Iterable<Blueprint> filtered(List<Blueprint> blueprints);

        protected abstract int resultPlease();

        @Override
        public void accept(String s) {
            blueprints.add(parse(s));
        }

        abstract void onResult(Blueprint blueprint, int geodes);
    }
}
