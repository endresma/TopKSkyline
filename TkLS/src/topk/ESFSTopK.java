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
import util.IPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * User: endresma
 * ESFS algorithm for Top-k computation as described in Brando, Goncalves, Gonzalez:
 * Evaluating Top-k Skyline Queries over Relational Databases
 * <p/>
 * ESFS exploits some kind of pre-sorting of the data.
 */
public class ESFSTopK extends EBNLTopK {

	
	
    public ESFSTopK(ArrayList<Object> R, int topK) {
        super(R, topK);

    }


    /**
     * no tuple t1 from w dominates t
     */
    protected boolean dom3(Object t, ArrayList<Object> w) {

        FlatLevelCombination flc_t = (FlatLevelCombination) t;
        for (Object t3 : w) {
            FlatLevelCombination flc_t3 = (FlatLevelCombination) t3;
            
            //counter.maxScore++;
            
            int compare = flc_t.compare(flc_t3);
            //            int compare = preference.compare(t, t3, null);
            // t3 is better than t, i.e. t is worse than t3
            if (compare == IPreference.LESS) {
                return false;
            }
        }

        return true;

    }

    protected void compute() {

        int i = 0;
        int count = 0;
        //        int idx = 0;

        // topological sort following Godfrey: Skyline with Presorting
        TopSort.sort(R);

        boolean cont;
        ArrayList<List<Object>> P = new ArrayList<>();

        //        try {
        while (count < topK && !R.isEmpty()) {
            // initialize Pi = \emptyset
            P.add(i, new ArrayList<>());
            cont = true;
            ArrayList<Object> R1 = new ArrayList<>();

            ArrayList<Object> w = new ArrayList<>();
            while (cont) {
                // get first tuple t from R
                Object t = R.remove(0);

                //                    while (k < Rsize) {
                while (!R.isEmpty() || t != null) {


                    // line 10

                    // if some tuple t1 from w dominates t then
                    if (dom1(t, w)) {
                        // t is inserted into the temporal table R1
                        R1.add(t);
                    } else // if no tuple t1 from w dominates t and there is enough room in w then
                        if (dom3(t, w)) {
                            // t is inserted into the window w
                            w.add(t);
                        }
                    //  else if no t1 fromw dominates t and there is not enough room in w then
                    // t is inserted into a temporal table R2

                    // line 19


                    // get the next tuple t from R
                    if (R.isEmpty())
                        t = null;
                    else
                        t = R.remove(0);

                } // end while

                // if exist tuples in R2 then
                // R = R2;
                // else
                cont = false;
                // end if
            } // end while

            // evaluate f for all tuples in w; copy tuples from w to Pi
            P.get(i).addAll(w);
            w.clear();

            int sizePi = P.get(i).size();
            count = count + sizePi;
            if (count >= topK)
                break;
            ++i;
            R = R1;

        }

        ArrayList<Object> topk_result = new ArrayList<>();

        for (List<Object> out : P) {
            TopSort.sort(out);
            for (Object t : out) {
                topk_result.add(t);
            }
        }

        result = topk_result.iterator();
        //System.out.println("Final Counter of ESFS: " + counter.maxScore);
    }


}
