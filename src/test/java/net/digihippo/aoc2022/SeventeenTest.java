package net.digihippo.aoc2022;

class SeventeenTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>";
    public SeventeenTest() {
        super(new Seventeen(), EXAMPLE_INPUT, 3068, 43, "seventeen.txt");
    }

}