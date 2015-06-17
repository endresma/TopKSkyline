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


import btg.BTGDataA;
import btg.BTGDataI;
import flatlc.levels.FlatLevelCombination;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Top-k lattice algorithm based on the implementation of Hexagon [PK07, MPJ07].
 * @author endresma
 */

public class HexagonTopK extends AbstractHexagonInMemoryFLC {


    /**
     * result set
     */
    private ArrayList<List<Object>> bmo_ml_result;
    /**
     * number of tuples to find altogether;
     */
    protected int topK;
    /**
     * number of tuples, that are already found;
     */
    private int foundK = 0;
    /**
     * array for result tuples
     */
    protected Object[] m_resultTupleMemory;
    /**
     * counter for the output;
     */
    private int returnedTupleNumber = 0;


    public HexagonTopK(Iterator input, final BTGDataA btg, int topK)  {

        super(input, btg, false);
        this.topK = topK;
    }


    /**
     * Tries to add a new object to the result set. If the object it worse than
     * every other object currently in the result set, it will not be added and
     * the. method will return <code>false</code>.
     *
     * @param object  the object to add
     * @return <code>true</code> if the object has been added,
     * <code>false</code> otherwise.
     */
    public boolean addObject(Object object) {

        FlatLevelCombination flc = (FlatLevelCombination)object;
        int[] lc = flc.getLevelCombination();
//        int id = btg.computeIdentifier(lc);
        int id = btg.getID(lc);
        if (id > btg.getSize()) {
            throw new RuntimeException(("ID " + id + " to big in " +
                    "HexagonTopK"));
        }

        return btg.addObject(id, object);
    }

    @Override
    public void computeResult() {

        // add all tuples to the BTG

            // give all input
            while (input.hasNext()) {
                addObject(input.next());
            }



        // fill BMO ml sets
        bmo_ml_result = fillBMOSetsTopK();

//        int lvl = 0;
//        int count_k = 0;
//        for (List stratum : bmo_ml_result) {
//            System.out.println("size of stratum " + (lvl++) + " : " + stratum.size());
//            count_k += stratum.size();
//            System.out.println("Count_k: " + count_k);
//
//        }


        int nrOfTuples_bmo_ml_set = 0;
//        returnedTupleNumber = 0;

        // total number of BMO_ml sets
        int nrOf_bmo_ml_sets = bmo_ml_result.size();

//        if (nrOf_bmo_ml_sets > 0) {


        // number of objects in BTG, this corresponds to the number of input tuples
        for (int k = 0; k < nrOf_bmo_ml_sets; ++k) {
            nrOfTuples_bmo_ml_set += bmo_ml_result.get(k).size();
        }
        // set topK if there are not enough input objects at all, i.e. topk > |R|
        if (nrOfTuples_bmo_ml_set < topK) {
            topK = nrOfTuples_bmo_ml_set;
        }

        m_resultTupleMemory = new Object[topK];

        int i = 0;
        int nrOfTuplesAt_bmo_ml_set;

        while ((i < nrOf_bmo_ml_sets) && (foundK < topK)) {
            // number of objects in BMO_ml set i
            nrOfTuplesAt_bmo_ml_set = bmo_ml_result.get(i).size();
            for (int j = 0; j < nrOfTuplesAt_bmo_ml_set && foundK < topK; ++j) {
                m_resultTupleMemory[foundK] = bmo_ml_result.get(i).get(j);
                ++foundK;
            }
            ++i;
        }

//        if (topK > 0) {
        peek = m_resultTupleMemory[returnedTupleNumber];
        returnedTupleNumber++;
//        }

        foundK = 0;


//        } else {   // no objects found
//            peek = null;
//            topK = 0;
//        }

    }


    /**
     * fill multi-level BMO sets
     *
     * @return
     */
    public ArrayList<List<Object>> fillBMOSetsTopK() {
        int[] tmp_ml = new int[btg.getSize()];

        // temporary objects to keep the BMO sets of different levels
        ArrayList<List<Object>> bmo_ml_sets = new ArrayList<>();
        // initialize first List in bmo_ml_sets
        bmo_ml_sets.add(new ArrayList<>());

        int currentNode = 0;

        do {
            // use offset for tmp_ml computation
            final int offset = (btg.getEC(currentNode) == null) ? 0 : 1;
            // reset the BMO of dominated nodes
            int[] levelComb;
            // necessary for the walk down in the BTG, consider each preference / dimension and compute the successor
            levelComb = btg.getLevelCombination(currentNode);
            for (int i = 0; i < btg.getDimension(); ++i) {
                if (levelComb[i] < btg.getMaxLevels()[i]) {
                    // dominated node found, direct dominated nodes
                    final int domID = currentNode + btg.getEdgeWeights()[i];
                    // compute tmp_ml
                    tmp_ml[domID] = Math.max(tmp_ml[domID], tmp_ml[currentNode] + offset);

                }
            }

            // node not empty, so add objects to BMO_ml sets
            if (btg.getEC(currentNode) != null) {
                // node is not empty: add tuples to the corresponding BMO set level
                // check if BMO level already exists in the ArrayList of bmoSets
                if (bmo_ml_sets.size() > tmp_ml[currentNode]) {
                    bmo_ml_sets.get(tmp_ml[currentNode]).addAll(btg.getEC(currentNode).getElements());
                } else {
                    // set does not exist yet
                    // append new multi-level BMO set
                    bmo_ml_sets.add(btg.getEC(currentNode).getElements());
                }

            }
            // remove unused memory
            btg.setEC(currentNode, null);
            // get next node in the BFT
            currentNode = btg.getNextNode(currentNode);

            // while not end of BTG is reached
        } while (currentNode != BTGDataI.END_OF_DATA);

        if (bmo_ml_sets.size() == 1 && (bmo_ml_sets.get(0)).size() == 0) {
            bmo_ml_sets.remove(0);
        }

        return bmo_ml_sets;

    }


    public Object next() {
        if (bmo_ml_result == null) {
            computeResult();
        }
        Object res = peek;
        /*
         * returnedTupleNumber is compared with foundK and not with topK,
		 * because topK can be greater than number of tuples in db.
		 * In such case we must return all tuples only.
		 */
        if (topK > 0 && returnedTupleNumber < topK) {
            peek = m_resultTupleMemory[returnedTupleNumber];
            ++returnedTupleNumber;
        } else {
            peek = null;
        }
        return res;
    }


    public boolean hasNext() {
        if (bmo_ml_result == null) {
            computeResult();
        }
        return peek != null;
    }

}
