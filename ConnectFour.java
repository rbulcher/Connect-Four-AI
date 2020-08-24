
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

public class ConnectFour extends JFrame {
	private boolean turn = true;
	private String player = "1";
	private int p1 = 0;
	private int p2 = 0;
	private int hum = 1;
	private int algo = 2;

	public static void main(String[] args) {
		ConnectFour board = new ConnectFour(6, 7);
	}

	private int rows, columns;
	private JPanel cellPanel, buttonPanel, boxPanel;
	private JCheckBox human = new JCheckBox("PvP ");
	private JCheckBox ai = new JCheckBox("Ai");
	private Cell[][] cells;
	private JButton[] columnButtons;
	private static int CELL_SIZE = 70; // adjust this for larger or smaller cells

	/**
	 * Constructs a Connect 4 grid with a specified number of rows and columns.
	 * 
	 * @param rows    the number of rows of squares
	 * @param columns the number of columns of squares
	 */
	public ConnectFour(int rows, int columns) {
		super("Start Game...");
		this.rows = rows;
		this.columns = columns;

		frameSetup();
		cellPanelSetup();
		buttonPanelSetup();
		pack();
		setVisible(true);
	}

	/**
	 * Sets up the details for the frame.
	 */
	private void frameSetup() {
		setLayout(new BorderLayout());
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	/**
	 * Sets up the details for the square panel itself.
	 */
	private void cellPanelSetup() {
		// The layout is based on the number of rows
		cellPanel = new JPanel(new GridLayout(rows, 0));
		cellPanel.setPreferredSize(new Dimension(CELL_SIZE * columns, CELL_SIZE * rows));
		// Each square in the grid is a Square object
		cells = new Cell[rows][columns];
		for (int row = 0; row < cells.length; row++) {
			for (int cell = 0; cell < cells[0].length; cell++) {
				cells[row][cell] = new Cell();
				cellPanel.add(cells[row][cell]);
			}
		}
		add(cellPanel, BorderLayout.CENTER);

		JCheckBox[] boxes = new JCheckBox[2];
		human.setSelected(true);
		boxes[0] = ai;
		boxes[1] = human;
		boxPanel = new JPanel(new GridLayout(1, 0));
		for (int i = 0; i < boxes.length; i++)
			boxPanel.add(boxes[i]);
		add(boxPanel, BorderLayout.SOUTH);
	}

	/**
	 * Sets up the buttons at the top of each column
	 */
	private void buttonPanelSetup() {
		buttonPanel = new JPanel(new GridLayout(1, 0));
		columnButtons = new JButton[columns];

		for (int i = 0; i < columns; i++) {
			columnButtons[i] = new JButton("" + i);
			columnButtons[i].setName("button" + i);
			columnButtons[i].addActionListener((new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int selectedColumn = Integer.parseInt(e.getActionCommand());
					// player playing against another player
					if (human.isSelected()) {
						humanPlay(selectedColumn);
					} else {
						// player playing against algorithm
						aiPlay(selectedColumn);
					}
				}

			}));

			columnButtons[i].setToolTipText("Move in column " + i);
			buttonPanel.add(columnButtons[i]);
		}
		add(buttonPanel, BorderLayout.NORTH);

