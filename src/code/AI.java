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
class AI extends PlayerFunctions implements Player {
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
        if (number == 1) {
            return moveBlack(selected, target);
        } else {
            return moveWhite(selected, target);
        }
    }

    private String moveBlack(Cell selected, Cell target) {
        //check for move
        if (target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1) {
            if (target.getyPos() == selected.getyPos()-1) {
                target.move(selected);
            }
        }

        //check for take
        if (target.getxPos() == selected.getxPos()+2 || target.getxPos() == selected.getxPos()-2) {
            if (target.getyPos() == selected.getyPos()-2) {
                Cell takeTarget;
                if (target.getxPos() - selected.getxPos() > 0) // get right piece
                    takeTarget = board.getValueAt(selected.getxPos()+1, selected.getyPos()-1);
                else // get left piece
                    takeTarget = board.getValueAt(selected.getxPos()-1, selected.getyPos()-1);
                // check that piece is black
                target.move(selected);
                takeTarget.setFree();
            }
        }

        //check for move and take if king
        if (selected.isKing()) {
            //check for move in opposite direction
            if (target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1) {
                if (target.getyPos() == selected.getyPos()+1) {
                    target.move(selected);
                }
            }
            //check for take in opposite direction
            if (target.getxPos() == selected.getxPos()+2 || target.getxPos() == selected.getxPos()-2) {
                if (target.getyPos() == selected.getyPos() + 2) {
                    Cell takeTarget;
                    if (target.getxPos() - selected.getxPos() > 0) // get right piece
                        takeTarget = board.getValueAt(selected.getxPos() + 1, selected.getyPos() + 1);
                    else // get left piece
                        takeTarget = board.getValueAt(selected.getxPos() - 1, selected.getyPos() + 1);
                    // check that piece is black
                    target.move(selected);
                    takeTarget.setFree();
                }
            }
        }
        return name + " moved";
    }

    private String moveWhite(Cell selected, Cell target) {
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
    public void makeMove() throws GameException {
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
    private void difficulty1() throws GameException {
        long startTime = System.currentTimeMillis();
        ArrayList<int[]> possibleMoves = getAvailableMoves(board.getBoard(), number);
        possibleMoves.addAll(getAvailableTakes(board.getBoard(), number));

        //get random move

        int[] nextMove;
        if (possibleMoves.size() == 0) {
            throw new GameException(number, "No moves possible");
        } else if (possibleMoves.size() < 2) {
            nextMove = possibleMoves.get(0);
        } else {
            nextMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
        }
        //System.out.println("AI Move:" + Arrays.toString(nextMove));

        long stopTime = System.currentTimeMillis();
        if (stopTime-startTime < idealTime) {
            try { Thread.sleep(idealTime - (stopTime-startTime)); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        try {
            tryMove(board.getValueAt(nextMove[0], nextMove[1]), board.getValueAt(nextMove[2], nextMove[3]));
        } catch (MoveException e) {
            //System.out.println(e.getReason());
        }

    }

    /**
     * Difficulty 2 is random takes, and if no takes, random move
     */
    private void difficulty2() throws GameException {
        long startTime = System.currentTimeMillis();
        ArrayList<int[]> possibleMoves = getAvailableMoves(board.getBoard(), number);
        ArrayList<int[]> possibleTakes = getAvailableTakes(board.getBoard(), number);

        //for all takes, check if another is possible

        int[] nextMove;
        if (possibleMoves.size() == 0 && possibleMoves.size() == 0) {
            throw new GameException(number, "No moves possible");
        }
        if (possibleTakes.size() == 1) {
            nextMove = possibleTakes.get(0);
        } else if (possibleTakes.size() > 1) {
            nextMove = possibleTakes.get(new Random().nextInt(possibleTakes.size()));
        } else {
            nextMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
        }
        //System.out.println("AI Move:" + Arrays.toString(nextMove));

        long stopTime = System.currentTimeMillis();
        if (stopTime-startTime < idealTime) {
            try { Thread.sleep(idealTime - (stopTime-startTime)); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        try {
            tryMove(board.getValueAt(nextMove[0], nextMove[1]), board.getValueAt(nextMove[2], nextMove[3]));
        } catch (MoveException e) {
            //System.out.println(e.getReason());
        }
    }

    /**
     * Difficulty 3 is minimax with depth of 2
     */
    private void difficulty3() throws GameException {
        long startTime = System.currentTimeMillis();
        currentBoard = board.getBoardData();
        successorEvaluations = new ArrayList<>();
        maxDepth = 2;

        minimax(1, currentBoard, number, Integer.MIN_VALUE, Integer.MAX_VALUE);

        //System.out.println("SUCCESSOR");
        int iter = 0;
        for (MoveAndScore ms : successorEvaluations) {
            //System.out.println(iter + ": " + Arrays.toString(ms.move) + " |" + ms.score);
            iter+=1;
        }

        if (successorEvaluations.size() == 0) {
            throw new GameException(number, "No moves possible");
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
                //System.out.println("Had to wait");
            }

            try {
                tryMove(board.getValueAt(selectedMove[0],selectedMove[1]), board.getValueAt(selectedMove[2],selectedMove[3]));
            } catch (MoveException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Difficulty 4 is minimax with depth of 5
     */
    private void difficulty4() throws GameException {
        long startTime = System.currentTimeMillis();
        currentBoard = board.getBoardData();
        successorEvaluations = new ArrayList<>();
        maxDepth = 5;

        minimax(1, currentBoard, number, Integer.MIN_VALUE, Integer.MAX_VALUE);

        //System.out.println("SUCCESSOR");
        int iter = 0;
        for (MoveAndScore ms : successorEvaluations) {
            //System.out.println(iter + ": " + Arrays.toString(ms.move) + " |" + ms.score);
            iter+=1;
        }

        if (successorEvaluations.size() == 0) {
            throw new GameException(number, "No moves possible");
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
                //System.out.println("Had to wait");
            }

            try {
                tryMove(board.getValueAt(selectedMove[0],selectedMove[1]), board.getValueAt(selectedMove[2],selectedMove[3]));
            } catch (MoveException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Difficulty 5 is minimax with depth of 10
     */
    private void difficulty5() throws  GameException {
        long startTime = System.currentTimeMillis();
        currentBoard = board.getBoardData();
        successorEvaluations = new ArrayList<>();
        maxDepth = 10;

        minimax(1, currentBoard, number, Integer.MIN_VALUE, Integer.MAX_VALUE);

        //System.out.println("SUCCESSOR");
        int iter = 0;
        for (MoveAndScore ms : successorEvaluations) {
            //System.out.println(iter + ": " + Arrays.toString(ms.move) + " |" + ms.score);
            iter+=1;
        }

        if (successorEvaluations.size() == 0) {
            throw new GameException(number, "No moves possible");
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
                //System.out.println("Had to wait");
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
    private double minimax(int depth, int[][] board, int player, double alpha, double beta) throws GameException {
        int[][] newBoard;
        double bestScore;

        //if max depth has been reached
        if (depth > maxDepth) {
            return 0;
        }

        //get possible moves
        ArrayList<int[]> availableMoves = getAvailableTakes(convertIntToBoard(board), player);
        availableMoves.addAll(getAvailableMoves(convertIntToBoard(board), player));


        //assign bestScore and check for available moves
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
            for (int[] move : availableMoves) {

                //get move
                String spaces = new String(new char[depth]).replace("\0", " ");
                //System.out.println("AI:" + spaces + Arrays.toString(move) + " |" + depth);

                //make player move and get BoardAndScore
                Object[] boardAndScore = getBoardAfterMove(copyData(board), move, depth);
                newBoard = (int[][]) boardAndScore[0];
                //printBoardData(newBoard);
                double score = (double) boardAndScore[1];
                //get minimax eval for previous move
                score += minimax(depth + 1, newBoard, opponentNum, alpha, beta);
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
            for (int[] move : availableMoves) {
                //get move
                String spaces = new String(new char[depth]).replace("\0", " ");
                //System.out.println("Hu:" + spaces + Arrays.toString(move) + " |" + depth);

                //make player move and get BoardAndScore
                Object[] boardAndScore = getBoardAfterMove(copyData(board), move, depth);
                newBoard = (int[][]) boardAndScore[0];
                double score = (double) boardAndScore[1];
                //printBoardData(newBoard);

                //get minimax eval for previous move
                score += minimax(depth + 1, newBoard, number, alpha, beta);
                bestScore = Math.min(bestScore, score);
                beta = Math.min(score, bestScore);
                if (alpha >= beta) break;
            }
        }
        return bestScore;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDifficulty() {
        return "AI Level " + Integer.toString(difficulty);
    }
}
