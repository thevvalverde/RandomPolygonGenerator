import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Random;

/**
 * A class to represent a series of lines connecting points in a 2D space.
 * The methods and variables in this class aim to build a program which can consistently generate from a series of points a polygon, or, more technically described,
 * a Hamilton cycle with no intersections. 
 * 
 * @author Felipe Valverde
 * @author Murilo Rosa
 * 
 */
class Candidate extends ArrayList<Coordinate> {
	
	private static final long serialVersionUID = 1L;
	Candidate parent;
	private int intersectionCount;
	private static SegmentMap pheromone = null;
	private static final Integer Q = 1;
	private int perimeter;
	NeighbourList neighbours;

	// ------------------------- Constructors ------------------------------ //
	
	/**
	 * Constructor for the class Candidate
	 * @param parent The arrayList that serves as the starting point for generating the initial candidate.
	 */
	public Candidate(ArrayList<Coordinate> parent){
		super(parent);
		parent = null;
		intersectionCount = -1;
		neighbours = null;
		perimeter = this.calculatePerim();
	}

	/**
	 * Constructor for a child of the class Candidate.
	 * @param parent The candidate which we want our new candidate to be a son of.
	 */
	public Candidate(Candidate parent){
		super(parent);
		this.parent = parent;
		intersectionCount = -1;
		neighbours = null;
		perimeter = this.calculatePerim();
	}

	/**
	 * Constructor for a Candidate which takes the selected method as an input.
	 * @param parent The list of coordinates to build the Candidate.
	 * @param generator The choice of method (1 for random, 2 for nearest neighbour).
	 */
	public Candidate(ArrayList<Coordinate> parent, byte generator){
		super(parent.size());
		this.parent = new Candidate(parent);

		ArrayList<Coordinate> base; 
		Coordinate cur = null, next;
		base = new ArrayList<>(parent);
		pheromone = new SegmentMap();
		switch(generator){
			case 1:		// generate by Random Permutation
				while (base.size() > 0) {
					int i = new Random().nextInt(base.size());
					this.add(base.remove(i));
				}
				break;
			case 2:		// generate by Nearest Neighbour
				cur = base.remove(0);
				while (cur != null) {
					this.add(cur);
					cur = findNearest(cur, base);
				}
				break;
			case 3:
				cur = base.remove( (new Random()).nextInt(parent.size()) );	// start at a random
				while (cur != null) {
					this.add(cur);
					cur = findNearest(cur, base);
				}
//				this.Q = calculatePerim();
				for(int i=1; i<this.size(); i++)
					pheromone.put( new Pair<Coordinate>(this.get(i-1), this.get(i)), 1.0);
				pheromone.put( new Pair<Coordinate>(this.get(size()-1), this.get(0)), 1.0);
				break;
			default:		// preferably throw Exception
				intersectionCount = -1;
				return;
		}

		intersectionCount = -1;
//		intersected = new IntersectionList(this);
		neighbours = null;
		perimeter = this.calculatePerim();
	}

	/**
	 * Constructor for a Candidate to deal with ants.
	 * @param parent The parent array.
	 * @param isAnt A boolean to choose this constructor.
	 */
	public Candidate(Candidate parent, boolean isAnt){
		super(parent.size());
		this.parent = parent;
		intersectionCount = -1;
		neighbours = null;
		perimeter = this.calculatePerim();

		if(isAnt && pheromone != null){
			ArrayList<Coordinate> base = new ArrayList<>(parent);
			Coordinate cur, next;
		
			cur = base.remove( (new Random()).nextInt(parent.size()) );	// start at a random
			while (cur != null) {
				this.add(cur);
				cur = findAntPath(cur, base);
				if(!base.remove(cur))
					break;
			}
			double L = (double)calculatePerim();
			for(int i=1; i<this.size(); i++)
				pheromone.put( new Pair<Coordinate>(this.get(i-1), this.get(i)), (double) Q/L);
			pheromone.put( new Pair<Coordinate>(this.get(size()-1), this.get(0)), (double) Q/L);
		}
	}

	// ------------------------- Getters ---------------------------------- //

	/**
	 * Getter for the perimeter.
	 * @return the perimeter (with squared euclidian distances).
	 */
	public int getPerimeter() {
		return this.perimeter;
	}

	// ------------------------- Checks ----------------------------------- //

	/**
	 * Guarantees that the candidate did not add or delete any node.
	 * @return true if the candidate has as many points as its parent.
	 */
	public boolean checkIntegrity(){
		return this.size() == parent.size();
	}

	// ------------------------- Initial Generation ----------------------- //

	/**
	 * Generates a random permutation of the points in an array.
	 *
	 * @param base   The array containing the points we wish to operate on.
	 * @return The Candidate containing the points in a random order.
	 */
	public static Candidate randomPermutation(ArrayList<Coordinate> base) {
		return new Candidate(base, (byte)1);
	}

	/**
	 * Insert into an ArrayList the neighbours sorted by who is nearest to
	 * the starting point.
	 *
	 * @param base   The array containing the points we wish to operate on.
	 * @return The Candidate containing the points sorted by the Nearest Neighbour technique.
	 */
	public static Candidate nearestNeighbour(ArrayList<Coordinate> base) {
		return new Candidate(base, (byte)2);
	}

	// ------------------------- Calculations ----------------------------- //

	/**
	 * Find the square of the euclidian distance between two points.
	 *
	 * @param x1 X coordinate of the first point.
	 * @param y1 Y coordinate of the first point.
	 * @param x2 X coordinate of the second point.
	 * @param y2 Y coordinate of the second point.
	 * @return the square of the euclidian distance.
	 */
	private static int euclidianDistance(int x1, int y1, int x2, int y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}

