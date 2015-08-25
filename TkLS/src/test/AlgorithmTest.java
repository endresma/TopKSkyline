/*
 * Copyright (c) 2015. markus endres, timotheus preisinger
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package test;

import btg.BTG;
import btg.BTGDataA;
import btg.BTGDataArray;
import flatlc.inputrelations.*;
import flatlc.realdata.FlatLCFileDataGenerator;
import topk.EBNLTopK;
import topk.ESFSTopK;
import topk.TkLS;
import util.GnuplotExporter;
import util.Stopwatch;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * User: endresma
 * Date: 17.06.15
 * Time: 15:24
 */
public class AlgorithmTest {

    public static void main(String[] args) {


        // Example 1, Figure 6 in the paper
        // Hotel search, low-card domain [3,4,4], input sizes {50,100,15}
        // top-k = 5
        // anti, corr, ind data


        // TODO: House, NBA
        // und weitere Experimente
        // anti, corr, ind, zillow, nba, house, weather (falls vorhanden)
        // verschiedene Dimensionen, verschiedenes k, verschieden Anzahl Objekte

        // anti, corr, ind, gaussian, zillow, nba, house, weather
        // zillow_data.txt, nba_data.txt, house_data.txt
        String distribution = "ind";
        int top_k = 25000;

        int n[] = new int[]{50000};
        int maxLevels[] = new int[]{2,3,5,10,100};

        // 3d: {2,3,5}
        // 5d: {2,3,5,10,100}
        // 7d: {2,3,5,10,10,10,100}


        /** config for real world data */
        // Zillow
        // int[] n = new int[]{2236252};
        // int maxLevels[] = new int[]{10, 10, 36, 45};

        // NBA
        //        int[] n = new int[]{17265};
        //        int maxLevels[] = new int[]{10, 10, 10, 10, 10};

        // House
        //         int[] n = new int[]{127931};
        //         int maxLevels[] = new int[]{10000,10000,10000,10000,10000,10000};
        //        int maxLevels[] = new int[]{100,100,100,100,100,100};


        String[] algorithms = new String[]{"EBNL", "ESFS", "TkLS"};


        runtime_tuples(algorithms, distribution, n, maxLevels, top_k);

    }


    /**
     * EBNL TopK algorithm
     */
    public static long runEBNLTopK(ArrayList<Object> input, int top_k) {
        System.out.println("Run EBNL");
        Stopwatch sw = new Stopwatch();
        EBNLTopK algorithm = new EBNLTopK(input, top_k);
        int skylineSize = countResult(algorithm);
        long runtime = sw.getElapsedNanoSecTime();

        System.out.println("Runtime: " + nanoToSeconds(runtime));
        System.out.println("skylineSize: " + skylineSize);

        return runtime;


    }

    /**
     * ESFS TopK algorithm
     */
    public static long runESFSTopK(ArrayList<Object> input, int top_k) {
        System.out.println("Run ESFS");

        Stopwatch sw = new Stopwatch();
        ESFSTopK algorithm = new ESFSTopK(input, top_k);
        int skylineSize = countResult(algorithm);
        long runtime = sw.getElapsedNanoSecTime();

        System.out.println("Runtime: " + nanoToSeconds(runtime));
        System.out.println("skylineSize: " + skylineSize);

        return runtime;
    }


    /**
     * TkLS TopK algorithm based on Lattice Skyline / Hexagon
     */
    public static long runTkLS(ArrayList<Object> input, int top_k, int[] maxLevels) {
        System.out.println("Run TkLS");

        Stopwatch sw = new Stopwatch();
        BTGDataA btg = new BTGDataArray(new BTG(maxLevels));
        TkLS algorithm = new TkLS(input.iterator(), btg, top_k);
        int skylineSize = countResult(algorithm);
        long runtime = sw.getElapsedNanoSecTime();

        System.out.println("Runtime: " + nanoToSeconds(runtime));
        System.out.println("skylineSize: " + skylineSize);

        return runtime;
    }


    //    /**
    //     * Execute the algorithm Extended-BNL EBNLTopK.
    //     *
    //     * @param input
    //     * @param pareto
    //     * @throws PreferenceException
    //     */
    //    public static ResultInfo runEBnlTopK(FlatLCResultSetA input, ParetoPreference pareto,
    //                                         int topk) throws PreferenceException {
    //
    //
    //        ArrayList<Object> arrayInput = input.getElements();
    //
    //        Stopwatch sw = new Stopwatch();
    //        EBNLTopK algorithm = new EBNLTopK(arrayInput, pareto, topk);
    //
    //        int bmoSize = countResult(algorithm);
    //        long runtime = sw.getElapsedNanoSecTime();
    //
    //        return new ResultInfo(runtime, bmoSize);
    //
    //
    //    }


