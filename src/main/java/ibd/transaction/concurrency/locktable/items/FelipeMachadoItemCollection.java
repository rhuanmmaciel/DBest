package ibd.transaction.concurrency.locktable.items;

import ibd.transaction.concurrency.Item;

import java.util.ArrayList;

public class FelipeMachadoItemCollection implements ItemCollection {

    private final AutoBalancedIntervalTree tree = new AutoBalancedIntervalTree();
    @Override
    public Item addItem(long lower, long higher) {
        Item item = getItem(lower, higher);
        if (item==null){
            item = new Item(lower, higher);
            tree.insert(item);
        }

        return item;
    }

    @Override
    public Item getItem(long lower, long higher) {
        if (!tree.search(lower,higher).isEmpty()) {
            return tree.search(lower,higher).get(0);
        }
        return null;
    }

    @Override
    public Iterable<Item> getAllItems() {
        return tree.getAllNodes();
    }

    @Override
    public Iterable<Item> getOverlappedItems(long lower, long higher) {
        return tree.search(lower,higher);
    }

    static class Node {
        Item item;
        long max;
        Node parent;
        Node left;
        Node right;

        public Node(Item item) {
            this.item = item;
            this.max = item.getHigher();
            this.parent = null;
            this.left = null;
            this.right = null;
        }
    }

    public static class AutoBalancedIntervalTree {
        private Node root;
        private final Node nil;

        public AutoBalancedIntervalTree() {
            nil = new Node(new Item(0,0));
            nil.max = 0;
            root = nil;
        }

        public void insert(Item item) {
            Node newNode = new Node(item);
            newNode.left = nil;
            newNode.right = nil;

            if (root == nil) {
                root = newNode;
                root.parent = nil;
                return;
            }

            Node current = root;
            Node parent = nil;

            while (current != nil) {
                parent = current;

                if (item.getLower() < current.item.getLower()) {
                    current = current.left;
                } else {
                    current = current.right;
                }
            }

            newNode.parent = parent;

            if (item.getLower() < parent.item.getLower()) {
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }

            balanceTree(newNode);
        }

        private void balanceTree(Node node) {
            while (node != root && node.parent.max < node.max) {
                if (node == node.parent.left) {
                    node.parent.max = Math.max(node.max, Math.max(node.parent.right.max, node.parent.item.getHigher()));
                } else {
                    node.parent.max = Math.max(node.max, Math.max(node.parent.left.max, node.parent.item.getHigher()));
                }
                node = node.parent;
            }

            root.max = Math.max(root.left.max, Math.max(root.right.max, root.item.getHigher()));
        }

        public ArrayList<Item> search(long low, long high) {
            ArrayList<Item> list = new ArrayList<>();
            searchIntervals(root, low, high, list);
            return list;
        }

        private void searchIntervals(Node node, long low, long high, ArrayList<Item> list) {
            MainItem.steps++;
            if (node == nil) {
                return;
            }

            if (node.left != nil && node.left.max >= low) {
                searchIntervals(node.left, low, high,list);
            }

            if (node.item.getLower() <= high && node.item.getHigher() >= low) {
                list.add(node.item);
            }

            if (node.right != nil && node.item.getLower() <= high) {
                searchIntervals(node.right, low, high,list);
            }
        }

        public Iterable<Item> getAllNodes() {
            ArrayList<Item> itemList = new ArrayList<>();
            getAllNodes(root, itemList);
            return itemList;
        }

        private void getAllNodes(Node node, ArrayList<Item> itemList) {
            if (node == nil) {
                return;
            }
            getAllNodes(node.left, itemList);
            itemList.add(node.item);
            getAllNodes(node.right, itemList);
        }
    }
}


