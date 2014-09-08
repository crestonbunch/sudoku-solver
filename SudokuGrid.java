import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

/**
 * Represents a sudoku puzzle.
 *
 * @author  Creston Bunch
 * @version 1.0
 */

public class SudokuGrid {

    public final int ROWS;
    public final int COLUMNS;

    private ArrayList<SudokuCell> cells;

    /**
     * Initializes the grid with specific number of rows/columns.
     *
     * @param rows  the number of rows
     * @param cols  the number of columns
     * @throws IllegalArgumentException if rows or columns are not square
     *                                  numbers
     */
    public SudokuGrid(int rows, int cols) throws IllegalArgumentException {
        if (!isSquare(rows) && !isSquare(cols)) {
            throw new IllegalArgumentException("Sudoku sizes must be square numbers.");
        }
        if (rows != cols) {
            throw new IllegalArgumentException("Sudoku puzzles must be square.");
        }
        this.ROWS = rows;
        this.COLUMNS = cols;
        buildCells();
    }

    /**
     * Initializes the puzzle with the default rows and columns
     */
    public SudokuGrid() {
        this(9, 9);
    }

    /**
     * Initializes the grid as a square.
     *
     * @param sideLength    the length of the sides
     */
    public SudokuGrid(int sideLength) {
        this(sideLength, sideLength);
    }

    /**
     * Sets the entry at a specific x,y coordinate. Makes it immutable.
     *
     * @param x     the column to set
     * @param y     the row to set
     * @param val   the value to set
     * @throws IndexOutOfBoundsException    if x and y are not within bounds
     * @throws IllegalArgumentException     if val is not a valid sudoku input
     */
    public void set(int x, int y, int val) 
    throws IndexOutOfBoundsException, IllegalArgumentException {
        if (x < COLUMNS && y < ROWS && x >= 0 && y >= 0) {
            if (validInput(val)) {
                SudokuCell c = getCellAt(x, y);
                c.freeze(val);
            } else {
                throw new IllegalArgumentException(
                    "Valid inputs are 1-" +
                    (new Integer(ROWS*COLUMNS)).toString()
                );
            }
        } else {
            throw new IndexOutOfBoundsException(
                "A sudoku puzzle is "+ROWS+"x"+COLUMNS
            );
        }
    }

    /**
     * Parses a string value as input.
     *
     * @param x     the column to set
     * @param y     the row to set
     * @param val   the value to set
     * @throws IndexOutOfBoundsException    if x and y are not within bounds
     * @throws IllegalArgumentException     if val is not a valid sudoku input
     */
    public void set(int x, int y, String val)
    throws IndexOutOfBoundsException, IllegalArgumentException {
        if (!val.equals("")) {
            int i = Integer.parseInt(val);
            this.set(x, y, i);
        }
    }

    /**
     * Solves the puzzle.
     */
    public void solve() {
        ArrayList<SudokuCell> empty = getEmpty();
        int index = 0;
        if (!isValid()) {
            System.out.println("Invalid puzzle.");
            System.exit(0);
        }
        while (!isSolved() && index >= 0 && index < empty.size()) {
            SudokuCell cell = empty.get(index);
            cell.refresh();
            if (!cell.isImpossible()) {
                cell.assign();
                index++;
            } else {
                cell.reset();
                index--;
            }
        }
    }

    /**
     * Gets all of the cells in a row.
     *
     * @param row   the row index to fetch
     * @return an array of all the cells in a row
     */
    public SudokuCell[] getRow(int row) {
        SudokuCell[] arr = new SudokuCell[COLUMNS];
        int index = 0;
        for (SudokuCell c : cells) {
            if (c.getRow() == row) {
                arr[index] = c;
                index++;
            }
        }
        return arr;
    }

    /**
     * Gets all of the cells in a column.
     *
     * @param col   the column index to fetch
     * @return an array of all the cells in a column
     */
    public SudokuCell[] getCol(int col) {
        SudokuCell[] arr = new SudokuCell[ROWS];
        int index = 0;
        for (SudokuCell c : cells) {
            if (c.getCol() == col) {
                arr[index] = c;
                index++;
            }
        }
        return arr;
    }

