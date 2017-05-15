package topk;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import dataGenerator.RandVector;
import extended.ExtendedRandVector;
import tkThreads.CompareToPeersThread;
import tkThreads.CompareToSmlThread;
import util.Stopwatch;

public class ParallelTopK extends AbstractTopK {

	// additionalData, level and dominators are used if mode is 3 or 4, for mode
	// 1 and 2 those infos are stored in ExtendedRandVectors
	// 0 == NOTDOMINATED, 1 == DOMINATED, 2 == PRUNED
	protected ArrayList<Integer> additionalData;
	protected ArrayList<Integer> level;

	// storing all dominators which dominate object j (where j is the index
	// position)
	protected ArrayList<ArrayList<Integer>> dominators;

	protected int threads;
	protected int blockSize;
	protected int currentIndex;
	// block, containing RandVector(mode 3 or 4) or ExtendedRandVector(mode 1
	// or 2)
	protected ArrayList<Object> B;

	// for messuring runtime of certain phase
	protected double init = 0;
	protected double phase1 = 0;
	protected double phase2 = 0;
	protected double phase3 = 0;

	// 1 == ExtendedRandVector, no inner parallelization
	// 2 == ExtendedRandVector, inner parallelization
	// 3 == ArrayLists, no inner parallelization
	// 4 == ArrayLists, inner parallelization
	protected int mode;

	// public Score counter;

	public ParallelTopK(ArrayList<Object> D, int k, int mode, int blockSize, int threads) {

		// Not included in runtime measurement
		super(D, k);

		Stopwatch sw = new Stopwatch();
		this.blockSize = blockSize;
		this.mode = mode;
		this.threads = threads;
		this.currentIndex = 0;

		init = 0;
		phase1 = 0;
		phase2 = 0;
		phase3 = 0;

		// counter = new Score();
		// counter.maxScore = 0;

		if (mode == 3 || mode == 4) {
			initLists();
		}

		double prior = sw.getElapsedSecTime();
		init = prior;

		// int undominated = 0, pruned = 0, dominators = 0;
		// while D is not empty do
		outerLoop: while (currentIndex < D.size()) {
			// B = next b points from D
			B = new ArrayList<Object>(blockSize);

			// get next block
			for (int i = currentIndex; i < currentIndex + blockSize && i < D.size(); i++) {

				if (mode == 1 || mode == 2) {
					B.add(new ExtendedRandVector((RandVector) D.get(i), i - currentIndex));
				} else if (mode == 3 || mode == 4) {
					B.add(D.get(i));
				}

			}
			
			compareToSml();
			phase1 += sw.getElapsedSecTime() - prior;
			prior = sw.getElapsedSecTime();

			compareToPeers();
			phase2 += sw.getElapsedSecTime() - prior;
			prior = sw.getElapsedSecTime();

			updateGlobalSkylineSml();
			phase3 += sw.getElapsedSecTime() - prior;
			prior = sw.getElapsedSecTime();
			
			if(S_ml.get(0).size() >= k)
				break outerLoop;

			currentIndex = currentIndex + blockSize;
		}

		// return the first k elements from S_ml
		topKResults();

		// System.out.println("Final counter: " + counter.maxScore);
	}

	// Algorithm 3
	protected void compareToSml() {
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		if (mode == 1 || mode == 2) {
			for (int i = 0; i < B.size(); i++) {
				pool.execute(new CompareToSmlThread(B.get(i), S_ml, k, mode, threads));
				// pool.execute(new CompareToSmlThread(B.get(i), S_ml, k, mode,
				// threads, counter));
			}
		} else if (mode == 3 || mode == 4) {
			for (int i = 0; i < B.size(); i++) {
				pool.execute(new CompareToSmlThread(B.get(i), S_ml, k, mode, additionalData, level, currentIndex + i,
						threads));
				// pool.execute(new CompareToSmlThread(B.get(i), S_ml, k, mode,
				// additionalData, level, currentIndex + i, threads, counter));
			}
		}

		pool.shutdown();
		try {
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.out.println("Error while waiting for termination!");
		}
	}

	protected void compareToSmlNotParallel() {
		for (int i = 0; i < B.size(); i++) {
			ExtendedRandVector a = (ExtendedRandVector) B.get(i);
			outerFor: for (int j = 0; j < k; j++) {
				a.setAdditionalData(0);
				innerFor: for (Object q : S_ml.get(j)) {
					if (((RandVector) q).compare(a.getRND()) == 1) {
						a.setAdditionalData(1);
						break innerFor;
					}
				}
				if (!(a.getAdditionalData() == 1)) {
					a.setLevel(j);
					break outerFor;
				}
			}

			if (a.getAdditionalData() == 1) {
				a.setAdditionalData(2);
			}
		}
	}

