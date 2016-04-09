package com.perivi.sudoku.java;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.perivi.sudoku.java.Grid.Cell;

/**
 * If a subgroup within a house is the only one that has a certain possibility,
 * we can rule it out from everything else in that row or column.
 * @author jdh
 *
 */
public class SubgroupExclusionStrategy implements Strategy {

	@Override
	public Boolean apply(@Nullable final Grid input) {
	    if (input == null) {
	        return Boolean.FALSE;
	    }

		for (final Grid.House house : input.houses()) {
			final Multimap<Integer, Integer> pToRowMap = HashMultimap.create();

			for (int i = 0; i < input.getHouseHeight(); ++i) {
				final Set<Integer> ps = new HashSet<>();

				for (final Cell cell : house.subRow(i)) {
					ps.addAll(cell.getNotes());
				}

				for (final Integer p : ps) {
					pToRowMap.put(p, i);
				}
			}

			for (final Entry<Integer, Collection<Integer>> entry : pToRowMap.asMap().entrySet()) {
				// If this possibility is only in one subgroup, we can rule it
				// out of every other cell in the row.
				if (entry.getValue().size() == 1) {
					boolean clearedNotes = false;

					final int subrowNum = entry.getValue().iterator().next();
					final Grid.Row row = house.subRow(subrowNum).iterator().next().getRowObject();
					for (final Grid.Cell cell : row) {
						if (!house.contains(cell) && cell.getNotes().contains(entry.getKey())) {
							cell.removeNote(entry.getKey());
							cell.notifyListeners();
							System.out.println("Applying subgroup exclusion to row " + row + " to eliminate " + entry.getKey() + " from " + cell);
							System.out.println("subgroup was: " + house.subRow(entry.getValue().iterator().next()));
							clearedNotes = true;
						}
					}

					if (clearedNotes) {
						return Boolean.TRUE;
					}
				}
			}
		}

		for (final Grid.House house : input.houses()) {
			final Multimap<Integer, Integer> pToColMap = HashMultimap.create();

			for (int i = 0; i < input.getHouseWidth(); ++i) {
				final Set<Integer> ps = new HashSet<>();

				for (final Cell cell : house.subColumn(i)) {
					ps.addAll(cell.getNotes());
				}

				for (final Integer p : ps) {
					pToColMap.put(p, i);
				}
			}

			for (final Entry<Integer, Collection<Integer>> entry : pToColMap.asMap().entrySet()) {
				// If this possibility is only in one subgroup, we can rule it
				// out of every other cell in the column.
				if (entry.getValue().size() == 1) {
					boolean clearedNotes = false;

					final int subcolNum = entry.getValue().iterator().next();
					final Grid.Column col = house.subColumn(subcolNum).iterator().next().getColumnObject();
					for (final Grid.Cell cell : col) {
						if (!house.contains(cell) && cell.getNotes().contains(entry.getKey())) {
							cell.removeNote(entry.getKey());
							cell.notifyListeners();
							System.out.println("Applying subgroup exclusion to col " + col + " to eliminate " + entry.getKey() + " from " + cell);
							clearedNotes = true;
						}
					}

					if (clearedNotes) {
						return Boolean.TRUE;
					}
				}
			}
		}

		return Boolean.FALSE;
	}

}
