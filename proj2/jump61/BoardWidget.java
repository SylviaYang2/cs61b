package jump61;

import ucb.gui2.Pad;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import java.util.concurrent.ArrayBlockingQueue;

import static jump61.Side.*;

/** A GUI component that displays a Jump61 board, and converts mouse clicks
 *  on that board to commands that are sent to the current Game.
 *  @author Mengmeng Yang
 */
class BoardWidget extends Pad {

    /** Length of the side of one square in pixels. */
    private static final int SQUARE_SIZE = 50;
    /** Width and height of a spot. */
    private static final int SPOT_DIM = 8;
    /** Minimum separation of center of a spot from a side of a square. */
    private static final int SPOT_MARGIN = 10;
    /** Width of the bars separating squares in pixels. */
    private static final int SEPARATOR_SIZE = 3;
    /** Width of square plus one separator. */
    private static final int SQUARE_SEP = SQUARE_SIZE + SEPARATOR_SIZE;

    /** Colors of various parts of the displayed board. */
    private static final Color
        NEUTRAL = Color.WHITE,
        SEPARATOR_COLOR = Color.BLACK,
        SPOT_COLOR = Color.BLACK,
        RED_TINT = new Color(255, 200, 200),
        BLUE_TINT = new Color(200, 200, 255);

    /** A new BoardWidget that monitors and displays a game Board, and
     *  converts mouse clicks to commands to COMMANDQUEUE. */
    BoardWidget(ArrayBlockingQueue<String> commandQueue) {
        _commandQueue = commandQueue;
        _side = 6 * SQUARE_SEP + SEPARATOR_SIZE;
        setMouseHandler("click", this::doClick);
    }

    /* .update and .paintComponent are synchronized because they are called
     *  by three different threads (the main thread, the thread that
     *  responds to events, and the display thread).  We don't want the
     *  saved copy of our Board to change while it is being displayed. */

    /** Update my display to show BOARD.  Here, we save a copy of
     *  BOARD (so that we can deal with changes to it only when we are ready
     *  for them), and recompute the size of the displayed board. */
    synchronized void update(Board board) {
        if (board.equals(_board)) {
            return;
        }
        if (_board != null && _board.size() != board.size()) {
            invalidate();
        }
        _board = new Board(board);
        _side = _board.size() * SQUARE_SEP + SEPARATOR_SIZE;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(_side, _side);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(_side, _side);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(_side, _side);
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        if (_board == null) {
            return;
        }
        g.setColor(NEUTRAL);
        g.fillRect(0, 0, _side, _side);
        g.setColor(SEPARATOR_COLOR);
        for (int i = 0; i < _side; i += SQUARE_SEP) {
            g.fillRect(0, i, _side, SEPARATOR_SIZE);
            g.fillRect(i, 0, SEPARATOR_SIZE, _side);
        }
        for (int i = 0; i < _board.size(); i++) {
            for (int j = 0; j < _board.size(); j++) {
                displaySpots(g, i, j);
            }
        }
    }

    /** Color and display the spots on the square at row R and column C
     *  on G.  (Used by paintComponent). */
    private void displaySpots(Graphics2D g, int r, int c) {
        int x = r * SQUARE_SEP + SEPARATOR_SIZE;
        int y = c * SQUARE_SEP + SEPARATOR_SIZE;
        if (_board.exists(r + 1, c + 1)) {
            int spotsNum = _board.get(r + 1, c + 1).getSpots();
            Side side = _board.get(r + 1, c + 1).getSide();
            if (side == WHITE) {
                drawSpots(g, spotsNum, x, y, NEUTRAL);
            } else if (side == RED) {
                drawSpots(g, spotsNum, x, y, RED_TINT);
            } else if (side == BLUE) {
                drawSpots(g, spotsNum, x, y, BLUE_TINT);
            }
        }
    }

    /** Draw one spot centered at position (X, Y) on G. */
    private void spot(Graphics2D g, int x, int y) {
        g.setColor(SPOT_COLOR);
        g.fillOval(x - SPOT_DIM / 2, y - SPOT_DIM / 2, SPOT_DIM, SPOT_DIM);
    }

    /**
     * Draw n spots at the (X, Y) coordinates.
     * @param g the Graph
     * @param n the number of spots
     * @param x coordinate x
     * @param y coordinate y
     * @param c the Color to be printed
     */
    private void drawSpots(Graphics2D g, int n, int x, int y, Color c) {
        g.setColor(c);
        g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
        if (n == 1) {
            spot(g, x + SPOT_MARGIN, y + SPOT_MARGIN);
        } else if (n == 2) {
            spot(g, x + SPOT_MARGIN, y + SPOT_MARGIN);
            spot(g, x + SPOT_MARGIN * 4, y + SPOT_MARGIN);
        } else if (n == 3) {
            spot(g, x + SPOT_MARGIN, y + SPOT_MARGIN);
            spot(g, x + SPOT_MARGIN * 4, y + SPOT_MARGIN);
            spot(g, x + SPOT_MARGIN, y + SPOT_MARGIN * 4);
        } else if (n == 4) {
            spot(g, x + SPOT_MARGIN, y + SPOT_MARGIN);
            spot(g, x + SPOT_MARGIN * 4, y + SPOT_MARGIN);
            spot(g, x + SPOT_MARGIN, y + SPOT_MARGIN * 4);
            spot(g, x + SPOT_MARGIN * 4, y + SPOT_MARGIN * 4);
        }
    }

    /** Respond to the mouse click depicted by EVENT. */
    public void doClick(String dummy, MouseEvent event) {
        /** x and y coordinates relative to the upper-left corner of the
        // upper-left square (increasing y is down). */
        int x = event.getX() - SEPARATOR_SIZE,
            y = event.getY() - SEPARATOR_SIZE;
        int r = (x - SEPARATOR_SIZE) / SQUARE_SIZE + 1;
        int c = (y - SEPARATOR_SIZE) / SQUARE_SIZE + 1;
        _commandQueue.offer(String.format("%d %d", r, c));
    }

    /** The Board I am displaying. */
    private Board _board;
    /** Dimension in pixels of one side of the board. */
    private int _side;
    /** Destination for commands derived from mouse clicks. */
    private ArrayBlockingQueue<String> _commandQueue;
}
