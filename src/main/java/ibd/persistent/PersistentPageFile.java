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

import ibd.table.Params;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * A PersistentPageFile stores pages persistently (in secondary storage).
 *
 * @author Elke Achtert - Adapted by Sergio Mergen
 * @since 0.1
 *
 * @composed - - - PageHeader
 * @composed - - - RandomAccessFile
 *
 * @param <P> Page type
 */
public class PersistentPageFile<P extends Page> extends AbstractStoringPageFile {

    /**
     * The file storing the pages.
     */
    protected  FileChannel file;

    

    /**
     * Whether we are initializing from an existing file.
     */
    private boolean existed;

    Path fileName;
        
  
    
    /**
     * Creates a new PersistentPageFile from an existing file.
     *
     * @param pageSize the page size
     */
    public PersistentPageFile(int pageSize, Path filename, boolean recreate) throws IOException {
        super(pageSize);
        this.fileName = filename;
        
        // create from existing file
        if (recreate) {
            File f = filename.toFile();
            f.delete();
        }

        existed = Files.exists(filename);

        file = FileChannel.open(filename, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);

    }

    @Override
    public void reopen() throws Exception {
        file = FileChannel.open(fileName, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
    }
    
    /**
     * Reads the page with the given id from this file.
     *
     * @param pageID the id of the page to be returned
     * @return the page with the given pageId
     */
    @Override
    public Page readPage(int pageID) {
        try {
            //Params.BLOCKS_LOADED++;
            engine.info.Parameters.BLOCKS_LOADED++;
            long offset = ((long) (header.getReservedPages() + pageID)) * (long) pageSize;
            byte[] buffer = new byte[pageSize];
            int read = file.read(ByteBuffer.wrap(buffer), offset);
            if (read != pageSize) {
                throw new IOException("Incomplete read at offset " + offset + " read " + read + " bytes, expected " + pageSize);
            }
            return byteArrayToPage(buffer);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred during reading of page " + pageID + "\n", e);
        }
    }

    /**
     * Deletes the node with the specified id from this file.
     *
     * @param pageID the id of the node to be deleted
     */
    @Override
    public void deletePage(int pageID) {
        try {
            // / put id to empty pages list
            super.deletePage(pageID);

            // delete from file
            byte[] array = pageToByteArray(null);
            long offset = (header.getReservedPages() + pageID) * (long) pageSize;
            int written = file.write(ByteBuffer.wrap(array), offset);
            if (written != pageSize) {
                throw new IOException("Incomplete write at offset " + offset + " wrote " + written + " bytes, expected " + array.length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called by the cache if the <code>page</code> is not longer
     * stored in the cache and has to be written to disk.
     *
     * @param page the page which has to be written to disk
     */
    @Override
    public void writePage(int pageID, Page page) {
        //System.out.println("teste");
        try {
            Params.BLOCKS_SAVED++;
            
            byte[] array = pageToByteArray(page);
            long offset = ((long) (header.getReservedPages() + pageID)) * (long) pageSize;
            assert offset >= 0 : header.getReservedPages() + " " + pageID + " " + pageSize + " " + offset;
            int written = file.write(ByteBuffer.wrap(array), offset);
            if (written != pageSize) {
                throw new IOException("Incomplete write at offset " + offset + " wrote " + written + " bytes, expected " + array.length);
            }
            page.setDirty(false);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to page file.", e);
        }
    }

    /**
     * Closes this file.
     */
    @Override
    public void close() {
        try {
            flush();
            super.close();
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Clears this PageFile.
     */
    @Override
    public void clear() {
//    try {
//      file.truncate(header.size());
//    }
//    catch(IOException e) {
//      throw new RuntimeException(e);
//    }
    }

    @Override
    public void reset() {
        super.reset();
        existed = false;
        //file.truncate(0);

    }

    /**
     * Reconstruct a serialized object from the specified byte array.
     *
     * @param array the byte array from which the object should be reconstructed
     * @return a serialized object from the specified byte array
     */
    private Page byteArrayToPage(byte[] array) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(array);
        DataInputStream ois = new DataInputStream(bais);
        return pageSerialization.readPage(ois);

    }

    /**
     * Serializes an object into a byte array.
     *
     * @param page the object to be serialized
     * @return the byte array
     */
    private byte[] pageToByteArray(Page page) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream oos = new DataOutputStream(baos);
            pageSerialization.writePage(oos, page);
            oos.close();
            baos.close();
            byte[] array = baos.toByteArray();

            if (array.length > this.pageSize) {
                throw new IllegalArgumentException("Size of page " + page + " is greater than specified" + " pagesize: " + array.length + " > " + pageSize);
            } else if (array.length == this.pageSize) {
                return array;
            } else {
                byte[] result = new byte[pageSize];
                System.arraycopy(array, 0, result, 0, array.length);
                return result;
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred! ", e);
        }
    }

    /**
     * @return the random access file storing the pages.
     */
    public FileChannel getFile() {
        return file;
    }

    

    /**
     * Set the next page id to the given value. If this means that any page ids
     * stored in <code>emptyPages</code> are smaller than
     * <code>next_page_id</code>, they are removed from this file's observation
     * stack.
     *
     * @param next_page_id the id of the next page to be inserted (if there are
     * no more empty pages to be filled)
     */
    @Override
    public void setNextPageID(int next_page_id) {
        this.nextPageID = next_page_id;
        while (!emptyPages.isEmpty() && emptyPages.get(emptyPages.size - 1) >= this.nextPageID) {
            --emptyPages.size;
        }
    }

    //protected abstract boolean isPageEmpty(ObjectInputStream ois) throws IOException;
    @Override
    public boolean initialize(PageHeader header) {
        try {
            if (existed) {
                // init the header
                this.header = header;

                header.readHeader(file);

                // reading empty nodes in Stack
                nextPageID = header.getLargestPageID();
                emptyPages = header.readEmptyPages(file);
                //headerListener.initializeFromFile(header);

            } // create new file
            else {
                // writing header
                this.header = header;
                header.writeHeader(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred.", e);
        }
        pageSize = header.getPageSize();
        // Return "new file" status
        return existed;
    }

    /*
    private void readWithoutHeader() throws IOException {
        int i = 0;
        while (file.position() + pageSize <= file.size()) {
            long offset = ((long) (header.getReservedPages() + i)) * (long) pageSize;
            byte[] buffer = new byte[pageSize];
            if (file.read(ByteBuffer.wrap(buffer), offset) != pageSize) {
                throw new IOException("Incomplete read at position " + offset);
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);
            if (isPageEmpty(ois)) {
                emptyPages.add(i);
            } else //if(type == FILLED_PAGE) 
            {
                nextPageID = i + 1;
            }
            //            else {
            //              throw new IllegalArgumentException("Unknown type: " + type);
            //            }
            i++;
        } // must scan complete file
    }
     */
    @Override
    public void flush() {
        try {
            //headerListener.updateHeader(header);
            //if (!emptyPages.isEmpty()) 
            {
                // write the list of empty pages to the end of the file
                header.writeEmptyPages(emptyPages, file);
            }
            header.setLargestPageID(nextPageID);
            header.writeHeader(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
