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

import java.util.Iterator;

/**
 * User: endresma
 * <p/>
 * The abstract super class of all lattice-based algorithms using
 * different data structures for the BTG.
 */
public abstract class AbstractLS<T extends Iterator<Object>> implements Iterator {


    /**
     * The data structure for the BTG.
     */
    protected BTGDataA btg;


    protected boolean hasElements = false;

    protected Iterator result;
    protected Object peek;


    /**
     * use pruning or not
     */
    //    protected boolean usePruning;


    /**
     * Input data for the algorithm
     */
    protected T input;

    /**
     * ctor
     */
    protected AbstractLS() {
    }

    protected AbstractLS(final T input, final BTGDataA btg) {
        this.input = input;
        this.btg = btg;
    }



    /**
     * adding Phase
     */
    protected abstract void computeResult();


    @Override
    public boolean hasNext() {
        if (result == null) {
            computeResult();
        }
        return peek != null;
    }

    @Override
    public Object next() {
        if (result == null) {
            computeResult();
        }
        Object res = peek;
        if (result.hasNext()) {
            peek = result.next();

        } else
            peek = null;
        return res;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove not supported in " + getClass().getName());

    }


    /**
     * Remove Phase
     * removeDominated is called from @see AbstractHexagonInMemoryFLC
     * .getResults() and removes dominated nodes from the BTG
     */
    protected void removeDominated() {

        if (btg.getFirstNode() == 0) {
            System.out.println("Zero node occupied, return -------------");
            // only tuples with a level of 0 belong to the result set
            //            btg.next[0] = -1;
            btg.removeBetween(0, BTGDataI.END_OF_DATA);
            return;
        }
        //        }


        int current;
        current = 1;

        int position;

        while (current != BTGDataI.END_OF_DATA) {
            if (btg.getEC(current) != null) {

                for (int i = 0; i < btg.getMaxLevels().length; i++) {

                    position = current + btg.getWeight(i);
                    if (position < btg.getSize() && btg.getOverallLevel(current) + 1 == btg.getOverallLevel(position)) {
                        walkDown(position, i, true);
                    }
                }
            } else {
                // current node does not contain elements => remove it from list
                btg.removeBetween(btg.getPrevNode(current), btg.getNextNode(current));
                //                }
            }

            current = btg.getNextNode(current);

        }

        btg.setFirstNode(btg.getNextNode(0));

    }


    /**
     * Sequential walk down the BTG and visit all dominated nodes.
     * <p/>
     * DFS, depth first search.
     * Walk down the BTG and visit all dominated nodes
     *
     * @param position start of the walk
     * @param edge     the edge the algorithm is "coming down"
     */

    protected void walkDown(int position, int edge, boolean remove) {

        FlatLevelCombination lc = null;

        if (btg.getEC(position) == null) {
            // node does not exist
            if (btg.isUsedClass(position)) {
                // node has been visited
                return;
            }
            // node has not been visited: walk down followers
            lc = btg.getBTG().constructLevelCombination(position);
        } else {
            lc = btg.getEC(position).getLevelCombination();


        }

        for (int i = 0; i <= edge; i++) {
            if (lc.getLevel(i) < btg.getLevel(i)) {
                // Erhoehung moeglich
                walkDown(position + btg.getWeight(i), i, remove);
            }
        }
        // remove element at current position
        // keep in mind that the element has been visited
        if (remove) {
            btg.removeEntry(position);
            btg.setUsedClass(position, true);

            btg.removeBetween(btg.getPrevNode(position), btg.getNextNode(position));
        }


    }


}
