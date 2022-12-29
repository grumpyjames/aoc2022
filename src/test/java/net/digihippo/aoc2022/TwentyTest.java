package net.digihippo.aoc2022;

class TwentyTest extends TestTemplate<Long, Long> {
    private static final String EXAMPLE_INPUT = """
            1
            2
            -3
            3
            -2
            0
            4""";

    public TwentyTest() {
        super(new Twenty(), EXAMPLE_INPUT, 3L, 1623178306L, "twenty.txt");
    }

}