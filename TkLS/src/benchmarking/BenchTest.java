package benchmarking;

import java.util.ArrayList;
import java.util.Arrays;

import dataGenerator.RandVector;

public class BenchTest {

	/**
	 * Creates rowstacked diagram showing the runtime of the parallelTopK for
	 * different block sizes
	 * 
	 * @param type
	 *            - data-type that should be used for input
	 * @param iter
	 *            - number of iterations
	 * @param m
	 *            - mode for parallelTopK (1, 2, 3 or 4)
	 */
	public static void benchmarkBlockSize(String type, int iter, int m) {

		/*TODO: iter+2 
		 * removeBestAndWorst entkommentieren*/
		int iterations = iter;
		int mode = m;
		int n = 100_000;
		int[] k = new int[] { 10, 100, 500, 719, 800, 921, 1000, 1132, 1140, 1150, 1155, 1164, 1170 };
		int dimensions = 5;
		int c = 16;

		// Options for blockSize
		int b[] = new int[] { 100, 1000, 10_000, 100_000 /* , 1_000_000 */ };

		// generate input-tuples
		RandVector[] input = BenchFunctions.getGeneratedInput(n, dimensions, type, false);

		for (int index = 0; index < k.length; index++) {
			// initialize results
			ArrayList<ArrayList<ArrayList<Object>>> algResults = BenchFunctions.initResult(5, b.length, iterations);

			// get times for every iteration
			for (int i = 0; i < iterations; i++) {
				System.out.println("\nIteration " + i + ": ");
				System.gc();
				for (int j = 0; j < b.length; j++) {
					System.out.println("BlockSize: " + b[j]);

					// get times for parallel topk (total runtime, init, phase1,
					// phase2, phase3)
					ArrayList<Object> times = AlgorithmTest.runParallelTopK(new ArrayList<Object>(Arrays.asList(input)),
							k[index], b[j], mode, c);
					for (int q = 0; q < times.size(); q++) {
						algResults.get(q).get(j).set(i, (double) times.get(q));
					}
					System.gc();
				}
			}

			// remove best and worst iterations for every phase and blockSize
			//algResults = BenchFunctions.removeBestAndWorst(algResults);
			// add iterations in result-variable with dimensions b.length*5
			ArrayList<ArrayList<Object>> result = BenchFunctions.addAlgResults(algResults);
			// compute average runtime for every phase and blockSize
			result = BenchFunctions.avgRuntime(result, iterations);

			// write results into datFile, write pltFile and plot diagram
			BenchWrite.writeValsToRowstacked(result, "/home/wohlfast/Tests/", new String[] { "BlockSize" },
					new String[] { "BlockSize", "Runtime(s)" },
					new String[] { "100", "1000", "10000",
							"100000"/* , "1000000" */ },
					new String[] { "BlockSize", "-" + type + "-n" + n + "-k" + k[index] });
		}
		System.out.println("--ENDE--");
	}

	/**
	 * Creates errorbar diagram showing the average runtime of ebnl, esfs, tkls,
	 * sequentialTopK and parallelTopK for different sizes of input-data
	 * 
	 * @param type
	 *            - data-type that should be used for input
	 * @param iter
	 *            - number of iterations
	 * @param m
	 *            - mode for parallelTopK (1, 2, 3 or 4)
	 */
	public static void benchmarkN(String type, int iter, int m) {

		int iterations = iter + 2;
		int mode = m;
		int dimensions = 5;
		int k = 10;
		int c = 16;
		int b[] = new int[] { 1000, 1000, 10000, 10000 };

		// Options for dataSize
		// int n[] = new int[] { 1000, 10000, 100000, 1000000 };
		int n[] = new int[] { 1_000_000 };

		// Input-data for every dataSize
		ArrayList<RandVector[]> input = new ArrayList<>();
		for (int i = 0; i < n.length; i++) {
			input.add(AlgorithmTest.generateInput(type, n[i], dimensions, false));
		}

		// initialize results (with (double)0)
		ArrayList<ArrayList<ArrayList<Object>>> algResults = BenchFunctions.initResult(1, n.length, iterations);

		// get times for every iteration
		for (int i = 0; i < iterations; i++) {
			System.out.println("\n\nIteration " + i + ": ");
			System.gc();
			for (int j = 0; j < input.size(); j++) {
				System.out.println("\nInput:  " + n[j]);
				// algResults.get(0).get(j).set(i,
				// (double) AlgorithmTest.runEBNLTopK(new
				// ArrayList<Object>(Arrays.asList(input.get(j))), k));
				// System.gc();
				// algResults.get(1).get(j).set(i,
				// (double) AlgorithmTest.runESFSTopK(new
				// ArrayList<Object>(Arrays.asList(input.get(j))), k));
				// System.gc();
				algResults.get(0).get(j).set(i, (double) AlgorithmTest
						.runSequentialTopK(new ArrayList<Object>(Arrays.asList(input.get(j))), k));
				System.gc();
				// algResults.get(2).get(j).set(i, (double) AlgorithmTest
				// .runParallelTopK(new
				// ArrayList<Object>(Arrays.asList(input.get(j))), k, b[j],
				// mode, c).get(0));
				// System.gc();
				// algResults.get(4).get(j).set(i,
				// (double) AlgorithmTest.runTkLS(new
				// ArrayList<Object>(Arrays.asList(input.get(j))), k,
				// dimensions));
				// System.gc();
			}
		}

		// get resulting times in format for errorbars, ignoring best and worst
		// result of every algorithm for every dataSize
		ArrayList<ArrayList<ArrayList<Object>>> result = BenchFunctions.getResultForErrorbars(algResults, n);

		// write results into datFile, write pltFile and plot diagram
		// String blockLabels[] = new String[] { "ebnl", "esfs", "sequential",
		// "parallel", "tkls" };
		String blockLabels[] = new String[] { "ebnl", "esfs", "parallel" };
		String data[] = new String[] { "1000", "10000", "100000", "1000000" };
		BenchWrite.writeValsToErrorbar(result, "/home/wohlfast/Tests/", blockLabels, data,
				new String[] { "Input-Size", "Runtime(s)" }, new String[] { "InputSize", "-" + type }, true);
		System.out.println("--ENDE--");
	}

