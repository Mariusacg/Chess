package engine.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import engine.Color;
import engine.board.Board;
import engine.board.Move;
import engine.board.MoveTransition;
import engine.board.Position;
import engine.board.Tile;
import engine.board.Move.LongCastleMove;
import engine.board.Move.ShortCastleMove;
import engine.board.MoveTransition.MoveStatus;
import engine.board.Position.File;
import engine.board.Position.Rank;
import engine.pieces.King;
import engine.pieces.Piece;
import engine.pieces.Rook;
import engine.pieces.Piece.PieceType;

public abstract class Player {
    protected final Board board;
    protected final King king;
    protected final Set<Move> legalMoves;
    protected final boolean isInCheck;
    protected final Rank playerFirstRank;

    Player(
            final Board board,
            final Set<Move> legalMoves,
            final Set<Move> opponentLegalMoves,
            final Rank playerFirstRank) {

        this.board = board;
        this.king = findKing(board);
        this.playerFirstRank = playerFirstRank;

        Set<Move> moves = new HashSet<>();

        for (final Move move : legalMoves)
            moves.add(move);

        for (final Move move : this.calculateCastleMoves(opponentLegalMoves))
            moves.add(move);

        this.legalMoves = moves;

        this.isInCheck = !getAttacksOnTile(this.king.getPosition(), opponentLegalMoves).isEmpty();
    }

    private List<Move> getAttacksOnTile(final Position position, final Set<Move> opponentLegalMoves) {
        final List<Move> attackMoves = new ArrayList<>();

        for (final Move opponentMove : opponentLegalMoves)
            if (position.equals(opponentMove.getDestinationPosition()))
                attackMoves.add(opponentMove);

        return attackMoves;
    }

    private King findKing(final Board board) {
        for (final Piece piece : getActivePieces())
            if (piece.getPieceType().equals(PieceType.KING))
                return (King) piece;

        throw new RuntimeException("Given configuration board is invalid");
    }

    public King getKing() {
        return this.king;
    }

    public Set<Move> getLegalMoves() {
        return this.legalMoves;
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && this.hasEscapeMoves() == false;
    }

    public boolean isInStaleMate() {
        return this.isInCheck == false && this.hasEscapeMoves() == false;
    }

    public boolean hasEscapeMoves() {
        for (final Move move : this.legalMoves) {
            final MoveTransition transition = makeMove(move);

            if (transition.getMoveStatus().equals(MoveStatus.DONE))
                return true;
        }

        return false;
    }

    public MoveTransition makeMove(final Move move) {
        if (isMoveLegal(move) == false)
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL);

        final Board transitionBoard = move.execute();

        final List<Move> kingAttacks = getAttacksOnTile(
                transitionBoard
                        .getCurrentPlayer()
                        .getOpponent()
                        .getKing()
                        .getPosition(),
                transitionBoard
                        .getCurrentPlayer()
                        .getLegalMoves());

        if (kingAttacks.isEmpty() == false)
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);

        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public Set<Move> calculateCastleMoves(final Set<Move> opponentLegalMoves) {
        final Set<Move> castleMoves = new HashSet<>();

        if (this.king.isFirstMove() && this.isInCheck == false) {
            final Position kingsBishopPosition = new Position(File.F, this.playerFirstRank);
            final Position kingsKnightPosition = new Position(File.G, this.playerFirstRank);
            final Position kingsRookPosition = new Position(File.H, this.playerFirstRank);

            final Position queenPosition = new Position(File.D, this.playerFirstRank);
            final Position queensBishopPosition = new Position(File.C, this.playerFirstRank);
            final Position queensKnightPosition = new Position(File.B, this.playerFirstRank);
            final Position queensRookPosition = new Position(File.A, this.playerFirstRank);

            if (this.board.getTile(kingsBishopPosition).isOccupied() == false &&
                    this.board.getTile(kingsKnightPosition).isOccupied() == false) {
                final Tile kingsRookTile = board.getTile(kingsRookPosition);

                if (kingsRookTile.isOccupied() &&
                        kingsRookTile.getPiece().isFirstMove() &&
                        kingsRookTile.getPiece().getPieceType().equals(PieceType.ROOK))

                    if (getAttacksOnTile(kingsBishopPosition, opponentLegalMoves).isEmpty() &&
                            getAttacksOnTile(kingsKnightPosition, opponentLegalMoves).isEmpty())
                        castleMoves.add(
                                new ShortCastleMove(
                                        this.board,
                                        this.king,
                                        kingsKnightPosition,
                                        (Rook) kingsRookTile.getPiece(),
                                        kingsRookPosition,
                                        kingsBishopPosition));
            }

            if (this.board.getTile(queenPosition).isOccupied() == false &&
                    this.board.getTile(queensBishopPosition).isOccupied() == false &&
                    this.board.getTile(queensKnightPosition).isOccupied() == false) {
                final Tile queensRookTile = board.getTile(queensRookPosition);

                if (queensRookTile.isOccupied() &&
                        queensRookTile.getPiece().isFirstMove() &&
                        queensRookTile.getPiece().getPieceType().equals(PieceType.ROOK))

                    if (getAttacksOnTile(queenPosition, opponentLegalMoves).isEmpty() &&
                            getAttacksOnTile(queensBishopPosition, opponentLegalMoves).isEmpty())
                        castleMoves.add(
                                new LongCastleMove(
                                        this.board,
                                        this.king,
                                        queensBishopPosition,
                                        (Rook) queensRookTile.getPiece(),
                                        queensRookPosition,
                                        queenPosition));
            }

        }

        return castleMoves;
    }

    public abstract List<Piece> getActivePieces();

    public abstract Color getColor();

    public abstract Player getOpponent();

    public static final class WhitePlayer extends Player {
        public WhitePlayer(Board board, Set<Move> legalMoves, Set<Move> opponentLegalMoves) {
            super(board, legalMoves, opponentLegalMoves, Rank.FIRST);
        }

        @Override
        public List<Piece> getActivePieces() {
            return this.board.getWhitePieces();
        }

        @Override
        public Color getColor() {
            return Color.WHITE;
        }

        @Override
        public Player getOpponent() {
            return this.board.getBlackPlayer();
        }
    }

    public static final class BlackPlayer extends Player {
        public BlackPlayer(Board board, Set<Move> legalMoves, Set<Move> opponentLegalMoves) {
            super(board, legalMoves, opponentLegalMoves, Rank.EIGHTH);
        }

        @Override
        public List<Piece> getActivePieces() {
            return this.board.getBlackPieces();
        }

        @Override
        public Color getColor() {
            return Color.BLACK;
        }

        @Override
        public Player getOpponent() {
            return this.board.getWhitePlayer();
        }
    }
}
