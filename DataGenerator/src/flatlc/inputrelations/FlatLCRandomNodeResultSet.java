package flatlc.inputrelations;

import flatlc.levels.FlatLevelCombination;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;


/**
 * <p>
 * This class represents a result set created of random numbers. The objects
 * returned will all belong to a number of nodes which can be specified when
 * constructing the object. If no number of nodes is given, between 10% and
 * 50% of the nodes will be chosen. There will (as it is possible) always be
 * at least one node without tuples belonging to it. 
 * </p>
 * <p>
 * The tuples will be distributed equally on the nodes. 
 * </p>  
 * 
 * <p>
 * As the seeds
 * for the random number generation are fixed, the numbers in a specific row
 * will always be the same. This makes it possible to use a large number of
 * statistically independent "rows" without having to store them on disc but
 * with total reproducibility. 
 * 
 * The computed values will be wrapped into
 * <code>FlatLevelCombination</code> objects and used as level values. The
 * minimum value therefore is the , the maximum value can be given in the
 * constructor. The default maximum level value is 9, leading to 10 different
 * level values for each of the contained values.
 * </p>
 * <p>
 * Each row is returned as an <code>FlatLevelCombination</code> object. The
 * preference object this level combination holds is an array of
 * <code>int</code> values. There is one column <code>id</code> representing a
 * unique id for each tuple. The <code>id</code>s are consecutive numbers.
 * The other column names are <code>colX</code>, where <code>X</code> is
 * the number of the column (starting at <code>0</code>).
 * </p>
 * <p>
 * Returning <code>FlatLevelCombination</code> object is one of the big
 * differences from <code>RandomResultSet</code>. Returning objects belonging
 * to only a specified number of levels is the main difference from 
 * <code>FlatLCRandomResultSet</code>.
 * </p>
 * <p>
 * Additionally, this result set implementation is also capable of pre-sorting
 * the created random result objects.
 * </p>
 * 
 * @see RandomResultSet
 * @see FlatLCRandomResultSet
 * @author Timotheus Preisinger
 * @author endresma
 */
public class FlatLCRandomNodeResultSet extends FlatLCResultSetA  {
  /**
   * default maxumum for column (=level) values
   */
  public static final int DEFAULT_MAX = 10;

 

  /**
   * counter containing the current row
   */
  int currentRow;

  /**
   * the origins of the nodes that will be returned
   */
  FlatLevelCombination[] nodes;

  /**
   * column value generator
   */
  Random generator;

  /**
   * meta data object
   */
  ResultSetMetaData meta;

  /**
   * maximum values for the column values
   */
  int[] maxValues;

  /**
   * multiplicators for the generation of unique ids
   */
  int[] multIDs;
  
  /**
   * Constructor. The column number is without counting the <code>id</code>
   * column. 
   * The minimum of 1 node and 10% of the nodes will be chosen as the 
   * number of return nodes.
   * The maximum column value is set to <code>DEFAULT_MAX</code>.
   * @param cols
   *          number of columns of random data
   * @param rows
   *          number of rows
   * @see FlatLCRandomResultSet.DEFAULT_MAX
   */
  public FlatLCRandomNodeResultSet(int cols, int rows) {
    this(cols, rows, Math.max((int) Math.pow(DEFAULT_MAX + 1, cols) / 10, 1), DEFAULT_MAX);
  }
  
  /**
   * Constructor. The column number is without counting the <code>id</code>
   * column. The maximum column value is set to <code>DEFAULT_MAX</code>.
   * 
   * @param cols
   *          number of columns of random data
   * @param rows
   *          number of rows
   * @param nodeCount
   *          the number of different nodes the returned tuples belong to
   * @see FlatLCRandomResultSet.DEFAULT_MAX
   */
  public FlatLCRandomNodeResultSet(int cols, int rows, int nodeCount) {
    this(cols, rows, nodeCount, DEFAULT_MAX);
  }


  /**
   * Constructor. The column number is without counting the <code>id</code>
   * column.
   * 
   * @param cols
   *          number of columns of random data
   * @param rows
   *          number of rows
   * @param nodeCount
   *          the number of different nodes the returned tuples belong to
   * @param maximum
   *          maximum level value for each column
   */
  public FlatLCRandomNodeResultSet(int cols, int rows, int nodeCount, int maximum) {
    this.rows = rows;
    this.maxValues = new int[cols];
    for (int i = 0; i < maxValues.length; i++) {
      maxValues[i] = maximum;
    }
    init(nodeCount);
  }

