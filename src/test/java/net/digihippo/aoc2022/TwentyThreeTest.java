package net.digihippo.aoc2022;

class TwentyThreeTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
            ....#..
            ..###.#
            #...#.#
            .#...##
            #.###..
            ##.#.##
            .#..#..""";

    public TwentyThreeTest() {
        super(new TwentyThree(), EXAMPLE_INPUT, 110, 20, "twentythree.txt");
    }
}