/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.block.freeblocks;

/**
 *
 * @author pccli
 */
public class FreeBlock implements Comparable<FreeBlock> {

    int blockId;
    int len;

    public FreeBlock(Integer bid) {
        blockId = bid;
        len = 0;
    }

    public int getBlockId() {
        return blockId;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int l) {
        this.len = l;
    }

    @Override
    public boolean equals(Object fb) {
        if (fb instanceof FreeBlock) {
            return this.blockId == ((FreeBlock) fb).blockId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.blockId;
        return hash;
    }

    @Override
    public int compareTo(FreeBlock o) {
        int res = Integer.compare(len, o.len);
        if (res != 0) {
            return res;
        }
        return Integer.compare(blockId, o.blockId);
    }

}
