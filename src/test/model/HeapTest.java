package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeapTest {
    Heap<Integer> intHeap;

    @BeforeEach
    void setup() {
        this.intHeap = new Heap<Integer>();
    }

    @Test
    void testConstructor() {
        assertNotNull(this.intHeap.getHeap());
        assertEquals(0, this.intHeap.getPtr());
    }

    @Test
    void testMalloc() {
        long ptr = this.intHeap.malloc(10);

        assertEquals(0, ptr);
        assertEquals(10, this.intHeap.get(ptr));

        this.intHeap.put(1, 10);
        assertEquals(10, this.intHeap.get(ptr));

        this.intHeap.setPtr(2);

        ptr = this.intHeap.malloc(12);

        assertEquals(2, ptr);
        assertEquals(12, this.intHeap.get(ptr));
    }
}
