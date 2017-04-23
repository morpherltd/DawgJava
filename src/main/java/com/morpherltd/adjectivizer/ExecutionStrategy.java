package com.morpherltd.adjectivizer;

/**
 * This is a kind of functional interface and can therefore be used as the assignment target instead of lambda expression or method reference.
 * Represents a Java 1.8 Function interface that accepts one argument and produces a result.
 *
 * @param <T>   the type of the input to the function
 * @param <R>   the type of the result of the function
 */

interface ExecutionStrategy<T, R> {

    /**
     *  Applies this function to the given argument.
     *
     * @param t     the function argument
     * @return      the function result
     */
    R apply(T t);
}
