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

package flatlc.inputrelations;

import flatlc.levels.FlatLCTuple;
import flatlc.levels.FlatLevelCombination;
import mdc.MetaDataCursor;

import java.sql.ResultSetMetaData;
import java.util.NoSuchElementException;

/**
 * Wrapper-Objekt fuer Cursor, die {@link FlatLevelCombination}-Instanzen
 * zurueckliefern. Zurueckgelieferte Objekte werden in ein {@link FlatLCTuple}
 * verpackt und zurueckgeliefert. Dadurch koennen die Tupel mit
 * Standard-Persistenz-Service-Objekten vom Typ
 * verarbeitet werden.
 * <p/>
 * <p>
 * (fuer Testzwecke geschrieben)
 * </p>
 *
 * @author Timotheus Preisinger
 * @author endresma
 */
public class FlatLCResultSetWrapper implements MetaDataCursor {
    MetaDataCursor input;
    ResultSetMetaData meta;
//    Iterator cursor;


    public FlatLCResultSetWrapper(MetaDataCursor input) {
        this.input = input;
        meta = (ResultSetMetaData) input.getMetaData();

//        cursor = null;
    }

//    public FlatLCResultSetWrapper(Iterator cursor, Object metaData) {
//        input = null;
////        this.cursor = cursor;
//        this.meta = (ResultSetMetaData) metaData;
//
//    }


    @Override
    public Object getMetaData() {
        return meta;
    }

    @Override
    public void close() {
        if (input != null)
            input.close();
    }

    @Override
    public boolean hasNext() throws IllegalStateException {
        if (input != null)
            return input.hasNext();
//        else return cursor.hasNext();

        return false;
    }

    @Override
    public Object next() throws IllegalStateException, NoSuchElementException {

        Object nxt = null;
        if (input != null) {
            nxt = input.next();
//        } else if (cursor != null) {
//            nxt = cursor.next();
        }
//        Object nxt = input.next();
        if (nxt instanceof FlatLevelCombination) {
            nxt = new FlatLCTuple((FlatLevelCombination) nxt);
        }
        return nxt;
    }

    @Override
    public void open() {
        if (input != null)
            input.open();
    }

    @Override
    public Object peek() throws IllegalStateException, NoSuchElementException,
            UnsupportedOperationException {
        Object peek = null;
        if (input != null) {
            peek = input.peek();
            if (peek instanceof FlatLevelCombination) {
                peek = new FlatLCTuple((FlatLevelCombination) peek);
            }
        }
//        } else {
//            // FIXME:
//            throw new UnsupportedOperationException("peek not allowed in " +
//                    "FlatLCResultSetWrapper");
//        }

        return peek;


    }

    @Override
    public void remove() throws IllegalStateException,
            UnsupportedOperationException {
        if (input != null)
            input.remove();
    }

    @Override
    public void reset() throws UnsupportedOperationException {
        if (input != null)
            input.reset();
//            // FIXME:
//        else throw new UnsupportedOperationException("reset not allowed in " +
//                "UnsupportedOperationException");
    }

    @Override
    public boolean supportsPeek() {
        if (input != null)
            return input.supportsPeek();

        return false;
    }

    @Override
    public boolean supportsRemove() {
        if (input != null)
            return input.supportsRemove();
        return false;
    }

    @Override
    public boolean supportsReset() {
        if (input != null)
            return input.supportsReset();
        return false;
    }

    @Override
    public boolean supportsUpdate() {
        if (input != null)
            return input.supportsUpdate();
        return false;
    }

    @Override
    public void update(Object object) throws IllegalStateException,
            UnsupportedOperationException {
        if (input != null)
            input.update(object);

    }
}
