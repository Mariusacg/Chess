package engine.pieces;

import java.util.Set;

import engine.Color;
import engine.board.Board;
import engine.board.Move;
import engine.board.Position;

public class Bishop extends Piece implements MultipleCoordinateMove {
    private final static int[] RELATIVE_FILE_COORDINATES = { -1, 1, 1, -1 };
    private final static int[] RELATIVE_RANK_COORDINATES = { 1, 1, -1, -1 };

    public Bishop(final Position position, final Color color, final boolean hasMoved) {
        super(PieceType.BISHOP, position, color, hasMoved);
    }

    Set<Move> getLegalMoves(final Board board, final Piece attackingPiece) {
        return getLegalMoves(
                board,
                attackingPiece,
                RELATIVE_RANK_COORDINATES,
                RELATIVE_FILE_COORDINATES);
    }

    @Override
    public Set<Move> getLegalMoves(final Board board) {
        return getLegalMoves(board, this);
    }

    @Override
    public Bishop movePiece(final Position destination) {
        return new Bishop(destination, this.color, true);
    }
}
