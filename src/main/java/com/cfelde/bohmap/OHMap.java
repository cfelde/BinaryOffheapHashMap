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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A serialization wrapper map around {@code BOHMap}, allowing you to easily
 * put and get any serializable Java object.
 *
 * @author cfelde (Christian Felde, cfelde.com)
 * @param <K> Key type
 * @param <V> Value type
 */
public class OHMap<K extends Serializable, V extends Serializable> implements Map<K, V> {
    private final BOHMap map;
    
    private final Function<Object, Binary> keySerializer;
    private final Function<Binary, Object> keyDeserializer;
    private final Function<Object, Binary> valueSerializer;
    private final Function<Binary, Object> valueDeserializer;
    
    /**
     * Create a new map wrapper around a {@code BOHMap} with the given
     * partition count. The default methods on {@code JavaSerializer} will
     * be used to convert between object and binary form.
     * 
     * @param partitionCount Number of partitions used within {@code BOHMap}
     */
    public OHMap(int partitionCount) {
        this(new BOHMap(partitionCount), JavaSerializer::serialize, JavaSerializer::deserialize, JavaSerializer::serialize, JavaSerializer::deserialize);
    }

    /**
     * Create a new map wrapper around a {@code BOHMap} with the given
     * partition count. Constructor allows you to define what serialization
     * methods to use, in addition to explicitly passing in the underlying
     * {@code BOHMap} instance used by the wrapper.
     * 
     * @param map Underlying {@code BOHMap}
     * @param keySerializer Serialization method for keys
     * @param keyDeserializer Deserialization method for keys
     * @param valueSerializer Serialization method for values
     * @param valueDeserializer Deserialization method for values
     */
    public OHMap(BOHMap map, Function<Object, Binary> keySerializer, Function<Binary, Object> keyDeserializer, Function<Object, Binary> valueSerializer, Function<Binary, Object> valueDeserializer) {
        this.map = map;
        this.keySerializer = keySerializer;
        this.keyDeserializer = keyDeserializer;
        this.valueSerializer = valueSerializer;
        this.valueDeserializer = valueDeserializer;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key == null ? null : keySerializer.apply(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value == null ? null : valueSerializer.apply(value));
    }

    @Override
    public V get(Object key) {
        return (V) valueDeserializer.apply(map.get(keySerializer.apply(key)));
    }

    @Override
    public V put(K key, V value) {
        return (V) valueDeserializer.apply(map.put(keySerializer.apply(key), valueSerializer.apply(value)));
    }

    @Override
    public V remove(Object key) {
        return (V) valueDeserializer.apply(map.remove(keySerializer.apply(key)));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach((k, v) -> put(k, v));
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return new KeySet(map.keySet());
    }

    @Override
    public Collection<V> values() {
        return new Values(map.values());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet(map.entrySet());
    }
    
    private class KeySet implements Set<K> {
        private final Set<Binary> keySet;
        
        private KeySet(Set<Binary> keySet) {
            this.keySet = keySet;
        }
        
        @Override
        public int size() {
            return keySet.size();
        }

        @Override
        public boolean isEmpty() {
            return keySet.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return keySet.contains(keySerializer.apply(o));
        }

        @Override
        public Iterator<K> iterator() {
            final Iterator<Binary> iterator = keySet.iterator();
            
            return new Iterator<K>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public K next() {
                    return (K) keyDeserializer.apply(iterator.next());
                }
            };
        }

        @Override
        public Object[] toArray() {
            Object[] bArray = keySet.toArray();
            Object[] oArray = new Object[bArray.length];
            
            for (int i = 0; i < bArray.length; i++) {
                oArray[i] = keyDeserializer.apply((Binary) bArray[i]);
            }
            
            return oArray;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            int size = size();
            T[] r = a.length >= size ? a : (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
            
            Iterator<K> it = iterator();
            for (int i = 0; i < r.length; i++) {
                if (!it.hasNext()) {
                    // Null terminate
                    r[i] = null;
                    return r;
                }
                
                r[i] = (T) it.next();
            }
            
            return r;
        }

        @Override
        public boolean add(K e) {
            return keySet.add(keySerializer.apply(e));
        }

        @Override
        public boolean remove(Object o) {
            return keySet.remove(keySerializer.apply(o));
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.stream().noneMatch((o) -> (!contains((K)o)));
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            boolean changed = false;
            for (K k : c) {
                changed = add(k) || changed;
            }
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean changed = false;
            for (Object k : c) {
                changed = remove((K)k) || changed;
            }
            return changed;
        }

        @Override
        public void clear() {
            keySet.clear();
        }
    }
    
