package net.digihippo.aoc2022;

class FourteenTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
            498,4 -> 498,6 -> 496,6
            503,4 -> 502,4 -> 502,9 -> 494,9""";

    protected FourteenTest() {
        super(new Fourteen(), EXAMPLE_INPUT, 24, 93, "fourteen.txt");
    }
}