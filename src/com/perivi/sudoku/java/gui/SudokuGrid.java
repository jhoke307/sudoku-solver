package com.perivi.sudoku.java.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.perivi.sudoku.java.Checker;
import com.perivi.sudoku.java.Grid;

public class SudokuGrid extends Composite {
    private final Grid inputGrid;
	private final List<SudokuHouse> houses = new ArrayList<>();

	SudokuGrid(final Grid inputGrid, final Composite parent, final int bits) {
		super(parent, bits);

		this.inputGrid = inputGrid;

		final GridLayout layout = new GridLayout(3, true);
		setLayout(layout);

		setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));

		for (final Grid.House house : inputGrid.houses()) {
			houses.add(new SudokuHouse(house, this, SWT.NONE));
		}
	}

	public void clearHighlight() {
		for (final SudokuHouse house : houses) {
			house.clearHighlight();
		}
	}

	public void check() {
	    Checker.check(inputGrid);
	}
}
