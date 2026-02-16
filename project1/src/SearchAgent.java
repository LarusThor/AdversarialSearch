import java.util.Arrays;
import java.util.Random;

public class SearchAgent implements Agent
{
    private Random random = new Random();

    private String role; // the name of this agent's role (white or black)
	private int playclock; // this is how much time (in seconds) we have before nextAction needs to return a move
	private boolean myTurn; // whether it is this agent's turn or not
	private int width, height; // dimensions of the board

    private State currentState;
    
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

    public int[][] LegalMoveArr(int[] lastMove, State currState){
        
        // Need to check for all possible moves if its valid and add to a 2D array
        int[][] legalMoves;
        int numPlayers = currState.getWhiteList().length;
        
        int[][] whitePos = currState.getWhiteList();
        int[][] blackPos = currState.getBlackList();
        
        int width = currState.getWidth();
        int height = currState.getHeight();

        int[][] currentPlayerQueens = currState.isMyTurn() ? whitePos : blackPos;
        // Maybe have a counter = 8 that decreases each direction you have tried to traverse and stop at
        int directions = 8;

        for (int[] queen : currentPlayerQueens) {
            int queenX = queen[0];
            int queenY = queen[1];
            int tempX = queenX;
            int tempY = queenY;
            for (int i = 0; i < directions; i++){
                boolean hitObstacle = false;
                while (!hitObstacle) {
                    
                }
            }
        }


        
        return null;
    }

    // lastMove is null the first time nextAction gets called (in the initial state)
    // otherwise it contains the coordinates x1,y1,x2,y2 of the move that the last player did
    public String nextAction(int[] lastMove) {
    	if (lastMove != null) {
    		int x1 = lastMove[0], y1 = lastMove[1], x2 = lastMove[2], y2 = lastMove[3];
    		String roleOfLastPlayer;
    		if (myTurn && role.equals("white") || !myTurn && role.equals("black")) {
    			roleOfLastPlayer = "white";
    		} else {
    			roleOfLastPlayer = "black";
    		}
   			System.out.println(roleOfLastPlayer + " moved from " + x1 + "," + y1 + " to " + x2 + "," + y2);
            
    	}
		
    	// update turn (above that line it myTurn is still for the previous state)
		myTurn = !myTurn;
		if (myTurn) {
			// TODO: 2. run alpha-beta search to determine the best move

			// Here we just construct a random move (that will most likely not even be possible),
			// this needs to be replaced with the actual best move.
			int x1,y1,x2,y2;
			x1 = random.nextInt(width)+1;
			y1 = random.nextInt(height)+1;
			x2 = random.nextInt(width)+1;
			y2 = random.nextInt(height)+1;
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
