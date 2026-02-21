/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.optimizer.join;

import ibd.query.binaryop.join.JoinTerm;
import java.util.ArrayList;
import java.util.List;

 
/**
 *
 * @author sergio
 */
public class Edge {

    public Vertex origin;
    public Vertex destination;
    public List<JoinTerm> terms = new ArrayList();

    Edge(Vertex o, Vertex d) {
        this.origin = o;
        this.destination = d;
    }
    
    public void addTerm(JoinTerm term){
        terms.add(term);
    }
    
}
