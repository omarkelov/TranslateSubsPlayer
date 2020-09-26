package ru.nsu.fit.markelov.util;

/**
 * Closure interface is a simple functional interface with the only method: call().
 *
 * @author Oleg Markelov
 */
@FunctionalInterface
public interface Closure {
    /**
     * Invokes the closure.
     */
    void call();
}
