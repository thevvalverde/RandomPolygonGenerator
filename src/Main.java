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

	/*
	 *
	 */
	public static boolean UIfindIntersect(){
		System.out.println("find Intersect? (yes = 1)");
		if(stdin.nextInt() == 1) return true;
		return false;
	}

	/**
	 * Clones an arraylist of coordinates and its contents.
	 *
	 * @param base The ArrayList to be cloned.
	 * @return A new ArrayList with the same elements as the parameter.
	 */
	private static ArrayList<Coordinate> cloneArrayList(ArrayList<Coordinate> base) {
		ArrayList<Coordinate> clonedList = new ArrayList<>(base.size());
		for (Coordinate c : base) {
			clonedList.add(c.clone());
		}
		return clonedList;
	}

	/**
	 * Clones an arrayList to a TreeSet.
	 *
	 * @param base The ArrayList to be transformed.
	 * @return A TreeSet with the same elements as the parameter.
	 */
	private static TreeSet<Coordinate> cloneToTreeSet(ArrayList<Coordinate> base) {
		TreeSet<Coordinate> clonedSet = new TreeSet<>();
		for(Coordinate c : base) {
			clonedSet.add(c.clone());
		}
		return clonedSet;
	}

	/**
	 * Generates text to the user and waits for a response, to determine the methods which will be used to achieve the final goal.
	 * @param args A String array containing command line arguments (not used).
	 */
	public static void main(String[] args) {
		stdin = new Scanner(System.in);

		while (true) {

			System.out.println("Please enter the number of points to be generated: ");
			int n = stdin.nextInt();
			if(n==0) {
				System.out.println("Exiting...");
				return;
			}
			list = new ArrayList<>(n);

			System.out.println("Please enter the number corresponding to the function you desire.");
			System.out.println("1 - Enter Coordinates");
			System.out.println("2 - Randomly Generate");
			System.out.println("0 - Exit the program.");
			int p = stdin.nextInt();
			if(p==0) {
				System.out.println("Exiting...");
				return;
			}
			if(p==1){
				Scanner generator = stdin;

				for (int i = 0; i < n; i++) {
					int x = generator.nextInt();
					int y = generator.nextInt();

					Coordinate coor = new Coordinate(x, y, i);
					if (list.contains(coor))
						i--; 		//  n/(2*m)^2 chance
					else
						list.add(coor);
				}
			}
			if(p==2){
				Random generator = random;

				System.out.println("Please enter the boundary to generate the points: ");
				int m = stdin.nextInt();
				if(m==0) {
					System.out.println("Exiting...");
					return;
				}

				if(n>(4*m*m)) continue;		// restart loop

				for (int i = 0; i < n; i++) {
					int x = generator.nextInt(2 * m - 1) - m;
					int y = generator.nextInt(2 * m - 1) - m;

					Coordinate coor = new Coordinate(x, y, i);
					if (list.contains(coor))
						i--; 		//  n/(2*m)^2 chance
					else
						list.add(coor);
				}
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

			Candidate result;
			switch(choice){
				case 0:
					System.out.println("Exiting...");
					stdin.close();
					return;
				case 1:
				case 2:
					result = new Candidate(list, (byte)choice);
					break;
				case 3:
					System.out.println("Please enter a limit number of ants: ");
					int maxIterations = stdin.nextInt();
					result = new Candidate(list, (byte)choice);
					while((--maxIterations) > 0){
						result = result.nextAnt();
						System.out.print("Current solution: ");
						result.printList();	
					}
					break;
				default:
					System.out.println("Invalid input. Try again.");
					continue;
			}

			if(!result.checkIntegrity()) {
				System.out.println("Invalid input. Try again.");
				continue;	// restart loop
			}

			if(result.getIntersectionCount() != 0) {

	//			result.printNeighbours();

				if(result.getIntersectionCount() != 0) {
					System.out.println("Please enter the number corresponding to the function you desire.");
					System.out.println("1 - Best-improvement First");
					System.out.println("2 - First-improvement");
					System.out.println("3 - Less-conflicts");
					System.out.println("4 - Anyone");
					System.out.println("5 - Simulated Annealing (amount of intersections)");
					System.out.println("# - Exit the program.");

					choice = stdin.nextInt();
				}

				while(result.getIntersectionCount() != 0) {

					switch (choice) {
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
							System.out.println("Invalid input. Try again.");
							choice = 0;
					}

					System.out.print("Current solution: ");
					result.printList();
				}
			}

			System.out.print("Found the simple polygon: ");
			result.printList();
			System.out.println(" ----------------------------  ");
		}

	}
}
