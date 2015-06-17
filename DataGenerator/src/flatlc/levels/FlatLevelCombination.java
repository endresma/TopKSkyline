package flatlc.levels;

import java.io.Serializable;


/**
 * <p>
 * Object to store the base preference levels generated for an object. The base
 * preference levels are stored in an array of integer values. This object is a
 * less mighty but more performing variation of
 * <code>{@link preference.csv.optimize.LevelCombination}</code>. A
 * <code>FlatLevelCombination</code> only supports Pareto preferences with all
 * contained preferences using regular SV semantics.
 * </p>
 * <p>
 * Therefore, no trees to represent the preference structure are needed. Also,
 * the level values always are integers.
 * </p>
 * <p/>
 * <p>
 * Just like their big brothers,
 * <code>{@link preference.csv.optimize.LevelCombination}</code> objects,
 * instances of <code>this</code> type are resettable. This is to minimize the
 * number of instances that have to be created and removed by the garbage
 * collector. The management of existing <code>FlatLevelCombination</code>
 * objects may be done by a <code>FlatLevelManager</code>
 * </p>
 * <p/>
 * <p>
 * By the restriction for the preference instances of this class are based on,
 * the always support maximum level values.
 * </p>
 * <p/>
 * <p>
 * It is clear that <code>FlatLevelCombination</code> objects compared to each
 * other must use the same <code>maxLevels</code> and <code>idMults</code>.
 * Otherwise, they would not represent objects rated by the same preference. It
 * may be useful not to directly create <code>FlatLevelCombination</code>
 * objects but to use a <code>FlatLevelManager</code> object that takes care of
 * such issues.
 * </p>
 * <p/>
 * <p>
 * Each <code>FlatLevelCombination</code> object also is an element in a list of
 * <code>FlatLevelCombination</code> objects. The list can be traversed by the
 * <code>next()</code> method. New elements are appended to the list by the
 * method <code>append(FlatLevelCombination)</code>. <code>getFirst()</code> and
 * <code>getLast()</code> offer access to the first rsp. last list element. <br/>
 * Iteration over a list of <code>FlatLevelCombination</code> objects looks like
 * the following:
 * <p/>
 * <pre>
 *   FlatLevelCombination flc = ...
 *
 *   do {
 *     ...
 *   } while ((flc = flc.getNext()) != null);
 * </pre>
 * <p/>
 * For the iteration it is crucial that a <code>FlatLevelCombination</code> is
 * no <code>java.util.Iterator</code>. Calling the <code>getNext()</code> method
 * of a <code>FlatLevelCombination</code> object will always return the same
 * object. To iterate over a whole list, the current object has to be changed to
 * the next one.
 * </p>
 *
 * @author Timotheus Preisinger
 * @author endresma
 * @version 1.0, 2006-12-12
 * @see preference.engine.levels.FlatLevelManager
 * @see preference.engine.levels.LevelCombination
 */

public class FlatLevelCombination implements Serializable {

    public static final int EQUAL = 0;
    public static final int GREATER = 1;
    public static final int LESS = -1;
    public static final int SUBSET = -4;
    public static final int SUBSTITUTABLE = 2;
    @Deprecated
    public static final int UNDEFINED = -3;
    public static final int UNRANKED = -2;

    /**
     * counter for the number of created instances
     */
    protected static int instanceCounter = 0;

    /**
     * number of current instance
     */
    protected int instanceNo = 0;

    /**
     * result of the level function for each value
     */
    public int[] level;

    /**
     * values which's preference level has been computed
     */
    protected Object[] value;

    /**
     * the generated overall level value for this object
     */
    protected int overallLevel;

    /**
     * the generated left or right overall level value for this object
     */
    protected int leftOverallLevel;
    protected int rightOverallLevel;

    /**
     * the generated unique integer identifier for this object
     */
    protected int identifier = -1;

    /**
     * array for storing maximum level values
     */
    protected final int[] maxLevels;

    /**
     * array for storing multiplicators for the computation of unique integer
     * identifiers for level combinations
     */
    protected final int[] idMults;

    protected int timeStamp;

