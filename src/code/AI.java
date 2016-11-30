package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Class for AI players, does actions through the GUI
 *
 * @author Lucas
 * @version 0.2
 */
class AI implements Player {
    private int number;
    private int opponentNum;
    private Board board;
    private String name;
    private int difficulty;

    //vars for minimax
    private int[][] currentBoard;
    private ArrayList<MoveAndScore> successorEvaluations;
    private int maxDepth;

    //var for timing
    private long idealTime = 2000;

    AI(int number, Board board, String name, int difficulty) {
        this.number = number;
        if (number == 1)
            opponentNum = 2;
        else
            opponentNum = 1;
        this.board = board;
        this.name = name;
        this.difficulty = difficulty;
    }

    @Override
    public String tryMove(Cell selected, Cell target) throws MoveException {
        //check for move
        if (target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1) {
            if (target.getyPos() == selected.getyPos()+1) {
                target.move(selected);
            }
        }

        //check for take
        if (target.getxPos() == selected.getxPos()+2 || target.getxPos() == selected.getxPos()-2) {
            if (target.getyPos() == selected.getyPos()+2) {
                Cell takeTarget;
                if (target.getxPos() - selected.getxPos() > 0) // get right piece
                    takeTarget = board.getValueAt(selected.getxPos()+1, selected.getyPos()+1);
                else // get left piece
                    takeTarget = board.getValueAt(selected.getxPos()-1, selected.getyPos()+1);
                // check that piece is black
                target.move(selected);
                takeTarget.setFree();
            }
        }

        //check for move and take if king
        if (selected.isKing()) {
            //check for move in opposite direction
            if (target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1) {
                if (target.getyPos() == selected.getyPos()-1) {
                    target.move(selected);
                }
            }
            //check for take in opposite direction
            if (target.getxPos() == selected.getxPos()+2 || target.getxPos() == selected.getxPos()-2) {
                if (target.getyPos() == selected.getyPos() - 2) {
                    Cell takeTarget;
                    if (target.getxPos() - selected.getxPos() > 0) // get right piece
                        takeTarget = board.getValueAt(selected.getxPos() + 1, selected.getyPos() - 1);
                    else // get left piece
                        takeTarget = board.getValueAt(selected.getxPos() - 1, selected.getyPos() - 1);
                    // check that piece is black
                    target.move(selected);
                    takeTarget.setFree();
                }
            }
        }
        return name + " moved";
    }

    @Override
    public void makeMove() {
        switch (difficulty) {
            case 1: difficulty1(); break;
            case 2: difficulty2(); break;
            case 3: difficulty3(); break;
            case 4: difficulty4(); break;
            case 5: difficulty5(); break;
        }
    }

