/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.block.management;

import ibd.table.block.Block;
import ibd.table.block.ChainedBlocksTable1;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.Header;
import ibd.table.prototype.LinkedDataRow;

public class HeapFile1 extends ChainedBlocksTable1 {

    public HeapFile1(Header header, String folder, String name) throws Exception {
        super(header, folder, name);
    }

    @Override
    protected LinkedDataRow addRecord1(BasicDataRow record) throws Exception {

        Block b = findFirstFittingBlock(record);
        if (b == null) {
            b = addBlock();
            setFirstHeapBlock(b.getPageID());
            setLastHeapBlock(b.getPageID());

        }

        LinkedDataRow rec = addRecord(b, record);
        
        if (!b.fits(400)) {
            removeBlockFromHeap(b);
        }

        return rec;

    }

    @Override
    protected LinkedDataRow removeRecord(Block block, DataRow record) throws Exception {

        LinkedDataRow linkedRecord = super.removeRecord(block, record);

        if (linkedRecord == null) {
            return null;
        }

        if (block.getPageSize() - block.getUsedSpace() > 30) {
            addBlockToHeap(block);
        }
        return linkedRecord;

    }

    private void addBlockToHeap(Block b) throws Exception {

        if (b.prev_heap_block_id != -1) {
            return;
        }

        int firstId = getFirstHeapBlock();
        int lastId = getLastHeapBlock();
        int blockId = b.getPageID();

        if (firstId == -1) {
            setFirstHeapBlock(blockId);
            setLastHeapBlock(blockId);
        } else {

            Block last = getBlock(lastId);
            last.next_heap_block_id = blockId;
            dataFile.writePage(last);
            b.prev_heap_block_id = lastId;
            setLastHeapBlock(blockId);
        }

        dataFile.writePage(b);

    }

    private void removeBlockFromHeap(Block b) throws Exception {

        if (b.prev_heap_block_id == -1) {
            return;
        }

        int firstId = getFirstHeapBlock();
        int lastId = getLastHeapBlock();

        int prevId = b.prev_heap_block_id;
        int blockId = b.getPageID();
        int nextId = b.next_heap_block_id;

        if (firstId == blockId) {
            setFirstHeapBlock(nextId);
        } else {
            Block prev = getBlock(prevId);
            prev.next_heap_block_id = nextId;
            dataFile.writePage(prev);
        }
        if (lastId == blockId) {
            setLastHeapBlock(prevId);
        } else {
            Block next = getBlock(nextId);
            next.prev_heap_block_id = prevId;
            dataFile.writePage(next);
        }
        b.prev_heap_block_id = -1;
        b.next_heap_block_id = -1;

    }

    private Block findFirstFittingBlock(DataRow record) throws Exception {
        int blockId = getLastHeapBlock();
        while (blockId != -1) {
            Block b1 = getBlock(blockId);
            if (b1.fits(record.len())) {
                return b1;
            }
            blockId = b1.prev_heap_block_id;
        }
        return null;
    }

    
    

}
