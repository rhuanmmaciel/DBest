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
package ibd.index.btree;

import java.nio.ByteBuffer;

import ibd.persistent.PageHeader;

/**
 * Encapsulates the header information of a tree-like index structure. This
 * information is needed for persistent storage.
 *
 * @author Elke Achtert - Adapted by Sergio Mergen
 * @since 0.1
 */
public abstract class TreeIndexHeader extends PageHeader {

    /**
     * The size of this header in Bytes, which is 6 Integer attibutes: 
     * rootID, firstLeafID
     */
    private static int SIZE = 2 * Integer.BYTES;


    /**
     * The page ID of the root node
     */
    private int rootID = 0;

    /**
     * The page ID of the left most leaf node
     */
    private int firstLeafID = 0;

    /**
     * Empty constructor for serialization.
     */
    public TreeIndexHeader(int pageSize) {
        super(pageSize);
    }

    /**
     * Creates a new header with the specified parameters.
     *
     * @param pageSize the size of a page in bytes
     * @param rootID the page id of the root node
     * @param firstLeafID the page id of the left most leaf node
     */
    public TreeIndexHeader(int pageSize, int rootID, int firstLeafID) {
        super(pageSize);
        this.rootID = rootID;
        this.firstLeafID = firstLeafID;
    }

    /**
     * Reads the header attributes from the specified file. 
     * @param buffer the content of the header
     */
    @Override
    public void readHeader(ByteBuffer buffer) {
        super.readHeader(buffer);
        this.rootID = buffer.getInt();
        this.firstLeafID = buffer.getInt();

    }

    /**
     * Writes this header attributes to the specified file. 
     */
    @Override
    public void writeHeader(ByteBuffer buffer) {
        super.writeHeader(buffer);
        buffer.putInt(this.rootID); //1
        buffer.putInt(this.firstLeafID);//2
    }
   

    public int getRootID() {
        return rootID;
    }

    public int getFirstLeafID() {
        return firstLeafID;
    }

    public void setRootID(int rootID) {
        this.rootID = rootID;
    }

    public void setFirstLeafID(int firstLeafID) {
        this.firstLeafID = firstLeafID;
    }

    /**
     * Returns the size of the header considering all attributes that needs saving. Note, this is only the base size and probably
     * <em>not</em> the overall size of this header, as there may be empty pages
     * to be maintained.
     */
    @Override
    public int size() {
        //each key or value columns takes a single character to represent its data type. 
        return super.size() + SIZE;
    }

}
