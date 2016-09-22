package topk;

import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import flatlc.levels.FlatLevelCombination;
import tkThreads.CompareToPeersThread;
import tkThreads.CompareToSmlThread;
import util.Stopwatch;
import extendedFLC.ExtendedFLC;
//import extendedFLC.Score;


public class ParallelTopK extends AbstractTopK{
	
	
	//additionalData, level and dominators are used if mode is 3 or 4, for mode 1 and 2 those infos are stored in ExtendedFLCs
	 //0 == NOTDOMINATED,	1 == DOMINATED, 	2 == PRUNED
	 protected ArrayList<Integer> additionalData;
	 protected ArrayList<Integer> level;
	 
	 //storing only the maxlevel of all dominators for a certain object
	 //since this is the only value that is actually needed
	 protected ArrayList<Integer> dominators;
	 
	 protected int threads;	 
	 protected int blockSize;
	 protected int currentIndex;	 
	 //block, containing FlatLevelCombination(mode 3 or 4) or ExtendedFLC(mode 1 or 2)
	 protected ArrayList<Object> B;
	 
	 //for messuring runtime of certain phase
	 protected long init = 0;
	 protected long phase1 = 0;
	 protected long phase2 = 0;
	 protected long phase3 = 0;
	 
	 // 1 == ExtendedFLC, no inner parallelization
	 // 2 == ExtendedFLC, inner parallelization
	 // 3 == ArrayLists, no inner parallelization
	 // 4 == ArrayLists, inner parallelization
	 protected int mode;
	 
