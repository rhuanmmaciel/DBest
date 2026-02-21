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

/**
 * Records are placed at the first fitting block. To find a fitting block, the
 * chained list of blocks is traversed from the beggining. This implementation
 * dos not release blocks when they become empty. They remain as options to be
 * used when necessary. However, when new blocks are required, the file will
 * always be expanded, regardless of the existence of empty blocks.
 *
 *
 * @author Sergio
 */
public class HeapFile2 extends ChainedBlocksTable1 {

    public HeapFile2(Header header, String folder, String name) throws Exception {
        super(header, folder, name);
    }

    /**
     * Adds a record to the table. The record is added at the first fitting
     * block
     *
     * @param record
     * @return
     * @throws Exception
     */
    @Override
    protected LinkedDataRow addRecord1(BasicDataRow record) throws Exception {

        //finds first block with enough space for the record
        Block b = findFirstFittingBlock(record);
        if (b == null) {
            b = addBlock();
        }

        return addRecord(b, record);

    }

    /**
     * Traverses the chained list of blocks looking for the first one that fits
     * the records
     */
    private Block findFirstFittingBlock(DataRow record) throws Exception {
        int blockId = getFirstBlock();
        while (blockId != -1) {
            Block b1 = getBlock(blockId);
            if (b1.fits(record.len())) {
                return b1;
            }
            blockId = b1.next_block_id;
        }
        return null;
    }

    


}
