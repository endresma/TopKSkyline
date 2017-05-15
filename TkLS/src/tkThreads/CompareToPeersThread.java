package tkThreads;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import dataGenerator.RandVector;
import extended.ExtendedRandVector;
//import extendedFLC.Score;
import flatlc.levels.FlatLevelCombination;

public class CompareToPeersThread extends Thread {

	protected int threads;

	protected Object b;
	protected ArrayList<Object> B;
	protected int k;
	protected int mode;

	// for ArrayLists (mode 3 and 4)
	protected ArrayList<Integer> additionalData;
	protected ArrayList<ArrayList<Integer>> dominators;
	protected ArrayList<Integer> level;
	protected int index;
	protected int bIndex;

	// protected Score counter;

	public CompareToPeersThread(Object b, ArrayList<Object> B, int k, int mode, int threads) {
		// public CompareToPeersThread(Object b, ArrayList<Object> B, int k, int
		// mode, int threads, Score counter){
		this.b = b;
		this.B = B;
		this.k = k;
		this.mode = mode;
		// this.pool = pool;
		this.threads = threads;

		// this.counter = counter;
	}

	public CompareToPeersThread(Object b, ArrayList<Object> B, int k, int mode, ArrayList<Integer> additionalData,
			ArrayList<ArrayList<Integer>> dominators, ArrayList<Integer> level, int index, int bIndex, int threads) {
		// public CompareToPeersThread(Object b, ArrayList<Object> B, int k, int
		// mode, ArrayList<Integer> additionalData, ArrayList<Integer>
		// dominators, ArrayList<Integer> level,int index, int bIndex, int
		// threads, Score counter){
		this.b = b;
		this.B = B;
		this.k = k;
		this.mode = mode;
		this.threads = threads;

		this.additionalData = additionalData;
		this.dominators = dominators;
		this.level = level;
		this.index = index;
		this.bIndex = bIndex;

		// this.counter = counter;
	}

	public void run() {
		if (mode == 1 || mode == 2) {
			runA((ExtendedRandVector) b);
		} else if (mode == 3 || mode == 4) {
			runB((RandVector) b);
		}
	}

	// for ExtendedRandVector
	public void runA(ExtendedRandVector a) {
		// if b is not marked as pruned then
		if (a.getAdditionalData() != 2) {

			// no inner parallelization
			if (mode == 1) {
				// for j = 1 to b.index
				for (int j = 0; j < a.getIndex(); j++) {
					// Alternative to calling B.get(j) three times within this
					// loop
					ExtendedRandVector temp = (ExtendedRandVector) B.get(j);
					// if B[j] <_pareto b then
					// only true if B[j] dominates b

					// counter.maxScore++;

					if (temp.getRND().compare(a.getRND()) == 1) {
						// if B[j].level == k-1 then
						if (temp.getLevel() == k - 1) {
							// mark b as pruned
							a.setAdditionalData(2);
							break;
						} else {
							// b.dominators.add(B[j])
							a.addDominator(temp);
						}
					}
				}
			}

			// ExtendedRandVector with inner parallelization
			else if (mode == 2) {
				ExecutorService pool = Executors.newFixedThreadPool(threads);
				for (int i = 0; i < a.getIndex(); i++) {
					pool.execute(new CTPinner(mode, a, B.get(i), k));
					if (a.getAdditionalData() == 2) {
						break;
					}
				}
				pool.shutdown();
				try {
					pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				} catch (InterruptedException e) {
					System.out.println("Error while waiting for termination!");
				}
			}
		}
	}

	// for ArrayLists
	public void runB(RandVector a) {
		// public void runB(ArrayList<Integer> a){
		// if b is not marked as pruned then
		if (additionalData.get(index) != 2) {

			// no inner parallelization
			if (mode == 3) {
				// for j = 1 to b.index
				for (int j = 0; j < bIndex; j++) {
					// if B[j] <_pareto b then
					// only true if B[j] dominates b

					// counter.maxScore++;

					if (((RandVector) B.get(j)).compare(a) == 1) {
						// if(TestDataGenerator.compare((ArrayList<Integer>)B.get(j),
						// a) == 1){
						// if B[j].level == k-1 then
						if (level.get(index - (bIndex - j)) == k - 1) {
							// mark b as pruned
							additionalData.set(index, 2);
							break;
						} else {
							// b.dominators.add(B[j])
								dominators.get(index).add(index - (bIndex - j));
							}
						}
					}
				}
			}

			// inner parallelization
			else if (mode == 4) {
				ExecutorService pool = Executors.newFixedThreadPool(threads);
				for (int j = 0; j < bIndex; j++) {
					pool.execute(new CTPinner(mode, a, (FlatLevelCombination) B.get(j), additionalData, dominators,
							level, index, index - (bIndex - j), k));
					if (additionalData.get(index) == 2) {
						break;
					}
				}

				pool.shutdown();
				try {
					pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				} catch (InterruptedException e) {
					System.out.println("Error while waiting for termination!");
				}
			}
		}
	}

