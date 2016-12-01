package code;

import java.util.ArrayList;

/**
 * Player interface, contains methods that Human and AI classes need to implement
 *
 * @author Lucas
 * @version 0.2
 */
public interface Player {

    /**
     * Method to try and make move on the board, checking for exceptions
     * @param selected selected cell to move piece from
     * @param target selected cell to move piece to
     * @throws MoveException piece movement exception for invalid moves
     */
    String tryMove(Cell selected, Cell target) throws MoveException;

    /**
     * Makes AI come up with move then try it
     */
    void makeMove() throws GameException;

    /**
     *
     * @return human
     */
    boolean isHuman();

    /**
     *
     * @return name
     */
    String getName();

    /**
     *
     * @return difficulty
     */
    String getDifficulty();
}

/**
 * Player functions, methods used by minimax and AIs
 */
class PlayerFunctions {


    /**
     * Gets available moves for a player
     * @param board reference for cells
     * @param number player number
     * @return ArrayList of int[] with xPos, yPos, target xPos, target yPos
     */
     ArrayList<int[]> getAvailableMoves(Cell[][] board, int number) {
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        ArrayList<Cell> allPieces;

        //get a list of all pieces owned by player
        allPieces = getPieces(board, number);

        //for each cell, check if it has possible moves
        for (Cell c : allPieces) {
            int[] tryMove;
            if (c.isKing()) {
                tryMove = c.canKingMoveDownLeft();
                if (tryMove != null) possibleMoves.add(tryMove);
                tryMove = c.canKingMoveDownRight();
                if (tryMove != null) possibleMoves.add(tryMove);
            }
            tryMove = c.canMoveLeft();
            if (tryMove != null) possibleMoves.add(tryMove);
            tryMove = c.canMoveRight();
            if (tryMove != null) possibleMoves.add(tryMove);
        }
        return possibleMoves;
    }

    /**
     * Gets available takes for a player
     * @param board refrence for cells
     * @param number player number
     * @return ArrayList of int[] with xPos, yPos, target xPos, target yPos
     */
     ArrayList<int[]> getAvailableTakes(Cell[][] board, int number) {
        ArrayList<int[]> possibleTakes = new ArrayList<>();
        ArrayList<Cell> allPieces;

        //get a list of all pieces owned by player
        allPieces = getPieces(board, number);
        //for each cell, check if it has possible moves
        for (Cell c : allPieces) {
            int[] tryMove;
            if (c.isKing()) {
                tryMove = c.canKingTakeDownLeft();
                if (tryMove != null) possibleTakes.add(tryMove);
                tryMove = c.canKingTakeDownRight();
                if (tryMove != null) possibleTakes.add(tryMove);
            }
            tryMove = c.canTakeLeft();
            if (tryMove != null) possibleTakes.add(tryMove);
            tryMove = c.canTakeRight();
            if (tryMove != null) possibleTakes.add(tryMove);
        }
        return possibleTakes;
    }

    /**
     * Executes move on simulated board and assigns score. A take is worth 2 points.
     * Crowning is worth 2 points. The points are divided bu the depth.
     * @param board the board of int's
     * @param move the move that needs to be exectued
     * @param depth the depth of minimax
     * @return Object with board and score
     */
     Object[] getBoardAfterMove(int[][] board, int[] move, int depth) {
        boolean take = false;
        double score = 0.00;
        //check if move (can be king)
        if (move[0] == move[2]+1 || move[0] == move[2]-1) {
            board[move[2]][move[3]] = board[move[0]][move[1]]; //copy selected into target
            board[move[0]][move[1]] = 0; //clear selected
            if  ((board[move[0]][move[1]] == 3 && move[3] > move[1]) || (board[move[0]][move[1]] == 4 && move[3] < move[1])) {
                score += (50/depth); //score for king move 50
            }
            if ((board[move[2]][move[3]] == 1 && move[3] == 0) || (board[move[2]][move[3]] == 2 && move[3] == 7)) {
                score += (150/depth); //score for king 150
            }
        }
        //check if take
        else if (move[0] == move[2]+2 || move[0] == move[2]-2) {
            board[move[2]][move[3]] = board[move[0]][move[1]]; //copy selected into target
            board[move[0]][move[1]] = 0; //clear selected
            int x = (move[2] - move[0])/2;
            int y = (move[3] - move[1])/2;
            board[move[0]+x][move[1]+y] = 0; //clear taken cell
            score += (100/depth); //score for take 100
            if ((board[move[2]][move[3]] == 1 && move[3] == 0) || (board[move[2]][move[3]] == 2 && move[3] == 7)) {
                score += (150/depth); //score for king 150
            }
        }
        return new Object[]{board, score};
    }

    /**
     * Converts an int board to a Cell[][] board
     * @param board int board
     * @return Cell board
     */
     Cell[][] convertIntToBoard(int[][] board) {
        Cell[][] newBoard = new Cell[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Cell c = new Cell(x, y, newBoard);
                switch (board[x][y]) {
                    case 0:
                        c.setFree();
                        break;
                    case 1:
                        c.setBlack();
                        break;
                    case 2:
                        c.setWhite();
                        break;
                    case 3:
                        c.setBlack();
                        c.setKing();
                        break;
                    case 4:
                        c.setWhite();
                        c.setKing();
                        break;
                }
                newBoard[x][y] = c;
            }
        }
        return newBoard;
    }

    /**
     * Given the board of Cells and player number, returns an ArrayList of all player pieces.
     * @param board current board
     * @param black1white2 player number
     * @return ArrayList<Cell> of all pieces of the player
     */
     ArrayList<Cell> getPieces(Cell[][] board, int black1white2) {
        ArrayList<Cell> output = new ArrayList<>();
        boolean black = black1white2 == 1;
        for (int y=0; y<8; y++) {
            for (int x = 0; x < 8; x++) {
                Cell c = board[x][y];
                if (c.isBlack() == black && !c.isFree()) {
                    output.add(c);
                }
            }
        }
        return output;
    }

    /**
     * Copies the data from the board to create a new simulation
     * @param board input board
     * @return output board
     */
    int[][] copyData(int[][] board) {
        int[][] output = new int[8][8];
        int temp;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                temp = board[x][y];
                output[x][y] = temp;
            }
        }
        return output;
    }

    void printBoardData(int[][] boardData) {
        int rowNum = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                System.out.print(boardData[x][y] + " ");
            }
            System.out.print("| " + rowNum);
            rowNum += 1;
            System.out.println();
        }
        System.out.println("----------------");
        System.out.println("0 1 2 3 4 5 6 7");
    }
}

/**
 * Class that stores the move and the score, for minimax
 */
class MoveAndScore {
    int[] move;
    double score;
    MoveAndScore(int[] move, double score) {
        this.move = move;
        this.score = score;
    }
}