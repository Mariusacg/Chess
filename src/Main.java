import engine.board.Board;
import gui.Window;

public class Main {
    public static void main(String[] args) {
        Board board = Board.createStandardBoard();
        System.out.println(board);

        Window window = new Window();
    }
}
