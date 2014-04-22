Binary off-heap hashmap for Java 8
==================================

The BOHMap is a hashmap implementation for Java 8 that uses off-heap memory for storing map entries (both key and value).

This allows the map to grow past the available JVM heap space, and at the same time will not impose any significant GC cost for the entries stored in the map. Essentially you can have millions of entries on a very small JVM heap.

It has no external third-party library dependencies, and is 100% Java code, i.e., no native code needed.

The B in BOHMap stands for binary; All keys and values must be in binary form. In order to make it more user friendly from a POJO standpoint, a small serialization wrapper is also made available. This wrapper, called OHMap, will benefit from the same off-heap nature of BOHMap but will allow you to use any serializable Java object as both key and value.

By default the OHMap will use standard Java serialization via ObjectOutputStream and ObjectInputStream, but this can be substituted by any serialization framework of your choice.

A set of tests are also included, but if they’ve missed any use cases and you find a bug, please let me know, thanks.

I've put together a short blog post with some performance numbers: http://blog.cfelde.com/2014/04/only-the-good-die-young-or-move-off-heap/
