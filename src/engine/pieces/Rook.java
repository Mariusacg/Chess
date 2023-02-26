package engine.pieces;

import java.util.Set;

import engine.Color;
import engine.board.Board;
import engine.board.Move;
import engine.board.Position;

public class Rook extends Piece implements MultipleCoordinateMove {
    private final static int[] RELATIVE_FILE_COORDINATES = { 1, -1, 0, 0 };
    private final static int[] RELATIVE_RANK_COORDINATES = { 0, 0, 1, -1 };

    public Rook(final Position position, final Color color, final boolean hasMoved) {
        super(PieceType.ROOK, position, color, hasMoved);
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
    public Rook movePiece(final Position destination) {
        return new Rook(destination, this.color, true);
    }
}
