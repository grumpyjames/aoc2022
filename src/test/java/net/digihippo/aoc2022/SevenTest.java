package net.digihippo.aoc2022;

import static org.junit.jupiter.api.Assertions.*;

class SevenTest extends TestTemplate<Long, Long> {
    private static final String EXAMPLE_INPUT = """
            $ cd /
            $ ls
            dir a
            14848514 b.txt
            8504156 c.dat
            dir d
            $ cd a
            $ ls
            dir e
            29116 f
            2557 g
            62596 h.lst
            $ cd e
            $ ls
            584 i
            $ cd ..
            $ cd ..
            $ cd d
            $ ls
            4060174 j
            8033020 d.log
            5626152 d.ext
            7214296 k""";
    SevenTest() {
        super(new Seven(), EXAMPLE_INPUT, 95437L, 24933642L, "seven.txt");
    }
}