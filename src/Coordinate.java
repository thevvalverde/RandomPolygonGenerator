class Coordinate implements Comparable<Coordinate>{
	private int x;
	private int y;
	private int c;
	private String name;
	private double distance; // Note: I don't think we even need this.

	/**
	 * Default constructor.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param c Letter to represent the point.
	 */
	public Coordinate(int x, int y, int c) {
		this.x = x;
		this.y = y;
		this.c = c;
		this.name = makeName();
		this.distance = Double.POSITIVE_INFINITY;
	}

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
		this.c = 0;
		this.name = null;
		this.distance = Double.POSITIVE_INFINITY;
	}

	/**
	 * Clones this element.
	 * @param none
	 * @return A new Coordinate clone of this element
	 */
	public Coordinate clone() {
		return new Coordinate(this.x, this.y, this.c);
	}

	/**
	 * Performs simple coordinate subtraction.
	 * @param that the coordinate which we want to subtract.
	 * @return a new coordinate with the values obtained from the subtraction.
	 */
	public Coordinate subtract(Coordinate that) {
		return new Coordinate(this.x - that.getX(), this.y - that.getY());
	}

	/**
	 * Perfomrs a cross product, which is the determinant of a 2x2 matrix with the coordinates.
	 * @param that the second coordinate.
	 * @return an integer which is the determinant.
	 */
	public int crossProduct(Coordinate that) {
		return ((this.x*that.y) - (that.x*this.y));
	}

	/**
	 * Getter for X value.
	 * @return x value.
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Getter for Y value.
	 * @return y value.
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Default equals method for this class, checking if both x and y are equal.
	 */
	@Override
	public boolean equals(Object o) {
		if(this==o) return true;
		if(o==null) return false;
		if(getClass()!=o.getClass()) return false;
		Coordinate c = (Coordinate) o;
		return (this.x==c.x && this.y==c.y);
	}

	/**
	 * Writes the coordinate as (X, Y).
	 */
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}

	private String makeName() {
		if(c<26)
			return ""+(char)('A'+ c%26);
		return (char)('A'+ c%26)+Integer.toString(c/26-1);
	}

	/**
	 *
	 * @return the letter that represents the point.
	 */
	public String printName() {
		return name;
	}

	@Override
	public int compareTo(Coordinate c) {
		if(this.distance > c.distance) return +1;
		if(this.distance < c.distance) return -1;
		if(this.c > c.c) return +1;
		if(this.c < c.c) return -1;
		return 0;
	}
}
