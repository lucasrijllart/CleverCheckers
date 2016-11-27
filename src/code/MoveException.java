package code;

/**
 * Created by Lucas on 27/11/2016.
 */
public class MoveException extends Exception {

    private String reason;

    public MoveException(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
}
