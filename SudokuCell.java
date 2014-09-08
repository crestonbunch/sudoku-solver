import java.util.ArrayList;

/**
 * Represents a single input in a SudokuGrid
 *
 * @author  Creston Bunch
 * @version 1.0
 */

public class SudokuCell {
    
    private int value = 0;
    private boolean immutable = false;
    private ArrayList<Integer> values;
    private ArrayList<Integer> tries;
    private SudokuGrid grid;
    private int row;
    private int col;

    /**
     * Creates an empty Sudoku cell.
     *
     * @param row the row the cell is in
     * @param col the column the cell is in
     */
    public SudokuCell(int row, int col, SudokuGrid g) {
        this.grid = g;
        this.row = row;
        this.col = col;
        this.tries = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    /**
     * Assigns the cell a value and make it immutable.
     *
     * @param val   the value to set
     */
    public void freeze(int val) {
        set(val);
        immutable = true;
    }

    /**
     * Sets the value of the cell if it is not immutable.
     * 
     * @param val   the value to set
     */
    public void set(int val) {
        if (!immutable) {
            this.value = val;
        }
    }

    /**
     * Gets the value of the cell.
     * 
     * @return  the value of the cell
     */
    public int get() {
        return this.value;
    }

    /**
     * Get the row this cell is is.
     *
     * @return the row this cell is in
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Get the column this cell is in.
     *
     * @return the column this cell is in
     */
    public int getCol() {
        return this.col;
    }

    /**
     * Check if this cell is empty.
     */
    public boolean isEmpty() {
        return value == 0;
    }

    /**
     * Check if cell is immutable.
     */
    public boolean isImmutable() {
        return immutable;
    }

    /**
     * Update the possible values of this sudoku cell.
     */
    public void refresh() {
        values = new ArrayList<>();

        for (int i = 1; i <= grid.ROWS; i++) {
            if (!tries.contains(new Integer(i))) {
                values.add(i);
            }
        }

        for (SudokuCell c : grid.getRow(row)) {
            if (c != this && !c.isEmpty()) {
                values.remove(new Integer(c.get()));
            }
        }
        for (SudokuCell c : grid.getCol(col)) {
            if (c != this && !c.isEmpty()) {
                values.remove(new Integer(c.get()));
            }
        }
        for (SudokuCell c : grid.getBlock(row, col)) {
            if (c != this && !c.isEmpty()) {
                values.remove(new Integer(c.get()));
            }
        }
    }

    /**
     * Check if this cell is impossible.
     *
     * @return true if there are no possible values
     */
    public boolean isImpossible() {
        if (isImmutable()) {
            return false;
        }
        return values.size() == 0;
    }

    /**
     * Assigns the first value in the list of possible
     * values to this cell.
     */
    public void assign() {
        if (!isImmutable()) {
            int val = values.remove(0);
            set(val);
            // add this number to tries
            tries.add(this.get());
        }
    }

    /**
     * Resets the tried numbers.
     */
    public void reset() {
        this.value = 0;
        this.tries = new ArrayList<>();
    }
}
