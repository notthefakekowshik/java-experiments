package com.kowshik;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockDemo {
    /*
        Two types of locks
        1. Intrinsic lock
           a. Synchronized keyword
        2. Extrinsic lock
           a. ReentrantLock
           b. ReadWriteLock
           c. StampedLock
     */
    public static void main(String[] args) {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    }
}
