package test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import flatlc.inputrelations.FlatLCResultSetA;

public class BenchFunctions {
	
	
	/**
	 * Generates FlatLCResultSetA, automatically using the right size and dimensions for realdata
	 * 
	 * @param m - size of input data (only used if no realdata is generated)
	 * @param dims - dimensions (only used if no realdata is generated)
	 * @param type - type of input-data 
	 * @return input-data
	 */
	public static FlatLCResultSetA getGeneratedInput(int m, int[]dims, String type){
		int n;
		int dimensions[];
		String path;
		switch(type){
			case "zillow":
				n = 2236252;
		        dimensions = new int[]{10, 10, 36, 45};
		        path = type;
		        break;
			case "nba":
				n = 17265;
		        dimensions = new int[]{10, 10, 10, 10, 10};
		        path = type;
		        break;        
			case "house":
				n = 127931;
				dimensions = new int[]{100,100,100,100,100,100};
				path = type;
		        break;
	        default:
	        	n = m;
	        	dimensions = dims;
	        	path = type;
	        	break;
		}
		FlatLCResultSetA input = (AlgorithmTest.generateInput(path, n, dimensions));
		return input;
	}
	
	
	/**
	 * Returns dims or, in case of realdata, dimensions fit for data type
	 * 
	 * @param type type of input-data
	 * @param dims dimensions
	 * @return dimensions for data type
	 */
	public static int[] getGeneratedDimensions(String type, int[]dims){
		int [] dimensions;
		switch(type){
			case "zillow":
		        dimensions = new int[]{10, 10, 36, 45};
		        break;
			case "nba":
		        dimensions = new int[]{10, 10, 10, 10, 10};
		        break;        
			case "house":
				dimensions = new int[]{100,100,100,100,100,100};
		        break;
	        default:
	        	dimensions = dims;
	        	break;
		}
		return dimensions;
	}
	
	
	/**
	 * Removes best and worst iteration from algResult
	 * 
	 * @param algResults - runtimes
	 * @return updated runtimes without best and worst result
	 */
	public static ArrayList<ArrayList<ArrayList<Object>>> removeBestAndWorst(ArrayList<ArrayList<ArrayList<Object>>> algResults){
		
		//Only remove results if there are more than 2 iterations
		if(algResults.get(0).get(0).size() < 3){
			System.out.println("No result removed!");
			return algResults;
		}
		
		//removing best and worst result
        for(int i = 0; i < algResults.size(); i++){
            long maxVal;
            long minVal;
        	for(int j = 0; j < algResults.get(i).size(); j++){
        		maxVal = (long)algResults.get(i).get(j).get(0);
        		minVal = (long)algResults.get(i).get(j).get(0);
        		
        		for(int q = 1; q < algResults.get(i).get(j).size(); q++){
            		if((long)algResults.get(i).get(j).get(q) < minVal){
            			minVal = (long)algResults.get(i).get(j).get(q);
            		}
            		if((long)algResults.get(i).get(j).get(q) > maxVal){
            			maxVal = (long)algResults.get(i).get(j).get(q);
            		}
            	}
        		//removing runtime of best and worst iteration
        		algResults.get(i).get(j).remove((Object)minVal);
        		algResults.get(i).get(j).remove((Object)maxVal);
        	}
        }	
		return algResults;
	}
	
	
	/**
	 * Initializes 3-dimensional ArrayList of size a*b*c with (long)0
	 * 
	 * @param a - size of first dimension
	 * @param b - size of second dimension
	 * @param c - size of third dimension
	 * @return 3-dimensional ArrayList filled with (long)0
	 */
	public static ArrayList<ArrayList<ArrayList<Object>>> initResult(int a, int b, int c){
		ArrayList<ArrayList<ArrayList<Object>>> result = new ArrayList<ArrayList<ArrayList<Object>>>();		
		for(int i = 0; i < a; i++){
			result.add(new ArrayList<ArrayList<Object>>());	
			for(int j = 0; j < b; j++){
				result.get(i).add(new ArrayList<Object>());
				for(int q = 0; q < c; q++){
					result.get(i).get(j).add((long)0);
				}
			}
		}
		return result;
	}
	
	
	/**
	 * Initializes 2-dimensional ArrayList of size a*b with (long)0
	 * 
	 * @param a - size of first dimension
	 * @param b - size of second dimension
	 * @return 2-dimensional ArrayList filled with (long)0
	 */
	public static ArrayList<ArrayList<Object>> initResult(int a, int b){
		ArrayList<ArrayList<Object>> result = new ArrayList<ArrayList<Object>>();		
		for(int i = 0; i < a; i++){
			result.add(new ArrayList<Object>());	
			for(int j = 0; j < b; j++){
				result.get(i).add((long)0);
			}
		}
		return result;
	}

	
	/**
	 * Computes average runtimes for 3-dimensional ArrayList
	 * 
	 * @param result - List of runtimes
	 * @param iterations - number of iterations
	 * @return List of average runtimes
	 */
	public static ArrayList<ArrayList<ArrayList<Object>>> avgResult(ArrayList<ArrayList<ArrayList<Object>>>result, int iterations){
		for(int i = 0; i < result.size(); i++){
			for(int j = 0; j < result.get(i).size(); j++){
				for(int q = 0; q < result.get(i).get(j).size(); q++){
					result.get(i).get(j).set(q, (long)result.get(i).get(j).get(q) / iterations);
				}
			}
		}
		return result;
	}
	
	
	/**
	 * Computes average runtimes for 2-dimensional ArrayList
	 * 
	 * @param result - List of runtimes
	 * @param iterations - number of iterations
	 * @return List of average runtimes
	 */
	public static ArrayList<ArrayList<Object>> avgRuntime(ArrayList<ArrayList<Object>> result, int iterations){
		for(int i = 0; i < result.size(); i++){
			for(int j = 0; j < result.get(i).size(); j++){				
				result.get(i).set(j, (long)result.get(i).get(j) / iterations);
			}
		}
		return result;
	}	
	
	
	/**
	 * Adds up runtimes for every iteration
	 * Sum is needed to compute average runtime
	 * 
	 * @param algResults - List of runtimes for every iteration
	 * @return Sum of runtimes
	 */
	public static ArrayList<ArrayList<Object>> addAlgResults(ArrayList<ArrayList<ArrayList<Object>>> algResults){
		
		ArrayList<ArrayList<Object>> result = BenchFunctions.initResult(algResults.get(0).size(), algResults.size());   
		
		for(int i = 0; i < algResults.get(0).size(); i++){
        	for(int j = 0; j < algResults.size(); j++){
        		for(int q = 0; q < algResults.get(j).get(i).size(); q++){
        			result.get(i).set(j, (long)result.get(i).get(j) + (long)algResults.get(j).get(i).get(q));
        		}        		
        	}
        }
		
		return result;
	}
	
	
	/**
	 * Changes total runtime to leftover runtime by subtracting the sum of the other runtimes
	 * For rowstacked histograms: total runtime is being divided into 5 phases: init, phase1, phase2, phase3, leftover runtime
	 * leftover runtime = total runtime - (init + phase1 + phase2 + phase3)
	 * 
	 * @param result - list of runtimes of parallelTopK (total runtime, init, phase1, phase2, phase2)
	 * @param iterations - number of iterations
	 * @return new list of runtimes, with leftover runtime instead of total runtime
	 */
	public static ArrayList<ArrayList<Object>> getLeftoverRuntime(ArrayList<ArrayList<Object>> result, int iterations){
		for(int i = 0; i < result.size(); i++){
        	long phases = 0;
        	for(int j = 1; j < result.get(i).size(); j++){
        		phases += (long)result.get(i).get(j);
        	}
        	if(((long)result.get(i).get(0) - phases) > 0){
        		result.get(i).set(0, (long)result.get(i).get(0) - phases);
        	}
        	else{
        		result.get(i).set(0, 0);
        	}
        }
		return result;
	}
	
	
	/**
	 * Computes values needed for creating errorbar diagrams and returns them in the format needed for BenchWrite.writeValsToErrorbar
	 * 
	 * @param algResults list of runtimes
	 * @param x first value of columns
	 * @return ArrayList of column-names and average-, min- and max-runtimes for every algorithm
	 */
	public static ArrayList<ArrayList<ArrayList<Object>>> getResultForErrorbars(ArrayList<ArrayList<ArrayList<Object>>> algResults, int[] x){

		ArrayList<ArrayList<ArrayList<Object>>> result = initResult(algResults.size(), algResults.get(0).size(), 4);
		
		//for every Algorithm (e.g. ebnl, esfs, tkls, sequential, parallel)
		for(int i = 0; i < algResults.size(); i++){
			//for ever block, e.g. n = {100, 1000, ...}
			for(int j = 0; j < algResults.get(i).size(); j++){				
				//get x
				result.get(i).get(j).set(0, x[j]);
				//ylow
				long minRuntime = (long)algResults.get(i).get(j).get(0);
				//yhigh
				long maxRuntime = (long)algResults.get(i).get(j).get(0);
				//y
				long avgRuntime = (long)algResults.get(i).get(j).get(0);
				//for every iteration
				for(int q = 1; q < algResults.get(i).get(j).size(); q++){
					//find minRuntime (= ylow), maxRuntime (= yhigh) and avgRuntime (= y)
					if((long)algResults.get(i).get(j).get(q) < minRuntime){
						minRuntime = (long)algResults.get(i).get(j).get(q);
					}
					if((long)algResults.get(i).get(j).get(q) > maxRuntime){
						maxRuntime = (long)algResults.get(i).get(j).get(q);
					}
					avgRuntime += (long)algResults.get(i).get(j).get(q);
				}
				avgRuntime /= algResults.get(i).get(j).size();
				//add runtimes to result
				result.get(i).get(j).set(1, avgRuntime);
				result.get(i).get(j).set(2, minRuntime);
				result.get(i).get(j).set(3, maxRuntime);
			}
		}
	
		return result;
	}
	
	
	
	/** 
	 * For unique name for Benchmark-Files
	 * 
	 * @return current time stamp
	 */
	public static String getTimeStamp(){
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	}
}
