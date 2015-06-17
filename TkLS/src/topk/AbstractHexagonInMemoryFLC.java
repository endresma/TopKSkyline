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
 * Date: 20.03.13
 * Time: 14:03
 * * <p/>
 * The abstract super class of all HexagonInMemoryFLC_Original algorithms using
 * different data structures for the BTG.
 */
public abstract class AbstractHexagonInMemoryFLC<T extends Iterator<Object>>
        implements Iterator {


    /**
     * The data structure for the BTG.
     */
    protected BTGDataA btg;


    protected boolean hasElements = false;

//    protected ParetoPreference preference;

    protected Iterator result;
    protected Object peek;


    /**
     * use pruning or not
     */
    protected boolean usePruning;

    /**
     * the pruning level
     */
    protected int pruningLevel;
//    protected AtomicIntegerCompare pruningLevel;

    /**
     * just for some optimizations in removeDominated()
     */
    protected int oldPruningLevel;
//    protected AtomicIntegerCompare oldPruningLevel;


    /**
     * Input data for the algorithm
     */
    protected T input;

    /**
     * ctor
     */


    protected AbstractHexagonInMemoryFLC() {

    }

    protected AbstractHexagonInMemoryFLC(final T input,
//                                         final ParetoPreference pref,
                                         final BTGDataA btg,
                                         final boolean usePruning) {
        this.input = input;
//        this.preference = pref;
        this.btg = btg;

        this.usePruning = usePruning;
//        if (usePruning) {
//            this.pruningLevel.set(btg.getMaxLevel() + 1);
////            this.pruningLevel = new AtomicIntegerCompare(btg.getMaxLevel() + 1);
//
////            this.pruningLevel = btg.getMaxLevel() + 1;
////            this.oldPruningLevel = pruningLevel;
//            this.oldPruningLevel = pruningLevel;
//        }

    }


    /**
     * Returns an <code>Iterator</code> that will iterate through all elements
     * of the result set.
     *
     * @return Iterator
     */
    public Iterator<Object> getResults() {

//        long begin = System.currentTimeMillis();
        removeDominated();
//        long end = System.currentTimeMillis();
//        long fdRuntime = end - begin;

//        System.out.println("Time to remove dominated nodes (sec): " + (fdRuntime/1000.));

        return btg;
    }


//    protected abstract void removeDominated();


    /**
     * Apply pruning on the different data structures. Since this differs
     * from algorithm to algorithm each one must implement this method itself.
     * <p/>
     * Removes all levels between from and to, incl.
     */
//    protected abstract void applyPruning(int from, int to);


    /**
     * adding Phase
     */
    protected abstract void computeResult();


//    /**
//     * Apply pruning on the different data structures. Since this differs
//     * from algorithm to algorithm each one must implement this method itself.
//     * Removes all levels between from and to, incl.
//     */
//    protected void applyPruning(int from, int to) {
//        for (int i = from; i <= to; i++) {
//            btg.removeLevel(i);
//        }
//    }


//    /**
//     * Set the pruning level to pruning level of this id if it is better than
//     * the current pruning level. In this case return true. If the pruning level is not changed, return false.
//     *
//     * @param id the current node id
//     * @return
//     */
//    protected boolean setPruningLevel(int id) {
//
//
//        int pl = btg.getPruningLevel(id);
//
//        return pruningLevel.setIfLess(pl);
//
////        if (pl < pruningLevel) {
////            oldPruningLevel = pruningLevel;
////            pruningLevel = pl;
////            return true;
////        }
////
////        return false;
//    }


    public void reset() {
        throw new UnsupportedOperationException("reset in " + getClass()
                .getName() + " not supported");
    }


    public boolean hasElements() {
        return this.hasElements;
    }

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
        throw new UnsupportedOperationException(
                "remove not supported in " + getClass().getName());

    }


    /**
     * Number of occupied nodes (Equivalence classes) in the BTG
     *
     * @return int
     */
    protected int getAllocation() {
        return btg.getAllocation();
    }

    /**
     * Remove Phase
     * removeDominated is called from @see AbstractHexagonInMemoryFLC
     * .getResults() and removes dominated nodes from the BTG
     */
    protected void removeDominated() {

//        if (btg instanceof BTGDataLevelBasedA) {
//            if (btg.getMinLevel() == 0) {
//                System.out.println("Top Node in Level 0 found!");
//
//                for (int i = 1; i <= btg.getHeight() - 1; i++) {
//                    btg.removeLevel(i);
//                }
//
//                return;
//            }
//        } else {
            if (btg.getFirstNode() == 0) {
                System.out.println("Zero node occupied, return -------------");
                // only tuples with a level of 0 belong to the result set
//            btg.next[0] = -1;
                btg.removeBetween(0, BTGDataI.END_OF_DATA);
                return;
            }
//        }


        int current;
//        if (btg instanceof BTGDataLevelBasedA) {
//            current = btg.getFirstNode();
//        } else {
            current = 1;
//        }

        int position;
//        int currentLevel;
//        while (current > 0) {


        while (current != BTGDataI.END_OF_DATA) {
            if (btg.getEC(current) != null) {

                for (int i = 0; i < btg.getMaxLevels().length; i++) {

                    position = current + btg.getWeight(i);
                    if (position < btg.getSize() &&
                            btg.getOverallLevel(current) + 1 ==
                                    btg.getOverallLevel(position)) {
                        // Erhoehung moeglich
//                        DebugPrinter.println("*** *** walkDown from " + current                               + " to " + position);
                        walkDown(position, i, true);
                    }
                }
            } else {
                // current node does not contain elements => remove it from list
//                if (!(btg instanceof BTGDataLevelBasedA)) {
//                    int prevNode = btg.getPrevNode(current);
//                    int nextNode = btg.getNextNode(current);
//                    DebugPrinter.println("removeBetween: " + prevNode + " " +                           "and " +                            nextNode);
                    btg.removeBetween(btg.getPrevNode(current),
                            btg.getNextNode(current));
//                }
            }

//            System.out.println("current = " + current);
//            printArray("BTG", btg.data);
//            printArray("next",);

//            if (btg instanceof BTGDataLevelBasedA) {
//                current = btg.getNextOccupiedNode(current);
//            } else {
                current = btg.getNextNode(current);
//            }
//

        }

//        if (!(btg instanceof BTGDataLevelBasedA)) {
            btg.setFirstNode(btg.getNextNode(0));
//        }

//        if (usePruning && btg.getOverallLevel(current) >= pruningLevel) {
//            current = BTGDataA.END_OF_DATA;
//        }

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
//        DebugPrinter.println("*** *** walkDown to " + position);
        int level = btg.getOverallLevel(position);
//        if (usePruning) {
//
////            DebugPrinter.println("------------------ Pruning");
//            if (level >= pruningLevel.get()) {
//                btg.removeEntry(position);
//                btg.setUsedClass(position, true);
//                return;
//            }
//        }

        FlatLevelCombination lc = null;

        if (btg.getEC(position) == null) {
            // node does not exist
            if (btg.isUsedClass(position)) {
                // node has been visited
//                DebugPrinter.println("return from " + position);
                return;
            }
            // node has not been visited: walk down followers
//            lc = btg.getLevelManager().constructLevelCombination(position);
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

//            if (!(btg instanceof BTGDataLevelBasedA)) {
                btg.removeBetween(btg.getPrevNode(position),
                        btg.getNextNode(position));
//            }
        }


    }


}
