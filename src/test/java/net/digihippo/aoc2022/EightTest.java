package net.digihippo.aoc2022;

public class EightTest extends TestTemplate<Integer, Integer> {

    private static final String EXAMPLE_INPUT = """
            30373
            25512
            65332
            33549
            35390""";

    EightTest() {
        super(new Eight(), EXAMPLE_INPUT, 21, 8, "eight.txt");
    }
}
