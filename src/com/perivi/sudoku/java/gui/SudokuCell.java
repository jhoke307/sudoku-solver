package com.perivi.sudoku.java.gui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.perivi.sudoku.java.Grid;

class SudokuCell extends Canvas {
	final private Color backgroundColor;
	final private Color highlightColor;
	final private Color inputColor;
	final private Color pencilColor;
	final private Color hintColor;
	final private Color noteColor;
	final private Color invalidColor;

	final private Font mainFont;
	final private Font noteFont;

	final private Point[] notePoints;

	private boolean highlight;
	private Integer mainNumber;
	private Grid.CellState mainState;
	private Set<Integer> noteNumbers = new HashSet<>();

	SudokuCell(Composite parent, int style) {
		super(parent, style);

		backgroundColor = new Color(null, 255, 255, 255);
		highlightColor = new Color(null, 249, 255, 82);
		inputColor = new Color(null, 0, 0, 0);
		pencilColor = new Color(null, 80, 80, 80);
		hintColor = new Color(null, 0, 155, 157);
		noteColor = new Color(null, 80, 90, 255);
		invalidColor = new Color(null, 255, 80, 80);

		setBackground(backgroundColor);

		final Font systemFont = getDisplay().getSystemFont();
		final FontData[] fd = systemFont.getFontData();
		fd[0].setHeight(48);
		mainFont = new Font(getDisplay(), fd);
		fd[0].setHeight(16);
		noteFont = new Font(getDisplay(), fd);

		final GC gc = new GC(this);
		gc.setFont(noteFont);
		notePoints = new Point[10];
		notePoints[1] = gc.stringExtent(""); notePoints[1].y *= 0;
		notePoints[2] = gc.stringExtent("1 "); notePoints[2].y *= 0;
		notePoints[3] = gc.stringExtent("1 2 "); notePoints[3].y *= 0;
		notePoints[4] = gc.stringExtent("");
		notePoints[5] = gc.stringExtent("4 ");
		notePoints[6] = gc.stringExtent("4 5 ");
		notePoints[7] = gc.stringExtent("");  notePoints[7].y *= 2;
		notePoints[8] = gc.stringExtent("7 "); notePoints[8].y *= 2;
		notePoints[9] = gc.stringExtent("7 8 "); notePoints[9].y *= 2;
		gc.dispose();

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				SudokuCell.this.widgetDisposed(arg0);
			}
		});

		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent arg0) {
				SudokuCell.this.paintControl(arg0);
			}
		});
	}

	private void widgetDisposed(DisposeEvent arg0) {
		backgroundColor.dispose();
		highlightColor.dispose();
		inputColor.dispose();
		pencilColor.dispose();
		hintColor.dispose();
		noteColor.dispose();
		invalidColor.dispose();

		mainFont.dispose();
		noteFont.dispose();
	}

	private void paintControl(PaintEvent e) {
		final GC gc = e.gc;

		if (mainNumber != null) {
			gc.setFont(mainFont);
			if (mainState == null || mainState == Grid.CellState.VALID) {
				gc.setForeground(pencilColor);
			}
			else {
				switch (mainState) {
				case HINT: gc.setForeground(hintColor); break;
				case INPUT: gc.setForeground(inputColor); break;
				case INVALID: gc.setForeground(invalidColor); break;
				case VALID: default: gc.setForeground(pencilColor); break;
				}
			}

			final String string = Integer.toString(mainNumber);
			final Point textExtent = gc.stringExtent(string);
			final Point widgetExtent = getSize();
			gc.drawString(string, (widgetExtent.x / 2) - (textExtent.x / 2), 1);
		}
		else {
			gc.setFont(noteFont);
			gc.setForeground(noteColor);

			for (final Integer note : noteNumbers) {
				final int n = note.intValue();
				gc.drawString(note.toString(), 1 + notePoints[n].x, 1 + notePoints[n].y);
			}
		}
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		final GC gc = new GC(this);

		gc.setFont(noteFont);
		final Point noteExtent = gc.stringExtent("9 9 9");

		gc.setFont(mainFont);
		final Point mainExtent = gc.stringExtent("9");

		gc.dispose();

		int width = Math.max(noteExtent.x, mainExtent.x);
		int height = Math.max(noteExtent.y, mainExtent.y);

		if (wHint != SWT.DEFAULT)
			width = wHint;

		if (hHint != SWT.DEFAULT)
			height = hHint;

		return new Point(width + 2, height + 2);
	}

	public Integer getMainNumber() {
		return mainNumber;
	}

	public void setMainNumber(Integer mainNumber) {
		this.mainNumber = mainNumber;
		redraw();
	}

	public Grid.CellState getMainState() {
		return mainState;
	}

	public void setMainState(Grid.CellState mainState) {
		this.mainState = mainState;
		redraw();
	}

	public Set<Integer> getNoteNumbers() {
		return noteNumbers;
	}

	public void setNoteNumbers(Set<Integer> noteNumbers) {
		this.noteNumbers = noteNumbers;
		redraw();
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
		setBackground(highlight ? highlightColor : backgroundColor);
		redraw();
	}
}