    /**
     * Difficulty 1 is random selection of all moves
     */
    private void difficulty1() {
        long startTime = System.currentTimeMillis();
        ArrayList<int[]> possibleMoves = getAvailableMoves(board.getBoard(), number);
        possibleMoves.addAll(getAvailableTakes(board.getBoard(), number));

        //for all takes, check if another is possible

        System.out.println("Moves:");
        for (int[] a : possibleMoves) {
            System.out.println(Arrays.toString(a));
        }

        int[] nextMove;
        if (possibleMoves.size() == 0) {
            //throw new game exception
            nextMove = null;
        } else if (possibleMoves.size() < 2) {
            nextMove = possibleMoves.get(0);
        } else {
            nextMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
        }
        System.out.println("AI Move:" + Arrays.toString(nextMove));

        long stopTime = System.currentTimeMillis();
        if (stopTime-startTime < idealTime) {
            try { Thread.sleep(idealTime - (stopTime-startTime)); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        try {
            tryMove(board.getValueAt(nextMove[0], nextMove[1]), board.getValueAt(nextMove[2], nextMove[3]));
        } catch (MoveException e) {
            System.out.println(e.getReason());
        }

    }

    /**
     * Difficulty 2 is random takes, and if no takes, random move
     */
    private void difficulty2() {
        long startTime = System.currentTimeMillis();
        ArrayList<int[]> possibleMoves = getAvailableMoves(board.getBoard(), number);
        ArrayList<int[]> possibleTakes = getAvailableTakes(board.getBoard(), number);

        //for all takes, check if another is possible

        System.out.println("Takes:");
        for (int[] a : possibleTakes) {
            System.out.println(Arrays.toString(a));
        }
        System.out.println("Moves:");
        for (int[] a : possibleMoves) {
            System.out.println(Arrays.toString(a));
        }

        int[] nextMove;
        if (possibleTakes.size() == 1) {
            nextMove = possibleTakes.get(0);
        } else if (possibleTakes.size() > 1) {
            nextMove = possibleTakes.get(new Random().nextInt(possibleTakes.size()));
        } else {
            nextMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
        }
        System.out.println("AI Move:" + Arrays.toString(nextMove));

        long stopTime = System.currentTimeMillis();
        if (stopTime-startTime < idealTime) {
            try { Thread.sleep(idealTime - (stopTime-startTime)); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        try {
            tryMove(board.getValueAt(nextMove[0], nextMove[1]), board.getValueAt(nextMove[2], nextMove[3]));
        } catch (MoveException e) {
            System.out.println(e.getReason());
        }
    }

    /**
     * Difficulty 3 is minimax with depth of 2
     */
    private void difficulty3() {
        long startTime = System.currentTimeMillis();
        currentBoard = board.getBoardData();
        successorEvaluations = new ArrayList<>();
        maxDepth = 3;

        minimax(1, currentBoard, number, Integer.MIN_VALUE, Integer.MAX_VALUE);

        //System.out.println("SUCCESSOR");
        int iter = 0;
        for (MoveAndScore ms : successorEvaluations) {
            System.out.println(iter + ": " + Arrays.toString(ms.move) + " |" + ms.score);
            iter+=1;
        }

        if (successorEvaluations.size() == 0) {
            //throw new GameException LOSS
        } else {
            int[] selectedMove = successorEvaluations.get(0).move;
            double bestScore = successorEvaluations.get(0).score;

            for (MoveAndScore ms : successorEvaluations) {
                if (ms.score > bestScore) selectedMove = ms.move;
            }
            //System.out.println("AI:" + Arrays.toString(selectedMove));

            long stopTime = System.currentTimeMillis();
            if (stopTime-startTime < idealTime) {
                try { Thread.sleep(idealTime - (stopTime-startTime)); } catch (InterruptedException e) { e.printStackTrace(); }
                System.out.println("Had to wait");
            }

            try {
                tryMove(board.getValueAt(selectedMove[0],selectedMove[1]), board.getValueAt(selectedMove[2],selectedMove[3]));
            } catch (MoveException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Difficulty 4 is minimax with depth of 6
     */
    private void difficulty4() {
        long startTime = System.currentTimeMillis();
        currentBoard = board.getBoardData();
        successorEvaluations = new ArrayList<>();
        maxDepth = 7;

        minimax(1, currentBoard, number, Integer.MIN_VALUE, Integer.MAX_VALUE);

        System.out.println("SUCCESSOR");
        int iter = 0;
        for (MoveAndScore ms : successorEvaluations) {
            System.out.println(iter + ": " + Arrays.toString(ms.move) + " |" + ms.score);
            iter+=1;
        }

        if (successorEvaluations.size() == 0) {
            //throw new GameException LOSS
        } else {
            int[] selectedMove = successorEvaluations.get(0).move;
            double bestScore = successorEvaluations.get(0).score;

            for (MoveAndScore ms : successorEvaluations) {
                if (ms.score > bestScore) selectedMove = ms.move;
            }
            System.out.println("AI:" + Arrays.toString(selectedMove));

            long stopTime = System.currentTimeMillis();
            if (stopTime-startTime < idealTime) {
                try { Thread.sleep(idealTime - (stopTime-startTime)); } catch (InterruptedException e) { e.printStackTrace(); }
                System.out.println("Had to wait");
            }

            try {
                tryMove(board.getValueAt(selectedMove[0],selectedMove[1]), board.getValueAt(selectedMove[2],selectedMove[3]));
            } catch (MoveException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Difficulty 5 is minimax with depth of 12
     */
    private void difficulty5() {
        long startTime = System.currentTimeMillis();
        currentBoard = board.getBoardData();
        successorEvaluations = new ArrayList<>();
        maxDepth = 13;

        minimax(1, currentBoard, number, Integer.MIN_VALUE, Integer.MAX_VALUE);

        System.out.println("SUCCESSOR");
        int iter = 0;
        for (MoveAndScore ms : successorEvaluations) {
            System.out.println(iter + ": " + Arrays.toString(ms.move) + " |" + ms.score);
            iter+=1;
        }

        if (successorEvaluations.size() == 0) {
            //throw new GameException LOSS
        } else {
            int[] selectedMove = successorEvaluations.get(0).move;
            double bestScore = successorEvaluations.get(0).score;

            for (MoveAndScore ms : successorEvaluations) {
                if (ms.score > bestScore) selectedMove = ms.move;
            }
            System.out.println("AI:" + Arrays.toString(selectedMove));

            long stopTime = System.currentTimeMillis();
            if (stopTime-startTime < idealTime) {
                try { Thread.sleep(idealTime - (stopTime-startTime)); } catch (InterruptedException e) { e.printStackTrace(); }
                System.out.println("Had to wait");
            }

            try {
                tryMove(board.getValueAt(selectedMove[0],selectedMove[1]), board.getValueAt(selectedMove[2],selectedMove[3]));
            } catch (MoveException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Minimax algorithm
     * @param depth current depth in simulation
     * @param board current state of the board
     * @param player player to next move
     * @param alpha alpha pruning
     * @param beta beta pruning
     * @return score
     */
    private double minimax(int depth, int[][] board, int player, double alpha, double beta) {
        int[][] newBoard;
        double bestScore;

        //if max depth has been reached
        if (depth > maxDepth) {
            return 0;
        }

        //get possible moves
        ArrayList<int[]> availableMoves = getAvailableTakes(convertIntToBoard(board), player);
        availableMoves.addAll(getAvailableMoves(convertIntToBoard(board), player));


        if (player == number) {
            bestScore = Integer.MIN_VALUE;
            if (availableMoves.size() == 0) return 0;
        } else {
            bestScore = Integer.MAX_VALUE;
            if (availableMoves.size() == 0) return 1;
        }


        //if player == number == 2 == AI == MAX
        if (player == number) {

            //for every AI move
            for (int child = 0; child < availableMoves.size(); child++) {

                //get move
                int[] move = availableMoves.get(child);
                String spaces = new String(new char[depth]).replace("\0", " ");
                //System.out.println("AI:" + spaces + Arrays.toString(move) + " |" + depth);

                //make player move and get BoardAndScore
                Object[] boardAndScore = getBoardAfterMove(copyData(board), move, depth);
                newBoard = (int[][]) boardAndScore[0];
                //printBoardData(newBoard);
                double score = (double) boardAndScore[1];
                //get minimax eval for previous move
                score += minimax(depth+1, newBoard, opponentNum, alpha, beta);
                //System.out.println(" |" + score);
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(score, alpha);
                //if max depth reached, add
                if (depth == 1) {
                    successorEvaluations.add(new MoveAndScore(move, score));
                }
                if (alpha >= beta) break;
            }
        }
        //if player == 1 == Human == MIN
        else {
            //for every human move
            for (int child = 0; child < availableMoves.size(); child++) {
                //get move
                int[] move = availableMoves.get(child);
                String spaces = new String(new char[depth]).replace("\0", " ");
                //System.out.println("Hu:" + spaces + Arrays.toString(move) + " |" + depth);

                //make player move and get BoardAndScore
                Object[] boardAndScore = getBoardAfterMove(copyData(board), move, depth);
                newBoard = (int[][]) boardAndScore[0];
                double score = (double) boardAndScore[1];
                //printBoardData(newBoard);

                //get minimax eval for previous move
                score += minimax(depth+1, newBoard, number, alpha, beta);
                bestScore = Math.min(bestScore, score);
                beta = Math.min(score, bestScore);
                if (alpha >= beta) break;
            }
        }
        return bestScore;
    }

    /**
     * Gets available moves for a player
     * @param board reference for cells
     * @param number player number
     * @return ArrayList of int[] with xPos, yPos, target xPos, target yPos
     */
    private ArrayList<int[]> getAvailableMoves(Cell[][] board, int number) {
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
    private ArrayList<int[]> getAvailableTakes(Cell[][] board, int number) {
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
    private Object[] getBoardAfterMove(int[][] board, int[] move, int depth) {
        boolean take = false;
        double score = 0;
        //check if move (can be king)
        if (move[0] == move[2]+1 || move[0] == move[2]-1) {
            board[move[2]][move[3]] = board[move[0]][move[1]]; //copy selected into target
            board[move[0]][move[1]] = 0; //clear selected
            if ((board[move[2]][move[3]] == 1 && move[3] == 0) || (board[move[2]][move[3]] == 2 && move[3] == 7)) {
                score += (2/depth); //score for king 2/depth
            }
        }
        //check if take
        else if (move[0] == move[2]+2 || move[0] == move[2]-2) {
            board[move[2]][move[3]] = board[move[0]][move[1]]; //copy selected into target
            board[move[0]][move[1]] = 0; //clear selected
            int x = (move[2] - move[0])/2;
            int y = (move[3] - move[1])/2;
            board[move[0]+x][move[1]+y] = 0; //clear taken cell
            score += (2/depth); //score for take 2/depth
            take = true;
            if ((board[move[2]][move[3]] == 1 && move[3] == 0) || (board[move[2]][move[3]] == 2 && move[3] == 7)) {
                score += (2/depth); //score for king 2/depth
            }
        }
        if (score != 0) {
            System.out.println("Take:" + take + "| score:" + score + "| depth:" + depth);
        }
        return new Object[]{board, score};
    }

    /**
     * Converts an int board to a Cell[][] board
     * @param board int board
     * @return Cell board
     */
    private Cell[][] convertIntToBoard(int[][] board) {
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
    private ArrayList<Cell> getPieces(Cell[][] board, int black1white2) {
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

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public String getName() {
        return name;
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
}
