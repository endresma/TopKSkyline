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
import java.util.Iterator;
import java.util.List;

/**
 * User: endresma
 * Date: 09.12.14
 * Time: 16:42
 * <p/>
 * EBNL algorithm for Top-k computation as described in Brando, Goncalves, Gonzalez:
 * Evaluating Top-k Skyline Queries over Relational Databases
 * <p/>
 * Quick and dirty implementation, can be optimized.
 */
public class EBNLTopK implements Iterator {

    protected int topk_counter = 0;

    protected ArrayList<Object> R;
    //    protected final ParetoPreference preference;
    protected final int topK;

    protected Iterator result;


    public EBNLTopK(ArrayList<Object> R, int topK) {

        this.R = R;
//        this.preference = preference;
        this.topK = topK;

        compute();
    }


    /**
     * if some tuple t1 from w dominates t
     *
     * @return
     */
    protected boolean dom1(Object t, ArrayList<Object> w) {
        FlatLevelCombination flc_t = (FlatLevelCombination) t;

        for (Object t1 : w) {
            FlatLevelCombination flc_t1 = (FlatLevelCombination) t1;
//            int compare = preference.compare(t, t1, null);
            int compare = flc_t.compare(flc_t1);
            // t is worse than t1
            if (compare == IPreference.LESS) {
                return true;
            }
        }

        return false;
    }

    /**
     * if t dominates some tuples from w
     */
    protected boolean dom2(Object t, ArrayList<Object> w) {
        FlatLevelCombination flc_t = (FlatLevelCombination) t;
        for (Object t2 : w) {
            FlatLevelCombination flc_t2 = (FlatLevelCombination) t2;
            int compare = flc_t.compare(flc_t2);
//            int compare = preference.compare(t, t2, null);
            // t is better than t2
            if (compare == IPreference.GREATER) {
                return true;
            }
        }

        return false;

    }


    protected void compute() {

        int i = 0;
        int count = 0;
//        int idx = 0;

//        ArrayList<Object> w = new ArrayList<>();

        boolean cont;
        ArrayList<List<Object>> P = new ArrayList<>();

//        try {
        while (count < topK && !R.isEmpty()) {
            // initialize Pi = \emptyset
            P.add(i, new ArrayList<>());
            cont = true;
            ArrayList<Object> R1 = new ArrayList<>();

//                int Rsize = R.size();
            ArrayList<Object> w = new ArrayList<>();
            while (cont) {
                // get first tuple t from R
//                    int k = 0;
                Object t = R.remove(0);

//                    while (k < Rsize) {
                while (!R.isEmpty() || t != null) {

                    // line 10

                    // begin if
                    // if some tuple t1 from w dominates t
                    if (dom1(t, w)) {
                        R1.add(t);

//                        for (Object t1 : w) {
//                            int compare = preference.compare(t, t1, null);
//                            // t is worse than t1
//                            if (compare == IPreference.LESS) {
//                                R1.add(t);
//                            }
//                        }
                    } // if t dominates some tuples from w
                    else if (dom2(t, w)) {
                        w.add(t);
                        // delete dominated tuple from w and insert them into R1

                        ArrayList<Object> tmp = new ArrayList<>();
                        FlatLevelCombination flc_t = (FlatLevelCombination) t;
                        for (Object v : w) {
                            FlatLevelCombination flc_v = (FlatLevelCombination) v;
                            int compare = flc_t.compare(flc_v);
//                            int compare = preference.compare(t, v, null);
                            // t is better than v
                            if (compare == IPreference.GREATER) {
//                                    w.remove(v);
                                tmp.add(v);
                                R1.add(v);
                            }
                        }

                        w.removeAll(tmp);

                    } // if no tuple t1 from w dominates t and there is enough room in w then
                    else {
                        w.add(t);
                    }

                    // else if no tuple t1 from w dominates t and there is NOT enough room in w then
                    // t is inserted into a temporal table R2
                    // end if

                    // line 19


                    // get the next tuple t from R
//                        ++k;
//                        if (k < R.size() - 1)
                    if (R.isEmpty())
                        t = null;
                    else
                        t = R.remove(0);
//                        else R.clear();

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
//                System.out.println("size of stratum " + i + " : " + sizePi);
            count = count + sizePi;
            if (count >= topK)
                break;

            ++i;
            R = R1;

        } // end while
//        } catch (PreferenceException e) {
//            e.printStackTrace();
//        }

//        return P;

//        long begin = System.currentTimeMillis();
        ArrayList<Object> topk_result = new ArrayList<>();

        for (List<Object> out : P) {
            TopSort.sort(out);
            for (Object t : out) {
                topk_result.add(t);
            }
        }

//        long end = System.currentTimeMillis();
//        System.out.println("topk_result: " + (end - begin) + "ms");

        result = topk_result.iterator();

    }


    @Override
    public boolean hasNext() {
        if (result == null) {
            compute();
        }
        if (topk_counter < topK && result.hasNext()) {
            ++topk_counter;
            return true;
        }

        return false;
    }

    @Override
    public Object next() {
        return result.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove not supported");

    }
}
