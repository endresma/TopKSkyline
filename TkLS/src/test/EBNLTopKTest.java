package test;

import java.util.ArrayList;

import org.junit.Test;

import topk.EBNLTopK;

public class EBNLTopKTest extends TopKTest{
	
	public EBNLTopKTest(int topK, ArrayList<Object> input, ArrayList<ArrayList<Object>> S_ml) {
		super(topK, input, S_ml);
	}
	

	@Test
	public void testResult() {
		EBNLTopK ebnl = new EBNLTopK(input, topK);
		ArrayList<Object> result = ebnl.getResult();
		assertResultEqual(result);
	}
}
