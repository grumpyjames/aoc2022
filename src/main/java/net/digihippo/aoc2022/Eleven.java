package net.digihippo.aoc2022;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Eleven extends SolutionTemplate<Long, Long> {
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    static final class Monkey
    {
        private final int index;
        private final Deque<BigInteger> items;
        private final Function<BigInteger, BigInteger> operation;
        private final Predicate<BigInteger> test;
        private final int onTrue;
        private final int onFalse;
        private final int worryDivisor;
        private final BigInteger worryDiv;

        private long inspectionCount = 0;

        Monkey(
                int index,
                Deque<BigInteger> items,
                Function<BigInteger, BigInteger> operation,
                Predicate<BigInteger> test,
                int onTrue,
                int onFalse,
                int worryDivisor) {
            this.index = index;
            this.items = items;
            this.operation = operation;
            this.test = test;
            this.onTrue = onTrue;
            this.onFalse = onFalse;
            this.worryDivisor = worryDivisor;
            worryDiv = BigInteger.valueOf(worryDivisor);
        }

        void takeTurn(List<Monkey> monkeys)
        {
            while (!items.isEmpty())
            {
                inspectionCount++;
                BigInteger worryLevel = items.pop();
                BigInteger newWorryLevel = operation.apply(worryLevel).divide(worryDiv);
                if (test.test(newWorryLevel)) {
                    monkeys.get(onTrue).onItem(newWorryLevel);
                } else {
                    monkeys.get(onFalse).onItem(newWorryLevel);
                }
            }
        }

        private void onItem(BigInteger newWorryLevel) {
            items.addLast(newWorryLevel);
        }
    }

    enum State {
        Index,
        Starting,
        Operation,
        Test,
        True,
        Skip, False
    }
    @Override
    Solution<Long> partOne() {
        int rounds = 20;
        int worryDivisor = 3;
        return new MonkeyBusiness(rounds, worryDivisor);
    }

    @Override
    Solution<Long> partTwo() {
        return new MonkeyBusiness(10000, 1);
    }

    private Function<BigInteger, BigInteger> toOperation(Matcher matcher) {
        if (matcher.find())
        {
            final String operand = matcher.group(2);
            final String argTwo = matcher.group(3);

            if (argTwo.equals("old"))
            {
                if (operand.equals("+")) {
                    return old -> old.add(old);
                }
                else if (operand.equals("*")) {
                    return old -> old.multiply(old);
                }
            }
            else
            {
                BigInteger argTwoBigInt = BigInteger.valueOf(Long.parseLong(argTwo));
                if (operand.equals("+")) {
                    return old -> old.add(argTwoBigInt);
                }
                else if (operand.equals("*")) {
                    return old -> old.multiply(argTwoBigInt);
                }
            }

        }
        throw new IllegalStateException("Not found");
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class MonkeyBuilder {
        private int index;
        private List<Integer> items;
        private Function<BigInteger, BigInteger> operation;
        private Predicate<BigInteger> test;
        private int onTrue;
        private int onFalse;
        private int worryDivisor;

        public MonkeyBuilder setIndex(int index) {
            this.index = index;
            return this;
        }

        public MonkeyBuilder setItems(List<Integer> items) {
            this.items = items;
            return this;
        }

        public MonkeyBuilder setOperation(Function<BigInteger, BigInteger> operation) {
            this.operation = operation;
            return this;
        }

        public MonkeyBuilder setTest(Predicate<BigInteger> test) {
            this.test = test;
            return this;
        }

        public MonkeyBuilder setOnTrue(int onTrue) {
            this.onTrue = onTrue;
            return this;
        }

        public MonkeyBuilder setOnFalse(int onFalse) {
            this.onFalse = onFalse;
            return this;
        }

        public Monkey createMonkey() {
            final Deque<BigInteger> itemDeque = new ArrayDeque<>();
            items.forEach(intItem -> itemDeque.addLast(BigInteger.valueOf((long) intItem)));

            return new Monkey(index, itemDeque, operation, test, onTrue, onFalse, worryDivisor);
        }

        public void setWorryDivisor(int worryDivisor) {
            this.worryDivisor = worryDivisor;
        }
    }

    private class MonkeyBusiness implements Solution<Long> {
        private final int rounds;
        private final int worryDivisor;

        public MonkeyBusiness(int rounds, int worryDivisor) {
            this.rounds = rounds;
            this.worryDivisor = worryDivisor;
            state = State.Index;
            mb = new MonkeyBuilder();
            count = 0;
            op = Pattern.compile("(old|[0-9]+) (\\*|\\+) (old|[0-9]+)");
            monkeys = new ArrayList<>();
        }

        @Override
        public Long result() {
            for (int i = 0; i < rounds; i++) {
                for (Monkey monkey : monkeys) {
                    monkey.takeTurn(monkeys);
                    if (i == 999) {
                        System.out.println("Monkey " + monkey.index + " inspected items " + monkey.inspectionCount + " times");
                    }
                }
            }

            monkeys.sort((o1, o2) -> (int) (o2.inspectionCount - o1.inspectionCount));

            return monkeys.get(0).inspectionCount * monkeys.get(1).inspectionCount;
        }

        State state;
        MonkeyBuilder mb;
        int count;
        Pattern op;

        private final List<Monkey> monkeys;

        @SuppressWarnings("RegExpRepeatedSpace")
        @Override
        public void accept(String s) {
            switch (state) {
                case Index -> {
                    mb.setIndex(count++);
                    mb.setWorryDivisor(worryDivisor);
                    state = State.Starting;
                }
                case Starting -> {
                    String[] split = s.replaceAll("  Starting items: ", "").split(", ");
                    mb.setItems(Arrays.stream(split).map(Integer::parseInt).collect(Collectors.toList()));
                    state = State.Operation;
                }
                case Operation -> {
                    String opStr = s.replaceAll("  Operation: new = ", "");
                    Matcher matcher = op.matcher(opStr);
                    mb.setOperation(toOperation(matcher));
                    state = State.Test;
                }
                case Test -> {
                    String divStr = s.replaceAll("  Test: divisible by ", "");
                    BigInteger divisor = BigInteger.valueOf(Long.parseLong(divStr));
                    mb.setTest(worry -> worry.mod(divisor).equals(BigInteger.ZERO));
                    state = State.True;
                }
                case True -> {
                    final String to = s.replaceAll("    If true: throw to monkey ", "");
                    mb.setOnTrue(Integer.parseInt(to));
                    state = State.False;
                }
                case False -> {
                    final String to = s.replaceAll("    If false: throw to monkey ", "");
                    mb.setOnFalse(Integer.parseInt(to));
                    monkeys.add(mb.createMonkey());
                    mb = new MonkeyBuilder();
                    state = State.Skip;
                }
                case Skip -> state = State.Index;
            }
        }
    }
}
