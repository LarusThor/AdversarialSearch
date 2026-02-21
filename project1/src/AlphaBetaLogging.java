import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.io.FileWriter;
import java.io.IOException;

public class AlphaBetaLogging implements Agent
{
    private Random random = new Random();

    private String role;
    private int playclock;
    private int width, height;
    private long startTime;
    private long timeLimit;
    private State currentState;

    // --- Logging fields ---
    private String boardSize;
    private int nodesExpanded = 0;
    private int totalDepthReached = 0;
    private long totalNodesPerSec = 0;
    private int movesMade = 0;
    private static final String CSV_FILE = "results.csv";

    // Writes one row per game: agent, board, playclock, role, outcome, avgDepth, avgNodes/sec
    private void logResult(String outcome) {
        int avgDepth = movesMade > 0 ? totalDepthReached / movesMade : 0;
        long avgNodes = movesMade > 0 ? totalNodesPerSec / movesMade : 0;
        try (FileWriter fw = new FileWriter(CSV_FILE, true)) {
            fw.write("AlphaBetaAgent," + boardSize + "," + playclock + ","
                    + role + "," + outcome + "," + avgDepth + "," + avgNodes + "\n");
        } catch (IOException e) {
            System.out.println("CSV write failed: " + e.getMessage());
        }
        System.out.println("Game over: " + outcome + " | avgDepth=" + avgDepth + " | avgNodes/s=" + avgNodes);
    }

    @Override
    public void init(String role, int width, int height, int playclock, int[][] white_positions, int[][] black_positions) {
        this.role = role;
        this.playclock = playclock;
        this.width = width;
        this.height = height;
        this.boardSize = width + "x" + height;

        // Reset per-game stats
        nodesExpanded = 0;
        totalDepthReached = 0;
        totalNodesPerSec = 0;
        movesMade = 0;

        char[][] newBoard = new char[height][width];
        int numberOfQueens = white_positions.length + black_positions.length;

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                newBoard[i][j] = '-';

        for (int[] pos : white_positions)
            newBoard[pos[1]-1][pos[0]-1] = 'w';

        for (int[] pos : black_positions)
            newBoard[pos[1]-1][pos[0]-1] = 'b';

        currentState = new State(true, ((width * height) - numberOfQueens),
            newBoard, white_positions, black_positions, width, height);
    }

