package se.mickelus.tetra.module.schema;

public class InvalidSchemaException extends Exception {

    private final String key;
    private final String[] faultyModules;

    public InvalidSchemaException(String key, String[] faultyModules) {
        this.key = key;
        this.faultyModules = faultyModules;
    }

    public void printMessage() {
        System.err.printf("Skipping schema '%s' due to faulty module keys:%n", key);
        for (String faultyKey : faultyModules) {
            System.err.println("\t" + faultyKey);
        }
    }
}
