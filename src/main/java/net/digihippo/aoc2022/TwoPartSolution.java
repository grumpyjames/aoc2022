package net.digihippo.aoc2022;

public abstract class TwoPartSolution<S, R> implements SolutionTemplate.Solution<R> {
    private S state = null;
    private Phase phase = Phase.State;
    enum Phase
    {
        State,
        Instructions
    }

    @Override
    public R result() {
        return result(state);
    }

    @Override
    public void accept(String s) {
        if (s.isBlank())
        {
            phase = Phase.Instructions;
            return;
        }

        switch (phase)
        {
            case State -> {
                if (state == null)
                {
                    state = initialiseState(s);
                }
                addToState(state, s);
            }
            case Instructions -> actOnState(state, s);
        }
    }

    protected abstract void actOnState(S state, String line);

    protected abstract void addToState(S state, String line);

    protected abstract S initialiseState(String line);

    protected abstract R result(S state);
}
