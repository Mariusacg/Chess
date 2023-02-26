package engine.board;

import java.util.HashMap;
import java.util.Map;

import engine.Color;
import engine.board.Position.File;
import engine.board.Position.Rank;
import engine.pieces.Piece;

public abstract class Tile {
    public static final int NUM_TILES = Position.NUM_RANKS * Position.NUM_FILES;
    private static final Map<Position, EmptyTile> EMPTY_TILES_CACHE = createAllEmptyTiles();

    protected final Position position;

    private Tile(final Position position) {
        this.position = position;
    }

    private static Map<Position, EmptyTile> createAllEmptyTiles() {
        final Map<Position, EmptyTile> emptyTiles = new HashMap<>();

        for (int rankOrdinal = Rank.FIRST.ordinal(); rankOrdinal <= Rank.EIGHTH.ordinal(); rankOrdinal++)
            for (int fileOrdinal = File.A.ordinal(); fileOrdinal <= File.H.ordinal(); fileOrdinal++) {
                Position p = new Position(fileOrdinal, rankOrdinal);

                emptyTiles.put(p, new EmptyTile(p));
            }

        return emptyTiles;
    }

    public static Tile createTile(final Position position) {
        return createTile(position, null);
    }

    public static Tile createTile(final Position position, final Piece piece) {
        return piece != null ? new OccupiedTile(position, piece) : EMPTY_TILES_CACHE.get(position);
    }

    public Position getPosition() {
        return this.position;
    }

    public abstract boolean isOccupied();

    public abstract Piece getPiece();

    public static final class EmptyTile extends Tile {
        private EmptyTile(final Position position) {
            super(position);
        }

        @Override
        public boolean isOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }
    }

    public static final class OccupiedTile extends Tile {
        private final Piece piece;

        private OccupiedTile(final Position position, final Piece piece) {
            super(position);
            this.piece = piece;
        }

        @Override
        public boolean isOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return piece;
        }

        @Override
        public String toString() {
            return this.piece.toString();
        }
    }
}
