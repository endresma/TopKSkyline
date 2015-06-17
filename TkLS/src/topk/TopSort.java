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

package topk;

import flatlc.levels.FlatLevelCombination;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: endresma
 * Date: 10.12.14
 * Time: 10:50
 * Sorts some data based on a sorting function
 */
public class TopSort {


    /**
     * sort a list of input data concerning the entropy criterion mentioned in
     * Godfrey: Skyline with Presorting
     * @param data
     */
    public static void sort(List<Object> data) {

        Comparator entropyComparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                FlatLevelCombination flc_o1 = (FlatLevelCombination) o1;
                FlatLevelCombination flc_o2 = (FlatLevelCombination) o2;

                float e1 = entropy(flc_o1);
                float e2 = entropy(flc_o2);

                if (e1 < e2)
                    return -1;
                else if (e1 > e2)
                    return 1;
                else // e1 == e2
                    return 0;


            }
        };


        Collections.sort(data, entropyComparator);

    }


    private static float entropy(FlatLevelCombination flc) {
        int[] t = flc.getLevelCombination();
        float e=0;


        // E(t) = \sum_{i=1}^k ln(t[a_i] + 1)
        for(int i=0; i<t.length; ++i) {
            e +=  Math.log(t[i] + 1);
        }

        return e;
    }


}
