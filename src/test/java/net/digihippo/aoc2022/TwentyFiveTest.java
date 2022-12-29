package net.digihippo.aoc2022;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class TwentyFiveTest extends TestTemplate<String, String> {
    private static final String EXAMPLE_INPUT = """
            1=-0-2
            12111
            2=0=
            21
            2=01
            111
            20012
            112
            1=-1=
            1-12
            12
            1=
            122""";

    public TwentyFiveTest() {
        super(new TwentyFive(), EXAMPLE_INPUT, "2=-1=0", null, "twentyfive.txt");
    }

    @Test
    void anExample() {
        assertEquals(1747L, TwentyFive.fromSnafu("1=-0-2"));
    }

    @Test
    void anotherExample() {
        assertEquals(1257L, TwentyFive.fromSnafu("20012"));
    }
}