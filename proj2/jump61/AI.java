package jump61;

import java.util.Random;

import static jump61.Side.*;

/** An automated Player.
 *  @author P. N. Hilfinger
 */
class AI extends Player {

    /** A new player of GAME initially COLOR that chooses moves automatically.
     *  SEED provides a random-number seed used for choosing moves.
     */
    AI(Game game, Side color, long seed) {
        super(game, color);
        _random = new Random(seed);
    }

    @Override
    /** Return my next move, or a command.  Assumes that I am of the
     *  proper color and that the game is not yet won. */
    String getMove() {
        Board board = getGame().getBoard();

        assert getSide() == board.whoseMove();
        int choice = searchForMove();
        getGame().reportMove(board.row(choice), board.col(choice));
        return String.format("%d %d", board.row(choice), board.col(choice));
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private int searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert getSide() == work.whoseMove();
        _foundMove = -1;
        if (getSide() == RED) {
            minMax(work, 4, true, 1, MIN, MAX);
        } else {
            minMax(work, 4, true, -1, MIN, MAX);
        }
        return _foundMove;
    }

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int minMax(Board board, int depth, boolean saveMove,
                       int sense, int alpha, int beta) {
        int bestSoFar;
        if (sense == 1) {
            bestSoFar = MIN;
        } else {
            bestSoFar = MAX;
        }
        if (board.getWinner() != null || depth == 0) {
            return staticEval(board, 100);
        }
        for (int i = 0; i < board.size() * board.size(); i++) {
            if (board.isLegal(board.whoseMove(), i)) {
                Board nextBoard = new Board(board);
                nextBoard.addSpot(board.whoseMove(), i);
                int response = minMax(nextBoard, depth - 1,
                        false, -sense, alpha, beta);

                if (sense == 1) {
                    if (response > bestSoFar) {
                        bestSoFar = response;
                        if (saveMove) {
                            _foundMove = i;
                        }
                        alpha = Math.max(alpha, bestSoFar);
                    }
                } else if (sense == -1) {
                    if (response < bestSoFar) {
                        bestSoFar = response;
                        if (saveMove) {
                            _foundMove = i;
                        }
                        beta = Math.min(beta, bestSoFar);
                    }
                }
                if (alpha >= beta) {
                    return bestSoFar;
                }
            }
        }
        return bestSoFar;
    }

    /** Return a heuristic estimate of the value of board position B.
     *  Use WINNINGVALUE to indicate a win for Red and -WINNINGVALUE to
     *  indicate a win for Blue. */
    private int staticEval(Board b, int winningValue) {
        if (b.getWinner() == RED) {
            return winningValue;
        } else if (b.getWinner() == BLUE) {
            return -winningValue;
        } else {
            return b.numOfSide(RED) - b.numOfSide(BLUE);
        }
    }

    /** A random-number generator used for move selection. */
    private Random _random;

    /** Used to convey moves discovered by minMax. */
    private int _foundMove;

    /** The infinitely large value. */
    private static final int MAX = Integer.MAX_VALUE;

    /** The infinitely small value. */
    private static final int MIN = Integer.MIN_VALUE;
}
