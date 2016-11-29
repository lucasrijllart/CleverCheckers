package code;

import java.util.Arrays;

/**
 *
 * @author Lucas Rijllart
 * @version 0.1
 */

public class Cell {

    private int xPos;
    private int yPos;

    private Board board;

    private boolean lighter;

    private boolean black;
    private boolean white;
    private boolean free;

    private boolean selected;
    private boolean king;

    Cell(int xPos, int yPos, Board board) {
        this.xPos = xPos;
        this.yPos = yPos;

        this.board = board;

        lighter = false;

        black = false;
        white = false;
        free = true;
        selected = false;
        king = false;
    }

    int getxPos() { return xPos; }
    int getyPos() { return yPos; }

    void setBlack() {
        black = true;
        free = false;
    }

    void setWhite() {
        white = true;
        free = false;
    }

    void setFree() {
        white = false;
        black = false;
        free = true;
    }

    void printCellData() {
        String state;
        if (black) state = "black";
        else state = "white";
        if (free) state = "free";
        System.out.println("Cell"+"["+xPos+","+yPos+"],"+state);
    }

    int[] canMoveLeft() {
        int[] output = new int[4];
        if (black) { // black left is x-1
            if (xPos > 0 && yPos > 1) { //don't consider pieces stuck to left wall
                if (board.getValueAt(xPos - 1, yPos - 1).isFree()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos - 1;
                    output[3] = yPos - 1;
                    System.out.println("Cell:" + xPos + "," + yPos);
                    System.out.println("Move:" + Arrays.toString(output));
                    return output;
                }
            }
        } else { // white left is x+1
            if (xPos < 7 && yPos < 6) { //don't consider pieces stuck to right wall
                if (board.getValueAt(xPos + 1, yPos + 1).isFree()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos + 1;
                    output[3] = yPos + 1;
                    System.out.println("Cell:" + xPos + "," + yPos);
                    System.out.println("Move:" + Arrays.toString(output));
                    return output;
                }
            }
        }
        return null;
    }

    int[] canMoveRight() {
        int[] output = new int[4];
        if (black) { // black right is x+1
            if (xPos < 7 && yPos > 1) { //don't consider pieces stuck to left wall
                if (board.getValueAt(xPos + 1, yPos - 1).isFree()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos + 1;
                    output[3] = yPos - 1;
                    return output;
                }
            }
        } else { // white right is x-1
            if (xPos > 0 && yPos < 6) { //don't consider pieces stuck to right wall
                if (board.getValueAt(xPos - 1, yPos + 1).isFree()) {
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

    int[] canTakeRight() {
        int[] output = new int[4];
        if (black) {
            if (xPos < 6 && yPos > 2) { //need to be 2 away from right wall
                if (board.getValueAt(xPos+2,yPos-2).isFree() &&
                        board.getValueAt(xPos+1, yPos-1).isWhite()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos + 2;
                    output[3] = yPos - 2;
                    return output;
                }
            }
        } else {
            if (xPos > 1 && yPos < 5) { //need to be 2 away from left wall
                if (board.getValueAt(xPos-2, yPos+2).isFree() &&
                        board.getValueAt(xPos-1, yPos+1).isBlack()) {
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

    int[] canTakeLeft() {
        int[] output = new int[4];
        if (black) {
            if (xPos > 1 && yPos > 2) { //need to be 2 away from left wall
                if (board.getValueAt(xPos-2,yPos-2).isFree() &&
                        board.getValueAt(xPos-1, yPos-1).isWhite()) {
                    output[0] = xPos;
                    output[1] = yPos;
                    output[2] = xPos - 2;
                    output[3] = yPos - 2;
                    return output;
                }
            }
        } else {
            if (xPos < 6 && yPos < 5) { //need to be 2 away from right wall
                if (board.getValueAt(xPos+2, yPos+2).isFree() &&
                        board.getValueAt(xPos+1, yPos+1).isBlack()) {
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

    public void setKing() { king = true; }
}
