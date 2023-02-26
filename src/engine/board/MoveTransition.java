package engine.board;

public class MoveTransition {
    private final Board nextBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveTransition(final Board nextBoard, final Move move, final MoveStatus moveStatus) {
        this.nextBoard = nextBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

    public Board getBoard() {
        return this.nextBoard;
    }

    public enum MoveStatus {
        DONE, ILLEGAL, LEAVES_PLAYER_IN_CHECK
    }
}
