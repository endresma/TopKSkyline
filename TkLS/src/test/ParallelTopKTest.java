package test;

import java.util.ArrayList;

import org.junit.Test;

import topk.ParallelTopK;

public class ParallelTopKTest extends TopKTest{
	
	private int mode = 1;
	int blockSize = 5;
	int threads = 4;
	
	public ParallelTopKTest(int topK, ArrayList<Object> input, ArrayList<ArrayList<Object>> S_ml) {
		super(topK, input, S_ml);
	}
	
	@Test
	public void testResult() {
		ParallelTopK parallel = new ParallelTopK(input, topK, mode, blockSize, threads);
		ArrayList<Object> result = parallel.getResult();
		assertResultEqual(result);
	}
}
