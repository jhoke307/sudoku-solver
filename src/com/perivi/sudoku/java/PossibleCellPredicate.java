package com.perivi.sudoku.java;

import com.google.common.base.Predicate;

public class PossibleCellPredicate implements Predicate<Grid.Cell> {
    private final int possibility;

    PossibleCellPredicate(int p) {
        this.possibility = p;
    }

    @Override
    public boolean apply(final Grid.Cell input) {
        return input.containsNote(possibility);
    }
}