  /**
   * Constructor.
   * The minimum of 1 node and 10% of the nodes will be chosen as the 
   * number of return nodes.
   * @param rows
   *          number of rows
   * @param maxValues
   *          maximum level values
   * @param lvl
   *          overall level of the tuples produced
   */
  public FlatLCRandomNodeResultSet(int rows, int[] maxValues) {
    this.rows = rows;
    this.maxValues = maxValues;
    int count = 1;
    for (int i = 0; i < maxValues.length; i++) {
      count *= maxValues[i] + 1;
    }
    
    init(Math.max(count / 10, 1));
  }

  /**
   * Constructor
   * @param rows
   *          number of rows
   * @param maxValues
   *          maximum level values
   * @param lvl
   *          overall level of the tuples produced
   */
  public FlatLCRandomNodeResultSet(int rows, int[] maxValues, int nodeCount) {
    this.rows = rows;
    this.maxValues = maxValues;
    init(nodeCount);
  }


  /**
   * Constructor
   * @param rows
   *          number of rows
   * @param maxValues
   *          maximum level values
   * @param lvl
   *          overall level of the tuples produced
   */
  public FlatLCRandomNodeResultSet(int rows, int[] maxValues, int[][] nodes) {
    this.rows = rows;
    this.maxValues = maxValues;
    init(nodes.length);
    
    for (int i = 0; i < nodes.length; i++) {
      Object[] tmp = new Object[nodes[i].length];
      for (int j = 0; j < tmp.length; j++) {
        tmp[j] = new Integer(nodes[i][j]);
      }
      this.nodes[i] = new FlatLevelCombination(nodes[i], tmp, maxValues, multIDs);
    }
  }

  /**
   * Initializes the random number generators.
   */
  private void init(int nodeCount) {
    int len = this.maxValues.length;

    this.currentRow = 0;
    this.generator = new Random(0);
    
    // compute the multiplicators
    this.multIDs = new int[len];
    this.multIDs[len - 1] = 1;
    for (int i = len; --i > 0;) {
      this.multIDs[i - 1] = this.multIDs[i] * (maxValues[i] + 1);
    }
    
    // create a number of prototype nodes
    this.nodes = new FlatLevelCombination[nodeCount];
    for (int i = 0; i < nodes.length; i++) {
      Object[] obj = new Object[len];
      int[] lvl = new int[len];
      for (int j = 0; j < lvl.length; j++) {
        lvl[j] = generator.nextInt(maxValues[j]);
        obj[j] = new Integer(lvl[j]);
      }
      nodes[i] = new FlatLevelCombination(lvl, obj, maxValues, multIDs);
    }
  }

  public void reset() {
    init(nodes.length);
  }

  public Object getMetaData() {
    if (meta == null) {
      meta = new RandomResultSetMetaData(maxValues);
    }
    return meta;
  }

  public boolean hasNext() throws IllegalStateException {
    return currentRow < rows;
  }

  public Object next() throws IllegalStateException, NoSuchElementException {
    FlatLevelCombination result = null;
    if (currentRow++ < rows) {
      // generate a new row:
      // chose one of the prototype nodes
      int[] levels = nodes[generator.nextInt(nodes.length)].getLevelCombination();
      // fill object array
      Object[] object = new Number[levels.length];
      for (int i = 0; i < levels.length; i++) {
        object[i] = new Integer(levels[i]);
      }
      result = new FlatLevelCombination(levels, object, maxValues, multIDs);
    }
    return result;
  }

  public Object peek() throws IllegalStateException, NoSuchElementException,
      UnsupportedOperationException {
    throw new UnsupportedOperationException("peek is not supported");
  }

  public void open() {
  }

  public void close() {
  }

  public boolean supportsPeek() {
    return false;
  }

  public boolean supportsRemove() {
    return false;
  }

  public boolean supportsReset() {
    return true;
  }

  public boolean supportsUpdate() {
    return false;
  }

  public void remove() throws IllegalStateException,
      UnsupportedOperationException {
    throw new UnsupportedOperationException("remove is not supported");
  }

  public void update(Object arg0) throws IllegalStateException,
      UnsupportedOperationException {
    throw new UnsupportedOperationException("update is not supported");
  }


  public static void main(String[] args) {
    FlatLCRandomNodeResultSet test = new FlatLCRandomNodeResultSet(4, 100);
    while (test.hasNext()) {
      System.out.println(test.next());
    }
    System.out.println("===================");
    int[] mx = new int[] { 4, 2, 1, 5, 3};
    test = new FlatLCRandomNodeResultSet(50, mx, 2);
    while (test.hasNext()) {
      System.out.println(test.next());
    }
  }

  
  public ArrayList<Object> getElements() {
	throw new UnsupportedOperationException(
		"Not implemented yet");
  }
  
@Override
public double[] nextVal() {
	// TODO Auto-generated method stub
	return null;
}
}
