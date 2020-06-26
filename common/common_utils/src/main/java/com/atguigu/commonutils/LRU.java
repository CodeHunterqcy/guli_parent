package com.atguigu.commonutils;

/**
 * @author CodeHunter_qcy
 * @date 2020/6/26 - 23:03
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LRU<K, V> {
    private int currentSize;
    private int capcity;
    private HashMap<K, Node> caches;
    private Node first;
    private Node last;

    public LRU(int size) {
        this.currentSize = 0;
        this.capcity = size;
        caches = new HashMap<K, Node>(size);
    }

    public void put(K key, V value) {
        Node node = caches.get(key);

        if (null == node) {
            if (caches.size() >= capcity) {
                caches.remove(last.key);
                removeLast();
            }
            node = new Node(key, value);
            caches.put(key, node);
            currentSize++;
        } else {
            node.value = value;
        }
        moveToHead(node);
    }

    public Object get(K key) {
        Node node = caches.get(key);
        if (node == null) {
            return null;
        }
        moveToHead(node);
        return node.value;
    }

    public Object remove(K key) {
        Node node = caches.get(key);
        if (node != null) {
            if (node.pre != null) {
                node.pre.next = node.next;
            }
            if (node.next != null) {
                node.next.pre = node.pre;
            }
            if (node == first) {
                first = node.next;
            }
            if (node == last) {
                last = node.pre;
            }
        }
        return caches.remove(key);
    }

    private void moveToHead(Node node) {
        if (first == node) {
            return;
        }
        if (node.pre != null) {
            node.pre.next = node.next;
        }
        if (node.next != null) {
            node.next.pre = node.pre;
        }

        if (node == last) {
            last = node.pre;
        }
        if (first == null || last == null) {
            first = last = node;
            return;
        }
        node.next = first;
        first.pre = node;
        first = node;
        first.pre = null;

    }

    private void removeLast() {
        if (last != null) {
            last = last.pre;
            if (last == null) {
                first = null;
            } else {
                last.next = null;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node node = first;
        while (node != null) {
            sb.append(String.format("%s:  ", node.key));
            node = node.next;
        }
        return sb.toString();
    }

    public void clear() {
        first = null;
        last = null;
        caches.clear();
    }
    public List getList(){
        ArrayList list = new ArrayList();
        Node node = first;
        while (node!=null){
            list.add(node.value);
            node = node.next;
        }
        return list;
    }

}

class Node {
    Object key;
    Object value;
    Node pre;
    Node next;

    public Node(Object key, Object value) {
        super();
        this.key = key;
        this.value = value;
    }

}

