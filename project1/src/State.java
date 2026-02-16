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
}
