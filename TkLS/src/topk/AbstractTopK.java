package topk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import flatlc.levels.FlatLevelCombination;


public abstract class AbstractTopK {

	 //number of tuples to find
	 protected final int k;
	 //list of all tuples
	 protected ArrayList<Object> D;
	 //k best tuples
	 protected ArrayList<Object> result;
	 //first k skylines 
	 protected ArrayList<ArrayList<Object>> S_ml;
	 
	 public AbstractTopK(ArrayList<Object> D, int k){
		 
		 this.D = D;
		 this.k = k;
		 
		 sortD();
		 
// initialize array (size k) of lists to store S_ml sets
		 S_ml = new ArrayList<ArrayList<Object>>();
		 for(int i = 0; i < k; i++){
			 S_ml.add(new ArrayList<Object>());
		 }			
	 }
	  
	 
	 /**
	  * Sort all Values in ArrayList D according to their L_1-norm 
	  */
	protected void sortD(){
		 if(D.size() == 0){
			 return;
		 }
		 Collections.sort(D, new Comparator<Object>(){
			 public int compare(Object o1, Object o2)
			 {
				 int norm1 = 0; int norm2 = 0;
				 int[] level1 = ((FlatLevelCombination)o1).getLevelCombination();
				 int[] level2 = ((FlatLevelCombination)o2).getLevelCombination();
				 for(int i = 0; i < level1.length; i++){
					 norm1 += level1[i];
					 norm2 += level2[i];
				 }
				 
				 return norm1 - norm2;
			 }
		 });
	 }
	 
	 
	 /**
	  * Copy first k elements from S_ml to results
	  */
	 protected void topKResults(){
		 //initialize result
		 result = new ArrayList<Object>();
		 //add first k elements from S_ml to result
		 resultFor:
		 for(int i = 0; i < S_ml.size(); i++){
			 if(S_ml.get(i).size() > 0){
				 for(int j = 0; j < S_ml.get(i).size(); j++){
					 if(result.size() < k){
						 result.add(S_ml.get(i).get(j));
					 }
					 else{
						 break resultFor;
					 }
				 }
			 }		 
		 }
	 }
	 
	 
	 public ArrayList<Object> getResult(){
		 return result;
	 }
	 
	 public void printD(){
		 System.out.println("D: ");
		 for(int i = 0; i < D.size(); i++){
			 System.out.println(D.get(i).toString());
		 }
		 System.out.print("\n");
	 }
	 
}
