package net.digihippo.aoc2022;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeventeenTest extends TestTemplate<Integer, Long> {
    private static final String EXAMPLE_INPUT = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>";
    public SeventeenTest() {
        super(new Seventeen(), EXAMPLE_INPUT, 3068, 1514285714288L, "seventeen.txt");
    }

    @Test
    void exampleTwoAgain() {
        SolutionTemplate.Solution<Long> hmm = new Seventeen().partTwo("###.###");
        hmm.accept(EXAMPLE_INPUT);
        assertEquals(exampleTwoAnswer, hmm.result());
    }
}