    /**
     * first element of this level combination list
     */
    FlatLevelCombination first;
    /**
     * next element in this level combination list
     */
    FlatLevelCombination next = null;
    /**
     * last element of this level combination list
     */
    FlatLevelCombination last;

    /**
     * split position for semi-pareto
     */
    protected int splitPosition = 0;

    /**
     * Constructor.
     *
     * @param size the size of the value array
     */
    public FlatLevelCombination(int size) {
        this(new int[size], new Object[size]);
    }

    /**
     * @param maxLevels maximum level values
     * @param idMults   multiplicators for unique id generation
     */
    public FlatLevelCombination(int[] maxLevels, int[] idMults) {
        this(new int[maxLevels.length], new Object[maxLevels.length],
                maxLevels, idMults);
    }

    /**
     * @param level
     * @param value
     */
    public FlatLevelCombination(int[] level, Object[] value) {
        this(level, value, null, null);
    }

    // public FlatLevelCombination(int[] level, Object[] value, int
    // splitPosition) {
    // this(level, value, null, null, splitPosition);
    // }

    /**
     * @param level     array for level values
     * @param value     array for base values
     * @param maxLevels array for maximum level values
     * @param idMults   multiplicators for the computation of a unique id
     */
    public FlatLevelCombination(int[] level, Object[] value, int[] maxLevels,
                                int[] idMults) {
        this.level = level;
        this.value = value;
        // consistency check
        // if (level.length > value.length) {
        // throw new
        // IllegalArgumentException("no object values for all level values");
        // }
        this.maxLevels = maxLevels;
        this.idMults = idMults;
        this.instanceNo = ++FlatLevelCombination.instanceCounter;

        // compute overall level
        computeOverallLevel();

        // list settings
        first = this;
        last = this;

    }

    /**
     *
     * @param level
     *            array for level values
     * @param value
     *            array for base values
     * @param maxLevels
     *            array for maximum level values
     * @param idMults
     *            multiplicators for the computation of a unique id
     * @param splitPosition
     *            position where Semi-Pareto splits the input
     */
    // public FlatLevelCombination(int[] level, Object[] value, int[] maxLevels,
    // int[] idMults, int splitPosition) {
    // this.level = level;
    // this.value = value;
    // // consistency check
    // // if (level.length > value.length) {
    // // throw new
    // // IllegalArgumentException("no object values for all level values");
    // // }
    // this.maxLevels = maxLevels;
    // this.idMults = idMults;
    // this.instanceNo = ++FlatLevelCombination.instanceCounter;
    //
    // this.splitPosition = splitPosition;
    //
    // // compute overall level
    // computeOverallLevel();
    //
    // // list settings
    // first = this;
    // last = this;
    //
    // }

    /**
     * Copy constructor. The level values of the given instance will be used for
     * the new instance.
     *
     * @param flc the instance whose level values will be copied.
     */
    public FlatLevelCombination(FlatLevelCombination flc) {
        this.hash = flc.hash;
        this.identifier = flc.identifier;
        this.idMults = flc.idMults;
        this.instanceNo = ++FlatLevelCombination.instanceCounter;
        this.timeStamp = flc.timeStamp;
        this.first = this;
        this.next = this;
        this.last = this;
        this.level = flc.level;
        this.value = flc.value;
        this.maxLevels = flc.maxLevels;
        this.overallLevel = flc.overallLevel;
        this.splitPosition = flc.splitPosition;
        this.leftOverallLevel = flc.leftOverallLevel;
        this.rightOverallLevel = flc.rightOverallLevel;
    }

    /**
     * Returns the number of base preferences represented by this object. On
     * construction, all existing base preferences in the given
     * <code>IPreference</code> -object are analyzed and corresponding objects
     * for base values, on which the levels are computed (which are saved as
     * well) and the prioritization information between the preferences are
     * stored.
     *
     * @return the number of base preferences represented by this object
     */
    public int getSize() {
        return this.level.length;
    }


    public int[] getIdMults() {
        return idMults;
    }

    public int[] getMaxLevels() {
        return maxLevels;
    }


    /**
     * Returns the level value for the base value with the specified index.
     *
     * @param index the index of the level value in the base preference list
     * @return the level value
     */
    public int getLevel(int index) {
        return this.level[index];
    }

