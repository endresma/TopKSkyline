package test;

import java.util.ArrayList;

import org.junit.Test;

import topk.SequentialTopK;

public class SequentialTopKTest extends TopKTest{
	
	public SequentialTopKTest(int topK, ArrayList<Object> input, ArrayList<ArrayList<Object>> S_ml) {
		super(topK, input, S_ml);
	}
	
	@Test
	public void testResult() {
		SequentialTopK sequential = new SequentialTopK(input, topK);
		ArrayList<Object> result = sequential.getResult();
		assertResultEqual(result);
	}
}
