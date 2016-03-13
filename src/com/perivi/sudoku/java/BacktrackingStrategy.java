package com.perivi.sudoku.java;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.perivi.sudoku.java.Grid.CellState;
import com.perivi.sudoku.java.Grid.House;

/**
 * The worst strategy. Try possibilities until we find a contradiction.
 *
 * @author jdh
 *
 */
public class BacktrackingStrategy implements Strategy {

    @Override
    public Boolean apply(final Grid input) {
        // This is rather slow. We should be able to do it for all cells.
        for (int i = 0; i < input.getHeight(); ++i) {
            for (int j = 0; j < input.getWidth(); ++j) {
                // TODO Look for a cell with only a few possibilities.
                final Grid.Cell cell = input.cellAt(i, j);
                if (cell.getValue() != null) {
                    continue;
                }

                final Set<Integer> rowValues = Sets.newHashSet(Grid.values(input.row(i)));
                final Set<Integer> colValues = Sets.newHashSet(Grid.values(input.column(j)));
                final House houseContaining = input.houseContaining(i, j);
                final Set<Integer> houseValues = Sets.newHashSet(Grid.values(houseContaining));

                final Set<Integer> remainingPossibilities = new HashSet<>(Sets.difference(Grid.ALL_POSSIBILITIES,
                        Sets.union(rowValues, Sets.union(colValues, houseValues))));
                final Multimap<Checker.State, Integer> stateMap = HashMultimap.create();

                System.out.println(String.format("Trying backtracking on cell %s - possibilities %s",
                        cell, remainingPossibilities));
                for (final Integer p : remainingPossibilities) {
                    final Grid copy = new Grid(input);
                    final Solver solver = Solver.smartSolver();
                    copy.cellAt(cell.getRow(), cell.getCol()).setValue(p);
                    stateMap.put(solver.solve(copy), p);
                }

                final Set<Integer> solutionSet = new HashSet<>(stateMap.get(Checker.State.SOLVED));
                if (solutionSet.size() == 1) {
                    System.out.println("Backtrack: Settled on value for " + cell + ": " + solutionSet);
                    cell.setValue(solutionSet.iterator().next());
                    cell.setState(CellState.HINT);
                    return Boolean.TRUE;
                }

                final Set<Integer> validSet = new HashSet<>(stateMap.get(Checker.State.VALID));
                if (validSet.size() == 1) {
                    System.out.println("Backtrack: Settled on value for " + cell + ": " + validSet);
                    cell.setValue(validSet.iterator().next());
                    cell.setState(CellState.HINT);
                    return Boolean.TRUE;
                }
            }
        }

        return Boolean.FALSE;
    }

}
