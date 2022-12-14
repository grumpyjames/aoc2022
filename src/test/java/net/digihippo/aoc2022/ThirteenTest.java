package net.digihippo.aoc2022;

import org.junit.jupiter.api.Test;

import java.util.List;

import static net.digihippo.aoc2022.Thirteen.ordered;
import static org.junit.jupiter.api.Assertions.*;

class ThirteenTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
            [1,1,3,1,1]
            [1,1,5,1,1]
                        
            [[1],[2,3,4]]
            [[1],4]
                        
            [9]
            [[8,7,6]]
                        
            [[4,4],4,4]
            [[4,4],4,4,4]
                        
            [7,7,7,7]
            [7,7,7]
                        
            []
            [3]
                        
            [[[]]]
            [[]]
                        
            [1,[2,[3,[4,[5,6,7]]]],8,9]
            [1,[2,[3,[4,[5,6,0]]]],8,9]
            """;
    public ThirteenTest() {
        super(new Thirteen(), EXAMPLE_INPUT, 13, 140, "thirteen.txt");
    }

    @Test
    void ffs() {
        assertEquals(-1, ordered(List.of(List.of(4, 4), 4, 4), List.of(List.of(4, 4), 4, 4, 4)));

        assertEquals(0, ordered(List.of(1), List.of(1)));
        assertEquals(-1, ordered(List.of(2, 3, 4), 4));

        assertEquals(-1, ordered(List.of(1, 2, 3), List.of(4)));
        assertEquals(-1, ordered(List.of(3, 3), List.of(4)));
        assertEquals(1, ordered(List.of(3, 3, 3), List.of(3, 3)));
        assertEquals(1, ordered(List.of(9), List.of(List.of(8, 7, 6))));

        assertEquals(1, ordered(List.of(5, 6, 7), List.of(5, 6, 0)));
    }
}