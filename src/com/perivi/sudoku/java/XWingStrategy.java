package com.perivi.sudoku.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * If four cells are the corners of a square and they are the only possibilities
 * for a number in either their respective columns or rows, you get X-Wing - you
 * can eliminate that number as a possibility from all other rows or columns.
 * @author jdh
 *
 */
public class XWingStrategy implements Strategy {
	@Override
	public Boolean apply(@Nullable final Grid input) {
	    if (input == null) {
	        return Boolean.FALSE;
	    }

	    for (final Integer possibility : Grid.ALL_POSSIBILITIES) {
//	        System.out.println("Considering " + possibility);

	        final Map<Grid.Column, Set<Grid.Cell>> columnsWithPossibleCells = new HashMap<>();
	        final PossibleCellPredicate pred = new PossibleCellPredicate(possibility);

	        for (final Grid.Column col : input.columns()) {
	            final List<Grid.Cell> possibleCells = Lists.newArrayList(Iterables.filter(col, pred));
//	            System.out.println("Column " + col.getColumn() + ": possible cells: " + possibleCells);
	            if (possibleCells.size() == 2) {
	                columnsWithPossibleCells.put(col, new HashSet<>(possibleCells));
	            }
	        }

	        final List<Grid.Column> possibleColumns = Lists.newArrayList(columnsWithPossibleCells.keySet()); 
	        for (int i = 0; i < possibleColumns.size(); i++) {
	            // What are the possible rows for this column? Do they match the other column?
	            final Grid.Column colI = possibleColumns.get(i);
	            final Set<Grid.Cell> possibleCellsI = columnsWithPossibleCells.get(colI); 
	            final Set<Integer> possibleRowsI = new HashSet<>();

	            for (final Grid.Cell cell : possibleCellsI) {
	                possibleRowsI.add(cell.getRow());
	            }

	            for (int j = i + 1; j < possibleColumns.size(); j++) {
	                final Grid.Column colJ = possibleColumns.get(j);
	                final Set<Grid.Cell> possibleCellsJ = columnsWithPossibleCells.get(colJ);
	                final Set<Integer> possibleRowsJ = new HashSet<>();

	                for (final Grid.Cell cell : possibleCellsJ) {
	                    possibleRowsJ.add(cell.getRow());
	                }

	                if (possibleRowsI.equals(possibleRowsJ)) {
	                    // Apply X-wing to this row and column
	                    final Set<Integer> columns = Sets.newHashSet(colI.getColumn(), colJ.getColumn());
	                    if (zap(input, possibility, possibleRowsI, columns)) {
	                        System.out.println("Applied X-Wing to " + possibility + ", rows " + possibleRowsI + " columns " + columns);
	                        return Boolean.TRUE;
	                    }
	                }

	            }
	        }
	    }

	    for (final Integer possibility : Grid.ALL_POSSIBILITIES) {
//	        System.out.println("Considering " + possibility);

	        final Map<Grid.Row, Set<Grid.Cell>> rowsWithPossibleCells = new HashMap<>();
	        final PossibleCellPredicate pred = new PossibleCellPredicate(possibility);

	        for (final Grid.Row col : input.rows()) {
	            final List<Grid.Cell> possibleCells = Lists.newArrayList(Iterables.filter(col, pred));
//	            System.out.println("Column " + col.getColumn() + ": possible cells: " + possibleCells);
	            if (possibleCells.size() == 2) {
	                rowsWithPossibleCells.put(col, new HashSet<>(possibleCells));
	            }
	        }

	        final List<Grid.Row> possibleRows = Lists.newArrayList(rowsWithPossibleCells.keySet()); 
	        for (int i = 0; i < possibleRows.size(); i++) {
	            // What are the possible rows for this column? Do they match the other column?
	            final Grid.Row rowI = possibleRows.get(i);
	            final Set<Grid.Cell> possibleCellsI = rowsWithPossibleCells.get(rowI); 
	            final Set<Integer> possibleColsI = new HashSet<>();

	            for (final Grid.Cell cell : possibleCellsI) {
	                possibleColsI.add(cell.getCol());
	            }

	            for (int j = i + 1; j < possibleRows.size(); j++) {
	                final Grid.Row rowJ = possibleRows.get(j);
	                final Set<Grid.Cell> possibleCellsJ = rowsWithPossibleCells.get(rowJ);
	                final Set<Integer> possibleColsJ = new HashSet<>();

	                for (final Grid.Cell cell : possibleCellsJ) {
	                    possibleColsJ.add(cell.getRow());
	                }

	                if (possibleColsJ.equals(possibleColsI)) {
	                    // Apply X-wing to this row and column
	                    final Set<Integer> rows = Sets.newHashSet(rowI.getRow(), rowJ.getRow());
	                    if (zap(input, possibility, rows, possibleColsI)) {
	                        System.out.println("Applied X-Wing to " + possibility + ", rows " + rows + " columns " + possibleColsI);
	                        return Boolean.TRUE;
	                    }
	                }
	            }
	        }
	    }

		return Boolean.FALSE;
	}

	private boolean zap(final Grid grid, final Integer possibility,
	        final Set<Integer> rows, final Set<Integer> columns) {
	    boolean zappedAny = false;

	    for (final Integer row : rows) {
	        final Grid.Row gridRow = grid.row(row);
	        for (final Grid.Cell cell : gridRow) {
	            if (!columns.contains(cell.getCol()) && cell.containsNote(possibility)) {
	                zappedAny = true;
	                System.out.println("applying X-Wing to row " + row + " cell " + cell + " possibility " + possibility);
	                cell.removeNote(possibility);
	                cell.notifyListeners();
	            }
	        }
	    }

	    for (final Integer col : columns) {
	        final Grid.Column gridCol = grid.column(col);
	        for (final Grid.Cell cell : gridCol) {
	            if (!rows.contains(cell.getRow()) && cell.containsNote(possibility)) {
	                zappedAny = true;
	                System.out.println("applying X-Wing to col " + col + " cell " + cell + " possibility " + possibility);
	                cell.removeNote(possibility);
	                cell.notifyListeners();
	            }
	        }
	    }

	    if (zappedAny) {
	        // Highlight X-wing cells too
	        for (final Integer row : rows) {
	            for (final Integer col : columns) {
	                grid.cellAt(row, col).notifyListeners();
	            }
	        }
	    }

	    return zappedAny;
	}

}
