package tkThreads;

import java.util.ArrayList;

import dataGenerator.RandVector;
import extended.ExtendedRandVector;

public class CTPinner extends Thread{

	protected Object a;
	protected Object b;
	protected ArrayList<Integer> additionalData;
	protected ArrayList<ArrayList<Integer>> dominators;
	protected ArrayList<Integer> level;
	protected int index;
	protected int j;
	protected int k;
	protected int mode;
	
	
	public CTPinner(int mode, Object a, Object b, ArrayList<Integer> additionalData, ArrayList<ArrayList<Integer>> dominators, ArrayList<Integer> level, int index, int j, int k){
		this.a = a;
		this.b = b;
		this.additionalData = additionalData;
		this.dominators = dominators;
		this.level = level;
		this.index = index;
		this.j = j;
		this.k = k;
		this.mode = mode;
	}
	
	
	public CTPinner(int mode, Object a, Object b, int k){
		this.a = a;
		this.b = b;
		this.k = k;
		this.mode = mode;
	}
	
	
	public void run(){
		
		//ExtendedRandVector
		if(mode == 2){			
			runA();
		}
		
		//RandVector + ArrayLists
		else if (mode == 4){
			runB();
		}
		
		else{
			System.out.println("Mode not suitable for CTPinner!");
		}
	}

	
//ExtendedRandVector	
	private void runA(){
		ExtendedRandVector a = (ExtendedRandVector)this.a;
		ExtendedRandVector b = (ExtendedRandVector)this.b;			
//if B[j] <_pareto b then			
		if(b.getRND().compare(a.getRND()) == 1){
			if(b.getLevel() == k - 1){
				a.setAdditionalData(2);					
			}
			else{		
				a.addDominator(b);				
			}
		}
	}
	
	
//ArrayLists	
	private void runB(){
//if B[j] <_pareto b then		
		if(((RandVector)b).compare((RandVector)a) == 1){			
			if(level.get(j) == k-1){						
				additionalData.set(index, 2);
			}
			else{					
				dominators.get(index).add(j);			
			}
		}
	}
}
