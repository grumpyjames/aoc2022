package net.digihippo.aoc2022;

import static org.junit.jupiter.api.Assertions.*;

class EighteenTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
            2,2,2
            1,2,2
            3,2,2
            2,1,2
            2,3,2
            2,2,1
            2,2,3
            2,2,4
            2,2,6
            1,2,5
            3,2,5
            2,1,5
            2,3,5""";

    public EighteenTest() {
        super(new Eighteen(), EXAMPLE_INPUT, 64, 10, "eighteen.txt");
    }
}