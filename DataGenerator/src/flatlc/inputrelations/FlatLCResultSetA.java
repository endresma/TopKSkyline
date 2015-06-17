package flatlc.inputrelations;

import mdc.MetaDataCursor;

import java.util.ArrayList;


/**
 * Abstract class for all flat level results
 * @author endresma
 *
 */

public abstract class FlatLCResultSetA implements MetaDataCursor,
        PaintableResultSet {

    /**
     * number of rows that will be produced
     */
    protected int rows;

    public int getSize() {
	return rows;
    }

    public abstract ArrayList<Object> getElements();

    public abstract Object next();

    public abstract boolean hasNext();

    public abstract void reset();

}
