package com.perivi.sudoku.java;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class Solver {
    private final static List<Strategy> smartStrategies = new ArrayList<>();
    static {
        smartStrategies.add(new OnlyChoiceStrategy());
        smartStrategies.add(new SinglePossibilityStrategy());
        smartStrategies.add(new OnlySquareStrategy());
        smartStrategies.add(new NakedMultiplesStrategy());
    }
    private final static List<Strategy> allStrategies = new ArrayList<>(smartStrategies);
    static {
        allStrategies.add(new BacktrackingStrategy());
    }

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

    public Checker.State solve(final Grid input) {
        final Multiset<Strategy> strategyCounters = HashMultiset.create();
        Grid lastGrid, currentGrid;
        currentGrid = input;
        do {
            lastGrid = currentGrid;
            currentGrid = new Grid(lastGrid);

            // checkAgainstSolution(currentGrid, solutionGrid);

            for (final Strategy strategy : strategies) {
                if (strategy.apply(currentGrid) == Boolean.TRUE) {
                    strategyCounters.add(strategy);
//                    System.out.println("after applying strategy " + strategy.getClass().getName() + ":");
//                    System.out.print(currentGrid.toString() + "\n");
//                    System.out.println("state: " + Checker.check(currentGrid));
                    break;
                }
            }
        } while (!lastGrid.equals(currentGrid) && Checker.check(currentGrid).equals(Checker.State.VALID));

        return Checker.check(currentGrid);
    }

}
