package net.digihippo.aoc2022;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Two {
    private static final Map<String, Integer> SCORES = new HashMap<>();
    static
    {
        SCORES.put("A X", 4);
        SCORES.put("A Y", 8);
        SCORES.put("A Z", 3);
        SCORES.put("B X", 1);
        SCORES.put("B Y", 5);
        SCORES.put("B Z", 9);
        SCORES.put("C X", 7);
        SCORES.put("C Y", 2);
        SCORES.put("C Z", 6);
    }

    private static final Map<String, Integer> OTHER_SCORES = new HashMap<>();
    static
    {
        OTHER_SCORES.put("A X", 3); // lose to rock, play scissors: 0 + 3
        OTHER_SCORES.put("A Y", 4); // draw to rock, play rock: 3 + 1
        OTHER_SCORES.put("A Z", 8); // beat rock, play paper: 6 + 2
        OTHER_SCORES.put("B X", 1); // lose to paper, play rock: 0 + 1
        OTHER_SCORES.put("B Y", 5); // draw with paper, play paper: 3 + 2
        OTHER_SCORES.put("B Z", 9); // beat paper, play scissors: 3 + 6
        OTHER_SCORES.put("C X", 2); // lose to scissors, play paper: 0 + 2
        OTHER_SCORES.put("C Y", 6); // draw to scissors, play scissors: 3 + 3
        OTHER_SCORES.put("C Z", 7); // win against scissors, play rock: 6 + 1
    }

    public static int score(String exampleInput) {
        return Arrays.stream(exampleInput.split("\n")).mapToInt(Two::scoreOne).sum();
    }

    public static int scoreDifferent(String exampleInput) {
        return Arrays.stream(exampleInput.split("\n")).mapToInt(Two::scoreOneDifferent).sum();
    }

    private static int scoreOneDifferent(String strategy) {
        return OTHER_SCORES.get(strategy.strip());
    }

    private static int scoreOne(String strategy) {
        return SCORES.get(strategy.strip());
    }

    public static int scoreStream(InputStream puzzleInput) throws IOException {
        Scorer scorer = new Scorer(Two::scoreOne);
        Lines.processLines(puzzleInput, scorer);
        return scorer.result();
    }

    public static int scoreDifferentStream(InputStream puzzleInput) throws IOException {
        Scorer scorer = new Scorer(Two::scoreOneDifferent);
        Lines.processLines(puzzleInput, scorer);
        return scorer.result();
    }

    private static class Scorer implements Consumer<String> {
        int score = 0;
        private final Function<String, Integer> scoreFn;

        public Scorer(Function<String, Integer> scoreFn) {

            this.scoreFn = scoreFn;
        }

        public int result() {
            return score;
        }

        @Override
        public void accept(String s) {
             score += scoreFn.apply(s);
        }
    }
}
