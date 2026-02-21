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
 * This class defines some general methods of a PageFile related to page ID control and page serialization: . 
 * 
 * @author Elke Achtert - Adapted by Sergio Mergen
 * @since 0.1
 * 
 * @param <P> Page type
 */
public abstract class AbstractStoringPageFile<P extends Page> extends PageFile {
  /**
   * A stack holding the empty page ids.
   */
  protected IntegerArray emptyPages;

  /**
   * The last page ID.
   */
  protected int nextPageID;

  /**
   * The size of a page in Bytes.
   */
  protected int pageSize;

  /**
   * The class that knows how to serialize pages to this page file.
   */
  PageSerialization pageSerialization;
  
  /**
     * The header of this page file.
     */
    protected PageHeader header;
  
  
  /**
   * Creates a new PageFile.
   */
  protected AbstractStoringPageFile(int pageSize) {
    this.emptyPages = new IntegerArray();
    this.nextPageID = 0;
    this.pageSize = pageSize;
  }

  
    
    public void setPageSerialization(PageSerialization pageSerialization){
      this.pageSerialization = pageSerialization;
  }
    
    /**
     * Get the header of this page file.
     *
     * @return the header used by this page file
     */
    @Override
    public PageHeader getHeader() {
        return header;
    }
  
  @Override
  public void reset(){
    this.emptyPages = new IntegerArray();
    this.nextPageID = 0;
  }
  
  
  /**
   * Sets the id of the given page.
   * 
   * @param page the page to set the id
   */
  @Override
  public int setPageID(Page page) {
    int pageID = page.getPageID();
    if(pageID == -1) {
      pageID = getNextEmptyPageID();
      if(pageID == -1) {
        pageID = nextPageID++;
      }
      page.setPageID(pageID);
      //System.out.println("new page id:"+pageID);
    }
    else {
      if(pageID >= nextPageID) {
        for(int i = nextPageID; i < pageID; i++) {
          emptyPages.add(i);
        }
        nextPageID = pageID + 1;
      }
    }
    return pageID;
  }

  /**
   * Deletes the node with the specified id from this file.
   * 
   * @param pageID the id of the node to be deleted
   */
  @Override
  public void deletePage(int pageID) {
    // put id to empty nodes
    emptyPages.add(pageID);
  }

  /**
   * Returns the next empty page id.
   * 
   * @return the next empty page id
   */
  private int getNextEmptyPageID() {
    return emptyPages.isEmpty() ? -1 : emptyPages.get(--emptyPages.size);
  }

  /**
   * Returns the next page id.
   * 
   * @return the next page id
   */
  @Override
  public int getNextPageID() {
    return nextPageID;
  }

  /**
   * Sets the next page id.
   * 
   * @param nextPageID the next page id to be set
   */
  @Override
  public void setNextPageID(int nextPageID) {
    this.nextPageID = nextPageID;
  }

  /**
   * Get the page size of this page file.
   * 
   * @return page size
   */
  @Override
  public int getPageSize() {
    return pageSize;
  }

}
