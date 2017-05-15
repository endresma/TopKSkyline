package benchmarking;

import java.util.ArrayList;

import util.GnuplotExporter;

public class BenchWrite {
	/**
	 * Use Gnuplot to create a partly rowstacked clustered histogram from
	 * datFiles and pltFiles created here (simply put the pltFile and all
	 * corresponding datFiles into one folder and open the pltFile with Gnuplot)
	 */

	/**
	 * Writes phaseResults and algResults into datFiles and creates
	 * corresponding pltFile for clustered histograms, partly rowstacked (to
	 * compare total runtimes from algResults to sum of phases from
	 * phaseResults)
	 * 
	 * @param phaseResults
	 *            - runtime of different phases of parallelTopK
	 * @param algResults
	 *            - total runtime of other algorithms
	 * @param clusterLabels
	 *            - names of algorithms
	 * @param blockLabels
	 *            - e.g. topK = 2, 5, 10, 100, ...
	 * @param xylabels
	 *            - labels of x- and y-axis
	 * @param title
	 *            - additions to the titles of datFile and pltFile to
	 *            distinguish the different files
	 */
	public static void writeToPartlyRowstacked(ArrayList<ArrayList<Object>> phaseResults,
			ArrayList<ArrayList<Object>> algResults, String[] clusterLabels, String[] blockLabels, String[] xylabels,
			String[] title) {

		// String path = "C:/Tests/";
		String path = "";
		String datFileName = "datFile";
		String pltFileName = "pltFile";
		StringBuffer toWrite;
		GnuplotExporter gpx = null;

		// for algResults
		gpx = new GnuplotExporter(path, datFileName + "Alg", pltFileName, blockLabels, xylabels, title, "");
		for (int i = 0; i < algResults.size(); i++) {
			toWrite = new StringBuffer();
			toWrite.append("# " + blockLabels[i]);
			gpx.write(toWrite.toString());
			toWrite = null;
			// data
			for (int j = 0; j < algResults.get(i).size(); j++) {
				toWrite = new StringBuffer();
				toWrite.append(clusterLabels[j] + "\t" + algResults.get(i).get(j));
				gpx.write(toWrite.toString());
				toWrite = null;
			}
			// tow lines before starting next block
			toWrite = new StringBuffer();
			toWrite.append("");
			gpx.write(toWrite.toString());
			gpx.write(toWrite.toString());
			toWrite = null;
		}
		gpx.close();

		// for phaseResults
		gpx = new GnuplotExporter(path, datFileName + "Phases", pltFileName, blockLabels, xylabels, title,
				"partly rowstacked");
		for (int i = 0; i < phaseResults.size(); i++) {
			toWrite = new StringBuffer();
			toWrite.append("# " + blockLabels[i]);
			gpx.write(toWrite.toString());
			toWrite = null;
			// data
			toWrite = new StringBuffer();
			toWrite.append("parallel\t");
			double count = 0;
			for (int j = 1; j < phaseResults.get(i).size(); j++) {
				toWrite.append(phaseResults.get(i).get(j));
				count += (double) phaseResults.get(i).get(j);
				toWrite.append("\t");
			}
			// Add leftover runtime that doesn't belong to any of the previous
			// phases
			// If there is no leftover runtime, add 0
			if ((double) phaseResults.get(i).get(0) - count > 0) {
				toWrite.append((double) phaseResults.get(i).get(0) - count);
			} else {
				toWrite.append((double) 0);
			}
			gpx.write(toWrite.toString());
			toWrite = null;
			// tow lines before starting next block
			toWrite = new StringBuffer();
			toWrite.append("");
			gpx.write(toWrite.toString());
			gpx.write(toWrite.toString());
			toWrite = null;
		}
		gpx.close();
		gpx.plot();
		gpx.showRuntimeDiagram();
	}

	/**
	 * Writes runtimes from vals into datFile and creates corresponding pltFile
	 * for rowstacked histograms (to compare sums of phases)
	 * 
	 * @param vals
	 *            - runtimes for all phases of parallelTopK
	 * @param path
	 *            - not used anymore
	 * @param labels
	 *            - here: BlockSize, Threads or Mode
	 * @param xylabels
	 *            - labels of x- and y-axis
	 * @param columnHeadline
	 *            - e.g c = 1, 2, 4, ...
	 * @param title
	 *            - additions to the titles of datFile and pltFile to
	 *            distinguish the different files
	 */
	public static void writeValsToRowstacked(ArrayList<ArrayList<Object>> vals, String path, String[] labels,
			String[] xylabels, String[] columnHeadline, String[] title) {

		StringBuffer toWrite;
		GnuplotExporter gpx = null;
		// gpx = new GnuplotExporter(path, "datFile", "pltFile", labels,
		// xylabels, new String[]{"Rowstacked", labels[0]}, "rowstacked");
		// gpx = new GnuplotExporter("", "datFile", "pltFile", labels, xylabels,
		// title, "rowstacked");
		gpx = new GnuplotExporter(path, "datFile", "pltFile", labels, xylabels, title, "rowstacked");
		for (int i = 0; i < vals.size(); i++) {
			toWrite = new StringBuffer();
			toWrite.append(columnHeadline[i]);
			double count = 0;
			for (int j = 1; j < vals.get(i).size(); j++) {
				toWrite.append("\t" + vals.get(i).get(j));
				count += (double) vals.get(i).get(j);
			}
			// Add leftover runtime that doesn't belong to any of the previous
			// phases
			// if there is no leftover runtime, add 0
			if ((double) vals.get(i).get(0) - count > 0) {
				toWrite.append("\t" + ((double) vals.get(i).get(0) - count));
			} else {
				toWrite.append("\t" + 0);
			}
			gpx.write(toWrite.toString());
			toWrite = null;
			toWrite = new StringBuffer();
			toWrite.append("");
			gpx.write(toWrite.toString());
			toWrite = null;
		}

		gpx.close();
		gpx.plot();
		gpx.showRuntimeDiagram();
	}

