package jump61;

import java.util.ArrayDeque;
import java.util.Formatter;

import java.util.function.Consumer;

import static jump61.Side.*;
import static jump61.Square.square;

/** Represents the state of a Jump61 game.  Squares are indexed either by
 *  row and column (between 1 and size()), or by square number, numbering
 *  squares by rows, with squares in row 1 numbered from 0 to size()-1, in
 *  row 2 numbered from size() to 2*size() - 1, etc. (i.e., row-major order).
 *
 *  A Board may be given a notifier---a Consumer<Board> whose
 *  .accept method is called whenever the Board's contents are changed.
 *
 *  @author Mengmeng Yang
 */
class Board {

    /** An uninitialized Board.  Only for use by subtypes. */
    protected Board() {
        _notifier = NOP;
    }

    /** An N x N board in initial configuration. */
    Board(int N) {
        this();
        _board = new Square[N * N];
        for (int i = 0; i < N * N; i++) {
            _board[i] = square(WHITE, 1);
        }
        _readonlyBoard = new ConstantBoard(this);
    }

    /** A board whose initial contents are copied from BOARD0, but whose
     *  undo history is clear, and whose notifier does nothing. */
    Board(Board board0) {
        this(board0.size());
        int newSize = board0.size() * board0.size();
        _board = new Square[newSize];
        for (int i = 0; i < newSize; i++) {
            _board[i] =
                    square(board0.get(i).getSide(), board0.get(i).getSpots());
        }
        _history.clear();
        _readonlyBoard = new ConstantBoard(this);
    }

    /** Returns a readonly version of this board. */
    Board readonlyBoard() {
        return _readonlyBoard;
    }

    /** (Re)initialize me to a cleared board with N squares on a side. Clears
     *  the undo history and sets the number of moves to 0. */
    void clear(int N) {
        _board = new Square[N * N];
        for (int i = 0; i < N * N; i++) {
            _board[i] = square(WHITE, 1);
        }
        _history.clear();
        _moves = 0;
        announce();
    }

    /** Copy the contents of BOARD into me. */
    void copy(Board board) {
        internalCopy(board);
    }

    /** Copy the contents of BOARD into me, without modifying my undo
     *  history. Assumes BOARD and I have the same size. */
    private void internalCopy(Board board) {
        assert size() == board.size();
        int newSize = board.size() * board.size();
        _board = new Square[newSize];
        for (int i = 0; i < newSize; i++) {
            _board[i] = square(board.get(i).getSide(), board.get(i).getSpots());
        }
        _readonlyBoard = board._readonlyBoard;
    }

    /** Return the number of rows and of columns of THIS. */
    int size() {
        return (int) Math.sqrt(_board.length);
    }

    /** Returns the contents of the square at row R, column C
     *  1 <= R, C <= size (). */
    Square get(int r, int c) {
        return get(sqNum(r, c));
    }

    /** Returns the contents of square #N, numbering squares by rows, with
     *  squares in row 1 number 0 - size()-1, in row 2 numbered
     *  size() - 2*size() - 1, etc. */
    Square get(int n) {
        return _board[n];
    }

    /** Returns the total number of spots on the board. */
    int numPieces() {
        int total = 0;
        for (int i = 0; i < _board.length; i++) {
            total += _board[i].getSpots();
        }
        return total;
    }

    /** Returns the Side of the player who would be next to move.  If the
     *  game is won, this will return the loser (assuming legal position). */
    Side whoseMove() {
        return ((numPieces() + size()) & 1) == 0 ? RED : BLUE;
    }

    /** Return true iff row R and column C denotes a valid square. */
    final boolean exists(int r, int c) {
        return 1 <= r && r <= size() && 1 <= c && c <= size();
    }

    /** Return true iff S is a valid square number. */
    final boolean exists(int s) {
        int N = size();
        return 0 <= s && s < N * N;
    }

    /** Return the row number for square #N. */
    final int row(int n) {
        return n / size() + 1;
    }

