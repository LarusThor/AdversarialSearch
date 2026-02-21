public class Main {
    public static void main(String[] args){
        try{
            int port = 4001;
            if(args.length >= 1){
                port = Integer.parseInt(args[0]);
            }

            // Pick agent based on second argument
            Agent agent;
            if(args.length >= 2 && args[1].equals("random")){
                agent = new SearchAgent();
            } else if(args.length >= 2 && args[1].equals("alphabeta")){
                agent = new AlphaBetaLogging();
            } else {
                agent = new AlphaBetaImproved(); // default
            }

            GamePlayer gp = new GamePlayer(port, agent);
            gp.waitForExit();
        } catch(Exception ex){
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}

// public class Main {
	
// 	/**
// 	 * starts the game player and waits for messages from the game master <br>
// 	 * Command line options: [port]
// 	 */
// 	public static void main(String[] args){
// 		try{
// 			// TODO: put in your agent here
// 			Agent agent = new SearchAgent();

// 			int port=4001;
// 			if(args.length>=1){
// 				port=Integer.parseInt(args[0]);
// 			}
// 			GamePlayer gp=new GamePlayer(port, agent);
// 			gp.waitForExit();
// 		}catch(Exception ex){
// 			ex.printStackTrace();
// 			System.exit(-1);
// 		}
// 	}
// }
