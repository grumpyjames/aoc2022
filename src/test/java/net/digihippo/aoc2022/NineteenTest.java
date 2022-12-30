package net.digihippo.aoc2022;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.digihippo.aoc2022.Nineteen.ResourceType.Clay;
import static net.digihippo.aoc2022.Nineteen.ResourceType.Ore;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NineteenTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
            Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
            Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.""";

    public NineteenTest() {
        super(new Nineteen(), EXAMPLE_INPUT, 33, 34, "nineteen.txt");
    }

    @Test
    void waitIfNoBudgetToBuild() {
        Nineteen.Blueprint bp = Nineteen.parse("Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.");
        Nineteen.BuildingSite buildingSite = Nineteen.newBuildingSite(bp);

        List<Nineteen.BuildingSite> buildingSites = buildingSite.tickOptions(20).stream().map(Supplier::get).collect(Collectors.toList());
        assertEquals(List.of(buildingSite), buildingSites);
    }

    @Test
    void accrueResourcesCorrectly() {
        Nineteen.Blueprint bp = Nineteen.parse("Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.");
        Nineteen.BuildingSite buildingSite = Nineteen.newBuildingSite(bp);

        buildingSite.accrueResources();
        buildingSite.accrueResources();

        EnumSet<Nineteen.ResourceType> expected = EnumSet.of(Ore);
        assertEquals(expected, buildingSite.buildOptions(20));
    }

    @Test
    void eitherSkipOrBuy() {
        Nineteen.Blueprint bp = Nineteen.parse("Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.");
        Nineteen.BuildingSite buildingSite = Nineteen.newBuildingSite(bp);

        buildingSite.accrueResources();
        buildingSite.accrueResources();

        List<Nineteen.BuildingSite> buildingSites = buildingSite.tickOptions(20).stream().map(Supplier::get).collect(Collectors.toList());

        assertEquals(
                List.of(buildingSite.withAdditionalRobot(Ore), buildingSite.skippingRobotType(Ore)),
                buildingSites);
    }

    @Test
    void cannotSkipBothOreAndClay() {
        Nineteen.Blueprint bp = Nineteen.parse("Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.");
        Nineteen.BuildingSite buildingSite = Nineteen.newBuildingSite(bp);

        buildingSite.accrueResources();
        buildingSite.accrueResources();

        Nineteen.BuildingSite oreSkipped = buildingSite.skippingRobotType(Ore);

        oreSkipped.accrueResources();

        assertEquals(
                List.of(oreSkipped.withAdditionalRobot(Clay)),
                oreSkipped.tickOptions(20).stream().map(Supplier::get).toList());
    }
}