package tkThreads;

import java.util.ArrayList;

import extendedFLC.ExtendedFLC;
import flatlc.levels.FlatLevelCombination;

public class CTSinner extends Thread{

	protected Object b;
	protected ArrayList<Integer> additionalData;
	protected FlatLevelCombination q;
	protected int index;
	protected int mode;
	
	
	public CTSinner(int mode, Object b, ArrayList<Integer> additionalData, FlatLevelCombination q, int index){
		this.b = b;
		this.additionalData = additionalData;
		this.q = q;
		this.index = index;
		this.mode = mode;
	}
	
	
	public CTSinner(int mode, Object b, FlatLevelCombination q){
		this.b = b;
		this.q = q;
		this.mode = mode;
	}
	
	
	public void run(){
		
		//using ExtendedFLC
		if(mode == 2){
//if q <_pareto b then				
			if(q.compare(((ExtendedFLC)b).getFLC()) == 1){
//mark b as dominated
				((ExtendedFLC)b).setAdditionalData(1);
			}
		}
		
		//using ArrayLists
		else if(mode == 4){
//if q <_pareto b then			
			if(q.compare((FlatLevelCombination)b) == 1){
//mark b as dominated				
				additionalData.set(index, 1);
			}
		}
		
		else{
			System.out.println("Mode not suitable for CTSinner!");
		}
		
	}
	
}
