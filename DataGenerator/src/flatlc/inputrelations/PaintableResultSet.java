package flatlc.inputrelations;


import mdc.MetaDataCursor;

/**
 * An implementation of this interface is able to return an array of
 * <code>double</code> values representing the values of an input tuple. This
 * array will be used to create a graphical representation of the returned
 * tuples.
 *
 * @author Timotheus Preisinger
 * @author endresma
 */
public interface PaintableResultSet extends MetaDataCursor {
    public double[] nextVal();
}
