package net.digihippo.aoc2022;

public class FiveTest extends TestTemplate<String, String> {
    private static final String EXAMPLE_INPUT = """
                [D]   \s
            [N] [C]   \s
            [Z] [M] [P]
             1   2   3\s
                        
            move 1 from 2 to 1
            move 3 from 1 to 3
            move 2 from 2 to 1
            move 1 from 1 to 2""";

    public FiveTest() {
        super(new Five(), EXAMPLE_INPUT, "CMZ", "MCD", "five.txt");
    }
}
