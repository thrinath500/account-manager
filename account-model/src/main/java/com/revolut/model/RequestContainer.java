package com.revolut.model;

public class RequestContainer {
    private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();

    private static final String TEST_ENV = "TEST_ENV";

    public static void set(String requestId){
        threadLocal.set(requestId);
    }

    public static String get(){
        return threadLocal.get();
    }

    public static void clear(){
        threadLocal.remove();
    }

    // Test variables
    public static void setTestEnv(){
        threadLocal.set(TEST_ENV);
    }

    public static boolean isTestEnv(){
        return threadLocal.get().equals(TEST_ENV);
    }
}
