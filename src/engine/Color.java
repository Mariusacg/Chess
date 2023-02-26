package engine;

import engine.board.Board;
import engine.player.Player;

public enum Color {
    WHITE {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public Player choosePlayer(final Board board) {
            return board.getWhitePlayer();
        }
    },
    BLACK {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public Player choosePlayer(final Board board) {
            return board.getBlackPlayer();
        }
    };

    public abstract int getDirection();

    public abstract Player choosePlayer(final Board board);
}
