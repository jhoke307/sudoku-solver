package com.perivi.sudoku.java.gui;

import com.perivi.sudoku.java.Grid;
import com.perivi.sudoku.java.Grid.Cell;

class CellController implements Grid.CellListener {
	SudokuCell widget;

	CellController(SudokuCell w) {
		widget = w;
	}

	@Override
	public void cellUpdated(Cell c) {
		widget.setMainNumber(c.getValue());
		widget.setMainState(c.getState());
		widget.setNoteNumbers(c.getNotes());
		widget.setHighlight(true);

	}
}