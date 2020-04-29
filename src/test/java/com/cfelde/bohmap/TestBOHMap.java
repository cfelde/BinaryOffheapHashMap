/**
 * Copyright 2014 Christian Felde (cfelde [at] cfelde [dot] com)
 * <p>
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.cfelde.bohmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author cfelde
 */
public class TestBOHMap {
    private Random random;
    private BOHMap map;

    public TestBOHMap() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        random = new Random();
        map = new BOHMap(13);
    }

    @After
    public void tearDown() {
        map.clear();
    }

    @Test
    public void putGet1() {
        byte[] key = new byte[8];
        byte[] value = new byte[8];

        random.nextBytes(key);
        random.nextBytes(value);

        assertNull(map.put(new Binary(key), new Binary(value)));
        assertEquals(new Binary(value), map.get(new Binary(key)));
    }

    @Test
    public void putPutGet1() {
        byte[] key = new byte[8];
        byte[] value1 = new byte[8];
        byte[] value2 = new byte[8];

        random.nextBytes(key);
        random.nextBytes(value1);
        random.nextBytes(value2);

        assertNull(map.put(new Binary(key), new Binary(value1)));
        assertEquals(new Binary(value1), map.put(new Binary(key), new Binary(value2)));
        assertEquals(new Binary(value2), map.get(new Binary(key)));
    }

    @Test
    public void putGetNull() {
        byte[] key = new byte[8];

        random.nextBytes(key);

        assertNull(map.put(new Binary(key), null));
        assertNull(map.get(new Binary(key)));
        assertTrue(map.containsKey(new Binary(key)));
        assertTrue(map.containsValue(null));
    }

    @Test
    public void putRemove1() {
        byte[] key = new byte[8];
        byte[] value = new byte[8];

        random.nextBytes(key);
        random.nextBytes(value);

        assertNull(map.put(new Binary(key), new Binary(value)));
        assertEquals(new Binary(value), map.remove(new Binary(key)));
        assertTrue(map.isEmpty());

        assertNull(map.remove(new Binary(key)));
        assertTrue(map.isEmpty());
    }

    @Test
    public void size() {
        int expectedSize = 0;

        assertTrue(map.isEmpty());

        for (int i = 0; i < 1000; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            if (map.put(new Binary(key), new Binary(value)) == null)
                expectedSize++;
        }

        assertEquals(expectedSize, map.size());
    }

    @Test
    public void containsKey() {
        List<Binary> expectedKeys = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            if (map.put(new Binary(key), new Binary(value)) == null)
                expectedKeys.add(new Binary(key));
        }

        expectedKeys.forEach((k) -> assertTrue(map.containsKey(k)));

        List<Binary> nonExpectedKeys = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            byte[] key = new byte[8];
            random.nextBytes(key);
            Binary bKey = new Binary(key);

            if (!expectedKeys.contains(bKey))
                nonExpectedKeys.add(bKey);
        }

        nonExpectedKeys.forEach((k) -> assertFalse(map.containsKey(k)));

        byte[] biggerKey = new byte[10];
        random.nextBytes(biggerKey);
        assertFalse(map.containsKey(new Binary(biggerKey)));
    }

    @Test
    public void containsValue() {
        List<Binary> expectedValues = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            Binary oldValue = map.put(new Binary(key), new Binary(value));
            if (oldValue != null)
                expectedValues.remove(oldValue);
            expectedValues.add(new Binary(value));
        }

        expectedValues.forEach((v) -> assertTrue(map.containsValue(v)));

        List<Binary> nonExpectedValues = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            byte[] value = new byte[8];
            random.nextBytes(value);
            Binary bValue = new Binary(value);

            if (!expectedValues.contains(bValue))
                nonExpectedValues.add(bValue);
        }

        nonExpectedValues.forEach((v) -> assertFalse(map.containsValue(v)));

        byte[] biggerValue = new byte[10];
        random.nextBytes(biggerValue);
        assertFalse(map.containsValue(new Binary(biggerValue)));
    }

    @Test
    public void clear() {
        List<Binary> expectedKeys = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            if (map.put(new Binary(key), new Binary(value)) == null)
                expectedKeys.add(new Binary(key));
        }

        expectedKeys.forEach((k) -> assertTrue(map.containsKey(k)));

        map.clear();

        assertTrue(map.isEmpty());

        expectedKeys.forEach((k) -> assertFalse(map.containsKey(k)));
    }

    @Test
    public void putAll() {
        Map<Binary, Binary> map2 = new HashMap<>();

        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            map2.put(new Binary(key), new Binary(value));
        }

        map.putAll(map2);

        assertEquals(map2.size(), map.size());
        assertTrue(map2.keySet().containsAll(map.keySet()));
        assertTrue(map2.values().containsAll(map.values()));
    }

    @Test
    public void keySetSize() {
        List<Binary> expectedKeys = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            if (map.put(new Binary(key), new Binary(value)) == null)
                expectedKeys.add(new Binary(key));
        }

        Set<Binary> keySet = map.keySet();

        assertEquals(expectedKeys.size(), keySet.size());
        assertFalse(keySet.isEmpty());
    }

    @Test
    public void keySetContains() {
        List<Binary> expectedKeys = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            if (map.put(new Binary(key), new Binary(value)) == null)
                expectedKeys.add(new Binary(key));
        }

        Set<Binary> keySet = map.keySet();

        expectedKeys.stream().forEach((b) -> assertTrue(keySet.contains(b)));
        assertTrue(keySet.containsAll(expectedKeys));
    }

    @Test
    public void keySetToArray() {
        List<Binary> expectedKeys = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            if (map.put(new Binary(key), new Binary(value)) == null)
                expectedKeys.add(new Binary(key));
        }

        Object[] objArray = map.keySet().toArray();

        assertEquals(expectedKeys.size(), objArray.length);
        assertTrue(expectedKeys.containsAll(Arrays.asList(objArray)));

        Binary[] bArray = map.keySet().toArray(new Binary[0]);

        assertEquals(expectedKeys.size(), bArray.length);
        assertTrue(expectedKeys.containsAll(Arrays.asList(bArray)));

        bArray = map.keySet().toArray(new Binary[map.size()]);

        assertEquals(expectedKeys.size(), bArray.length);
        assertTrue(expectedKeys.containsAll(Arrays.asList(bArray)));
    }

    @Test
    public void keySetToRemove() {
        List<Binary> expectedKeys = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            if (map.put(new Binary(key), new Binary(value)) == null)
                expectedKeys.add(new Binary(key));
        }

        Binary removedKey = expectedKeys.remove(expectedKeys.size() / 2);
        assertTrue(map.keySet().remove(removedKey));
        assertFalse(map.keySet().remove(removedKey));
    }

    @Test
    public void keySetToRemoveAll() {
        List<Binary> expectedKeys = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            if (map.put(new Binary(key), new Binary(value)) == null)
                expectedKeys.add(new Binary(key));
        }

        assertTrue(map.keySet().removeAll(expectedKeys));
        assertFalse(map.keySet().removeAll(expectedKeys));
        assertTrue(map.keySet().isEmpty());
        assertTrue(map.isEmpty());
    }

    @Test
    public void keySetClear() {
        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            map.put(new Binary(key), new Binary(value));
        }

        map.keySet().clear();
        assertTrue(map.keySet().isEmpty());
        assertTrue(map.isEmpty());
    }

    @Test
    public void keySetIterator() {
        List<Binary> expectedKeys = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            if (map.put(new Binary(key), new Binary(value)) == null)
                expectedKeys.add(new Binary(key));
        }

        Iterator<Binary> iterator = map.keySet().iterator();
        List<Binary> actualKeys = new ArrayList<>();
        iterator.forEachRemaining(actualKeys::add);

        assertEquals(expectedKeys.size(), actualKeys.size());
        assertTrue(expectedKeys.containsAll(actualKeys));
    }

    @Test
    public void valuesSize() {
        List<Binary> expectedValues = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key1 = new byte[8];
            byte[] key2 = new byte[8];

            random.nextBytes(key1);
            random.nextBytes(key2);

            Binary old = map.put(new Binary(key1), new Binary(key1));
            if (old != null)
                expectedValues.remove(old);
            map.put(new Binary(key2), new Binary(key1));
            if (old != null)
                expectedValues.remove(old);
            expectedValues.add(new Binary(key1));
            expectedValues.add(new Binary(key1));
        }

        Collection<Binary> values = map.values();

        assertEquals(expectedValues.size(), values.size());
    }

    @Test
    public void valuesContains() {
        List<Binary> expectedValues = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key1 = new byte[8];
            byte[] key2 = new byte[8];

            random.nextBytes(key1);
            random.nextBytes(key2);

            Binary old = map.put(new Binary(key1), new Binary(key1));
            if (old != null)
                expectedValues.remove(old);
            old = map.put(new Binary(key2), new Binary(key1));
            if (old != null)
                expectedValues.remove(old);
            expectedValues.add(new Binary(key1));
            expectedValues.add(new Binary(key1));
        }

        Collection<Binary> values = map.values();

        expectedValues.stream().forEach((b) -> assertTrue(values.contains(b)));
        assertTrue(values.containsAll(expectedValues));
    }

    @Test
    public void valuesToArray() {
        List<Binary> expectedValues = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key1 = new byte[8];
            byte[] key2 = new byte[8];

            random.nextBytes(key1);
            random.nextBytes(key2);

            Binary old = map.put(new Binary(key1), new Binary(key1));
            if (old != null)
                expectedValues.remove(old);
            old = map.put(new Binary(key2), new Binary(key1));
            if (old != null)
                expectedValues.remove(old);
            expectedValues.add(new Binary(key1));
            expectedValues.add(new Binary(key1));
        }

        Collection<Binary> values = map.values();

        Object[] objArray = values.toArray();
        assertEquals(expectedValues.size(), objArray.length);
        assertTrue(expectedValues.containsAll(Arrays.asList(objArray)));

        Binary[] bArray = values.toArray(new Binary[0]);
        assertEquals(expectedValues.size(), bArray.length);
        assertTrue(expectedValues.containsAll(Arrays.asList(bArray)));

        bArray = values.toArray(new Binary[map.size()]);
        assertEquals(expectedValues.size(), bArray.length);
        assertTrue(expectedValues.containsAll(Arrays.asList(bArray)));
    }

    @Test
    public void valuesClear() {
        for (int i = 0; i < 100; i++) {
            byte[] key = new byte[8];
            byte[] value = new byte[8];

            random.nextBytes(key);
            random.nextBytes(value);

            map.put(new Binary(key), new Binary(value));
        }

        map.values().clear();
        assertTrue(map.isEmpty());
        assertTrue(map.values().isEmpty());
    }

    @Test
    public void valuesIterator() {
        List<Binary> expectedValues = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            byte[] key1 = new byte[8];
            byte[] key2 = new byte[8];

            random.nextBytes(key1);
            random.nextBytes(key2);

            Binary old = map.put(new Binary(key1), new Binary(key1));
            if (old != null)
                expectedValues.remove(old);
            old = map.put(new Binary(key2), new Binary(key1));
            if (old != null)
                expectedValues.remove(old);
            expectedValues.add(new Binary(key1));
            expectedValues.add(new Binary(key1));
        }

        Collection<Binary> values = map.values();

        Iterator<Binary> iterator = values.iterator();
        List<Binary> actualValues = new ArrayList<>();
        iterator.forEachRemaining(actualValues::add);

        assertEquals(expectedValues.size(), actualValues.size());
        assertTrue(expectedValues.containsAll(actualValues));
    }

    @Test
    public void entrySetSize() {
        Map<Binary, Binary> map2 = new HashMap<>();

        for (int i = 0; i < 100; i++) {
            byte[] key1 = new byte[8];
            byte[] key2 = new byte[8];

            random.nextBytes(key1);
            random.nextBytes(key2);

            map.put(new Binary(key1), new Binary(key1));
            map2.put(new Binary(key1), new Binary(key1));
        }

        assertEquals(map2.size(), map.entrySet().size());
    }

    @Test
    public void entrySetIsEmpty() {
        for (int i = 0; i < 100; i++) {
            byte[] key1 = new byte[8];
            byte[] key2 = new byte[8];

            random.nextBytes(key1);
            random.nextBytes(key2);

            map.put(new Binary(key1), new Binary(key1));
        }

        assertFalse(map.entrySet().isEmpty());
        map.clear();
        assertTrue(map.entrySet().isEmpty());
    }

    @Test
    public void entrySetContains() {
        Map<Binary, Binary> map2 = new HashMap<>();

        for (int i = 0; i < 100; i++) {
            byte[] key1 = new byte[8];
            byte[] key2 = new byte[8];

            random.nextBytes(key1);
            random.nextBytes(key2);

            map.put(new Binary(key1), new Binary(key1));
            map2.put(new Binary(key1), new Binary(key1));
        }

        map2.entrySet().forEach((e) -> assertTrue(map.entrySet().contains(e)));
        map.entrySet().containsAll(map2.entrySet());
    }

    @Test
    public void entrySetToArray() {
        Map<Binary, Binary> map2 = new HashMap<>();

        for (int i = 0; i < 100; i++) {
            byte[] key1 = new byte[8];
            byte[] key2 = new byte[8];

            random.nextBytes(key1);
            random.nextBytes(key2);

            map.put(new Binary(key1), new Binary(key1));
            map2.put(new Binary(key1), new Binary(key1));
        }

        Object[] objArray = map.entrySet().toArray();
        assertEquals(map.size(), objArray.length);
        assertTrue(map2.entrySet().containsAll(Arrays.asList(objArray)));

        Entry<Binary, Binary>[] eArray = map.entrySet().toArray(new Entry[0]);
        assertEquals(map.size(), eArray.length);
        assertTrue(map2.entrySet().containsAll(Arrays.asList(eArray)));

        eArray = map.entrySet().toArray(new Entry[map.size()]);
        assertEquals(map.size(), eArray.length);
        assertTrue(map2.entrySet().containsAll(Arrays.asList(eArray)));
    }

    @Test
    public void entrySetClear() {
        for (int i = 0; i < 100; i++) {
            byte[] key1 = new byte[8];
            byte[] key2 = new byte[8];

            random.nextBytes(key1);
            random.nextBytes(key2);

            map.put(new Binary(key1), new Binary(key1));
        }

        assertFalse(map.entrySet().isEmpty());
        map.entrySet().clear();
        assertTrue(map.entrySet().isEmpty());
    }

    @Test
    public void entrySetIterator() {
        Map<Binary, Binary> map2 = new HashMap<>();

        for (int i = 0; i < 100; i++) {
            byte[] key1 = new byte[8];
            byte[] key2 = new byte[8];

            random.nextBytes(key1);
            random.nextBytes(key2);

            map.put(new Binary(key1), new Binary(key1));
            map2.put(new Binary(key1), new Binary(key1));
        }

        List<Entry<Binary, Binary>> entrySetContent = new ArrayList<>();
        map.entrySet().iterator().forEachRemaining(entrySetContent::add);

        assertEquals(map2.size(), entrySetContent.size());
        assertTrue(map2.entrySet().containsAll(entrySetContent));
    }

    @Test
    public void putRemoveIterate() {
        byte[] key = new byte[8];
        byte[] value = new byte[8];

        random.nextBytes(key);
        random.nextBytes(value);

        assertTrue(map.isEmpty());

        map.put(new Binary(key), new Binary(value));
        map.remove(new Binary(key));

        Iterator<Binary> iterator = map.values().iterator();

        assertFalse(iterator.hasNext());
        assertTrue(map.isEmpty());

        map.put(new Binary(key), new Binary(value));

        iterator = map.values().iterator();

        assertTrue(iterator.hasNext());
        assertEquals(new Binary(value), iterator.next());
        assertFalse(map.isEmpty());

        map.remove(new Binary(key));

        iterator = map.values().iterator();

        assertFalse(iterator.hasNext());
        assertTrue(map.isEmpty());
    }
}
