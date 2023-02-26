package engine.pieces;

import java.util.HashSet;
import java.util.Set;

import engine.Color;
import engine.board.Board;
import engine.board.Move;
import engine.board.Position;
import engine.board.Tile;
import engine.board.Move.PawnAttackMove;
import engine.board.Move.PawnMove;

public class Pawn extends Piece {
    private final static int[] RELATIVE_FILE_COORDINATES = { -1, 1 };

    public Pawn(final Position position, final Color color, final boolean hasMoved) {
        super(PieceType.PAWN, position, color, hasMoved);
    }

    @Override
    public Set<Move> getLegalMoves(final Board board) {
        final HashSet<Move> legalMoves = new HashSet<>();

        final int nextRankOrdinal = position.rank.ordinal() + this.color.getDirection();

        Position destinationPosition = new Position(position.file.ordinal(), nextRankOrdinal);
        Tile destinationTile = board.getTile(destinationPosition);

        if (destinationTile.isOccupied() == false) {
            legalMoves.add(new PawnMove(board, this, destinationPosition));

            if (this.hasMoved == false) {
                destinationPosition = new Position(
                        position.file.ordinal(),
                        nextRankOrdinal + this.color.getDirection());

                destinationTile = board.getTile(destinationPosition);

                if (destinationTile.isOccupied() == false)
                    legalMoves.add(new PawnMove(board, this, destinationPosition));
            }
        }

        for (int attackedFileCoordinate : RELATIVE_FILE_COORDINATES) {
            final int attackedFileOrdinal = this.position.file.ordinal() + attackedFileCoordinate;
            final Position attackedPosition = new Position(attackedFileOrdinal, nextRankOrdinal);

            if (attackedPosition.isValid() == false)
                continue;

            final Tile attackedTile = board.getTile(attackedPosition);

            if (attackedTile.isOccupied() == false)
                continue;

            final Piece attackedPiece = attackedTile.getPiece();

            if (this.color.equals(attackedPiece.getColor()) == false)
                legalMoves.add(new PawnAttackMove(board, this, attackedPosition, attackedPiece));
        }

        return legalMoves;
    }

    @Override
    public Pawn movePiece(final Position destination) {
        return new Pawn(destination, this.color, true);
    }
}
