/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.locktable.items;

import ibd.transaction.concurrency.Item;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio
 */
public class OverlapList implements ItemCollection {

    List<Item> itens = new ArrayList();

    @Override
    public Item addItem(long lower, long higher) {
        Item item = getItem(lower, higher);
        if (item == null) {
            item = new Item(lower, higher);
            itens.add(item);
        }

        return item;

    }

    @Override
    public Iterable<Item> getAllItems() {
        return itens;
    }

    @Override
    public Iterable<Item> getOverlappedItems(long lower, long higher) {
        List<Item> result = new ArrayList();
        for (Item item : itens) {
            if (temSobreposicao(item.firstPrimaryKey, item.lastPrimaryKey, lower, higher)) {
                result.add(item);
            }

        }
        return result;
    }

    private boolean igual(long start1, long end1, long start2, long end2) {
        if (start1 != start2) {
            return false;
        }
        if (end1 != end2) {
            return false;
        }

        return true;

    }

    private boolean temSobreposicao(long start1, long end1, long start2, long end2) {

        if (end1 < start2) {
            return false;
        }
        if (end2 < start1) {
            return false;
        }

        return true;

    }

    @Override
    public Item getItem(long lower, long higher) {
        for (Item item : itens) {
            if (igual(item.firstPrimaryKey, item.lastPrimaryKey, lower, higher)) {
                return item;
            }
        }
        return null;
    }
}
