package code;

/**
 * Cell class contians information of one cell from the board.
 * Keeps track of state of one square and has movement checks.
 * @author Lucas Rijllart
 * @version 0.2
 */

class Cell {

    private int xPos;
    private int yPos;

    private Cell[][] board;

    private boolean lighter;

    private boolean black;
    private boolean white;
    private boolean free;

    private boolean selected;
    private boolean king;
    private boolean hint;

    Cell(int xPos, int yPos, Cell[][] board) {
        this.xPos = xPos;
        this.yPos = yPos;

        this.board = board;

        lighter = false;

        black = false;
        white = false;
        free = true;
        selected = false;
        king = false;
        hint = false;
    }

    int getxPos() { return xPos; }
    int getyPos() { return yPos; }

    void setBlack() {
        black = true;
        free = false;
        hint = false;
        if (!king && getyPos() == 0) king = true;
    }

    void setWhite() {
        white = true;
        free = false;
        hint = false;
        if (!king && getyPos() == 7) king = true;
    }

    void move(Cell c) {
        this.king = c.isKing();
        if (c.isBlack()) {
            setBlack();
        } else {
            setWhite();
        }
        c.setFree();
    }

    void setFree() {
        white = false;
        black = false;
        hint = false;
        free = true;
    }

    String getCellData() {
        String output;
        String state;
        if (black) state = "black";
        else state = "white";
        if (free) state = "free";
        if (king) state += ", king";
        output = "Cell" + "["+xPos+","+yPos+"],"+state;
        return output;
    }

    int[] canMoveLeft() {
        int[] output = new int[4];
        if (black) { // black left is x-1, y-1
            if (xPos > 0 && yPos > 0) { //don't consider pieces stuck to left wall
                if (board[xPos - 1][yPos - 1].isFree()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos - 1;
                    output[3] = yPos - 1;
                    return output;
                }
            }
        } else { // white left is x+1] y+1
            if (xPos < 7 && yPos < 7) { //don't consider pieces stuck to right wall
                if (board[xPos + 1][yPos + 1].isFree()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos + 1;
                    output[3] = yPos + 1;
                    return output;
                }
            }
        }
        return null;
    }

    int[] canMoveRight() {
        int[] output = new int[4];
        if (black) { // black right is x+1
            if (xPos < 7 && yPos > 0) { //don't consider pieces stuck to left wall
                if (board[xPos + 1][yPos - 1].isFree()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos + 1;
                    output[3] = yPos - 1;
                    return output;
                }
            }
        } else { // white right is x-1
            if (xPos > 0 && yPos < 7) { //don't consider pieces stuck to right wall
                if (board[xPos - 1][yPos + 1].isFree()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos - 1;
                    output[3] = yPos + 1;
                    return output;
                }
            }
        }
        return null;
    }

    int[] canTakeLeft() {
        int[] output = new int[4];
        if (black) {
            if (xPos > 1 && yPos > 1) { //need to be 2 away from left wall
                if (board[xPos-2][yPos-2].isFree() &&
                        board[xPos-1] [yPos-1].isWhite()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos - 2;
                    output[3] = yPos - 2;
                    return output;
                }
            }
        } else {
            if (xPos < 6 && yPos < 6) { //need to be 2 away from right wall and bottom wall
                if (board[xPos+2][yPos+2].isFree() &&
                        board[xPos+1] [yPos+1].isBlack()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos + 2;
                    output[3] = yPos + 2;
                    return output;
                }
            }
        }
        return null;
    }

    int[] canTakeRight() {
        int[] output = new int[4];
        if (black) {
            if (xPos < 6 && yPos > 1) { //need to be 2 away from right wall
                if (board[xPos+2][yPos-2].isFree() &&
                        board[xPos+1][yPos-1].isWhite()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos + 2;
                    output[3] = yPos - 2;
                    return output;
                }
            }
        } else {
            if (xPos > 1 && yPos < 6) { //need to be 2 away from left wall
                if (board[xPos-2][yPos+2].isFree() &&
                        board[xPos-1][yPos+1].isBlack()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos - 2;
                    output[3] = yPos + 2;
                    return output;
                }
            }
        }
        return null;
    }

    int[] canKingMoveDownLeft() {
        int[] output = new int[4];
        if (black) { // black left is x-1
            if (xPos > 0 && yPos < 7) { //don't consider pieces stuck to left wall or bottom wall
                if (board[xPos - 1][yPos + 1].isFree()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos - 1;
                    output[3] = yPos + 1;
                    return output;
                }
            }
        } else { // white left is x+1
            if (xPos < 7 && yPos > 0) { //don't consider pieces stuck to right wall
                if (board[xPos + 1][yPos - 1].isFree()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos + 1;
                    output[3] = yPos - 1;
                    return output;
                }
            }
        }
        return null;
    }

    int[] canKingMoveDownRight() {
        int[] output = new int[4];
        if (black) { // black right is x+1
            if (xPos < 7 && yPos < 7) { //don't consider pieces stuck to left wall
                if (board[xPos + 1][yPos + 1].isFree()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos + 1;
                    output[3] = yPos + 1;
                    return output;
                }
            }
        } else { // white right is x-1
            if (xPos > 0 && yPos > 0) { //don't consider pieces stuck to right wall
                if (board[xPos - 1][yPos - 1].isFree()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos - 1;
                    output[3] = yPos - 1;
                    return output;
                }
            }
        }
        return null;
    }

    int[] canKingTakeDownLeft() {
        int[] output = new int[4];
        if (black) {
            if (xPos > 1 && yPos < 6) { //need to be 2 away from left wall and bottom wall
                if (board[xPos-2][yPos+2].isFree() &&
                        board[xPos-1][yPos+1].isWhite()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos - 2;
                    output[3] = yPos + 2;
                    return output;
                }
            }
        } else {
            if (xPos < 6 && yPos > 1) { //white take down left is x+2, y-2
                if (board[xPos+2][yPos-2].isFree() &&
                        board[xPos+1][yPos-1].isBlack()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos + 2;
                    output[3] = yPos - 2;
                    return output;
                }
            }
        }
        return null;
    }

    int[] canKingTakeDownRight() {
        int[] output = new int[4];
        if (black) {
            if (xPos < 6 && yPos < 6) { //need to be 2 away from right wall
                if (board[xPos+2][yPos+2].isFree() &&
                        board[xPos+1][yPos+1].isWhite()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos + 2;
                    output[3] = yPos + 2;
                    return output;
                }
            }
        } else {
            if (xPos > 1 && yPos > 1) { //need to be 2 away from left wall
                if (board[xPos-2][yPos-2].isFree() &&
                        board[xPos-1][yPos-1].isBlack()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos - 2;
                    output[3] = yPos - 2;
                    return output;
                }
            }
        }
        return null;
    }

    boolean isBlack() { return black; }

    boolean isWhite() {
        return white;
    }

    boolean isFree() { return free; }

    boolean isLighter() { return lighter; }

    void setLighter() {
        lighter = true;
    }

    boolean isSelected() { return selected; }

    void setSelected(boolean value) { selected = value; }

    boolean isKing() { return king; }

    void setKing() { king = true; }

    void setHint(boolean value) {
        hint = value;
    }

    boolean isHint() {
        return hint;
    }
}
