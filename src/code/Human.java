package code;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for human players, does actions through the GUI
 *
 * @author Lucas
 * @version 0.1
 */
public class Human extends PlayerFunctions implements Player {
    private int number;
    private Board board;
    private String name;

    //for hints
    private ArrayList<MoveAndScore> successorEvaluations;
    private int maxDepth = 7;
    private long idealTime = 2000;
    private int opponentNum;

    Human(int number, Board board, String name) {
        this.number = number;
        if (number == 1)
            opponentNum = 2;
        else
            opponentNum = 1;
        this.board = board;
        this.name = name;
    }

    @Override
    public String tryMove(Cell selected, Cell target) throws MoveException {
        //boolean if move has been done
        boolean move = false;
        boolean take = false;
        boolean moveKing = false;
        boolean takeKing = false;

        selected.printCellData();
        target.printCellData();

        // check cell is not free
        if (selected.isFree())
            throw new MoveException("No selected cell, try again");

        // check target is free
        if (!target.isFree())
            throw new MoveException("Target cell is not free, try again");

        //check for move
        if ((target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1)
                && target.getyPos() == selected.getyPos()-1) {
            target.move(selected);
            move = true;
        }

        //check for take
        // if target x is 2 left or right of selection x and target y is 2 higher than selected y
        if ((target.getxPos() == selected.getxPos()+2 || target.getxPos() == selected.getxPos()-2)
                && target.getyPos() == selected.getyPos()-2) {
                //check that a white piece is in between
                Cell targetCell;
                // if take is towards right
                if (target.getxPos() - selected.getxPos() > 0) // get right piece
                    targetCell = board.getValueAt(selected.getxPos()+1, selected.getyPos()-1);
                else // get left piece
                    targetCell = board.getValueAt(selected.getxPos()-1, selected.getyPos()-1);
                // check that piece is white
                if ((number==1 && targetCell.isWhite()) || (number==2 && targetCell.isBlack())) {
                    target.move(selected);
                    targetCell.setFree();
                    take = true;
                } else { // cell is not white
                    throw new MoveException("No opposing piece to take");
                }

        }

        //check for move and take if king
        if (selected.isKing()) {
            //check for move in opposite direction
            if (target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1) {
                if (target.getyPos() == selected.getyPos() + 1) {
                    // checks are fine, make move
                    target.move(selected);
                    moveKing = true;
                } else { // move seems correct
                    throw new MoveException("Move is not one cell forward");
                }
            }
            //check for take in opposite direction
            if (target.getxPos() == selected.getxPos()+2 || target.getxPos() == selected.getxPos()-2) {
                // if target y is 2 higher than selection
                if (target.getyPos() == selected.getyPos()+2) {
                    //check that a white piece is in between
                    Cell targetCell;
                    // if take is towards right
                    if (target.getxPos() - selected.getxPos() > 0) // get right piece
                        targetCell = board.getValueAt(selected.getxPos()+1, selected.getyPos()+1);
                    else // get left piece
                        targetCell = board.getValueAt(selected.getxPos()-1, selected.getyPos()+1);
                    // check that piece is white
                    if ((number==1 && targetCell.isWhite()) || (number==2 && targetCell.isBlack())) {
                        target.move(selected);
                        targetCell.setFree();
                        takeKing = true;
                    } else { // cell is not white
                        throw new MoveException("No opposing piece to take");
                    }
                }
            }
        }

        if (!move && !take) {
            throw new MoveException("No valid move or take");
        }

        return name + " moved";
    }

    @Override
    public void makeMove() throws GameException {

    }

    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDifficulty() {
        return "Human";
    }

    private void getHint() throws GameException {
        long startTime = System.currentTimeMillis();
        int[][] currentBoard = board.getBoardData();
        successorEvaluations = new ArrayList<>();
        int maxDepth = 13;

        minimax(1, currentBoard, number, Integer.MIN_VALUE, Integer.MAX_VALUE);

        System.out.println("SUCCESSOR");
        int iter = 0;
        for (AI.MoveAndScore ms : successorEvaluations) {
            System.out.println(iter + ": " + Arrays.toString(ms.move) + " |" + ms.score);
            iter+=1;
        }

        if (successorEvaluations.size() == 0) {
            //throw new GameException LOSS
        } else {
            int[] selectedMove = successorEvaluations.get(0).move;
            double bestScore = successorEvaluations.get(0).score;

            for (AI.MoveAndScore ms : successorEvaluations) {
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
            if (availableMoves.size() == 0) {
                if (depth == 1) {
                    throw new GameException(number, "No moves possible");
                }
                return 0;
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            if (availableMoves.size() == 0) {
                return 1;
            }
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


}
