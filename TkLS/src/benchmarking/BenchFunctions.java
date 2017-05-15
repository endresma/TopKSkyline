package benchmarking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

import dataGenerator.RandVector;

public class BenchFunctions {

//	/**
//	 * Generates FlatLCResultSetA, automatically using the right size and
//	 * dimensions for realdata
//	 * 
//	 * @param m
//	 *            - size of input data (only used if no realdata is generated)
//	 * @param dims
//	 *            - dimensions (only used if no realdata is generated)
//	 * @param type
//	 *            - type of input-data
//	 * @return input-data
//	 */
//	public static FlatLCResultSetA getGeneratedInput(int m, int[] dims, String type) {
//		int n;
//		int dimensions[];
//		String path;
//		switch (type) {
//		case "zillow":
//			n = 2236252;
//			dimensions = new int[] { 10, 10, 36, 45, 10, 10 };
//			path = type;
//			break;
//		case "nba":
//			n = 17265;
//			dimensions = new int[] { 10, 10, 10, 10, 10 };
//			path = type;
//			break;
//		case "house":
//			n = 127931;
//			dimensions = new int[] { 100, 100, 100, 100, 100, 100 };
//			path = type;
//			break;
//		default:
//			n = m;
//			dimensions = dims;
//			path = type;
//			break;
//		}
//		FlatLCResultSetA input = (AlgorithmTest.generateInput(path, n, dimensions));
//		return input;
//	}
	
	/**
	 * Generates FlatLCResultSetA, automatically using the right size and
	 * dimensions for realdata
	 * 
	 * @param m
	 *            - size of input data (only used if no realdata is generated)
	 * @param dims
	 *            - dimensions (only used if no realdata is generated)
	 * @param type
	 *            - type of input-data
	 * @return input-data
	 */
	public static RandVector[] getGeneratedInput(int m, int dims, String type, boolean isPadding) {
		int n;
		int dimensions;
		String path;
		switch (type) {
		case "zillow":
			n = 2245108;
			dimensions = 6;
			path = type;
			break;
		case "nba":
			n = 17265;
			dimensions = 5;
			path = type;
			break;
		case "house":
			n = 127931;
			dimensions = 6;
			path = type;
			break;
		default:
			n = m;
			dimensions = dims;
			path = type;
			break;
		}
		RandVector[] input = (AlgorithmTest.generateInput(path, n, dimensions, isPadding));
		return input;
	}

