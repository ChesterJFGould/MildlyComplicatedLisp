package model;

// Just a nice wrapper so I don't have to write new Exception(String.format(...)).
public class Exception extends java.lang.Exception {
    // A nice constructor to format errors
    public Exception(java.lang.String message, Object... args) {
        super(java.lang.String.format(message, args));
    }
}
