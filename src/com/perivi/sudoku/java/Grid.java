package com.perivi.sudoku.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Grid {
	public enum CellState {
		INPUT,
		VALID,
		INVALID,
		HINT;
	}

    public final static Set<Integer> ALL_POSSIBILITIES = Sets.newHashSet(1, 2, 3, 4, 5, 6, 7, 8, 9);
    public final static int DEFAULT_WIDTH = 9;
    public final static int DEFAULT_HEIGHT = 9;
    public final static int DEFAULT_HOUSE_WIDTH = 3;
    public final static int DEFAULT_HOUSE_HEIGHT = 3;

    @Getter
    private final int width;
    @Getter
    private final int height;
    @Getter
    private final int houseWidth;
    @Getter
    private final int houseHeight;

    private final Cell[][] gridData;

    public Grid() {
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        houseWidth = DEFAULT_HOUSE_WIDTH;
        houseHeight = DEFAULT_HOUSE_HEIGHT;

        if (width % houseWidth != 0) {
            throw new IllegalArgumentException("width must be an integral multiple of houseWidth");
        }

        if (height % houseHeight != 0) {
            throw new IllegalArgumentException("height must be an integral multiple of houseHeight");
        }

        final CellListener listener = new CellListener() {
        	@Override
        	public void cellUpdated(Cell c) {
        		final Integer value = c.getValue();

        		if (value != null) {
        			final Iterable<Grid.Cell> cells =
        					Iterables.concat(row(c.getRow()), column(c.getCol()),
        							houseContaining(c.getRow(), c.getCol()));
        			for (final Grid.Cell otherCell : cells) {
        				if (!c.equals(otherCell)) {
        					otherCell.removeNote(value);
        				}
        			}
        		}
        	}
        };

        gridData = new Cell[height][];
        for (int i = 0; i < height; ++i) {
            gridData[i] = new Cell[width];
            for (int j = 0; j < width; ++j) {
                gridData[i][j] = new Cell(i, j);
                gridData[i][j].addListener(listener);
            }
        }
    }

    public Grid(final Grid source) {
        this();
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                cellAt(i, j).setValue(source.cellAt(i, j).getValue());
                cellAt(i, j).setNotes(source.cellAt(i,  j).getNotes());
                cellAt(i, j).setState(source.cellAt(i,  j).getState());
            }
        }
    }

    public Cell cellAt(final int row, final int col) {
        return gridData[row][col];
    }

    public Column column(final int col) {
        return new Column(col);
    }

    public Row row(final int row) {
        return new Row(row);
    }

    public House house(final int row, final int col) {
        return new House(row, col);
    }

    public House houseContaining(final int row, final int col) {
        final int houseRow = row / getHouseHeight();
        final int houseCol = col / getHouseWidth();
        return house(houseRow, houseCol);
    }

    public Iterable<Cell> cells() {
    	final List<Cell> l = new ArrayList<>(width * height);
    	for (int i = 0; i < height; ++i) {
    		for (int j = 0; j < width; ++j) {
    			l.add(cellAt(i, j));
    		}
    	}
    	return l;
    }

    public Iterable<Column> columns() {
        final List<Column> l = new ArrayList<>(getWidth());
        for (int i = 0; i < getWidth(); ++i) {
            l.add(column(i));
        }
        return l;
    }

    public Iterable<Row> rows() {
        final List<Row> l = new ArrayList<>(getHeight());
        for (int i = 0; i < getHeight(); ++i) {
            l.add(row(i));
        }
        return l;
    }

    public Iterable<House> houses() {
        final int houseRows = height / houseHeight;
        final int houseCols = width / houseWidth;
        final List<House> l = new ArrayList<>(houseRows * houseCols);
        for (int i = 0; i < houseRows; ++i) {
            for (int j = 0; j < houseCols; ++j) {
                l.add(house(i, j));
            }
        }

        return l;
    }

    public Iterable<Iterable<Cell>> sections() {
        return Iterables.concat(rows(), columns(), houses());
    }

    public static List<Integer> values(final Iterable<Grid.Cell> section) {
        final List<Integer> l = new ArrayList<>();

        for (final Grid.Cell c : section) {
            l.add(c.getValue());
        }

        return l;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < height; ++i) {
            for (final Cell c : row(i)) {
                b.append(Objects.toString(c.getValue(), " "));
            }
            b.append(System.lineSeparator());
        }
        return b.toString();
    }

    /**
     * @param input
     * @return
     * @throws NullPointerException
     *             if input is null
     * @throws IllegalArgumentException
     *             if input is not in expected format
     */
    public static Grid fromString(final String input) {
        Objects.requireNonNull(input, "input cannot be null");

        final String lines[] = input.split("\\r?\\n");
        if (lines.length != DEFAULT_HEIGHT) {
            throw new IllegalArgumentException("Input has " + lines.length + " lines, expected " + DEFAULT_HEIGHT);
        }

        for (final String l : lines) {
            if (l.length() != DEFAULT_WIDTH) {
                throw new IllegalArgumentException(
                        "Input has line with length " + l.length() + ", expected " + DEFAULT_WIDTH);
            }

            if (!l.matches("^[1-9 \n]+$")) {
                throw new IllegalArgumentException("Input must only contain characters 1-9, blanks and newlines.");
            }
        }

        final Grid instance = new Grid();
        for (int i = 0; i < DEFAULT_HEIGHT; i++) {
            for (int j = 0; j < DEFAULT_WIDTH; j++) {
                final char c = lines[i].charAt(j);
                if (c != ' ') {
                    instance.cellAt(i, j).setValue(Integer.parseInt(Character.toString(c)));
                    instance.cellAt(i, j).setState(CellState.INPUT);
                }
            }
        }

        return instance;
    }

    @Data
    public class Cell {
        private final int row;
        private final int col;
        private Integer value;
        private CellState state;
        private Set<Integer> notes = new HashSet<>(Grid.ALL_POSSIBILITIES);
        private List<CellListener> listeners = new ArrayList<>();

        public void setValue(Integer value) {
        	this.value = value;
        	this.notes.clear();
        	notifyListeners();
        }

        public void setState(CellState state) {
        	this.state = state;
        	notifyListeners();
        }

        public void addNote(Integer n) {
        	notes.add(n);
        }

        public void removeNote(Integer n) {
        	notes.remove(n);
        }

        public void addListener(CellListener l) {
        	listeners.add(l);
        	l.cellUpdated(this);
        }

        private void notifyListeners() {
        	for (final CellListener l : listeners) {
        		l.cellUpdated(this);
        	}
        }
    }

    public interface CellListener {
    	void cellUpdated(Cell c);
    }

    @Data
    public class Row implements Iterable<Cell> {
        private final int row;

        public Cell cell(final int idx) {
            return gridData[row][idx];
        }

        @Override
        public Iterator<Cell> iterator() {
            return Arrays.asList(gridData[row]).iterator();
        }
    }

    @Data
    public class Column implements Iterable<Cell> {
        private final int column;

        public Cell cell(final int idx) {
            return gridData[idx][column];
        }

        @Override
        public Iterator<Cell> iterator() {
            final List<Cell> l = new ArrayList<>(height);
            for (int i = 0; i < height; ++i) {
                l.add(cell(i));
            }
            return l.iterator();
        }
    }

    @Data
    public class House implements Iterable<Cell> {
        private final int row;
        private final int column;

        public Cell cell(final int r, final int c) {
            return gridData[row * houseHeight + r][column * houseWidth + c];
        }

        @Override
        public Iterator<Cell> iterator() {
            final List<Cell> l = new ArrayList<>(houseHeight * houseWidth);
            for (int i = 0; i < houseHeight; i++) {
                for (int j = 0; j < houseWidth; j++) {
                    l.add(cell(i, j));
                }
            }
            return l.iterator();
        }
    }
}