    /**
     * Returns the level combination of this object. This is no copy, so be
     * careful with it!
     *
     * @return the level combination
     */
    public int[] getLevelCombination() {
        return this.level;
    }


    /**
     * Returns the level combination of this object as an Object
     *
     * @return the level combination
     */
    public Object[] getLevelCombinationObject() {
        Object[] obj = new Object[level.length];
        for (int i = 0; i < obj.length; i++) {
            obj[i] = level[i];
        }

        return obj;
    }

    /**
     * Returns the maximum level value for the base value with the specified
     * index
     *
     * @param index the index of the level value in the base preference list
     * @return the maximum level value
     */
    public int getMaxLevel(int index) {
        return this.maxLevels[index];
    }

    /**
     * Returns the generated overall level value
     *
     * @return the level value
     */
    public int getOverallLevel() {
        return this.overallLevel;
    }

    public int getOverallLevel(boolean isLeftSP) {
        if (isLeftSP)
            return getLeftOverallLevel();
        return getRightOverallLevel();
    }

    public int getLeftOverallLevel() {
        return this.leftOverallLevel;
    }

    public int getRightOverallLevel() {
        return this.rightOverallLevel;
    }

    /**
     * Returns a unique identifier for this level value combination.
     *
     * @return the level value
     */
    public int getIdentifier() {
        // compute id if neccessary
        if (this.identifier == -1)
            computeIdentifier();
        return this.identifier;
    }

    /**
     * Returns the object rated to generate the level value at position
     * <code>index</code>.
     *
     * @param index the index of the value in the base preference list
     * @return the base value
     */
    public Object getValue(int index) {
        return this.value[index];
    }


    public float getFloatValue(int index) {
        Object val = getValue(index);
        return ((Integer) val).floatValue();
    }

    /**
     * Compare to <code>LevelCombination</code> objects with each other. The
     * result is one of
     * <ul>
     * <li>LESS (if <code>this</code> is worse)</li>
     * <li>GREATER</li>
     * <li>EQUAL</li>
     * <li>SUBSTITUTABLE</li>
     * <li>UNRANKED</li>
     * <li>UNDEFINED</li>
     * <li>SUBSET.</li>
     * </ul>
     * The result can be inserted in the sentence <code>this</code> is
     * <i>&lt;result&gt;</li> than/to/of <code>compareTo</code>.
     *
     * @param compareTo the object to compare to
     * @return the result of the comparison
     */

    public int leftCompare(FlatLevelCombination compareTo) {
        return compare(compareTo, true);
    }

    public int rightCompare(FlatLevelCombination compareTo) {
        return compare(compareTo, false);
    }

    public int compare(FlatLevelCombination compareTo) {
        return compare(compareTo.level, 0, level.length);
    }

    public int compare(FlatLevelCombination compareTo, boolean left) {

        if (splitPosition == 0) { // Pareto
            return compare(compareTo);
        } else {
            int leftResult = compare(compareTo.level, 0, splitPosition);
            int rightResult = compare(compareTo.level, splitPosition,
                    level.length);

            if (left) { // LSP
                // better in the first and better or equal in the second
                // component
                if (leftResult == GREATER
                        && (rightResult == GREATER
                        || rightResult == SUBSTITUTABLE || rightResult == EQUAL))
                    return GREATER;

                // worse in the first and worse or equal in the second component
                if (leftResult == LESS
                        && (rightResult == LESS
                        || rightResult == SUBSTITUTABLE || rightResult == EQUAL))
                    return LESS;

            } else { // RSP
                // better in the first and better or equal in the second
                // component
                if (rightResult == GREATER
                        && (leftResult == GREATER
                        || leftResult == SUBSTITUTABLE || leftResult == EQUAL))
                    return GREATER;

                // worse in the first and worse or equal in the second component
                if (rightResult == LESS
                        && (leftResult == LESS
                        || leftResult == SUBSTITUTABLE || leftResult == EQUAL))
                    return LESS;
            }

        }

        return UNRANKED;

    }

