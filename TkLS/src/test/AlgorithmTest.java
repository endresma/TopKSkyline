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
import flatlc.inputrelations.FlatLCRandomResultSet;
import flatlc.inputrelations.FlatLCResultSetA;
import topk.EBNLTopK;
import topk.ESFSTopK;
import topk.TkLS;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * User: endresma
 * Date: 17.06.15
 * Time: 15:24
 */
public class AlgorithmTest {

    public static void main(String[] args) {


        int top_k = 15;
        int n = 10000;
        int maxLevels[] = new int[]{2, 3, 5, 10, 100};

        FlatLCResultSetA input = new FlatLCRandomResultSet(n, maxLevels, true);

        /** EBNL */
        ArrayList<Object> arrayInput = input.getElements();

        EBNLTopK algorithm = new EBNLTopK(arrayInput, top_k);

        int bmoSize = countResult(algorithm);
        System.out.println("bmoSize: " + bmoSize);

        /** ESFS */
        input.reset();
        arrayInput = input.getElements();
        algorithm = new ESFSTopK(arrayInput, top_k);
        bmoSize = countResult(algorithm);
        System.out.println("bmoSize: " + bmoSize);

        /** TkLS */
        input.reset();
        arrayInput = input.getElements();

        BTGDataA btg = new BTGDataArray(new BTG(maxLevels));
        TkLS hex = new TkLS(arrayInput.iterator(), btg, top_k);

        bmoSize = countResult(hex);
        System.out.println("bmoSize: " + bmoSize);


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


            System.out.println(o.toString());


            counter++;
        }

        cursor = null;
        return counter;
    }

}
