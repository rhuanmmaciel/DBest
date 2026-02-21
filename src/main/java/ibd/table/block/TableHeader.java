/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table.block;

import ibd.persistent.PageHeader;
import java.nio.ByteBuffer;

/**
 *
 * @author Sergio
 */
public class TableHeader extends PageHeader {

    private static final int SIZE = 12;

    private int firstBlock = -1;

    private int firstHeapBlock = -1;
    private int lastHeapBlock = -1;

    public TableHeader(int pageSize, int fb, int fhb, int lhb) {
        super(pageSize);
        this.firstBlock = fb;
        this.firstHeapBlock = fhb;
        this.lastHeapBlock = lhb;
    }

    public int getFirstBlock() {
        return firstBlock;
    }

    public void setFirstBlock(int fb) {
        this.firstBlock = fb;
    }

    public int getFirstHeapBlock() {
        return firstHeapBlock;
    }

    public void setFirstHeapBlock(int fhb) {
        this.firstHeapBlock = fhb;
    }

    public int getLastHeapBlock() {
        return lastHeapBlock;
    }

    public void setLastHeapBlock(int lhb) {
        this.lastHeapBlock = lhb;
    }

    @Override
    public void readHeader(ByteBuffer buffer) {
        super.readHeader(buffer);
        this.firstBlock = buffer.getInt();
        this.firstHeapBlock = buffer.getInt();
        this.lastHeapBlock = buffer.getInt();
    }

    @Override
    public void writeHeader(ByteBuffer buffer) {
        super.writeHeader(buffer);
        buffer.putInt(this.firstBlock); //
        buffer.putInt(this.firstHeapBlock); //
        buffer.putInt(this.lastHeapBlock); //
        //buffer.flip();
    }

    @Override
    public int size() {
        return super.size() + SIZE;
    }

}
