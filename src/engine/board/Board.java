package engine.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import engine.Color;
import engine.board.Position.File;
import engine.board.Position.Rank;
import engine.pieces.Bishop;
import engine.pieces.King;
import engine.pieces.Knight;
import engine.pieces.Pawn;
import engine.pieces.Piece;
import engine.pieces.Queen;
import engine.pieces.Rook;
import engine.player.Player;
import engine.player.Player.BlackPlayer;
import engine.player.Player.WhitePlayer;

public class Board {
    private final List<Tile> gameBoard;

    private final List<Piece> whitePieces;
    private final List<Piece> blackPieces;

    private final Player whitePlayer;
    private final Player blackPlayer;
    private final Player currentPlayer;

    private Board(final BoardBuilder builder) {
        this.gameBoard = createBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Color.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Color.BLACK);

        final Set<Move> whiteLegalMoves = calculateLegalMoves(this.whitePieces);
        final Set<Move> blackLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackLegalMoves, whiteLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this);
    }

    private static List<Tile> createBoard(final BoardBuilder builder) {
        final Tile[] tiles = new Tile[Tile.NUM_TILES];
        int i = 0;

        for (int rankOrdinal = Rank.FIRST.ordinal(); rankOrdinal <= Rank.EIGHTH.ordinal(); rankOrdinal++)
            for (int fileOrdinal = File.A.ordinal(); fileOrdinal <= File.H.ordinal(); fileOrdinal++) {
                Position position = new Position(fileOrdinal, rankOrdinal);
                Piece piece = builder.boardConfig.get(position);

                tiles[i++] = Tile.createTile(position, piece);
            }

        return Arrays.asList(tiles);
    }

    private static List<Piece> calculateActivePieces(final List<Tile> gameBoard, final Color color) {
        final List<Piece> activePieces = new ArrayList<>();

        for (final Tile tile : gameBoard) {
            if (tile.isOccupied() == false)
                continue;

            final Piece piece = tile.getPiece();

            if (piece.getColor().equals(color))
                activePieces.add(piece);
        }

        return activePieces;
    }

    public static Board createStandardBoard() {
        BoardBuilder builder = new BoardBuilder();

        builder.setPiece(new Rook(new Position(File.A, Rank.FIRST), Color.WHITE, false))
                .setPiece(new Knight(new Position(File.B, Rank.FIRST), Color.WHITE, false))
                .setPiece(new Bishop(new Position(File.C, Rank.FIRST), Color.WHITE, false))
                .setPiece(new Queen(new Position(File.D, Rank.FIRST), Color.WHITE, false))
                .setPiece(new King(new Position(File.E, Rank.FIRST), Color.WHITE, false))
                .setPiece(new Bishop(new Position(File.F, Rank.FIRST), Color.WHITE, false))
                .setPiece(new Knight(new Position(File.G, Rank.FIRST), Color.WHITE, false))
                .setPiece(new Rook(new Position(File.H, Rank.FIRST), Color.WHITE, false))
                .setPiece(new Pawn(new Position(File.A, Rank.SECOND), Color.WHITE, false))
                .setPiece(new Pawn(new Position(File.B, Rank.SECOND), Color.WHITE, false))
                .setPiece(new Pawn(new Position(File.C, Rank.SECOND), Color.WHITE, false))
                .setPiece(new Pawn(new Position(File.D, Rank.SECOND), Color.WHITE, false))
                .setPiece(new Pawn(new Position(File.E, Rank.SECOND), Color.WHITE, false))
                .setPiece(new Pawn(new Position(File.F, Rank.SECOND), Color.WHITE, false))
                .setPiece(new Pawn(new Position(File.G, Rank.SECOND), Color.WHITE, false))
                .setPiece(new Pawn(new Position(File.H, Rank.SECOND), Color.WHITE, false));

        builder.setPiece(new Rook(new Position(File.A, Rank.EIGHTH), Color.BLACK, false))
                .setPiece(new Knight(new Position(File.B, Rank.EIGHTH), Color.BLACK, false))
                .setPiece(new Bishop(new Position(File.C, Rank.EIGHTH), Color.BLACK, false))
                .setPiece(new Queen(new Position(File.D, Rank.EIGHTH), Color.BLACK, false))
                .setPiece(new King(new Position(File.E, Rank.EIGHTH), Color.BLACK, false))
                .setPiece(new Bishop(new Position(File.F, Rank.EIGHTH), Color.BLACK, false))
                .setPiece(new Knight(new Position(File.G, Rank.EIGHTH), Color.BLACK, false))
                .setPiece(new Rook(new Position(File.H, Rank.EIGHTH), Color.BLACK, false))
                .setPiece(new Pawn(new Position(File.A, Rank.SEVENTH), Color.BLACK, false))
                .setPiece(new Pawn(new Position(File.B, Rank.SEVENTH), Color.BLACK, false))
                .setPiece(new Pawn(new Position(File.C, Rank.SEVENTH), Color.BLACK, false))
                .setPiece(new Pawn(new Position(File.D, Rank.SEVENTH), Color.BLACK, false))
                .setPiece(new Pawn(new Position(File.E, Rank.SEVENTH), Color.BLACK, false))
                .setPiece(new Pawn(new Position(File.F, Rank.SEVENTH), Color.BLACK, false))
                .setPiece(new Pawn(new Position(File.G, Rank.SEVENTH), Color.BLACK, false))
                .setPiece(new Pawn(new Position(File.H, Rank.SEVENTH), Color.BLACK, false));

        builder.setMoveMaker(Color.WHITE);

        return builder.build();
    }

    private Set<Move> calculateLegalMoves(final List<Piece> pieces) {
        final Set<Move> legalMoves = new HashSet<>();

        for (final Piece piece : pieces)
            legalMoves.addAll(piece.getLegalMoves(this));

        return legalMoves;
    }

    public Tile getTile(final Position tilePosition) {
        return this.gameBoard.get(tilePosition.getTileCoordinate());
    }

    public List<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    public List<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Player getWhitePlayer() {
        return this.whitePlayer;
    }

    public Player getBlackPlayer() {
        return this.blackPlayer;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public Set<Move> getAllLegalMoves() {
        final Set<Move> legalMoves = new HashSet<>();

        for (final Move move : this.whitePlayer.getLegalMoves())
            legalMoves.add(move);

        for (final Move move : this.blackPlayer.getLegalMoves())
            legalMoves.add(move);

        return legalMoves;

    }

    // for debugging purposes
    public void printLegalMoves() {
        try {
            java.io.FileWriter f = new java.io.FileWriter("a.txt");

            for (final Move move : this.getAllLegalMoves())
                f.write(move.toString() + "\n");

            f.close();
            System.out.println("Printed moves to file");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (int rankOrdinal = Rank.EIGHTH.ordinal(); rankOrdinal >= Rank.FIRST.ordinal(); rankOrdinal--) {
            for (int fileOrdinal = File.A.ordinal(); fileOrdinal <= File.H.ordinal(); fileOrdinal++) {
                final String tileString = this.gameBoard
                        .get(Position.getTileCoordinate(fileOrdinal, rankOrdinal))
                        .toString();

                sb.append(' ' + tileString + ' ');
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    public static class BoardBuilder {
        Map<Position, Piece> boardConfig;
        Color nextMoveMaker;
        Pawn enPassantPawn;

        public BoardBuilder() {
            boardConfig = new HashMap<Position, Piece>();
        }

        public BoardBuilder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPosition(), piece);
            return this;
        }

        public BoardBuilder setMoveMaker(final Color nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public void setEnPassant(Pawn movedPawn) {
            this.enPassantPawn = movedPawn;
        }

        public Board build() {
            return new Board(this);
        }
    }
}
