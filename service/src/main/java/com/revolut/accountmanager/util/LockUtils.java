package com.revolut.accountmanager.util;

import com.revolut.accountmanager.exception.UnableToAcquireLockException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Slf4j
public class LockUtils {

    public static final int MAX_LOCK_WAIT_TIME_MILLIS = 2000;

    public static <T> void executeInLockingManner(ReentrantLock lock, Consumer<T> consumer, T t) throws UnableToAcquireLockException {
        try{
            lock.tryLock(MAX_LOCK_WAIT_TIME_MILLIS, TimeUnit.MILLISECONDS);
            try{
                consumer.accept(t);
            }finally {
                lock.unlock();
            }
        }catch (InterruptedException ie){
            log.error("Timeout while acquiring lock", ie);
            throw new UnableToAcquireLockException("Timeout while acquiring lock");
        }
    }
}