    /** Return the column number for square #N. */
    final int col(int n) {
        return n % size() + 1;
    }

    /** Return the square number of row R, column C. */
    final int sqNum(int r, int c) {
        return (c - 1) + (r - 1) * size();
    }

    /** Return a string denoting move (ROW, COL)N. */
    String moveString(int row, int col) {
        return String.format("%d %d", row, col);
    }

    /** Return a string denoting move N. */
    String moveString(int n) {
        return String.format("%d %d", row(n), col(n));
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
        to square at row R, column C. */
    boolean isLegal(Side player, int r, int c) {
        return isLegal(player, sqNum(r, c));
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
     *  to square #N. */
    boolean isLegal(Side player, int n) {
        return n >= 0 && n < size() * size()
                && player.playableSquare(get(n).getSide()) && isLegal(player);
    }

    /** Returns true iff PLAYER is allowed to move at this point. */
    boolean isLegal(Side player) {
        return whoseMove().equals(player) && getWinner() == null;
    }

    /** Returns the winner of the current position, if the game is over,
     *  and otherwise null. */
    final Side getWinner() {
        if (numOfSide(RED) == _board.length) {
            return RED;
        } else if (numOfSide(BLUE) == _board.length) {
            return BLUE;
        }
        return null;
    }

    /** Return the number of squares of given SIDE. */
    int numOfSide(Side side) {
        int count = 0;
        for (int i = 0; i < _board.length; i++) {
            if (_board[i].getSide() == side) {
                count += 1;
            }
        }
        return count;
    }

    /** Add a spot from PLAYER at row R, column C.  Assumes
     *  isLegal(PLAYER, R, C). */
    void addSpot(Side player, int r, int c) {
        markUndo();
        _moves += 1;
        simpleAdd(player, r, c, 1);
        jump(sqNum(r, c));
    }

    /** Add a spot from PLAYER at square #N.  Assumes isLegal(PLAYER, N). */
    void addSpot(Side player, int n) {
        addSpot(player, row(n), col(n));
    }

    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white). */
    void set(int r, int c, int num, Side player) {
        internalSet(r, c, num, player);
        announce();
    }

    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white).  Does not announce
     *  changes. */
    private void internalSet(int r, int c, int num, Side player) {
        internalSet(sqNum(r, c), num, player);
    }

    /** Set the square #N to NUM spots (0 <= NUM), and give it color PLAYER
     *  if NUM > 0 (otherwise, white). Does not announce changes. */
    private void internalSet(int n, int num, Side player) {
        if (num > 0) {
            _board[n] = square(player, num);
        } else {
            _board[n] = square(WHITE, num);
        }
    }

     /** There are two obvious ways to conduct a game-tree search in the AI.

     First, you can explore the consequences of a possible move from
     position A by making a copy of the Board in position A, and then
     modifying that copy. Since you retain position A, you can return to
     it to try other moves from that position.

     Second, you can explore the consequences of a possible move from
     position A by making that move on your Board and then, when your
     analysis of the move is complete, undoing the move to return you to
     position A. This method is more complicated to implement, but has
     the advantage that it can be considerably faster than making copies
     of the Board (you will need one copy per move tried, which will very
     quickly be thrown away). */

    /** Undo the effects of one move (that is, one addSpot command).  One
     *  can only undo back to the last point at which the undo history
     *  was cleared, or the construction of this Board. */
    void undo() {
        copy(_history.removeLast());
    }

    /** Record the beginning of a move in the undo history. */
    private void markUndo() {
        _history.add(new Board(this));
    }

    /** Add DELTASPOTS spots of side PLAYER to row R, column C,
     *  updating counts of numbers of squares of each color. */
    private void simpleAdd(Side player, int r, int c, int deltaSpots) {
        internalSet(r, c, deltaSpots + get(r, c).getSpots(), player);
    }

    /** Add DELTASPOTS spots of color PLAYER to square #N,
     *  updating counts of numbers of squares of each color. */
    private void simpleAdd(Side player, int n, int deltaSpots) {
        internalSet(n, deltaSpots + get(n).getSpots(), player);
    }

