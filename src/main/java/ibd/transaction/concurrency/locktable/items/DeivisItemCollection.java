/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.concurrency.locktable.items;

import java.util.ArrayList;
import java.util.Vector;

import ibd.transaction.concurrency.Item;

/**
 *
 * @author Sergio
 */
public class DeivisItemCollection implements ItemCollection {
    // Array list ordenada de chaves e items
    private Vector<Item> items = new Vector<>();

    @Override
    public Item addItem(long lower, long higher) {

        Item item = getItem(lower, higher);
        if (item == null) {
            item = new Item(lower, higher);

            // Adiciona o item a lista
            items.add(item);

            // Ordena a lista pelo lower
            items.sort((Item i1, Item i2) -> {
                if (i1.getLower() < i2.getLower()) {
                    return -1;
                } else if (i1.getLower() > i2.getLower()) {
                    return 1;
                } else {
                    return 0;
                }
            });
        }

        return item;
    }

    @Override
    public Item getItem(long lower, long higher) {
        // Faz uma busca binaria na lista de itens
        int index = binarySearch(items, lower);
        // Se não encontrou um item retorn null
        if (index < 0) {
            return null;
        }
        // Verifica se o item tem o mesmo lower e higher passado como parametro
        if (index >= 0 && items.get(index).getLower() == lower && items.get(index).getHigher() == higher) {
            return items.get(index);
        } else {

            // Caso não tenha o mesmo lower e higher passado como parametro
            // Tem que verificar tanto para direita quanto para esquerda se tem item com o
            // mesmo lower e higher
            // Apenas em caso de ter lower igual
            int indexTemp = index;
            boolean continua = true;
            // Verifica se tem item na posicao anterior
            while (continua) {
                if (indexTemp - 1 >= 0) {
                    // Verifica se o item tem o mesmo lower
                    if (items.get(indexTemp - 1).getLower() == lower) {
                        // Verifica se o item tem o mesmo higher
                        if (items.get(indexTemp - 1).getHigher() == higher) {
                            return items.get(indexTemp - 1);
                        } else {
                            indexTemp--;
                        }
                    } else {
                        continua = false;
                    }
                } else {
                    continua = false;
                }
            }

            indexTemp = index;
            continua = true;

            // Verifica se tem item na posicao posterior
            while (continua) {
                if (indexTemp + 1 < items.size()) {
                    // Verifica se o item tem o mesmo lower
                    if (items.get(indexTemp + 1).getLower() == lower) {
                        // Verifica se o item tem o mesmo higher
                        if (items.get(indexTemp + 1).getHigher() == higher) {
                            return items.get(indexTemp + 1);
                        } else {
                            indexTemp++;
                        }
                    } else {
                        continua = false;
                    }
                } else {
                    continua = false;
                }
            }
        }

        return null;
    }

    private int binarySearch(Vector<Item> items, long lower) {
        int low = 0;
        int high = items.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            Item midVal = items.get(mid);
            int cmp = midVal.getLower().compareTo(lower);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1); // key not found
    }

    @Override
    public Iterable<Item> getAllItems() {
        return items;
    }

    @Override
    public Iterable<Item> getOverlappedItems(long lower, long higher) {
        // Lista de itens que estao na lista de bloqueados
        ArrayList<Item> list = new ArrayList<>();
        // Percorre a lista de bloqueados
        for (Item item : items) {
            MainItem.steps++;
            if (item.getLower() > higher) {
                break;
            }
            // Verifica se o item esta na lista
            if (item.getLower() <= higher && item.getHigher() >= lower) {
                list.add(item);
            }
        }
        return list;
    }

}
