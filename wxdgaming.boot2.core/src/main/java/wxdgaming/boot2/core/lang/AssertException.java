package wxdgaming.boot2.core.lang;

import wxdgaming.boot2.core.Throw;

public class AssertException extends RuntimeException {

    public AssertException(String message) {
        this(message, Throw.StackTraceEmpty);
    }

    public AssertException(String message, StackTraceElement[] stackTrace) {
        super(message);
        setStackTrace(stackTrace);
    }

    @Override public synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override public String toString() {
        return getMessage();
    }

}
