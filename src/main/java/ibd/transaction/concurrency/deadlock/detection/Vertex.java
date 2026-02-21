/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.deadlock.detection;

/**
 *
 * @author vinicius
 */
import java.util.ArrayList;
import java.util.List;

public class Vertex {

    char name;
    int trans;
    String op;
    List<Edge> adj;

    Vertex(char name, int trans, String op) {
        this.name = name;
        this.trans = trans;
        this.op = op;
        this.adj = new ArrayList<Edge>();
    }

    void addAdj(Edge e) {
        adj.add(e);
    }

}
