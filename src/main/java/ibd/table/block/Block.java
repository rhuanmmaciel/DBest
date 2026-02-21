/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.block;

import ibd.table.Params;
import ibd.table.prototype.BasicDataRow;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.Column;

/**
 *
 * @author pccli
 */
public class Block extends PersistentBlock implements Iterable {

    public Integer prev_block_id = -1;
    public Integer next_block_id = -1;
    public Integer prev_heap_block_id = -1;
    public Integer next_heap_block_id = -1;

    TreeMap<String, DataRow> records = new TreeMap<>();
    private int offset;
    
    Prototype prototype;

    public Block(int pageSize, Prototype prototype) {
        super(pageSize);
        this.prototype = prototype;
    }

    @Override
    public Iterator iterator() {
        return records.values().iterator();
    }

    public void removeAllRecords() throws Exception {
        for (DataRow rec : records.values()) {
            removeRecord(rec);
        }
        
    }
    
    @Override
    public int getHeaderSizeInBytes(){
        return super.getHeaderSizeInBytes() + 20;
    }

    public boolean fits(int len) {
        return (pageSize - getHeaderSizeInBytes() - usedSpace - len >= 0);
    }

    public boolean isEmpty() {
        return (usedSpace == 0);
    }

    public DataRow getRecord(DataRow rowData) {
        return records.get(getKey(rowData));
    }

    public int getRecordsCount() {
        return records.size();
    }

    public DataRow[] getRecords() {
        DataRow result[] = new DataRow[records.size()];
        return records.values().toArray(result);
    }

    public DataRow addRecord(BasicDataRow rec) {

        if (!fits(rec.len())) {
            return null;
        }

        Params.RECORDS_ADDED++;

        rec.setBlock(this);
        records.put(getKey(rec), rec);

        usedSpace += rec.len();

        return rec;

    }

    public DataRow removeRecord(DataRow rec) throws Exception {

        if (!records.containsKey(getKey(rec))) {
            return null;
        }

        records.remove(getKey(rec));

        Params.RECORDS_REMOVED++;
        //System.out.println("removing record from "+block_id+" now contains free records = "+freeRecords.size());

        usedSpace -= rec.len();
        return rec;
    }

    public LinkedDataRow updateRecord(LinkedDataRow originalRec, BasicDataRow changedRec) throws Exception {

        List<Column> columns = prototype.getColumns();
        
        for (Column column : columns) {
            //if (column.isPrimaryKey()) continue;
            switch (column.getType()) {
                case Column.STRING_TYPE: 
                    String originalValueS = originalRec.getString(column.getName());
                    String changedValueS = changedRec.getString(column.getName());
                    if (changedValueS==null)
                        changedRec.setString(column.getName(), originalValueS);
                    break;
                case Column.LONG_TYPE: 
                    Long originalValueL = originalRec.getLong(column.getName());
                    Long changedValueL = changedRec.getLong(column.getName());
                    if (changedValueL==null)
                        changedRec.setLong(column.getName(), originalValueL);
                    break;
                case Column.INTEGER_TYPE:
                    Integer originalValueI = originalRec.getInt(column.getName());
                    Integer changedValueI = changedRec.getInt(column.getName());
                    if (changedValueI==null)
                        changedRec.setInt(column.getName(), originalValueI);
                    break;
                case Column.FLOAT_TYPE:
                    Float originalValueF = originalRec.getFloat(column.getName());
                    Float changedValueF = changedRec.getFloat(column.getName());
                    if (changedValueF==null)
                        changedRec.setFloat(column.getName(), originalValueF);
                    break;
                case Column.BOOLEAN_TYPE:
                    Boolean originalValueB = originalRec.getBoolean(column.getName());
                    Boolean changedValueB = changedRec.getBoolean(column.getName());
                    if (changedValueB==null)
                        changedRec.setBoolean(column.getName(), originalValueB);
                    break;
                case Column.DOUBLE_TYPE:
                    Double originalValueD = originalRec.getDouble(column.getName());
                    Double changedValueD = changedRec.getDouble(column.getName());
                    if (changedValueD==null)
                        changedRec.setDouble(column.getName(), originalValueD);
                    break;
                default:
                    throw new AssertionError();
            }
            
        }
        

//        int updatedLen = changedRec.len() - originalRec.len();
//
//        if (!fits(updatedLen)) {
//            return null;
//        }
//
//        usedSpace += updatedLen;

        records.put(getKey(changedRec), changedRec);
        
        return changedRec.getLinkedDataRow(prototype);

    }

