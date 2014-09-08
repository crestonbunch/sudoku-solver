import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Creates a graphical interface to solve Sudoku puzzles.
 *
 * @author  Creston Bunch
 * @version 1.0
 */

public class SudokuSolver extends JFrame {

    private JTextField[][] fields;
    private JButton button;
    private SudokuGrid grid;

    /**
     * Initializes the graphical interface and components.
     */
    public SudokuSolver() {
        super("Sudoku Solver");

        grid = new SudokuGrid();

        fields = new JTextField
                 [grid.COLUMNS]
                 [grid.ROWS];

        // creates the solve button
        button = new JButton("Solve");

        setLayout(new GridBagLayout());

        // build input array
        buildInputArray();

        // add solve button
        buildSolveButton();

        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void buildInputArray() {
        GridBagConstraints con = new GridBagConstraints();
        con.weightx = 0.5;
        con.weighty = 0.5;
        con.fill = GridBagConstraints.HORIZONTAL;
        for (int r = 0; r < fields.length; r++) {
            for (int c = 0; c < fields[r].length; c++) {

                int padx = 0;
                int pady = 0;
                if ((r + 1) % Math.sqrt(fields.length) == 0
                && r < fields.length - 1) {
                    pady = 10;
                }
                if ((c + 1) % Math.sqrt(fields.length) == 0
                && c < fields[r].length - 1) {
                    padx = 10;
                }

                fields[r][c] = new JTextField(2);
                con.insets = new Insets(0, 0, pady, padx);
                con.gridy = r;
                con.gridx = c;
                this.add(fields[r][c], con);
            }
        }
    }

    private void buildSolveButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = grid.ROWS;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = grid.COLUMNS;
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                solveSudoku();
            }
        });
        this.add(button, c);
    }

    private void solveSudoku() {
        for (int x = 0; x < fields.length; x++) {
            for (int y = 0; y < fields[x].length; y++) {
                JTextField f = fields[x][y];
                String s = f.getText();
                grid.set(x, y, s);
            }
        }
        grid.solve();
        for (int x = 0; x < fields.length; x++) {
            for (int y = 0; y < fields[x].length; y++) {
                SudokuCell cell = grid.getCellAt(x, y);
                String s = (new Integer(cell.get())).toString();
                fields[x][y].setText(s);
            }
        }
    }

    public void write(int x, int y, int val) {
        fields[x][y].setText((new Integer(val)).toString());
    }

    public static void main(String[] args) {
        new SudokuSolver();
    }

}
