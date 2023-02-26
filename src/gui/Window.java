package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import engine.board.Board;
import engine.board.Move;
import engine.board.MoveTransition;
import engine.board.MoveTransition.MoveStatus;
import engine.board.Position;
import engine.board.Position.File;
import engine.board.Position.Rank;
import engine.board.Tile;
import engine.board.Move.MoveFactory;
import engine.pieces.Piece;

public class Window {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private static final Dimension IMAGE_DIMENSION = new Dimension(70, 70);
    private static final Color lightTileColor = Color.decode("#FFFACD");
    private static final Color darkTileColor = Color.decode("#593E1A");
    private static final String IMAGES_PATH = "images/";

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private Board gameBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    public Window() {
        this.gameBoard = Board.createStandardBoard();
        this.gameFrame = new JFrame("JavaChess");
        this.boardPanel = new BoardPanel();

        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setJMenuBar(createMenuBar());
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setResizable(false);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);

        this.gameFrame.setVisible(true);
    }

    private static JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());

        return menuBar;

    }

    private static JMenu createFileMenu() {
        final JMenu menuBar = new JMenu("File");

        final JMenuItem loadPGN = new JMenuItem("Load PGN File");

        loadPGN.addActionListener(e -> {
            System.out.println("load pgn");
        });

        final JMenuItem exiItem = new JMenuItem("Exit");

        exiItem.addActionListener(e -> {
            System.exit(0);
        });

        menuBar.add(loadPGN);
        menuBar.add(exiItem);

        return menuBar;
    }

    private class BoardPanel extends JPanel {
        final ArrayList<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(Position.NUM_RANKS, Position.NUM_FILES));

            this.boardTiles = new ArrayList<>();

            for (int rankOrdinal = Rank.EIGHTH.ordinal(); rankOrdinal >= Rank.FIRST.ordinal(); rankOrdinal--)
                for (int fileOrdinal = File.A.ordinal(); fileOrdinal <= File.H.ordinal(); fileOrdinal++) {
                    final Position tilePosition = new Position(fileOrdinal, rankOrdinal);
                    final TilePanel tilePanel = new TilePanel(this, tilePosition);
                    this.boardTiles.add(tilePanel);
                    this.add(tilePanel);
                }

            this.setPreferredSize(BOARD_PANEL_DIMENSION);
        }

        void drawBoard(final Board board) {
            this.removeAll();

            for (final TilePanel tilePanel : boardTiles) {
                tilePanel.drawTile(board);
                add(tilePanel);

            }
            this.validate();
            this.repaint();
        }
    }

    private class TilePanel extends JPanel {
        private final Position tilePosition;

        TilePanel(final BoardPanel boardPanel, final Position position) {
            super(new GridLayout());

            this.tilePosition = position;
            this.setPreferredSize(TILE_PANEL_DIMENSION);
            this.assignTileColor();
            this.assignPieceImage(gameBoard);
            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            // first click
                            sourceTile = gameBoard.getTile(tilePosition);
                            humanMovedPiece = sourceTile.getPiece();

                            if (humanMovedPiece == null)
                                sourceTile = null;
                            else
                                System.out.println("Source tile: " + sourceTile.getPosition());

                        } else {
                            // second click

                            destinationTile = gameBoard.getTile(tilePosition);

                            if (destinationTile.getPosition().equals(sourceTile.getPosition())) {
                                destinationTile = null;
                            } else {
                                System.out.println("Destination tile: " + destinationTile.getPosition());

                                final Move move = MoveFactory.create(
                                        gameBoard,
                                        sourceTile.getPosition(),
                                        destinationTile.getPosition());

                                final MoveTransition transition = gameBoard.getCurrentPlayer().makeMove(move);

                                if (transition.getMoveStatus().equals(MoveStatus.DONE))
                                    gameBoard = transition.getBoard();

                                sourceTile = null;
                                destinationTile = null;
                                humanMovedPiece = null;
                            }
                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                boardPanel.drawBoard(gameBoard);
                            }

                        });
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    }
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                }

            });

            this.validate();
        }

        private void assignPieceImage(final Board board) {
            this.removeAll();

            if (board.getTile(this.tilePosition).isOccupied()) {
                try {

                    final BufferedImage bufferImage = ImageIO.read(
                            new java.io.File(
                                    IMAGES_PATH +
                                            board.getTile(this.tilePosition).getPiece().getImage()));

                    final Image image = bufferImage
                            .getScaledInstance(
                                    (int) IMAGE_DIMENSION.getWidth(),
                                    (int) IMAGE_DIMENSION.getHeight(),
                                    Image.SCALE_SMOOTH);

                    this.add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            final int coordinate = this.tilePosition.getTileCoordinate();

            this.setBackground((coordinate + coordinate / 8) % 2 == 0 ? darkTileColor : lightTileColor);
        }

        public void drawTile(final Board board) {
            this.assignTileColor();
            this.assignPieceImage(board);
            this.validate();
            this.repaint();
        }
    }
}
