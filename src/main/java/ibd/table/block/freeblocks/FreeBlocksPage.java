/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.block.freeblocks;

import ibd.table.block.PersistentBlock;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Sergio
 */
public class FreeBlocksPage extends PersistentBlock {

    int nextBlockID = -1;
    ArrayList<FreeBlock> freeBlocks = new ArrayList();

    public FreeBlocksPage(int pageSize) {
        super(pageSize);
    }

    private int getSpaceLeft() {
        return pageSize - 20 - (freeBlocks.size() * 8);
    }

    

    public boolean addFreeBlock(FreeBlock fb) {
        if (getSpaceLeft() < 8) {
            return false;
        }

        freeBlocks.add(fb);
        return true;
    }

    @Override
    public void writeExternal(DataOutput out) throws IOException {

        super.writeExternal(out); //8
        out.writeInt(nextBlockID); //4

        out.writeInt(freeBlocks.size()); //4
        for (int i = 0; i < freeBlocks.size(); i++) {
            FreeBlock entry = freeBlocks.get(i);
            out.writeInt(entry.getBlockId());
            out.writeInt(entry.getLen());
        }

    }

    @Override
    public void readExternal(DataInput in) throws IOException {

        super.readExternal(in);

        this.nextBlockID = in.readInt();
        
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            int bid = in.readInt();
            int len = in.readInt();
            FreeBlock fb = new FreeBlock(bid);
            fb.setLen(len);
            freeBlocks.add(fb);
        }
    }

}
