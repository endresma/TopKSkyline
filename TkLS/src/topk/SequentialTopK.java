package topk;

import java.util.ArrayList;

import dataGenerator.RandVector;

public class SequentialTopK extends AbstractTopK {

	protected boolean isSkylinePoint;

	public SequentialTopK(ArrayList<Object> D, int k) {

		// sort D, initialize S_ml
		super(D, k);

		// Maximum index of known Skyline stratum
		int stratum = 0;
		// for all p in D do
		outerLoop: for (Object p : D) {

			// System.out.println("Durchlauf: "+ (dl++));

			// for i = 0 to stratum do
			// < or <= ?
			outerFor: for (int i = 0; i <= stratum; i++) {

				isSkylinePoint = true;
				// for all q in s_ml[i] do
				innerFor: for (Object q : S_ml.get(i)) {

					// if q <_pareto p then
					// only true if q dominates p, not if they are equal,
					// substitutable or unranked!
					if (((RandVector) q).compare((RandVector) p) == 1) {
						// p is dominated by any q in S_ml[i]
						isSkylinePoint = false;
						// break inner 'for all' loop
						break innerFor;
					}
				}

				// if isSkylinePoint then
				if (isSkylinePoint) {
					S_ml.get(i).add(p);
					// break outer 'for all' loop
					break outerFor;
				}
			}

			// if !isSkylinePoint and stratum < k-1 then
			if (!isSkylinePoint && stratum < k - 1) {
				stratum++;
				S_ml.get(stratum).add(p);
			}
			
			if (S_ml.get(0).size() >= k)
				break outerLoop;
		}
		

		topKResults();
		// printResult();
	}

	public void printResult() {
		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i).toString());
		}
	}

}
