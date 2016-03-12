package com.perivi.sudoku.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.perivi.sudoku.java.Grid.House;

/**
 * There may be only one square in a section that can take a certain number.
 * (There may be other numbers that are possible for it, but if it's the only
 * one that can have a number the others are automatically ruled out.)
 *
 * This is an elimination rule.
 * 
 * @author jdh
 *
 */
public class OnlySquareStrategy implements Strategy {

    @Override
    public Boolean apply(final Grid input) {
        // For each row, column, house:
        for (final Iterable<Grid.Cell> section : input.sections()) {
            final List<Grid.Cell> cells = Lists.newArrayList(section);
            final List<Set<Integer>> possibilities = new ArrayList<>();
            for (final Grid.Cell cell : cells) {
                if (cell.getValue() != null) {
                    possibilities.add(Collections.<Integer> emptySet());
                } else {
                    final int i = cell.getRow();
                    final int j = cell.getCol();
                    final Set<Integer> rowValues = Sets.newHashSet(Grid.values(input.row(i)));
                    final Set<Integer> colValues = Sets.newHashSet(Grid.values(input.column(j)));
                    final House houseContaining = input.houseContaining(i, j);
                    final Set<Integer> houseValues = Sets.newHashSet(Grid.values(houseContaining));

                    final Set<Integer> remainingPossibilities = Sets.difference(Grid.ALL_POSSIBILITIES,
                            Sets.union(rowValues, Sets.union(colValues, houseValues)));
                    possibilities.add(remainingPossibilities);
                }
            }

            final Multiset<Integer> allPossibilities = HashMultiset.create(Grid.ALL_POSSIBILITIES.size() + 1);
            for (final Set<Integer> p : possibilities) {
                allPossibilities.addAll(p);
            }

            for (final Integer p : Grid.ALL_POSSIBILITIES) {
                if (allPossibilities.count(p) == 1) {
                    // Find the cell it applied to
                    for (int j = 0; j < possibilities.size(); ++j) {
                        if (possibilities.get(j).contains(p) && cells.get(j).getValue() == null) {
//                            System.out.println(String.format("cell(%s) is the only one in %s that can be %s",
//                                    cells.get(j), section, p));
                            cells.get(j).setValue(p);
                            return Boolean.TRUE;
                        }
                    }

                }

            }
        }

        return Boolean.FALSE;
    }

}
