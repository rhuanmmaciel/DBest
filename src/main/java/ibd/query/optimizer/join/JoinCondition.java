/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.optimizer.join;

import ibd.query.binaryop.join.JoinTerm;
import ibd.query.sourceop.FullTableScan;
import java.util.List;

/**
 *
 * @author ferna
 */
public class JoinCondition {
    public FullTableScan scan;
    public List<JoinTerm> terms;
    public JoinCondition(FullTableScan scan, List<JoinTerm> terms){
        this.scan = scan;
        this.terms = terms;
    }
}
