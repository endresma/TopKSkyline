package tkThreads;

import extendedFLC.ExtendedFLC;
import flatlc.levels.FlatLevelCombination;
import java.util.ArrayList;

public class CTPinner extends Thread{

	protected Object b;
	protected Object q;
	protected ArrayList<Integer> additionalData;
	protected ArrayList<Integer> dominators;
	protected ArrayList<Integer> level;
	protected int index;
	protected int j;
	protected int k;
	protected int mode;
	
	
	public CTPinner(int mode, Object b, Object q, ArrayList<Integer> additionalData, ArrayList<Integer> dominators, ArrayList<Integer> level, int index, int j, int k){
		this.b = b;
		this.q = q;
		this.additionalData = additionalData;
		this.dominators = dominators;
		this.level = level;
		this.index = index;
		this.j = j;
		this.k = k;
		this.mode = mode;
	}
	
	
	public CTPinner(int mode, Object b, Object q, int k){
		this.b = b;
		this.q = q;
		this.k = k;
		this.mode = mode;
	}
	
	
	public void run(){
		
		//ExtendedFLC
		if(mode == 2){			
			runA();
		}
		
		//FLC + ArrayLists
		else if (mode == 4){
			runB();
		}
		
		else{
			System.out.println("Mode not suitable for CTPinner!");
		}
	}

	
//ExtendedFLC	
	private void runA(){
		ExtendedFLC q = (ExtendedFLC)this.q;
		ExtendedFLC b = (ExtendedFLC)this.b;			
//if B[j] <_pareto b then			
		if(q.getFLC().compare(b.getFLC()) == 1){
			if(q.getLevel() == k - 1){
				b.setAdditionalData(2);					
			}
			else{		
				b.addDominator(q.getLevel());				
			}
		}
	}
	
	
//ArrayLists	
	private void runB(){
//if B[j] <_pareto b then		
		if(((FlatLevelCombination)q).compare((FlatLevelCombination)b) == 1){			
			if(level.get(index) == k-1){						
				additionalData.set(index, 2);
			}
			else{					
				if(level.get(j) > dominators.get(index)){
					dominators.set(index, level.get(j));
				}				
			}
		}
	}
}
