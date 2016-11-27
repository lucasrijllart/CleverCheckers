package code;

/**
 *
 * @author Lucas Rijllart
 * @version 0.1
 */
public class CheckersGame {

    private Board board;
    private Window gui;

    public int player;

    private Player player1;
    private Player player2;

    public static void main(String[] args) {
        new CheckersGame();
    }

    /**
     * New game with player1=human player2=AI
     */
    public CheckersGame() {
        board = new Board(this, 8, 8);
        newGame("Player", true, 0, "AI", true, 3);
        //create GUI
        gui = new Window(this, board);
    }


    public void newGame(String p1Name, boolean p1Human, int p1Diff, String p2Name, boolean p2Human, int p2Diff) {
        board.reset();
        this.player1 = new Player(1, board, p1Name, p1Human, p1Diff);
        this.player2 = new Player(2, board, p2Name, p2Human, p2Diff);
        player = 1;
    }

    public void gotInput(Cell selected, Cell target) {
        if (isGameRunning()) {
            if (player == 1) {
                System.out.println("Player " + player);
                try {
                    player1.tryMove(selected, target);
                    gui.infoField.setText("Move made");
                    player = 2;
                } catch (MoveException me) {
                    gui.infoField.setText(me.getReason());
                }
            } else {
                System.out.println("Player " + player);

                try {
                    player2.tryMove(selected, target);
                    gui.infoField.setText("Move made");
                    player = 1;
                } catch (MoveException me) {
                    gui.infoField.setText(me.getReason());
                }
            }
        }
    }

    private boolean isGameRunning() {
        int player = 0;
        int[][] boardData = board.getBoardData();
        for (int y = 0; y < board.getRowCount(); y++) {
            for (int x = 0; x < board.getColumnCount(); x++) {
                if (boardData[x][y] != 0 && player == 0) {
                    player = boardData[x][y];
                }
                if (boardData[x][y] != 0 && boardData[x][y] != player) {
                    return true;
                }
            }
        }
        return false;
    }

    public Board getBoard() {
        return board;
    }
}
