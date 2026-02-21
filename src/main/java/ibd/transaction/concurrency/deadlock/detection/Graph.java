/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.deadlock.detection;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author vinicius
 */
public class Graph {

    List<Vertex> V;
    List<Edge> E;
    LinkedList<Integer>[] adjList;

    public Graph() {
        V = new ArrayList<Vertex>();
        E = new ArrayList<Edge>();
        adjList = new LinkedList[20];
        for (int i = 0; i < 20; i++) {
            adjList[i] = new LinkedList<>();
        }
    }

    Vertex addVertex(char name, int trans, String op) {
        Vertex ver = new Vertex(name, trans, op);
        V.add(ver);
        return ver;
    }

    public Edge addEdge(Vertex o, Vertex d) {
        Edge edg = new Edge(o, d);
        //o.addAdj(edg);
        E.add(edg);

        //adjList[o.trans].addFirst(d.trans);
        adjList[d.trans].addFirst(o.trans);
        return edg;
    }

    //Monta Edge
    public void createEdge() {
        //Percorre todos os vertices
        for (int i = 0; i < V.size(); i++) {
            //Percorre todos os vertices a frente do atual
            for (int j = i + 1; j < V.size(); j++) {

                //System.out.println(V.get(i).trans + " - " + V.get(j).trans + " " + V.get(i).name + " " + V.get(j).name);
                //Se eles usarem o mesmo tipo de dado
                if (V.get(i).name == V.get(j).name) {
                    //Se eles não forem a mesma transação
                    if (V.get(i).trans != V.get(j).trans) {
                        //E se eles não forem dois READS
                        if (!V.get(i).op.equals(V.get(j).op)) {
                            //Passando nisso, o V(i) precisa terminar antes do V(j)
                            // V(i)->V(j)
                            //if (!existAdj(V.get(i).trans, V.get(j).trans)) {
                            System.out.println("# Criou a aresta " + V.get(i).trans + "->" + V.get(j).trans);
                            E.add(addEdge(V.get(i), V.get(j)));
                            //}

                        }
                    }
                }
            }
        }
    }

    public boolean existAdj(int a, int b) {
        System.out.println("a: " + a + " b: " + b);
        for (int i = 0; i < adjList[b].size(); i++) {
            //System.out.println("adjList[" + b + "].get[" + i + "] == " + a);
            if (adjList[b].get(i) == a) {
                //System.out.println("Achou");
                return true;
            }

        }
        return false;
    }

    public void removeEdge(int trans) {
        for (int i = 0; i < V.size(); i++) {
            if (V.get(i).trans == trans) {
                V.remove(i);
                //System.out.println("Removeu a trans: " + trans + " da lista de vertice");
                i--;
            }
        }

        for (int i = 0; i < E.size(); i++) {
            if (E.get(i).origin.trans == trans) {
                E.remove(i);
                //System.out.println("!! Removeu a trans " + trans + " da lista de aresta");
            }
        }

        for (int i = 0; i < adjList[trans].size(); i++) {
            try {
                //System.out.println("&& Removeu " + adjList[trans].get(i) + " da lista de adj");
                adjList[trans].remove(i);
                i--;
            } catch (Exception e) {

            }

        }

    }

    public String printGraph() {
        String graph_string = "";
        for (Vertex u : V) {
            graph_string += u.name + " -> ";
            for (Edge e : u.adj) {
                Vertex v = e.destination;
                graph_string += v.name + ", ";
            }
            graph_string += "\n";
        }
        return graph_string;
    }

    public void printEdge() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                try {
                    System.out.println("$$ Aresta: " + i + "[" + j + "]" + " => " + adjList[i].get(j));
                } catch (Exception e) {

                }
            }
        }
    }

    public void printVertex() {
        System.out.println("Print Arestas: ");
        for (int i = 0; i < V.size(); i++) {
            System.out.println(V.get(i).trans + ":" + V.get(i).name + ":" + V.get(i).op);
        }
    }

    public int DFS(int source, boolean[] visited, int parent) {

        visited[source] = true;

        for (int i = 0; i < adjList[source].size(); i++) {
            int vertex = adjList[source].get(i);
            //procura nos vizinhos
            if (vertex != parent) {
                if (visited[vertex]) {
                    //Se passar por aqui significa que esse vertice
                    //ja foi visitado, entao existe ciclo
                    System.out.println("@ Encontrou ciclo, T" + vertex + " precisa esperar o aborto da mais nova.");
                    System.out.println("Source: " + source);
                    //return vertex;
                    return source;
                }
            }
        }
        return 0;
    }

    public int detectCycle() {
        int result = 0;
        boolean[] visited = new boolean[20];
        for (int i = 0; i < 20; i++) {
            if (visited[i] == false) {
                result = DFS(i, visited, -1);
                if (result != 0) {
                    //System.out.println("Encontrou ciclo no vertice: T" + result);
                    //Existe ciclo
                    //Retornar o numero da trans?
                    return result;
                }
            }
        }

        return result;
    }

}