    private int compare(int[] compareTo, int from, int to) {
        int result = SUBSTITUTABLE;
        for (int i = from; i < to; i++) {
            if (this.level[i] < compareTo[i]) {
                // this is better in the current base preference
                if (result == LESS) {
                    // at least once worse and now better: unranked
                    return UNRANKED;
                }
                result = GREATER;
            } else if (this.level[i] > compareTo[i]) {
                // this is worse in the current base preference
                if (result == GREATER) {
                    // at least once better and now worse: unranked
                    return UNRANKED;
                }
                result = LESS;
            }
        }
        return result;
    }

    /**
     * Returns the object this <code>LevelCombination</code> object is based on.
     *
     * @return the preference object
     */
    public Object getPreferenceObject() {
        return this;
    }

    /**
     * Is it a real or pseudo FlatLevelCombination, for StairCase-PruningRegion
     * only
     */
    public boolean isRealFLC() {
        return this.value != null;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append('[');
//        result.append(this.getIdentifier());
        result.append("(id=");
        result.append(this.instanceNo);

        for (int i = 0; i < this.level.length; i++) {
            result.append(",");
            result.append(this.level[i]);
        }
        result.append(")");
        result.append(this.overallLevel);
        result.append(",");
        result.append(splitPosition);
        result.append(']');
        return result.toString();
    }

    public boolean equals(Object obj) {

        FlatLevelCombination other = (FlatLevelCombination) obj;
        if (this.level.length != other.level.length)
            return false;
        for (int i = 0; i < this.value.length; i++) {
            if (!other.value[i].equals(this.value[i])) {
                return false;
            }
        }
        return true;
    }

    private int hash = -1;

    /**
     * Computes the hash code of a level object.
     */
    public int hashCode() {
        if (hash == -1) {
            hash = this.value.hashCode();
            for (int i = 0; i < this.level.length; i++) {
                hash += this.level[i];
            }
        }
        return this.hash;
    }

