package ibd.transaction.concurrency.locktable.items;

import ibd.transaction.concurrency.Item;
import java.util.ArrayList;


public class JoaoZucchiItemCollection implements ItemCollection {
    private ArrayList<Item> items = new ArrayList<>();
    
    @Override
    public Item addItem(long lower, long higher) {
        int lowIndex = 0;
        for(int i = 0; i < items.size(); i++){
            Item aux = items.get(i);

            if(aux.firstPrimaryKey < lower){
                lowIndex = i;//aproveita o laço e guarda o index para inserir ordenadamente
            }
            
            if(aux.firstPrimaryKey == lower && aux.lastPrimaryKey == higher){
                return null;
            }
        }
        Item item = new Item(lower, higher);
        items.add(lowIndex, item);
            
        return item;
    }

    @Override
    public Item getItem(long lower, long higher) {
        for(int i = 0; i < items.size(); i++){
            Item aux = items.get(i);
            if(aux.firstPrimaryKey == lower && aux.lastPrimaryKey == higher){
                return aux;
            }
        }
        return null;
    }

    @Override
    public Iterable<Item> getAllItems() {
        return items;
    }

    @Override
    public Iterable<Item> getOverlappedItems(long lower, long higher) {
        ArrayList<Item> overlapped = new ArrayList<>();
        for(int i = 0; i < items.size(); i++){
            MainItem.steps++;
            Item aux = items.get(i);
            long first = aux.firstPrimaryKey;
            long last = aux.lastPrimaryKey;
            /*
            if(!((first < lower && last < lower) || (first > higher && last > higher))){
                overlapped.add(aux);
            }*/
            if(first <= higher && last >= lower){
                overlapped.add(aux);
            }
        }
        return overlapped;
    }
}
