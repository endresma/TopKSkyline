package extended;

import java.util.ArrayList;

/**
 * 
 * Can be used to store all information needed for every tuple when using parallelTopK
 *
 */
public abstract class ExtendedVector {
		
		//protected enum additionalData;
		//0 == NOTDOMINATED,	1 == DOMINATED, 	2 == PRUNED
		protected int additionalData;
		
		protected int level;

		//index w.r.t. L_1 norm	
		protected int index;
		
		//protected ArrayList<ExtendedVector> dominators;
		//The parallelTopK-Algorithm only needs to know if there are any dominators and the max level of all dominators
		//no dominators are represented by -1, otherwise the max level is stored here
		//using an ArrayList to store all levels caused severe problems when using inner parallelization 
		protected ArrayList<ExtendedVector> dominators;
		
		public ExtendedVector(int index){
			this.level = 0;		
			this.index = index;
			this.additionalData = 0;	
			//dominators = new ArrayList<Integer>();
			this.dominators = new ArrayList<>();
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
		
		public void addDominator(ExtendedVector dominator){	
			
			//this.dominators.add(domLevel);
			dominators.add(dominator);
		}
		
		public ArrayList<ExtendedVector> getDominators(){
			return dominators;
		}
		
		public int getMaxDomLevel() {
			int maxLevel = 0;
			for(ExtendedVector vec: dominators){
				int level = vec.getLevel();
				if(level > maxLevel)
					maxLevel = level;
			}
			return maxLevel;
		}
	}

