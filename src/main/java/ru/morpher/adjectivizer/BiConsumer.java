package ru.morpher.adjectivizer;

/**
 * This is a kind of functional interface and can therefore be used as the assignment target instead of lambda expression or method reference.
 *
 * Represents a Java 1.8 BiConsumer interface that accepts two input arguments and returns no result.
 *
 * @param <T>  the type of the first argument to the operation
 * @param <U>  the type of the second argument to the operation
 */
interface BiConsumer<T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    void accept(T t, U u);

}
