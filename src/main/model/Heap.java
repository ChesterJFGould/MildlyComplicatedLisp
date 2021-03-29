package model;

import java.util.HashMap;

public class Heap<T> {
    private HashMap<Long, T> heap;
    private long nextPtr;

    public Heap() {
        this.heap = new HashMap<>();
        this.nextPtr = 0;
    }

    public long malloc(T obj) {
        this.heap.put(this.nextPtr, obj);
        return this.nextPtr++;
    }

    public T get(long ptr) {
        return this.heap.get(ptr);
    }

    public void put(long ptr, T obj) {
        this.heap.put(ptr, obj);
    }

    public HashMap<Long, T> getHeap() {
        return this.heap;
    }

    public void setPtr(long ptr) {
        this.nextPtr = ptr;
    }

    public long getPtr() {
        return this.nextPtr;
    }
}
