import java.io.IOException;
import java.util.ArrayList;

public class HW {
    public static void main(String[] args) throws IOException {
        GameBoard gb = new GameBoard();

        gb.readInput(args[0]);

        ArrayList<Pair> path = gb.getPlan();

        System.out.println(path.size());

        for (int i=0; i<path.size(); i++) {
            System.out.println(path.get(i).getId() + " " + path.get(i).getDirection());
        }
    }
}
