/*
 * This file is part of ELKI:
 * Environment for Developing KDD-Applications Supported by Index-Structures
 *
 * Copyright (C) 2022
 * ELKI Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ibd.index.btree.table;

import ibd.index.btree.TreeIndexHeader;
import java.nio.ByteBuffer;

import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.BooleanColumn;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.column.DoubleColumn;
import ibd.table.prototype.column.FloatColumn;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.LongColumn;
import ibd.table.prototype.column.StringColumn;

/**
 * Encapsulates the header information for a B+ tree index structure. The key
 * and value information are structured based on a table schema.
 *
 * @author Elke Achtert - Adapted by Sergio Mergen
 * @since 0.1
 */
public class TreeIndexHeaderTable extends TreeIndexHeader {

    /**
     * The schema of the table
     */
    Prototype prototype;

    /**
     * Creates a new header with the specified parameters.
     *
     * @param pageSize the size of a page in bytes
     * @param rootID the page id of the root node
     * @param firstLeafID the page id of the left most leaf node
     * @param prototype the schema of the table
     */
    public TreeIndexHeaderTable(int pageSize, int rootID, int firstLeafID, Prototype prototype) {
        super(pageSize, rootID, firstLeafID);
        this.prototype = prototype;
    }

    /**
     * Reads the header attributes from the specified file.
     * @param buffer the content of the header
     */
    @Override
    public void readHeader(ByteBuffer buffer) {
        super.readHeader(buffer);

        prototype = new Prototype();

        int numberOfColumns = buffer.getShort();
        for (int i = 0; i < numberOfColumns; i++) {
            String colType = readString(buffer);
            String colName = readString(buffer);
            int colSize = buffer.getInt();
            short colFlags = buffer.getShort();
            Column col = null;
            switch (colType) {
                case Column.STRING_TYPE:
                    col = new StringColumn(colName, colSize, colFlags);
                    break;
                case Column.INTEGER_TYPE:
                    col = new IntegerColumn(colName, colSize, colFlags);
                    break;
                case Column.LONG_TYPE:
                    col = new LongColumn(colName, colSize, colFlags);
                    break;
                case Column.FLOAT_TYPE:
                    col = new FloatColumn(colName, colSize, colFlags);
                    break;
                case Column.DOUBLE_TYPE:
                    col = new DoubleColumn(colName, colSize, colFlags);
                    break;
                case Column.BOOLEAN_TYPE:
                    col = new BooleanColumn(colName, colSize, colFlags);
                    break;
            }
            prototype.addColumn(col);
        }

    }

    private String readString(ByteBuffer buffer) {
        int stringLength = buffer.getInt();
        byte[] bytes = new byte[stringLength];
        buffer.get(bytes);
        return new String(bytes);
    }

    private void writeString(ByteBuffer buffer, String str) {

        buffer.putInt(str.length());
        buffer.put(str.getBytes());
    }

    /**
     * Writes this header attributes to the specified file.
     * @param buffer the buffer where the header attributes are written to
     */
    @Override
    public void writeHeader(ByteBuffer buffer) {
        super.writeHeader(buffer);

        buffer.putShort(prototype.size());
        for (int i = 0; i < prototype.size(); i++) {
            Column col = prototype.getColumn(i);
            writeString(buffer, col.getType());
            writeString(buffer, col.getName());
            buffer.putInt(col.getSize());
            buffer.putShort(col.getFlags());
        }

        //buffer.flip();
    }

    /**
     * Returns the size of the header considering all attributes that needs
     * saving. Note, this is only the base size and probably
    <em>not</em> the overall size of this header, as there may be empty pages
 to be maintained.
     * @return the size of the header
     */
    @Override
    public int size() {
        //each key or value columns takes a single character to represent its data type. 
        //return super.size() + SIZE + prototype.getSizeInBytes();
        
        return super.size() + 2000;
    }

}
