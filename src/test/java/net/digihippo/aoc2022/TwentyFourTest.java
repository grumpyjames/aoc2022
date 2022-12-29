package net.digihippo.aoc2022;

class TwentyFourTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
            #.######
            #>>.<^<#
            #.<..<<#
            #>v.><>#
            #<^v^^>#
            ######.#""";

    public TwentyFourTest() {
        super(new TwentyFour(), EXAMPLE_INPUT, 18, 54, "twentyfour.txt");
    }
}