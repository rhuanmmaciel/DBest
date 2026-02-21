/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.optimizer.join;

import ibd.query.Operation;
import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.query.binaryop.join.JoinTerm;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.sourceop.FullTableScan;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author luizf
 */
public class JoinQueryOptimizer1 {

    Graph graph = new Graph();

    public Operation optimizeQuery(Operation op) throws Exception {
        buildVertices(op);
        buildGraph(op);

        List<Vertex> list = findOrder();
        Operation query = buildTree(list);
        //Operation query = op;
        return query;
    }

    private List<Vertex> findOrder() {
        List<Vertex> visited = new ArrayList();
        List<Vertex> orderedVertices = new ArrayList();
        for (Vertex vertex : graph.V) {
            if (!visited.contains(vertex)) {
                findOrder1(vertex, visited, orderedVertices);
            }
        }
        return orderedVertices;

    }

    private void findOrder1(Vertex vertex, List<Vertex> visited, List<Vertex> orderedVertices) {
        visited.add(vertex);
        Iterator<Edge> i = vertex.getEdges();
        while (i.hasNext()) {
            Edge e = i.next();
            Vertex dest = e.destination;
            if (!visited.contains(dest)) {
                findOrder1(dest, visited, orderedVertices);
            }
        }
        orderedVertices.add(0, vertex);

    }

    private void buildGraph(Operation op) throws Exception {

        if (op instanceof NestedLoopJoin) {
            NestedLoopJoin join = (NestedLoopJoin) op;
            List<JoinTerm> terms = join.getTerms();
            for (JoinTerm term : terms) {
                Vertex v1 = graph.getVertexByName(term.getLeftTableAlias());
                Vertex v2 = graph.getVertexByName(term.getRightTableAlias());

                List<String> pks = v1.getScan().table.getPrototype().getPKColumns();
                if (pks.contains(term.getLeftColumn()) && pks.size() == 1) {
                    v2.addEdge(v1, term);
                }

                pks = v2.getScan().table.getPrototype().getPKColumns();
                if (pks.contains(term.getRightColumn()) && pks.size() == 1) {
                    v1.addEdge(v2, term);
                }
            }
            buildGraph(join.getLeftOperation());
            buildGraph(join.getRightOperation());
        }
    }

    private void buildVertices(Operation op) throws Exception {

        if (op instanceof FullTableScan) {
            FullTableScan ts = (FullTableScan) op;
            graph.addVertex(ts);
        } else if (op instanceof NestedLoopJoin) {
            NestedLoopJoin join = (NestedLoopJoin) op;
            buildVertices(join.getLeftOperation());
            buildVertices(join.getRightOperation());
        }
    }

    private Operation buildTree(List<Vertex> joins) throws Exception {

        Vertex v1 = joins.get(0);
        FullTableScan ts = v1.getScan();
        Operation opAnt = new FullTableScan(ts.getDataSourceAlias(), ts.table);
        List<Vertex> verticesAdded = new ArrayList();
        verticesAdded.add(v1);
        for (int i = 1; i < joins.size(); i++) {
            Vertex v2 = joins.get(i);
            JoinPredicate terms = new JoinPredicate();
            List<JoinTerm> newTerms = new ArrayList();
            for (Vertex leftVertex : verticesAdded) {
                Edge e = v2.getEdge(leftVertex);
                if (e == null) {
                    e = leftVertex.getEdge(v2);
                    if (e == null) {
                        continue;
                    }
                }

                for (JoinTerm term : e.terms) {
                    if (term.getLeftTableAlias().equals(v2.getScan().getDataSourceAlias())) {
                        JoinTerm newTerm = new JoinTerm(term.getRightTableAlias(), term.getRightColumn(), term.getLeftTableAlias(), term.getLeftColumn());
                        newTerms.add(newTerm);
                    } else {
                        JoinTerm newTerm = new JoinTerm(term.getLeftTableAlias(), term.getLeftColumn(), term.getRightTableAlias(), term.getRightColumn());
                        newTerms.add(newTerm);
                    }
                }
            }

            terms.setTerms(newTerms);
            ts = v2.getScan();
            verticesAdded.add(v2);
            Operation op = new FullTableScan(ts.getDataSourceAlias(), ts.table);
            Operation join = new NestedLoopJoin(opAnt, op, terms);
            opAnt = join;
        }
        return opAnt;

    }
    
    /*
    Comparator c = null;
        try{
        c = (Comparator<Table>) (Table o1, Table o2) -> Long.compare(o1.getRecordsAmount(), o2.getRecordsAmount());
        } catch(Exception e){e.printStackTrace();}
        Collections.sort(tables, c);
    
    */

}
