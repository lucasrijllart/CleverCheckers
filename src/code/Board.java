package code;


import javax.swing.table.AbstractTableModel;


/**
 * Board class
 * @author Lucas Rijllart
 * @version 0.1
 */
public class Board extends AbstractTableModel {
    private final int		width;
    private final int		height;
    private Cell[][]  board;
    private CheckersGame game;

    /**
     * Create a new Board, which represents the board with the pieces
     * @param game  The game logic and actions
     * @param width  Board width
     * @param height  Board height
     */
    Board(CheckersGame game, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
    }

    /**
     * Reset game state and notify the GUI
     */
    void reset() {
        //createAltBoard();
        createStartBoard();
    }

    private void createStartBoard() {
        board = new Cell[width][height];
        for (int y=0; y<width; y++) { //fill board with cells
            for (int x=0; x<height; x++) {
                board[x][y] = new Cell(x, y, board);
            }
        }
        //setting even cells with a lighter colour
        for (int row = 0; row < height; row += 1) {
            for (int column = 0; column < width; column += 2) {
                setCellLighter(column, row);
            }
            row += 1;
            for (int column = 1; column < width; column += 2) {
                setCellLighter(column, row);
            }
        }

        //white pieces
        for (int i = 1; i < width; i += 2) { setCellBlack(i, 0, false); }
        for (int i = 0; i < 8; i += 2) { setCellBlack(i, 1, false); }
        for (int i = 1; i < 8; i += 2) { setCellBlack(i, 2, false); }

        //black pieces
        for (int i = 0; i < width; i += 2) { setCellBlack(i, 5, true); }
        for (int i = 1; i < 8; i += 2) { setCellBlack(i, 6, true); }
        for (int i = 0; i < 8; i += 2) { setCellBlack(i, 7, true); }
    }

    private void setCellBlack(int x, int y, boolean black) {
        Cell c = board[x][y];
        if(black)
            c.setBlack();
        else
            c.setWhite();
    }

    private void setCellLighter(int x, int y) {
        Cell c = board[x][y];
        c.setLighter();
    }

    int[][] getBoardData() {
        int[][] boardData = new int[8][8];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell c = board[x][y];
                if (c.isBlack()) {
                    if (c.isKing())
                        boardData[x][y] = 3;
                    else
                        boardData[x][y] = 1;
                } else if (c.isWhite()) {
                    if (c.isKing())
                        boardData[x][y] = 4;
                    else
                        boardData[x][y] = 2;
                } else {
                    boardData[x][y] = 0;
                }
            }
        }
        return boardData;
    }

    Cell[][] getBoard() {
        return board;
    }

    void updateTable() {
        game.updateTable();
    }

    /**
     * How high is the grid, for AbstractTableModel
     * @return rows
     */
    @Override
    public int getRowCount() {
        return height;
    }

    /**
     * How wide is the grid, for AbstractTableModel
     * @return rows
     */
    @Override
    public int getColumnCount() {
        return width;
    }

    /**
     * Get the object at the given coordinates, for AbstractTableModel
     * Will always be a Cell
     * @return the cell
     */
    @Override
    public Cell getValueAt(int rowIndex, int columnIndex) {return board[rowIndex][columnIndex];}

    /**
     * Get the class of the objects in a given column, for AbstractTableModel
     * Will always be the Cell class
     * @return the class
     */
    @Override
    public Class getColumnClass(int columnIndex) {
        return Cell.class;
    }

    /**
     * Is a cell editable, for AbstractTableModel
     * @return false
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

}
