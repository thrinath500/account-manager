package com.revolut.accountmanager.action;

public abstract class BaseAction<Input, Output> {

    abstract void validate(Input input);

    abstract Output execute(Input input);

    public Output process(Input input){
        validate(input);
        return execute(input);
    }
}
