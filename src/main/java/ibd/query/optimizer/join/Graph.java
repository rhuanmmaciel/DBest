/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.optimizer.join;

import ibd.query.sourceop.FullTableScan;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author sergio
 */
public class Graph {

    public List<Vertex> V;

    public Graph() {
        V = new ArrayList<>();
    }

    public Vertex addVertex(FullTableScan scan){
        for (Vertex vertex : V) {
            if (vertex.getScan().equals(scan))
                return vertex;
        }
        Vertex newV = new Vertex(scan);
        V.add(newV);
        return newV;
    }
    
    public Vertex getVertexByName(String tableName){
        for (Vertex vertex : V) {
            if (vertex.getScan().getDataSourceAlias().equals(tableName))
                return vertex;
        }
        
        return null;
    }
    
    

}
