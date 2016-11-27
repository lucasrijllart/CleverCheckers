package code;

/**
 *
 * @author Lucas Rijllart
 * @version 0.1
 */

public class Cell {

    private int xPos;
    private int yPos;

    private boolean lighter;

    private boolean black;
    private boolean white;
    private boolean free;
    private boolean selected;
    private boolean king;

    Cell(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;

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

    boolean isBlack() {
        return black;
    }

    boolean isWhite() {
        return white;
    }

    boolean isFree() {
        return free;
    }

    boolean isLighter() { return lighter; }

    void setLighter() {
        lighter = true;
    }

    boolean isSelected() { return selected; }

    void setSelected(boolean value) { selected = value; }

    boolean isKing() { return king; }

    public void setKing() { king = true; }
}
