/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.block.management;

import ibd.table.block.Block;
import ibd.table.block.ChainedBlocksTable1;
import ibd.table.prototype.BasicDataRow;
import java.util.ArrayList;
import java.util.Collections;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.Header;
import ibd.table.prototype.LinkedDataRow;

public class HalfFullOrderedFile extends ChainedBlocksTable1 {

    public HalfFullOrderedFile(Header header, String folder, String name) throws Exception {
        super(header, folder, name);
    }

    @Override
    protected LinkedDataRow addRecord1(BasicDataRow record) throws Exception {

        DataRow ir = blockIndex.getLargestSmallerKey(record);
        Block b;
        if (ir != null) {
            b = getBlock(ir.getInt("position"));
        } else {
            int fb = getFirstBlock();
            if (fb == -1) {
                b = createFirstBlock();
            } else {
                b = getBlock(fb);
            }
        }

        if (!b.fits(record.len())) {
            splitBlock(b, new ArrayList<>());
            if (b.maxPrimaryKey().compareTo(record)>0) {
                //blockId = b.getPageID();
            } else {
                b = getBlock(b.next_block_id);
            }
        }

        return addRecord(b, record);

    }

    boolean ignoreEmptyBlock = false;

    @Override
    protected LinkedDataRow removeRecord(Block block, DataRow record) throws Exception {

        LinkedDataRow linkedRecord = super.removeRecord(block, record);

        if (linkedRecord == null) {
            return null;
        }

        if (ignoreEmptyBlock) {
            return linkedRecord;
        }

        if (block.isEmpty()) {

            int prevId = block.prev_block_id;
            if (prevId != -1) {
                Block prevBlock = getBlock(prevId);
                prevBlock.next_block_id = block.next_block_id;
                dataFile.writePage(prevBlock);
            } else {
                setFirstBlock(block.next_block_id);
            }

            int nextId = block.next_block_id;
            if (nextId != -1) {
                Block nextBlock = getBlock(nextId);
                nextBlock.prev_block_id = block.prev_block_id;
                dataFile.writePage(nextBlock);
            }

            dataFile.deletePage(block.getPageID());
        }

        return linkedRecord;

    }

    
    private void splitBlock(Block b, ArrayList<DataRow> recordsToMove) throws Exception {

        DataRow[] records = b.getRecords();

        ignoreEmptyBlock = true;
        for (DataRow record : records) {
            recordsToMove.add(record);
            removeRecord(b, record);
        }
        ignoreEmptyBlock = false;

        Collections.sort(recordsToMove);

        Collections.reverse(recordsToMove);

        int recordsToMoveSize = recordsToMove.size();
        int len = 0;
        for (int i = recordsToMoveSize - 1; i >= 0; i--) {
            DataRow rec = recordsToMove.get(i);
            if (len > b.getPageSize() / 2) {
                break;
            }
            addRecord(b, (BasicDataRow)rec);
            recordsToMove.remove(i);
            len += rec.len();

        }

        if (recordsToMove.isEmpty()) {
            return;
        }
        //Block nextBlock = bufferManager.getBlock(b.block_id+1, tableIO);

        Block nextBlock = null;
        if (b.next_block_id == -1) {
            nextBlock = addBlock(b);
        } else {
            nextBlock = getBlock(b.next_block_id);
        }

        splitBlock(nextBlock, recordsToMove);

    }

    
    

    

    /**
     * traverses the numeric values from the primaryKey down until an existing
     * indexed record is found
     */
//    private RowData getLargestSmallerKey(long primaryKey) {
//        List<DictionaryPair> pairs = tree.searchAll();
//        for (int i = pairs.size()-1; i >= 0; i--) {
//            DictionaryPair pair = pairs.get(i);
//            Key key = pair.getKey();
//            RowData keyData = (RowData)key.get(0);
//            Value value = pair.getValue();
//            byte bytes_[] = (byte[]) value.get(0);
//            RowData rowData = tree.translator.convertBinaryToRowData(bytes_, null, true, false);
//            
//            long pk = keyData.getLong("id");
//            if (pk<primaryKey) return rowData;
//            
//        }
//        return null;
//    }

}
