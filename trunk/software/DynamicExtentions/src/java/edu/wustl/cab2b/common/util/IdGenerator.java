package edu.wustl.cab2b.common.util;


/**
 * Class to be used for ID generation.
 * For use create a instance of this class, and use it for id generation.
 * Any number of instances are allowed for this class.
 * @author Chandrakant Talele
 */
public class IdGenerator {

    /**
     * Maintains next available id
     */
    private long nextAvailableId;

    /**
     * @param value Initial value for id.
     */
    public IdGenerator(long value) {
        nextAvailableId = value;
    }
    
    /**
     * @return next available id.
     */
    public synchronized long getNextId() {
        return nextAvailableId++;
    }
}