		// calls every round to determine if user changes game mode
		gameSwitch();

	}

	private int getScores(int x) {
		if (x == algo)
			return 10000;
		else
			return -10000;
	}

	private void gameSwitch() {
		human.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				p1 = 0;
				p2 = 0;
				if (e.getStateChange() == ItemEvent.SELECTED) {
					reset();
					ai.setSelected(false);
					setTitle("[Connect Four]                   Player 1: " + p1 + "              Human: " + p2);
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					reset();
					ai.setSelected(true);
					setTitle("[Connect Four]                   Player 1: " + p1 + "              Ai: " + p2);
				}
			}
		});
		ai.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				p1 = 0;
				p2 = 0;
				if (e.getStateChange() == ItemEvent.SELECTED) {
					human.setSelected(false);
					reset();
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					human.setSelected(true);
					reset();
				}
			}
		});
	}

	private void aiPlay(int col) {
		// Player Playing vs AI (minimax algorithm)
		setTitle("[Connect Four]                   Player 1: " + p1 + "              Ai: " + p2);
		int selectedCol = col;
		for (int i = rows - 1; i >= 0; i--) {
			if (cells[i][selectedCol].getPlayer() == 0) {
				// statusUpdate(player, Integer.parseInt(player));
				cells[i][selectedCol].setPlayer(hum);
				// call Ai Operation

				bestMove();

				if (gameOver()) {
					String winner = "";
					if (boardFilled())
						winner = "Draw";
					else {
						player = !turn ? "1" : "2";
						if (Integer.parseInt(player) == 1)
							p1++;
						else
							p2++;
						setTitle("[Connect Four]                   Player 1: " + p1 + "             Ai: " + p2);
						if (player == "2")
							winner = "Ai is the winner!";
						else
							winner = "Player is the winner!";
					}
					JOptionPane.showMessageDialog(null, winner);
					reset();
				}
				break;
			}
		}
	}

	private void humanPlay(int col) {
		setTitle("[Connect Four]                   Player 1: " + p1 + "             Player 2: " + p2);
		int selectedCol = col;
		for (int i = rows - 1; i >= 0; i--) {
			if (cells[i][selectedCol].getPlayer() == 0) {
				turn = !turn;
				player = turn ? "1" : "2";
				// statusUpdate(player, Integer.parseInt(player));
				cells[i][selectedCol].setPlayer(Integer.parseInt(player));
				if (gameOver()) {
					String winner = "";
					if (boardFilled())
						winner = "Draw";
					else {
						player = !turn ? "1" : "2";
						if (Integer.parseInt(player) == 1)
							p1++;
						else
							p2++;
						setTitle("[Connect Four]                   Player 1: " + p1 + "             Player 2: " + p2);
						winner = player + " is the winner!";

					}
					// statusUpdate(winner, 1);
					if (!winner.equals("Draw"))
						JOptionPane.showMessageDialog(null, "Player " + winner);
					else
						JOptionPane.showMessageDialog(null, winner);
					reset();
				}
				break;
			}
		}
	}

	// AI plays here
	private void bestMove() {
		int bestScore = (int) Double.NEGATIVE_INFINITY;
		int moveRow = 0;
		int moveCol = 0;
		int temp = 0;
		int depth = 5;

		for (int j = 0; j < columns; j++) {
			temp = nextSpace(j);

			if (temp >= 0) {
				cells[temp][j].setPlayer(algo);
				int score = minimax(cells, depth, false, 1);
				cells[temp][j].setPlayer(0);

				if (score > bestScore) {
					bestScore = score;
					moveRow = j;
					moveCol = temp;
				}
			}
		}

		cells[moveCol][moveRow].setPlayer(algo);
	}

	private int minimax(Cell[][] board, int depth, boolean isMaximizing, int numMoves) {
		int result = getWinner();
		if (result > 0) {
			return getScores(result) - 20 * numMoves;
		}
		if (result == -1) {
			return 0 - 50 * numMoves;
		}

		if (depth == 0) {
			return score_position(algo, hum);
		}

		if (isMaximizing) {
			int bestScore = -10000000;
			for (int i = 0; i < columns; i++) {
				int temp = nextSpace(i);
				if (temp < rows && temp > -1) {
					cells[temp][i].setPlayer(algo);

					int score = minimax(cells, depth - 1, false, numMoves + 1);
					cells[temp][i].setPlayer(0);
					bestScore = Math.max(score, bestScore);
				}
			}
			return bestScore;
		} else {
			int bestScore = 100000000;
			for (int i = 0; i < columns; i++) {
				int temp = nextSpace(i);
				if (temp < rows && temp > -1) {
					cells[temp][i].setPlayer(hum);

					int score = minimax(cells, depth - 1, true, numMoves + 1);
					cells[temp][i].setPlayer(0);

					bestScore = Math.min(score, bestScore);
				}
			}
			return bestScore;
		}

	}

	private int countPieces(int i, int j, int i2, int j2, int player) {
		int pieces = 0;

		for (int x = i; x < i2; x++) {
			for (int y = j; y < j2; y++) {
				if (cells[x][y].getPlayer() == player) {
					pieces += 1;
				}

			}

		}
		return pieces;
	}

	private int countDiagonal(int i, int j, int direction, int player) {

		int pieces = 0;

		for (int x = 0; x < 4; x++) {
			if (direction == 1) {
				if (i + x < rows && j + x < columns) {
					if (cells[i + x][j + x].getPlayer() == player) {
						pieces += 1;
					}
				}

			} else {
				if (i + x < rows && j - x < columns && j - x > 0) {
					if (cells[i + x][j - x].getPlayer() == player) {
						pieces += 1;
					}

				}

			}

		}
		return pieces;
	}

	private int score_position(int p1, int p2) {
		int score = 0;
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				if ((countPieces(i, j, i + 4, j, p1) == 3 && countPieces(i, j, i + 4, j, 0) == 1)
						|| (countPieces(i, j, i, j + 4, p1) == 3 && countPieces(i, j, i, j + 4, 0) == 1)
						|| (countDiagonal(i, j, 0, p1) == 3 && countDiagonal(i, j, 1, 0) == 1)) {

					score += 1000;
				}

				if ((countPieces(i, j, i + 4, j, p1) == 2 && countPieces(i, j, i + 4, j, 0) == 2)
						|| (countPieces(i, j, i, j + 4, p1) == 2 && countPieces(i, j, i, j + 4, 0) == 2)
						|| (countDiagonal(i, j, 0, p1) == 2 && countDiagonal(i, j, 1, 0) == 2)) {

					score += 10;
				}

				if ((countPieces(i, j, i + 4, j, p1) == 1 && countPieces(i, j, i + 4, j, 0) == 3)
						|| (countPieces(i, j, i, j + 4, p1) == 1 && countPieces(i, j, i, j + 4, 0) == 3)
						|| (countDiagonal(i, j, 0, p1) == 1 && countDiagonal(i, j, 1, 0) == 3)) {

					score += 1;

				}

				if ((countPieces(i, j, i + 4, j, p2) == 3 && countPieces(i, j, i + 4, j, 0) == 1)
						|| (countPieces(i, j, i, j + 4, p2) == 3 && countPieces(i, j, i, j + 4, 0) == 1)
						|| (countDiagonal(i, j, 0, p2) == 3 && countDiagonal(i, j, 1, 0) == 1)) {
					score -= 1000;

				}

				if ((countPieces(i, j, i + 4, j, p2) == 2 && countPieces(i, j, i + 4, j, 0) == 2)
						|| (countPieces(i, j, i, j + 4, p2) == 2 && countPieces(i, j, i, j + 4, 0) == 2)
						|| (countDiagonal(i, j, 0, p2) == 2 && countDiagonal(i, j, 1, 0) == 2)) {
					score -= 10;
				}

				if ((countPieces(i, j, i + 4, j, p2) == 1 && countPieces(i, j, i + 4, j, 0) == 3)
						|| (countPieces(i, j, i, j + 4, p2) == 1 && countPieces(i, j, i, j + 4, 0) == 3)
						|| (countDiagonal(i, j, 0, p2) == 1 && countDiagonal(i, j, 1, 0) == 3)) {
					score -= 1;

				}
			}
		}
		return score;
	}

	private int nextSpace(int x) {
		for (int y = rows - 1; y >= 0; y--) {
			if (cells[y][x].getPlayer() == 0) {
				return y;
			}
		}
		return -1;
	}

	private boolean boardFilled() {
		int count = 0;
		for (int i = 0; i < columns; i++)
			if (cells[0][i].getPlayer() != 0)
				count++;
		if (count == columns)
			return true;

		return false;
	}

	private boolean gameOver() {

		// horizontal Check
		for (int j = 0; j < columns - 3; j++) {
			for (int i = 0; i < rows; i++) {
				if (cells[i][j].getPlayer() != 0 && cells[i][j + 1].getPlayer() == cells[i][j].getPlayer()
						&& cells[i][j + 2].getPlayer() == cells[i][j].getPlayer()
						&& cells[i][j + 3].getPlayer() == cells[i][j].getPlayer()) {
					cells[i][j].setBackground(Color.green);
					cells[i][j + 1].setBackground(Color.green);
					cells[i][j + 2].setBackground(Color.green);
					cells[i][j + 3].setBackground(Color.green);
					return true;
				}
			}
		}
		// vertical Check
		for (int i = 0; i < rows - 3; i++) {
			for (int j = 0; j < columns; j++) {
				if (cells[i][j].getPlayer() != 0 && cells[i + 1][j].getPlayer() == cells[i][j].getPlayer()
						&& cells[i + 2][j].getPlayer() == cells[i][j].getPlayer()
						&& cells[i + 3][j].getPlayer() == cells[i][j].getPlayer()) {
					cells[i][j].setBackground(Color.green);
					cells[i + 1][j].setBackground(Color.green);
					cells[i + 2][j].setBackground(Color.green);
					cells[i + 3][j].setBackground(Color.green);
					return true;
				}
			}
		}
		// ascending Diagonal Check
		for (int i = 3; i < rows; i++) {
			for (int j = 0; j < columns - 3; j++) {
				if (cells[i][j].getPlayer() != 0 && cells[i - 1][j + 1].getPlayer() == cells[i][j].getPlayer()
						&& cells[i - 2][j + 2].getPlayer() == cells[i][j].getPlayer()
						&& cells[i - 3][j + 3].getPlayer() == cells[i][j].getPlayer()) {
					cells[i][j].setBackground(Color.green);
					cells[i - 1][j + 1].setBackground(Color.green);
					cells[i - 2][j + 2].setBackground(Color.green);
					cells[i - 3][j + 3].setBackground(Color.green);
					return true;
				}

			}
		}
		// descending Diagonal Check
		for (int i = 3; i < rows; i++) {
			for (int j = 3; j < columns; j++) {
				if (cells[i][j].getPlayer() != 0 && cells[i - 1][j - 1].getPlayer() == cells[i][j].getPlayer()
						&& cells[i - 2][j - 2].getPlayer() == cells[i][j].getPlayer()
						&& cells[i - 3][j - 3].getPlayer() == cells[i][j].getPlayer()) {
					cells[i][j].setBackground(Color.green);
					cells[i - 1][j - 1].setBackground(Color.green);
					cells[i - 2][j - 2].setBackground(Color.green);
					cells[i - 3][j - 3].setBackground(Color.green);
					return true;
				}

			}

		}
		return false;
	}

	private int getWinner() {
		// horizontal Check
		for (int j = 0; j < columns - 3; j++) {
			for (int i = 0; i < rows; i++) {
				if (cells[i][j].getPlayer() != 0 && cells[i][j + 1].getPlayer() == cells[i][j].getPlayer()
						&& cells[i][j + 2].getPlayer() == cells[i][j].getPlayer()
						&& cells[i][j + 3].getPlayer() == cells[i][j].getPlayer()) {
					return cells[i][j].getPlayer();
				}
			}
		}
		// vertical Check
		for (int i = 0; i < rows - 3; i++) {
			for (int j = 0; j < columns; j++) {
				if (cells[i][j].getPlayer() != 0 && cells[i + 1][j].getPlayer() == cells[i][j].getPlayer()
						&& cells[i + 2][j].getPlayer() == cells[i][j].getPlayer()
						&& cells[i + 3][j].getPlayer() == cells[i][j].getPlayer()) {
					return cells[i][j].getPlayer();
				}
			}
		}
		// ascending Diagonal Check
		for (int i = 3; i < rows; i++) {
			for (int j = 0; j < columns - 3; j++) {
				if (cells[i][j].getPlayer() != 0 && cells[i - 1][j + 1].getPlayer() == cells[i][j].getPlayer()
						&& cells[i - 2][j + 2].getPlayer() == cells[i][j].getPlayer()
						&& cells[i - 3][j + 3].getPlayer() == cells[i][j].getPlayer())
					return cells[i][j].getPlayer();
			}
		}
		// descending Diagonal Check
		for (int i = 3; i < rows; i++) {
			for (int j = 3; j < columns; j++) {
				if (cells[i][j].getPlayer() != 0 && cells[i - 1][j - 1].getPlayer() == cells[i][j].getPlayer()
						&& cells[i - 2][j - 2].getPlayer() == cells[i][j].getPlayer()
						&& cells[i - 3][j - 3].getPlayer() == cells[i][j].getPlayer())
					return cells[i][j].getPlayer();
			}
		}

		// tie
		if (boardFilled())
			return -1;

		return 0;
	}

	private void reset() {
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++) {
				cells[i][j].setPlayer(0);
				cells[i][j].resetBackground();
			}
		turn = true;
	}

	/**
	 * Updates a specified square by indicating which player should now occupy that
	 * square
	 * 
	 * @param row    the row of the square
	 * @param col    the column of the square
	 * @param player which player should occupy that square (0=empty, 1=player1,
	 *               2=player2)
	 */
	public void updateCell(int row, int col, int player) {
		cells[row][col].setPlayer(player);
	}
}
