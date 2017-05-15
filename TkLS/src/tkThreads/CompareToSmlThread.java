package tkThreads;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import dataGenerator.RandVector;
import extended.ExtendedRandVector;


public class CompareToSmlThread extends Thread{
	
	protected int threads;

	
	protected Object b;	
	protected ArrayList<ArrayList<Object>> S_ml;
	protected int k;
	protected int mode;
	
	//for ArrayLists (mode 3 and 4)
	protected ArrayList<Integer> additionalData;
	protected ArrayList<Integer> level;
	protected int index;
	
	
	static boolean is = false;
	//protected Score counter;
	
	public CompareToSmlThread(Object b, ArrayList<ArrayList<Object>> S_ml, int k, int mode, int threads){
	//public CompareToSmlThread(Object b, ArrayList<ArrayList<Object>> S_ml, int k, int mode, int threads, Score counter){
		this.b = b; 
		this.S_ml = S_ml;
		this.k = k;
		this.mode = mode;
		this.threads = threads;
		
		//this.counter = counter;
	}

	
	public CompareToSmlThread(Object b, ArrayList<ArrayList<Object>> S_ml, int k, int mode, ArrayList<Integer> additionalData, ArrayList<Integer> level, int index, int threads){
	//public CompareToSmlThread(Object b, ArrayList<ArrayList<Object>> S_ml, int k, int mode, ArrayList<Integer> additionalData, ArrayList<Integer> level, int index, int threads, Score counter){
		this.b = b; 
		this.S_ml = S_ml;
		this.k = k;
		this.mode = mode;	
		this.threads = threads;
		
		this.additionalData = additionalData;
		this.level = level;
		this.index = index;
		
		//this.counter = counter;
	}
	
	
	public void run(){
		if(mode == 1 || mode == 2){
			 runA((ExtendedRandVector) b);
		}
		else if(mode == 3 || mode == 4){
			runB((RandVector) b);
		}
	}
	
	
//Alg 3, line 2, for ExtendedRandVector:	
	protected void runA(ExtendedRandVector a){
		
//for j = 0 to k do	
		outerFor:
		for(int j = 0; j < k; j++){
//mark b as not dominated
			a.setAdditionalData(0);
//for all q in level S_ml[j] do
			
			//no inner parallelization
			if(mode == 1){
				innerFor:
					for(Object q: S_ml.get(j)){
//if q <_pareto b then		
	//Only true if q dominates b!	
						
						//counter.maxScore++;
						
						if(((RandVector)q).compare(a.getRND()) == 1){
//mark b as dominated		
							a.setAdditionalData(1);
							break innerFor;
						}
					}
			}

			
			//inner parallelization
			else if(mode == 2){
				ExecutorService pool = Executors.newFixedThreadPool(threads);
//for all q in level S_ml[j] do					
				for(Object q: S_ml.get(j)){
					pool.execute(new CTSinner(mode, a, (RandVector) q));
//if b is marked as dominated then						
					if(a.getAdditionalData() == 1){
//break inner for-loop
						break;
					}	
				}
				pool.shutdown();
				 try{
					 pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				 }
				 catch(InterruptedException e){
					 System.out.println("Error while waiting for termination!");
				 }		
			}
			

			
			
//if b is not marked as dominated then	
			if(! (a.getAdditionalData() == 1)){
//b.level <- j		
				a.setLevel(j);
				break outerFor;
			}
		}	
	
//if b is marked as dominated then	
		if(a.getAdditionalData() == 1){
//mark b as pruned		
			a.setAdditionalData(2);
		}
	}
	
	
//Alg 3, line 2, for ArrayLists:
	protected void runB(RandVector a){
//protected void runB(ArrayList<Integer>a){
//for j = 0 to k do	
		outerFor:
		for(int j = 0; j < k; j++){
//mark b as not dominated
			additionalData.set(index, 0);
			
			
			//No inner parallelization
			if(mode == 3){
//for all q in level S_ml[j] do	
				innerFor:
				for(Object q: S_ml.get(j)){
//if q <_pareto b then		
	//Only true if q dominates b!	
					
					//counter.maxScore++;
					
					if(((RandVector)q).compare(a) == 1){
//if(TestDataGenerator.compare((ArrayList<Integer>)q, a) == 1){					
//mark b as dominated	
						additionalData.set(index, 1);
						break innerFor;
					}
				}
			}
			
			
			//inner parallelization
			else if(mode == 4){				
				ExecutorService pool = Executors.newFixedThreadPool(threads);
//for all q in level S_ml[j] do				
				for(Object q: S_ml.get(j)){
//if q <_pareto b then
					pool.execute(new CTSinner(mode, a, additionalData, (RandVector)q, index));
//if b is marked as dominated then						
					if(additionalData.get(index) == 1){						
//break inner for-loop
						break;
					}
				}				
				pool.shutdown();
				 try{
					 pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				 }
				 catch(InterruptedException e){
					 System.out.println("Error while waiting for termination!");
				 }			 
			}
			
			
//if b is not marked as dominated then	
			if(! (additionalData.get(index) == 1)){
//b.level <- j	
				level.set(index, j);
				break outerFor;
			}
			
		}
	
//if b is marked as dominated then	
		if(additionalData.get(index) == 1){
//mark b as pruned			
			additionalData.set(index, 2);	
		}
	}


}
