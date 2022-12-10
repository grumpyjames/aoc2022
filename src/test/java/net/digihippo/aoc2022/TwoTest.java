package net.digihippo.aoc2022;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TwoTest {

    public static final String EXAMPLE_INPUT = """
            A Y
            B X
            C Z""";

    @Test
    void exampleOne()
    {
        assertEquals(15, Two.score(EXAMPLE_INPUT));
    }

    @Test
    void realOne() throws IOException {
        System.out.println(Two.scoreStream(Inputs.puzzleInput("two.txt")));
    }

    @Test
    void exampleTwo()
    {
        assertEquals(12, Two.scoreDifferent(EXAMPLE_INPUT));
    }

    @Test
    void realTwo() throws IOException {
        System.out.println(Two.scoreDifferentStream(Inputs.puzzleInput("two.txt")));
    }
}
