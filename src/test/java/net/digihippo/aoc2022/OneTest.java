package net.digihippo.aoc2022;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static net.digihippo.aoc2022.Inputs.puzzleInput;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OneTest
{
    private static final String EXAMPLE_INPUT = """
            1000
            2000
            3000
                            
            4000
                            
            5000
            6000
                            
            7000
            8000
            9000
                            
            10000
            """;

    @Test
    void exampleOne()
    {
        assertEquals(24000, One.maximumCalories(EXAMPLE_INPUT));
    }

    @Test
    void realOne() throws IOException {
        System.out.println(One.maximumCaloriesStream(puzzleInput("one.txt")));
    }

    @Test
    void exampleTwo() {
        assertEquals(45000, One.topThreeElves(EXAMPLE_INPUT));
    }

    @Test
    void realTwo() throws IOException {
        System.out.println(One.topThreeElvesStream(puzzleInput("one.txt")));
    }

}
