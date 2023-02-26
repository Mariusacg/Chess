package engine.pieces;

import java.util.Set;

import engine.Color;
import engine.board.Board;
import engine.board.Move;
import engine.board.Position;

public class Knight extends Piece implements SingleCoordinateMove {
    private final static int[] RELATIVE_FILE_COORDINATES = { -2, -2, -1, -1, 1, 1, 2, 2 };
    private final static int[] RELATIVE_RANK_COORDINATES = { -1, 1, -2, 2, -2, 2, -1, 1 };

    public Knight(Position position, Color color, boolean hasMoved) {
        super(PieceType.KNIGHT, position, color, hasMoved);
    }

    @Override
    public Set<Move> getLegalMoves(final Board board) {
        final Set<Move> legalMoves = getLegalMoves(
                board,
                this,
                RELATIVE_RANK_COORDINATES,
                RELATIVE_FILE_COORDINATES);

        return legalMoves;
    }

    @Override
    public Knight movePiece(final Position destination) {
        return new Knight(destination, this.color, true);
    }
}