	 //public Score counter;

	 
	 public ParallelTopK(ArrayList<Object> D, int k, int mode, int blockSize, int threads){
		 
		 //Not included in runtime measurement
		 super(D, k);
		 
		 Stopwatch sw = new Stopwatch();
		 this.blockSize = blockSize;
		 this.mode = mode;
		 this.threads = threads;
		 this.currentIndex = 0;
		 
		 init = 0;
		 phase1 = 0;
		 phase2 = 0;
		 phase3 = 0;
		 
		 //counter = new Score();
		 //counter.maxScore = 0;
		 
		 if(mode == 3 || mode == 4){
			 initLists();
		 }
		 
		 long prior = sw.getElapsedNanoSecTime();
		 init = prior;

//while D is not empty do
		 while(currentIndex < D.size()){
// B = next b points from D	
			 B = new ArrayList<Object>();
			 
			 //get next block
			 for(int i = currentIndex; i < currentIndex + blockSize && i < D.size(); i++){
				
				 if(mode == 1 || mode == 2){
					 B.add(new ExtendedFLC((FlatLevelCombination)D.get(i), i - currentIndex));
				 }
				 else if(mode == 3 || mode == 4){
					 B.add(D.get(i));
				 }				 
				 
			 }
			 
			 compareToSml();			
			 phase1 += sw.getElapsedNanoSecTime() - prior;
			 prior = sw.getElapsedNanoSecTime();
			 
			 compareToPeers();			 
			 phase2 += sw.getElapsedNanoSecTime() - prior;
			 prior = sw.getElapsedNanoSecTime();
			 
			 updateGlobalSkylineSml();
			 phase3 += sw.getElapsedNanoSecTime() - prior;
			 prior = sw.getElapsedNanoSecTime();
			 
			 currentIndex = currentIndex + blockSize;
		 }
		 
//return the first k elements from S_ml
		 topKResults();
		 
		 //System.out.println("Final counter: " + counter.maxScore);	 
	 }
	
	 
//Algorithm 3	 
	 protected void compareToSml(){

		 ExecutorService pool = Executors.newFixedThreadPool(threads);
		 
		 for(int i = 0; i < B.size(); i++){
			 if(mode == 1 || mode == 2){
				 pool.execute(new CompareToSmlThread(B.get(i), S_ml, k, mode, threads));
				 //pool.execute(new CompareToSmlThread(B.get(i), S_ml, k, mode, threads, counter));
			 }
			 else if(mode == 3 || mode == 4){
				 pool.execute(new CompareToSmlThread(B.get(i), S_ml, k, mode, additionalData, level, currentIndex + i, threads));
				 //pool.execute(new CompareToSmlThread(B.get(i), S_ml, k, mode, additionalData, level, currentIndex + i, threads, counter));
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

	 
//Algorithm 4	 
	 protected void compareToPeers(){

		 ExecutorService pool = Executors.newFixedThreadPool(threads);
		 
		 for(int i = 0; i < B.size(); i++){	
			 if(mode == 1 || mode == 2){
				 pool.execute(new CompareToPeersThread(B.get(i), B, k, mode, threads));
				 //pool.execute(new CompareToPeersThread(B.get(i), B, k, mode, threads, counter));
			 }
			 else if(mode == 3 || mode == 4){
				 pool.execute(new CompareToPeersThread(B.get(i), B, k, mode, additionalData, dominators, level, currentIndex + i, i, threads));
				 //pool.execute(new CompareToPeersThread(B.get(i), B, k, mode, additionalData, dominators, level, currentIndex + i, i, threads, counter));
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
	 
	 
//Algorithm 5	 
	 protected void updateGlobalSkylineSml(){
		 
//for i = 0 to blockSize
		 for(int i = 0; i < B.size(); i++){
			 
			 //for ExtendedFLC
			 if(mode == 1 || mode == 2){
				ugsA((ExtendedFLC)B.get(i));			 
			 }
			 
			 //For ArrayLists
			 else if(mode == 3 || mode == 4){
				 ugsB(i);
			 }

		 }
	 }
	 
	 
//Algorithm 5 for ExtendedFLC
	 protected void ugsA(ExtendedFLC temp){
		 
//if B[i] is not marked as pruned then				
		if(temp.getAdditionalData() != 2){
//if B[i].dominators.size() != 0 then
			//if(temp.getDominators().size() != 0){
			if(temp.getMaxDomLevel() >= 0){
				
//maxlevel = max{q.level | q element of B[i].dominators}
				int maxLevel = temp.getMaxDomLevel();
//if B[i].level < maxlevel + 1 then
				if(temp.getLevel() < maxLevel +1){
//B[i].level <- maxlevel + 1
					temp.setLevel(maxLevel + 1);
				}
			}
//if B[i].level < k then			
			if(temp.getLevel() < k){
//S_ml[B[i].level].add(B[i])				
				S_ml.get(temp.getLevel()).add(temp);
			}
		}
	 }
	 
	 
//Algorithm 5 for or ArrayLists
	 protected void ugsB(int i){
		 int j = currentIndex + i;
		 
//if B[i] is not marked as pruned then
		 if(additionalData.get(j) != 2){
//if B[i].dominators.size() != 0 then
			 if(dominators.get(j) != -1){
				 
//maxlevel = max{q.level | q element of B[i].dominators}
				 int maxLevel = dominators.get(j);
//if B[i].level < maxlevel + 1 then
				 if(level.get(j) < maxLevel + 1){
//B[i].level <- maxlevel + 1	
					 level.set(j, maxLevel + 1);
				 }
			 }
			 if(level.get(j) < k){
				 S_ml.get(level.get(j)).add(B.get(i));
			 }
		 }
	 }
	 
 
	 /**
	  * Initialize ArrayLists to store information about level, dominators and additional data for every tuple
	  * If data is stored in ArrayLists
	  * 
	  */
	 protected void initLists(){
		 level = new ArrayList<Integer>();
		 additionalData = new ArrayList<Integer>();
		 dominators = new ArrayList<Integer>();
		 for(int i = 0; i < D.size(); i++){
			 level.add(0);
			 //Initialize every Object as NOTDOMINATED
			 additionalData.add(0);
			 //Initialize maxLevel of dominators as -1
			 dominators.add(-1);
		 }
	 }
	 
	 
	 /**
	  * 
	  * @return Runtimes of phases as ArrayList
	  */
	 public ArrayList<Object> getTimes(){
		 ArrayList<Object> times = new ArrayList<Object>();
		 times.add(init); times.add(phase1); times.add(phase2); times.add(phase3);
		 return times;
	 }
	 
	 
	 /**
	  * Print  result depending on mode
	  * (mode 3 and 4 equal print-function from SequentialTopK)
	  * 
	  */
	 public void printResult(){
		 for(int i = 0; i < result.size(); i++){
			 if(mode == 1 || mode == 2){
				 ((ExtendedFLC)result.get(i)).print();
			 }
			 else if(mode == 3 || mode == 4){
				 System.out.println(result.get(i).toString());				 
			 }
		 }
	 }	 
}
