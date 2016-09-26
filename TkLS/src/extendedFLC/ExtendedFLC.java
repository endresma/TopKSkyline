package extendedFLC;


import flatlc.levels.FlatLevelCombination;

/**
 * 
 * Can be used to store all information needed for every tuple when using parallelTopK
 *
 */
public class ExtendedFLC {

	
	protected FlatLevelCombination flc;
	
	//protected FLCenum additionalData;
	//0 == NOTDOMINATED,	1 == DOMINATED, 	2 == PRUNED
	protected int additionalData;
	
	protected int level;

	//index w.r.t. L_1 norm	
	protected int index;
	
	//protected ArrayList<Integer> dominators;
	//The parallelTopK-Algorithm only needs to know if there are any dominators and the max level of all dominators
	//no dominators are represented by -1, otherwise the max level is stored here
	//using an ArrayList to store all levels caused severe problems when using inner parallelization 
	protected int dominators;
	
	public ExtendedFLC(FlatLevelCombination flc, int index){
		this.flc = flc;	
		this.level = 0;		
		this.index = index;
		this.additionalData = 0;
		
		//dominators = new ArrayList<Integer>();
		this.dominators = -1;
	}
	
	
	public FlatLevelCombination getFLC(){
		return flc;
	}
	
	public void setAdditionalData(int newVal){
		this.additionalData = newVal;
	}
	
	public int getAdditionalData(){
		return additionalData;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public int getLevel(){
		return level;
	}
	
	public int getIndex(){
		return index;
	}
	
	public void setIndex(int i){
		this.index = i;
	}
	
	public void addDominator(int domLevel){	
		
		//this.dominators.add(domLevel);
		if(domLevel > dominators){
			dominators = domLevel;
		}
	}
	
	/*public ArrayList<Integer> getDominators(){
		return dominators;
	}
	
	public Integer getDominator(int i){
		return dominators.get(i);
	}*/	
	
	public int getMaxDomLevel(){
		/*int maxLevel = -1;
		for(int i = 0; i < dominators.size(); i++){
			if(dominators.get(i).getLevel() > maxLevel){
				maxLevel = dominators.get(i).getLevel();
			}
		}
		return maxLevel*/
		
		//return Collections.max(dominators);
		return dominators;
	}

	
	public void print(){
		System.out.println("FLC: " + flc.toString() + ", Level: " + level);
	}
}
