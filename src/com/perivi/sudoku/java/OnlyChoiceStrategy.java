package com.perivi.sudoku.java;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.perivi.sudoku.java.Grid.CellState;

/**
 * If there is only one remaining possibility in a row or column, it must fill
 * the remaining cell.
 *
 * @author jdh
 *
 */
public class OnlyChoiceStrategy implements Strategy {
	@Override
	public Boolean apply(@Nullable final Grid grid) {
	    if (grid == null) {
	        return Boolean.FALSE;
	    }

	    for (final Iterable<Grid.Cell> section : grid.sections()) {
	        final List<Grid.Cell> emptyCells = new ArrayList<>();
	        for (final Grid.Cell cell : section) {
	            if (cell.getValue() == null) {
	                emptyCells.add(cell);
	            }
	        }

	        if (emptyCells.size() == 1) {
	            final Grid.Cell cell = emptyCells.get(0);
	            if (cell.getNotes().size() == 1) {
	                // It's legitimate for this if to fail if the
	                // BacktrackingStrategy is running. That's the whole point!
	                System.out.println("empty cell " + cell + " has only choice for " + cell.getNotes());
	                cell.setValue(cell.getNotes().iterator().next());
	                cell.setState(CellState.HINT);
	                return Boolean.TRUE;
	            }
	        }
	    }
		return Boolean.FALSE;
	}
}