	// Algorithm 4
	protected void compareToPeers() {
		ExecutorService pool = Executors.newFixedThreadPool(threads);

		if (mode == 1 || mode == 2) {
			for (int i = 0; i < B.size(); i++) {
				pool.execute(new CompareToPeersThread(B.get(i), B, k, mode, threads));
				// pool.execute(new CompareToPeersThread(B.get(i), B, k, mode,
				// threads, counter));
			}
		} else if (mode == 3 || mode == 4) {
			for (int i = 0; i < B.size(); i++) {
				pool.execute(new CompareToPeersThread(B.get(i), B, k, mode, additionalData, dominators, level,
						currentIndex + i, i, threads));
				// pool.execute(new CompareToPeersThread(B.get(i), B, k, mode,
				// additionalData, dominators, level, currentIndex + i, i,
				// threads, counter));
			}
		}

		pool.shutdown();
		try{
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.out.println("Error while waiting for termination!");
		}
	}

	// Algorithm 4
	protected void compareToPeersNotParallel() {

		for (int i = 0; i < B.size(); i++) {
			ExtendedRandVector a = (ExtendedRandVector) B.get(i);
			if (a.getAdditionalData() != 2) {
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
		}
	}

	// Algorithm 5
	protected void updateGlobalSkylineSml() {

		// for i = 0 to blockSize
		for (int i = 0; i < B.size(); i++) {

			// for ExtendedRandVector
			if (mode == 1 || mode == 2) {
				ugsA((ExtendedRandVector) B.get(i));
			}

			// For ArrayLists
			else if (mode == 3 || mode == 4) {
				ugsB(i);
			}
		}			
	}

	// Algorithm 5 for ExtendedRandVector
	protected void ugsA(ExtendedRandVector temp) {

		// if B[i] is not marked as pruned then
		if (temp.getAdditionalData() != 2) {
			// if B[i].dominators.size() != 0 then
			// if(temp.getDominators().size() != 0){
			if (temp.getDominators().size() != 0) {

				// maxlevel = max{q.level | q element of B[i].dominators}
				int maxLevel = temp.getMaxDomLevel();
				// if B[i].level < maxlevel + 1 then
				if (temp.getLevel() < maxLevel + 1) {
					// B[i].level <- maxlevel + 1
					temp.setLevel(maxLevel + 1);
				}
			}
			// if B[i].level < k then
			if (temp.getLevel() < k) {
				// S_ml[B[i].level].add(B[i])
				S_ml.get(temp.getLevel()).add(temp.getRND());

			}
		}
	}

	// Algorithm 5 for or ArrayLists
	protected void ugsB(int i) {
		int j = currentIndex + i;

		// if B[i] is not marked as pruned then
		if (additionalData.get(j) != 2) {
			// if B[i].dominators.size() != 0 then
			if (dominators.get(j).size() != 0) {

				// maxlevel = max{q.level | q element of B[i].dominators}
				int maxLevel = getMaxDomLevel(j);
				// if B[i].level < maxlevel + 1 then
				if (level.get(j) < maxLevel + 1) {
					// B[i].level <- maxlevel + 1
					level.set(j, maxLevel + 1);
				}
			}
			if (level.get(j) < k) {
				S_ml.get(level.get(j)).add(B.get(i));

			}
		}
	}

	protected int getMaxDomLevel(int i) {
		int maxLevel = 0;
		ArrayList<Integer> indexes = dominators.get(i);
		for (int index : indexes) {
			int lvl = level.get(index);
			if (lvl > maxLevel)
				maxLevel = lvl;
		}
		return maxLevel;
	}

	/**
	 * Initialize ArrayLists to store information about level, dominators and
	 * additional data for every tuple If data is stored in ArrayLists
	 * 
	 */
	protected void initLists() {
		level = new ArrayList<Integer>(D.size());
		additionalData = new ArrayList<Integer>(D.size());
		dominators = new ArrayList<>(D.size());
		for (int i = 0; i < D.size(); i++) {
			level.add(0);
			// Initialize every Object as NOTDOMINATED
			additionalData.add(0);
			// Initialize maxLevel of dominators as -1
			dominators.add(new ArrayList<Integer>());
		}
	}

	/**
	 * 
	 * @return Runtimes of phases as ArrayList
	 */
	public ArrayList<Object> getTimes() {
		ArrayList<Object> times = new ArrayList<Object>();
		times.add(init);
		times.add(phase1);
		times.add(phase2);
		times.add(phase3);
		return times;
	}

	/**
	 * Print result depending on mode (mode 3 and 4 equal print-function from
	 * SequentialTopK)
	 * 
	 */
	public void printResult() {
		for (int i = 0; i < result.size(); i++) {
			if (mode == 1 || mode == 2) {
				System.out.println(((ExtendedRandVector) result.get(i)));
			} else if (mode == 3 || mode == 4) {
				System.out.println(result.get(i).toString());
			}
		}
	}
}
