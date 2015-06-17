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

package btg;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract super class for all BTG data structure implementations.
 * <p/>
 * User: endresma
 * Date: 25.01.13
 * Time: 07:39
 */
public abstract class BTGDataA implements BTGDataI, BTGI {

    /**
     * minimal level with occupied nodes
     */
    protected volatile int minLevel;

    /**
     * allocation of the btg, i.e. the number of occupied equivalence classes
     * (not the number of added objects)
     */
    protected AtomicInteger allocation;

    /**
     * BTG representation.
     */
    protected final BTG btg;

    /**
     * the current node
     */
    private int current;

    protected Iterator<?> ec;

    /**
     * Flag to indicate initialization
     */
    private boolean initialized = false;


    /**
     * ctor. Use pruning if flag is true.
     *
     * @param btg //     * @param usePruning
     * @return
     */
    public BTGDataA(BTG btg) {
        this.btg = btg;
        allocation = new AtomicInteger();
    }


    @Override
    public int getAllocation() {
        return allocation.get();
    }


    @Override
    public boolean hasNext() {
        if (!initialized) {
//            removeDominated();
            current = getFirstNode();
//            if (current == -1) {
            if (current == END_OF_DATA) {
                throw new RuntimeException("no object found in BTGDataA.hasNext()");
            }
            ec = getEC(current).iterator();
            initialized = true;
        }
        if (ec != null && ec.hasNext()) {
            return true;
        }
        // move on to the next equivalence class
        // TODO: check original code vs. >= 0 && ...
        // if(current > 0)
        if (current != END_OF_DATA) {
//            if (current == 0) {
//                System.err.println("BTGDataA.hasNext(), current = 0");
//            }
//            System.out.println("current = " + current);
            current = getNextOccupiedNode(current);
//            System.out.println("nextOccupiedNode = " + current);

            // TODO: check original code vs. >= 0 && ...
//            if(current > 0) {
            if (current != END_OF_DATA) {
//                if (current == 0) {
//                    System.err.println("BTGDataA.hasNext(), current = 0");
//                }
                ec = getEC(current).iterator();
                return hasNext();
            }
        }
        return false;
    }


    @Override
    public Object next() {
        if (!initialized) {
            throw new UnsupportedOperationException("not initialized in " +
                    "BTGDataA");
        }
        Object nxt = ec.next();
        return nxt;
    }



    /**
     * set new local dominator if it is less than the old one.
     *
     * @param id
     * @param ld
     */
    public boolean setLDIfLess(int id, int ld) {
        throw new RuntimeException("one unrestricted domain not supported in " + getClass().getName());
    }

    public int getLD(int id) {
        throw new RuntimeException("one unrestricted domain not supported in " + getClass().getName());
    }


    public boolean addObjectIfLDIsLess(int id, Object object, int ld)  {
        throw new RuntimeException("one unrestricted domain not supported in " + getClass().getName());
    }


    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove is not supported");
    }

//    @Override
//    public LevelManager getLevelManager() {
//        return btg.getLevelManager();
//    }

    public BTG getBTG() {
        return btg;
    }

    @Override
    public int getDimension() {
        return btg.getDimension();
    }

    @Override
    public int getHeight() {
        return btg.getHeight();
    }

    @Override
    public int[] getEdgeWeights() {
        return btg.getEdgeWeights();
    }

    @Override
    public int getWeight(int index) {
        return btg.getWeight(index);
    }

    @Override
    public int getSize() {
        return btg.getSize();
    }

    @Override
    public int getOverallLevel(int id) {
        return btg.getOverallLevel(id);
    }

    @Override
    public int getOverallLevel(int[] levelCombination) {
        return btg.getOverallLevel(levelCombination);
    }

    @Override
    public int[] getLevelCombination(int id) {
        return btg.getLevelCombination(id);
    }

    @Override
    public int getID(int[] levelCombination) {
        return btg.getID(levelCombination);
    }

    @Override
    public int getInvertedID(int id, int[] levelComb) {
        return btg.getInvertedID(id, levelComb);
    }

    @Override
    public int getLevel(int i) {
        return btg.getLevel(i);
    }

    @Override
    public int getMaxLevel() {
        return btg.getMaxLevel();
    }

    @Override
    public int[] getMaxLevels() {
        return btg.getMaxLevels();
    }

    @Override
    public int getWidth(int level) {
        return btg.getWidth(level);
    }

    @Override
    public int getPruningLevel(int[] lvlCombination) {
        return btg.getPruningLevel(lvlCombination);
    }

    @Override
    public int getPruningLevel(int id) {
        return btg.getPruningLevel(id);
    }


}
