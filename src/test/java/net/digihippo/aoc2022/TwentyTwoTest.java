package net.digihippo.aoc2022;

import java.util.List;

class TwentyTwoTest extends TestTemplate<Integer, Integer> {
    private static final String EXAMPLE_INPUT = """
                    ...#
                    .#..
                    #...
                    ....
            ...#.......#
            ........#...
            ..#....#....
            ..........#.
                    ...#....
                    .....#..
                    .#......
                    ......#.
                        
            10R5L5R10L4R5L5""";

    TwentyTwoTest() {
        super(new TwentyTwo(), EXAMPLE_INPUT, 6032, 5031, "twentytwo.txt");
    }

    @Override
    void visitExampleTwo(SolutionTemplate.Solution<Integer> solution) {
        TwentyTwo.ElfMap em = (TwentyTwo.ElfMap) solution;

        em.acceptEdges(List.of(new TwentyTwo.Edge(new TwentyTwo.Judge() { // A -> B
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.y() == 0 && yAfter == -1;
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(3 - before.x(), 4, 0, 1);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // B -> A
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.y() == 4 & yAfter == 3 && (0 <= before.x() && before.x() <= 3);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(3 - before.x(), 0, 0, 1);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // A -> C
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.x() == 8 & xAfter == 7 && (0 <= before.y() && before.y() <= 3);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(4 + before.y(), 4, 0, 1);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // C -> A
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.y() == 4 & yAfter == 3 && (4 <= before.x() && before.x() <= 7);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(8, before.x() - 4,  1, 0);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // A -> F
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.x() == 11 & xAfter == 12 && (0 <= before.y() && before.y() <= 3);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(15, 11 - before.y(), -1, 0);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // F -> A
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.x() == 15 & xAfter == 16 && (8 <= before.y() && before.y() <= 11);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(11, 11 - before.y(), -1, 0);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // D -> F
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.x() == 11 & xAfter == 12 && (4 <= before.y() && before.y() <= 7);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        // x: 4 -> 15
                        // x: 7 -> 12
                        return new TwentyTwo.Position(19 - before.y(), 8, 0, 1);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // F -> D
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.y() == 8 & yAfter == 7 && (12 <= before.x() && before.x() <= 15);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(11, 19 - before.x(), -1, 0);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // C -> E
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.y() == 7 & yAfter == 8 && (4 <= before.x() && before.x() <= 7);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(8, 15 - before.x(), 1, 0);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // E -> C
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.x() == 8 & xAfter == 7 && (8 <= before.y() && before.y() <= 11);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(11, 15 - before.y(), 0, -1);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // C -> E
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.y() == 7 & yAfter == 8 && (4 <= before.x() && before.x() <= 7);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(8, 15 - before.x(), 1, 0);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // E -> C
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.x() == 8 & xAfter == 7 && (8 <= before.y() && before.y() <= 11);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(11, 15 - before.y(), 0, -1);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // B -> F
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.x() == 0 & xAfter == -1 && (4 <= before.y() && before.y() <= 7);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(19 - before.y(), 11, 0, -1);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // F -> B
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.y() == 11 & yAfter == 12 && (12 <= before.x() && before.x() <= 15);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(19 - before.x(), 15 - before.y(), 1, 0);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // B -> E
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.y() == 7 & yAfter == 8 && (0 <= before.x() && before.x() <= 3);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(11 - before.x(), 11, 0, -1);
                    }
                }), new TwentyTwo.Edge(new TwentyTwo.Judge() { // E -> B
                    @Override
                    public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                        return before.y() == 11 & yAfter == 12 && (8 <= before.x() && before.x() <= 11);
                    }

                    @Override
                    public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                        return new TwentyTwo.Position(11 - before.x(), 7, 0, -1);
                    }
                })
        ));
    }
}