	/**
	 * Only using large input-sizes (500.000 to 2.000.000) Creates errorbar
	 * diagram showing the average runtime of ebnl, esfs, tkls, sequentialTopK
	 * and parallelTopK for different sizes of input-data
	 * 
	 * @param type
	 *            - data-type that should be used for input
	 * @param iter
	 *            - number of iterations
	 * @param m
	 *            - mode for parallelTopK (1, 2, 3 or 4)
	 */
	public static void benchmarkBigN(String type, int iter, int m) {

		int iterations = iter;
		int mode = m;
		int dimensions = 5;
		int[] k = new int[]{ 10, 100, 1035, 1999, 3166, 8348, 14781, 20000 };
		int c = 16;
		int[] b = new int[] { 1000, 10_000, 10_000 };

		// Options for dataSize
		int n[] = new int[] { 100_000, 1_000_000, 10_000_000 };

		// Data for every dataSize
		ArrayList<RandVector[]> input = new ArrayList<>();
		for (int i = 0; i < n.length; i++) {
			type = "ind-n" +n[i]+ "-d5";
			input.add(AlgorithmTest.generateInput(type, n[i], dimensions, false));
		}

		for (int index = 0; index < k.length; index++) {

			// initialize results (with (double)0)
			ArrayList<ArrayList<ArrayList<Object>>> algResults = BenchFunctions.initResult(1, n.length, iterations);

			// get runtimes for every iteration
			for (int i = 0; i < iterations; i++) {
				System.out.println("\n\nIteration " + i + ": ");
				System.gc();
				for (int j = 0; j < input.size(); j++) {
					System.out.println("\nInput:  " + n[j]);
//					algResults.get(0).get(j).set(i,
//							(double) AlgorithmTest.runEBNLTopK(new ArrayList<Object>(Arrays.asList(input.get(j))), k[index]));
//					System.gc();
//					algResults.get(1).get(j).set(i,
//							(double) AlgorithmTest.runESFSTopK(new ArrayList<Object>(Arrays.asList(input.get(j))), k[index]));
//					System.gc();
					// algResults.get(2).get(j).set(i, (double) AlgorithmTest
					// .runSequentialTopK(new
					// ArrayList<Object>(Arrays.asList(input.get(j))), k));
					// System.gc();
					algResults.get(0).get(j).set(i, (double) AlgorithmTest
							.runParallelTopK(new ArrayList<Object>(Arrays.asList(input.get(j))), k[index], b[j], mode, c)
							.get(0));
					System.gc();
					// algResults.get(4).get(j).set(i,
					// (double)
					// AlgorithmTest.runTkLS(input.get(j).getElements(), k,
					// dimensions));
					// System.gc();
				}
			}
			// get resulting times in format for errorbars, ignoring best and
			// worst
			// result of every algorithm for every dataSize
			ArrayList<ArrayList<ArrayList<Object>>> result = BenchFunctions.getResultForErrorbars(algResults, n);

			// write results into datFile, write pltFile and plot diagram
			// String blockLabels[] = new String[] { "ebnl", "esfs",
			// "sequential",
			// "parallel", "tkls"};
			String blockLabels[] = new String[] { /*"ebnl", "esfs",*/ "parallel" };
			String data[] = new String[] { "100000", "1000000", "10000000" };
			BenchWrite.writeValsToErrorbar(result, "/home/wohlfast/Tests/", blockLabels, data,
					new String[] { "Input-Size", "Runtime(s)" }, new String[] { "BigN", "-" + type + "k-" +k[index]}, false);

		}
		System.out.println("--ENDE--");
	}

