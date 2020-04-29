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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Default serialization methods used by {@code OHMap} to convert POJOs
 * to and from {@code Binary}.
 *
 * @author cfelde (Christian Felde, cfelde.com)
 */
public class JavaSerializer {
    public static <T> Binary serialize(T o) {
        if (o == null)
            return null;
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        
        return new Binary(baos.toByteArray());
    }
    
    public static <T> T deserialize(Binary b) {
        if (b == null)
            return null;
        
        ByteArrayInputStream bais = new ByteArrayInputStream(b.getValue());
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
