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

import java.io.DataInput;
import java.io.DataOutput;

import java.io.IOException;

/**
 * Abstract superclass for pages.
 * It implements ExternalizablePage so it knows how to serialize itself.
 * 
 * @author Elke Achtert - Adapted by Sergio Mergen
 * @since 0.1
 */
// todo elke revise comments
public abstract class AbstractExternalizablePage implements ExternalizablePage, Page {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 2;

  /**
   * The unique id if this page.
   */
  private int id;

  /**
   * The dirty flag of this page.
   */
  private transient boolean dirty;
  
  
  /**
   * The amount of bytes used.
   */
  protected int usedSpace;
  
  /**
   * Empty constructor for Externalizable interface.
   */
  public AbstractExternalizablePage() {
    super();
    this.id = -1;
    this.usedSpace = 0;
  }

  public int getUsedSpace() {
    return usedSpace;
  }
  
  /*
  * The fixed size of the page header. 
  */
  public int getHeaderSizeInBytes() {
    //page id  + usedSpace
    return 2 * Integer.BYTES;
  }
  
  
  /**
   * Returns the unique id of this Page.
   * 
   * @return the unique id of this Page
   */
  @Override
  public final int getPageID() {
    return id;
  }

  /**
   * Sets the unique id of this Page.
   * 
   * @param id the id to be set
   */
  @Override
  public final void setPageID(int id) {
    this.id = id;
  }

  /**
   * Returns true if this page is dirty, false otherwise.
   * 
   * @return true if this page is dirty, false otherwise
   */
  @Override
  public final boolean isDirty() {
    return dirty;
  }

  /**
   * Sets the dirty flag of this page.
   * 
   * @param dirty the dirty flag to be set
   */
  @Override
  public final void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  /**
   * The object implements the writeExternal method to save its contents by
   * calling the methods of DataOutput for its primitive values or calling the
   * writeObject method of ObjectOutput for objects, strings, and arrays.
   * 
   * @param out the stream to write the object to
   * @throws java.io.IOException Includes any I/O exceptions that may occur
   * @serialData Overriding methods should use this tag to describe the data
   *             layout of this Externalizable object. List the sequence of
   *             element types and, if possible, relate the element to a
   *             public/protected field and/or method of this Externalizable
   *             class.
   */
  @Override
  public void writeExternal(DataOutput out) throws IOException {
    out.writeInt(id);
    out.writeInt(usedSpace);
  }

  /**
   * The object implements the readExternal method to restore its contents by
   * calling the methods of DataInput for primitive types and readObject for
   * objects, strings and arrays. The readExternal method must read the values
   * in the same sequence and with the same types as were written by
   * writeExternal.
   * 
   * @param in the stream to read data from in order to restore the object
   * @throws java.io.IOException if I/O errors occur
   * @throws ClassNotFoundException If the class for an object being restored
   *         cannot be found.
   */
  @Override
  public void readExternal(DataInput in) throws IOException {
    id = in.readInt();
    usedSpace = in.readInt();
  }
  
  /*
  not used for pages
  */
  @Override
  public int getSizeInBytes(){
      return -1;
  }

  /**
   * Returns a string representation of the object.
   * 
   * @return a string representation of the object
   */
  @Override
  public String toString() {
    return Integer.toString(id);
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * 
   * @param o the object to be tested
   * @return true, if o is an AbstractNode and has the same id and the same
   *         entries as this node.
   */
  @Override
  public boolean equals(Object o) {
    return this == o || (o != null && getClass() == o.getClass() //
        && id == ((AbstractExternalizablePage) o).getPageID());
    //xxx id!=-1 recently added
  }

  /**
   * Returns as hash code value for this node the id of this node.
   * 
   * @return the id of this node
   */
  @Override
  public int hashCode() {
    return id;
  }
}