    //    /**
    //     * Execute the algorithm Extended-SFS ESFSTopK.
    //     *
    //     * @param input
    //     * @param pareto
    //     * @throws PreferenceException
    //     */
    //    public static ResultInfo runESFSTopK(FlatLCResultSetA input, ParetoPreference pareto,
    //                                         int topk) throws PreferenceException {
    //
    //
    //        ArrayList<Object> arrayInput = input.getElements();
    //
    //        Stopwatch sw = new Stopwatch();
    //        ESFSTopK algorithm = new ESFSTopK(arrayInput, pareto, topk);
    //
    //        int bmoSize = countResult(algorithm);
    //        long runtime = sw.getElapsedNanoSecTime();
    //
    //        return new ResultInfo(runtime, bmoSize);
    //
    //
    //    }
    //
    //
    //    /**
    //     * Execute the algorithm HexagonTopK to evaluate the top-k BMO objects based on
    //     * FlatLevelCombinations.
    //     *
    //     * @param input
    //     * @param pareto
    //     * @throws PreferenceException
    //     */
    //    public static ResultInfo runHexagonTopK(FlatLCResultSetA input, ParetoPreference pareto, int topk) throws PreferenceException {
    //
    //        Stopwatch sw = new Stopwatch();
    //        BTGDataA btg = new BTGDataArray(new BTG(pareto));
    //        System.out.println("create BTGData: " + sw.getElapsedMillSecTime());
    //
    //        HexagonTopK algorithm = new HexagonTopK(input, pareto, btg, topk);
    //
    //        int bmoSize = countResult(algorithm);
    //        long runtime = sw.getElapsedNanoSecTime();
    //
    //        return new ResultInfo(runtime, bmoSize);
    //
    //    }


    /**
     * Generate input data based on
     * anti, corr, ind, gaussian, zillow, nba, house, weather
     */
    public static FlatLCResultSetA generateInput(String distribution, int inputSize, int[] maxLevels) {
        System.out.println("\n********************************************************************************");
        System.out.println("Generate data: " + distribution);

        //        System.out.println("Generate " + inputSize + " tuples");
        //        System.out.println("Generate Input Data Set of size: "                + this.getDataSize() + "");

        FlatLCResultSetA input = null;
        // generate input in memory, do not write to file
        boolean inMemory = true;

        switch (distribution) {
            case "anti":
                input = new FlatLCAntiCorrelatedResultSet(inputSize, maxLevels, inMemory);
                break;
            case "corr":
                input = new FlatLCCorrelatedResultSet(inputSize, maxLevels, inMemory);
                break;
            case "ind":
                input = new FlatLCRandomResultSet(inputSize, maxLevels, inMemory);
                break;
            case "guassian":
                input = new FlatLCGaussianResultSet(inputSize, maxLevels, inMemory);
                break;
            default:
                // in case of real world data read it from file and generate a FlatLCFileDataGenerator object
                input = new FlatLCFileDataGenerator(distribution, inputSize, maxLevels);
                //                throw new RuntimeException("input data not supported: " + distribution);
        }

        return input;


    }

    /**
     * converts nano seconds to seconds
     */
    public static double nanoToSeconds(long nano) {
        return nano / 1000. / 1000. / 1000.;
    }


    /**
     * count the size of the BMO size. Just iterator through the cursor.
     *
     * @param cursor
     * @return
     */
    private static int countResult(Iterator cursor) {
        int counter = 0;

        Object o = null;
        while (cursor.hasNext()) {
            o = cursor.next();
            //            System.out.println(o.toString());
            counter++;
        }


        return counter;
    }


    /**
     * test runtimes for different data sets, distributions, etc.
     */
    public static void runtime_tuples(String[] algorithms, String distribution, int[] inputSize, int[] maxLevels, int top_k) {


        String xLabel = "Input Size";
        String yLabel = "Runtime (sec)";
        String[] xylabels = {xLabel, yLabel};
        String dirPath = "out/";


        StringBuffer toWrite;
        GnuplotExporter gpx = null;


        gpx = new GnuplotExporter(dirPath, "datFile", "pltFile", algorithms, xylabels, new String[]{distribution, "Runtime"});

        // for all input sizes
        for (int iSize : inputSize) {


            FlatLCResultSetA input = generateInput(distribution, iSize, maxLevels);

            toWrite = new StringBuffer();
            toWrite.append(iSize);


            /** EBNL */
            ArrayList<Object> arrayInput = input.getElements();
            long runtimeEBENL = runEBNLTopK(arrayInput, top_k);

            toWrite.append('\t');
            toWrite.append(nanoToSeconds(runtimeEBENL));

            System.gc();

            /** ESFS */
            input.reset();
            arrayInput = input.getElements();
            long runtimeESFS = runESFSTopK(arrayInput, top_k);

            toWrite.append('\t');
            toWrite.append(nanoToSeconds(runtimeESFS));

            System.gc();


            /** TkLS */
            input.reset();
            arrayInput = input.getElements();
            long runtimeTkLS = runTkLS(arrayInput, top_k, maxLevels);

            toWrite.append('\t');
            toWrite.append(nanoToSeconds(runtimeTkLS));

            System.gc();

            gpx.write(toWrite.toString());
            toWrite = null;

        }

        gpx.close();
        gpx.plot();
        gpx.showRuntimeDiagram();


    }


}
