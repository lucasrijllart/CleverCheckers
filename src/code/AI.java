package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Class for AI players, does actions through the GUI
 *
 * @author Lucas
 * @version 0.1
 */
class AI implements Player {
    private int number;
    private Board board;
    private String name;
    private int difficulty;

    //vars for minimax
    private int[][] currentBoard;
    private ArrayList<int[]> successorEvaluations;

    AI(int number, Board board, String name, int difficulty) {
        this.number = number;
        this.board = board;
        this.name = name;
        this.difficulty = difficulty;
    }

    @Override
    public void tryMove(Cell selected, Cell target) throws MoveException {
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
    }

    @Override
    public void makeMove() {
        checkDifficulty();
    }

    private void checkDifficulty() {
        switch (difficulty) {
            case 1: difficulty1(); break;
            case 2: difficulty2(); break;
            case 3: difficulty3(); break;
        }
    }

    private void difficulty1() {
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        ArrayList<Cell> allPieces;

        //get a list of all pieces owned by player
        allPieces = board.getPieces(number);
        System.out.println("\n\nPieces:" + allPieces.size());
        //for each cell, check if it has possible moves
        for (Cell c : allPieces) {
            int[] tryMove;
            tryMove = c.canTakeLeft();
            if (tryMove != null) possibleMoves.add(tryMove);
            tryMove = c.canTakeRight();
            if (tryMove != null) possibleMoves.add(tryMove);
            tryMove = c.canMoveLeft();
            if (tryMove != null) possibleMoves.add(tryMove);
            tryMove = c.canMoveRight();
            if (tryMove != null) possibleMoves.add(tryMove);
            if (c.isKing()) {
                tryMove = c.canKingTakeDownLeft();
                if (tryMove != null) possibleMoves.add(tryMove);
                tryMove = c.canKingTakeDownRight();
                if (tryMove != null) possibleMoves.add(tryMove);
                tryMove = c.canKingMoveDownLeft();
                if (tryMove != null) possibleMoves.add(tryMove);
                tryMove = c.canKingMoveDownRight();
                if (tryMove != null) possibleMoves.add(tryMove);
            }
        }

        //for all takes, check if another is possible

        System.out.println("Moves:");
        for (int[] a : possibleMoves) {
            System.out.println(Arrays.toString(a));
        }

        int[] nextMove;
        if (possibleMoves.size() < 2) {
            nextMove = possibleMoves.get(0);
        } else {
            nextMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
        }
        System.out.println("AI Move:" + Arrays.toString(nextMove));

        //wait
        try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }

        try {
            tryMove(board.getValueAt(nextMove[0], nextMove[1]), board.getValueAt(nextMove[2], nextMove[3]));
        } catch (MoveException e) {
            System.out.println(e.getReason());
        }
    }

    private void difficulty2() {
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        ArrayList<int[]> possibleTakes = new ArrayList<>();
        ArrayList<Cell> allPieces;

        //get a list of all pieces owned by player
        allPieces = board.getPieces(number);
        System.out.println("\n\nPieces:" + allPieces.size());
        //for each cell, check if it has possible moves
        for (Cell c : allPieces) {
            int[] tryMove;
            tryMove = c.canTakeLeft();
            if (tryMove != null) possibleTakes.add(tryMove);
            tryMove = c.canTakeRight();
            if (tryMove != null) possibleTakes.add(tryMove);
            tryMove = c.canMoveLeft();
            if (tryMove != null) possibleMoves.add(tryMove);
            tryMove = c.canMoveRight();
            if (tryMove != null) possibleMoves.add(tryMove);
            if (c.isKing()) {
                tryMove = c.canKingTakeDownLeft();
                if (tryMove != null) possibleTakes.add(tryMove);
                tryMove = c.canKingTakeDownRight();
                if (tryMove != null) possibleTakes.add(tryMove);
                tryMove = c.canKingMoveDownLeft();
                if (tryMove != null) possibleMoves.add(tryMove);
                tryMove = c.canKingMoveDownRight();
                if (tryMove != null) possibleMoves.add(tryMove);
            }
        }

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

        try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }


        try {
            tryMove(board.getValueAt(nextMove[0], nextMove[1]), board.getValueAt(nextMove[2], nextMove[3]));
        } catch (MoveException e) {
            System.out.println(e.getReason());
        }
    }

    private void difficulty3() {
        currentBoard = board.getBoardData();

        successorEvaluations = new ArrayList<>();

        minimax(0, currentBoard, number);

        for (int[] move : successorEvaluations) {
            System.out.println(Arrays.toString(move));
        }

        int[] selectedMove;

        selectedMove = successorEvaluations.get(0);

        System.out.println(Arrays.toString(selectedMove));
    }

    private int minimax(int depth, int[][] board, int player) {
        //get possible moves
        return 0;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }
}
