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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Strings;
import com.google.common.collect.Multiset;
import com.perivi.sudoku.java.Grid;
import com.perivi.sudoku.java.Solver;
import com.perivi.sudoku.java.Strategy;

public class AppShell {
	private static Label fileLabel;

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

		final SudokuGrid widgetGrid = new SudokuGrid(inputGrid, shell, SWT.NONE);
		final GridData gd_widgetGrid = new GridData();
		gd_widgetGrid.horizontalAlignment = GridData.FILL;
		gd_widgetGrid.horizontalSpan = 9;
		widgetGrid.setLayoutData(gd_widgetGrid);

		final Solver solver = Solver.smartSolver();
		final Button stepButton = new Button(shell, SWT.NONE);
		stepButton.setText("Step");
		stepButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				widgetGrid.clearHighlight();
				solver.solveStep(inputGrid);
			}
		});

		final Button solveButton = new Button(shell, SWT.NONE);
		solveButton.setText("Solve");
		solveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				final int interval = 100;

				final Runnable runnable = new Runnable() {
					@Override
					public void run() {

						widgetGrid.clearHighlight();
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
						fileLabel.setText(filename);
					}
					catch (final IllegalArgumentException | IOException e) {
						final MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR);
						msg.setText("Error parsing file \"" + filename + "\": " + e.getMessage());
						msg.open();
					}
				}
			}
		});
		
		fileLabel = new Label(shell, SWT.NONE);
		fileLabel.setText("449");
		final GridData gd_fileLabel = new GridData();
		gd_fileLabel.horizontalSpan = 6;
		gd_fileLabel.horizontalAlignment = GridData.FILL;
		fileLabel.setLayoutData(gd_fileLabel);


		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

    private static Grid loadGrid(final String filename) throws IOException {
        final byte[] encoded = Files.readAllBytes(Paths.get(filename));
        final String puzzle =  new String(encoded, Charset.defaultCharset());
        return Grid.fromString(puzzle);
    }
}
