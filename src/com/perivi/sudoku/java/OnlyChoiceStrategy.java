package com.perivi.sudoku.java;

import java.util.ArrayList;
import java.util.List;

import com.perivi.sudoku.java.Grid.CellState;

/**
 * If there is only one remaining possibility in a row or column, it must fill
 * the remaining cell.
 *
 * This actually does nothing since the SinglePossibilityStrategy supersedes it.
 *
 * @author jdh
 *
 */
public class OnlyChoiceStrategy implements Strategy {
	@Override
	public Boolean apply(final Grid grid) {
	    for (final Iterable<Grid.Cell> section : grid.sections()) {
	        final List<Grid.Cell> emptyCells = new ArrayList<>();
	        for (final Grid.Cell cell : section) {
	            if (cell.getValue() == null) {
	                emptyCells.add(cell);
	            }
	        }

	        if (emptyCells.size() == 1) {
	            final Grid.Cell cell = emptyCells.get(0);
	            System.out.println("empty cell " + cell + " has only choice for " + cell.getNotes());
	            cell.setValue(cell.getNotes().iterator().next());
	            cell.setState(CellState.HINT);
	            return Boolean.TRUE;
	        }
	    }
		return Boolean.FALSE;
	}
}
