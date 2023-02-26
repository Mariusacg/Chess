package engine.pieces;

import java.util.Set;

import engine.Color;
import engine.board.Board;
import engine.board.Move;
import engine.board.Position;

public class King extends Piece implements SingleCoordinateMove {
    private final static int[] RELATIVE_FILE_COORDINATES = { 1, 0, -1, -1, -1, 0, 1, 1 };
    private final static int[] RELATIVE_RANK_COORDINATES = { -1, -1, -1, 0, 1, 1, 1, 0 };

    public King(final Position position, final Color color, final boolean hasMoved) {
        super(PieceType.KING, position, color, hasMoved);
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
    public King movePiece(final Position destination) {
        return new King(destination, this.color, true);
    }
}
