package code;


import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;


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
        createStartBoard();
    }

    private void createStartBoard() {
        board = new Cell[width][height];
        for (int y=0; y<width; y++) { //fill board with cells
            for (int x=0; x<height; x++) {
                System.out.print("["+x+","+y+"]");
                board[x][y] = new Cell(x, y, this);
            }
            System.out.println();
        }
        System.out.println();
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
                if (board[x][y].isBlack()) {
                    boardData[x][y] = 2;
                } else if (board[x][y].isWhite()) {
                    boardData[x][y] = 1;
                } else {
                    boardData[x][y] = 0;
                }
            }
        }
        return boardData;
    }

    ArrayList<Cell> getPieces(int black1white2) {
        ArrayList<Cell> output = new ArrayList<>();
        boolean black = black1white2 == 1;

        for (int y=0; y<width; y++) {
            for (int x = 0; x < height; x++) {
                Cell c = board[x][y];
                if (c.isBlack() == black && !c.isFree()) {
                    output.add(c);
                }
            }
        }
        return output;
    }

    void printBoardData() {
        int[][] boardData = getBoardData();
        int rowNum = 8;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(boardData[x][y] + " ");
            }
            System.out.print("| " + rowNum);
            rowNum -= 1;
            System.out.println();
        }
        System.out.println("----------------");
        System.out.println("a b c d e f g h");
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
