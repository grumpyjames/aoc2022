package net.digihippo.aoc2022;

class TwelveTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
            Sabqponm
            abcryxxl
            accszExk
            acctuvwj
            abdefghi""";
    public TwelveTest() {
        super(new Twelve(), EXAMPLE_INPUT, 31, 31, "twelve.txt");
    }
}