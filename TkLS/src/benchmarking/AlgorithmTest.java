/*
 * Copyright (c) 2015. markus endres, timotheus preisinger
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package benchmarking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import btg.BTG;
import btg.BTGDataA;
import btg.BTGDataArray;
import dataGenerator.GenerateAnti;
import dataGenerator.GenerateCorr;
import dataGenerator.GenerateIndep;
import dataGenerator.RandVector;
import realdata.RandomFileDataGenerator;
import topk.EBNLTopK;
import topk.ESFSTopK;
import topk.ParallelTopK;
import topk.SequentialTopK;
import topk.TkLS;
import util.GnuplotExporter;
import util.Stopwatch;

/**
 * User: endresma Date: 17.06.15 Time: 15:24
 */
public class AlgorithmTest {
	
	private static SMLResultTester tester = new SMLResultTester();
	private static String type;
	private static String option;
	private static int k=0;

	public static void main(String[] args) {

		// if (args.length == 0) {
		// System.out.println("Usage: ");
		// System.out.println("java -jar TopKSkyline.jar <dist> <input_sizes>
		// <max_levels> <top_k>");
		// System.out.println("<dist>: anti, corr, ind, gaussian,
		// zillow_data.txt, nba_data.txt, house_data.txt");
		// System.out.println("<input_size>: [n_1,n_2,...,n_m]");
		// System.out.println("<max_levels>: [max_1,max_2,...,max_d]");
		// System.out.println("<top_k>: k");
		//
		// System.exit(0);
		// }

		// Example 1, Figure 6 in the paper
		// Hotel search, low-card domain [3,4,4], input sizes {50,100,15}
		// top-k = 5
		// anti, corr, ind data

		// TODO: House, NBA
		// und weitere Experimente
		// anti, corr, ind, zillow, nba, house, weather (falls vorhanden)
		// verschiedene Dimensionen, verschiedenes k, verschieden Anzahl Objekte

		// anti, corr, ind, gaussian, zillow, nba, house, weather
		// zillow_data.txt, nba_data.txt, house_data.txt

		// <dist>
		// String distribution = "ind";

		// String distribution = "ind";

		// <input_sizes>
		// int top_k = 25000;
		// int input_sizes = 200;
		// input_sizes = input_sizes.substring(1, args[1].length() - 1);
		// StringTokenizer st = new StringTokenizer(input_sizes, ",");
		// int dim_input = st.countTokens();

		// int n[] = new int[]{ 100 };

		// int i = 0;
		// while (st.hasMoreElements()) {
		// n[i++] = Integer.parseInt(st.nextToken());
		// }

		// <max_levels>
		// int maxLevels[] = new int[]{2, 3, 5};
		// String max_levels = args[2];
		// max_levels = max_levels.substring(1, args[2].length() - 1);
		// st = new StringTokenizer(max_levels, ",");
		// int dim_levels = st.countTokens();
		// int maxLevels[] = new int[dim_levels];
		// i = 0;
		// while (st.hasMoreElements()) {
		// maxLevels[i++] = Integer.parseInt(st.nextToken());
		// }

		// <top_k>

		// int top_k = 10;

		// 3d: {2,3,5}
		// 5d: {2,3,5,10,100}
		// 7d: {2,3,5,10,10,10,100}

		/** config for real world data */
		// Zillow
		// int[] n = new int[]{2236252};
		// int maxLevels[] = new int[]{10, 10, 36, 45};

		// NBA
		// int[] n = new int[]{17265};
		// int maxLevels[] = new int[]{10, 10, 10, 10, 10};

		// House
		// int[] n = new int[]{127931};
		// int maxLevels[] = new int[]{10000,10000,10000,10000,10000,10000};
		// int maxLevels[] = new int[]{100,100,100,100,100,100};

		if (args.length < 3) {
			System.out.println("Missing Arguments!");
			System.out.println("\n<benchmark> <datatype> <iterations>");
			System.out.println("<benchmark>: b, n, k, d_small, d_big, c");
			System.out.println("<datatype>: anti, corr, ind, gaussian (, house, nba, zillow)");
			System.out.println("<iterations>: 1,...,10,...,100,...");
			System.out.println("Optional: <mode>: 1, 2, 3, 4 (Default: 1)");
		}
		option = args[0];
		switch (args[0]) {
		case "b":
			System.out.println("\nBlockSize");
			// type of input-data, iterations, (optional: mode)
			if (args.length > 3) {
				BenchTest.benchmarkBlockSize(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			} else {
				BenchTest.benchmarkBlockSize(args[1], Integer.parseInt(args[2]), 1);
			}
			break;
		case "n":
			System.out.println("\nDataSize");
			// type of input-data, iterations
			if (args.length > 3) {
				BenchTest.benchmarkN(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			} else {
				BenchTest.benchmarkN(args[1], Integer.parseInt(args[2]), 1);
			}
			break;
		case "n_big":
			System.out.println("\nBigN");
			// type of input-data, iterations
			if (args.length > 3) {
				BenchTest.benchmarkBigN(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			} else {
				BenchTest.benchmarkBigN(args[1], Integer.parseInt(args[2]), 1);
			}
			break;
		case "k":
			System.out.println("\nTop-k");
			// type of input-data, iterations
			if (args.length > 3) {
				BenchTest.benchmarkK(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			} else {
				BenchTest.benchmarkK(args[1], Integer.parseInt(args[2]), 1);
			}
			break;
		/*
		 * case "maxLevel": System.out.println("\nMaxLevel"); //type of
		 * input-data, iterations if(args.length > 3){
		 * BenchTest.benchmarkMaxLevel(args[1], Integer.parseInt(args[2]),
		 * Integer.parseInt(args[3])); } else{
		 * BenchTest.benchmarkMaxLevel(args[1], Integer.parseInt(args[2]), 1); }
		 * break; case "maxLevel_big": System.out.println("\nMaxLevel - big");
		 * //type of input-data, iterations if(args.length > 3){
		 * BenchTest.benchmarkMaxLevelBig(args[1], Integer.parseInt(args[2]),
		 * Integer.parseInt(args[3])); } else{
		 * BenchTest.benchmarkMaxLevelBig(args[1], Integer.parseInt(args[2]),
		 * 1); } break;
		 */
		case "d_small":
			System.out.println("\nDimensions (small)");
			// type of input-data, iterations
			if (args.length > 3) {
				BenchTest.benchmarkDSmall(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			} else {
				BenchTest.benchmarkDSmall(args[1], Integer.parseInt(args[2]), 1);
			}
			break;
		case "d_big":
			System.out.println("\nDimensions (big)");
			// type of input-data, iterations
			if (args.length > 3) {
				BenchTest.benchmarkDBig(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			} else {
				BenchTest.benchmarkDBig(args[1], Integer.parseInt(args[2]), 1);
			}
			break;
		case "c":
			System.out.println("\nThreads");
			// type of input-data, iterations, mode
			if (args.length > 3) {
				BenchTest.benchmarkC(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			} else {
				BenchTest.benchmarkC(args[1], Integer.parseInt(args[2]), 1);
			}
			break;
		/*
		 * case "m": System.out.println("\nMode"); //type of input-data,
		 * iterations BenchTest.benchmarkMode(args[1],
		 * Integer.parseInt(args[2])); break;
		 */

		default:
			System.out.println("Invalid input!");
			break;
		}
	}


	/**
	 * Sequential TopK algorithm
	 */
	public static double runSequentialTopK(ArrayList<Object> input, int top_k) {
		System.out.println("Run sequential TopK");
		int dims = ((RandVector) input.get(0)).getValues().length;
		
		Stopwatch sw = new Stopwatch();
		SequentialTopK algorithm = new SequentialTopK(input, top_k);
		double runtime = sw.getElapsedSecTime();

		System.out.println("Runtime: " + runtime);
		
//		tester.assertEqualsSML(algorithm.getSML(), top_k, "sequential");
//		tester.assertEqualsResult(algorithm.getResult(), top_k, "sequential");

		return runtime;
	}

	/**
	 * parallel TopK algorithm
	 */
	public static ArrayList<Object> runParallelTopK(ArrayList<Object> input, int top_k, int block_size, int mode,
			int threads) {
		
		System.out.println("Run parallel TopK");
		//int dims = ((RandVector) input.get(0)).getValues().length;
		
		Stopwatch sw = new Stopwatch();
		ParallelTopK algorithm = new ParallelTopK(input, top_k, mode, block_size, threads);
		double runtime = sw.getElapsedSecTime();

		System.out.println("Runtime: " + runtime);

		ArrayList<Object> result = algorithm.getTimes();
		result.add(0, runtime);
		
//		if(k!=top_k || k==0){
//			tester = new SMLResultTester();
//			k=top_k;
//		}
//		tester.assertEqualsSML(algorithm.getSML(), block_size, "parallel");
//		tester.assertEqualsResult(algorithm.getResult(), block_size, "parallel");
//	
		
		// algorithm.printResult();
		// return runtime;
		return result;
	}

	/**
	 * EBNL TopK algorithm
	 */
	public static double runEBNLTopK(ArrayList<Object> input, int top_k) {
		System.out.println("Run EBNL");
		int dims = ((RandVector) input.get(0)).getValues().length;
		Stopwatch sw = new Stopwatch();
		EBNLTopK algorithm = new EBNLTopK(input, top_k);
		// int skylineSize = countResult(algorithm);
		double runtime = sw.getElapsedSecTime();

		System.out.println("Runtime: " + runtime);
		
		//tester.assertEqualsResult(algorithm.getResult(), top_k, "ebnl");
		
		// System.out.println("skylineSize: " + skylineSize);
		return runtime;

	}

	/**
	 * ESFS TopK algorithm
	 */
	public static double runESFSTopK(ArrayList<Object> input, int top_k) {
		System.out.println("Run ESFS");
		int dims = ((RandVector) input.get(0)).getValues().length;
		Stopwatch sw = new Stopwatch();
		ESFSTopK algorithm = new ESFSTopK(input, top_k);
		// int skylineSize = countResult(algorithm);
		double runtime = sw.getElapsedSecTime();

		System.out.println("Runtime: " + runtime);
		
		//tester.assertEqualsResult(algorithm.getResult(), top_k, "esfs");
		
		// System.out.println("skylineSize: " + skylineSize);
		return runtime;
	}

	/**
	 * TkLS TopK algorithm based on Lattice Skyline / Hexagon
	 */
	public static double runTkLS(ArrayList<Object> input, int top_k, int[] maxLevels) {
		System.out.println("Run TkLS");

		Stopwatch sw = new Stopwatch();
		BTGDataA btg = new BTGDataArray(new BTG(maxLevels));
		TkLS algorithm = new TkLS(input.iterator(), btg, top_k);
		int skylineSize = countResult(algorithm);
		double runtime = sw.getElapsedSecTime();

		System.out.println("Runtime: " + runtime);
		return runtime;
	}

	// /**
	// * Execute the algorithm Extended-BNL EBNLTopK.
	// *
	// * @param input
	// * @param pareto
	// * @throws PreferenceException
	// */
	// public static ResultInfo runEBnlTopK(FlatLCResultSetA input,
	// ParetoPreference pareto,
	// int topk) throws PreferenceException {
	//
	//
	// ArrayList<Object> arrayInput = input.getElements();
	//
	// Stopwatch sw = new Stopwatch();
	// EBNLTopK algorithm = new EBNLTopK(arrayInput, pareto, topk);
	//
	// int bmoSize = countResult(algorithm);
	// long runtime = sw.getElapsedNanoSecTime();
	//
	// return new ResultInfo(runtime, bmoSize);
	//
	//
	// }

	// /**
	// * Execute the algorithm Extended-SFS ESFSTopK.
	// *
	// * @param input
	// * @param pareto
	// * @throws PreferenceException
	// */
	// public static ResultInfo runESFSTopK(FlatLCResultSetA input,
	// ParetoPreference pareto,
	// int topk) throws PreferenceException {
	//
	//
	// ArrayList<Object> arrayInput = input.getElements();
	//
	// Stopwatch sw = new Stopwatch();
	// ESFSTopK algorithm = new ESFSTopK(arrayInput, pareto, topk);
	//
	// int bmoSize = countResult(algorithm);
	// long runtime = sw.getElapsedNanoSecTime();
	//
	// return new ResultInfo(runtime, bmoSize);
	//
	//
	// }
	//
	//
	// /**
	// * Execute the algorithm HexagonTopK to evaluate the top-k BMO objects
	// based on
	// * FlatLevelCombinations.
	// *
	// * @param input
	// * @param pareto
	// * @throws PreferenceException
	// */
	// public static ResultInfo runHexagonTopK(FlatLCResultSetA input,
	// ParetoPreference pareto, int topk) throws PreferenceException {
	//
	// Stopwatch sw = new Stopwatch();
	// BTGDataA btg = new BTGDataArray(new BTG(pareto));
	// System.out.println("create BTGData: " + sw.getElapsedMillSecTime());
	//
	// HexagonTopK algorithm = new HexagonTopK(input, pareto, btg, topk);
	//
	// int bmoSize = countResult(algorithm);
	// long runtime = sw.getElapsedNanoSecTime();
	//
	// return new ResultInfo(runtime, bmoSize);
	//
	// }

	/**
	 * Generate input data based on anti, corr, ind, gaussian, zillow, nba,
	 * house, weather
	 */
	public static RandVector[] generateInput(String distribution, int inputSize, int dims, boolean isPadding) {
		System.out.println("\n********************************************************************************");
		System.out.println("Generate data: " + distribution);

		// System.out.println("Generate " + inputSize + " tuples");
		// System.out.println("Generate Input Data Set of size: " +
		// this.getDataSize() + "");

		RandVector[] input = null;
		// generate input in memory, do not write to file

		Integer padding = null;
		if (isPadding)
			padding = new Integer(30);
		
		type = distribution;

		switch (distribution) {
		case "anti":
			input = new GenerateAnti(inputSize, dims, null, padding, null).getRandVector();
			break;
		case "corr":
			input = new GenerateCorr(inputSize, dims, null, padding, null).getRandVector();
			break;
		case "ind":
			input = new GenerateIndep(inputSize, dims, null, padding, null).getRandVector();
			break;
		default:
			// in case of real world data read it from file and generate a
			// FlatLCFileDataGenerator object
			input = new RandomFileDataGenerator(distribution, inputSize, dims).getElements();
		}
	
		return input;

	}

	/**
	 * converts nano seconds to seconds
	 */
	public static double nanoToSeconds(long nano) {
		return nano / 1000. / 1000. / 1000.;
	}

	/**
	 * converts nano seconds to seconds
	 */
	public static double milliToSeconds(double milli) {
		return milli / 1000.;
	}

	/**
	 * count the size of the BMO size. Just iterator through the cursor.
	 *
	 * @param cursor
	 * @return
	 */
	private static int countResult(Iterator cursor) {
		int counter = 0;

		Object o = null;
		while (cursor.hasNext()) {
			o = cursor.next();
			// System.out.println(o.toString());
			counter++;
		}

		return counter;
	}

	/**
	 * test runtimes for different data sets, distributions, etc.
	 */
	public static void runtime_tuples(String[] algorithms, String distribution, int[] inputSize, int dims, int top_k) {

		String xLabel = "Input Size";
		String yLabel = "Runtime (sec)";
		String[] xylabels = { xLabel, yLabel };
		String dirPath = "C:/Tests/";

		StringBuffer toWrite;
		GnuplotExporter gpx = null;

		gpx = new GnuplotExporter(dirPath, "datFile", "pltFile", algorithms, xylabels,
				new String[] { distribution, "Runtime" }, "plt");

		// for all input sizes
		for (int iSize : inputSize) {

			RandVector[] input = generateInput(distribution, iSize, dims, false);

			toWrite = new StringBuffer();
			toWrite.append(iSize);

			/** SequentialTopK */
			ArrayList<Object> arrayInput = new ArrayList<Object>(Arrays.asList(input));
			double runtimeSTopK = runSequentialTopK(arrayInput, top_k);

			toWrite.append('\t');
			toWrite.append(milliToSeconds(runtimeSTopK));

			System.gc();

			/** ParallelTopK */
			// runParallelTopK returns an ArrayList containing complete runtime
			// and times for init and phase 1 - 3
			double runtimePTopK = (double) (runParallelTopK(arrayInput, top_k, 6, 1, 16)).get(0);

			toWrite.append('\t');
			toWrite.append(milliToSeconds(runtimePTopK));

			/** EBNL */
			input = generateInput(distribution, iSize, dims, false);
			arrayInput = new ArrayList<Object>(Arrays.asList(input));
			double runtimeEBENL = runEBNLTopK(arrayInput, top_k);

			toWrite.append('\t');
			toWrite.append(milliToSeconds(runtimeEBENL));

			System.gc();

			/** ESFS */
			input = generateInput(distribution, iSize, dims, false);
			arrayInput = new ArrayList<Object>(Arrays.asList(input));
			double runtimeESFS = runESFSTopK(arrayInput, top_k);

			toWrite.append('\t');
			toWrite.append(milliToSeconds(runtimeESFS));

			System.gc();

			/** TkLS */
			// input = generateInput(distribution, iSize, dims);
			// arrayInput = new ArrayList<Object>(Arrays.asList(input));
			// long runtimeTkLS = runTkLS(arrayInput, top_k, dims);
			//
			// toWrite.append('\t');
			// toWrite.append(nanoToSeconds(runtimeTkLS));
			//
			// System.gc();

			gpx.write(toWrite.toString());
			toWrite = null;

		}

		gpx.close();
		gpx.plot();
		gpx.showRuntimeDiagram();

	}

}