	/**
	 * Find the square of the euclidian distance between two coordinates
	 * @param a The first coordinate
	 * @param b The second coordinate
	 * @return the square of the euclidian distance.
	 */
	public static int euclidianDistance(Coordinate a, Coordinate b) {
		return euclidianDistance(a.getX(), a.getY(), b.getX(), b.getY());
	}

	/**
	 *
	 * @param none
	 * @return the preimeter of the path given by this Candidate.
	 */
	public int calculatePerim(){
		int curPerimeter = 0;
		for(int j = 0; j < this.size(); j++) {
			if(j < this.size()-1)
				curPerimeter += Candidate.euclidianDistance(this.get(j), this.get(j+1));
			else 
				curPerimeter += Candidate.euclidianDistance(this.get(j), this.get(0));
		}
		return curPerimeter;
	}

	/**
	 * Counts how many lines cross in the present candidate.
	 * @return an integer with the number of intersections.
	 */
	public int getIntersectionCount(){
		if(neighbours == null)
			neighbours = new NeighbourList(this);
		if(intersectionCount == -1)
			intersectionCount = neighbours.size();
		return intersectionCount;
	}
		
	/**
	 * Analyses all non-visited neighbours and finds the nearest.
	 *
	 * @param cur  The current coordinate we are measuring distances from.
	 * @param base The ArrayList containing its neighbours.
	 * @return the nearest coordinate.
	 */
	private static Coordinate findNearest(Coordinate cur, ArrayList<Coordinate> base) {
		if (base.size() == 0)
			return null;

		int toBeRemoved = 0;
		int distance = Integer.MAX_VALUE;

		for (int i = 0; i < base.size(); i++) {
			Coordinate next = base.get(i);
			int nextDist = euclidianDistance(cur, next);
			if (nextDist < distance) {
				distance = nextDist;
				toBeRemoved = i;
			}
		}

		return base.remove(toBeRemoved);
	}

	// ------------------------- Neighbour Searching --------------------- //

	/**
	 * Checks the perimeter of every neighbour candidate and finds the smallest.
	 * @return the candidate with the smallest perimeter.
	 */
	public Candidate improveBestFirst() {
		if(neighbours == null)
			neighbours = new NeighbourList(this);
		return neighbours.getSmallestPerimeter();
	}

	/**
	 * Chooses the first candidate in the list of neighbours.
	 * @return the candidate ate index 0 in the neighbour list.
	 */
	public Candidate improveFirst(){
		if(neighbours == null)
			neighbours = new NeighbourList(this);
		return neighbours.get(0);
	}

	/**
	 * Counts the number of conflicts in every candidate in the neighbour list.
	 * @return the neighbour which has the less number of intersections.
	 */
	public Candidate improveLessConflict() {
		if(neighbours == null)
			neighbours = new NeighbourList(this);
		return neighbours.getLessIntersections();
	}

	
	/**
	 * Gets a random neighbour.
	 * @return a random neighbour.
	 */
	public Candidate improveRandom() {
		if(neighbours == null)
			neighbours = new NeighbourList(this);
		return neighbours.get( new Random().nextInt(neighbours.size()) );
	}

	// -------------------------- Ant Colony ----------------------------- //

	/**
	 * 
	 * @return a poligon generated by the next ant.
	 */
	public Candidate nextAnt(){
		if(Q != null && pheromone != null)
			return new Candidate(this, true);
		return null;
	}

	/**
	 * 
	 * @param i current coordinate
	 * @param list the points to which 'i' can link to form a segment
	 * @return a segment generated by ant.
	 */
	public Coordinate findAntPath(Coordinate i, ArrayList<Coordinate> list){
		if(list.size() < 1)
			return null;

		TreeMap<Double, Coordinate> probability = new TreeMap<Double, Coordinate>();
		double value = 0, last = 0;
		for(Coordinate k : list)
			if(pheromone.get( new Pair<Coordinate>(i, k) ) != null)
				value += (pheromone.get( new Pair<Coordinate>(i, k) ) * ( 1.0/euclidianDistance(i,k) ));
		for(Coordinate j : list){
			if(pheromone.get( new Pair<Coordinate>(i, j) ) == null)
				pheromone.put( new Pair<Coordinate>(i, j), 0.0 );
			else
				last += (pheromone.get( new Pair<Coordinate>(i, j) ) * ( 1.0/euclidianDistance(i,j) ))
					/ value;
			probability.put(last, j);
		}
//		System.out.println("!!!!!!!!!!!!"+list.size());
		return probability.ceilingEntry( (new Random()).nextDouble() * last ).getValue();
	}

	// --------------------------  Prints -------------------------------- //

	/**
	 * Prints every candidate in the neighbourList of the current candidate. 
	 */
	public void printNeighbours() {

		if(neighbours == null)
			neighbours = new NeighbourList(this);
		System.out.print("Original order: ");
		this.printList();
		System.out.println("Number of intersections: " + this.getIntersectionCount());
		for(Candidate list : neighbours) {
			list.printList();
		}
	}

	/**
	 * Prints an ArrayList of coordinates using their names.
	 * @param result The ArrayList to be printed.
	 */
	public void printList() {
		if(this == null){
			System.out.println("result is null");
			return;
		}
		String ans = "[";
		for (int i = 0; i < this.size(); i++) {
			ans += this.get(i).printName();
			if (i != this.size() - 1)
				ans += ", ";
		}
		ans += "]";
		System.out.println(ans);
		System.out.println("Perimeter: " + this.getPerimeter() + " | Intersections: " + this.getIntersectionCount());
	}


}
