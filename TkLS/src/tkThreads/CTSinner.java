package tkThreads;

import java.util.ArrayList;

import dataGenerator.RandVector;
import extended.ExtendedRandVector;

public class CTSinner extends Thread{

	protected Object b;
	protected ArrayList<Integer> additionalData;
	protected RandVector q;
	protected int index;
	protected int mode;
	
	
	public CTSinner(int mode, Object b, ArrayList<Integer> additionalData, RandVector q, int index){
		this.b = b;
		this.additionalData = additionalData;
		this.q = q;
		this.index = index;
		this.mode = mode;
	}
	
	
	public CTSinner(int mode, Object b, RandVector q){
		this.b = b;
		this.q = q;
		this.mode = mode;
	}
	
	
	public void run(){
		
		//using ExtendedRandVector
		if(mode == 2){
//if q <_pareto b then				
			if(q.compare(((ExtendedRandVector)b).getRND()) == 1){
//mark b as dominated
				((ExtendedRandVector)b).setAdditionalData(1);
			}
		}
		
		//using ArrayLists
		else if(mode == 4){
//if q <_pareto b then			
			if(q.compare((RandVector)b) == 1){
//mark b as dominated				
				additionalData.set(index, 1);
			}
		}
		
		else{
			System.out.println("Mode not suitable for CTSinner!");
		}
		
	}
	
}
