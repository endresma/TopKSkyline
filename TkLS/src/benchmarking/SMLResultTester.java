package benchmarking;

import java.util.ArrayList;
import java.util.HashMap;

public class SMLResultTester {

	private HashMap<Integer, ArrayList<Object>> ebnl_result = new HashMap<>();

	private HashMap<Integer, ArrayList<Object>> esfs_result = new HashMap<>();

	private HashMap<Integer, ArrayList<ArrayList<Object>>> sequential_S_ml = new HashMap<>();
	private HashMap<Integer, ArrayList<Object>> sequential_result = new HashMap<>();

	private HashMap<Integer, ArrayList<ArrayList<Object>>> parallel_S_ml = new HashMap<>();
	private HashMap<Integer, ArrayList<Object>> parallel_result = new HashMap<>();

	public SMLResultTester() {
	}

	private ArrayList<ArrayList<Object>> getSML(ArrayList<ArrayList<Object>> input, int benchmarkPar, String type) {

		ArrayList<ArrayList<Object>> S_ml = null;

		switch (type) {
		case "sequential":
			if (!sequential_S_ml.containsKey(benchmarkPar))
				sequential_S_ml.put(benchmarkPar, input);
			else
				S_ml = sequential_S_ml.get(benchmarkPar);
			break;
			
		case "parallel":
			if (!parallel_S_ml.containsKey(benchmarkPar))
				parallel_S_ml.put(benchmarkPar, input);
			else
				S_ml = parallel_S_ml.get(benchmarkPar);
			break;
		}

		return S_ml;
	}
	
	private ArrayList<Object> getResult(ArrayList<Object> input, int benchmarkPar, String type) {

		ArrayList<Object> result = null;

		switch (type) {
		case "ebnl":
			if (!ebnl_result.containsKey(benchmarkPar))
				ebnl_result.put(benchmarkPar, input);
			else
				result = ebnl_result.get(benchmarkPar);
			break;
			
		case "esfs":
			if (!esfs_result.containsKey(benchmarkPar))
				esfs_result.put(benchmarkPar, input);
			else
				result = esfs_result.get(benchmarkPar);
			break;
			
		case "sequential":
			if (!sequential_result.containsKey(benchmarkPar))
				sequential_result.put(benchmarkPar, input);
			else
				result = sequential_result.get(benchmarkPar);
			break;
			
		case "parallel":
			if (!parallel_result.containsKey(benchmarkPar))
				parallel_result.put(benchmarkPar, input);
			else
				result = parallel_result.get(benchmarkPar);
			break;
		}
		
		return result;
	}

	/**
	 * Checks if the input is equal to the previous input of the algorithm type.
	 * If not an assertion exception will be thrown.
	 * 
	 * @param input
	 * @param type
	 */
	public void assertEqualsSML(ArrayList<ArrayList<Object>> input, int benchmarkPar, String type) {

		ArrayList<ArrayList<Object>> S_ml = getSML(input, benchmarkPar, type);
		if(S_ml == null)
			return;
		assert (S_ml.size() == input.size()) : "S_ml size is not equal to input size";
		for (int i = 0; i < input.size(); i++) {
			assert (S_ml.get(i).size() == input.get(i).size()) : "S_ml[i] size is not equal to input[i] size";
			assert (S_ml.get(i).containsAll(input.get(i))) : "S_ml[i] doesn't contain all objects of input[i]";
		}
	}

	/**
	 * Checks if the input is equal to the previous input of the algorithm type. If not an assertion
	 * exception will be thrown.
	 * 
	 * @param input
	 * @param type
 */
	public void assertEqualsResult(ArrayList<Object> input, int benchmarkPar, String type) {
		
		ArrayList<Object> result = getResult(input, benchmarkPar, type);
		if(result == null)
			return;
		
		assert (result.size() == input.size()) : "result size is not equal to input size";
		assert (result.containsAll(input)) : "result doesn't contain all object of input";
		assert (input.containsAll(result)) : "input doesn't contain all object of result";
	}
}
