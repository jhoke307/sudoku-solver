package com.perivi.sudoku.java;

import com.perivi.sudoku.java.Grid.CellState;

/**
 * If between the row, column, and house, a possibility is the only remaining
 * one, it must be that.
 * 
 * @author jdh
 *
 */
public class SinglePossibilityStrategy implements Strategy {

    @Override
    public Boolean apply(final Grid grid) {
    	for (final Grid.Cell cell : grid.cells()) {
    		if (cell.getNotes().size() == 1 && cell.getValue() == null) {
    			cell.setValue(cell.getNotes().iterator().next());
    			cell.setState(CellState.HINT);
    			return Boolean.TRUE;
    		}
    	}

    	return Boolean.FALSE;
    }

}
