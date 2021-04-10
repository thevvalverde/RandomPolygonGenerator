import java.util.ArrayList;

class NeighbourList extends ArrayList<Candidate>{

	/**
	 * Default constructor for NeighbourList class.
	 * @param candidate the candidate for which the neighbourlist is generated.
	 */
	public NeighbourList(Candidate candidate){
		super();

		ArrayList<Coordinate> base = new ArrayList<>(candidate);
		base.add(base.get(0));
		Coordinate a, b, c, d;

		for(int i = 0; i < base.size()-3; i++) {
			a = base.get(i);
			b = base.get(i+1);
			for(int j = i+2; j < base.size()-1; j++) {
				if(i == 0 && j == base.size()-2) continue;
				c = base.get(j);
				d = base.get(j+1);
				if(segmentsIntersect(a, b, c, d)) {
					Candidate next = twoExchange(candidate, b, c);
					if( !next.equals(candidate.parent) )
						this.add(next);
				}
			}
		}

	}

	/**
	 * Finds the neighbour with the smallest perimeter.
	 * @return the candidate with the smallest perimeter.
	 */
	public Candidate getSmallestPerimeter() {
		int size = Integer.MAX_VALUE;
		Candidate answer = null;
		for(int i = 0; i < this.size(); i++) {
			Candidate current = this.get(i);
			if (current.getPerimeter() < size) {
				size = current.getPerimeter();
				answer = current;
			}	
		}
		return answer;
	}

	/**
	 * Finds the neighbour with the fewest conflicts.
	 * @return the candidate with the fewest intersections.
	 */
	public Candidate getLessIntersections() {
		int intersections = Integer.MAX_VALUE;
		Candidate answer = null;
		for(int i=0; i < this.size(); i++){
			int conflicts = this.get(i).getIntersectionCount();
			if(conflicts < intersections) {
				intersections = conflicts;
				answer = this.get(i);
			}
		}
		return answer;
	}

	/**
	 * Exchange two coordinates, which changes two segments in the candidate.
	 * @param candidate The candidate which will have its segments changed.
	 * @param b The second point of the first segment.
	 * @param c The first point of the second segment.
	 * @return The candidate, updated.
	 */
	private Candidate twoExchange(Candidate candidate, Coordinate b, Coordinate c) {
		Candidate answer = new Candidate(candidate);
		int cIndex = answer.indexOf(c);
		int bIndex = answer.indexOf(b);

		answer.set(bIndex, c);
		answer.set(cIndex, b);

		return answer;
	}

	/**
	 * Checks if two segments intersect using geometric properties.
	 * @param a The first point of the first segment.
	 * @param b The second point of the first segment.
	 * @param c The first point of the second segment.
	 * @param d The second point of the second segment.
	 * @return True if the segments cross.
	 */
	private static boolean segmentsIntersect(Coordinate a, Coordinate b, Coordinate c, Coordinate d){
		int d1, d2, d3, d4;
		d1 = dot(a, b, c);
		d2 = dot(a, b, d);
		d3 = dot(c, d, a);
		d4 = dot(c, d, b);
		if( d1*d2 < 0 && d3*d4 < 0 ) return true;
		if( d1 == 0 && isInBox(a, b, c) ) return true;
		if( d2 == 0 && isInBox(a, b, d) ) return true;
		if( d3 == 0 && isInBox(c, d, a) ) return true;
		if( d4 == 0 && isInBox(c, d, b) ) return true;
		return false;
	}

	/**
	 * Checks if p3 is directly on top of the segment p1p2.
	 * @param p1 The first point of the segment.
	 * @param p2 The second point of the segment.
	 * @param p3 The point which we are checking.
	 * @return true p3 is directly on top of the segment p1p2.
	 */
	private static boolean isInBox(Coordinate p1, Coordinate p2, Coordinate p3) {
		return (Math.min(p1.getX(), p2.getX()) <= p3.getX() && p3.getX() <= Math.max(p1.getX(), p2.getX())) 
			&& (Math.min(p1.getY(), p2.getY()) <= p3.getY() && p3.getY()<= Math.max(p1.getY(), p2.getY()));
	}

	/**
	 * Checks to see, when we go from p1 to p3, and then to p3 to p2, if we have to turn left or right at p3.
	 * @param p1 The starting, reference point.
	 * @param p2 The point which we are analysing.
	 * @param p3 The end of the first segment.
	 * @return positive if we turn left at p1, negative if we turn right, and 0 if we do not turn.
	 */
	private static int dot(Coordinate p1, Coordinate p2, Coordinate p3) {
		return (p3.subtract(p1)).crossProduct(p2.subtract(p1));
	}

}