    public DataRow maxPrimaryKey() {
        Iterator<DataRow> it = records.values().iterator();

        DataRow maxRec = null;

        while (it.hasNext()) {
            DataRow rec = it.next();
            if (maxRec==null){
                maxRec = rec;
                continue;
            }
            if (rec.compareTo(maxRec)>0) {
                {
                    maxRec = rec;
                }
            }
        }

        return maxRec;
    }

    @Override
    public void writeExternal(DataOutput out) throws IOException {

        super.writeExternal(out);

        Params.BLOCKS_SAVED++;

        out.writeInt(this.prev_block_id);//4
        out.writeInt(this.next_block_id);//4
        out.writeInt(this.prev_heap_block_id);//4
        out.writeInt(this.next_heap_block_id);//4

        out.writeInt(records.size());//4
        //save record
        //for (int x = 0; x < block.getRecordsCount(); x++) {
        //Record rec_ = block.getRecord(x);
        //if (rec_==null) continue;
        Iterator<LinkedDataRow> it = iterator();
        while (it.hasNext()) {
            LinkedDataRow rec_ = it.next();
            
            //file.seek(blockOffset + Block.HEADER_LEN + rec_.getRecordId() * Record.RECORD_SIZE);
            byte bytes[] = prototype.convertToArray(rec_);
            out.writeInt(bytes.length);
            out.write(bytes);
        }

    }

    @Override
    public void readExternal(DataInput in) throws IOException {

        super.readExternal(in);

        //System.out.println("loading block "+block_id);
        Params.BLOCKS_LOADED++;
        //start read

        //Long blockOffset = HEADER_LEN + Block.BLOCK_LEN * block_id;
        //file.seek(blockOffset);
        //4 bytes were read by the super class
        //byte[] bytes = new byte[Block2.BLOCK_LEN.intValue()-4];
        //in.readFully(bytes);
        //offset = 0;
        prev_block_id = in.readInt();//readInt(bytes);
        next_block_id = in.readInt();//readInt(bytes);
        prev_heap_block_id = in.readInt();//readInt(bytes);
        next_heap_block_id = in.readInt();//readInt(bytes);

        //load records
        Integer len = in.readInt();//readInt(bytes);
        for (int index = 0; index < len; index++) {
            //loadRecord(index, bytes);
            loadRecord(index, in);
        }

    }

    private void loadRecord(int index, DataInput in) throws IOException {
        //private void loadRecord(int index, byte[] bytes) throws IOException {

        //offset = (int) (Block3.HEADER_LEN + Record.RECORD_SIZE * index);
        
        byte b[] = new byte[in.readInt()];
        in.readFully(b);
        DataRow rowData = prototype.convertBinaryToRowData(b, null, true, false);
        
        rowData.setBlock(this);
        records.put(getKey(rowData), rowData);

        //addRecord(rec);
        //block.records.put(rec.getRec_id(), rec);
        //index.put(rec.getContent_id(), rec);
    }
    
    private String getKey(DataRow rowData){
        List<Column> columns = prototype.getColumns();
        String key = "";
        for (Column column : columns) {
            if (column.isPrimaryKey()){
                String v = rowData.getValue(column.getName()).toString();
                if (v!=null) key+=v+",";
            }
        }
        return key;
    }

    public int readInt(byte[] b) {
        int l = ((int) b[offset++] << 24)
                | ((int) b[offset++] & 0xff) << 16
                | ((int) b[offset++] & 0xff) << 8
                | ((int) b[offset++] & 0xff);
        return l;
    }

    public long readLong(byte[] b) {
        long l = ((long) b[offset++] << 56)
                | ((long) b[offset++] & 0xff) << 48
                | ((long) b[offset++] & 0xff) << 40
                | ((long) b[offset++] & 0xff) << 32
                | ((long) b[offset++] & 0xff) << 24
                | ((long) b[offset++] & 0xff) << 16
                | ((long) b[offset++] & 0xff) << 8
                | ((long) b[offset++] & 0xff);
        return l;
    }

    public short readShort(byte[] b) {
        long l = ((long) b[offset++] << 8)
                | ((long) b[offset++] & 0xff);
        return (short) l;
    }

    public String readUTF8(byte[] b) {
        short len = readShort(b);
        return new String(b, offset, len, StandardCharsets.UTF_8);
    }

    public boolean readBoolean(byte[] b) {

        return ((b[offset++] & 0xff) == 1);

    }

}