	/**
	 * Writes runtimes from vals to datFile and creates corresponding pltFile
	 * For histograms (to compare total runtimes)
	 * 
	 * @param vals
	 *            - total runtimes of different algorithms
	 * @param path
	 *            - not used in this version
	 * @param data
	 *            - names of algorithms
	 * @param labels
	 *            of x- and y-axis
	 * @param title
	 *            - additions to the titles of datFile and pltFile to
	 *            distinguish the different files
	 */
	public static void writeValsToBox(ArrayList<Object> vals, String path, String[] data, String[] xylabels,
			String[] title) {
		StringBuffer toWrite;
		GnuplotExporter gpx = null;
		// gpx = new GnuplotExporter(path, "datFile", "pltFile", data, xylabels,
		// new String[]{"Clustered", "TEST"}, "box");
		gpx = new GnuplotExporter("", "datFile", "pltFile", data, xylabels, title, "box");

		for (int i = 0; i < vals.size(); i++) {
			toWrite = new StringBuffer();
			toWrite.append(data[i]);
			toWrite.append("\t");
			toWrite.append(vals.get(i));
			gpx.write(toWrite.toString());
			toWrite = null;

			toWrite = new StringBuffer();
			toWrite.append("");
			gpx.write(toWrite.toString());
			toWrite = null;
		}
		gpx.close();
		gpx.plot();
		gpx.showRuntimeDiagram();
	}

	/**
	 * Writes runtimes from vals to datFile and creates corresponding pltFile
	 * For erorrbar-diagrams (to compare total runtimes with large differences)
	 * 
	 * @param vals
	 *            - x-value, y (average runtime), ylow (min rumtime), yhigh (max
	 *            runtime) for every algorithm
	 * @param path
	 *            - not used in this version
	 * @param blockLabels
	 *            - names of different algorithms
	 * @param data
	 *            - x-values (e.g. 2, 3, 5, 10, ...)
	 * @param labels
	 *            of x- and y-axis
	 * @param title
	 *            - additions to the titles of datFile and pltFile to
	 *            distinguish the different files
	 */
	public static void writeValsToErrorbar(ArrayList<ArrayList<ArrayList<Object>>> vals, String path,
			String[] blockLabels, String[] data, String[] xylabels, String[] title, boolean logscale) {
		StringBuffer toWrite;
		GnuplotExporter gpx = null;

		// for every algorithm
		for (int i = 0; i < vals.size(); i++) {

			if (i < vals.size() - 1) {
				// don't plot yet
				// gpx = new GnuplotExporter(path, "datFile" + blockLabels[i],
				// "pltFile", blockLabels, xylabels, title, "");
				// gpx = new GnuplotExporter("", "datFile" + blockLabels[i],
				// "pltFile", blockLabels, xylabels, title, "");
				gpx = new GnuplotExporter(path, "datFile" + blockLabels[i], "pltFile", blockLabels, xylabels, title,
						"");
			} else {
				// here you can plot!
				// gpx = new GnuplotExporter(path, "datFile" + blockLabels[i],
				// "pltFile", blockLabels, xylabels, title, "errorbar");
				// gpx = new GnuplotExporter("", "datFile" + blockLabels[i],
				// "pltFile", blockLabels, xylabels, title,
				// "errorbar-"+logscale);
				gpx = new GnuplotExporter(path, "datFile" + blockLabels[i], "pltFile", blockLabels, xylabels, title,
						"errorbar");
			}

			toWrite = new StringBuffer();
			toWrite.append("#" + blockLabels[i]);
			gpx.write(toWrite.toString());
			toWrite = null;

			// for e.g. ever n in [100, 1000, ...]
			for (int j = 0; j < vals.get(i).size(); j++) {
				toWrite = new StringBuffer();

				// for x, y, ylow, yhigh
				for (int q = 0; q < vals.get(i).get(j).size(); q++) {
					toWrite.append(vals.get(i).get(j).get(q) + "\t");
				}

				gpx.write(toWrite.toString());
				toWrite = null;
			}

			// seperate blocks by adding two blank lines
			toWrite = new StringBuffer();
			toWrite.append("");
			gpx.write(toWrite.toString());
			gpx.write(toWrite.toString());
			toWrite = null;

			gpx.close();
		}

		gpx.plot();
		gpx.showRuntimeDiagram();
	}

}
