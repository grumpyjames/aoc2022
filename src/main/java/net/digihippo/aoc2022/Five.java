package net.digihippo.aoc2022;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Five extends SolutionTemplate<String, String> {
    private final Pattern stackPattern = Pattern.compile("\\[([A-Z])\\]");
    private final Pattern movePattern = Pattern.compile("move ([0-9]+) from ([0-9]+) to ([0-9]+)");

    @Override
    Solution<String> partOne() {
        return new Stacker(this::actorOne);
    }

    @Override
    Solution<String> partTwo() {
        return new Stacker(this::actorTwo);
    }

    public void actorOne(Deque<Character>[] state, String line)
    {
        final Matcher matcher = movePattern.matcher(line);
        if (matcher.find())
        {
            final int count = Integer.parseInt(matcher.group(1));
            final int from = Integer.parseInt(matcher.group(2)) - 1;
            final int to = Integer.parseInt(matcher.group(3)) - 1;
            final Deque<Character> fromDeque = state[from];
            final Deque<Character> toDeque = state[to];
            for (int i = 0; i < count; i++) {
                toDeque.addFirst(fromDeque.remove());
            }
        }
    }

    public void actorTwo(Deque<Character>[] state, String line)
    {
        Matcher matcher = movePattern.matcher(line);
        if (matcher.find())
        {
            final int count = Integer.parseInt(matcher.group(1));
            final int from = Integer.parseInt(matcher.group(2)) - 1;
            final int to = Integer.parseInt(matcher.group(3)) - 1;
            final Deque<Character> fromDeque = state[from];
            final Deque<Character> toDeque = state[to];
            final List<Character> reverse = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                reverse.add(fromDeque.remove());
            }
            Collections.reverse(reverse);
            for (Character character : reverse) {
                toDeque.addFirst(character);
            }
        }
    }

    private class Stacker extends TwoPartSolution<Deque<Character>[], String> {
        private final BiConsumer<Deque<Character>[], String> actor;

        private Stacker(BiConsumer<Deque<Character>[], String> actor) {
            this.actor = actor;
        }

        @Override
        protected void actOnState(Deque<Character>[] state, String line) {
            actor.accept(state, line);
        }

        @Override
        protected void addToState(Deque<Character>[] state, String line) {
            for (int i = 0; i < state.length; i++) {
                String letter = line.substring(i * 4, (i + 1) * 4 - 1);
                Matcher matcher = stackPattern.matcher(letter);
                if (matcher.find())
                {
                    state[i].addLast(matcher.group(1).charAt(0));
                }
            }
        }

        @Override
        protected Deque<Character>[] initialiseState(String line) {
            final int stackCount = line.length() / 4 + 1;
            //noinspection unchecked
            Deque<Character>[] stacks = new Deque[stackCount];
            for (int i = 0; i < stacks.length; i++) {
                stacks[i] = new ArrayDeque<>();
            }
            return stacks;
        }

        @Override
        protected String result(Deque<Character>[] state) {
            final char[] result = new char[state.length];
            for (int i = 0; i < state.length; i++) {
                Deque<Character> stack = state[i];
                result[i] = stack.peekFirst();
            }
            return new String(result);
        }
    }
}
