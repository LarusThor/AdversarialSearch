import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SearchAgent implements Agent
{
    private Random random = new Random();

    private String role; // the name of this agent's role (white or black)
	private int playclock; // this is how much time (in seconds) we have before nextAction needs to return a move
	private boolean myTurn; // whether it is this agent's turn or not
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

        if (role.equals("white"))
            myTurn = true;
        else
            myTurn = false;

        currentState = new State(myTurn, ((width * height) - numberOfQueens),
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

    // lastMove is null the first time nextAction gets called (in the initial state)
    // otherwise it contains the coordinates x1,y1,x2,y2 of the move that the last player did
    @Override
    public String nextAction(int[] lastMove) {
        try {
        Thread.sleep(1000);
        } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        }


    	if (lastMove != null) {
    		int x1 = lastMove[0], y1 = lastMove[1], x2 = lastMove[2], y2 = lastMove[3];
    		String roleOfLastPlayer;
    		if (myTurn && role.equals("white") || !myTurn && role.equals("black")) {
    			roleOfLastPlayer = "white";
    		} else {
    			roleOfLastPlayer = "black";
    		}
   			System.out.println(roleOfLastPlayer + " moved from " + x1 + "," + y1 + " to " + x2 + "," + y2);

            currentState = new State(currentState, lastMove);
    	}
		
    	// update turn (above that line it myTurn is still for the previous state)
		myTurn = !myTurn;
		if (myTurn) {
			// TODO: 2. run alpha-beta search to determine the best move

			// Here we just construct a random move (that will most likely not even be possible),
			// this needs to be replaced with the actual best move.
			int x1,y1,x2,y2;
            ArrayList<int[]> legalMoves = LegalMoveArr(lastMove, currentState);

            /* 
             TODO: Need to test the state space and make sure there are no problems there
             Afterwards we can start to implement search algorithm alpha beta search
            */
            if (legalMoves.isEmpty()) {
                // No legal moves - handle this case
                System.out.println("Game is over.");
                return "noop"; // or handle game over
            }
            int[] randomMove = legalMoves.get(random.nextInt(legalMoves.size()));
			x1 = randomMove[0];
			y1 = randomMove[1];
			x2 = randomMove[2];
			y2 = randomMove[3];

            currentState = new State(currentState, randomMove);

			return "(play " + x1 + " " + y1 + " " + x2 + " " + y2 + ")";
		} else {
			return "noop";
		}
	}

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cleanup'");
    }
}
