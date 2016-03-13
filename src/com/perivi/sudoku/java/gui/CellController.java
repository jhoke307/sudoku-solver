package com.perivi.sudoku.java.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import com.perivi.sudoku.java.Grid;
import com.perivi.sudoku.java.Grid.Cell;
import com.perivi.sudoku.java.Grid.CellState;

class CellController implements Grid.CellListener {
	private final SudokuCell widget;
	private final Grid.Cell cell;
	private final SudokuGrid gridWidget;

	CellController(SudokuGrid g, SudokuCell w, Grid.Cell c) {
		widget = w;
		cell = c;
		gridWidget = g;

		w.addKeyListener(new KeyAdapter() {
		    @Override
		    public void keyPressed(KeyEvent e) {
		        CellController.this.keyPressed(e);
		    }
		});

		w.addFocusListener(new FocusAdapter() {
		    @Override
		    public void focusGained(FocusEvent e) {
		        CellController.this.focusGained(e);
		    }
        });
	}

	private void keyPressed(final KeyEvent e) {
	    switch (e.keyCode) {
	    case '1':
	    case '2':
	    case '3':
	    case '4':
	    case '5':
	    case '6':
	    case '7':
	    case '8':
	    case '9':
	        if ((e.stateMask & (SWT.SHIFT|SWT.ALT|SWT.CONTROL)) != 0) {
	            toggleNote(e.keyCode);
	        }
	        else {
	            toggleValue(e.keyCode);
	        }
	        break;
	    case 'q': toggleNote(1); break;
	    case 'w': toggleNote(2); break;
	    case 'e': toggleNote(3); break;
	    case 'r': toggleNote(4); break;
	    case 't': toggleNote(5); break;
	    case 'y': toggleNote(6); break;
	    case 'u': toggleNote(7); break;
	    case 'i': toggleNote(8); break;
	    case 'o': toggleNote(9); break;
	    }
	}

	private void focusGained(final FocusEvent e) {
	    gridWidget.clearHighlight();
	}

	@Override
	public void cellUpdated(Cell c) {
		widget.setMainNumber(c.getValue());
		widget.setMainState(c.getState());
		widget.setNoteNumbers(c.getNotes());
		widget.setHighlight(true);
	}
	
	private void toggleValue(final int valueKey) {
	    final int value = Integer.parseInt(Character.toString((char) valueKey));

	    gridWidget.clearHighlight();
	    if (cell.getValue() == null || cell.getValue() != value) {
	        cell.setValue(value);
	        cell.setState(CellState.VALID);
	    }
	    else {
	        cell.setValue(null);
	        cell.setState(CellState.VALID);
	    }
	    gridWidget.check();
	}

	private void toggleNote(final int valueOrKey) {
	    final int value;
	    if (valueOrKey < 10) {
	        value = valueOrKey;
	    }
	    else {
	        value = Integer.parseInt(Character.toString((char) valueOrKey));
	    }

	    gridWidget.clearHighlight();
	    if (cell.containsNote(value)) {
	        cell.removeNote(value);
	    }
	    else {
	        cell.addNote(value);
	    }
	    gridWidget.check();
	}
}