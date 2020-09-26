package ru.nsu.fit.markelov.util.validation;

/**
 * The <code>Validatable<T></code> interface is used to let validate the object from outside. It
 * provides one method for validating the object: <code>validate()</code>.
 *
 * @author Oleg Markelov
 */
public interface Validatable<T> {
    /**
     * Validates and returns this object. Throws an exception in case it is invalid.
     *
     * @return the object itself.
     * @throws IllegalInputException if any validating parameter is null or illegal.
     */
    T validate() throws IllegalInputException;
}
