package ibd.transaction.concurrency.timestamp.ordering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
import ibd.transaction.log.EmptyLogger;
import ibd.transaction.instruction.Instruction;
import ibd.transaction.Transaction;
import ibd.transaction.concurrency.ConcurrencyManager;
import ibd.transaction.instruction.SingleReadInstruction;
import ibd.transaction.instruction.SingleUpdateInstruction;
import ibd.table.prototype.DataRow;


public class MarcosLuzTimestampOrderingConcurrencyManager extends ConcurrencyManager {

    int currentTime = 0;
    Map<Integer, Integer> transactionTimestamps = new HashMap<>();
    ArrayList<ArrayList<Integer>> writesList= new ArrayList<>();
    ArrayList<ArrayList<Integer>> readsList= new ArrayList<>();

    public MarcosLuzTimestampOrderingConcurrencyManager() {
        logger = new EmptyLogger();
    }

    @Override
    public List<DataRow> processInstruction(Transaction t) throws Exception {
        boolean ignore = false;
        currentTime++;
        Integer timeStampTrans = getTransactionStartTime(t);
        logTansactionStart(t);
        Instruction i = t.getCurrentInstruction();
        if (i instanceof SingleUpdateInstruction) {
            for (ArrayList<Integer> readInstance : readsList) {
                if (readInstance.get(0) == (int) i.getPk()) {
                    if (timeStampTrans < readInstance.get(1)) {
                        abort(t);
                        return null;
                    }
                    if (timeStampTrans > readInstance.get(1)) {
                        for (ArrayList<Integer> writeInstance : writesList) {
                            if (i.getPk() == writeInstance.get(0)) {
                                if (timeStampTrans > writeInstance.get(1)) {
                                    writeInstance.add(1, timeStampTrans);
                                } else if (timeStampTrans < writeInstance.get(1)) {
                                    ignore = true;
                                }
                                return processCurrentInstruction(t, ignore);
                            }
                        }
                    }
                }
            }

            boolean timeMatch=true;
            for (ArrayList<Integer> writeInstance : writesList) {
                for (int j = 1; j < writeInstance.size(); j++) {
                    if ((int) writeInstance.get(j) == timeStampTrans) {
                        timeMatch = false;
                        break;
                    }
                }
            }
            ArrayList<Integer> tempList2 = new ArrayList<>();
            if (writesList.isEmpty() || timeMatch){
                tempList2.add((int)i.getPk());
                tempList2.add(timeStampTrans);
                writesList.add(0,tempList2);
            }
        }
        else if (i instanceof SingleReadInstruction) {
            for (ArrayList<Integer> writeInstance : writesList) {
                if (writeInstance.get(0) == (int) i.getPk()) {
                    if (timeStampTrans < writeInstance.get(1)) {
                        abort(t);
                        return null;
                    }
                    if (timeStampTrans > writeInstance.get(1)) {
                        for (ArrayList<Integer> readInstance : readsList) {
                            if (i.getPk() == readInstance.get(0)) {
                                if (timeStampTrans > readInstance.get(1)) {
                                    readInstance.add(1, timeStampTrans);
                                    return processCurrentInstruction(t, ignore);
                                }
                            }
                        }
                    }
                }
            }
            boolean timeMatch=true;
            for (ArrayList<Integer> readInstance : readsList) {
                for (int k = 1; k < readInstance.size(); k++) {
                    if ((int) readInstance.get(k) == timeStampTrans) {
                        timeMatch = false;
                        break;
                    }
                }
            }

            ArrayList<Integer> tempList = new ArrayList<>();
            if (readsList.isEmpty() || timeMatch){
                tempList.add((int)i.getPk());
                tempList.add(timeStampTrans);
                readsList.add(0,tempList);
            }
        }
        return processCurrentInstruction(t, ignore);
    }


    protected List<DataRow> processCurrentInstruction(Transaction t, boolean ignore) throws Exception {
        if (ignore) {
            t.advanceInstruction();
            return new ArrayList<>();
        }
        Instruction i = t.getCurrentInstruction();
        List<DataRow> recs = i.process();
        if (i.endProcessing()) {
            t.advanceInstruction();
        }
        return recs;
    }
    @Override

    protected void abort(Transaction t) throws Exception {
        super.abort(t);
        int transactionTime = getTransactionStartTime(t);
        transactionTimestamps.remove(t.getId());

        try {
            for (int i = 0; i < readsList.size(); i++) {
                for (int j = 1; j < readsList.get(i).size(); j++) {
                    if (readsList.get(i).get(j) == transactionTime) {
                        if (readsList.get(i).size() == 2) {
                            readsList.remove(i);
                            break;
                        } else {
                            readsList.get(i).remove(j);
                        }
                    }
                }
            }
        }catch (ConcurrentModificationException e){
            System.out.println("Erro de concurrentModification na lista de read");
            e.printStackTrace();
        }
        try{
            for (int i = 0; i < writesList.size(); i++) {
                for (int j = 1; j < writesList.get(i).size(); j++) {
                    if (writesList.get(i).get(j) == transactionTime){
                        if (writesList.get(i).size()==2){
                            writesList.remove(i);
                            break;
                        }else{
                            writesList.get(i).remove(j);
                        }
                    }
                }
            }
        }catch (ConcurrentModificationException e){
            System.out.println("Erro de concurrentModification na lista de write");
            e.printStackTrace();
        }
    }

    private Integer getTransactionStartTime(Transaction t) {
        if (transactionTimestamps.containsKey(t.getId())){
            return transactionTimestamps.get(t.getId());
        }else{
            transactionTimestamps.put(t.getId(),currentTime);
            return currentTime;
        }
    }
}