	/**
	 * Returns dims or, in case of realdata, dimensions fit for data type
	 * 
	 * @param type
	 *            type of input-data
	 * @param dims
	 *            dimensions
	 * @return dimensions for data type
	 */
	public static int getGeneratedDimensions(String type, int dims) {
		int dimensions;
		switch (type) {
		case "zillow":
			dimensions = 6;
			break;
		case "nba":
			dimensions = 5;
			break;
		case "house":
			dimensions = 6;
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
	 * @param algResults
	 *            - runtimes
	 * @return updated runtimes without best and worst result
	 */
	public static ArrayList<ArrayList<ArrayList<Object>>> removeBestAndWorst(
			ArrayList<ArrayList<ArrayList<Object>>> algResults) {

		// Only remove results if there are more than 2 iterations
		if (algResults.get(0).get(0).size() < 3) {
			System.out.println("No result removed!");
			return algResults;
		}

		// removing best and worst result
		for (int i = 0; i < algResults.size(); i++) {
			double maxVal;
			double minVal;
			for (int j = 0; j < algResults.get(i).size(); j++) {
				maxVal = (double) algResults.get(i).get(j).get(0);
				minVal = (double) algResults.get(i).get(j).get(0);

				for (int q = 1; q < algResults.get(i).get(j).size(); q++) {
					if ((double) algResults.get(i).get(j).get(q) < minVal) {
						minVal = (double) algResults.get(i).get(j).get(q);
					}
					if ((double) algResults.get(i).get(j).get(q) > maxVal) {
						maxVal = (double) algResults.get(i).get(j).get(q);
					}
				}
				// removing runtime of best and worst iteration
				algResults.get(i).get(j).remove((Object) minVal);
				algResults.get(i).get(j).remove((Object) maxVal);
			}
		}
		return algResults;
	}

	/**
	 * Initializes 3-dimensional ArrayList of size a*b*c with (double)0
	 * 
	 * @param a
	 *            - size of first dimension
	 * @param b
	 *            - size of second dimension
	 * @param c
	 *            - size of third dimension
	 * @return 3-dimensional ArrayList filled with (double)0
	 */
	public static ArrayList<ArrayList<ArrayList<Object>>> initResult(int a, int b, int c) {
		ArrayList<ArrayList<ArrayList<Object>>> result = new ArrayList<ArrayList<ArrayList<Object>>>();
		for (int i = 0; i < a; i++) {
			result.add(new ArrayList<ArrayList<Object>>());
			for (int j = 0; j < b; j++) {
				result.get(i).add(new ArrayList<Object>());
				for (int q = 0; q < c; q++) {
					result.get(i).get(j).add((double) 0);
				}
			}
		}
		return result;
	}

	/**
	 * Initializes 2-dimensional ArrayList of size a*b with (double)0
	 * 
	 * @param a
	 *            - size of first dimension
	 * @param b
	 *            - size of second dimension
	 * @return 2-dimensional ArrayList filled with (double)0
	 */
	public static ArrayList<ArrayList<Object>> initResult(int a, int b) {
		ArrayList<ArrayList<Object>> result = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < a; i++) {
			result.add(new ArrayList<Object>());
			for (int j = 0; j < b; j++) {
				result.get(i).add((double) 0);
			}
		}
		return result;
	}

	/**
	 * Computes medians for 3-dimensional ArrayList
	 * 
	 * @param runtimes
	 *            - List of runtimes
	 * @param iterations
	 *            - number of iterations
	 * @return List with the median of the runtimes
	 */
	public static ArrayList<ArrayList<Object>> medianResult(ArrayList<ArrayList<ArrayList<Object>>> runtimes) {

		double median;
		int size = runtimes.get(0).get(0).size();
		boolean isEven = size % 2 == 0 ? true : false;
		ArrayList<ArrayList<Object>> result = new ArrayList<>();

		for (int i = 0; i < runtimes.get(0).size(); i++) {
			ArrayList<Object> tmp = new ArrayList<>();
			for (int j = 0; j < runtimes.size(); j++) {
				// sort runtimes
				runtimes.get(i).get(j).sort(new Comparator<Object>() {
					@Override
					public int compare(Object o1, Object o2) {
						if ((double) o1 > (double) o2)
							return 1;
						else
							return -1;
					}
				});
				if (isEven)
					median = ((double) runtimes.get(j).get(i).get(size / 2 - 1)
							+ (double) runtimes.get(j).get(i).get(size / 2)) / 2;
				else
					median = (double) runtimes.get(j).get(i).get((size + 1) / 2 - 1);

				tmp.add(median);
			}
			result.add(tmp);
		}
		return result;
	}

	/**
	 * Computes medians for 2-dimensional ArrayList
	 * 
	 * @param runtimes
	 *            - List of runtimes
	 * @param iterations
	 *            - number of iterations
	 * @return List with the median of the runtimes
	 */
	public static ArrayList<Object> medianRuntime(ArrayList<ArrayList<Object>> runtimes) {

		double median;
		int size = runtimes.get(0).size();
		boolean isEven = size % 2 == 0 ? true : false;
		ArrayList<Object> result = new ArrayList<>();

		for (int i = 0; i < runtimes.size(); i++) {
			// sort runtimes
			runtimes.get(i).sort(new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					if ((double) o1 > (double) o2)
						return 1;
					else
						return -1;
				}
			});

			if (isEven)
				median = ((double) runtimes.get(i).get(size / 2 - 1) + (double) runtimes.get(i).get(size / 2)) / 2;
			else
				median = (double) runtimes.get(i).get((size + 1) / 2 - 1);

			result.add(median);
		}
		return result;
	}

	/**
	 * Computes variances for 3-dimensional ArrayList
	 * 
	 * @param runtimes
	 *            - List of runtimes
	 * @param iterations
	 *            - number of iterations
	 * @return List of average runtimes
	 */
	public static ArrayList<ArrayList<Object>> varianceResult(ArrayList<ArrayList<ArrayList<Object>>> runtimes,
			int iterations) {

		ArrayList<ArrayList<Object>> result = new ArrayList<>();
		
		// add iterations in result-variable with dimensions b.length*5
		ArrayList<ArrayList<Object>> avg = BenchFunctions.addAlgResults(runtimes);
		// compute average runtime for every phase and blockSize
		avg = BenchFunctions.avgRuntime(avg, iterations - 2);

		for (int i = 0; i < runtimes.get(0).size(); i++) {
			ArrayList<Object> tmp = new ArrayList<>();
			for (int j = 0; j < runtimes.size(); j++) {
				double variance = 0L;
				for (int q = 0; q < runtimes.get(j).get(i).size(); q++) {
					double diff = (double) runtimes.get(j).get(i).get(q) - (double) avg.get(i).get(j);
					variance += diff * diff;
				}
				tmp.add(variance/runtimes.get(j).get(i).size());
			}
			result.add(tmp);
		}

		return result;
	}

	
	/**
	 * Computes standard deviation for 3-dimensional ArrayList
	 * 
	 * @param runtimes
	 *            - List of runtimes
	 * @param iterations
	 *            - number of iterations
	 * @return List of average runtimes
	 */
	public static ArrayList<ArrayList<Object>> stdDeviationResult(ArrayList<ArrayList<ArrayList<Object>>> runtimes,
			int iterations) {
;
		ArrayList<ArrayList<Object>> result = varianceResult(runtimes, iterations);
		for(ArrayList<Object> list: result){
			for(int i=0; i < list.size(); i++){
				list.set(i, (double) Math.sqrt((double) list.get(i)));
			}
		}

		return result;
	}
	
	
	/**
	 * Computes average runtimes for 3-dimensional ArrayList
	 * 
	 * @param result
	 *            - List of runtimes
	 * @param iterations
	 *            - number of iterations
	 * @return List of average runtimes
	 */
	public static ArrayList<ArrayList<ArrayList<Object>>> avgResult(ArrayList<ArrayList<ArrayList<Object>>> result,
			int iterations) {
		for (int i = 0; i < result.size(); i++) {
			for (int j = 0; j < result.get(i).size(); j++) {
				for (int q = 0; q < result.get(i).get(j).size(); q++) {
					result.get(i).get(j).set(q, (double) result.get(i).get(j).get(q) / iterations);
				}
			}
		}
		return result;
	}

	/**
	 * Computes average runtimes for 2-dimensional ArrayList
	 * 
	 * @param result
	 *            - List of runtimes
	 * @param iterations
	 *            - number of iterations
	 * @return List of average runtimes
	 */
	public static ArrayList<ArrayList<Object>> avgRuntime(ArrayList<ArrayList<Object>> result, int iterations) {

		for (int i = 0; i < result.size(); i++) {
			for (int j = 0; j < result.get(i).size(); j++) {
				result.get(i).set(j, (double) result.get(i).get(j) / iterations);
			}
		}
		return result;
	}

	/**
	 * Adds up runtimes for every iteration Sum is needed to compute average
	 * runtime
	 * 
	 * @param algResults
	 *            - List of runtimes for every iteration
	 * @return Sum of runtimes
	 */
	public static ArrayList<ArrayList<Object>> addAlgResults(ArrayList<ArrayList<ArrayList<Object>>> algResults) {

		ArrayList<ArrayList<Object>> result = BenchFunctions.initResult(algResults.get(0).size(), algResults.size());

		for (int i = 0; i < algResults.get(0).size(); i++) {
			for (int j = 0; j < algResults.size(); j++) {
				for (int q = 0; q < algResults.get(j).get(i).size(); q++) {
					result.get(i).set(j, (double) result.get(i).get(j) + (double) algResults.get(j).get(i).get(q));
				}
			}
		}

		return result;
	}

	/**
	 * Changes total runtime to leftover runtime by subtracting the sum of the
	 * other runtimes For rowstacked histograms: total runtime is being divided
	 * into 5 phases: init, phase1, phase2, phase3, leftover runtime leftover
	 * runtime = total runtime - (init + phase1 + phase2 + phase3)
	 * 
	 * @param result
	 *            - list of runtimes of parallelTopK (total runtime, init,
	 *            phase1, phase2, phase2)
	 * @param iterations
	 *            - number of iterations
	 * @return new list of runtimes, with leftover runtime instead of total
	 *         runtime
	 */
	public static ArrayList<ArrayList<Object>> getLeftoverRuntime(ArrayList<ArrayList<Object>> result, int iterations) {
		for (int i = 0; i < result.size(); i++) {
			double phases = 0;
			for (int j = 1; j < result.get(i).size(); j++) {
				phases += (double) result.get(i).get(j);
			}
			if (((double) result.get(i).get(0) - phases) > 0) {
				result.get(i).set(0, (double) result.get(i).get(0) - phases);
			} else {
				result.get(i).set(0, 0);
			}
		}
		return result;
	}

	/**
	 * Computes values needed for creating errorbar diagrams and returns them in
	 * the format needed for BenchWrite.writeValsToErrorbar
	 * 
	 * @param algResults
	 *            list of runtimes
	 * @param x
	 *            first value of columns
	 * @return ArrayList of column-names and average-, min- and max-runtimes for
	 *         every algorithm
	 */
	public static ArrayList<ArrayList<ArrayList<Object>>> getResultForErrorbars(
			ArrayList<ArrayList<ArrayList<Object>>> algResults, int[] x) {

		ArrayList<ArrayList<ArrayList<Object>>> result = initResult(algResults.size(), algResults.get(0).size(), 4);

		// for every Algorithm (e.g. ebnl, esfs, tkls, sequential, parallel)
		for (int i = 0; i < algResults.size(); i++) {
			// for ever block, e.g. n = {100, 1000, ...}
			for (int j = 0; j < algResults.get(i).size(); j++) {
				// get x
				result.get(i).get(j).set(0, x[j]);
				// ylow
				double minRuntime = (double) algResults.get(i).get(j).get(0);
				// yhigh
				double maxRuntime = (double) algResults.get(i).get(j).get(0);
				// y
				double avgRuntime = (double) algResults.get(i).get(j).get(0);
				// for every iteration
				for (int q = 1; q < algResults.get(i).get(j).size(); q++) {
					// find minRuntime (= ylow), maxRuntime (= yhigh) and
					// avgRuntime (= y)
					if ((double) algResults.get(i).get(j).get(q) < minRuntime) {
						minRuntime = (double) algResults.get(i).get(j).get(q);
					}
					if ((double) algResults.get(i).get(j).get(q) > maxRuntime) {
						maxRuntime = (double) algResults.get(i).get(j).get(q);
					}
					avgRuntime += (double) algResults.get(i).get(j).get(q);
				}
				avgRuntime /= algResults.get(i).get(j).size();
				// add runtimes to result
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
	public static String getTimeStamp() {
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	}
}
