package ibd.transaction.concurrency.locktable.items;

import ibd.transaction.concurrency.Item;
import java.util.ArrayList;
import java.util.List;

/**
 * Utiliza uma interval tree para fazer a busca
 * por itens sobrepostos. A ávore é uma BST AVL.
 * @author Alexandre Chagas Brites
 */
public class AlexandreChagasBritesItemCollection implements ItemCollection {

    /**
     * Usa o campo max para filtar a busca por itens e
     * usa o campo height para balancear a árvore. 
     */
    private class Node {
        private Item item;
        private long max;

        private int height;
        private Node parent;
        private Node left;
        private Node right;

        public Node(Item item) {
            this.item = item;
            max = item.getHigher();
            height = 1;
        }

        public void updateMax() {
            max = item.getHigher();
            if (left != null) {
                max = Math.max(max, left.max);
            }
            if (right != null) {
                max = Math.max(max, right.max);
            }
        }

        public void updateHeight() {
            height = 1 + Math.max(getHeight(left), getHeight(right));
        }

        public void setLeft(Node left) {
            this.left = left;
            if (left != null) {
                left.parent = this;
            }
        }

        public void setRight(Node right) {
            this.right = right;
            if (right != null) {
                right.parent = this;
            }
        }

        public Node leftRotate() {
            Node node = right;
            Node tmp = node.left;

            node.parent = parent;
            node.setLeft(this);
            setRight(tmp);

            updateHeight();
            node.updateHeight();

            return node;
        }

        public Node rightRotate() {
            Node node = left;
            Node tmp = node.right;

            node.parent = parent;
            node.setRight(this);
            setLeft(tmp);

            updateHeight();
            node.updateHeight();

            return node;
        }
    }

    private Node root;
    
    @Override
    public Item addItem(long lower, long higher) {
        root = insertItem(root, lower, higher);
        return root.item;
    }

    private Node insertItem(Node node, long lower, long higher) {
        if (node == null) {
            return new Node(new Item(lower, higher));
        }

        if (lower < node.item.getLower() || (lower == node.item.getLower() && higher < node.item.getHigher())) {
            node.setLeft(insertItem(node.left, lower, higher));
        } else if (lower == node.item.getLower() && higher <= node.item.getHigher()) {
            return node;
        } else {
            node.setRight(insertItem(node.right, lower, higher));
        }

        node.updateHeight();
        node.max = Math.max(node.max, higher);
        
        int balance = getBalance(node);
        if (balance > 1) {
            if (lower < node.left.item.getLower()) {
                node = node.rightRotate();
                updateMax(node.right);
            } else if (lower > node.left.item.getLower()) {
                node.setLeft(node.left.leftRotate());
                node = node.rightRotate();
                node.left.updateMax();
                node.right.updateMax();
                updateMax(node);
            }
        } else if (balance < -1) {
            if (lower > node.right.item.getLower()) {
                node = node.leftRotate();
                updateMax(node.left);
            } else if (lower < node.right.item.getLower()) {
                node.setRight(node.right.rightRotate());
                node = node.leftRotate();
                node.left.updateMax();
                node.right.updateMax();
                updateMax(node);
            }
        }

        return node;
    }

    private int getHeight(Node node) {
        return node != null ? node.height : 0;
    }

    private int getBalance(Node node) {
        return node != null ? getHeight(node.left) - getHeight(node.right) : 0;
    }

    private void updateMax(Node node) {
        while (node != null) {
            node.updateMax();
            node = node.parent;
        }
    }

    @Override
    public Item getItem(long lower, long higher) {
        return searchItem(root, lower, higher);
    }

    private Item searchItem(Node node, long lower, long higher) {
        if (node == null) {
            return null;
        } else if (lower < node.item.getLower() || (lower == node.item.getLower() && higher < node.item.getHigher())) {
            return searchItem(node.left, lower, higher);
        } else if (lower == node.item.getLower() && higher == node.item.getHigher()) {
            return node.item;
        } else {
            return searchItem(node.right, lower, higher);
        }
    }

    @Override
    public Iterable<Item> getAllItems() {
        List<Item> result = new ArrayList<>();
        addAllItemsInto(root, result);
        return result;
    }

    private void addAllItemsInto(Node node, List<Item> result) {
        if (node != null) {
            result.add(node.item);
            addAllItemsInto(node.left, result);
            addAllItemsInto(node.right, result);
        }
    }

    @Override
    public Iterable<Item> getOverlappedItems(long lower, long higher) {
        List<Item> result = new ArrayList<>();
        addOverlappedItemsInto(root, lower, higher, result);
        return result;
    }

    private void addOverlappedItemsInto(Node node, long lower, long higher, List<Item> result) {
        MainItem.steps++;
        if (node == null) {
            return;
        }
        if (lower > node.max) {
            return;
        }
        addOverlappedItemsInto(node.left, lower, higher, result);
        if (node.item.getLower() <= higher && node.item.getHigher() >= lower) {
            result.add(node.item);
        }
        if (higher < node.item.getLower()) {
            return;
        }
        addOverlappedItemsInto(node.right, lower, higher, result);
    }
}
