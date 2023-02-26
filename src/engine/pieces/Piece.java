package engine.pieces;

import java.util.Set;

import engine.Color;
import engine.board.Board;
import engine.board.Move;
import engine.board.Position;

public abstract class Piece {
    private final int cachedHashCode;

    protected final Position position;
    protected final Color color;
    protected final boolean hasMoved;
    protected final PieceType type;

    Piece(final PieceType type, final Position position, final Color color, final boolean hasMoved) {
        this.position = position;
        this.color = color;
        this.hasMoved = hasMoved;
        this.type = type;
        this.cachedHashCode = calculateHashCode(this);
    }

    private static int calculateHashCode(final Piece piece) {
        int hash = piece.position.hashCode();
        hash = 31 * hash + piece.type.hashCode();
        hash = 31 * hash + piece.color.hashCode();
        hash = 31 * hash + piece.color.hashCode();
        hash = 31 * hash + (piece.hasMoved ? 1 : 0);

        return hash;
    }

    public Color getColor() {
        return this.color;
    }

    public Position getPosition() {
        return this.position;
    }

    public PieceType getPieceType() {
        return this.type;
    }

    public boolean isFirstMove() {
        return !this.hasMoved;
    }

    public String getImage() {
        return (this.color.equals(Color.WHITE) ? "w" : "b") +
                this.type.toString().toLowerCase() +
                ".png";
    }

    @Override
    public String toString() {
        String str = type.toString();

        return this.color.equals(Color.BLACK) ? str.toLowerCase() : str;

    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (other instanceof Piece == false)
            return false;

        Piece otherPiece = (Piece) other;

        return this.position.equals(otherPiece.position) &&
                this.type.equals(otherPiece.type) &&
                this.color.equals(otherPiece.color) &&
                this.hasMoved == otherPiece.hasMoved;
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    public abstract Set<Move> getLegalMoves(final Board board);

    public abstract Piece movePiece(final Position destination);

    public enum PieceType {
        PAWN('P'),
        KNIGHT('N'),
        BISHOP('B'),
        ROOK('R'),
        QUEEN('Q'),
        KING('K');

        private final char pieceChar;

        PieceType(final char pieceChar) {
            this.pieceChar = pieceChar;
        }

        @Override
        public String toString() {
            return String.valueOf(this.pieceChar);
        }
    }
}