    private class Values implements Collection<V> {
        private final Collection<Binary> values;
        
        private Values(Collection<Binary> values) {
            this.values = values;
        }

        @Override
        public int size() {
            return values.size();
        }

        @Override
        public boolean isEmpty() {
            return values.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return values.contains(valueSerializer.apply(o));
        }

        @Override
        public Iterator<V> iterator() {
            final Iterator<Binary> iterator = values.iterator();
            
            return new Iterator<V>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public V next() {
                    return (V) valueDeserializer.apply(iterator.next());
                }
            };
        }

        @Override
        public Object[] toArray() {
            Object[] bArray = values.toArray();
            Object[] oArray = new Object[bArray.length];
            
            for (int i = 0; i < bArray.length; i++) {
                oArray[i] = valueDeserializer.apply((Binary) bArray[i]);
            }
            
            return oArray;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            int size = size();
            T[] r = a.length >= size ? a : (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
            
            Iterator<V> it = iterator();
            for (int i = 0; i < r.length; i++) {
                if (!it.hasNext()) {
                    // Null terminate
                    r[i] = null;
                    return r;
                }
                
                r[i] = (T) it.next();
            }
            
            return r;
        }

        @Override
        public boolean add(V e) {
            return values.add(valueSerializer.apply(e));
        }

        @Override
        public boolean remove(Object o) {
            return values.remove(valueSerializer.apply(o));
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.stream().noneMatch((o) -> (!contains((V)o)));
        }

        @Override
        public boolean addAll(Collection<? extends V> c) {
            boolean changed = false;
            for (V k : c) {
                changed = add(k) || changed;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean changed = false;
            for (Object k : c) {
                changed = remove((V)k) || changed;
            }
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            values.clear();
        }
    }
    
    private class EntrySet implements Set<Entry<K, V>> {
        private final Set<Entry<Binary, Binary>> entrySet;
        
        private EntrySet(Set<Entry<Binary, Binary>> entrySet) {
            this.entrySet = entrySet;
        }

        @Override
        public int size() {
            return entrySet.size();
        }

        @Override
        public boolean isEmpty() {
            return entrySet.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            
            Map.Entry entry = (Map.Entry) o;
            
            final K key = (K) entry.getKey();
            final V value = (V) entry.getValue();
            
            return entrySet.contains(new Map.Entry<Binary, Binary>() {
                @Override
                public Binary getKey() {
                    return keySerializer.apply(key);
                }

                @Override
                public Binary getValue() {
                    return valueSerializer.apply(value);
                }

                @Override
                public Binary setValue(Binary value) {
                    throw new UnsupportedOperationException();
                }
            });
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            final Iterator<Entry<Binary, Binary>> iterator = entrySet.iterator();
            
            return new Iterator<Entry<K, V>>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Entry<K, V> next() {
                    final Entry<Binary, Binary> next = iterator.next();
                    
                    return new Entry<K, V>() {
                        @Override
                        public K getKey() {
                            return (K) keyDeserializer.apply(next.getKey());
                        }

                        @Override
                        public V getValue() {
                            return (V) valueDeserializer.apply(next.getValue());
                        }

                        @Override
                        public V setValue(V value) {
                            return (V) valueDeserializer.apply(next.setValue(valueSerializer.apply(value)));
                        }
                    };
                }
            };
        }

        @Override
        public Object[] toArray() {
            Object[] values = new Object[size()];
            
            Iterator<Entry<K, V>> it = iterator();
            for (int i = 0; i < values.length && it.hasNext(); i++) {
                values[i] = it.next();
            }
            
            return values;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            int size = size();
            T[] r = a.length >= size ? a : (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
            
            Iterator<Entry<K, V>> it = iterator();
            for (int i = 0; i < r.length; i++) {
                if (!it.hasNext()) {
                    // Null terminate
                    r[i] = null;
                    return r;
                }
                
                r[i] = (T) it.next();
            }
            
            return r;
        }

        @Override
        public boolean add(Entry<K, V> e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.stream().noneMatch((o) -> (!contains((Entry<K,V>)o)));
        }

        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            entrySet.clear();
        }
    }
}
