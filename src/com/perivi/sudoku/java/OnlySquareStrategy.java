package com.perivi.sudoku.java;

import java.util.Collection;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.perivi.sudoku.java.Grid.Cell;
import com.perivi.sudoku.java.Grid.CellState;

/**
 * There may be only one square in a section that can take a certain number.
 * (There may be other numbers that are possible for it, but if it's the only
 * one that can have a number the others are automatically ruled out.)
 *
 * @author jdh
 *
 */
public class OnlySquareStrategy implements Strategy {

    @Override
    public Boolean apply(@Nullable final Grid input) {
        if (input == null) {
            return Boolean.FALSE;
        }

        // For each row, column, house:
        for (final Iterable<Grid.Cell> section : input.sections()) {
            final Multimap<Integer, Grid.Cell> possibilityMap = HashMultimap.create();
            for (final Grid.Cell cell : section) {
                for (final Integer p : cell.getNotes()) {
                    possibilityMap.put(p, cell);
                }
            }

            for (final Entry<Integer, Collection<Cell>> entry : possibilityMap.asMap().entrySet()) {
                if (entry.getValue().size() == 1) {
                    final Integer p = entry.getKey();
                    final Grid.Cell cell = entry.getValue().iterator().next();

                    System.out.println(String.format("cell(%s) is the only one in %s that can be %s",
                            cell, section, p));
                    cell.setValue(p);
                    cell.setState(CellState.HINT);
                    return Boolean.TRUE;
                }
            }
        }

        return Boolean.FALSE;
    }

}
