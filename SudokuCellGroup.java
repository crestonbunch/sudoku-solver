import java.util.ArrayList;

/**
 * Represents a collection of Sudoku cells which must all be unique.
 *
 * @author  Creston Bunch
 * @version 1.0
 */

public class SudokuCellGroup {

    ArrayList<SudokuCell> cells = new ArrayList<>();

    /**
     * Initialize the cell group with some number of cells.
     *
     * @param c the cells to add to the group
     */
    public SudokuCellGroup(ArrayList<SudokuCell> list) {
        cells = list;
    }

    /**
     * Check if each cell is unique.
     *
     * @return  true if every cell is unique
     */
    public boolean isValid() {
        return missingValues().size() == 0;
    }

    /**
     * Get conflicting cells.
     *
     * @return  an array list of cells that are duplicates of each other
     */
    public ArrayList<SudokuCell> conflictingCells() {
        ArrayList<SudokuCell> badCells = new ArrayList<>();
        for (SudokuCell cell : cells) {
            if (countDuplicates(cell) > 1 && !cell.isImmutable()) {
                badCells.add(cell);
            }
        }
        return badCells;
    }

    /**
     * Returns empty cells.
     *
     * @return an array list of empty cells
     */
    public ArrayList<SudokuCell> emptyCells() {
        ArrayList<SudokuCell> emptyCells = new ArrayList<>();
        for (SudokuCell cell : cells) {
            if (cell.isEmpty()) {
                emptyCells.add(cell);
            }
        }
        return emptyCells;
    }

    /**
     * Return missing cell values
     *
     * @return  an ArrayList of the values missing from the group
     */
    public ArrayList<Integer> missingValues() {
        ArrayList<Integer> values = new ArrayList<>();
        // construct a list of values [1, cells.size()]
        for (int i = 1; i <= cells.size(); i++) {
            values.add(i);
        }
        // remove existing values from the list
        for (SudokuCell cell : cells) {
            values.remove(new Integer(cell.get()));
        }
        return values;
    }

    private int countDuplicates(SudokuCell cell) {
        int count = 0;
        for (SudokuCell c : cells) {
            if (c.equals(cell)) {
                count++;
            }
        }
        return count;
    }

}
