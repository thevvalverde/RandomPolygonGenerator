import java.util.Scanner;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Random;

/**
 * The program asks the user for inputs to choose a way of generating, correcting and returning a simple polygon (Hamilton cycle with no intersections)
 * from a collection of randomly generated points in a 2D space.
 * 
 * @author Felipe Valverde
 * @author Murilo Rosa
 */
public class Main {
	static Scanner stdin;
	static ArrayList<Coordinate> list;
	static Random random = new Random();		// create new random generator

	/**
	 * Generic exiting message and return.
	 */
	public static void leave() {
		System.out.println("Exiting...");
		return;
	}
	
	/**
	 * Generates text to the user and waits for a response, to determine the methods which will be used to achieve the final goal.
	 * @param args A String array containing command line arguments (not used).
	 */
	public static void main(String[] args) {
		stdin = new Scanner(System.in);

		System.out.println("Please enter the number of points to be generated: ");

		int N = stdin.nextInt();
		if(N==0) {
			System.out.println("Exiting...");
			return;
		}

		list = new ArrayList<>(N);

		System.out.println("Please enter the number corresponding to the function you desire.");
		System.out.println("0 - Exit the program.");
		System.out.println("1 - Enter Coordinates");
		System.out.println("2 - Randomly Generate");
		int P = stdin.nextInt();

		switch(P) {
			case 0:
				leave();
				break;
			case 1:
				for(int i = 0; i < N; i++) {
					int x = stdin.nextInt();
					int y = stdin.nextInt();

					Coordinate coor = new Coordinate(x, y, i);
					if(list.contains(coor)) {
						System.out.println("Duplicate coordinate.");
						leave();
					}
					list.add(coor);
				}
				break;
			case 2:
				Random generator = random;
				System.out.println("Please enter a boundary:");
				
				int M = stdin.nextInt();
				if (N > (4*M*M)) {
					System.out.println("Boundary too small.");
					leave();
				}
				for (int i = 0; i < N; i++) {
					int x = generator.nextInt(2 * M - 1) - M;
					int y = generator.nextInt(2 * M - 1) - M;

					Coordinate coor = new Coordinate(x, y, i);
					if (list.contains(coor))
						i--; 		//  N/(2*M)^2 chance
					else
						list.add(coor);
				}
				break;
			default:
				System.out.println("Invalid input.");
				leave();
				break;
	
		}

		for (Coordinate c : list) {
			System.out.println(c.printName() + " " + c.toString());
		}

		int choice = -1;


		System.out.println("Please enter the number corresponding to the function you desire.");
		System.out.println("1 - Random permutation");
		System.out.println("2 - Nearest Neighbour");
		System.out.println("3 - Ant Colony Optimization");
		System.out.println("0 - Exit the program.");

		choice = stdin.nextInt();
		Candidate result = null;

		switch(choice){
			case 0:
				leave();
				break;	
			case 1:
			case 2:
				result = new Candidate(list, (byte)choice);
				break;
			case 3:
				System.out.println("Please enter the number of ants: ");
				int maxIterations = stdin.nextInt();
				result = new Candidate(list, (byte)choice);
				while((--maxIterations) >= 0){
					result = result.nextAnt();
					System.out.print("Current solution: ");
					result.printList();	
				}
				break;
			default:
				System.out.println("Invalid input.");
				return;
		}

		if(!result.checkIntegrity()) {
			System.out.println("Invalid input.");
			return;	
		}

		if(result.getIntersectionCount() != 0) {

			System.out.println("Please enter the number corresponding to the function you desire.");
			System.out.println("0 - Exit the program.");
			System.out.println("1 - Best-improvement First");
			System.out.println("2 - First-improvement");
			System.out.println("3 - Less-conflicts");
			System.out.println("4 - Anyone");
			System.out.println("5 - Simulated Annealing (amount of intersections)");

			choice = stdin.nextInt();

			while(result.getIntersectionCount() != 0) {

				switch (choice) {
					case 0:
						leave();
						break;
					case 1:
						result = result.improveBestFirst();
						break;
					case 2:
						result = result.improveFirst();
						break;
					case 3:
						result = result.improveLessConflict();
						break;
					case 4:
						result = result.improveRandom();
						break;
					case 5:
						System.out.println("Please enter a limit number of annealing iterations: ");
						int maxIterations = stdin.nextInt();
						double probability = 1.0;
						Candidate next;
						while( (--maxIterations) > 0 && probability > 0){
							probability *= 0.98;
							next = result.improveRandom();
							int delta = next.getIntersectionCount() - result.getIntersectionCount();
							if(delta > 0)
								result = next;
							else if (random.nextDouble() > probability)
								result = next;
						}
						choice = 3;		// change to find result by less conflicting
						break;
					default:
						System.out.println("Invalid input.");
						leave();
				}

				System.out.print("Current solution: ");
				result.printList();
			}
		}

		System.out.print("Found the simple polygon: ");
		result.printList();
		System.out.println(" ---------------------------- ");
	}
}
