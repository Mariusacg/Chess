package engine.board;

import java.util.HashSet;
import java.util.Set;

import engine.board.Board.BoardBuilder;
import engine.pieces.Pawn;
import engine.pieces.Piece;
import engine.pieces.Rook;

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final Position destinationPosition;

    public static final NullMove NULL_MOVE = new NullMove();

    private Move(final Board board, final Piece movedPiece, final Position destinationPosition) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationPosition = destinationPosition;
    }

    protected BoardBuilder copyBoardWithoutPieces(final HashSet<Piece> excludedPieces) {
        final BoardBuilder builder = new BoardBuilder();

        for (final Piece piece : this.board.getCurrentPlayer().getActivePieces())
            if (excludedPieces.contains(piece) == false)
                builder.setPiece(piece);

        for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces())
            if (excludedPieces.contains(piece) == false)
                builder.setPiece(piece);

        return builder;
    }

    public Board execute() {
        final HashSet<Piece> excludedPieces = new HashSet<>();
        excludedPieces.add(this.movedPiece);

        final BoardBuilder builder = this.copyBoardWithoutPieces(excludedPieces);

        builder.setPiece(movedPiece.movePiece(this.destinationPosition));
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getColor());

        return builder.build();
    }

    public Position getCurrentPosition() {
        return this.movedPiece.getPosition();
    }

    public Position getDestinationPosition() {
        return destinationPosition;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public boolean isAttackMove() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (other instanceof Move == false)
            return false;

        final Move otherMove = (Move) other;

        return this.movedPiece.equals(otherMove.getMovedPiece()) &&
                this.destinationPosition.equals(otherMove.getDestinationPosition());
    }

    @Override
    public int hashCode() {
        int hash = 1;

        hash = 31 * hash + this.movedPiece.hashCode();
        hash = 31 * hash + this.destinationPosition.hashCode();

        return hash;
    }

    public static final class NormalMove extends Move {
        public NormalMove(
                final Board board,
                final Piece movedPiece,
                final Position destinationPosition) {

            super(board, movedPiece, destinationPosition);
        }

        @Override
        public String toString() {
            return this.movedPiece.toString() + this.destinationPosition.toString();
        }
    }

    public static class AttackMove extends Move {
        final Piece attackedPiece;

        public AttackMove(
                final Board board,
                final Piece movedPiece,
                final Position destinationPosition,
                final Piece attackedPiece) {

            super(board, movedPiece, destinationPosition);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public boolean isAttackMove() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other)
                return true;

            if (other instanceof AttackMove == false)
                return false;

            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(other)
                    && this.attackedPiece.equals(otherAttackMove.attackedPiece);
        }

        @Override
        public int hashCode() {
            return 31 * super.hashCode() + this.attackedPiece.hashCode();
        }

        @Override
        public String toString() {
            return this.movedPiece.toString() + "x" + this.destinationPosition.toString();
        }
    }

    public static class PawnMove extends Move {
        public PawnMove(
                final Board board,
                final Piece movedPiece,
                final Position destinationPosition) {

            super(board, movedPiece, destinationPosition);
        }

        @Override
        public String toString() {
            return this.destinationPosition.toString();
        }
    }

    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(
                final Board board,
                final Piece movedPiece,
                final Position destinationPosition,
                final Piece attackedPiece) {

            super(board, movedPiece, destinationPosition, attackedPiece);
        }

        @Override
        public String toString() {
            return this.movedPiece
                    .getPosition().file
                    .name()
                    .toLowerCase()
                    + "x"
                    + this.destinationPosition.toString();
        }
    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove {
        public PawnEnPassantAttackMove(
                final Board board,
                final Piece movedPiece,
                final Position destinationPosition,
                final Piece attackedPiece) {

            super(board, movedPiece, destinationPosition, attackedPiece);
        }
    }

    public static final class PawnJumpMove extends PawnMove {
        public PawnJumpMove(final Board board, final Piece movedPiece, final Position destinationPosition) {
            super(board, movedPiece, destinationPosition);
        }

        @Override
        public Board execute() {
            final HashSet<Piece> excludedPieces = new HashSet<>();
            excludedPieces.add(this.movedPiece);

            final BoardBuilder builder = this.copyBoardWithoutPieces(excludedPieces);

            final Pawn movedPawn = (Pawn) movedPiece.movePiece(this.destinationPosition);

            builder.setPiece(movedPawn);
            builder.setEnPassant(movedPawn);
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getColor());

            return builder.build();
        }
    }

    static abstract class CastleMove extends Move {
        protected final Rook castledRook;
        protected final Position rookStartPosition;
        protected final Position rookDestinationPosition;

        public CastleMove(
                final Board board,
                final Piece movedPiece,
                final Position destinationPosition,
                final Rook castledRook,
                final Position rookStartPosition,
                final Position rookDestinationPosition) {

            super(board, movedPiece, destinationPosition);
            this.castledRook = castledRook;
            this.rookStartPosition = rookStartPosition;
            this.rookDestinationPosition = rookDestinationPosition;
        }

        public Rook getCastledRook() {
            return this.castledRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final HashSet<Piece> excludedPieces = new HashSet<>();
            excludedPieces.add(this.movedPiece);
            excludedPieces.add(this.castledRook);

            final BoardBuilder builder = this.copyBoardWithoutPieces(excludedPieces);

            builder.setPiece(movedPiece.movePiece(this.destinationPosition));
            builder.setPiece(new Rook(this.rookDestinationPosition, this.castledRook.getColor(), false));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getColor());

            return builder.build();
        }
    }

    public static final class ShortCastleMove extends CastleMove {
        public ShortCastleMove(
                final Board board,
                final Piece movedPiece,
                final Position destinationPosition,
                final Rook castledRook,
                final Position rookStartPosition,
                final Position rookDestinationPosition) {

            super(board, movedPiece, destinationPosition, castledRook, rookStartPosition, rookDestinationPosition);
        }

        @Override
        public String toString() {
            return "O-O";
        }
    }

    public static final class LongCastleMove extends CastleMove {
        public LongCastleMove(final Board board,
                final Piece movedPiece,
                final Position destinationPosition,
                final Rook castledRook,
                final Position rookStartPosition,
                final Position rookDestinationPosition) {

            super(board, movedPiece, destinationPosition, castledRook, rookStartPosition, rookDestinationPosition);
        }

        @Override
        public String toString() {
            return "O-O-O";
        }
    }

    private static final class NullMove extends Move {
        public NullMove() {
            super(null, null, new Position(Position.File.NONE, Position.Rank.NONE));
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute NullMove");
        }
    }

    public static class MoveFactory {
        private MoveFactory() {
            throw new RuntimeException("MoveFactory cannot be instantiated");
        }

        public static Move create(
                final Board board,
                final Position currentPosition,
                final Position destinationPosition) {

            Set<Move> legalMoves = board.getAllLegalMoves();

            for (final Move move : legalMoves)
                if (currentPosition.equals(move.getCurrentPosition())
                        && destinationPosition.equals(move.getDestinationPosition()))
                    return move;

            return NULL_MOVE;
        }
    }
}
