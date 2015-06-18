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

package mdc;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface Cursor extends Iterator<Object> {

	/**
	 * Opens the cursor, i.e., signals the cursor to reserve resources, open
	 * files, etc. Before a cursor has been opened calls to methods like
	 * <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield proper
	 * results. Therefore <tt>open</tt> must be called before a cursor's data
	 * can be processed. Multiple calls to <tt>open</tt> do not have any effect,
	 * i.e., if <tt>open</tt> was called the cursor remains in the state
	 * <i>opened</i> until its <tt>close</tt> method is called.
	 * 
	 * <p>Note, that a call to the <tt>open</tt> method of a closed cursor
	 * usually does not open it again because of the fact that its state
	 * generally cannot be restored when resources are released respectively
	 * files are closed.</p>
	 */
	 void open();

	/**
	 * Closes the cursor, i.e., signals the cursor to clean up resources, close
	 * files, etc. When a cursor has been closed calls to methods like
	 * <tt>next</tt> or <tt>peek</tt> are not guaranteed to yield proper
	 * results. Multiple calls to <tt>close</tt> do not have any effect, i.e.,
	 * if <tt>close</tt> was called the cursor remains in the state
	 * <i>closed</i>.
	 * 
	 * <p>Note, that a closed cursor usually cannot be opened again because of
	 * the fact that its state generally cannot be restored when resources are
	 * released respectively files are closed.</p>
	 */
	 void close();

	/**
	 * Returns <tt>true</tt> if the iteration has more elements. (In other
	 * words, returns <tt>true</tt> if <tt>next</tt> or <tt>peek</tt> would
	 * return an element rather than throwing an exception.)
	 * 
	 * <p>This operation should be implemented idempotent, i.e., consequent
	 * calls to <tt>hasNext</tt> do not have any effect.</p>
	 * <b>Important:</b> In order to guarantee a certain semantics, an implementation of a <tt>Cursor</tt>
	 * has to ensure that a call of <tt>hasNext()</tt> always returns <tt>false</tt> after the first time 
	 * <tt>false</tt>
 	 * is delivered. Thus, it should not be possible to receive an element by calling <tt>hasNext()</tt>
 	 * and <tt>next()</tt> at a later point in time, if <tt>hasNext()</tt> returned <tt>false</tt> 
 	 * before (even if the underlying data structure received a new element meanwhile). 
	 *
	 * @return <tt>true</tt> if the cursor has more elements.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 */
	 boolean hasNext() throws IllegalStateException;

	/**
	 * Returns the next element in the iteration. This element will be
	 * accessible by some of the cursor's methods, e.g., <tt>update</tt> or
	 * <tt>remove</tt>, until a call to <tt>next</tt> or <tt>peek</tt> occurs.
	 * This is calling <tt>next</tt> or <tt>peek</tt> proceeds the iteration and
	 * therefore its previous element will not be accessible any more.
	 *
	 * @return the next element in the iteration.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 * @throws NoSuchElementException if the iteration has no more elements.
	 */
	 Object next() throws IllegalStateException, NoSuchElementException;

	/**
	 * Shows the next element in the iteration without proceeding the iteration
	 * (optional operation). After calling <tt>peek</tt> the returned element is
	 * still the cursor's next one such that a call to <tt>next</tt> would be
	 * the only way to proceed the iteration. But be aware that an
	 * implementation of this method uses a kind of buffer-strategy, therefore
	 * it is possible that the returned element will be removed from the
	 * <i>underlying</i> iteration, e.g., the caller can use an instance of a
	 * cursor depending on an iterator, so the next element returned by a call
	 * to <tt>peek</tt> will be removed from the underlying iterator which does
	 * not support the <tt>peek</tt> operation and therefore the iterator has to
	 * be wrapped and buffered.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors. After calling the <tt>peek</tt> method a call to <tt>next</tt>
	 * is strongly recommended.</p> 
	 *
	 * @return the next element in the iteration.
	 * @throws IllegalStateException if the cursor is already closed when this
	 *         method is called.
	 * @throws NoSuchElementException iteration has no more elements.
	 * @throws UnsupportedOperationException if the <tt>peek</tt> operation is
	 *         not supported by the cursor.
	 */
	 Object peek() throws IllegalStateException, NoSuchElementException, UnsupportedOperationException;

	/**
	 * Returns <tt>true</tt> if the <tt>peek</tt> operation is supported by the
	 * cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>peek</tt> operation is supported by the
	 *         cursor, otherwise <tt>false</tt>.
	 */
	 boolean supportsPeek();

	/**
	 * Removes from the underlying data structure the last element returned by
	 * the cursor (optional operation). This method can be called only once per
	 * call to <tt>next</tt> or <tt>peek</tt> and removes the element returned
	 * by this method. Note, that between a call to <tt>next</tt> and
	 * <tt>remove</tt> the invocation of <tt>peek</tt> or <tt>hasNext</tt> is
	 * forbidden. The behaviour of a cursor is unspecified if the underlying
	 * data structure is modified while the iteration is in progress in any way
	 * other than by calling this method.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>remove</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>remove</tt> operation is
	 *         not supported by the cursor.
	 */
	 void remove() throws IllegalStateException, UnsupportedOperationException;

	/**
	 * Returns <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>remove</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	 boolean supportsRemove();

	/**
	 * Replaces the last element returned by the cursor in the underlying data
	 * structure (optional operation). This method can be called only once per
	 * call to <tt>next</tt> or <tt>peek</tt> and updates the element returned
	 * by this method. Note, that between a call to <tt>next</tt> and
	 * <tt>update</tt> the invocation of <tt>peek</tt> or <tt>hasNext</tt> is
	 * forbidden. The behaviour of a cursor is unspecified if the underlying
	 * data structure is modified while the iteration is in progress in any way
	 * other than by calling this method.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @param object the object that replaces the last element returned by the
	 *        cursor.
	 * @throws IllegalStateException if the <tt>next</tt> or <tt>peek</tt> method
	 *         has not yet been called, or the <tt>update</tt> method has already
	 *         been called after the last call to the <tt>next</tt> or
	 *         <tt>peek</tt> method.
	 * @throws UnsupportedOperationException if the <tt>update</tt> operation is
	 *         not supported by the cursor.
	 */
	 void update(Object object) throws IllegalStateException, UnsupportedOperationException;

	/**
	 * Returns <tt>true</tt> if the <tt>update</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>update</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	 boolean supportsUpdate();

	/**
	 * Resets the cursor to its initial state such that the caller is able to
	 * traverse the underlying data structure again without constructing a new
	 * cursor (optional operation). The modifications, removes and updates
	 * concerning the underlying data structure, are still persistent.
	 * 
	 * <p>Note, that this operation is optional and might not work for all
	 * cursors.</p>
	 *
	 * @throws UnsupportedOperationException if the <tt>reset</tt> operation is
	 *         not supported by the cursor.
	 */
	 void reset() throws UnsupportedOperationException;

	/**
	 * Returns <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 * the cursor. Otherwise it returns <tt>false</tt>.
	 *
	 * @return <tt>true</tt> if the <tt>reset</tt> operation is supported by
	 *         the cursor, otherwise <tt>false</tt>.
	 */
	 boolean supportsReset();

}