	/**
	 * Creates errorbar diagram showing the average runtime of ebnl, esfs, tkls,
	 * sequentialTopK and parallelTopK for different topK (2 to 1000) Also works
	 * for real data (house, nba, zillow)
	 * 
	 * @param type
	 *            - data-type that should be used for input
	 * @param iter
	 *            - number of iterations
	 * @param m
	 *            - mode for parallelTopK (1, 2, 3 or 4)
	 */
	public static void benchmarkK(String type, int iter, int m) {

		// Resulting histogram is hard to read due to big differences in
		// runtimes.
		// Gnuplot does not seem to support logarithmic scales for histograms

		// only used for anti, corr, ind and gaussian
		int dims = 5;
		int n = 100_000;
		int dimensions = BenchFunctions.getGeneratedDimensions(type, dims);

		int iterations = iter + 2;
		int mode = m;
		int c = 16;
		int b = 10000;
		// int b = 1000;

		// Options for topK
		int k[] = new int[] { 10, 100, 500, 719, 800, 921, 1000, 1132, 1140, 1150, 1155, 1164, 1170 };

		// generate input-tuples
		RandVector[] input = BenchFunctions.getGeneratedInput(n, dims, type, false);

		// for histogram
		// EBNL, ESFS, TKLS, SequentialTopK
		/*
		 * ArrayList<ArrayList<ArrayList<Object>>> algResults =
		 * BenchFunctions.initResult(4, k.length, iterations); //ParallelTopK
		 * ArrayList<ArrayList<ArrayList<Object>>> phaseResults =
		 * BenchFunctions.initResult(5, k.length, iterations); //No TkLS if
		 * "house"-data is used (too many dimensions)
		 * if(type.equalsIgnoreCase("house")){ algResults =
		 * BenchFunctions.initResult(3, k.length, iterations); }
		 */
		// for errorbars
		ArrayList<ArrayList<ArrayList<Object>>> algResults;
		// tkls doesn't work for house-data (too many dimensions)
		if (type.equalsIgnoreCase("house.txt")) {
			algResults = BenchFunctions.initResult(3, k.length, iterations);
		}
		// ebnl and esfs are too slow for zillow-data (too many tuples)
		else if (type.equalsIgnoreCase("zillow.txt")) {
			algResults = BenchFunctions.initResult(1, k.length, iterations);
		} else {
			algResults = BenchFunctions.initResult(3, k.length, iterations);
		}

		// get runtime of every algorithm for every iteration
		for (int i = 0; i < iterations; i++) {
			System.out.println("\nIteration " + i + ": ");
			System.gc();
			for (int j = 0; j < k.length; j++) {

				System.out.println("K: " + k[j]);

				// if type == "house": no TkLS!
				if (type.equalsIgnoreCase("house.txt")) {
					algResults.get(0).get(j).set(i,
							(double) AlgorithmTest.runEBNLTopK(new ArrayList<Object>(Arrays.asList(input)), k[j]));
					System.gc();
					algResults.get(1).get(j).set(i,
							(double) AlgorithmTest.runESFSTopK(new ArrayList<Object>(Arrays.asList(input)), k[j]));
					System.gc();
//					algResults.get(2).get(j).set(i, (double) AlgorithmTest
//							.runSequentialTopK(new ArrayList<Object>(Arrays.asList(input)), k[j]));
//					System.gc();
					// for errorbars
					algResults.get(2).get(j).set(i, (double) AlgorithmTest
							.runParallelTopK(new ArrayList<Object>(Arrays.asList(input)), k[j], b, mode, c).get(0));
					System.gc();
					// if type == "zillow": no TkLS!
				} else if (type.equalsIgnoreCase("zillow.txt")) {
					// algResults.get(0).get(j).set(i, (double) AlgorithmTest
					// .runSequentialTopK(new
					// ArrayList<Object>(Arrays.asList(input)), k[j]));
					// System.gc();
					algResults.get(0).get(j).set(i, (double) AlgorithmTest
							.runParallelTopK(new ArrayList<Object>(Arrays.asList(input)), k[j], b, mode, c).get(0));
					System.gc();
				} else {
					algResults.get(0).get(j).set(i,
							(double) AlgorithmTest.runEBNLTopK(new ArrayList<Object>(Arrays.asList(input)), k[j]));
					System.gc();
					algResults.get(1).get(j).set(i,
							(double) AlgorithmTest.runESFSTopK(new ArrayList<Object>(Arrays.asList(input)), k[j]));
					System.gc();
					// algResults.get(2).get(j).set(i, (double) AlgorithmTest
					// .runSequentialTopK(new
					// ArrayList<Object>(Arrays.asList(input)), k[j]));
					// System.gc();
					// for errorbars
					algResults.get(2).get(j).set(i, (double) AlgorithmTest
							.runParallelTopK(new ArrayList<Object>(Arrays.asList(input)), k[j], b, mode, c).get(0));
					System.gc();
					// algResults.get(4).get(j).set(i,
					// (double) AlgorithmTest.runTkLS(input.getElements(), k[j],
					// dimensions));
					// System.gc();
				}

				// for histograms
				/*
				 * ArrayList<Object> times =
				 * AlgorithmTest.runParallelTopK(input.getElements(), k[j], b,
				 * mode, c); for(int q = 0; q < times.size(); q++){
				 * phaseResults.get(q).get(j).set(i, times.get(q)); }
				 * System.gc();
				 */
			}
		}

		// for histograms
		// removing best and worst result
		/*
		 * algResults = BenchFunctions.removeBestAndWorst(algResults);
		 * phaseResults = BenchFunctions.removeBestAndWorst(phaseResults); //add
		 * runtimes for every iteration ArrayList<ArrayList<Object>> result =
		 * BenchFunctions.addAlgResults(algResults);
		 * ArrayList<ArrayList<Object>> parallelTimes =
		 * BenchFunctions.addAlgResults(phaseResults); //get average runtimes
		 * result = BenchFunctions.avgRuntime(result, iterations-2);
		 * parallelTimes = BenchFunctions.avgRuntime(parallelTimes,
		 * iterations-2);
		 */

		// for errorbars
		// get resulting times in format for errorbars, ignoring best and worst
		// result of every algorithm for every dataSize
		ArrayList<ArrayList<ArrayList<Object>>> result = BenchFunctions.getResultForErrorbars(algResults, k);

		// write results into datFile, write pltFile and plot diagram
		String[] clusterLabels;
		if (type.equalsIgnoreCase("house.txt")) {
			clusterLabels = new String[] { "ebnl", "esfs"/*, "sequential"*/, "parallel" };
		} else if (type.equalsIgnoreCase("zillow.txt")) {
			clusterLabels = new String[] { /* "tkls", "sequential", */ "parallel" };
		} else {
			clusterLabels = new String[] { "ebnl", "esfs"/*, "sequential"*/,
					"parallel" /* , "tkls" */ };
		}
		String[] blockLabels = new String[] { "10",	"100", "500", "719", "800", "921", "1000", "1132", "1140", "1150", "1155", "1164", "1170" };
		String[] xylabels = new String[] { "TopK", "Runntime(s)" };
		String[] title = new String[] { "K", "-" + type + "-n" + n };
		// BenchWrite.writeToPartlyRowstacked(parallelTimes, result,
		// clusterLabels, blockLabels, xylabels, title);
		BenchWrite.writeValsToErrorbar(result, "/home/wohlfast/Tests/", clusterLabels, blockLabels, xylabels, title,
				true);
		System.out.println("--ENDE--");
	}

