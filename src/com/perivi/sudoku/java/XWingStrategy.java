package com.perivi.sudoku.java;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/**
 * If four cells are the corners of a square and they are the only possibilities
 * for a number in either their respective columns or rows, you get X-Wing - you
 * can eliminate that number as a possibility from all other rows or columns.
 * @author jdh
 *
 */
public class XWingStrategy implements Strategy {

	@Override
	public Boolean apply(Grid input) {
		// TODO I think this is only half, it looks for the columns first
		final Map<Integer, Multimap<Integer, Integer>> colToPToRows = new HashMap<>();

		for (final Grid.Column col : input.columns()) {
			final Multimap<Integer, Integer> pToRows = HashMultimap.create();

			for (final Grid.Cell cell : col) {
				for (final Integer p : cell.getNotes()) {
					pToRows.put(p, cell.getRow());
				}
			}

			colToPToRows.put(col.getColumn(), pToRows);
		}

		// Now for each column, if it has only two cells for a possibility,
		// count it as potential. If between any two such columns the cells
		// line up in the same row, we can do the X-Wing
		for (final Integer p : Grid.ALL_POSSIBILITIES) {
			final Multiset<Integer> rowCount = HashMultiset.create();
			final Set<Integer> colsWithTwo = new HashSet<>();

			for (final Integer col : colToPToRows.keySet()) {
				final Multimap<Integer, Integer> pToRows = colToPToRows.get(col);
				final Collection<Integer> rows = pToRows.asMap().get(p);
				if (rows != null && rows.size() == 2) {
					colsWithTwo.add(col);
					for (final Integer row : colToPToRows.get(col).asMap().get(p)) {
						rowCount.add(row);
					}
				}
			}

			final Set<Integer> rowsWithTwo = new HashSet<>();
			for (final Integer row : rowCount.elementSet()) {
				if (rowCount.count(row) == 2) {
					rowsWithTwo.add(row);
				}
			}

			if (rowsWithTwo.size() == 2) {
				final Set<Grid.Cell> matchingCells = new HashSet<>();
				boolean clearedNote = false;

				for (final Integer col : colsWithTwo) {
					for (final Integer row : rowsWithTwo) {
						matchingCells.add(input.cellAt(row, col));
					}
				}
				
				for (final Integer col : colsWithTwo) {
					for (final Grid.Cell cell : input.column(col)) {
						if (!matchingCells.contains(cell)) {
							if (cell.getNotes().contains(p)) {
								System.out.println("applying X-Wing to cell " + cell + " possibility " + p);
								cell.removeNote(p);
								clearedNote = true;
							}
						}
					}
				}

				for (final Integer row : rowsWithTwo) {
					for (final Grid.Cell cell : input.row(row)) {
						if (!matchingCells.contains(cell)) {
							if (cell.getNotes().contains(p)) {
								System.out.println("applying X-Wing to cell " + cell + " possibility " + p);
								cell.removeNote(p);
								clearedNote = true;
							}
						}
					}
				}

				if (clearedNote) {
					System.out.println("Applied X-Wing, rows " + rowsWithTwo + " columns " + colsWithTwo);
					return Boolean.TRUE;
				}
			}
		}

		return null;
	}

}
