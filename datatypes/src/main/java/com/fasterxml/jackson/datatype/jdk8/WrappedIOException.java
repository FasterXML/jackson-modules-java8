package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;

/**
 * {@link IOException} runtime wrapper
 * <p>
 * Wrap an {@link IOException} to a {@link RuntimeException}
 * </p>
 */
public class WrappedIOException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     *
     * @param cause IOException to wrap
     */
    public WrappedIOException(IOException cause) {
        super(cause);
    }

    /**
     * Returns the wrapped {@link IOException}
     *
     * @return the wrapped {@link IOException}
     */
    @Override
    public IOException getCause() {
        return (IOException) super.getCause();
    }
}
