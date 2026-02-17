import java.util.ArrayList;

public class State {

    private boolean myTurn;
    private int emptySquares;
    
    private char[][] board;
    private int[][] whiteList;
    private int[][] blackList;

	private int width, height;

    public State(boolean myTurn, int emptySquares, char[][] board,
                 int[][]whiteList, int[][] blackList,
                 int width, int height){
        
        this.myTurn = myTurn;
        this.emptySquares = emptySquares;
        
        this.board = board;
        
        this.whiteList = whiteList;
        this.blackList = blackList;
        
        this.width = width;
        this.height = height;
    }

    public boolean isMyTurn() {
        return myTurn;
    }
    
    public int getEmptySquares() {
        return emptySquares;
    }
    
    public char[][] getBoard() {
        return board;
    }
    
    public int[][] getWhiteList() {
        return whiteList;
    }
    
    public int[][] getBlackList() {
        return blackList;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    private int[][] copyPositions(int[][] positions){
        int[][] copy = new int[positions.length][2];
        for (int i = 0; i < positions.length; i++){
            copy[i][0] = positions[i][0];
            copy[i][1] = positions[i][1];
        }
        return copy;
    }

    private int[][] copyAndUpdatePositions(int[][] positions, int[] action){
        int[][] copy = new int[positions.length][2];
        for (int i = 0; i < positions.length; i++){
            copy[i][0] = positions[i][0];
            copy[i][1] = positions[i][1];

            if (copy[i][0] == action[0] && copy[i][1] == action[1]){
                copy[i][0] = action[2];
                copy[i][1] = action[3];
            }
        }
        return copy;
    }

    public State(State previousState, int[] action){
        this.width = previousState.width;
        this.height = previousState.height;

        this.board = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.board[i][j] = previousState.board[i][j];
            }
        }

        int x1 = action[0] - 1, y1 = action[1] - 1;
        int x2 = action[2] - 1, y2 = action[3] - 1;

        char piece = this.board[y1][x1];
        this.board[y2][x2] = piece;
        this.board[y1][x1] = 'x';

        this.emptySquares = previousState.emptySquares - 1;

        if (previousState.myTurn){
            this.whiteList = copyAndUpdatePositions(previousState.whiteList, action);
            this.blackList = copyPositions(previousState.blackList);
        } else {
            this.whiteList = copyPositions(previousState.whiteList);
            this.blackList = copyAndUpdatePositions(previousState.blackList, action);
        }

        this.myTurn = !previousState.myTurn;
    }

}
