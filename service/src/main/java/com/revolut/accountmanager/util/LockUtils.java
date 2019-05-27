package com.revolut.accountmanager.util;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LockUtils {

    public static <T> void executeInLockingManner(ReentrantLock lock, Consumer<T> consumer, T t){
        lock.lock();
        try{
            consumer.accept(t);
        }finally {
            lock.unlock();
        }
    }
}
