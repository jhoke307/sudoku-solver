package com.perivi.sudoku.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.perivi.sudoku.java.Grid.CellState;
import com.perivi.sudoku.java.Grid.House;

/**
 * We may find that some set of numbers is only possible within a given set of
 * squares. This often knocks out enough possibilities to give us other squares
 * outside the group.
 *
 * @author jdh
 *
 */
public class NakedMultiplesStrategy implements Strategy {

    @Override
    public Boolean apply(@Nullable final Grid input) {
        if (input == null) {
            return Boolean.FALSE;
        }

        // For each row, column, house:
        for (final Iterable<Grid.Cell> section : input.sections()) {
            // Build the list of possibilities.
            final List<Grid.Cell> cells = Lists.newArrayList(section);
            final List<Set<Integer>> possibilities = new ArrayList<>();
            final Multimap<Integer, Grid.Cell> possibilityToCellsMap = ArrayListMultimap.create();
            for (final Grid.Cell cell : cells) {
                if (cell.getValue() != null) {
                    possibilities.add(Collections.<Integer> emptySet());
                } else {
                    final int i = cell.getRow();
                    final int j = cell.getCol();
                    final Set<Integer> rowValues = Sets.newHashSet(Grid.values(input.row(i)));
                    final Set<Integer> colValues = Sets.newHashSet(Grid.values(input.column(j)));
                    final House houseContaining = input.houseContaining(i, j);
                    final Set<Integer> houseValues = Sets.newHashSet(Grid.values(houseContaining));

                    final Set<Integer> remainingPossibilities = new HashSet<>(Sets.difference(Grid.ALL_POSSIBILITIES,
                            Sets.union(rowValues, Sets.union(colValues, houseValues))));
                    possibilities.add(remainingPossibilities);

                    for (final Integer p : remainingPossibilities) {
                        possibilityToCellsMap.put(p, cell);
                    }
                }
            }

            // TODO There is probably a clever recursive way to write this
            boolean converged;
            do {
                converged = true;

                // See if any two, three or four cells are naked twins, triplets
                // or quads
                for (int i = 0; i < possibilities.size(); ++i) {
                    final Set<Integer> ips = possibilities.get(i);
                    if (ips.size() > 4) {
                        continue;
                    }

                    for (int j = i + 1; j < possibilities.size(); ++j) {
                        final Set<Integer> jps = possibilities.get(j);
                        if (jps.size() > 4) {
                            continue;
                        }

                        if (ips.size() == 2 && ips.equals(jps)) {
                            for (int k = 0; k < possibilities.size(); ++k) {
                                if (k != i && k != j) {
                                    final Set<Integer> ks = possibilities.get(k);
                                    if (!Collections.disjoint(ks, ips)) {
                                        ks.removeAll(ips);
                                        converged = false;
                                    }
                                }
                            }
                        } else {
                            for (int k = j + 1; k < possibilities.size(); ++k) {
                                final Set<Integer> kps = possibilities.get(k);
                                if (kps.size() > 4) {
                                    continue;
                                }

                                // It could be a chain. If the possibilities for
                                // all
                                // three cells are only three in number...
                                // final Set<Integer> ups =
                                // Sets.union(Sets.union(ips, jps), kps);
                                if (kps.size() == 3 && kps.equals(ips) && kps.equals(jps)) {
                                    for (int l = 0; l < possibilities.size(); ++l) {
                                        if (l != i && l != j && l != k) {
                                            final Set<Integer> lps = possibilities.get(l);
                                            if (!Collections.disjoint(lps, kps)) {
                                                lps.removeAll(kps);
                                                converged = false;
                                            }
                                        }
                                    }
                                } else {
                                    for (int l = k + 1; l < possibilities.size(); ++l) {
                                        final Set<Integer> lps = possibilities.get(l);
                                        if (lps.size() > 4) {
                                            continue;
                                        }

                                        // It could be a chain
                                        // final Set<Integer> uups =
                                        // Sets.union(ups, lps);
                                        if (lps.size() == 4 && lps.equals(ips) && lps.equals(jps) && lps.equals(kps)) {
                                            for (int m = 0; m < possibilities.size(); ++m) {
                                                if (m != i && m != j && m != k && m != l) {
                                                    final Set<Integer> mps = possibilities.get(m);
                                                    if (!Collections.disjoint(mps, ips)) {
                                                        mps.removeAll(ips);
                                                        converged = false;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Look for hidden twins -- the set of possible cells for two
                // possibilities
                // is two
                // First find remaining possibilities within group
                final Set<Integer> remainingInGroup = new HashSet<>();
                for (final Set<Integer> ps : possibilities) {
                    remainingInGroup.addAll(ps);
                }
                final List<Integer> remainingList = new ArrayList<>(remainingInGroup);
                for (int i = 0; i < remainingList.size(); ++i) {
                    final Set<Grid.Cell> ics = new HashSet<>(possibilityToCellsMap.get(remainingList.get(i)));

                    for (int j = i + 1; j < remainingList.size(); ++j) {
                        final Set<Grid.Cell> jcs = new HashSet<>(possibilityToCellsMap.get(remainingList.get(j)));
                        if (ics.size() == 2 && ics.equals(jcs)) {
                            // For each cell -
                            for (int idx = 0; idx < possibilities.size(); ++idx) {
                                // If it is one of our two cells,
                                // we can remove all other possibilities
                                final Set<Integer> psForCell = possibilities.get(idx);
                                final Grid.Cell thisCell = cells.get(idx);
                                if (ics.contains(thisCell) && psForCell.size() != 2) {
                                    psForCell.clear();
                                    psForCell.add(remainingList.get(i));
                                    psForCell.add(remainingList.get(j));

                                    // We can also remove these cells from all
                                    // other possibilities.
                                    for (final Integer p : Sets.difference(Grid.ALL_POSSIBILITIES, psForCell)) {
                                        possibilityToCellsMap.remove(p, thisCell);
                                    }

                                    converged = false;
                                }

                                // We can also remove our two possibilities from
                                // all other cells
                                if (!ics.contains(thisCell)) {
                                    if (psForCell.contains(remainingList.get(i))
                                            || psForCell.contains(remainingList.get(j))) {
                                        converged = false;
                                    }

                                    possibilityToCellsMap.remove(remainingList.get(i), thisCell);
                                    possibilityToCellsMap.remove(remainingList.get(j), thisCell);
                                    psForCell.remove(remainingList.get(i));
                                    psForCell.remove(remainingList.get(j));
                                }

                            }

                        }
                    }
                }
            } while (!converged);

            // After all that, if there is any cell which has only one
            // possibility,
            // it must be the one!
            for (int i = 0; i < possibilities.size(); ++i) {
                final Set<Integer> ps = possibilities.get(i);
                if (ps.size() == 1 && cells.get(i).getValue() == null) {
                    System.out.println(
                            String.format("cell(%s) has only one possibility %s after elimination", cells.get(i), ps));
                    cells.get(i).setValue(ps.iterator().next());
                    cells.get(i).setState(CellState.HINT);
                    return Boolean.TRUE;
                }
            }

        }

        return Boolean.FALSE;
    }
}
