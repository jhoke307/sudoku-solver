package com.perivi.sudoku.java;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class Checker {
    public enum State {
        INVALID, VALID, SOLVED;
    }

    /**
     * A solved Sudoku must have each of the possible numbers occur exactly once
     * in each row, column, and house.
     *
     * @param g
     * @return
     */
    public static State check(final Grid g) {
        boolean solvedSoFar = true;

        for (final Iterable<Grid.Cell> section : g.sections()) {
            final State result = checkOneSection(section);
            if (result.equals(State.INVALID)) {
                System.out.println(String.format("section %s seems to be invalid: %s",
                        section, Grid.values(section)));
                return State.INVALID;
            }

            if (result.equals(State.VALID)) {
                solvedSoFar = false;
            }
        }

        if (solvedSoFar) {
            return State.SOLVED;
        } else {
            return State.VALID;
        }

    }

    private static State checkOneSection(final Iterable<Grid.Cell> section) {
        final List<Integer> values = values(section);
        final Multiset<Integer> valueSet = HashMultiset.create(values);

        if (valueSet.elementSet().equals(Grid.ALL_POSSIBILITIES)) {
            return State.SOLVED;
        }

        for (final Integer i : Grid.ALL_POSSIBILITIES) {
            if (valueSet.count(i) > 1) {
                return State.INVALID;
            }
        }

        return State.VALID;
    }

    private static List<Integer> values(final Iterable<Grid.Cell> section) {
        final List<Integer> l = new ArrayList<>();

        for (final Grid.Cell c : section) {
            l.add(c.getValue());
        }

        return l;
    }
}
