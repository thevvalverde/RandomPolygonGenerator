import java.util.ArrayList;

class IntersectionList extends ArrayList< Pair<Pair<Coordinate>> >{

	public IntersectionList(){ super(); }

	public IntersectionList(Candidate list){
		super();

		list.add( list.get(0) );		// put first at the end to loop

		for(int i=0; i+1 < list.size(); i++){
			Pair<Coordinate> seg1 = new Pair<>(list.get(i), list.get(i+1));
			for(int j=i+1; j+1 < list.size(); j++){
				Pair<Coordinate> seg2 = new Pair<>(list.get(j), list.get(j+1));
				if(segmentsIntersect(seg1, seg2))
					this.add(new Pair<Pair<Coordinate>>(seg1, seg2));
			}
		}

		list.remove( list.size() -1 ); 	// remove the one we added
	}

	private static boolean segmentsIntersect(Pair<Coordinate> seg1, Pair<Coordinate> seg2){
		int d1, d2, d3, d4;
		d1 = dot(seg1.getKey(), seg1.getValue(), seg2.getKey());
		d2 = dot(seg1.getKey(), seg1.getValue(), seg2.getValue());
		d3 = dot(seg2.getKey(), seg2.getValue(), seg1.getKey());
		d4 = dot(seg2.getKey(), seg2.getValue(), seg1.getValue());
		if( d1*d2 < 0 && d3*d4 < 0 ) return true;
		if( d1 == 0 && isInBox(seg1.getKey(), seg1.getValue(), seg2.getKey()) ) return true;
		if( d2 == 0 && isInBox(seg1.getKey(), seg1.getValue(), seg2.getValue()) ) return true;
		if( d3 == 0 && isInBox(seg2.getKey(), seg2.getValue(), seg1.getKey()) ) return true;
		if( d4 == 0 && isInBox(seg2.getKey(), seg2.getValue(), seg1.getValue()) ) return true;
		return false;
	}

	private static boolean isInBox(Coordinate p1, Coordinate p2, Coordinate p3) {
		return (Math.min(p1.getX(), p2.getX()) <= p3.getX() && p3.getX() <= Math.max(p1.getX(), p2.getX()))
			&& (Math.min(p1.getY(), p2.getY()) <= p3.getY() && p3.getY()<= Math.max(p1.getY(), p2.getY()));
	}

	private static int dot(Coordinate p1, Coordinate p2, Coordinate p3) {
		return (p3.subtract(p1)).crossProduct(p1.subtract(p2));
	}

}
