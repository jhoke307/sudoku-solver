package com.perivi.sudoku.java.gui;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Strings;
import com.google.common.collect.Multiset;
import com.perivi.sudoku.java.Grid;
import com.perivi.sudoku.java.Grid.Cell;
import com.perivi.sudoku.java.Solver;
import com.perivi.sudoku.java.Strategy;

public class AppShell {
	private static List<SudokuCell> allCells = new ArrayList<>();

	public static void main(final String[] args) throws IOException {
		final Display display = new Display();
		final Shell shell = new Shell(display);

//		Text helloWorldTest = new Text(shell, SWT.NONE);
//		helloWorldTest.setText("Hello World SWT");
//		helloWorldTest.pack();

		final Grid inputGrid = loadGrid("data/puzzle449.txt");
		final GridLayout layout = new GridLayout(9, true);
		shell.setLayout(layout);
		shell.setText("Sudoku Solver");
		
		System.out.print(inputGrid.toString());

		for (final Grid.Cell cell : inputGrid.cells()) {
			final SudokuCell w = new SudokuCell(shell, SWT.NONE);
			cell.addListener(new CellController(w));
			allCells.add(w);
		}

		final Solver solver = Solver.smartSolver();
		final Button stepButton = new Button(shell, SWT.NONE);
		stepButton.setLayoutData(new GridData(9));
		stepButton.setText("Step");
		stepButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				clearHighlight();
				solver.solveStep(inputGrid);
			}
		});

		final Button solveButton = new Button(shell, SWT.NONE);
		solveButton.setLayoutData(new GridData(9));
		solveButton.setText("Solve");
		solveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				final int interval = 100;

				final Runnable runnable = new Runnable() {
					@Override
					public void run() {

						clearHighlight();
						if (solver.solveStep(inputGrid)) {
							display.timerExec(interval, this);
						}
						else {
							System.out.println("stats: ");
							for (final Multiset.Entry<Strategy> e : solver.getStrategyCounters().entrySet()) {
								System.out.println(String.format("% 3d %s", e.getCount(), e.getElement().getClass().getSimpleName()));
							}
						}
					}
				};

				display.timerExec(interval, runnable);
			}
		});

		final Button loadButton = new Button(shell, SWT.NONE);
		loadButton.setText("Load");
		loadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				final FileDialog dlg = new FileDialog(shell, SWT.OPEN);
				dlg.setFilterExtensions(new String[] { "*.txt" });
				dlg.setText("Choose a file");
				final String filename = dlg.open();
				if (Strings.isNullOrEmpty(filename)) {
				}
				else if (!Files.exists(Paths.get(filename))) {
					final MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR);
					msg.setText("Unable to open file \"" + filename + "\"");
					msg.open();
				}
				else {
					try {
						final Grid newGrid = loadGrid(filename);
						for (final Grid.Cell srcCell : newGrid.cells()) {
							final Grid.Cell dstCell =
									inputGrid.cellAt(srcCell.getRow(), srcCell.getCol());
							dstCell.setValue(srcCell.getValue());
							dstCell.setState(srcCell.getState());
							dstCell.getNotes().clear();
							dstCell.getNotes().addAll(srcCell.getNotes());
						}

						solver.getStrategyCounters().clear();
					}
					catch (final IllegalArgumentException | IOException e) {
						final MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR);
						msg.setText("Error parsing file \"" + filename + "\": " + e.getMessage());
						msg.open();
					}
				}
			}
		});


		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void clearHighlight() {
		for (final SudokuCell c : allCells) {
			c.setHighlight(false);
		}
	}

    private static Grid loadGrid(final String filename) throws IOException {
        final byte[] encoded = Files.readAllBytes(Paths.get(filename));
        final String puzzle =  new String(encoded, Charset.defaultCharset());
        return Grid.fromString(puzzle);
    }

    private static class CellController implements Grid.CellListener {
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
}
