/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.sort.external;

import ibd.query.SourceTuple;
import ibd.query.Tuple;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ibd.table.prototype.DataRow;

/**
 *
 * @author Sergio
 */
public class TupleBucketIO {

    RandomAccessFile file;
    long totalNumberOfTuples;
    int sourceTuplesAmount;
    String folder;
    String name;
    boolean openFile = false;

    int tuplesWrote;

    boolean write = false;

    public TupleBucketIO(String folder, String name, boolean write) {
        this.folder = folder;
        this.name = name;
        this.write = write;
    }

    public long getBucketSize() {
        return totalNumberOfTuples;
    }

    public void open() throws Exception {
        if (openFile) {
            return;
        }

        Path path = Paths.get(folder);
        Files.createDirectories(path);
        file = new RandomAccessFile(folder + File.separator + name, "rw");
        openFile = true;
        file.seek(0);

        if (write) {
            file.writeLong(tuplesWrote);
            file.writeShort(0);
        } else {
            totalNumberOfTuples = file.readLong();
            sourceTuplesAmount = file.readShort();
        }
    }

    public void close() throws IOException {
        file.close();
    }

    public Tuple readTuple() throws IOException {
        Tuple tuple = new Tuple();
        SourceTuple sourceTuples[] = new SourceTuple[sourceTuplesAmount];
        for (int j = 0; j < sourceTuplesAmount; j++) {
            ibd.query.SourceTuple st = new ibd.query.SourceTuple();
            String source = file.readUTF();
//            DataRow rec = new DataRow(file.readLong());
//            rec.setContent(file.readUTF());
//            st.source = source;
//            st.record = rec;
            sourceTuples[j] = st;

        }
        //tuple.setSources(sourceTuples);
        return tuple;
    }

    public void saveTuple(Tuple tuple) throws IOException {
//        for (int j = 0; j < tuple.sourceTuples.length; j++) {
//            SourceTuple st = tuple.sourceTuples[j];
//            file.writeUTF(st.source);
//            file.writeLong(st.record.getPrimaryKey());
//            //System.out.println(st.record.getPrimaryKey());
//            file.writeUTF(st.record.getContent());
//        }
        tuplesWrote++;
    }

    public void updateFile(Tuple tuple) throws IOException {
        long currentFilePointer = file.getFilePointer();

        file.seek(0);
        file.writeLong(tuplesWrote);
        if (tuple != null) {
            //file.writeShort(tuple.sourceTuples.length);
        } else {
            file.writeShort(0);
        }
        file.seek(currentFilePointer);
    }

}
