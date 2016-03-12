package com.perivi.sudoku.java;

/**
 * If there is only one remaining possibility in a row or column, it must fill
 * the remaining cell.
 *
 * This actually does nothing since the SinglePossibilityStrategy supersedes it.
 *
 * @author jdh
 *
 */
public class OnlyChoiceStrategy implements Strategy {
	@Override
	public Boolean apply(final Grid grid) {
		return Boolean.FALSE;
	}
}
