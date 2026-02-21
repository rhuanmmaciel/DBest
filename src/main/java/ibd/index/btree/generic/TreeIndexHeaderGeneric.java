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
package ibd.index.btree.generic;

import ibd.index.btree.TreeIndexHeader;
import java.nio.ByteBuffer;


/**
 * Encapsulates the header information for a B+ tree index structure.
 * The key and value information are structured based on primitive data types.
 *
 * @author Elke Achtert - Adapted by Sergio Mergen
 * @since 0.1
 */
public class TreeIndexHeaderGeneric extends TreeIndexHeader {

    /**
     * The size of this header in Bytes, which is 6 Integer attibutes: 
     * dirCapacity, leafCapacity, rootID, firstLeafID, number of key field columns and number of value field columns
     */
    private static int SIZE = 6 * Integer.BYTES;

    
    /**
     * The schema of the key fields
     */
    RowSchema keySchema;
    
    /**
     * The schema of the value fields
     */
    RowSchema valueSchema;


    /**
     * Empty constructor for serialization.
     * @param pageSize the size of a page in bytes
     */
    public TreeIndexHeaderGeneric(int pageSize) {
        super(pageSize);
    }

    /**
     * Creates a new header with the specified parameters.
     *
     * @param pageSize the size of a page in bytes
     * @param dirCapacity the maximum number of entries in a directory node
     * @param leafCapacity the maximum number of entries in a leaf node
     * @param rootID the page id of the root node
     * @param firstLeafID the page id of the left most leaf node
     * @param keySchema the schema of the key fields
     * @param valueSchema the schema of the value fields 
     */
    public TreeIndexHeaderGeneric(int pageSize, int dirCapacity, int leafCapacity, int rootID, int firstLeafID, RowSchema keySchema, RowSchema valueSchema) {
        super(pageSize, rootID, firstLeafID);
        this.keySchema = keySchema;
        this.valueSchema = valueSchema;
    }

    /**
     * Reads the header attributes from the specified file. 
     */
    @Override
    public void readHeader(ByteBuffer buffer) {
        super.readHeader(buffer);
        
        keySchema = new RowSchema(buffer.getInt());
        for (int i = 0; i < keySchema.getSize(); i++) {
            char type = buffer.getChar();
            int size = buffer.getInt();
            if (type!='K')
                keySchema.addDataType(type);
            else {
                keySchema.addBigKeyDataType(size);
            }
        }
        valueSchema = new RowSchema(buffer.getInt());
        for (int i = 0; i < valueSchema.getSize(); i++) {
            char type = buffer.getChar();
            int size = buffer.getInt();
            if (type!='R')
                valueSchema.addDataType(type);
            else {
                valueSchema.addRecordDataType(size);
            }
        }

    }

    /**
     * Writes this header attributes to the specified file. 
     */
    @Override
    public void writeHeader(ByteBuffer buffer) {
        super.writeHeader(buffer);
        
        buffer.putInt(keySchema.getSize());//5
        for (int i = 0; i < keySchema.getSize(); i++) {
            char type = keySchema.get(i);
            buffer.putChar(type);
            buffer.putInt(keySchema.getSize(i));
                
        }
        buffer.putInt(valueSchema.getSize());//6
        for (int i = 0; i < valueSchema.getSize(); i++) {
            char type = valueSchema.get(i);
            buffer.putChar(type);
            buffer.putInt(valueSchema.getSize(i));
        }
        //buffer.flip();
    }


    public RowSchema getKeySchema() {
        return keySchema;
    }
    
    public RowSchema getValueSchema() {
        return valueSchema;
    }

    /**
     * Returns the size of the header considering all attributes that needs saving. Note, this is only the base size and probably
     * <em>not</em> the overall size of this header, as there may be empty pages
     * to be maintained.
     */
    @Override
    public int size() {
        //each key or value columns takes a single character to represent its data type. 
        return super.size() + SIZE + keySchema.getSize()* (Character.BYTES+Integer.BYTES) + valueSchema.getSize()*(Character.BYTES + Integer.BYTES);
    }

}
