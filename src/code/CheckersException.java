package code;

interface CheckersException {
    String getReason();
}

class GameException extends Exception implements CheckersException {
    private String reason;

    GameException(String reason) {
        this.reason = reason;
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
