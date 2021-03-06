package code;

import java.util.ArrayList;

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
    private Cell hintCell;

    //for hints
    private ArrayList<MoveAndScore> successorEvaluations;
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

        if (hintCell != null) removeHint();

        // check cell is not free
        if (selected.isFree())
            throw new MoveException("No selected cell, try again");

        // check target is free
        if (!target.isFree())
            throw new MoveException("Target cell is not free, try again");

        if (number == 1) {
            isBlack(selected, target);
        } else {
            isWhite(selected, target);
        }

        return name + " moved";
    }

    private String isBlack(Cell selected, Cell target) throws MoveException {
        //boolean if move has been done
        boolean move = false;
        boolean take = false;
        boolean moveKing = false;
        boolean takeKing = false;
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
            if (targetCell.isWhite()) {
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
                    if (targetCell.isWhite()) {
                        target.move(selected);
                        targetCell.setFree();
                        takeKing = true;
                    } else { // cell is not white
                        throw new MoveException("No opposing piece to take");
                    }
                }
            }
        }

        if (!move && !take && !moveKing && !takeKing) {
            if (selected.isKing()) {
                if (target.getxPos() == selected.getxPos()) {
                    throw new MoveException("Cannot move forwards");
                } else if ((target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1)) {
                    throw new MoveException("Move is not 1 cell diagonally");
                } else {
                    throw new MoveException("Take is not 2 cells diagonally");
                }
            } else { //if not king
                if (target.getyPos() == selected.getyPos() + 1) {
                    throw new MoveException("Cannot move backwards");
                } else if (target.getxPos() == selected.getxPos()) {
                    throw new MoveException("Cannot move forwards");
                } else  if ((target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1)) {
                    throw new MoveException("Move is not 1 cell diagonally");
                } else {
                    throw new MoveException("Take is not 2 cells diagonally");
                }
            }
        }
        return name + " moved";
    }

    private String isWhite(Cell selected, Cell target) throws MoveException {
        //boolean if move has been done
        boolean move = false;
        boolean take = false;
        boolean moveKing = false;
        boolean takeKing = false;
        //check for move
        if ((target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1)
                && target.getyPos() == selected.getyPos()+1) {
            target.move(selected);
            move = true;
        }

        //check for take
        // if target x is 2 left or right of selection x and target y is 2 higher than selected y
        if ((target.getxPos() == selected.getxPos()+2 || target.getxPos() == selected.getxPos()-2)
                && target.getyPos() == selected.getyPos()+2) {
            //check that a white piece is in between
            Cell targetCell;
            // if take is towards right
            if (target.getxPos() - selected.getxPos() > 0) // get right piece
                targetCell = board.getValueAt(selected.getxPos()+1, selected.getyPos()+1);
            else // get left piece
                targetCell = board.getValueAt(selected.getxPos()-1, selected.getyPos()+1);
            // check that piece is white
            if (targetCell.isBlack()) {
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
                if (target.getyPos() == selected.getyPos() - 1) {
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
                if (target.getyPos() == selected.getyPos()-2) {
                    //check that a white piece is in between
                    Cell targetCell;
                    // if take is towards right
                    if (target.getxPos() - selected.getxPos() > 0) // get right piece
                        targetCell = board.getValueAt(selected.getxPos()+1, selected.getyPos()-1);
                    else // get left piece
                        targetCell = board.getValueAt(selected.getxPos()-1, selected.getyPos()-1);
                    // check that piece is white
                    if (targetCell.isBlack()) {
                        target.move(selected);
                        targetCell.setFree();
                        takeKing = true;
                    } else { // cell is not white
                        throw new MoveException("No opposing piece to take");
                    }
                }
            }
        }

        if (!move && !take && !moveKing && !takeKing) {
            if (selected.isKing()) {
                if (target.getxPos() == selected.getxPos()) {
                    throw new MoveException("Cannot move forwards");
                } else if ((target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1)) {
                    throw new MoveException("Move is not 1 cell diagonally");
                } else {
                    throw new MoveException("Take is not 2 cells diagonally");
                }
            } else { //if not king
                if (target.getyPos() == selected.getyPos() - 1) {
                    throw new MoveException("Cannot move backwards");
                } else if (target.getxPos() == selected.getxPos()) {
                    throw new MoveException("Cannot move forwards");
                } else  if ((target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1)) {
                    throw new MoveException("Move is not 1 cell diagonally");
                } else {
                    throw new MoveException("Take is not 2 cells diagonally");
                }
            }
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

    void getHint() throws GameException {
        int[][] currentBoard = board.getBoardData();
        successorEvaluations = new ArrayList<>();

        minimax(1, currentBoard, number, Integer.MIN_VALUE, Integer.MAX_VALUE);

        if (successorEvaluations.size() == 0) {
            throw new GameException(number, "No possible moves");
        } else {
            int[] selectedMove = successorEvaluations.get(0).move;
            double bestScore = successorEvaluations.get(0).score;

            for (MoveAndScore ms : successorEvaluations) {
                if (ms.score > bestScore) selectedMove = ms.move;
            }

            hintCell = board.getValueAt(selectedMove[2],selectedMove[3]);
            hintCell.setHint(true);
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
        int maxDepth = 7;
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

    private void removeHint() {
        hintCell.setHint(false);
        board.updateTable();

    }
}
