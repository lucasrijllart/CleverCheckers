package code;

interface CheckersException {
    String getReason();
}

class GameException extends Exception implements CheckersException {
    private int loser;
    private String reason;

    GameException(int loser, String reason) {
        this.loser = loser;
        this.reason = reason;
    }

    public int getWinner() {
        if (loser == 1)
            return 2;
        else
            return 1;
    }

    @Override
    public String getReason() {
        return reason;
    }
}

class MoveException extends Exception implements CheckersException {

    private String reason;

    MoveException(String reason) {
        this.reason = reason;
    }

    @Override
    public String getReason() {
        return reason;
    }
}
