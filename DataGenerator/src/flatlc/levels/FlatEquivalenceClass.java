package flatlc.levels;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * An object of this class represents an equivalence class (i.e. a node) of a
 * better-than-graph (BTG). For each node, the level combination identifying the
 * equivalence class and the objects belonging to it are stored.
 *
 * @author Timotheus Preisinger
 * @author endresma
 * @version 08.11.2005
 */
public class FlatEquivalenceClass implements Iterable {
    /**
     * tuples belonging to the equivalence class
     */
    protected ArrayList elements = new ArrayList();

    /**
     * level combination representing this equivalence class
     */
    protected FlatLevelCombination lvl;

    /**
     * Constructor. The equivalence class is initialized with its level
     * combination.
     *
     * @param lvl level combination of this equivalence class
     */
    public FlatEquivalenceClass(FlatLevelCombination lvl) {
        this.lvl = lvl;
        this.elements.add(this.lvl.getPreferenceObject());
    }

    /**
     * Adds one element to the equivalence class.
     * It is not checked if an element belongs to the same equivalence classe.
     *
     * @param level the level combination of the object to be added. It is important
     *              that the level combination object contains the element to be
     *              added.
     */
    public void add(FlatLevelCombination level) {
        elements.add(level.getPreferenceObject());
    }

    /**
     * Adds an object to this equivalence class. It is not checked that the object
     * really belongs to this equivalence class.
     * If this should be checked, a <code>FlatLevelCombination</code>
     * object must be created and checked with the method <code>checkEC</code>.
     *
     * @param object the object to add
     * @return <code>true</code>
     * @see FlatEquivalenceClass#checkEC(FlatLevelCombination)
     */
    public void add(Object object) {
        elements.add(object);
    }

    /**
     * Adds all preference objects from a given equivalence class.
     * It is not checked if they represent the same level combination.
     *
     * @param lvl the equivalence class of the object to be added
     * @see FlatEquivalenceClass#checkEC(FlatLevelCombination)
     */
    public void add(FlatEquivalenceClass ec) {
        elements.addAll(ec.elements);
    }

    /**
     * Adds all preference objects from a given equivalence class.
     * It is not checked if they represent the same level combination.
     *
     * @param lvl the equivalence class of the object to be added
     * @see FlatEquivalenceClass#checkEC(FlatLevelCombination)
     */
    public boolean checkEC(FlatLevelCombination flc) {
        switch (this.lvl.compare(flc)) {
            case FlatLevelCombination.EQUAL:
            case FlatLevelCombination.SUBSTITUTABLE:
                return true;
        }
        return false;
    }

    /**
     * Returns the <code>preference.csv.optimize.LevelCombination</code>-Object
     * of the equivalence class.
     *
     * @return the equivalence class's level combination object
     */
    public FlatLevelCombination getFlatLC() {
        return this.lvl;
    }

    /**
     * Returns all elements of the equivalence class.
     *
     * @return an <code>java.util.ArrayList</code>-Object containing all
     *         elements
     */
    public ArrayList getElements() {
        return this.elements;
    }

    /**
     * Returns an iterator over all elements of the equivalence class
     *
     * @return an <code>java.util.Iterator</code> over all contained
     *         elements
     */
    public Iterator iterator() {
        return this.elements.iterator();
    }

    /**
     * Returns the number of elements in this equivalence class.
     *
     * @return the number of elements in this equivalence class
     */
    public int getSize() {
        return this.elements.size();
    }

    /**
     * Returns the element at the specified position.
     *
     * @param index the index of the result to return.
     * @return the requested result
     */
    public Object getElement(int index) {
        return this.elements.get(index);
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        // Lazy-Instantiierung
        if (hash == Integer.MAX_VALUE) {
            double result = this.lvl.getLevel(0);
            int size = this.lvl.getSize();
            for (int i = 1; i < size; i++) {
                result *= 1000.0;
                result += this.lvl.getLevel(i);
            }
            hash = (int) result;
        }

        return hash;
    }

    protected int hash = Integer.MAX_VALUE;

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(getClass().getName());
        result.append('[');
        int size = elements.size();
        result.append(this.lvl).append(size).append(" Element");
        if (size != 1)
            result.append('e');
        if (size > 0) {
            result.append(": ");
            for (int i = 0; i < size; i++) {
                result.append(elements.get(i));
            }
        }
        result.append(']');
        return result.toString();
    }

    public int getIdentifier() {
        return lvl.getIdentifier();
    }

    /**
     * Compares <code>this</code> equivalence class with another one. The
     * comparison is done by their <code>LevelCombination</code> objects.
     *
     * @param ec the equivalence class to compare to
     * @return number indicating which equivalence class is better (or if they are
     *         equal, substitable, incomparable, etc.)
     * @see LevelCombination#compare(LevelCombination)
     */
    public int compare(FlatEquivalenceClass ec) {
        return this.lvl.compare(ec.lvl);
    }
}
