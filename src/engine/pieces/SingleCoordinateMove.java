package engine.pieces;

import java.util.HashSet;
import java.util.Set;

import engine.board.Board;
import engine.board.Move;
import engine.board.Position;
import engine.board.Tile;
import engine.board.Move.AttackMove;
import engine.board.Move.NormalMove;

public interface SingleCoordinateMove {
    default Set<Move> getLegalMoves(
            final Board board,
            final Piece piece,
            final int[] relativeFileCoordinates,
            final int[] relativeRankCoordinates) {

        final HashSet<Move> legalMoves = new HashSet<>();

        for (int i = 0; i < relativeFileCoordinates.length; i++) {
            int fileOrdinal = piece.getPosition().file.ordinal() + relativeFileCoordinates[i];
            int rankOrdinal = piece.getPosition().rank.ordinal() + relativeRankCoordinates[i];
            Position destinationPosition = new Position(fileOrdinal, rankOrdinal);

            if (destinationPosition.isValid() == false)
                continue;

            final Tile destinationTile = board.getTile(destinationPosition);

            if (destinationTile.isOccupied() == false) {
                legalMoves.add(new NormalMove(board, piece, destinationPosition));
                continue;
            }

            final Piece pieceAtDestination = destinationTile.getPiece();

            if (piece.getColor().equals(pieceAtDestination.getColor()) == false)
                legalMoves.add(new AttackMove(board, piece, destinationPosition, pieceAtDestination));

        }

        return legalMoves;
    }
}
