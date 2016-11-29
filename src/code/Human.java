package code;

/**
 * Class for human players, does actions through the GUI
 *
 * @author Lucas
 * @version 0.1
 */
public class Human implements Player {
    private int number;
    private Board board;
    private String name;


    Human(int number, Board board, String name) {
        this.number = number;
        this.board = board;
        this.name = name;
    }

    @Override
    public void tryMove(Cell selected, Cell target) throws MoveException {
        //System.out.println(target.getxPos() + ":" + selection.getxPos());
        //System.out.println("|" + target.getxPos() + "=" + (selection.getxPos()+1) + ": " + (target.getxPos() == selection.getxPos()+1));
        //System.out.println("|" + target.getxPos() + "=" + (selection.getxPos()-1) + ": " + (target.getxPos() == selection.getxPos()-1));
        //System.out.println(target.getyPos() + ":" + selection.getyPos());
        //System.out.println("|" + target.getyPos() + "=" + (selection.getyPos()-1) + ": " + (target.getxPos() == selection.getxPos()-1));

        selected.printCellData();
        target.printCellData();

        // check cell is not free
        if (selected.isFree())
            throw new MoveException("No selected cell, try again");

        // check target is free
        if (!target.isFree())
            throw new MoveException("Target cell is not free, try again");

        if (number == 1) { // player 1 starts at the bottom, black
            makeBlackMove(selected, target);
        } else { // player 2 starts at top of screen, white
            makeWhiteMove(selected, target);
        }
    }

    @Override
    public void makeMove() {

    }

    private boolean makeBlackMove(Cell selected, Cell target) throws MoveException {
        // Exception that is assigned to if move is not 1 or 2 left or right
        MoveException ex;

        //check that cell is black
        if (selected.isWhite())
            throw new MoveException("Cannot move a white piece");

        //check for move
        if (target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1) {
            if (target.getyPos() == selected.getyPos() - 1) {
                // checks are fine, make move
                selected.setFree();
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
        if (target.getxPos() == selected.getxPos()+2 || target.getxPos() == selected.getxPos()-2) {
            // if target y is 2 higher than selection
            if (target.getyPos() == selected.getyPos()-2) {
                //check that a white piece is in between
                Cell whiteTarget;
                // if take is towards right
                System.out.println((target.getxPos() - selected.getxPos()));
                if (target.getxPos() - selected.getxPos() > 0) // get right piece
                    whiteTarget = board.getValueAt(selected.getxPos()+1, selected.getyPos()-1);
                else // get left piece
                    whiteTarget = board.getValueAt(selected.getxPos()-1, selected.getyPos()-1);
                // check that piece is white
                if (whiteTarget.isWhite()) {
                    selected.setFree();
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
        if (selected.isKing()) {
            //check for move in opposite direction

            //check for take in opposite direction
        }

        throw ex;
    }

    private boolean makeWhiteMove(Cell selected, Cell target) throws MoveException {
        // Exception that is assigned to if move is not 1 or 2 left or right
        MoveException ex;

        //check that cell is white
        if (selected.isBlack()) throw new MoveException("Cannot move a black piece");

        //check for move
        if (target.getxPos() == selected.getxPos()+1 || target.getxPos() == selected.getxPos()-1) {
            if (target.getyPos() == selected.getyPos()+1) {
                // checks are fine, make move
                selected.setFree();
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
        if (target.getxPos() == selected.getxPos()+2 || target.getxPos() == selected.getxPos()-2) {
            // if target y is 2 higher than selection
            if (target.getyPos() == selected.getyPos()+2) {
                //check that a black piece is in between
                Cell blackTarget;
                // if take is towards right
                if (target.getxPos() - selected.getxPos() > 0) // get right piece
                    blackTarget = board.getValueAt(selected.getxPos()+1, selected.getyPos()+1);
                else // get left piece
                    blackTarget = board.getValueAt(selected.getxPos()-1, selected.getyPos()+1);
                // check that piece is black
                if (blackTarget.isBlack()) {
                    selected.setFree();
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
        if (selected.isKing()) {
            //check for move in opposite direction

            //check for take in opposite direction
        }
        throw ex;
    }

    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }
}
