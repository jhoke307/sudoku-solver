# Sudoku Solver

Attempt to solve Sudoku puzzles using human strategies.

Sudoku puzzles consist of a 9-by-9 grid of numbers and blanks which may
be subdivided into 9 3-by-3 boxes. To solve the puzzle, the player needs
to fill in all blanks such that each row, column, and box contains
exactly one each of the numbers 1 through 9.

## Supported Platforms

This has only been tested on Debian Linux (amd64).

## Usage

Run the command-line solver:

```
gradlew run -Pargs=data/bb001.txt
```

Run the GUI solver:

```
gradlew runGui
```
