
package ibd.index.btree;

import ibd.index.btree.generic.RowSchema;
import ibd.index.btree.generic.TreeIndexHeaderGeneric;
import java.nio.ByteBuffer;


/**
 * Encapsulates the header information of a tree-like index structure. This
 * information is needed for persistent storage.
 * 
 * @author Elke Achtert
 * @since 0.1
 */
/**
 * Encapsulates the header information of a tree-like index structure. This
 * information is needed for persistent storage.
 * 
 * @author Elke Achtert
 * @since 0.1
 */
public class BTreeIndexHeader extends TreeIndexHeaderGeneric {
  
  private int SIZE;
 
  private int[] outerSeqLevel;

  /**
   * Empty constructor for serialization.
   */
  public BTreeIndexHeader(int pageSize) {
    super(pageSize);
  }

    public BTreeIndexHeader(int pageSize, int dirCapacity, int leafCapacity, int rootID, int firstLeafID, RowSchema keySchema, RowSchema valueSchema, int levels) {
    super(pageSize, dirCapacity, leafCapacity, rootID, firstLeafID, keySchema, valueSchema);
    this.SIZE = levels * 4;
    outerSeqLevel = new int[levels];
      for (int i = 0; i < outerSeqLevel.length; i++) {
          outerSeqLevel[i] = 0;
      }
  }

  
  @Override
  public void readHeader(ByteBuffer buffer) {
    super.readHeader(buffer);
    
      for (int i = 0; i < outerSeqLevel.length; i++) {
          outerSeqLevel[i] = buffer.getInt();
      }
  }

   @Override
  public void writeHeader(ByteBuffer buffer) {
    super.writeHeader(buffer);
          
        for (int i = 0; i < outerSeqLevel.length; i++) {
              buffer.putInt(outerSeqLevel[i]);
          }
        buffer.flip();
  }

  
  
  public void setOuterSeqLevel(int level, int value){
      outerSeqLevel[level] = value;
  }
  
  public int getOuterSeqLevel(int level){
      return outerSeqLevel[level];
  }

  
  public int size() {
    return super.size() + SIZE;
  }

  

}
