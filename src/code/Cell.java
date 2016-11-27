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

    public int getxPos() { return xPos; }
    public int getyPos() { return yPos; }

    public void setBlack() {
        black = true;
        free = false;
    }

    public void setWhite() {
        white = true;
        free = false;
    }

    public void setFree() {
        white = false;
        black = false;
        free = true;
    }

    public void printCellData() {
        String state;
        if (black) state = "black";
        else state = "white";
        if (free) state = "free";
        System.out.println("Cell"+"["+xPos+","+yPos+"],"+state);
    }

    public boolean isBlack() {
        return black;
    }

    public boolean isWhite() {
        return white;
    }

    public boolean isFree() {
        return free;
    }

    public boolean isLighter() { return lighter; }

    public void setLighter() {
        lighter = true;
    }

    public boolean isSelected() { return selected; }

    public void setSelected(boolean value) { selected = value; }

    public boolean isKing() { return king; }

    public void setKing() { king = true; }
}