    /** Used in jump to keep track of squares needing processing.  Allocated
     *  here to cut down on allocations. */
    private final ArrayDeque<Integer> _workQueue = new ArrayDeque<>();

    /** Do all jumping on this board, assuming that initially, S is the only
     *  square that might be over-full. */
    private void jump(int S) {
        if (getWinner() != null) {
            return;
        }
        int currentSpots = get(S).getSpots();
        int neighbor = neighbors(S);
        int row = row(S);
        int col = col(S);
        Side color = get(S).getSide();
        if (currentSpots > neighbor) {
            set(row, col, 1, color);
            if (exists(row - 1, col)) {
                simpleAdd(color, row - 1, col, 1);
                jump(sqNum(row - 1, col));
            }
            if (exists(row + 1, col)) {
                simpleAdd(color, row + 1, col, 1);
                jump(sqNum(row + 1, col));
            }
            if (exists(row, col - 1)) {
                simpleAdd(color, row, col - 1, 1);
                jump(sqNum(row, col - 1));
            }
            if (exists(row, col + 1)) {
                simpleAdd(color, row, col + 1, 1);
                jump(sqNum(row, col + 1));
            }
        }
    }

    /** Returns my dumped representation. */
    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int i = 1; i <= size(); i++) {
            out.format("    ");
            for (int j = 1; j <= size(); j++) {
                int spots = get(i, j).getSpots();
                Side side = get(i, j).getSide();
                String sideStr;
                if (side == RED) {
                    sideStr = "r";
                } else if (side == BLUE) {
                    sideStr = "b";
                } else {
                    sideStr = "-";
                }
                out.format("%d%s ", spots, sideStr);
            }
            out.format("%n");
        }
        out.format("===");
        return out.toString();
    }

    /** Returns an external rendition of me, suitable for human-readable
     *  textual display, with row and column numbers.  This is distinct
     *  from the dumped representation (returned by toString). */
    public String toDisplayString() {
        String[] lines = toString().trim().split("\\R");
        Formatter out = new Formatter();
        for (int i = 1; i + 1 < lines.length; i += 1) {
            out.format("%2d %s%n", i, lines[i].trim());
        }
        out.format("  ");
        for (int i = 1; i <= size(); i += 1) {
            out.format("%3d", i);
        }
        return out.toString();
    }

    /** Returns the number of neighbors of the square at row R, column C. */
    int neighbors(int r, int c) {
        int size = size();
        int n;
        n = 0;
        if (r > 1) {
            n += 1;
        }
        if (c > 1) {
            n += 1;
        }
        if (r < size) {
            n += 1;
        }
        if (c < size) {
            n += 1;
        }
        return n;
    }

    /** Returns the number of neighbors of square #N. */
    int neighbors(int n) {
        return neighbors(row(n), col(n));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board)) {
            return false;
        } else {
            Board B = (Board) obj;
            return B.toString().equals(toString())
                    && whoseMove() == B.whoseMove();
        }
    }

    @Override
    public int hashCode() {
        return numPieces();
    }

    /** Set my notifier to NOTIFY. */
    public void setNotifier(Consumer<Board> notify) {
        _notifier = notify;
        announce();
    }

    /** Take any action that has been set for a change in my state. */
    private void announce() {
        _notifier.accept(this);
    }

    /** A notifier that does nothing. */
    private static final Consumer<Board> NOP = (s) -> { };

    /** A read-only version of this Board. */
    private ConstantBoard _readonlyBoard;

    /** Use _notifier.accept(B) to announce changes to this board. */
    private Consumer<Board> _notifier;

    /** The 1-d array filled with Square objects to represent the board. */
    private Square[] _board;

    /** the ArrayDeque to store the past Board objects when making a move. */
    private ArrayDeque<Board> _history = new ArrayDeque<>();

    /** The integer to keep track of the number of moves. */
    private int _moves;
}
