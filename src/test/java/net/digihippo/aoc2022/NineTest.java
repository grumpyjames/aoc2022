package net.digihippo.aoc2022;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NineTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
            R 4
            U 4
            L 3
            D 1
            R 4
            D 1
            L 5
            R 2""";


    protected NineTest() {
        super(new Nine(), EXAMPLE_INPUT, 13, 1, "nine.txt");
    }

    @Test
    void alternativeExample() {
        assertEquals(36, solution.examplePartTwo("""
                R 5
                U 8
                L 8
                D 3
                R 17
                D 10
                L 25
                U 20"""));
    }
}