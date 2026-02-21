/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.optimizer.join;

/**
 *
 * @author sergio
 */
import ibd.query.binaryop.join.JoinTerm;
import ibd.query.sourceop.FullTableScan;
import java.util.HashMap;
import java.util.Iterator;

public class Vertex {

    private FullTableScan scan;
    private HashMap<Vertex, Edge> edges = new HashMap();

    Vertex(FullTableScan scan) {
        this.scan = scan;
    }
    
    public Edge getEdge(Vertex v){
        return edges.get(v);
    }
    
    public Iterator<Edge> getEdges(){
        return edges.values().iterator();
    }
    
    public FullTableScan getScan(){
        return scan;
    }

    public Edge addEdge(Vertex dest, JoinTerm term) {
        
        Edge edge = edges.get(dest);
        if (edge == null){
            edge = new Edge(this, dest);
            edges.put(dest, edge);
        }
        edge.addTerm(term);
        return edge;
    }
    
    public void deleteEdge(Vertex dest){
        edges.remove(dest);
    }
    
    public String toString(){
        return scan.getDataSourceAlias();
    }

}
