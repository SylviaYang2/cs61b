package jump61;

import ucb.gui2.TopLevel;
import ucb.gui2.LayoutSpec;

import java.util.concurrent.ArrayBlockingQueue;

import static jump61.Side.*;

/** The GUI controller for jump61.  To require minimal change to textual
 *  interface, we adopt the strategy of converting GUI input (mouse clicks)
 *  into textual commands that are sent to the Game object through a
 *  a Writer.  The Game object need never know where its input is coming from.
 *  A Display is an Observer of Games and Boards so that it is notified when
 *  either changes.
 *  @author Mengmeng Yang
 *  */
class Display extends TopLevel implements View, CommandSource, Reporter {

    /** A new window with given TITLE displaying GAME, and using COMMANDWRITER
     *  to send commands to the current game. */
    Display(String title) {
        super(title, true);

        addMenuButton("Game->Quit", this::quit);
        addMenuButton("Game->New Game", this::newGame);
        addMenuButton("Game->Manual red", this::manualAndAuto);
        addMenuButton("Game->Manual blue", this::manualAndAuto);
        addMenuButton("Game->Auto red", this::manualAndAuto);
        addMenuButton("Game->Auto blue", this::manualAndAuto);
        addMenuButton("Game->size 3", this::sizeThree);


        addLabel("Mred", "Mred",
                new LayoutSpec("x", 0, "y", 2, "height", 1, "width", 1));
        addLabel("Mblue", "Mblue",
                new LayoutSpec("x", 1, "y", 2, "height", 1, "width", 1));
        addLabel("Ared", "Ared",
                new LayoutSpec("x", 0, "y", 3, "height", 1, "width", 1));
        addLabel("Ablue", "Ablue",
                new LayoutSpec("x", 1, "y", 3, "height", 1, "width", 1));
        addLabel("Winner", "Winner",
                new LayoutSpec("x", 0, "y", 4, "height", 1, "width", 1));

        _boardWidget = new BoardWidget(_commandQueue);
        add(_boardWidget, new LayoutSpec("y", 1, "width", 2));
        display(true);
    }

    /** Response to "Quit" button click. */
    void quit(String dummy) {
        System.exit(0);
    }

    /** Response to "New Game" button click. */
    void newGame(String dummy) {
        _commandQueue.offer("new");
    }

    void manualAndAuto(String input) {
        String[] setting = input.split("->")[1].split(" ");
        String mode = setting[0];
        String color = setting[1];
        if (mode.equals("Manual")) {
            if (color.equals("red")) {
                setLabel("Mred", "Manual red");
            } else {
                setLabel("Mblue", "Manual blue");
            }
        } else if (mode.equals("Auto")) {
            if (color.equals("red")) {
                setLabel("Ared", "Auto red");
            } else {
                setLabel("Ablue", "Auto blue");
            }
        }
        _commandQueue.offer(input.split("->")[1]);
    }

    void sizeThree(String dummy) {
        newGame(dummy);
        _commandQueue.offer("size 3");
//        newGame(dummy);
    }

    @Override
    public void update(Board board) {
        Side side = board.getWinner();
        if (side != null) {
            setLabel("Winner", String.format("%s wins!!!", side));
        }
        _boardWidget.update(board);
        pack();
        _boardWidget.repaint();
    }

    @Override
    public String getCommand(String ignored) {
        try {
            return _commandQueue.take();
        } catch (InterruptedException excp) {
            throw new Error("unexpected interrupt");
        }
    }

    @Override
    public void announceWin(Side side) {
        showMessage(String.format("%s wins!", side.toCapitalizedString()),
                    "Game Over", "information");
    }

    @Override
    public void announceMove(int row, int col) {
    }

    @Override
    public void msg(String format, Object... args) {
        showMessage(String.format(format, args), "", "information");
    }

    @Override
    public void err(String format, Object... args) {
        showMessage(String.format(format, args), "Error", "error");
    }

    /** Time interval in msec to wait after a board update. */
    static final long BOARD_UPDATE_INTERVAL = 50;

    /** The widget that displays the actual playing board. */
    private BoardWidget _boardWidget;
    /** Queue for commands going to the controlling Game. */
    private final ArrayBlockingQueue<String> _commandQueue =
        new ArrayBlockingQueue<>(5);
}