    /**
     * Gets all of the cells in a block
     *
     * @param row   the row of the cell in the block
     * @param col   the column of the cell in the block
     * @return an array of all the cells in the block
     */
    public SudokuCell[] getBlock(int row, int col) {
        int blockRows = (int) Math.sqrt(ROWS);
        int blockCols = (int) Math.sqrt(COLUMNS);
        // difference from the starting row/col of the block
        int dr = row % blockRows;
        int dc = col % blockCols;
        // staring row/col of the block
        int r = row - dr;
        int c = col - dc;
        // ending row/col of the block (exclusive)
        int er = r + blockRows;
        int ec = c + blockCols;

        SudokuCell[] arr = new SudokuCell[ROWS];
        int index = 0;
        for (SudokuCell cell : cells) {
            if (cell.getRow() >= r && cell.getRow() < er) {
                if (cell.getCol() >= c && cell.getCol() < ec) {
                    arr[index] = cell;
                    index++;
                }
            }
        }
        return arr;
    }

    /**
     * Gets the cell at the specific row and column.
     *
     * @param row   the row of the cell
     * @param col   the column of the cell
     * @return the cell at the row and column
     * @throws IndexOutOfBoundsException if the cell does not exist
     */
    public SudokuCell getCellAt(int row, int col)
    throws IndexOutOfBoundsException {
        for (SudokuCell c : cells) {
            if (c.getRow() == row && c.getCol() == col) {
                return c;
            }
        }
        throw new IndexOutOfBoundsException("Cell does not exist.");
    }

    /**
     * Checks if the grid is solved.
     *
     * @return true if the grid is solved
     */
    public boolean isSolved() {
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (SudokuCell c : cells) {
            int k = c.get();
            if (counts.containsKey(k)) {
                // increment
                counts.put(k, counts.get(k) + 1);
            } else {
                // create
                counts.put(k, 1);
            }
        }
        
        for (Integer v : counts.values()) {
            if (v != COLUMNS) {
                return false; // not enough values
            }
        }

        return (counts.size() == ROWS);
    }

    /**
     * Checks if the initial state is solvable.
     *
     * @return true if the puzzle is valid
     */
    public boolean isValid() {
        for (int r = 0; r < ROWS; r++) {
            SudokuCell[] l = getRow(r);
            for (SudokuCell cell : l) {
               // there is more than one occurance of c
               if (count(l, cell) > 1) {
                    return false;
               }
            }
        }
        for (int c = 0; c < COLUMNS; c++) {
            SudokuCell[] l = getCol(c);
            for (SudokuCell cell : l) {
                if (count(l, cell) > 1) {
                    return false;
                }
            }
        }
        int dr = (int) Math.sqrt(ROWS);
        int dc = (int) Math.sqrt(COLUMNS);
        for (int r = 0; r < ROWS; r = r + dr) {
            for (int c = 0; c < COLUMNS; c = c + dc) {
                SudokuCell[] l = getBlock(r, c);
                for (SudokuCell cell : l) {
                    if (count(l, cell) > 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void buildCells() {
        cells = new ArrayList<>();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                cells.add(new SudokuCell(r, c, this));
            }
        }
    }

    private boolean validInput(int val) {
        return (val >= 1 && (val <= ROWS || val <= COLUMNS));
    }

    private boolean isSquare(int num) {
        double root = Math.sqrt((double) num);
        return root - Math.floor(root) == 0;
    }

    private ArrayList<SudokuCell> getEmpty() {
        ArrayList<SudokuCell> list = new ArrayList<>();
        for (SudokuCell c : cells) {
            if(c.isEmpty()) {
                list.add(c);
            }
        }
        return list;
    }

    private int count(SudokuCell[] arr, SudokuCell cell) {
        int count = 0;
        for (SudokuCell c : arr) {
            if (c.isEmpty()) {
                continue;
            }
            if (c.get() == cell.get()) {
                count++;
            }
        }
        return count;
    }

}