    /**
     * Returns <code>true</code>, if the given level object is equal to this
     * object. Two level objects with identical elements in their value arrays
     * will be rated equal.
     *
     * @param lvl the level object to be compared
     * @return true, if all elements of the value array are equal
     */
    public boolean levelEqual(FlatLevelCombination lvl) {
        if (lvl.hashCode() != this.hashCode())
            return false;
        if (lvl.level.length != this.level.length)
            return false;
        for (int i = 0; i < this.level.length; i++) {
            if (this.level[i] != lvl.level[i]
                    || !this.value[i].equals(lvl.value[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns <code>true</code>, as this this level object does support maximum
     * level values. This is essential for the computation of a pruning level,
     * uncomparable and worse equivalence classes
     *
     * @return <code>true</code>
     */
    public boolean supportsMaxLevels() {
        return true;
    }

    /**
     * Returns the overall level from which on tupels need not be compared
     * anymore but are worse in any case.
     *
     * @return the highest level not to bad to be in the result set
     */
    public int getPruningLevel() {
        if (overallLevel == 0) {
            return 0;
        }
        // Basispraeferenz mit dem kleinsten Abstand zu ihrem Maximum finden
        // Gleichzeitig alle Level-Maxima aufaddieren
        int min = Integer.MAX_VALUE;
        int sum = 0, curVal, valSum = 0;

        if (level.length == 1) {
            return level[0];
        }
        for (int i = 0; i < level.length; i++) {
            sum += maxLevels[i];
            int curLvl = level[i];
            curVal = maxLevels[i] - curLvl;
            valSum += curVal;
            if (curLvl > 0 && min > curVal) {
                min = curVal;
            }
        }

        if (valSum == 0) {
            // level is maximal
            return sum + 1;
        }

        // Minimum abziehen
        sum -= min + 1;
        return sum;
    }

    /**
     * Returns the number of base level value combinations (equivalence classes)
     * that dominate (or: are better then) objects belonging to
     * <code>this</code> level combination.
     *
     * @return the number of dominated level value combinations
     */
    public int getBetterEquivalenceClasses() {
        if (splitPosition != 0)
            throw new UnsupportedOperationException();

        int count = 1;
        for (int i = 0; i < maxLevels.length; i++) {
            count *= level[i] + 1;
        }
        count -= 1;
        return count;
    }

    /**
     * Returns the number of base level value combinations (equivalence classes)
     * that are dominated (or: are worse then) by objects with this level.
     *
     * @return the number of dominated level value combinations
     */
    public int getWorseEquivalenceClasses() {
        if (splitPosition != 0)
            throw new UnsupportedOperationException();
        int count = 1;
        for (int i = 0; i < maxLevels.length; i++) {
            count *= maxLevels[i] - level[i] + 1;
        }
        count -= 1;
        return count;
    }

    /**
     * Returns the number of base level value combinations (equivalence classes)
     * which dominate (or: are better than) the objects with this object's
     * level.
     *
     * @return the number of dominating level value combinations
     */
    public int getEquivalenceClasses() {
        if (splitPosition != 0)
            throw new UnsupportedOperationException();
        int ec = 1;
        for (int i = 0; i < maxLevels.length; i++) {
            ec *= maxLevels[i] + 1;
        }
        return ec;
    }

    /**
     * Returns the number of base level value combinations (equivalence classes)
     * which are uncomparable to the objects with this object's level. This
     * method will only return a correct result if the preference the level is
     * based on is a Pareto preference containing only base preferences.
     *
     * @return the number of dominating level value combinations
     */
    public int getUncomparableEquivalenceClasses() {
        if (splitPosition != 0)
            throw new UnsupportedOperationException();
        return getEquivalenceClasses() - getWorseEquivalenceClasses()
                - getBetterEquivalenceClasses();
    }

    /**
     * Computes a unique identifier for this level combination. Therefore, the
     * maximum level values of all contained base preferences are used as bases
     * for a numerical system.
     * <p/>
     * Example: We have two base preferences with maximum level values m1 = 3,
     * m2 = 4. A node c = (c1, c2) has the following unique identifier:
     * <p/>
     * <code>c2 + (m2 + 1) * c1</code>.
     * <p/>
     * The node (2, 1) has the unique identifier <code>1 + 5 * 2 = 11</code>.
     * <p/>
     * Beginning from the last value, each level is added to the overall sum
     * after being multiplied with the maximum level value plus one of all level
     * value following to it. In our example, 1 is the rightmost level value. It
     * is not multiplied with any number. 2 can be multiplied with (4 + 1).
     *
     * @return unique identifier
     */
    private int computeIdentifier() {
        this.identifier = 0;
//        if(idMults == null) {
//            this.identifier = -1;
//            return this.identifier;
//        }

        for (int i = 0; i < idMults.length; i++) {
            this.identifier += idMults[i] * level[i];
        }
        return this.identifier;
    }

    /**
     * Computes the overall level of an object.
     *
     * @return the overall level
     */
    private void computeOverallLevel() {
        this.overallLevel = 0;

        if (splitPosition > 0) {
            this.leftOverallLevel = leftOverallLevel();
            this.rightOverallLevel = rightOverallLevel();
        } else {
            this.leftOverallLevel = -1;
            this.rightOverallLevel = -1;
            // NO_SP
            this.overallLevel = 0;
            for (int i = 0; i < level.length; i++) {
                this.overallLevel += level[i];
            }
        }

    }

    /**
     * Indicates if the list has more elements.
     *
     * @return <code>true</code> until the end of the list is reached
     */
    public boolean hasNext() {
        return next != null;
    }

    /**
     * Returns the next flat level combination object.
     *
     * @return next object in list
     */
    public FlatLevelCombination next() {
        return next;
    }

    /**
     * Appends an element to this list.
     */
    public void append(FlatLevelCombination lc) {
        last.next = lc;
        last = lc;
        // lc.first = first;
    }

    public void setNextNull() {
        this.next = null;
        last = this;
    }

    /**
     * Inserts an element right after the current list element.
     *
     * @param lc the element to insert
     */
    public void insert(FlatLevelCombination lc) {
        lc.next = next;
        next = lc;
        lc.last = last;
        // lc.first = first;
    }

    /**
     * Returns the first element of this list.
     *
     * @return the first element of this list
     * @deprecated will return <code>this</code> object
     */
    public FlatLevelCombination getFirst() {
        return first;
    }

    /**
     * Returns the last element of this list.
     *
     * @return the last element of this list
     */
    public FlatLevelCombination getLast() {
        return last;
    }

    /**
     * Get the serial number of the object. Every object sets the number of
     * instance of this type it is in it's constructor.
     *
     * @return serial number
     */
    public int getSerial() {
        return this.instanceNo;
    }

    /**
     * Returns a time stamp of the object. This timestamp is used by BNL
     * algorithms to determine the order of reading (and writing to disk) of
     * tuples
     *
     * @return time stamp
     */
    public int getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * Sets a time stamp of the object. This timestamp is used by BNL algorithms
     * to determine the order of reading (and writing to disk) of tuples
     *
     * @param value the new time stamp;
     */
    public void setTimeStamp(int value) {
        this.timeStamp = value;
    }

    public FlatLCPruningRegion getLeftPruningRegion() {
        return getPruningRegion(true);
    }

    public FlatLCPruningRegion getRightPruningRegion() {
        return getPruningRegion(false);
    }

    public FlatLCPruningRegion getPruningRegion(boolean left) {

        int leftPL = -1;
        int rightPL = -1;
        if (splitPosition > 0) {
            leftPL = getLeftPruningLevel();
            rightPL = getRightPruningLevel();
            if (left) {
                if (splitPosition == level.length - 1)
                    return new FlatLCPruningRegion(leftPL, rightPL - 1);
            } else if (!left) {
                if (splitPosition == 1)
                    return new FlatLCPruningRegion(leftPL - 1, rightPL);
            }
        } else { // NO_SP
            leftPL = rightPL = getPruningLevel();
        }

        // NO_SP
        return new FlatLCPruningRegion(leftPL, rightPL);
    }

    public FlatLCPartialLevels getPartialLevels() {
        return new FlatLCPartialLevels(leftOverallLevel(), rightOverallLevel());
    }

    protected int leftOverallLevel() {
        int lolvl = 0;
        for (int i = 0; i < splitPosition; i++) {
            lolvl += level[i];
        }
        return lolvl;
    }

    protected int rightOverallLevel() {
        int rolvl = 0;
        for (int i = splitPosition; i < level.length; i++) {
            rolvl += level[i];
        }
        return rolvl;
    }

    public int getLeftPruningLevel() {

        // Basispraeferenz mit dem kleinsten Abstand zu ihrem Maximum finden
        // Gleichzeitig alle Level-Maxima aufaddieren
        int min = Integer.MAX_VALUE;
        int sum = 0, curVal, valSum = 0;

        if (level.length == 1) {
            return level[0];
        }

        if (splitPosition == 1) {
            return level[0];
        }

        for (int i = 0; i < splitPosition; i++) {
            sum += maxLevels[i];
            double curLvl = level[i];
            curVal = (int) (maxLevels[i] - curLvl);
            valSum += curVal;
            if (curLvl > 0 && min > curVal) {
                min = curVal;
            }
        }

        // Minimum abziehen
        sum -= min + 1;
        return sum;
    }

    public int getRightPruningLevel() {

        // Basispraeferenz mit dem kleinsten Abstand zu ihrem Maximum finden
        // Gleichzeitig alle Level-Maxima aufaddieren
        int min = Integer.MAX_VALUE;
        int sum = 0, curVal, valSum = 0;

        if (level.length == 1) {
            return level[0];
        }

        if (splitPosition == level.length - 1) {
            return level[level.length - 1];
        }

        for (int i = splitPosition; i < level.length; i++) {
            sum += maxLevels[i];
            int curLvl = level[i];
            curVal = maxLevels[i] - curLvl;
            valSum += curVal;
            if (curLvl > 0 && min > curVal) {
                min = curVal;
            }
        }

        // Minimum abziehen
        sum -= min + 1;
        return sum;
    }

    public void setSplitPosition(int splitPosition) {
        this.splitPosition = splitPosition;
    }

    public int getColumnCount() {
        return value.length;
    }

    public Object[] getObjectArray() {
        return value;
    }

    public int getInstanceNo() {
        return this.instanceNo;
    }

}