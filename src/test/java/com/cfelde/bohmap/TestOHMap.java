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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class TestOHMap {
    private Map<String, String> map;

    public TestOHMap() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        map = new OHMap<>(13);
    }

    @After
    public void tearDown() {
        map.clear();
    }

    @Test
    public void clear() {
        map.put("Key1", "Value1");
        assertFalse(map.isEmpty());
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    public void containsKey() {
        map.put("Key1", "Value1");
        map.put("Key2", "Value2");

        assertTrue(map.containsKey("Key1"));
        assertTrue(map.containsKey("Key2"));
        assertFalse(map.containsKey("Key3"));
    }

    @Test
    public void containsValue() {
        map.put("Key1", "Value1");
        map.put("Key2", "Value2");

        assertTrue(map.containsValue("Value1"));
        assertTrue(map.containsValue("Value2"));
        assertFalse(map.containsValue("Value3"));
    }

    @Test
    public void putGet() {
        assertNull(map.put("Key1", "Value1"));
        assertNull(map.put("Key2", "Value2"));

        assertEquals("Value1", map.get("Key1"));
        assertEquals("Value2", map.get("Key2"));
        assertNull(map.get("Key3"));
    }

    @Test
    public void putGetNull() {
        assertNull(map.put("Key1", null));
        assertNull(map.get("Key1"));
        assertTrue(map.containsKey("Key1"));
        assertTrue(map.containsValue(null));
    }

    @Test
    public void putAll() {
        Map<String, String> map2 = new HashMap<>();
        map2.put("Key1", "Value");
        map2.put("Key2", "Value");
        map2.put("Key3", "Value");

        map.putAll(map2);

        assertEquals(map2.size(), map.size());
        map2.forEach((k, v) -> assertTrue(map.containsKey(k) && map.containsValue(v)));
    }

    @Test
    public void remove() {
        Map<String, String> map2 = new HashMap<>();
        map2.put("Key1", "Value");
        map2.put("Key2", "Value");
        map2.put("Key3", "Value");

        map.putAll(map2);

        map2.remove("Key2");
        assertEquals("Value", map.remove("Key2"));

        assertEquals(map2.size(), map.size());
        map2.forEach((k, v) -> assertTrue(map.containsKey(k) && map.containsValue(v)));
    }

    @Test
    public void size() {
        assertEquals(0, map.size());

        map.put("Key1", "Value");
        assertEquals(1, map.size());

        map.put("Key2", "Value");
        assertEquals(2, map.size());

        map.put("Key3", "Value");
        assertEquals(3, map.size());

        assertEquals("Value", map.put("Key2", "Value2"));
        assertEquals(3, map.size());
    }

    @Test
    public void isEmpty() {
        assertTrue(map.isEmpty());

        map.put("Key", "Value");
        assertFalse(map.isEmpty());

        map.remove("Key");
        assertTrue(map.isEmpty());

        assertNull(map.put("Key", "Value"));
        assertFalse(map.isEmpty());

        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    public void keySet() {
        map.put("Key1", "Value1");
        map.put("Key2", "Value2");

        assertEquals(2, map.keySet().size());
        assertFalse(map.keySet().isEmpty());

        assertTrue(map.keySet().contains("Key1"));
        assertTrue(map.keySet().contains("Key2"));
        assertFalse(map.keySet().contains("Key3"));
        assertTrue(map.keySet().containsAll(Arrays.asList("Key1", "Key2")));
        assertFalse(map.keySet().containsAll(Arrays.asList("Key1", "Key2", "Key3")));

        List<String> keys = new ArrayList<>();
        map.keySet().iterator().forEachRemaining(keys::add);
        assertEquals(2, keys.size());
        assertTrue(keys.containsAll(Arrays.asList("Key1", "Key2")));

        Object[] objArray = map.keySet().toArray();
        assertEquals(2, objArray.length);
        assertTrue(keys.containsAll(Arrays.asList(objArray)));

        String[] sArray = map.keySet().toArray(new String[0]);
        assertEquals(2, sArray.length);
        assertTrue(keys.containsAll(Arrays.asList(sArray)));

        sArray = map.keySet().toArray(new String[2]);
        assertEquals(2, sArray.length);
        assertTrue(keys.containsAll(Arrays.asList(sArray)));
    }

    @Test
    public void values() {
        map.put("Key1", "Value");
        map.put("Key2", "Value");

        assertEquals(2, map.values().size());
        assertFalse(map.values().isEmpty());

        assertTrue(map.values().contains("Value"));
        assertFalse(map.values().contains("Foo"));
        assertTrue(map.values().containsAll(Arrays.asList("Value", "Value")));
        assertFalse(map.values().containsAll(Arrays.asList("Value", "Foo")));

        List<String> values = new ArrayList<>();
        map.values().iterator().forEachRemaining(values::add);
        assertEquals(2, values.size());
        assertTrue(values.containsAll(Arrays.asList("Value", "Value")));

        Object[] objArray = map.values().toArray();
        assertEquals(2, objArray.length);
        assertTrue(values.containsAll(Arrays.asList(objArray)));

        String[] sArray = map.values().toArray(new String[0]);
        assertEquals(2, sArray.length);
        assertTrue(values.containsAll(Arrays.asList(sArray)));

        sArray = map.values().toArray(new String[2]);
        assertEquals(2, sArray.length);
        assertTrue(values.containsAll(Arrays.asList(sArray)));
    }

    @Test
    public void entrySet() {
        map.put("Key1", "Value");
        map.put("Key2", "Value");

        Map<String, String> map2 = new HashMap<>();
        map2.put("Key1", "Value");
        map2.put("Key2", "Value");

        assertEquals(2, map.entrySet().size());
        assertFalse(map.entrySet().isEmpty());

        map2.entrySet().forEach((e) -> assertTrue(map.entrySet().contains(e)));
        map.entrySet().containsAll(map2.entrySet());

        List<Entry<String, String>> entries = new ArrayList<>();
        map.entrySet().iterator().forEachRemaining(entries::add);
        assertEquals(2, entries.size());
        assertTrue(map2.entrySet().containsAll(entries));

        Object[] objArray = map.entrySet().toArray();
        assertEquals(2, objArray.length);
        Arrays.asList(objArray).forEach((o) -> {
            Entry e = (Entry) o;
            assertTrue(map.containsKey((String) e.getKey()));
            assertTrue(map.containsValue((String) e.getValue()));
        });

        Entry[] eArray = map.entrySet().toArray(new Entry[0]);
        assertEquals(2, eArray.length);
        Arrays.asList(eArray).forEach((e) -> {
            assertTrue(map.containsKey((String) e.getKey()));
            assertTrue(map.containsValue((String) e.getValue()));
        });

        eArray = map.entrySet().toArray(new Entry[2]);
        assertEquals(2, eArray.length);
        Arrays.asList(eArray).forEach((e) -> {
            assertTrue(map.containsKey((String) e.getKey()));
            assertTrue(map.containsValue((String) e.getValue()));
        });
    }

    @Test
    public void putRemoveIterate() {
        String key = "KEY";
        String value = "VALUE";

        OHMap<String, String> ohMap = new OHMap<>(10);

        ohMap.put(key, value);
        ohMap.remove(key);

        Iterator<String> iterator = ohMap.values().iterator();

        assertFalse(iterator.hasNext());
        assertTrue(ohMap.isEmpty());

        ohMap.put(key, value);

        iterator = ohMap.values().iterator();

        assertTrue(iterator.hasNext());
        assertEquals(value, iterator.next());
        assertFalse(ohMap.isEmpty());

        ohMap.remove(key);

        iterator = ohMap.values().iterator();

        assertFalse(iterator.hasNext());
        assertTrue(ohMap.isEmpty());
    }
}
