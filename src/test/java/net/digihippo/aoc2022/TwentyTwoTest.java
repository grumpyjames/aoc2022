package net.digihippo.aoc2022;

class TwentyTwoTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
                    ...#
                    .#..
                    #...
                    ....
            ...#.......#
            ........#...
            ..#....#....
            ..........#.
                    ...#....
                    .....#..
                    .#......
                    ......#.
                        
            10R5L5R10L4R5L5""";

    TwentyTwoTest() {
        super(new TwentyTwo(), EXAMPLE_INPUT, 6032, 34234, "twentytwo.txt");
    }
}