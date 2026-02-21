/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.transaction.concurrency.deadlock.detection;

import ibd.transaction.Transaction;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 *
 * @author pccli
 */
public class CycleDetection {
    
    Graph graph = new Graph();
    
    Hashtable<Transaction, Vertex> transactions = new Hashtable<>();
    
    
    public void removeDependencies(Transaction t){
        Vertex v1 = transactions.get(t);
        if (v1==null) return;
        v1.removeOutputs();
        
        Iterator<Vertex> i = transactions.values().iterator();
        while (i.hasNext()){
            Vertex v2 = i.next();
            v2.removeOutput(v1);
        }
        
    }
    
    public void addTransaction(Transaction t){
        if (transactions.get(t)!=null) return;
        
        Vertex v = graph.addVertex(t);
        transactions.put(t, v);
    }
    
    public ArrayList addDependency(Transaction orig, Transaction dest){
        addTransaction(orig);
        addTransaction(dest);
        
        Vertex vOrig = transactions.get(orig);
        Vertex vDest = transactions.get(dest);
        vOrig.addOutput(vDest);
        return detect(vOrig);
    
    }
    
    public void removeDependency(Transaction orig, Transaction dest){
        addTransaction(orig);
        addTransaction(dest);
        
        Vertex vOrig = transactions.get(orig);
        Vertex vDest = transactions.get(dest);
        vOrig.removeOutput(vDest);
    }
    
    
    
    class Graph{
        ArrayList<Vertex> vertices = new ArrayList<>();
        
        public Vertex addVertex(Object o){
            Vertex v = new Vertex();
            v.o = o;
            v.index = vertices.size();
            vertices.add(v);
            return v;
        }
        
        public int getVerticesCount(){
            return vertices.size();
        }
        
        public Vertex getVertex(int index){
            return vertices.get(index);
        }
        
    
    
    }
    class Vertex{
        //Transaction t;
        //String name;
        Object o;
        ArrayList<Vertex> outputVertices = new ArrayList<>();
        int index;
        
        public void addOutput(Vertex dest){
            outputVertices.add(dest);
        }
        
        public void removeOutput(Vertex dest){
            outputVertices.remove(dest);
        }
        
        public void removeOutputs(){
            outputVertices.clear();
            
        }
    }
    
    
    
    class CycleItem{
    Vertex vertex;
    CycleItem prox_item;
    }
    
    
    class List{
    CycleItem  first;
    CycleItem  last;
    
    boolean isEmpty(){
    return (first==null);
}
    
    
    CycleItem getItem(Vertex vertex){
    CycleItem item = first;
    while (item!=null){
        if (item.vertex == vertex)
            return item;
        item = item.prox_item;
    }
    return null;
}
    
    
CycleItem  addItem(Vertex vertex){
    CycleItem item = new CycleItem();
    item.vertex = vertex;
    item.prox_item = null;

    if (first==null){
        first = item;
        last = item;
    }
    else{
        last.prox_item = item;
        last = item;
    }
    return item;

}
    
    void removeItem(Vertex vertex){
    CycleItem item = first;
    CycleItem previous = null;
    while (item!=null){
        if (item.vertex == vertex)
        {
            if (previous==null){ //primeiro item
                first = item.prox_item;
            }
            else{
                previous.prox_item = item.prox_item;
            }
            if (item.prox_item==null){ //ultimo item
                last = previous;
            }
            return;

        }
        previous = item;
        item = item.prox_item;

    }

}

    }
    
int[] initMap(Graph graph){
    int num_vertices = graph.getVerticesCount();
    
    int [] mapa = new int[num_vertices];
    int i;
    for (i=0;i<num_vertices;i++){
        mapa[i] = -1;
    }
return mapa;
}
    
ArrayList detect (Graph graph){

    List whiteList = createList();
    feedList(graph, whiteList);

    return detect(whiteList);
    
}


ArrayList detect (Vertex vertex){
    

    List  whiteList = createList();
    whiteList.addItem(vertex);
    return detect(whiteList);
}

ArrayList detect (List whiteList){

    //boolean cycle = false;
    List  grayList = createList();
    List  blackList = createList();

    int[] map = initMap(graph);

    while (!whiteList.isEmpty()){
        CycleItem item = whiteList.first;
        int resp = detect(item.vertex,whiteList, grayList, blackList, map);
        if (resp!=-1){
            //System.out.println("cycle");
            return getCycle(graph, map, resp);
            //cycle = true;
        }

    }
    
    //System.out.println("no cycles");
    return null;

}


int detect(Vertex  vertex, List whiteList, List grayList,List blackList, int [] map){

    if (blackList.getItem(vertex)!=null)
        return -1;

    if (grayList.getItem(vertex)!=null)
        return vertex.index;

    grayList.addItem(vertex);
    whiteList.removeItem(vertex);

    int i = 0;
    for (i=0;i<vertex.outputVertices.size();i++){
        Vertex outputV = vertex.outputVertices.get(i);
        map[outputV.index] = vertex.index;
        int resp = detect(outputV, whiteList, grayList, blackList, map);
        if (resp != -1)
            return resp;

    }
    grayList.removeItem(vertex);
    blackList.addItem(vertex);

return -1;


}



List createList(){
    List list = new List();
    list.first = null;
    list.last = null;
    return list;
}


void feedList(Graph graph, List list){

    int index = 0;
    for (index=0;index<graph.getVerticesCount();index++){
        Vertex  v = graph.getVertex(index);
        list.addItem(v);
    }

}



ArrayList getCycle(Graph graph, int [] map, int begin){
    int prox = begin;
    ArrayList list = new ArrayList();
    while (true){
        Vertex v = graph.getVertex(prox);
        //System.out.println(v.index);
        list.add(v.o);
        prox = map[prox];
        if (prox==begin){
            v = graph.getVertex(prox);
            //System.out.println(v.index);
            return list;
        }
    }

}






public void test(){
 Graph graph = new Graph();
   
 Vertex vMauro = graph.addVertex("Mauro");
   Vertex vAna = graph.addVertex("Ana");
   Vertex vPedro = graph.addVertex("Pedro");
   Vertex vPaulo = graph.addVertex("Paulo");
   Vertex vLuisa = graph.addVertex("Luisa");
   Vertex vGabriel = graph.addVertex("Gabriel");


   vMauro.addOutput(vAna);
   vMauro.addOutput(vLuisa);
   vLuisa.addOutput(vPaulo);
   vPaulo.addOutput(vAna);
   vMauro.addOutput(vPedro);
   vPedro.addOutput(vGabriel);
   vGabriel.addOutput(vAna);
   
   vPaulo.addOutput(vMauro);

    //detect(graph);
   detect(vMauro);
}


    public static void main(String[] args) {
       CycleDetection cd = new CycleDetection();
       cd.test();
    }
}