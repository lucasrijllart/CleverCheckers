package code;

/**
 * Player interface, contains methods that Human and AI classes need to implement
 *
 * @author Lucas
 * @version 0.1
 */
public interface Player {

    /**
     * Method to try and make move on the board, checking for exceptions
     * @param selected selected cell to move piece from
     * @param target selected cell to move piece to
     * @throws MoveException piece movement exception for invalid moves
     */
    String tryMove(Cell selected, Cell target) throws MoveException;

    /**
     * Makes AI come up with move then try it
     */
    void makeMove();

    /**
     *
     * @return human
     */
    boolean isHuman();

    /**
     *
     * @return name
     */
    String getName();
}
