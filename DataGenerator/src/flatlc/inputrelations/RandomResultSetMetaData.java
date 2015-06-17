package flatlc.inputrelations;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Simple implementation of a {@link ResultSetMetaData} for use in algorithm
 * tests.
 *
 * @author Timotheus Preisinger
 * @author endresma
 */
public class RandomResultSetMetaData implements ResultSetMetaData, Serializable {
    String[] columns;

    public RandomResultSetMetaData(int[] maxValues) {
        this(maxValues.length);
    }

    public RandomResultSetMetaData(int columnCount) {
        columns = new String[columnCount + 1];
        for (int i = columnCount; --i >= 0; ) {
            columns[i] = new StringBuffer(8).append("myCol").append(i)
                    .toString();
        }
        columns[columnCount] = "id";
    }

    public String getCatalogName(int column) {
        return null;
    }

    public String getColumnClassName(int column) {
        return "int";
    }

    public int getColumnCount() {
        return columns.length + 1;
    }

    public int getColumnDisplaySize(int column) {
        return 6;
    }

    public String getColumnLabel(int column) {
        return columns[column];
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getColumnType(int column) {
        return java.sql.Types.INTEGER;
    }

    public String getColumnTypeName(int column) {
        return "int";
    }

    public int getPrecision(int column) {
        return 0;
    }

    public int getScale(int column) {
        return 0;
    }

    public String getSchemaName(int column) {
        return "randomSchema";
    }

    public String getTableName(int column) {
        return "randomTable";
    }

    public boolean isAutoIncrement(int column) {
        return column == 1;
    }

    public boolean isCaseSensitive(int column) {
        return false;
    }

    public boolean isCurrency(int column) {
        return false;
    }

    public boolean isDefinitelyWritable(int column) {
        return false;
    }

    public int isNullable(int column) {
        return ResultSetMetaData.columnNoNulls;
    }

    public boolean isReadOnly(int column) {
        return true;
    }

    public boolean isSearchable(int column) {
        return false;
    }

    public boolean isSigned(int column) {
        return true;
    }

    public boolean isWritable(int column) {
        return false;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }
}