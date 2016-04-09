package com.perivi.sudoku.java;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

public class PossibleCellPredicate implements Predicate<Grid.Cell> {
    private final int possibility;

    PossibleCellPredicate(int p) {
        this.possibility = p;
    }

    @Override
    public boolean apply(@Nullable final Grid.Cell input) {
        if (input == null) {
            return false;
        }

        return input.containsNote(possibility);
    }
}
