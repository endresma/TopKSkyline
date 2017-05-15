package extended;


import flatlc.levels.FlatLevelCombination;

/**
 * 
 * Can be used to store all information needed for every tuple when using parallelTopK
 *
 */
public class ExtendedFLC extends ExtendedVector{

	
	protected FlatLevelCombination flc;
	
	
	public ExtendedFLC(FlatLevelCombination flc, int index){
		super(index);
		this.flc = flc;	
	}
	
	
	public FlatLevelCombination getFLC(){
		return flc;
	}

	@Override
	public String toString() {
		return new String("FLC: " + flc.toString() + ", Level: " + level);
	}
	
	
}
