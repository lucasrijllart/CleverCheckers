package code;

/**
 *
 * @author Lucas Rijllart
 * @version 0.1
 */
public class CheckersGame {

    private Board board;
    private Window gui;

    int player;

    private Player player1;
    private Player player2;

    public static void main(String[] args) {
        new CheckersGame();
    }

    /**
     * New game with player1=human player2=AI
     */
    private CheckersGame() {
        board = new Board(this, 8, 8);
        newGame("You", true, 3, "AI", false, 3);
        //create GUI
        gui = new Window(this, board);
    }


    void newGame(String p1Name, boolean p1Human, int p1Diff, String p2Name, boolean p2Human, int p2Diff) {
        board.reset();
        if (p1Human)
            this.player1 = new Human(1, board, p1Name);
        else
            this.player1 = new AI(1, board, p1Name, p1Diff);
        if (p2Human)
            this.player2 = new Human(2, board, p2Name);
        else
            this.player2 = new AI(2, board, p2Name, p2Diff);

        //if player1 is AI, make AI move
        if (!player1.isHuman()) {
            makeAIMove();
            player = 2;
        } else {
            player = 1;
        }
    }

    boolean gotInput(Cell selected, Cell target) {
        if (isGameRunning()) {
            if (player == 1) {
                System.out.println("Player " + player);
                try { //player 1 makes move
                    player1.tryMove(selected, target);
                    //check if player can make another move
                    gui.infoField.setText("Move made");
                    player = 2;
                    if (!player2.isHuman())
                        makeAIMove();
                } catch (MoveException me) {
                    gui.infoField.setText(me.getReason());
                }
            } else {
                System.out.println("Player " + player);
                try {
                    player2.tryMove(selected, target);
                    //check if player can make another move
                    gui.infoField.setText("Move made");
                    player = 1;
                    if (!player1.isHuman())
                        makeAIMove();
                } catch (MoveException me) {
                    gui.infoField.setText(me.getReason());
                }
            }
        } else {
            if (player2Lost())
                gui.finish.setText(player1.getName() + " WINS!");
            else
                gui.finish.setText(player2.getName() + " WINS!");
        }
        return false;
    }

    private void makeAIMove() {
        if (player == 1) {
            try {
                player1.makeMove();
            } catch (GameException e) {
                gui.infoField.setText(e.getReason());
                if (e.getWinner() == 1) {
                    gui.finish.setText(player1.getName() + " WINS!");
                } else {
                    gui.finish.setText(player2.getName() + " WINS!");
                }
            }
            updateTable();
            player = 2;
        } else {
            try {
                player2.makeMove();
            } catch (GameException e) {
                gui.infoField.setText(e.getReason());
                if (e.getWinner() == 1) {
                    gui.finish.setText(player1.getName() + " WINS!");
                } else {
                    gui.finish.setText(player2.getName() + " WINS!");
                }
            }
            updateTable();
            player = 1;
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

    private boolean player2Lost() {
        boolean foundPlayer1Piece = false;
        int[][] boardData = board.getBoardData();
        for (int y = 0; y < board.getRowCount(); y++) {
            for (int x = 0; x < board.getColumnCount(); x++) {
                if (boardData[x][y] == 1)
                    foundPlayer1Piece = true;
            }
        }
        return foundPlayer1Piece;
    }

    void updateTable() {
        gui.updateTable();
    }

    Player getPlayer1() {
        return player1;
    }

    Player getPlayer2() {
        return player2;
    }
}
