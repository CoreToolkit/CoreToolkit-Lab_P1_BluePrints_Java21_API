package edu.eci.arsw.blueprints.persistence;

public class BlueprintAlreadyExistsException extends BlueprintPersistenceException {
    public BlueprintAlreadyExistsException(String msg) { super(msg); }
}