	/**
	 * Creates errorbar-diagram displaying the average runtime of ebnl, esfs,
	 * tkls, sequentialTopK and parallelTopK for different (small) dimensions
	 * 
	 * @param type
	 *            - data-type that should be used for input
	 * @param iter
	 *            - number of iterations
	 * @param m
	 *            - mode for parallelTopK (1, 2, 3 or 4)
	 */
	public static void benchmarkDSmall(String type, int iter, int m) {

		int iterations = iter + 2;
		int mode = m;
		int n = 10_000_000;
		int k = 10;
		int c = 16;
		int b = 1000;

		// Options for dimensions
		ArrayList<Object> dims = new ArrayList<Object>();
		dims.add(2);
		dims.add(3);
		dims.add(5);

		// Data for every set of dimensions
		ArrayList<RandVector[]> input = new ArrayList<>();
		for (int i = 0; i < dims.size(); i++) {
			input.add(AlgorithmTest.generateInput(type, n, (int) dims.get(i), false));
		}

		ArrayList<ArrayList<ArrayList<Object>>> algResults = BenchFunctions.initResult(3, dims.size(), iterations);

		// get times for every iteration
		for (int i = 0; i < iterations; i++) {
			System.out.println("\nIteration " + i + ": ");
			System.gc();
			for (int j = 0; j < input.size(); j++) {
				System.out.println("Dimensions: " + (dims.get(j)));

				algResults.get(0).get(j).set(i,
						(double) AlgorithmTest.runEBNLTopK(new ArrayList<Object>(Arrays.asList(input.get(j))), k));
				System.gc();
				algResults.get(1).get(j).set(i,
						(double) AlgorithmTest.runESFSTopK(new ArrayList<Object>(Arrays.asList(input.get(j))), k));
				System.gc();
				// algResults.get(2).get(j).set(i, (double) AlgorithmTest
				// .runSequentialTopK(new
				// ArrayList<Object>(Arrays.asList(input.get(j))), k));
				// System.gc();
				algResults.get(2).get(j).set(i, (double) AlgorithmTest
						.runParallelTopK(new ArrayList<Object>(Arrays.asList(input.get(j))), k, b, mode, c).get(0));
				System.gc();
				// algResults.get(4).get(j).set(i,
				// (double) AlgorithmTest.runTkLS(new
				// ArrayList<Object>(Arrays.asList(input.get(j))), k, (int[])
				// dims.get(j)));
				// System.gc();
			}
		}

		// first value of every column
		int x[] = new int[] { 2, 3, 5 };
		// get resulting times in format for errorbars, ignoring best and worst
		// result of every algorithm for every dimension
		ArrayList<ArrayList<ArrayList<Object>>> result = BenchFunctions.getResultForErrorbars(algResults, x);

		// write results into datFile, write pltFile and plot diagram
		String[] clusterLabels = new String[] { "ebnl", "esfs", /* "sequential", */
				"parallel"/* , "tkls" */ };
		String[] blockLabels = new String[] { "2", "3", "5" };
		String[] xylabels = new String[] { "Dimensions", "Runntime(s)" };
		String[] title = new String[] { "D-small", "-" + type + "-n" + n + "-k" + k };

		BenchWrite.writeValsToErrorbar(result, "/home/wohlfast/Tests/", clusterLabels, blockLabels, xylabels, title,
				false);
		System.out.println("--ENDE--");
	}

