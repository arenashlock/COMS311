import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class GameBoard {
    // The initial configuration from the file
    Configuration initConfig;
    // HashMap to check if a Configuration has already been explored (while conducting BFS)
    HashMap<HashKey, Configuration> explored = new HashMap<>();

    public void readInput(String fName) throws IOException {
        FileInputStream fstream = new FileInputStream(fName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine;

        String[] strEl;
        int numOfVehicles;
        int[] locs;

        // Reading the first line for the numOfVehicles
        strLine = br.readLine();
        strEl = strLine.trim().split("\\s+");
        numOfVehicles = Integer.parseInt(strEl[0]);
        initConfig = new Configuration();
        ArrayList<int[]> vehicles = new ArrayList<>();

        for (int i = 0; i < numOfVehicles; i++) {
            strLine = br.readLine();
            strEl = strLine.trim().split("\\s+");
            locs = new int[strEl.length];
            for (int j = 0; j < locs.length; j++) {
                locs[j] = Integer.parseInt(strEl[j]);
            }
            vehicles.add(locs);
        }

        initConfig.setVehicles(vehicles);
    }

    public ArrayList<Pair> getPlan() {
        // Call the explore method to get a winning configuration
        Configuration winningPlan = explore(initConfig);

        // Create the ArrayList to store the plan
        ArrayList<Pair> plan = new ArrayList<>();

        // There was a bug that would occur if there was no plan (explore returns null), so we have a check to prevent it
        if (winningPlan != null) {
            // The starting configuration has no parent or plan, so continue until you reach the starting board
            while (winningPlan.parent != null) {
                plan.add(winningPlan.previousMove);
                winningPlan = winningPlan.parent;
            }
        }

        // Reverse since the plan is built in reverse order
        Collections.reverse(plan);
        return plan;
    }

    // This is BFS exploration
    // Input is the starting configuration (initConfig)
    public Configuration explore(Configuration start) {
        // Start a Queue to conduct BFS
        Queue<Configuration> bfsQueue = new LinkedList<>();

        // Initialize the Queue and HashMap with the starting board
        explored.put(new HashKey(start.getLastLocs()), start);
        bfsQueue.add(start);

        // If the Queue gets empty, then we cannot find a winning board
        while (!(bfsQueue.isEmpty())) {
            Configuration currentBoard = bfsQueue.remove();

            // getLastLocs() returns an array of the rightmost or bottommost locations of each vehicle
            // Since the first vehicle is the only vehicle we care about reaching spot 18, we can just check array[0]
            if (currentBoard.getLastLocs()[0] == 18) {
                return currentBoard;
            }

            // List of neighbors
            ArrayList<Configuration> neighbors = currentBoard.getNext();

            // Check each neighbor to see if it already exists in HashMap
            for (int i = 0; i < neighbors.size(); i++) {
                Configuration currentBoardNeighbor = neighbors.get(i);
                // null means the Configuration is not in the HashMap
                if (explored.get(new HashKey(currentBoardNeighbor.getLastLocs())) == null) {
                    // Set the parent for getting the plan later
                    currentBoardNeighbor.parent = currentBoard;

                    // Build up the HashMap and Queue
                    explored.put(new HashKey(currentBoardNeighbor.getLastLocs()), currentBoardNeighbor);
                    bfsQueue.add(currentBoardNeighbor);
                }
            }
        }

        return null;
    }
}

class Configuration {
    // ArrayList containing the location of each vehicle in the form of an integer array
    ArrayList<int[]> vehicles;
    // Move we made to get to this Configuration
    Pair previousMove;

    // For backtracking the winning board while getting the plan
    Configuration parent;

   public void setVehicles(ArrayList<int[]> vehicles) {
       this.vehicles = new ArrayList<>(vehicles);
   }

    // getLastLocs is used to generate the HashKey attribute for the configuration
    public int[] getLastLocs() {
	    int[] res = new int[vehicles.size()];

        // Make an array of the rightmost or bottommost values in each vehicle array
	    for (int i=0; i<vehicles.size(); i++) {
	        int[] locs = vehicles.get(i);
	        res[i] = locs[locs.length-1];
	    }

	    return res;
    }

    // getNext is used for finding all the neighbors of this Configuration to conduct BFS
    public ArrayList<Configuration> getNext() {
        // ArrayList that will contain all the *potential* configurations that can be reached by this configuration
            // Still need to check that we are not returning to a board already explored (done in the explore method using Hashkeys)
        ArrayList<Configuration> allNeighbors = new ArrayList<>();

        // Check all possible moves for each vehicle in the Configuration
        for(int i = 0; i < vehicles.size(); i++) {
            int[] vehicle = vehicles.get(i);

            // Horizontal vehicle
            if((vehicle[1] - vehicle[0]) == 1) {
                // If moving right is a valid move
                if(validMove(vehicles, vehicle[vehicle.length - 1], 1)) {
                    // Create new Configuration to add to ArrayList
                    Configuration moveRight = new Configuration();
                    int[] movedVehicle = new int[vehicle.length];

                    // Make new ArrayList for vehicle positions
                    ArrayList<int[]> newVehicles = new ArrayList<>(vehicles);

                    // Adjust the int[] for the vehicle that got moved
                    for(int j = 0; j < vehicle.length; j++) {
                        movedVehicle[j] = vehicle[j] + 1;
                    }
                    newVehicles.set(i, movedVehicle);

                    // Finish making the Configuration and add to the ArrayList
                    moveRight.vehicles = newVehicles;
                    moveRight.previousMove = new Pair(i, 'e');
                    allNeighbors.add(moveRight);
                }

                // If moving left is a valid move
                if(validMove(vehicles, vehicle[0], -1)) {
                    // Create new Configuration to add to ArrayList
                    Configuration moveLeft = new Configuration();
                    int[] movedVehicle = new int[vehicle.length];

                    // Make new ArrayList for vehicle positions
                    ArrayList<int[]> newVehicles = new ArrayList<>(vehicles);

                    // Adjust the int[] for the vehicle that got moved
                    for(int j = 0; j < vehicle.length; j++) {
                        movedVehicle[j] = vehicle[j] - 1;
                    }
                    newVehicles.set(i, movedVehicle);

                    // Finish making the Configuration and add to the ArrayList
                    moveLeft.vehicles = newVehicles;
                    moveLeft.previousMove = new Pair(i, 'w');
                    allNeighbors.add(moveLeft);
                }
            }

            // Vertical vehicle
            else {
                // If moving down is a valid move
                if(validMove(vehicles, vehicle[vehicle.length - 1], 6)) {
                    // Create new Configuration to add to ArrayList
                    Configuration moveDown = new Configuration();
                    int[] movedVehicle = new int[vehicle.length];

                    // Make new ArrayList for vehicle positions
                    ArrayList<int[]> newVehicles = new ArrayList<>(vehicles);

                    // Adjust the int[] for the vehicle that got moved
                    for(int j = 0; j < vehicle.length; j++) {
                        movedVehicle[j] = vehicle[j] + 6;
                    }
                    newVehicles.set(i, movedVehicle);

                    // Finish making the Configuration and add to the ArrayList
                    moveDown.vehicles = newVehicles;
                    moveDown.previousMove = new Pair(i, 's');
                    allNeighbors.add(moveDown);
                }

                // If moving up is a valid move
                if(validMove(vehicles, vehicle[0], -6)) {
                    // Create new Configuration to add to ArrayList
                    Configuration moveUp = new Configuration();
                    int[] movedVehicle = new int[vehicle.length];

                    // Make new ArrayList for vehicle positions
                    ArrayList<int[]> newVehicles = new ArrayList<>(vehicles);

                    // Adjust the int[] for the vehicle that got moved
                    for(int j = 0; j < vehicle.length; j++) {
                        movedVehicle[j] = vehicle[j] - 6;
                    }
                    newVehicles.set(i, movedVehicle);

                    // Finish making the Configuration and add to the ArrayList
                    moveUp.vehicles = newVehicles;
                    moveUp.previousMove = new Pair(i, 'n');
                    allNeighbors.add(moveUp);
                }
            }
        }

        return allNeighbors;
    }

    // validMove checks if a move is valid by seeing if it is in bounds, same row (if a horizontal vehicle), and that the space is not already occupied
    public boolean validMove(ArrayList<int[]> vehicles, int original, int movement) {
        // Check that the movement for any vehicle is not outside the map
        if(((original + movement) > 36) || ((original + movement) < 1)) {
            return false;
        }

        // For horizontal vehicles, make sure it isn't moving to another row
        // Moving left
        if((movement == -1) && ((original + movement) == ((original / 6) * 6))) {
            return false;
        }
        // Moving right
        if((movement == 1) && ((original + movement) == (((original / 6) * 6) + 1))) {
            return false;
        }

        // Check that the position is not already occupied
        for(int i = 0; i < vehicles.size(); i++) {
            int[] vehicle = vehicles.get(i);
            for(int j = 0; j < vehicle.length; j++) {
                if(vehicle[j] == (original + movement)) {
                    return false;
                }
            }
        }

        return true;
    }
} 

// No need to modify anything below
class HashKey {
        int[] c;

        public HashKey(int[] inputc) {
                c = new int[inputc.length];
                c = inputc;
        }
        
        public boolean equals(Object o) {
                boolean flag = true;
                if (this == o) return true;
                if ((o instanceof HashKey)) {
                        HashKey h = (HashKey)o;
                        int[] locs1 = h.c;
                        int[] locs = c;
                        if (locs1.length == locs.length) {
                                for (int i=0; i<locs1.length; i++) {
                                        if (locs1[i] != locs[i]) {
                                                flag = false;
                                                break;
                                        }
                                }
                        }
                        else
                                flag = false;
                }
                else 
                        flag = false;
                return flag;
        }
        
        /*
         * (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
                return Arrays.hashCode(c);
        }
}

// For constructing the solution
class Pair {
        int id; 
        char direction;  // 1: east; 2: south; 3: west; 4: north
        
        public Pair(int i, char d) {  id = i; direction = d; } 
        
        char getDirection() { return direction; }
        
        int getId() { return id; }
        
        void setDirection(char d) { direction = d; }
        
        void setId(int i) { id = i; }
}
