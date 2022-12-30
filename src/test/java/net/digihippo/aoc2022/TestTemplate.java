package net.digihippo.aoc2022;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TestTemplate<T1, T2> {
    protected final SolutionTemplate<T1, T2> solution;
    protected final String exampleInput;
    private final T1 exampleOneAnswer;
    protected final T2 exampleTwoAnswer;
    private final String puzzleFileName;

    protected TestTemplate(
            SolutionTemplate<T1, T2> solution,
            String exampleInput,
            T1 exampleOneAnswer,
            T2 exampleTwoAnswer,
            String puzzleFileName) {
        this.solution = solution;
        this.exampleInput = exampleInput;
        this.exampleOneAnswer = exampleOneAnswer;
        this.exampleTwoAnswer = exampleTwoAnswer;
        this.puzzleFileName = puzzleFileName;
    }

    @Test
    void exampleOne()
    {
        assertEquals(exampleOneAnswer, solution.examplePartOne(exampleInput));
    }

    @Test
    void realOne() {
        T1 result = solution.puzzlePartOne(Inputs.puzzleInput(puzzleFileName));
        System.out.println(result);
    }

    @Test
    void exampleTwo() {
        SolutionTemplate.Solution<T2> sol = solution.partTwo();
        visitExampleTwo(sol);
        assertEquals(exampleTwoAnswer, solution.examplePartTwo(exampleInput, sol));
    }

    @Test
    void realTwo() {
        SolutionTemplate.Solution<T2> sol = solution.partTwo();
        visitRealTwo(sol);
        System.out.println(solution.puzzlePartTwo(Inputs.puzzleInput(puzzleFileName), sol));
    }

    void visitExampleTwo(SolutionTemplate.Solution<T2> solution) {}

    void visitRealTwo(SolutionTemplate.Solution<T2> solution) {}
}
