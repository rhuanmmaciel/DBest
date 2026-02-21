/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.binaryop.join;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ferna
 */
public class JoinPredicate {
    private List<JoinTerm> terms = new ArrayList();

    public void addTerm(String leftColumn, String rightColumn) throws Exception {
        JoinTerm term = new JoinTerm(leftColumn, rightColumn);
        terms.add(term); 
    }
    
    public void addTerm(String leftTableAlias, String leftColumn, String rightTableAlias, String rightColumn) {
        JoinTerm term = new JoinTerm(leftTableAlias, leftColumn, rightTableAlias, rightColumn);
        terms.add(term);
    }
    
    public int size(){
        return terms.size();
    }
    
    public List<JoinTerm> getTerms(){
        return terms;
    }

    /**
     * @param terms the terms to set
     */
    public void setTerms(List<JoinTerm> terms) {
        this.terms = terms;
    }
    
    @Override
    public String toString(){
        String result = "";
        boolean first = true;
        for (JoinTerm term : terms) {
            if (first){
                result+=term.toString();
                first = false;
            }
            else result = " and "+term.toString();
            
        }
        return result;
    }
}
