package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dataGenerator.RandVector;
import topk.TopSort;

@RunWith(value = Parameterized.class)
public class TopSortTest {
	
	private ArrayList<Object> actual;
	private ArrayList<Object> expected;
	
	public TopSortTest(ArrayList<Object> actual, ArrayList<Object> expected) {
		this.actual = actual;
		this.expected = expected;
	}
	
	@Parameters(name = "{index}: testSort({0}) = {1}")
	public static Collection<Object[]> data(){
		
		List<Object[]> input = new ArrayList<>();
		
		/*Entropy: [0.25127311367437281, 0.3419881690481886, 0.3148779915358124, 0.1774787825423224]*/
		input.add(new Object[]{new ArrayList<>(Arrays.asList(new RandVector(new double[]{0.45,0.23}),
				new RandVector(new double[]{0.98,0.11}), new RandVector(new double[]{0.16,0.78}), new RandVector(new double[]{0.14,0.32}))),
				
				new ArrayList<>(Arrays.asList(new RandVector(new double[]{0.14,0.32}), new RandVector(new double[]{0.45,0.23}),
						new RandVector(new double[]{0.16,0.78}), new RandVector(new double[]{0.98,0.11})))
				});
		/*Entropy: [0.25127311367437281, 0.1774787825423224, 0.3148779915358124, 0.1774787825423224]*/
		input.add(new Object[]{new ArrayList<>(Arrays.asList(new RandVector(new double[]{0.45,0.23}),
				new RandVector(new double[]{0.14,0.32}), new RandVector(new double[]{0.16,0.78}), new RandVector(new double[]{0.14,0.32}))),
				
				new ArrayList<>(Arrays.asList(new RandVector(new double[]{0.14,0.32}), new RandVector(new double[]{0.14,0.32}),
						new RandVector(new double[]{0.45,0.23}), new RandVector(new double[]{0.16,0.78})))
				});
		return input;
	}

	@Test
	public void testSort() {
		TopSort.sort(actual);
		Assert.assertArrayEquals(expected.toArray(), actual.toArray());
	}

}
