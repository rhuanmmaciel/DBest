/*
 * This file is adapted from ELKI:
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
package ibd.persistent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * Defines the requirements for a header of a page file. A header must at least
 * store the size of a page in Bytes. The header is more useful for persistent
 * files
 *
 * @author Elke Achtert - Adapted by Sergio Mergen
 * @since 0.1
 */
public class PageHeader {

    /**
     * The size of this header in Bytes, which is four integer attributes: 
     * {@link #FILE_VERSION}, {@link #pageSize}, {@link #pageSize}, 
   * {@link #emptyPagesSize} and {@link #largestPageID}).
     */
    private static final int SIZE = 4 * Integer.BYTES;

    /**
     * Version number of this header (magic number).
     */
    private static final int FILE_VERSION = 841150978;

    /**
     * The size of a page in bytes.
     */
    private int pageSize = -1;

    /**
     * The number of bytes additionally needed for the listing of empty pages of
     * the headed page file.
     */
    private int emptyPagesSize = 0;

    /**
     * The largest ID used so far
     */
    private int largestPageID = 0;

    /**
     * Creates a new header with the specified parameters.
     *
     * @param pageSize the size of a page in bytes
     */
    public PageHeader(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Read the header from an input file.
     *
     * @param file File to read from
     * @throws IOException
     */
    public void readHeader(FileChannel file) throws IOException {
        readHeader(file.map(MapMode.READ_ONLY, 0, size()));
    }

    /**
     * Read the header from an input file.
     *
     * @param file File to read from
     * @throws IOException
     */
    public void writeHeader(FileChannel file) throws IOException {
        writeHeader(file.map(MapMode.READ_WRITE, 0, size()));
    }

    /**
     * Returns the size of this header in Bytes.
     *
     * @return the size of this header in Bytes
     */
    public int size() {
        return SIZE;
    }

    /**
     * @return the number of bytes needed for the listing of empty pages
     */
    public int getEmptyPagesSize() {
        return emptyPagesSize;
    }

    /**
     * Set the size required by the listing of empty pages.
     *
     * @param emptyPagesSize the number of bytes needed for this listing of
     * empty pages
     */
    public void setEmptyPagesSize(int emptyPagesSize) {
        this.emptyPagesSize = emptyPagesSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Returns the largest page ID assigned
     *
     * @return the largest page ID assigned
     */
    public int getLargestPageID() {
        return largestPageID;
    }

    /**
     * Sets the largest page ID assigned
     */
    public void setLargestPageID(int largestPageID) {
        this.largestPageID = largestPageID;
    }

    /**
     * Reads the header attributes from the given Byte array. Looks for the right
     * version and reads the integer value of {@link #pageSize} from the file.
     *
     * @param data byte array with the page data.
     */
    public void readHeader(ByteBuffer data) {
        if (data.getInt() != FILE_VERSION) {
            throw new RuntimeException("PersistentPageFile version does not match!");
        }
        this.pageSize = data.getInt();
        this.emptyPagesSize = data.getInt();
        this.largestPageID = data.getInt();
    }

    /**
     * Writes this header attributes to the specified file. 
     *
     * @param buffer Buffer to write to
     * @throws IOException IOException if an I/O-error occurs during writing
     */
    public void writeHeader(ByteBuffer buffer) {
        buffer.putInt(FILE_VERSION) //
                .putInt(pageSize)
                .putInt(this.emptyPagesSize) //
                .putInt(this.largestPageID);
    }

    /**
     * @return the size of a page in Bytes
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @return the number of pages necessary for the header
     */
    public int getReservedPages() {
        return size() / getPageSize() + 1;
    }

    /**
     * Write the indices of empty pages to the end of <code>file</code>.
     * Calling this method should be followed by a
     * {@link #writeHeader(FileChannel)}.
     *
     * @param emptyPages the stack of empty page ids which remain to be filled
     * @param file File to work with
     * @throws IOException thrown on IO errors
     */
    public void writeEmptyPages(IntegerArray emptyPages, FileChannel file) throws IOException {
        if (emptyPages.isEmpty()) {
            this.emptyPagesSize = 0;
            return; // nothing to write
        }
        emptyPages.sort();
        this.emptyPagesSize = emptyPages.size * Integer.BYTES;
        ByteBuffer buf = ByteBuffer.allocateDirect(this.emptyPagesSize);
        buf.asIntBuffer().put(emptyPages.data, 0, emptyPages.size);
        file.write(buf, file.size());
    }

    /**
     * Read the empty pages from the end of <code>file</code>.
     *
     * @param file File to work with
     * @return a stack of empty pages in <code>file</code>
     * @throws IOException thrown on IO errors
     * @throws ClassNotFoundException if the stack of empty pages could not be
     * correctly read from file
     */
    public IntegerArray readEmptyPages(FileChannel file) throws IOException {
        IntegerArray emptyPages = new IntegerArray();
        if (emptyPagesSize > 0) {
            int n = emptyPagesSize / Integer.BYTES;
            if (n > emptyPages.data.length) {
                emptyPages.data = new int[n];
            }
            ByteBuffer buf = ByteBuffer.allocateDirect(emptyPagesSize);
            //ByteBuffer buf = ByteBuffer.wrap(new byte[emptyPagesSize]);
            file.read(buf, file.size() - emptyPagesSize);
            //System.out.println(buf.limit());
            //System.out.println(buf.position());
            buf.position(0);

            for (int i = 0; i < n; i++) {
                emptyPages.data[i] = buf.getInt();
            }
            //buf.asIntBuffer().get(emptyPages.data, 0, n);
        }
        return emptyPages;
    }

}