    public ArrayList<int[]> LegalMoveArr(State currState){
        ArrayList<int[]> legalMoves = new ArrayList<>();
        int[][] currentPlayerQueens = currState.isMyTurn() ? currState.getWhiteList() : currState.getBlackList();
        char[][] currentBoard = currState.getBoard();
        int w = currState.getWidth(), h = currState.getHeight();
        int[][] directions = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}};

        for (int[] queen : currentPlayerQueens) {
            int qx = queen[0], qy = queen[1];
            for (int[] d : directions) {
                int nx = qx, ny = qy;
                while (true) {
                    nx += d[0]; ny += d[1];
                    if (nx < 1 || nx > w || ny < 1 || ny > h) break;
                    if (currentBoard[ny-1][nx-1] != '-') break;
                    legalMoves.add(new int[]{qx, qy, nx, ny});
                }
            }
        }
        return legalMoves;
    }

    public int hasLegalMove(State state, int[][] player) {
        int[][] directions = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}};
        int moveable = 0;
        for (int[] queen : player) {
            for (int[] d : directions) {
                int nx = queen[0] + d[0], ny = queen[1] + d[1];
                if (nx < 1 || nx > width || ny < 1 || ny > height) continue;
                if (state.getBoard()[ny-1][nx-1] == '-') { moveable++; break; }
            }
        }
        return moveable;
    }

    public int evaluate(State state){
        int wMoves = hasLegalMove(state, state.getWhiteList());
        int bMoves = hasLegalMove(state, state.getBlackList());

        if (wMoves <= 0 && bMoves <= 0) return 0;
        else if (wMoves <= 0) bMoves = 100;
        else if (bMoves <= 0) wMoves = 100;

        int diff = wMoves - bMoves;
        return role.equals("white") ? diff : -diff;
    }

    public boolean is_Terminal(State s){
        if (s.getEmptySquares() <= s.getWidth()) return true;
        if (hasLegalMove(s, s.getWhiteList()) <= 0) return true;
        if (hasLegalMove(s, s.getBlackList()) <= 0) return true;
        return false;
    }

    int AlphaBeta(State s, int depth, int alpha, int beta) throws TimeoutException {
        nodesExpanded++;
        if (System.currentTimeMillis() - startTime > timeLimit)
            throw new TimeoutException();

        if (depth == 0 || is_Terminal(s))
            return evaluate(s);

        ArrayList<int[]> actions = LegalMoveArr(s);
        if (actions.isEmpty()) return evaluate(s);

        boolean isMyMove = (s.isMyTurn() && role.equals("white") || !s.isMyTurn() && role.equals("black"));
        if (isMyMove) {
            int value = -1_000_000;
            for (int[] a : actions) {
                State child = new State(s, a);
                value = Math.max(value, AlphaBeta(child, depth-1, alpha, beta));
                if (value >= beta) break;
                alpha = Math.max(alpha, value);
            }
            return value;
        } else {
            int value = 1_000_000;
            for (int[] a : actions) {
                State child = new State(s, a);
                value = Math.min(value, AlphaBeta(child, depth-1, alpha, beta));
                if (value <= alpha) break;
                beta = Math.min(beta, value);
            }
            return value;
        }
    }

    @Override
    public String nextAction(int[] lastMove) {
        if (lastMove != null) {
            char pieceAtSource = currentState.getBoard()[lastMove[1]-1][lastMove[0]-1];
            if (pieceAtSource != 'x')
                currentState = new State(currentState, lastMove);
        }

        if (currentState.isMyTurn() && role.equals("white") || !currentState.isMyTurn() && role.equals("black")) {

            int[][] myQueens  = role.equals("white") ? currentState.getWhiteList() : currentState.getBlackList();
            int[][] oppQueens = role.equals("white") ? currentState.getBlackList() : currentState.getWhiteList();
            boolean myStuck   = hasLegalMove(currentState, myQueens) <= 0;
            boolean oppStuck  = hasLegalMove(currentState, oppQueens) <= 0;
            boolean boardFull = currentState.getEmptySquares() <= currentState.getWidth();

            int[] bestMove = null;
            int depth = 1;
            int INF = 1_000_000;
            nodesExpanded = 0;

            startTime = System.currentTimeMillis();
            timeLimit = (playclock - 1) * 1000L;

            while (true) {
                try {
                    int alpha = -INF, beta = INF;
                    int[] candidate = null;
                    int bestValue = -INF;

                    ArrayList<int[]> legalMoves = LegalMoveArr(currentState);
                    for (int[] a : legalMoves) {
                        if (System.currentTimeMillis() - startTime > timeLimit)
                            throw new TimeoutException();
                        State child = new State(currentState, a);
                        int value = AlphaBeta(child, depth-1, alpha, beta);
                        if (value > bestValue) {
                            bestValue = value;
                            candidate = a;
                        }
                    }
                    bestMove = candidate;
                    depth++;
                } catch (TimeoutException e) {
                    break;
                }
            }

            // Accumulate stats for this move
            long elapsed = Math.max(System.currentTimeMillis() - startTime, 1);
            totalDepthReached += (depth - 1);
            totalNodesPerSec  += (nodesExpanded * 1000L / elapsed);
            movesMade++;

            if (bestMove == null)
                bestMove = LegalMoveArr(currentState).get(0);

            currentState = new State(currentState, bestMove);
            return "(play " + bestMove[0] + " " + bestMove[1] + " " + bestMove[2] + " " + bestMove[3] + ")";
        }
        return "noop";
    }

    @Override
    public void cleanup() {
        // Note: currentState should already be up to date from nextAction
        // but if the game ended on opponent's move, we need to check carefully
        
        int whiteStuck = hasLegalMove(currentState, currentState.getWhiteList());
        int blackStuck = hasLegalMove(currentState, currentState.getBlackList());
        boolean boardFull = currentState.getEmptySquares() <= currentState.getWidth();

        String outcome;
        if (boardFull) {
            outcome = "draw";
        } else if (whiteStuck <= 0 && blackStuck <= 0) {
            outcome = "draw";
        } else if (role.equals("white") && blackStuck <= 0) {
            outcome = "win";
        } else if (role.equals("black") && whiteStuck <= 0) {
            outcome = "win";
        } else if (role.equals("white") && whiteStuck <= 0) {
            outcome = "loss";
        } else if (role.equals("black") && blackStuck <= 0) {
            outcome = "loss";
        } else {
            outcome = "unknown";
        }

        logResult(outcome);
    }
}
