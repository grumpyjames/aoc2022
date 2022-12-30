package net.digihippo.aoc2022;

import java.util.ArrayList;
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

    @Override
    void visitRealTwo(SolutionTemplate.Solution<Integer> solution) {
        TwentyTwo.ElfMap elfMap = (TwentyTwo.ElfMap) solution;

        final List<TwentyTwo.Edge> edges = new ArrayList<>();

        // A: 50->99,0->49
        // B: 100->149,0->49
        // C: 50->99,50->99
        // D: 0->49,100->149
        // E: 50->99,100->149
        // F: 0->49,150->199
        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // A -> F
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.y() == 0 && yAfter == -1 && (50 <= before.x() && before.x() <= 99);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // leftmost a goes to topmost f
                return new TwentyTwo.Position(0, 100 + before.x(), 1, 0);
            }
        }));
        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // F -> A
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.x() == 0 && xAfter == -1 && (150 <= before.y() && before.y() <= 199);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // topmost f goes to leftmost a
                return new TwentyTwo.Position(before.y() - 100, 0, 0, 1);
            }
        }));

        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // A -> D
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.x() == 50 && xAfter == 49 && (0 <= before.y() && before.y() <= 49);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // topmost a goes to bottom most d
                // y: 0 goes to y 149
                // y: 49 goes to y 100
                return new TwentyTwo.Position(0, 149 - before.y(), 1, 0);
            }
        }));
        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // D -> A
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.x() == 0 && xAfter == -1 && (100 <= before.y() && before.y() <= 149);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // bottommost d goes to top most a
                // y: 149 goes to y 0
                // y: 100 goes to y 49
                return new TwentyTwo.Position(50, 149 - before.y(), 1, 0);
            }
        }));

        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // B -> F
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.y() == 0 && yAfter == -1 && (100 <= before.x() && before.x() <= 149);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // leftmost b goes to leftmost f
                // x 100 -> x 0
                return new TwentyTwo.Position(before.x() - 100, 199, 0, -1);
            }
        }));
        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // F -> B
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.y() == 199 && yAfter == 200 && (0 <= before.x() && before.x() <= 49);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // leftmost b goes to leftmost f
                // x 100 -> x 0
                return new TwentyTwo.Position(before.x() + 100, 0, 0, 1);
            }
        }));

        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // B -> E
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.x() == 149 && xAfter == 150 && (0 <= before.y() && before.y() <= 49);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // topmost b goes to bottom-most e
                // y 0 -> y 149
                // y 49 -> y 100
                return new TwentyTwo.Position(99, 149 - before.y(), -1, 0);
            }
        }));
        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // E -> B
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.x() == 99 && xAfter == 100 && (100 <= before.y() && before.y() <= 149);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // bottom-most e goes to top-most b
                // y 100 -> y 49
                // y 149 -> y 0
                return new TwentyTwo.Position(149, 149 - before.y(), -1, 0);
            }
        }));

        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // B -> C
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.y() == 49 && yAfter == 50 && (100 <= before.x() && before.x() <= 149);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // leftmost b goes to top-most c
                // x 100 -> y 50
                // x 149 -> y 99
                return new TwentyTwo.Position(99, before.x() - 50, -1, 0);
            }
        }));

        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // C -> B
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.x() == 99 && xAfter == 100 && (50 <= before.y() && before.y() <= 99);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // topmost c goes to left-most b
                // y 50 -> x 100
                // y 99 -> x 149
                return new TwentyTwo.Position(before.y() + 50, 49, 0, -1);
            }
        }));

        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // C -> D
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.x() == 50 && xAfter == 49 && (50 <= before.y() && before.y() <= 99);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // topmost c goes to left-most d
                // y 50 -> x 0
                // y 99 -> x 49
                return new TwentyTwo.Position(before.y() - 50, 100, 0, 1);
            }
        }));

        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // D -> C
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.y() == 100 && yAfter == 99 && (0 <= before.x() && before.x() <= 49);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // leftmost d goes to top-most d
                // x 0 -> y 50
                // x 49 -> y 99
                return new TwentyTwo.Position(50, before.x() + 50, 1, 0);
            }
        }));

        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // E -> F
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.y() == 149 && yAfter == 150 && (50 <= before.x() && before.x() <= 99);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // leftmost e goes to top-most f
                // x 50 -> y 150
                // x 99 -> y 199
                return new TwentyTwo.Position(49, before.x() + 100, -1, 0);
            }
        }));

        edges.add(new TwentyTwo.Edge(new TwentyTwo.Judge() { // F -> E
            @Override
            public boolean applies(TwentyTwo.Position before, int xAfter, int yAfter) {
                return before.x() == 49 && xAfter == 50 && (150 <= before.y() && before.y() <= 199);
            }

            @Override
            public TwentyTwo.Position apply(TwentyTwo.Position before, int newX, int newY) {
                // topmost f goes to left-most e
                // y 150 -> x 50
                // y 199 -> x 99
                return new TwentyTwo.Position(before.y() - 100, 149, 0, -1);
            }
        }));

        elfMap.acceptEdges(edges);
    }
}