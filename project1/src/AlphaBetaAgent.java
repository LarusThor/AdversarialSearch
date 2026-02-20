import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class AlphaBetaAgent implements Agent
{
    private Random random = new Random();

    private String role; // the name of this agent's role (white or black)
	private int playclock; // this is how much time (in seconds) we have before nextAction needs to return a move
	private int width, height; // dimensions of the board
    private long startTime;
    private long timeLimit;

    private State currentState;
    
    @Override
    public void init(String role, int width, int height, int playclock, int[][] white_positions, int[][] black_positions) {
		// ("Playing " + role + " on a " + width + "x" + height + " board with " + playclock + "s per move");
		// ("White starting positions: " + Arrays.deepToString(white_positions));
		// ("Black starting positions: " + Arrays.deepToString(black_positions));
		
		this.role = role;
		this.playclock = playclock;
		this.width = width;
		this.height = height;
        
        char[][] newBoard = new char[height][width];

        int numberOfQueens = white_positions.length + black_positions.length;
        
        // ("White positions: " + white_positions + "\n");
        // ("Black positions: " + black_positions + "\n");

        // White starting positions: [[2, 1], [3, 1]]
        // Black starting positions: [[2, 4], [3, 4]]
        // java -jar chesslikesim.jar

        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                newBoard[i][j] = '-';
            }
        }

        for (int[] pos : white_positions) {
            int x = pos[0] - 1;  
            int y = pos[1] - 1;
            newBoard[y][x] = 'w';
        }

        for (int[] pos : black_positions) {
            int x = pos[0] - 1;
            int y = pos[1] - 1;
            newBoard[y][x] = 'b';
        }

        currentState = new State(true, ((width * height) - numberOfQueens),
         newBoard, white_positions, black_positions, width, height);
    }

    public ArrayList<int[]> LegalMoveArr(State currState){
        
        // Need to check for all possible moves if its valid and add to a 2D array
        ArrayList<int[]> legalMoves = new ArrayList<>();
        
        int[][] whitePos = currState.getWhiteList();
        int[][] blackPos = currState.getBlackList();
        char[][] currentBoard = currState.getBoard();
        int width = currState.getWidth();
        int height = currState.getHeight();

        int[][] currentPlayerQueens = currState.isMyTurn() ? whitePos : blackPos;

        int[][] directions = {
        {0, 1},   // up
        {0, -1},  // down
        {1, 0},   // right
        {-1, 0},  // left
        {1, 1},   // diagonal up-right
        {1, -1},  // diagonal down-right
        {-1, 1},  // diagonal up-left
        {-1, -1}  // diagonal down-left
        };

        for (int[] queen : currentPlayerQueens) {
            int queenX = queen[0];
            int queenY = queen[1];

            for (int[] direction : directions){
                int dirX = direction[0];
                int dirY = direction[1];

                int newX = queenX;
                int newY = queenY;

                while (true){
                    newX += dirX;
                    newY += dirY;

                    if (newX < 1 || newX > width || newY < 1 || newY > height){
                        break;
                    }

                    char square = currentBoard[newY-1][newX-1];

                    if (square != '-'){
                        break;
                    }

                    legalMoves.add(new int[]{queenX, queenY, newX, newY});
                }
            }
        }
        return legalMoves;
    }

    public int hasLegalMove(State state, int[][]player) {
        // For each queen, check each direction
        // Return the amount of movable queens 

        int[][] directions = {
        {0, 1},   // up
        {0, -1},  // down
        {1, 0},   // right
        {-1, 0},  // left
        {1, 1},   // diagonal up-right
        {1, -1},  // diagonal down-right
        {-1, 1},  // diagonal up-left
        {-1, -1}  // diagonal down-left
        };

        int moveableQueens = 0;

        for (int[] queen : player) {
            int queenX = queen[0];
            int queenY = queen[1];

            for (int[] direction : directions){
                int dirX = direction[0];
                int dirY = direction[1];

                int newX = queenX;
                int newY = queenY;
                newX += dirX;
                newY += dirY;
                // Square is out of bounds
                if (newX < 1 || newX > width || newY < 1 || newY > height){
                    continue;
                }
                char [][]board = state.getBoard();
                char square = board[newY-1][newX-1];

                if (square != '-'){
                    //Square isn't free
                    continue;
                } else {
                    //we found a move for this queen
                    moveableQueens += 1;
                    continue;
                }
            }
        }
        
        return moveableQueens;
    }

    public int evaluate(State state){
        // 

        int moveableWhiteQueens = 0;
        int moveableBlackQueens = 0;
        
        moveableWhiteQueens = hasLegalMove(state, state.getWhiteList());
        
        moveableBlackQueens = hasLegalMove(state, state.getBlackList());
        
        if (moveableWhiteQueens  <= 0 && moveableBlackQueens <= 0) return 0;
        else if (moveableWhiteQueens <= 0)
            moveableBlackQueens = 100;
        else if (moveableBlackQueens <= 0)
            moveableWhiteQueens = 100;
        
        moveableWhiteQueens = moveableWhiteQueens - moveableBlackQueens;
        
        if (role.equals("white")){
            return moveableWhiteQueens;
        } 
        return -moveableWhiteQueens;
    }

    public boolean is_Terminal(State s){
        if (s.getEmptySquares() <= s.getWidth()) return true;
        if (hasLegalMove(s, s.getWhiteList()) <= 0) return true;
        if (hasLegalMove(s, s.getBlackList()) <= 0) return true;
        return false;
    }

    int AlphaBeta(State s, int depth, int alpha, int beta) throws TimeoutException{
        if(System.currentTimeMillis() - startTime > timeLimit)
            throw new TimeoutException();

        if (depth == 0 || is_Terminal(s))
            return evaluate(s);

        ArrayList<int[]> actions = LegalMoveArr(s);

        if (actions.isEmpty())
            return evaluate(s);

        boolean isMyMove = (s.isMyTurn() && role.equals("white") || !s.isMyTurn() && role.equals("black")); 
        if (isMyMove){
            int value = -1_000_000;
            for (int[] a : actions){
                State child = new State(s, a);
                value = Math.max(value, AlphaBeta(s, depth-1, alpha, beta));
                if (value >= beta)
                    break;
                alpha = Math.max(alpha, value);
            }
            return value;
        } else {
            int value = 1_000_000;
            for (int[] a : actions){
                State child = new State(s, a);
                value = Math.min(value, AlphaBeta(child, depth-1, alpha, beta));
                if (value <= alpha)
                    break;
                beta = Math.min(beta, value);
            }
            return value;
        }   
    }

    // lastMove is null the first time nextAction gets called (in the initial state)
    // otherwise it contains the coordinates x1,y1,x2,y2 of the move that the last player did
    @Override
    public String nextAction(int[] lastMove) {
    	if (lastMove != null) {
            
            char pieceAtSource = currentState.getBoard()[lastMove[1]-1][lastMove[0]-1];
            
            boolean isOpponentMove = (pieceAtSource != 'x');
        
            int x1 = lastMove[0], y1 = lastMove[1], x2 = lastMove[2], y2 = lastMove[3];
    		String roleOfLastPlayer;
    		if (currentState.isMyTurn() && role.equals("white") || !currentState.isMyTurn() && role.equals("black")) {
    			roleOfLastPlayer = "white";
    		} else {
    			roleOfLastPlayer = "black";
    		}
            if (isOpponentMove){
                currentState = new State(currentState, lastMove);
            }
    	}
		
    	// update turn (above that line it myTurn is still for the previous state)
			// TODO: 2. run alpha-beta search to determine the best move
        if (currentState.isMyTurn() && role.equals("white") || !currentState.isMyTurn() && role.equals("black")){

			// Here we just construct a random move (that will most likely not even be possible),
			// this needs to be replaced with the actual best move.
            //At the root level:
            int[] bestMove = null;
            int depth = 1;
            
            int INF = 1_000_000;
           
            int remainingTime = this.playclock;
            
            startTime = System.currentTimeMillis();
            timeLimit = (playclock - 1) * 1000L;
            
            while (true) {
                try {
                    int alpha = -INF;
                    int beta = INF;
                    int[] candidate = null;
                    int bestValue = -INF;
                    ArrayList<int[]> legalMoves = LegalMoveArr(currentState);
                    for (int[] a : legalMoves){
                        if(System.currentTimeMillis() - startTime > timeLimit)
                            throw new TimeoutException();
                        State child = new State(currentState, a);
                        int value = AlphaBeta(child, depth-1, alpha, beta);
                        //("Move: " + Arrays.toString(a) + " Score: " + value); // add this
                        if (value > bestValue){
                            bestValue = value;
                            candidate = a;
                        }
                    }
                    bestMove = candidate;
                    //("Searching depth: " + depth);
                    depth += 1;
                }
                catch (TimeoutException e){
                    //("TIME RAN OUT at depth: " + depth);
                    break;
                }
            }

            if (bestMove == null){
                bestMove = LegalMoveArr(currentState).get(0);
            }

            return "(play " + bestMove[0] + " " + bestMove[1] + " " + bestMove[2] + " " + bestMove[3] + ")";  
        } else {
        return "noop";
    }
}
    @Override
    public void cleanup() {

        }
    }
