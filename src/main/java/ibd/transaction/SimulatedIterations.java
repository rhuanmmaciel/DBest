/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction;

import ibd.transaction.instruction.Instruction;
import ibd.transaction.concurrency.ConcurrencyManager;
import java.util.ArrayList;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class SimulatedIterations {

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
        t.setId(transactions.size());
    }

    public static String getString(int i) {
        String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (i >= 0 && i <= 25) {
            return LETTERS.substring(i, i + 1);
        } else {
            return String.valueOf(i);
        }
    }

    public static int getValue(String c) {
        String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int index = LETTERS.indexOf(c);
        if (index >= 0) {
            return index;
        }
        return Integer.valueOf(c);
    }

    public static String getTab(int index) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < index * 15; i++) {
            sb.append(' ');
        }
        return sb.toString();

    }

    public void run(int error, boolean displayContent, ConcurrencyManager manager) throws Exception {

        //manager = new ConcurrencyManager();
        //manager = new OptimisticConcurrencyManager();
        int commited = 0;

        int step = 0;

        while (commited < transactions.size()) {

            for (int i = 0; i < transactions.size(); i++) {

                if (step == error) {
                    throw new Exception("the step is over the boundary");
                }

                Transaction t = transactions.get(i);
                if (!t.hasNext()) {
                    if (!t.isCommited()) {
                        boolean ok = manager.commit(t);
                        if (ok) {
                            System.out.println(getTab(t.getId() - 1) + t.getId() + " commit");
                            commited++;
                            step++;
                        }
                    }
                } else {
                    Instruction inst = t.getCurrentInstruction();
                    List<DataRow> recs = manager.processInstruction(t);
                    if (recs != null) {
                        System.out.print(getTab(i) + t.getId() + " ");
                        //System.out.print(inst);
                        System.out.print(inst.getModeType() + "  ");
                        //System.out.print(" values ");
                        boolean first = true;
                        for (DataRow rec : recs) {
                            String pk = getString((int) rec.getInt("id"));
                            String endent = "";
                            if (!first) {
                                endent += getTab(i) + "          ";
                            }
                            if (displayContent) {
                                System.out.println(endent + pk + "(" + rec.getString("nome") + ")");
                            } else {
                                System.out.println(endent + pk);
                            }
                            first = false;
                        }
                        if (first) {
                            System.out.println();
                        }

                        //System.out.println(r);
                        step++;
                    }
                    //else System.out.println(getTab(t.getId()-1)+t.getId() + " wait");
                }
            }
        }

        //manager.flushLog();
    }

}
