package SummerInternshipCourse;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

class SudokoSolverGUI {
    private JFrame frame;
    private JPanel puzzlePanel;
    private JTextField[][] puzzleGrid;
    private JButton solveButton;
    private JButton restartButton;

    public SudokoSolverGUI() {
        frame = new JFrame("Sudoku Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        // Create a label for the heading
        JLabel headingLabel = new JLabel("SUDOKU SOLVER GAME", SwingConstants.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(headingLabel, BorderLayout.NORTH);

        puzzlePanel = new JPanel(new GridLayout(9, 9));
        puzzleGrid = new JTextField[9][9];

        // Define a larger font size
        Font largerFont = new Font("Arial", Font.PLAIN, 16); // You can adjust the size as needed

        // Initialize the puzzle grid with text fields and empty borders for separation
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                puzzleGrid[i][j] = new JTextField(1);
                puzzleGrid[i][j].setHorizontalAlignment(JTextField.CENTER);
                puzzleGrid[i][j].setBorder(createCellBorder(i, j));

                // Set the font for the text field
                puzzleGrid[i][j].setFont(largerFont);
                puzzlePanel.add(puzzleGrid[i][j]);
            }
        }

        solveButton = new JButton("Solve");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isInputProvided()) {
                    JOptionPane.showMessageDialog(frame, "Please provide input values in the puzzle.", "Input Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Check for invalid input
                if (!isInputValid()) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter numbers from 1 to 9.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Create a 2D char array to represent the puzzle
                char[][] board = new char[9][9];
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        String value = puzzleGrid[i][j].getText();
                        if (!value.isEmpty()) {
                            board[i][j] = value.charAt(0);
                            // Set the text color of input digits (e.g., blue)
                            puzzleGrid[i][j].setForeground(Color.BLUE);
                        } else {
                            board[i][j] = '.';
                        }
                    }
                }

                // Solve the Sudoku puzzle
                SudokoSolverGame solver = new SudokoSolverGame();
                solver.solveSudoku(board);

                // Update the GUI with the solved puzzle and set the text color of solved digits (e.g., red)
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        String currentValue = puzzleGrid[i][j].getText();
                        String solvedValue = Character.toString(board[i][j]);

                        // Set the text color of solved digits (e.g., red) and distinguish from input
                        if (!currentValue.equals(solvedValue)) {
                            puzzleGrid[i][j].setText(solvedValue);
                            puzzleGrid[i][j].setForeground(Color.RED);
                        }
                    }
                }
            }
        });

        restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the puzzle grid
                clearGrid();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(solveButton);
        buttonPanel.add(restartButton);

        frame.add(puzzlePanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private boolean isInputValid() {
        // Check if input values are valid (within the range 1-9) and do not have repetitions
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String value = puzzleGrid[i][j].getText();
                if (!value.isEmpty()) {
                    int num = Integer.parseInt(value);
                    if (num < 1 || num > 9) {
                        return false;
                    }

                    // Check for repetitions in the same row
                    for (int k = 0; k < 9; k++) {
                        if (k != j && value.equals(puzzleGrid[i][k].getText())) {
                            return false;
                        }
                    }

                    // Check for repetitions in the same column
                    for (int k = 0; k < 9; k++) {
                        if (k != i && value.equals(puzzleGrid[k][j].getText())) {
                            return false;
                        }
                    }

                    // Check for repetitions in the 3x3 grid
                    int startRow = (i / 3) * 3;
                    int startCol = (j / 3) * 3;
                    for (int row = startRow; row < startRow + 3; row++) {
                        for (int col = startCol; col < startCol + 3; col++) {
                            if (row != i && col != j && value.equals(puzzleGrid[row][col].getText())) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isInputProvided() {
        // Check if at least one input field is provided
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!puzzleGrid[i][j].getText().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }


    private void clearGrid() {
        // Clear all text fields in the puzzle grid
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                puzzleGrid[i][j].setText("");
            }
        }
    }

    // Create an empty border with top and left insets to separate the 3x3 subgrids
    private Border createCellBorder(int row, int col) {
        int top = 0;
        int left = 0;

        if (row % 3 == 0) {
            top = 2;
        }

        if (col % 3 == 0) {
            left = 2;
        }
        return BorderFactory.createMatteBorder(top, left, 1, 1, Color.BLACK);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SudokoSolverGUI();
            }
        });
    }
}

class SudokoSolver {
    public void solveSudoku(char[][] board) {
        int[] complete = new int[1];
        solver(board, 0, 0, complete);
    }

    public void solver(char[][] board, int row, int col, int[] complete) {
        if (row == board.length) {
            complete[0] = 1;
            return;
        }

        if (col == board[0].length) {
            solver(board, row + 1, 0, complete);
            return;
        }

        if (board[row][col] == '.') {
            for (char ch = '1'; ch <= '9'; ch++) {
                if (isValid(board, row, col, ch)) {
                    board[row][col] = ch;
                    solver(board, row, col + 1, complete);

                    if (complete[0] != 1) {
                        board[row][col] = '.';
                    } else {
                        return;
                    }
                }
            }
        } else {
            solver(board, row, col + 1, complete);
        }
    }

    public boolean isValid(char[][] board, int row, int col, char ch) {
        // Check row
        for (int i = 0; i < 9; i++) {
            if (board[i][col] == ch) {
                return false;
            }
        }

        // Check column
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == ch) {
                return false;
            }
        }

        // Check 3x3 grid
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == ch) {
                    return false;
                }
            }
        }
        return true;
    }
}
