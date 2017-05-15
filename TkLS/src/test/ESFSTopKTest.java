package test;

import java.util.ArrayList;

import org.junit.Test;

import topk.EBNLTopK;
import topk.ESFSTopK;

public class ESFSTopKTest extends TopKTest{
	
	public ESFSTopKTest(int topK, ArrayList<Object> input, ArrayList<ArrayList<Object>> S_ml){
		super(topK, input, S_ml);
	}
	
	
	
	@Test
	public void testResult() {
		EBNLTopK esfs = new ESFSTopK(input, topK);
		ArrayList<Object> result = esfs.getResult();
		assertResultEqual(result);
	}
}