	/**
	 * Creates partly rowstacked clustered diagram displaying the average
	 * runtime of ebnl, esfs, sequentiallTopK and parallelTopK for different
	 * (large) dimensions
	 * 
	 * @param type
	 *            data-type that should be used for input
	 * @param iter
	 *            number of iterations
	 * @param m
	 *            - mode for parallelTopK (1, 2, 3 or 4)
	 */
	public static void benchmarkDBig(String type, int iter, int m) {
		int iterations = iter + 2;
		int mode = m;
		int n = 1_000_000;
		int[] k = new int[] { 10, 15, 50, 100, 500, 1000, 1999, 4000, 14653, 30000, 50000, 106986, 200000 };
		int c = 16;
		int b = 10000;

		// Options for dimensions
		ArrayList<Object> dims = new ArrayList<Object>();
		dims.add(2);
		dims.add(3);
		dims.add(5);
		dims.add(7);
		dims.add(10);

		// generate input-tuples for different dimensions
		ArrayList<RandVector[]> input = new ArrayList<>();
		for (int i = 0; i < dims.size(); i++) {
			type = "ind-n1000000-d" + dims.get(i);
			input.add(AlgorithmTest.generateInput(type, n, (int) dims.get(i), false));
		}

		for (int index = 0; index < k.length; index++) {

			// for errorbars
			ArrayList<ArrayList<ArrayList<Object>>> algResults = BenchFunctions.initResult(3, dims.size(), iterations);

			// get times for every iteration
			for (int i = 0; i < iterations; i++) {
				System.out.println("\nIteration " + i + ": ");
				System.gc();
				for (int j = 0; j < input.size(); j++) {
					System.out.println("Dimensions: " + (dims.get(j)));

					algResults.get(0).get(j).set(i, (double) AlgorithmTest
							.runEBNLTopK(new ArrayList<Object>(Arrays.asList(input.get(j))), k[index]));
					System.gc();
					algResults.get(1).get(j).set(i, (double) AlgorithmTest
							.runESFSTopK(new ArrayList<Object>(Arrays.asList(input.get(j))), k[index]));
					System.gc();
					// algResults.get(2).get(j).set(i, (double) AlgorithmTest
					// .runSequentialTopK(new
					// ArrayList<Object>(Arrays.asList(input.get(j))), k));
					// System.gc();
					algResults.get(2).get(j).set(i, (double) AlgorithmTest
							.runParallelTopK(new ArrayList<Object>(Arrays.asList(input.get(j))), k[index], b, mode, c)
							.get(0));
					System.gc();
				}
			}

			int x[] = new int[] { 2, 3, 5, 7, 10 /* , 15 */ };
			// get resulting times in format for errorbars, ignoring best and
			// worst
			// result of every algorithm for every dataSize
			ArrayList<ArrayList<ArrayList<Object>>> result = BenchFunctions.getResultForErrorbars(algResults, x);

			// write results into datFile, write pltFile and plot diagram
			String[] clusterLabels = new String[] { "ebnl", "esfs", /* "sequential", */ "parallel" };
			String[] blockLabels = new String[] { "2", "3", "5", "7", "10"/* , "15" */ };
			String[] xylabels = new String[] { "Dimensions", "Runntime(s)" };
			String[] title = new String[] { "D-big", "-" + type + "-n" + n + "-k" + k[index] };

			BenchWrite.writeValsToErrorbar(result, "/home/wohlfast/Tests/", clusterLabels, blockLabels, xylabels, title,
					false);

		}
		System.out.println("--ENDE--");
	}

	/**
	 * Creates rowstacked diagram displaying the average runtime of parallelTopK
	 * for different numbers of threads
	 * 
	 * @param type
	 *            - data-type that should be used for input
	 * @param iter
	 *            - number of iterations
	 * @param m
	 *            - mode for parallelTopK (1, 2, 3 or 4)
	 */
	public static void benchmarkC(String type, int iter, int m) {

		int iterations = iter + 2;
		int mode = m;
		int b = 10000;
		int[] k = new int[] { 10, 100, 500, 719, 800, 921, 1000, 1132, 1140, 1150, 1155, 1164, 1170 };
		int n = 100_000;
		int dims = 5;

		// Options for number of threads
		int c[] = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512 };

		// generate input-tuples
		RandVector[] input = BenchFunctions.getGeneratedInput(n, dims, type, false);

