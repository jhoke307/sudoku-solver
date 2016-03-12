package com.perivi.sudoku.java;

import java.util.Set;

import com.google.common.collect.Sets;
import com.perivi.sudoku.java.Grid.CellState;
import com.perivi.sudoku.java.Grid.House;

/**
 * If between the row, column, and house, a possibility is the only remaining
 * one, it must be that.
 * 
 * @author jdh
 *
 */
public class SinglePossibilityStrategy implements Strategy {

    @Override
    public Boolean apply(final Grid input) {
        for (int i = 0; i < input.getHeight(); ++i) {
            for (int j = 0; j < input.getWidth(); ++j) {
                if (input.cellAt(i, j).getValue() != null) {
                    continue;
                }

                final Set<Integer> rowValues = Sets.newHashSet(Grid.values(input.row(i)));
                final Set<Integer> colValues = Sets.newHashSet(Grid.values(input.column(j)));
                final House houseContaining = input.houseContaining(i, j);
                final Set<Integer> houseValues = Sets.newHashSet(Grid.values(houseContaining));

                final Set<Integer> remainingPossibilities = Sets.difference(Grid.ALL_POSSIBILITIES,
                        Sets.union(rowValues, Sets.union(colValues, houseValues)));

                if (remainingPossibilities.size() == 1) {
//                    System.out.println(String.format("cell(%d,%d): rowValues %s, colValues %s, %s houseValues %s: remainingPossibilities %s",
//                            i, j,
//                            rowValues,
//                            colValues,
//                            houseContaining,
//                            houseValues,
//                            remainingPossibilities));
                    input.cellAt(i, j).setValue(remainingPossibilities.iterator().next());
                    input.cellAt(i, j).setState(CellState.HINT);
                    return Boolean.TRUE;
                }
            }
        }

        return Boolean.FALSE;
    }

}
