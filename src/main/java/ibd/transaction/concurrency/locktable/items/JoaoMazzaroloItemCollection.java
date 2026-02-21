package ibd.transaction.concurrency.locktable.items;

import ibd.transaction.concurrency.Item;

import java.util.ArrayList;
import java.util.Vector;

public class JoaoMazzaroloItemCollection implements ItemCollection {
    private Vector<Item> items = new Vector<Item>();

    @Override
    public Item addItem(long lower, long higher) {
        Item item = getItem(lower, higher);

        if (item == null) {
            item = new Item(lower, higher);
            items.add(item);
            items.sort((current, next) -> 
                {
                    if (current.getLower() < next.getLower()) {
                        return -1;
                    } else if (current.getLower() > next.getLower()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            );
        }

        return item;
    }

    @Override
    public Item getItem(long lower, long higher) {  // busca binaria em um vetor ordenado de todos os itens, utilizando o menor valor do intervalo como chave
        int left = 0;
        int right = items.size() - 1;
        int middle = (left + right) / 2;
        boolean finded = false;

        while (left <= right) {
            Item item = items.get(middle);  // acessa o elemento do meio
            if (item.getLower() < lower) {
                left = middle + 1;
            } else if (item.getLower() > lower) {
                right = middle - 1;
            } else {
                finded = true;
                break;
            }
            middle = (left + right) / 2;
        }

        if (!finded) {
            return null;
        }

        boolean toLeft = true, toRight = true;
        left = middle - 1;
        right = middle + 1;

        Item item = items.get(middle);

        if (item.getLower() == lower && item.getHigher() == higher) {
            return item;
        }

        while(toLeft || toRight)        // busca linear para esquerda e direita a partir do elemento do meio, caso exista mais de um elemento com o mesmo inicio de intervalo
        {
            if(toLeft && left >= 0)
            {
                item = items.get(left);
                if(item.getLower() == lower)
                {
                    if (item.getLower() == lower && item.getHigher() == higher) {
                        return item;
                    }
                    left--;
                }
                else
                {
                    toLeft = false;
                }
            }
            else
            {
                toLeft = false;
            }
            if(toRight && right < items.size())
            {
                item = items.get(right);
                if(item.getLower() == lower)
                {
                    if (item.getLower() == lower && item.getHigher() == higher) {
                        return item;
                    }
                    right++;
                }
                else
                {
                    toRight = false;
                }
            }
        }

        return null;
    }

    @Override
    public Iterable<Item> getAllItems() {
        return items;   // Retorna todos os itens
    }

    @Override
    public Iterable<Item> getOverlappedItems(long lower, long higher) {
        ArrayList<Item> list = new ArrayList<Item>();
        for (Item item : items) {
            MainItem.steps++;
            if(item.getLower() > higher) {
                break;
            }
            if (item.getLower() <= higher && item.getHigher() >= lower) {
                list.add(item);
            }
        }
        return list;
    }
}
