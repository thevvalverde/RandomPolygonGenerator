import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;

public class Pair<T> extends AbstractMap.SimpleEntry<T, T>{
	public Pair(T a, T b){
		super(a, b);
	}

	@Override
	public String toString(){
		return "("+ this.getKey().toString() + ", " + this.getValue().toString() + ")";
	}
}
