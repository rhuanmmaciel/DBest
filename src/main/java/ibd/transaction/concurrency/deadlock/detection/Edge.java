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
public class Edge {

    Vertex origin;
    Vertex destination;

    Edge(Vertex o, Vertex d) {
        this.origin = o;
        this.destination = d;
    }

}
