import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class PA1Driver {
    public int testCount = 18; //set the number of tests here
    public ArrayList<TestInfo> tests;


    ArrayList<Pair> plan;
    int numOfPaths;

    public static void main(String[] args) throws Exception {
        PA1Driver driver = new PA1Driver();

        try{
			driver.readTests();
		} catch (Exception e){
			System.err.println("There was an error while reading the test files");
			System.err.println(e.getMessage());
		}

		double p = 0;

        for(TestInfo test : driver.tests){
            try{
                p += driver.testing(test);
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

		System.err.println("\n\n");
		System.err.println("             Total Points: " + p);
		System.exit(0); // There some solutions that hold the process here for some reason
						// My best guess is it has something to do with using the deprecated thread.stop() method
    }

    public double testing(TestInfo test){
		System.err.println("\n Testing " + test.shorterFilename + " ( " + test.points + " pts )");

        boolean flag = true;
        GameBoard game = new GameBoard();

        try { // considerable number of submissions doing everything in readinput and crashing
			TBlock timeoutBlock = new TBlock(test.timeLimitInMilliseconds);// set timeout in milliseconds
			Runnable block = new Runnable() {
				@Override
				public void run() {
					try {
						game.readInput(test.inFilename);
					} catch (Exception e) {
					    System.err.println("readinput: " + e.getMessage());
					} catch (Error e){
						System.err.println(e.getMessage());
					}
				}
			};
			timeoutBlock.addBlock(block, "readInput()");// execute the runnable block
		} catch (Throwable e) {
			flag = false;
			System.err.println(e.getMessage());
		}

        plan = null;
        try {
			TBlock timeoutBlock = new TBlock(test.timeLimitInMilliseconds);// set timeout in milliseconds
			Runnable block = new Runnable() {
				@Override
				public void run() {
					try {
						plan = game.getPlan();
					} catch (Exception e) {
						System.err.println(e.getMessage());
					} catch (Error e){
						System.err.println(e.getMessage());
					}
				}
			};
			timeoutBlock.addBlock(block, "getPlan()");// execute the runnable block
		} catch (Throwable e) {
			flag = false;
			System.err.println(e.getMessage());
		}

        /*numOfPaths = 0;
        try {
			TBlock timeoutBlock = new TBlock(test.timeLimitInMilliseconds);// set timeout in milliseconds
			Runnable block = new Runnable() {
				@Override
				public void run() {
					try {
						numOfPaths = game.getNumOfPaths();
					} catch (Exception e) {
						System.err.println(e.getMessage());
					} catch (Error e){
						System.err.println(e.getMessage());
					}
				}
			};
			timeoutBlock.addBlock(block, "getNumOfPaths()");// execute the runnable block
		} catch (Throwable e) {
			flag = false;
			System.err.println(e.getMessage());
		}

        double numOfPathsCorrect = 0;
		if(numOfPaths == test.numOfPaths){
			numOfPathsCorrect = 1;
		} else {
			System.err.println("NumOfPaths do not match. Expected: " + test.numOfPaths + ", Student: " + numOfPaths);
		}*/

		double planCorrect = 0;
		if(plan == null){
			if(test.shortestPath == 0){
				planCorrect = 1;
			} else {
				System.err.println("There is a shortest path of length " + test.shortestPath + ", but the student solution returns null");
			}
		} else if(plan != null){
			if(plan.size() == test.shortestPath){
				if(test.validator.validate(plan)){
					planCorrect = 1;
				} else {
					System.err.println("The student solution path of length " + plan.size() + " is not valid:\n" + plan);
				}
			} else {
				System.err.println("There is a shortest path of length " + test.shortestPath + ", but the student solution has size " + plan.size());
			}
		}
		
		double finalPts = (test.points * planCorrect);
		double scoredPoints = Math.round(finalPts * 100) / 100.0;

		System.err.println("\n            Points Scored: " + scoredPoints);
			
        return scoredPoints;
    }

    /*
         * Reads the test in and out files and creates bunch of testinfo objects.
         * The input files are assumed to be in ./tests/in/inputx.txt format
         * The output files are assumed to be in ./tests/out/outputx.txt format
         * where x is the test number: {1, 2, 3, ..., n}
         * 
         * Output files should contain 3 lines:
         * ---- 1) Length of the shortest plan/path
         * ---- 2) Number of different shortest plans
         * ---- 3) The amount of points to earn for this test
    */
    private void readTests() throws NumberFormatException, IOException{
		tests = new ArrayList<>();
        for(int i=0; i<testCount; i++){
            String inFilename = "./tests/in/input" + i + ".txt";
            String outFilename = "./tests/out/output" + i + ".txt";
			String shorterFilename = "input" + i + ".txt";

            FileInputStream filestream = new FileInputStream(outFilename);
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(filestream));

            int shortestPath = Integer.parseInt(bufferedReader.readLine().trim().split("\\s+")[0]);
            int numOfPaths = Integer.parseInt(bufferedReader.readLine().trim().split("\\s+")[0]);
            int points = Integer.parseInt(bufferedReader.readLine().trim().split("\\s+")[0]);
			int timeLimitInMilliseconds = Integer.parseInt(bufferedReader.readLine().trim().split("\\s+")[0]);

			Validator validator = new Validator();
			validator.readInput(inFilename);

            tests.add(new TestInfo(inFilename, shorterFilename, shortestPath, numOfPaths, points, timeLimitInMilliseconds, validator));

            bufferedReader.close();
        }
    }
}

class TestInfo{
    public String inFilename, shorterFilename;
    public int shortestPath;
    public int numOfPaths;
    public int points; // points for passing this test
	public int timeLimitInMilliseconds;
	public Validator validator;

    public TestInfo(String _inFilename, String _shorterFilename, int _shortestPath,
					int _numOfPaths, int _points, int _timeLimitInMilliseconds, Validator _validator){
        inFilename = _inFilename;
		shorterFilename = _shorterFilename;
        shortestPath = _shortestPath;
        numOfPaths = _numOfPaths;
        points = _points;
		validator = _validator;
		timeLimitInMilliseconds = _timeLimitInMilliseconds;
    }
}

class Validator {
    GameLayout initConfig;  // 0th element is the icecream truck

	public void readInput(String fName) throws IOException { 
		FileInputStream fstream = new FileInputStream(fName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;

		String[] strEl;
		int numOfVehicles;
		int[] locs; 
		
		// reading the first line for the numOfVehicles
		strLine = br.readLine();
		strEl = strLine.trim().split("\\s+");
		numOfVehicles = Integer.parseInt(strEl[0]);
		initConfig = new GameLayout();
		ArrayList<int[]> vehicles = new ArrayList<>();
		
		for (int i=0; i<numOfVehicles; i++) {
			strLine = br.readLine();
			strEl = strLine.trim().split("\\s+");
			locs = new int[strEl.length];
			for (int j=0; j<locs.length; j++) {
				locs[j] = Integer.parseInt(strEl[j]);
			}
			vehicles.add(locs);
		}
		initConfig.setVehicles(vehicles);
		br.close();
	}
	
	
	public void setInit(GameLayout c) {
		initConfig = c;
	}
	
	public boolean validate(ArrayList<Pair> moveSequence){
		GameLayout currentGameLayout = initConfig;
		int cnt = 0;
		for(Pair move : moveSequence){
			cnt++;
			if(move.getId() >= initConfig.getNumOfVehicles() || move.getId() < 0){
				System.err.println("Error at move " + cnt + ": " 
									+ "The move (" + move.getId() + ", " + (char)move.getDirection()
									+ ") has car id outside of range [0, " + initConfig.getNumOfVehicles() + "]\n");
				return false;
			}
			try{
				move.setDirection(Character.toLowerCase((char)move.getDirection()));
			} catch (Exception e){
				System.err.println(e.getMessage());
			}
			if("nsew".indexOf((char)move.getDirection()) == -1){
				System.err.println("Error at move " + cnt + ": " 
									+ "The move (" + move.getId() + ", " + (char)move.getDirection()
									+ ") has an invalid direction");
			}
			if(!currentGameLayout.isValidMove(move)){
				System.err.println("Error at move " + cnt + ": " 
									+ "The move (" + move.getId() + ", " + (char)move.getDirection()
									+ ") is invalid");
				return false;
			}
			currentGameLayout = currentGameLayout.moveVehicle(move);
		}
		if(!currentGameLayout.isGoal()){
			System.err.println("The final game configuration is not a goal!");
		}

		return currentGameLayout.isGoal();
	}
}

/*
 * Previous Configuration class, renamed to GameLayout to avoid possible issues with students
 * having the same class name in their implementation.
 */
class GameLayout {
	
	// location of vehicles
	// 0-th vehicle is the icecream truck
	private ArrayList<int[]> vehicles; 
	
	// level is needed to find the length of the shortest path
	// and the number of paths
	private int level = -1;
	
	// parent is used to construct the solution path from goal location
	// to the initial GameLayout
	private LinkedList<GameLayout> parents = new LinkedList<>();

	// the number of ways we can arrive at this GameLayout from the initial GameLayout
	private int numOfWays = 0;
	
	
	// print method - may come handy to debug
	public void printthis() {
		for (int i=0; i<vehicles.size(); i++) {
			int[] locs = vehicles.get(i);
			System.out.print("vehicle: " + i + " ");
			for (int j=0; j<locs.length; j++) {
				System.out.print(locs[j] + " ");
			}
			System.err.println();
		}
	}
	
	// get methods - let's just plug in some of these - can be removed in not needed
	public ArrayList<int[]> getVehicles() { return vehicles; }
	public int getNumOfVehicles() { return vehicles.size(); }
	public int getLevel() { return level; }
	public LinkedList<GameLayout> getParents() { return parents; }
	public int getNumOfWays() { return numOfWays; }
	
	// This method is used for hashing purpose. 
	// For each vehicle, record just the last location in int[]
	// This should uniquely describe a GameLayout
	public int[] getlastlocs() {
		int[] res = new int[vehicles.size()];
		for (int i=0; i<vehicles.size(); i++) {
			int[] locs = vehicles.get(i);
			res[i] = locs[locs.length-1];
		}
		return res;		
	}

        // How about adding some of these as well
	// set methods: is this needed much?
	public void setVehicles(ArrayList<int[]> v) {
		int[] locs; 
		
		vehicles = new ArrayList<>();
		for (int i=0; i<v.size(); i++) {
			locs = new int[v.get(i).length];
			for (int j=0; j<locs.length; j++) {
				locs[j] = v.get(i)[j];
			}
			vehicles.add(locs);
		}
	}
	
	public void setLevel(int l) { level = l; }

	public void setNumOfWays(int l) { numOfWays = l; }
	
	public void addParent(GameLayout p) { parents.add(p); }
	
	// helper methods
	// Find all the next GameLayouts for the current GameLayout
	public ArrayList<GameLayout> getNext() {
		
		ArrayList<GameLayout> res = new ArrayList<>();
		
		// for each vehicle, find the possible moves
		for (int i=0; i<vehicles.size(); i++) {
			// each vehicle can go in two directions
			int[] loc = vehicles.get(i);
			//int[] newloc; 

			// Nasty coding - hardcode from hell -  parameterize
			// east-west vehicle
			if (loc[0] + 1 == loc[1]) {
				if ( (loc[loc.length-1]%6!=0) && isEmpty(loc[loc.length-1]+1) ) {
							// can go east
					int[] newloc = new int[loc.length];
					for (int k=0; k<loc.length; k++) {
						newloc[k] = loc[k] + 1;
					}
					//System.err.println("going east");
					GameLayout newc = update(i, newloc);
					res.add(newc);
				}
			}
			if (loc[0] + 1 == loc[1]) {
				if ( (loc[0]%6!=1) && isEmpty(loc[0]-1) ) {
					// can go west
					int[] newloc = new int[loc.length];
					for (int k=0; k<loc.length; k++) {
						newloc[k] = loc[k] - 1;
					}
					//System.err.println("going west");
					GameLayout newc = update(i, newloc);
					res.add(newc);
				}
			}
			// north-south vehicles
			if (loc[0] + 6 == loc[1]) {
				if ( (loc[0] + ((loc.length) * 6) <= 36 ) && isEmpty(loc[0] + (loc.length * 6)) ) {
					// can go south
					int[] newloc = new int[loc.length];
					for (int k=0; k<loc.length; k++) {
						newloc[k] = loc[k] + 6;
					}
					//System.err.println("going south");
					GameLayout newc = update(i, newloc);
					res.add(newc);
				}
			}
			if (loc[0] + 6 == loc[1]) {
				if ( (loc[0] - 6  >= 1 ) && isEmpty(loc[0] - 6) ) {
					// can go north
					int[] newloc = new int[loc.length];
					for (int k=0; k<loc.length; k++) {
						newloc[k] = loc[k] - 6;
					}
					//System.err.println("going north");
					GameLayout newc = update(i, newloc);
					res.add(newc);
				}
			}
		}
		return res;
	}
	
	
	public boolean isEmpty(int l) {
		int[] loc;
		for (int i=0; i<vehicles.size(); i++) {
			loc = vehicles.get(i);
			for (int j=0; j<loc.length; j++) {
				if (loc[j] == l) 
					return false;
			}
		}
		return true;
	}
	
	// Somewhat unclean implementation: rewrite
	private GameLayout update(int i, int[] locs) {
		GameLayout res = new GameLayout(); // create a new object
		res.vehicles = new ArrayList<>();
		
		for (int k=0; k<vehicles.size(); k++) {
			if (k==i) {
				res.vehicles.add(locs);
			}
			else {
				res.vehicles.add(vehicles.get(k));
			}
		}
		return res;  // same as the this object except for the i-th vehicle location
	}
	
	// 0-th vehicle is the icecream truck of length 2
	public boolean isGoal() {
		return (vehicles.get(0)[1] == 18);
	}

	// check if the given move is valid, the same logic used as in getNext()
	public boolean isValidMove(Pair move){
		int[] loc = vehicles.get(move.getId());
		if((char)move.getDirection() == 'n'){
			return (loc[0] + 6 == loc[1]) && (loc[0] >= 7) && isEmpty(loc[0] - 6);
		} else if((char)move.getDirection() == 's'){
			return (loc[0] + 6 == loc[1]) && (loc[loc.length-1] <= 30) && isEmpty(loc[loc.length-1] + 6);
		} else if((char)move.getDirection() == 'w'){
			return (loc[0] + 1 == loc[1]) && (loc[0] % 6 != 1) && isEmpty(loc[0] - 1);
		} else if((char)move.getDirection() == 'e'){
			return (loc[0] + 1 == loc[1]) && (loc[loc.length-1] % 6 != 0) && isEmpty(loc[loc.length-1] + 1);
		}
		return false;
	}

	public GameLayout moveVehicle(Pair move){
		int[] locs = vehicles.get(move.getId());
		char dir = (char)move.getDirection();
		int delta = ((dir == 'n' || dir == 'w') ? -1 : 1) * ((dir == 'n' || dir == 's') ? 6 : 1);

		int[] newLocs = new int[locs.length];
		for(int i=0; i<locs.length; i++){
			newLocs[i] = locs[i] + delta;
		}
		return update(move.getId(), newLocs);
	}
	
	public Pair getDirection(GameLayout c) {
		ArrayList<int[]> parentV = c.getVehicles();
		ArrayList<int[]> childV = this.getVehicles();
		for (int i=0; i<parentV.size(); i++) {
			if (parentV.get(i)[0] == childV.get(i)[0] - 1) {
				return new Pair(i, 'e');
			}
			if (parentV.get(i)[0] == childV.get(i)[0] + 1) {
				return new Pair(i, 'w');
			}
			if (parentV.get(i)[0] == childV.get(i)[0] - 6) {
				return new Pair(i, 's');
			}
			if (parentV.get(i)[0] == childV.get(i)[0] + 6) {
				return new Pair(i, 'n');
			}
			
		}
		return null; 
	}
	
	
}


// from stackoverflow
// https://stackoverflow.com/questions/5715235/java-set-timeout-on-a-certain-block-of-code/19183452

class TBlock {

	private final long timeoutMilliSeconds;
	private long timeoutInteval = 20;

	public TBlock(long timeoutMilliSeconds) {
		this.timeoutMilliSeconds = timeoutMilliSeconds;
	}

	public void addBlock(Runnable runnable, String runnableName) throws Throwable {
		long collectIntervals = 0;
		Thread timeoutWorker = new Thread(runnable);
		timeoutWorker.start();
		do {
			if (collectIntervals >= this.timeoutMilliSeconds) {
				timeoutWorker.stop();
				throw new Exception("<<<<<<<<<< " + runnableName + " >>>>>>>>>>> Timeout Block Execution Time Exceeded In "
						+ timeoutMilliSeconds + " Milli Seconds. Thread Block Terminated.\n");
			}
			collectIntervals += timeoutInteval;
			Thread.sleep(timeoutInteval);

		} while (timeoutWorker.isAlive());
		System.err.println("<<<<<<<<<< " + runnableName + " >>>>>>>>>>> Executed Within " + collectIntervals + " Milli Seconds.");
	}

	/**
	 * @return the timeoutInteval
	 */
	public long getTimeoutInteval() {
		return timeoutInteval;
	}

	/**
	 * @param timeoutInteval the timeoutInteval to set
	 */
	public void setTimeoutInteval(long timeoutInteval) {
		this.timeoutInteval = timeoutInteval;
	}
}
