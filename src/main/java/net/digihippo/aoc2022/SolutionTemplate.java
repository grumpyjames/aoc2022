package net.digihippo.aoc2022;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public abstract class SolutionTemplate<T1, T2> {
    interface Solution<X> extends Consumer<String>
    {
        X result();
    }

    abstract Solution<T1> partOne();

    abstract Solution<T2> partTwo();

    T1 examplePartOne(String input) {
        return example(input, partOne());
    }

    T1 puzzlePartOne(InputStream puzzleInput)
    {
        return puzzle(puzzleInput, partOne());
    }

    T2 examplePartTwo(String example)
    {
        return example(example, partTwo());
    }

    T2 puzzlePartTwo(InputStream puzzleInput)
    {
        return puzzle(puzzleInput, partTwo());
    }

    private static <X> X example(String input, Solution<X> s) {
        String[] lines = input.split("\n");
        for (String line : lines) {
            s.accept(line);
        }
        return s.result();
    }

    private static <X> X puzzle(InputStream puzzleInput, Solution<X> s) {
        try {
            Lines.processLines(puzzleInput, s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return s.result();
    }
}
