import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class AlphaBetaAgent implements Agent
{
    private Random random = new Random();

    private String role; // the name of this agent's role (white or black)
	private int playclock; // this is how much time (in seconds) we have before nextAction needs to return a move
	private int width, height; // dimensions of the board

    private State currentState;
    
    @Override
    public void init(String role, int width, int height, int playclock, int[][] white_positions, int[][] black_positions) {
		System.out.println("Playing " + role + " on a " + width + "x" + height + " board with " + playclock + "s per move");
		System.out.println("White starting positions: " + Arrays.deepToString(white_positions));
		System.out.println("Black starting positions: " + Arrays.deepToString(black_positions));
		
		this.role = role;
		this.playclock = playclock;
		this.width = width;
		this.height = height;
        
        char[][] newBoard = new char[height][width];

        int numberOfQueens = white_positions.length + black_positions.length;
        
        System.out.println("White positions: " + white_positions + "\n");
        System.out.println("Black positions: " + black_positions + "\n");

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

    public ArrayList<int[]> LegalMoveArr(int[] lastMove, State currState){
        
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
                    break;
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

    /*
    int AlphaBeta (int depth, State s, int alpha, int beta) {
            
        if ( s is terminal or depth <= 0 )
            return evaluate( s );
        
        bestValue = -INF;
        
        for ( for all successors s’ of s ) {
            value = - AlphaBeta( depth–1, s’, - beta, - alpha );
            (Note: switch and negate bounds)
            bestValue = max( value, bestValue);
        
            if ( bestValue > alpha ) {
        
            alpha = bestValue; (adjust the lower bound)
        
            if ( alpha >= beta ) break; (beta cutoff)
        }
    }
        return bestValue;   
    }
    
    */

    // lastMove is null the first time nextAction gets called (in the initial state)
    // otherwise it contains the coordinates x1,y1,x2,y2 of the move that the last player did
    @Override
    public String nextAction(int[] lastMove) {
        try {
        Thread.sleep(500);
        } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        }

        System.out.println("Testing lastMove array: " + Arrays.toString(lastMove) + "\n");

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
   			System.out.println(roleOfLastPlayer + " moved from " + x1 + "," + y1 + " to " + x2 + "," + y2);
            if (isOpponentMove){
                currentState = new State(currentState, lastMove);
            }
    	}
		
    	// update turn (above that line it myTurn is still for the previous state)
			// TODO: 2. run alpha-beta search to determine the best move
        if (currentState.isMyTurn() && role.equals("white") || !currentState.isMyTurn() && role.equals("black")){

            System.out.println("isMyTurn: " + currentState.isMyTurn());
            System.out.println("White queens: " + Arrays.deepToString(currentState.getWhiteList()));
            System.out.println("Black queens: " + Arrays.deepToString(currentState.getBlackList()));

			// Here we just construct a random move (that will most likely not even be possible),
			// this needs to be replaced with the actual best move.
			int x1,y1,x2,y2;
            ArrayList<int[]> legalMoves = LegalMoveArr(lastMove, currentState);

            
            //At the root level:
            int[] bestMove;
            int depth = 1;
            
            int INF = 1_000_000;
            int alpha = -INF;
            int beta = INF;
            int bestValue = -INF;
            int remainingTime = this.playclock;
            
            
            for (int[] move : legalMoves){
                int value = -AlphaBeta(depth + 1, result(state, move), -beta, -alpha);
                if (value > bestValue){
                    bestValue = value;
                    bestMove = move;
                }
            }
             */
        //    
            // int[][] bestMove = AlphaBeta( MaxDepth, s, -INF, INF )
            

         
            //  TODO: Need to test the state space and make sure there are no problems there
            //  Afterwards we can start to implement search algorithm alpha beta search
            // */

        //    if (currentState.isMyTurn())
        //     System.out.println("Available moves:");
        //     for (int[] move : legalMoves) {
        //         System.out.println(Arrays.toString(move));
        //     }
        
            //  // DRAW SCENARIO
            // if (currentState.getEmptySquares() <= currentState.getWidth()){
            //     System.out.println("Game resulted in draw.");
            //     return "noop"; // or handle game over
            // // WHITE WINS SCENARIO
            // } else if (legalMoves.isEmpty() && currentState.isMyTurn()) {
            //     // No legal moves - handle this case
            //     System.out.println("White wins.");
            //     return "noop"; // or handle game over
            // // BLACK WINS SCENARIO
            // } else if (legalMoves.isEmpty() && !currentState.isMyTurn()){
            //     System.out.println("Black wins wins.");
            //     return "noop"; // or handle game over
            // }


            //currentState = new State(currentState, randomMove);

		// 	return "(play " + x1 + " " + y1 + " " + x2 + " " + y2 + ")";
	    // } else {
        //     return "noop";        
        }
        return "noop";
    }

    @Override
    public void cleanup() {

    }
}
