package extended;

import dataGenerator.RandVector;

/**
 * 
 * Can be used to store all information needed for every tuple when using parallelTopK
 *
 */
public class ExtendedRandVector extends ExtendedVector{
	
	protected RandVector rnd;
	
	public ExtendedRandVector(RandVector rnd, int index){
		super(index);
		this.rnd = rnd;	
	}
	
	
	public RandVector getRND(){
		return rnd;
	}
	
	
	@Override
	public String toString() {
		return rnd.toString();
	}
}
