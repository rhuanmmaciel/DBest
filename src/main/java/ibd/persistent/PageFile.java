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

/**
 * Abstract base class for the page file API for both caches and true page files
 * (in-memory and on-disk).
 * 
 * @author Elke Achtert - Adapted by Sergio Mergen
 * @since 0.4.0
 * 
 * @param <P> page type
 */
public abstract class PageFile<P extends Page> {
  
    
    public abstract void setPageSerialization(PageSerialization pageSerialization);
    
   /**
   * Sets the id of the given page.
   * 
   * @param page the page to set the id
   * @return the page id
   */
  public abstract int setPageID(P page);

  

  /**
   * Reads the page with the given id from this file.
   * 
   * @param pageID the id of the page to be returned
   * @return the page with the given pageId
   */
  public abstract P readPage(int pageID);

  /**
   * Deletes the node with the specified id from this file.
   * 
   * @param pageID the id of the node to be deleted
   */
  public abstract void deletePage(int pageID);

  

  /**
   * Clears this PageFile.
   */
  public abstract void clear();
  
  /**
   * Resets this PageFile.
   */
  public abstract void reset();
  
  /**
   * Flushes the contents of this PageFile.
   */
  public abstract void flush();

  /**
   * Returns the next page id.
   * 
   * @return the next page id
   */
  public abstract int getNextPageID();

  /**
   * Sets the next page id.
   * 
   * @param nextPageID the next page id to be set
   */
  public abstract void setNextPageID(int nextPageID);

  /**
   * Get the page size of this page file.
   * 
   * @return page size
   */
  public abstract int getPageSize();

  /**
   * Initialize the page file with the given header - return "true" if the file
   * already existed.
   * 
   * @param header Header
   * @return true when the file already existed.
   */
  public abstract boolean initialize(PageHeader header);

  /**
   * Returns the page header.
   */
  public abstract PageHeader getHeader();

/**
   * Writes a page into this file. The method tests if the page has already an
   * id, otherwise a new id is assigned and returned.
   * 
   * @param page the page to be written
   * @return the id of the page
   */
  public final synchronized int writePage(P page) {
    int pageid = setPageID(page);
    writePage(pageid, page);
    //System.out.println("page id: "+pageid);
    return pageid;
  }

  /**
   * Perform the actual page write operation.
   * 
   * @param pageid Page id
   * @param page Page to write
   */
  protected abstract void writePage(int pageid, P page);
  
   /**
   * Reopens the file, just to make sure that the internal variables are still valid
   */
  public abstract void reopen() throws Exception;  
  
  

  /**
   * Closes this file.
   */
  public void close() {
    clear();
  }  
  
}
