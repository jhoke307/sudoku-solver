package com.perivi.sudoku.java;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import lombok.Getter;

public class Solver {
    private final static List<Strategy> smartStrategies = new ArrayList<>();
    static {
        smartStrategies.add(new OnlyChoiceStrategy());
        smartStrategies.add(new SinglePossibilityStrategy());
        smartStrategies.add(new OnlySquareStrategy());
        smartStrategies.add(new SubgroupExclusionStrategy());
        smartStrategies.add(new NakedMultiplesStrategy());
        smartStrategies.add(new XWingStrategy());
    }
    private final static List<Strategy> allStrategies = new ArrayList<>(smartStrategies);
    static {
        allStrategies.add(new BacktrackingStrategy());
    }

    @Getter
    private final Multiset<Strategy> strategyCounters = HashMultiset.create();
    private final List<Strategy> strategies;

    public Solver(List<Strategy> strategies) {
        this.strategies = strategies;
    }

    public static Solver smartSolver() {
        return new Solver(smartStrategies);
    }

    public static Solver dumbSolver() {
        return new Solver(allStrategies);
    }

    public boolean solveStep(final Grid input) {
    	for (final Strategy strategy : strategies) {
    		if (strategy.apply(input) == Boolean.TRUE) {
    			strategyCounters.add(strategy);
    			System.out.println(strategy.getClass().getSimpleName());
    			return true;
    		}
    	}

    	System.out.println("Couldn't make any progress with any strategy. Halting.");
    	return false;
    }

    public Checker.State solve(final Grid input) {
    	Checker.State lastState = null;

    	while (solveStep(input) &&
    			(lastState = Checker.check(input)).equals(Checker.State.VALID)) {
    		/* loop */
    	}

        return lastState;
    }
}
