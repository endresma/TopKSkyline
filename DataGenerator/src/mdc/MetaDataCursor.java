package mdc;


/**
 * A metadata-cursor realizes a cursor additionally providing metadata. It
 * extends the interface {@link xxl.core.cursors.Cursor} and
 * {@link xxl.core.util.MetaDataProvider}. So the main difference between a
 * regular cursor and a metadata-cursor lies in the provision of the
 * <tt>getMetaData</tt> method, which returns metadata information about the
 * elements this metadata-cursor delivers. The return value of the
 * <tt>getMetaData</tt> method can be an arbitrary kind of metadata information,
 * e.g., relational metadata information, therefore it is of type
 * {@link Object}.
 *
 * <p>When using a metadata-cursor, it has to be guaranteed, that all elements
 * contained in this metadata-cursor refer to the same metadata information.
 * That means, every time <tt>getMetaData</tt> is called on a metadata-cursor,
 * it should return the same metadata information. Generally this method is
 * called only once.</p>
 *

 */
public interface MetaDataCursor extends Cursor {

	/**
	 * Returns the metadata information for this metadata-cursor. The return
	 * value of this method can be an arbitrary kind of metadata information,
	 * e.g., relational metadata information, therefore it is of type
	 * {@link Object}. When using a metadata-cursor, it has to be
	 * guaranteed, that all elements contained in this metadata-cursor refer to
	 * the same metadata information. That means, every time <tt>getMetaData</tt>
	 * is called on a metadata-cursor, it should return exactly the same metadata
	 * information.
	 *
	 * @return an object representing metadata information for this
	 *         metadata-cursor.
	 */
	public abstract Object getMetaData();

}
