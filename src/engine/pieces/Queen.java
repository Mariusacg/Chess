package engine.pieces;

import java.util.Set;

import engine.Color;
import engine.board.Board;
import engine.board.Move;
import engine.board.Position;

public class Queen extends Piece {
    private final Bishop bishop;
    private final Rook rook;

    public Queen(final Position position, final Color color, final boolean hasMoved) {
        super(PieceType.QUEEN, position, color, hasMoved);

        bishop = new Bishop(position, color, hasMoved);
        rook = new Rook(position, color, hasMoved);
    }

    @Override
    public Set<Move> getLegalMoves(final Board board) {
        Set<Move> legalMoves = bishop.getLegalMoves(board, this);

        legalMoves.addAll(rook.getLegalMoves(board, this));

        return legalMoves;
    }

    @Override
    public Queen movePiece(final Position destination) {
        return new Queen(destination, this.color, true);
    }
}
