package com.perivi.sudoku.java;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class SudokuSolver {
    private final static List<Strategy> strategies = new ArrayList<>();
    static {
        strategies.add(new OnlyChoiceStrategy());
        strategies.add(new SinglePossibilityStrategy());
        strategies.add(new OnlySquareStrategy());
        strategies.add(new SubgroupExclusionStrategy());
        strategies.add(new NakedMultiplesStrategy());
        strategies.add(new XWingStrategy());
        strategies.add(new BacktrackingStrategy());
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1 && args.length != 2) {
            System.out.println("Usage: SudokuSolver <input> [solution]");
            System.exit(1);
        }

        final Grid originalGrid = loadGrid(args[0]);
        final Grid solutionGrid = (args.length == 2) ? loadGrid(args[1]) : null;
        System.out.println("loaded grid:");
        System.out.print(originalGrid.toString());
        System.out.println("state: " + Checker.check(originalGrid));
        if (solutionGrid != null) {
            System.out.println("solution state: " + Checker.check(solutionGrid));
        }
        System.out.println();

        // Try to solve it
        final Multiset<Strategy> strategyCounters = HashMultiset.create();
        Grid lastGrid, currentGrid;
        currentGrid = originalGrid;
        do {
            lastGrid = currentGrid;
            currentGrid = new Grid(lastGrid);

            if (solutionGrid != null) {
                checkAgainstSolution(currentGrid, solutionGrid);
            }

            for (final Strategy strategy : strategies) {
                if (strategy.apply(currentGrid) == Boolean.TRUE) {
                    strategyCounters.add(strategy);
                    System.out.println("after applying strategy " + strategy.getClass().getName() + ":");
                    System.out.print(currentGrid.toString() + "\n");
                    System.out.println("state: " + Checker.check(currentGrid));
                    break;
                }
            }
        } while (!lastGrid.equals(currentGrid) && Checker.check(currentGrid).equals(Checker.State.VALID));

        if (lastGrid.equals(currentGrid) && Checker.check(currentGrid).equals(Checker.State.VALID)) {
            System.out.println("No progress on last iteration; giving up.");
        }

        System.out.println("stats: ");
        for (final Multiset.Entry<Strategy> e : strategyCounters.entrySet()) {
            System.out.println(String.format("% 3d %s", e.getCount(), e.getElement().getClass().getSimpleName()));
        }
    }

    private static Grid loadGrid(final String filename) throws IOException {
        final byte[] encoded = Files.readAllBytes(Paths.get(filename));
        final String puzzle =  new String(encoded, Charset.defaultCharset());
        return Grid.fromString(puzzle);
    }

    private static void checkAgainstSolution(final Grid current, final Grid solution) {
        for (int i = 0; i < current.getHeight(); ++i) {
            for (int j = 0; j < current.getWidth(); ++j) {
                final Integer v = current.cellAt(i, j).getValue();
                final Integer x = solution.cellAt(i, j).getValue();
                if (v != null && x != null && !v.equals(x)) {
                    System.out.println(String.format("current and solution disagree on %s: current says %s, solution %s",
                            current.cellAt(i, j), v, x));
                    throw new IllegalStateException("Puzzle in progress and solution disagree");
                }
                else if (v == null && !current.cellAt(i, j).getNotes().contains(x)) {
                	System.out.println(String.format("current NOTES and solution disagree on %s: current says %d is not a possibility.",
                			current.cellAt(i, j), x));
                    throw new IllegalStateException("Puzzle in progress and solution disagree");
                }
            }
        }
    }

}
