package code;

/**
 * Player class, contains information and methods for players.
 * @author Lucas
 * @version 0.1
 */
public class Player {

    private int number; // 1 for black, 2 for white
    private Board board;

    private String name;
    private boolean human;
    private int difficulty;

    Player(int number, Board board, String name, boolean human, int difficulty) {
        this.number = number;
        this.board = board;
        this.name = name;
        this.human = human;
        this.difficulty = difficulty;
    }

    boolean tryMove(Cell selection, Cell target) throws MoveException {

        //System.out.println(target.getxPos() + ":" + selection.getxPos());
        //System.out.println("|" + target.getxPos() + "=" + (selection.getxPos()+1) + ": " + (target.getxPos() == selection.getxPos()+1));
        //System.out.println("|" + target.getxPos() + "=" + (selection.getxPos()-1) + ": " + (target.getxPos() == selection.getxPos()-1));
        //System.out.println(target.getyPos() + ":" + selection.getyPos());
        //System.out.println("|" + target.getyPos() + "=" + (selection.getyPos()-1) + ": " + (target.getxPos() == selection.getxPos()-1));

        selection.printCellData();
        target.printCellData();

        // check cell is not free
        if (selection.isFree())
            throw new MoveException("No selected cell, try again");

        // check target is free
        if (!target.isFree())
            throw new MoveException("Target cell is not free, try again");

        if (number == 1) { // player 1 starts at the bottom, black
            if (human) {
                return tryBlackHuman(selection, target);
            } else { // AI
                minimaxBlack(selection, target);
            }
        } else { // player 2 starts at top of screen, white
            if (human) {
                return tryWhiteHuman(selection, target);
            } else { // AI
                minimaxWhite(selection, target);
            }
        }
        return false;
    }

    private boolean tryBlackHuman(Cell selection, Cell target) throws MoveException {
        // Exception that is assigned to if move is not 1 or 2 left or right
        MoveException ex;

        //check that cell is black
        if (selection.isWhite())
            throw new MoveException("Cannot move a white piece");

        //check for move
        if (target.getxPos() == selection.getxPos()+1 || target.getxPos() == selection.getxPos()-1) {
            if (target.getyPos() == selection.getyPos() - 1) {
                // checks are fine, make move
                selection.setFree();
                target.setBlack();
                return true;
            } else { // move seems correct
                throw new MoveException("Move is not one cell forward");
            }
        } else {
            ex = new MoveException("Move is not 1 cell left or right");
        }

        //check for take
        // if target x is 2 left or right of selection x
        if (target.getxPos() == selection.getxPos()+2 || target.getxPos() == selection.getxPos()-2) {
            // if target y is 2 higher than selection
            if (target.getyPos() == selection.getyPos()-2) {
                //check that a white piece is in between
                Cell whiteTarget;
                // if take is towards right
                System.out.println((target.getxPos() - selection.getxPos()));
                if (target.getxPos() - selection.getxPos() > 0) // get right piece
                    whiteTarget = board.getValueAt(selection.getxPos()+1, selection.getyPos()-1);
                else // get left piece
                    whiteTarget = board.getValueAt(selection.getxPos()-1, selection.getyPos()-1);
                // check that piece is white
                if (whiteTarget.isWhite()) {
                    selection.setFree();
                    whiteTarget.setFree();
                    target.setBlack();
                    return true;
                } else { // cell is not white
                    throw new MoveException("No white piece to take");
                }
            } else { // target y is not 2 higher than selection
                throw new MoveException("Take is not two cells forward");
            }
        } else {
            ex = new MoveException("Take is not 2 cells left or right");
        }

        //check for move and take if king
        if (selection.isKing()) {
            //check for move in opposite direction

            //check for take in opposite direction
        }

        throw ex;
    }

    private boolean minimaxBlack(Cell selection, Cell target) throws MoveException {
        return false;
    }

    private boolean tryWhiteHuman(Cell selection, Cell target) throws MoveException {
        // Exception that is assigned to if move is not 1 or 2 left or right
        MoveException ex;

        //check that cell is white
        if (selection.isBlack()) throw new MoveException("Cannot move a black piece");

        //check for move
        if (target.getxPos() == selection.getxPos()+1 || target.getxPos() == selection.getxPos()-1) {
            if (target.getyPos() == selection.getyPos()+1) {
                // checks are fine, make move
                selection.setFree();
                target.setWhite();
                return true;
            } else { // move seems correct
                throw new MoveException("Move is not one cell forward");
            }
        } else {
            ex = new MoveException("Move is not 1 cell left or right");
        }

        //check for take
        // if target x is 2 left or right of selection x
        if (target.getxPos() == selection.getxPos()+2 || target.getxPos() == selection.getxPos()-2) {
            // if target y is 2 higher than selection
            if (target.getyPos() == selection.getyPos()+2) {
                //check that a black piece is in between
                Cell blackTarget;
                // if take is towards right
                if (target.getxPos() - selection.getxPos() > 0) // get right piece
                    blackTarget = board.getValueAt(selection.getxPos()+1, selection.getyPos()+1);
                else // get left piece
                    blackTarget = board.getValueAt(selection.getxPos()-1, selection.getyPos()+1);
                // check that piece is black
                if (blackTarget.isBlack()) {
                    selection.setFree();
                    blackTarget.setFree();
                    target.setWhite();
                    return true;
                } else { // cell is not white
                    throw new MoveException("No black piece to take");
                }
            } else { // target y is not 2 higher than selection
                throw new MoveException("Take is not two cells forward");
            }
        } else {
            ex = new MoveException("Take is not 2 cells left or right");
        }

        //check for move and take if king
        if (selection.isKing()) {
            //check for move in opposite direction

            //check for take in opposite direction
        }
        throw ex;
    }

    private boolean minimaxWhite(Cell selection, Cell target) throws MoveException {
        return false;
    }

    public int getNumber() { return number; }

    public String getName() { return name; }

    public boolean isHuman() { return human; }

    public int getDifficulty() { return difficulty; }

    //public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
}
