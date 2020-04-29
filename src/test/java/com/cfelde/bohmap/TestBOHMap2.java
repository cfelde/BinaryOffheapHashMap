/**
 * Copyright 2014 Christian Felde (cfelde [at] cfelde [dot] com)
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.cfelde.bohmap;

import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests related to an earlier stack overflow error issue
 *
 * @author cfelde
 */
public class TestBOHMap2 {
    private Random random;
    private BOHMap map;
    
    public TestBOHMap2() {
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
        map = new BOHMap(1024 * 1024);
    }
    
    @After
    public void tearDown() {
        map.clear();
    }
    
    @Test
    public void keySetHasNext() {
        assertFalse(map.keySet().iterator().hasNext());
    }
    
    @Test
    public void keySetNext() {
        boolean gotException = false;
        try {
            map.keySet().iterator().next();
        } catch (NoSuchElementException ex) {
            gotException = true;
        }
        
        assertTrue(gotException);
    }
    
    @Test
    public void valuesHasNext() {
        assertFalse(map.values().iterator().hasNext());
    }
    
    @Test
    public void valuesNext() {
        boolean gotException = false;
        try {
            map.values().iterator().next();
        } catch (NoSuchElementException ex) {
            gotException = true;
        }
        
        assertTrue(gotException);
    }
    
    @Test
    public void entrySetHasNext() {
        assertFalse(map.entrySet().iterator().hasNext());
    }
    
    @Test
    public void entrySetNext() {
        boolean gotException = false;
        try {
            map.entrySet().iterator().next();
        } catch (NoSuchElementException ex) {
            gotException = true;
        }
        
        assertTrue(gotException);
    }
}
