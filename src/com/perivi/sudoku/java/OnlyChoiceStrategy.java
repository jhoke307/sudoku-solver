package com.perivi.sudoku.java;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * If there is only one remaining possibility in a row or column, it must fill
 * the remaining cell.
 * @author jdh
 *
 */
public class OnlyChoiceStrategy implements Strategy {
    @Override
    public Boolean apply(final Grid grid) {
        for (final Iterable<Grid.Cell> section : grid.sections()) {
            final List<Integer> values = Grid.values(section);
            final Set<Integer> valueSet = Sets.newHashSet(values);
            final Set<Integer> remainingPossibilities =
                    Sets.difference(Grid.ALL_POSSIBILITIES, valueSet);
            if (remainingPossibilities.size() == 1) {
                final Integer lastPossibility = remainingPossibilities.iterator().next();
                for (final Grid.Cell c : section) {
                    if (c.getValue() == null) {
                        System.out.println(String.format("Section %s seems like only possibility for this cell is: %s",
                                section, lastPossibility));
                        c.setValue(lastPossibility);
                        return Boolean.TRUE;
                    }
                }
            }
        }

        return Boolean.FALSE;
    }
}
