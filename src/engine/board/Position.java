package engine.board;

public class Position {
    public static final int NUM_RANKS = 8;
    public static final int NUM_FILES = 8;

    public final File file;
    public final Rank rank;

    private final int cachedTileCoordinate;

    public Position(final File file, final Rank rank) {
        this.file = file;
        this.rank = rank;
        this.cachedTileCoordinate = computeTileCoordinate(this.file, this.rank);
    }

    public Position(final int fileOrdinal, final int rankOrdinal) {
        this.file = fileOrdinal < 0 || fileOrdinal >= File.values().length
                ? File.NONE
                : File.values()[fileOrdinal];

        this.rank = rankOrdinal < 0 || rankOrdinal >= Rank.values().length
                ? Rank.NONE
                : Rank.values()[rankOrdinal];

        this.cachedTileCoordinate = computeTileCoordinate(this.file, this.rank);
    }

    private static int computeTileCoordinate(final File file, final Rank rank) {
        return file.equals(File.NONE) || rank.equals(Rank.NONE)
                ? -1
                : getTileCoordinate(file.ordinal(), rank.ordinal());
    }

    public static int getTileCoordinate(final int fileOrdinal, final int rankOrdinal) {
        return rankOrdinal * NUM_RANKS + fileOrdinal;
    }

    public boolean isValid() {
        return this.file.equals(File.NONE) == false && this.rank.equals(Rank.NONE) == false;
    }

    public int getTileCoordinate() {
        return cachedTileCoordinate;
    }

    @Override
    public String toString() {
        return this.file.name().toLowerCase() + (this.rank.ordinal() + 1);
    }

    @Override
    public int hashCode() {
        return this.getTileCoordinate();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other)
            return true;

        if (other instanceof Position == false)
            return false;

        Position otherPosition = (Position) other;

        return this.file.equals(otherPosition.file) && this.rank.equals(otherPosition.rank);
    }

    public enum File {
        A, B, C, D, E, F, G, H, NONE
    }

    public enum Rank {
        FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH, NONE
    }
}
