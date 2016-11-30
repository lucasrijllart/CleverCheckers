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
    public void makeMove() {

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
}
