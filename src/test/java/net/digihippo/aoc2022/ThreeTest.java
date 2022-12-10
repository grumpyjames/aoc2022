package net.digihippo.aoc2022;

public class ThreeTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
            vJrwpWtwJgWrhcsFMMfFFhFp
            jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
            PmmdzqPrVvPwwTWBwg
            wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
            ttgJtRGJQctTZtZT
            CrZsJsPPZsGzwwsLwLmpwMDw""";

    protected ThreeTest() {
        super(new Three(), EXAMPLE_INPUT, 157, 70, "three.txt");
    }
}
