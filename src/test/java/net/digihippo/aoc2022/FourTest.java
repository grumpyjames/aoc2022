package net.digihippo.aoc2022;

public class FourTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
            2-4,6-8
            2-3,4-5
            5-7,7-9
            2-8,3-7
            6-6,4-6
            2-6,4-8""";

    protected FourTest() {
        super(new Four(), EXAMPLE_INPUT, 2, 4, "four.txt");
    }
}
