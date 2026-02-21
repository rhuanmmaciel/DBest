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

import java.util.HashMap;


/**
 * A memory based implementation of a PageFile that simulates I/O-access.
 * Implemented as a Map with keys representing the ids of the saved pages.
 *
 * @author Elke Achtert - Adapted by Sergio Mergen
 * @since 0.1
 *
 * @param <P> Page type
 */
public class MemoryPageFile extends AbstractStoringPageFile {

  /**
   * Holds the pages.
   */
  private final HashMap<Integer, Page> file;
  

  /**
   * Creates a new MemoryPageFile that is supported by a cache with the
   * specified parameters.
   *
   * @param pageSize the size of a page in Bytes
   */
  public MemoryPageFile(int pageSize) {
    super(pageSize);
    this.file = new HashMap<>();
  }

  @Override
  public synchronized Page readPage(int pageID) {
    return file.get(pageID);
  }

  @Override
  protected void writePage(int pageID, Page page) {
    file.put(pageID, page);
    page.setDirty(false);
  }

  @Override
  public synchronized void deletePage(int pageID) {
    // put id to empty nodes and
    // delete from cache
    super.deletePage(pageID);

    // delete from file
    file.remove(pageID);
  }

  @Override
  public void clear() {
    file.clear();
  }

    @Override
    public void flush() {
        
    }
    
    @Override
    public void reopen() {
        
    }

    @Override
    public boolean initialize(PageHeader header) {
        this.header = header;
        return true;
    }


}
