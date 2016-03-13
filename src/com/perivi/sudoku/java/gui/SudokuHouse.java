package com.perivi.sudoku.java.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.perivi.sudoku.java.Grid;

public class SudokuHouse extends Composite {
	private final List<SudokuCell> cells = new ArrayList<>();

	public SudokuHouse(final Grid.House house, final SudokuGrid parent, final int bits) {
		super(parent, bits);

		final GridLayout layout = new GridLayout(3, true);
		setLayout(layout);

		final Color darkGray = new Color(null, 128, 128, 128);
		setBackground(darkGray);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				darkGray.dispose();
			}
		});

		for (final Grid.Cell cell : house) {
			final SudokuCell w = new SudokuCell(this, SWT.NONE);
			cell.addListener(new CellController(parent, w, cell));
			cells.add(w);
		}
	}

	public void clearHighlight() {
		for (final SudokuCell c : cells) {
			c.setHighlight(false);
		}
	}

}