		for (int index = 0; index < k.length; index++) {
			// initialize results
			ArrayList<ArrayList<ArrayList<Object>>> algResults = BenchFunctions.initResult(5, c.length, iterations);

			// get times for every iteration
			for (int i = 0; i < iterations; i++) {
				System.out.println("\nIteration " + i + ": ");
				System.gc();
				for (int j = 0; j < c.length; j++) {
					System.out.println("Threads: " + c[j]);

					ArrayList<Object> times = AlgorithmTest.runParallelTopK(new ArrayList<Object>(Arrays.asList(input)),
							k[index], b, mode, c[j]);
					for (int q = 0; q < times.size(); q++) {
						algResults.get(q).get(j).set(i, (double) times.get(q));
					}
					System.gc();
				}
			}

			// get average runtime for every number of threads
			algResults = BenchFunctions.removeBestAndWorst(algResults);
			ArrayList<ArrayList<Object>> result = BenchFunctions.addAlgResults(algResults);
			result = BenchFunctions.avgRuntime(result, iterations - 2);

			// write results into datFile, write pltFile and plot diagram
			BenchWrite.writeValsToRowstacked(result, "/home/wohlfast/Tests/", new String[] { "Threads" },
					new String[] { "Threads", "Runtime(s)" },
					new String[] { "1", "2", "4", "8", "16", "32", "64", "128", "256", "512" },
					new String[] { "Threads", "-" + type + "-n" + n + "-k" + k[index] });

		}
		System.out.println("--ENDE--");
	}

	/**
	 * Creates partly rowstacked clustered diagram displaying the average
	 * runtime of ebnl, esfs, tkls, sequentialTopK and parallelTopK for
	 * different maxLevel
	 * 
	 * @param type
	 *            - data-type that should be used for input
	 * @param iter
	 *            - number of iterations
	 * @param m
	 *            - mode for parallelTopK (1, 2, 3 or 4)
	 */
	/*
	 * public static void benchmarkMaxLevel(String type, int iter, int m) {
	 * 
	 * int iterations = iter + 2; int mode = m; int n = 100_00; int k = 10; int
	 * c = 4; int b = 1000;
	 * 
	 * // Options for dimensions ArrayList<Object> dims = new
	 * ArrayList<Object>(); dims.add(new int[] { 2, 2, 2 }); dims.add(new int[]
	 * { 5, 5, 5 }); dims.add(new int[] { 10, 10, 10 }); dims.add(new int[] {
	 * 50, 50, 50 }); dims.add(new int[] { 100, 100, 100 });
	 * 
	 * // Data for every set of dimensions ArrayList<FlatLCResultSetA> input =
	 * new ArrayList<FlatLCResultSetA>(); for (int i = 0; i < dims.size(); i++)
	 * { input.add(AlgorithmTest.generateInput(type, n, (int[]) dims.get(i))); }
	 * 
	 * // initialze results (with (long)0)
	 * ArrayList<ArrayList<ArrayList<Object>>> algResults =
	 * BenchFunctions.initResult(5, dims.size(), iterations);
	 * 
	 * // get times for every iteration for (int i = 0; i < iterations; i++) {
	 * System.out.println("\nIteration " + i + ": "); System.gc(); for (int j =
	 * 0; j < input.size(); j++) { System.out.println("\nmaxLevel: " + ((int[])
	 * dims.get(j))[0]);
	 * 
	 * // algResults.get(0).get(j).set(i, (long)
	 * AlgorithmTest.runEBNLTopK(input.get(j).getElements(), k)); //
	 * algResults.get(1).get(j).set(i, (long)
	 * AlgorithmTest.runESFSTopK(input.get(j).getElements(), k)); //
	 * algResults.get(2).get(j).set(i, // (long)
	 * AlgorithmTest.runTkLS(input.get(j).getElements(), k, (int[])
	 * dims.get(j))); // algResults.get(3).get(j).set(i, (long)
	 * AlgorithmTest.runSequentialTopK(input.get(j).getElements(), k));
	 * 
	 * algResults.get(4).get(j).set(i, (long)
	 * AlgorithmTest.runParallelTopK(input.get(j).getElements(), k, b, mode,
	 * c).get(0)); System.gc(); } }
	 * 
	 * // first value of every column int x[] = new int[] { 2, 5, 10, 50, 100 };
	 * // int x[] = new int[]{50, 100, 500, 1000, 5000};
	 * 
	 * // get resulting times in format for errorbars, ignoring best and worst
	 * // result of every algorithm for every maxLevel
	 * ArrayList<ArrayList<ArrayList<Object>>> result =
	 * BenchFunctions.getResultForErrorbars(algResults, x);
	 * 
	 * // write results into datFile, write pltFile and plot diagram String[]
	 * clusterLabels = new String[] { "ebnl", "esfs", "tkls", "sequential",
	 * "parallel" }; String[] blockLabels = new String[] { "2", "5", "10", "50",
	 * "100" }; // String[] blockLabels = new String[]{"50", "100", "500",
	 * "1000", // "5000"}; String[] xylabels = new String[] { "MaxLevel",
	 * "Runntime(nanoseconds)" }; String[] title = new String[] { "MaxLevel",
	 * "-" + type }; BenchWrite.writeValsToErrorbar(result,
	 * "/home/wohlfast/Tests/", clusterLabels, blockLabels, xylabels, title,
	 * true); System.out.println("--ENDE--"); }
	 */

	/**
	 * Creates partly rowstacked clustered diagram displaying the average
	 * runtime of ebnl, esfs, sequentialTopK and parallelTopK for different
	 * large maxLevel
	 * 
	 * @param type
	 *            - data-type that should be used for input
	 * @param iter
	 *            - number of iterations
	 * @param m
	 *            - mode for parallelTopK (1, 2, 3 or 4)
	 */
	/*
	 * public static void benchmarkMaxLevelBig(String type, int iter, int m) {
	 * 
	 * int iterations = iter + 2; int mode = m; int n = 100_00; int k = 10; int
	 * c = 4; int b = 1000;
	 * 
	 * // Options for dimensions ArrayList<Object> dims = new
	 * ArrayList<Object>();
	 * 
	 * dims.add(new int[] { 100, 100, 100 }); dims.add(new int[] { 500, 500, 500
	 * }); dims.add(new int[] { 1000, 1000, 1000 }); dims.add(new int[] { 5000,
	 * 5000, 5000 }); dims.add(new int[] { 10000, 10000, 10000 });
	 * 
	 * // Data for every set of dimensions ArrayList<FlatLCResultSetA> input =
	 * new ArrayList<FlatLCResultSetA>(); for (int i = 0; i < dims.size(); i++)
	 * { input.add(AlgorithmTest.generateInput(type, n, (int[]) dims.get(i))); }
	 * 
	 * // initialze results (with (long)0)
	 * ArrayList<ArrayList<ArrayList<Object>>> algResults =
	 * BenchFunctions.initResult(4, dims.size(), iterations);
	 * 
	 * // get times for every iteration for (int i = 0; i < iterations; i++) {
	 * System.out.println("\nIteration " + i + ": "); System.gc(); for (int j =
	 * 0; j < input.size(); j++) { System.out.println("\nmaxLevel: " + ((int[])
	 * dims.get(j))[0]);
	 * 
	 * // algResults.get(0).get(j).set(i, (long)
	 * AlgorithmTest.runEBNLTopK(input.get(j).getElements(), k)); //
	 * algResults.get(1).get(j).set(i, (long)
	 * AlgorithmTest.runESFSTopK(input.get(j).getElements(), k)); //
	 * algResults.get(2).get(j).set(i, (long)
	 * AlgorithmTest.runSequentialTopK(input.get(j).getElements(), k));
	 * 
	 * algResults.get(3).get(j).set(i, (long)
	 * AlgorithmTest.runParallelTopK(input.get(j).getElements(), k, b, mode,
	 * c).get(0)); System.gc(); } }
	 * 
	 * // first value of every column int x[] = new int[] { 100, 500, 1000,
	 * 5000, 10000 };
	 * 
	 * // get resulting times in format for errorbars, ignoring best and worst
	 * // result of every algorithm for every maxLevel
	 * ArrayList<ArrayList<ArrayList<Object>>> result =
	 * BenchFunctions.getResultForErrorbars(algResults, x);
	 * 
	 * // write results into datFile, write pltFile and plot diagram String[]
	 * clusterLabels = new String[] { "ebnl", "esfs", "sequential", "parallel"
	 * }; String[] blockLabels = new String[] { "100", "500", "1000", "5000",
	 * "10000" }; String[] xylabels = new String[] { "MaxLevel",
	 * "Runntime(nanoseconds)" }; String[] title = new String[] { "BigLevel",
	 * "-" + type }; BenchWrite.writeValsToErrorbar(result,
	 * "/home/wohlfast/Tests/", clusterLabels, blockLabels, xylabels, title,
	 * true); System.out.println("--ENDE--"); }
	 */

	/**
	 * Creates rowstacked diagram displaying the average runtime of the 4 modes
	 * of parallelTopK
	 * 
	 * @param type
	 *            data-type that should be used for input
	 * @param iter
	 *            number of iterations
	 */
	/*
	 * public static void benchmarkMode(String type, int iter){
	 * 
	 * int iterations = iter+2; int c = 16; int b = 1000; int k = 10; int n =
	 * 1000000; int dims[] = new int[]{10, 10, 10}; FlatLCResultSetA input =
	 * BenchFunctions.getGeneratedInput(n, dims, type);
	 * 
	 * //initialize result with dimensions 4xinput.size()x5 (4 modes, 4 phases
	 * and total runtime) ArrayList<ArrayList<Object>> result;
	 * ArrayList<ArrayList<ArrayList<Object>>> algResults =
	 * BenchFunctions.initResult(5, 4, iterations);
	 * 
	 * //get times for every iteration for(int i = 0; i < iterations; i++){
	 * System.out.println("\nIteration " + i + ": "); System.gc(); for(int j =
	 * 0; j < 4; j++){ System.out.println("Mode " + (j+1));
	 * 
	 * ArrayList<Object> times =
	 * AlgorithmTest.runParallelTopK(input.getElements(), k, b, (j+1), c);
	 * for(int q = 0; q < times.size(); q++){ algResults.get(q).get(j).set(i,
	 * (long)times.get(q)); } System.gc(); } }
	 * 
	 * //get average runtime for every mode algResults =
	 * BenchFunctions.removeBestAndWorst(algResults); result =
	 * BenchFunctions.addAlgResults(algResults); result =
	 * BenchFunctions.avgRuntime(result, iterations-2);
	 * 
	 * //write results into datFile, write pltFile and plot diagram
	 * BenchWrite.writeValsToRowstacked(result, "C:/Tests/", new
	 * String[]{"Mode"}, new String[]{"Mode", "Runtime(nanosec)"}, new
	 * String[]{"mode1", "mode2", "mode3", "mode4"}, new String[]{"Mode",
	 * "-"+type}); }
	 */

	/**
	 * Computes diagram that compares runtimes of ebnl, esfs and parallelTopK
	 * for nba-data
	 * 
	 * @param iter
	 *            - number of iterations
	 */
	/*
	 * public static void benchNBA(int iter){ int iterations = iter + 2; int c =
	 * 16; int b = 1000; int k = 10;
	 * 
	 * FlatLCResultSetA input = AlgorithmTest.generateInput("nba.txt", 17265,
	 * new int[]{10,10,10,10,10}); ArrayList<ArrayList<Object>> result =
	 * BenchFunctions.initResult(3, iterations);
	 * 
	 * for(int i = 0; i < iterations; i++){ System.out.println("Iteration " +
	 * i); System.gc(); result.get(0).set(i,
	 * AlgorithmTest.runEBNLTopK(input.getElements(), k)); result.get(1).set(i,
	 * AlgorithmTest.runESFSTopK(input.getElements(), k)); result.get(2).set(i,
	 * AlgorithmTest.runParallelTopK(input.getElements(), k, b, 1, c).get(0)); }
	 * 
	 * //remove best and worst iteration for every algorithm for(int i = 0; i <
	 * result.size(); i++){ long min = (long)result.get(i).get(0); long max =
	 * (long)result.get(i).get(0); for(idouble = 1; j < iterations; j++){
	 * if((long)result.get(i).get(j) < min){ min = (long)result.get(i).get(j); }
	 * if((long)result.get(i).get(j) > max){ max = (long)result.get(i).get(j); }
	 * } result.get(i).remove(min); result.get(i).remove(max); }
	 * 
	 * //get average runtime ArrayList<Object> avgResult = new
	 * ArrayList<Object>(); for(int i = 0; i < result.size(); i++){
	 * avgResult.add((long)0); for(int j = 0; j < result.get(i).size();j++){
	 * avgResult.set(i, (long)avgResult.get(i) + (long)result.get(i).get(j)); }
	 * avgResult.set(i, (long)avgResult.get(i) / result.get(i).size()); }
	 * BenchWrite.writeValsToBox(avgResult, "C:/Tests/", new String[]{"EBNL",
	 * "ESFS", "Parallel"}, new String[]{"Algorithm", "Runntime"}, new
	 * String[]{"CompareNBA"}); }
	 */

	/**
	 * Computes diagram that compares runtimes of ebnl, esfs and parallelTopK
	 * for zillow-data
	 * 
	 * @param iter
	 *            - number of iterations
	 */
	/*
	 * public static void benchZillow(int iter){ int iterations = iter + 2; int
	 * c = 16; int b = 1000; int k = 10; int maxLevels[] = new int[]
	 * {10,10,36,45};
	 * 
	 * FlatLCResultSetA input = AlgorithmTest.generateInput("zillow.txt",
	 * 100000, new int[]{10,10,36,45}); int size = (input.getElements()).size();
	 * System.out.println("Size: " + size);
	 * 
	 * ArrayList<ArrayList<Object>> result = BenchFunctions.initResult(5,
	 * iterations);
	 * 
	 * for(int i = 0; i < iterations; i++){ System.out.println("Iteration " +
	 * i); System.gc(); result.get(0).set(i,
	 * AlgorithmTest.runEBNLTopK(input.getElements(), k)); result.get(1).set(i,
	 * AlgorithmTest.runESFSTopK(input.getElements(), k)); result.get(2).set(i,
	 * AlgorithmTest.runTkLS(input.getElements(), k, maxLevels));
	 * result.get(3).set(i, AlgorithmTest.runSequentialTopK(input.getElements(),
	 * k)); result.get(4).set(i,
	 * AlgorithmTest.runParallelTopK(input.getElements(), k, b, 1, c).get(0)); }
	 * 
	 * //remove best and worst iteration for every algorithm for(int i = 0; i <
	 * result.size(); i++){ long min = (long)result.get(i).get(0); long max =
	 * (long)result.get(i).get(0); for(int j = 1; j < iterations; j++){
	 * if((long)result.get(i).get(j) < min){ min = (long)result.get(i).get(j); }
	 * if((long)result.get(i).get(j) > max){ max = (long)result.get(i).get(j); }
	 * } result.get(i).remove(min); result.get(i).remove(max); }
	 * 
	 * //get average runtime ArrayList<Object> avgResult = new
	 * ArrayList<Object>(); for(int i = 0; i < result.size(); i++){
	 * avgResult.add((long)0); for(int j = 0; j < result.get(i).size();j++){
	 * avgResult.set(i, (long)avgResult.get(i) + (long)result.get(i).get(j)); }
	 * avgResult.set(i, (long)avgResult.get(i) / result.get(i).size()); }
	 * BenchWrite.writeValsToBox(avgResult, "C:/Tests/", new String[]{"EBNL",
	 * "ESFS", "TkLS", "Sequential", "Parallel"}, new String[]{"Algorithm",
	 * "Runntime"}, new String[]{"CompareZillow"}); }
	 */

	/**
	 * To show number of comparisons for parallelTopK, ebnl and esfs Counter and
	 * print-command are currently commented out so that they won't affect the
	 * runtime when testing!
	 */
	/*
	 * public static void compareCounter(){ FlatLCResultSetA input =
	 * AlgorithmTest.generateInput("ind", 100, new int[]{10,10,36,45}); for(int
	 * i = 0; i < 1; i++){ System.out.println("\nIteration " + i + ": ");
	 * System.out.println("Parallel - Mode 1: ");
	 * AlgorithmTest.runParallelTopK(input.getElements(), 10, 1000, 1, 16);
	 * System.out.println("Parallel - Mode 3: ");
	 * AlgorithmTest.runParallelTopK(input.getElements(), 10, 1000, 3, 16);
	 * System.out.println("EBNL:");
	 * AlgorithmTest.runEBNLTopK(input.getElements(), 10);
	 * System.out.println("ESFS: ");
	 * AlgorithmTest.runESFSTopK(input.getElements(), 10); } }
	 */
}
