package edu.wustl.cab2b.server.path;

/**
 * Enumeration to represents possible value which column "ASSOCIATION.ASSOCIATION_TYPE" can take.
 * @author Chandrakant Talele
 */
public enum AssociationType {
    INTER_MODEL_ASSOCIATION(1), INTRA_MODEL_ASSOCIATION(2);
    private int value;

    AssociationType(int value) {
        this.value = value;
    }

    /**
     * @return integer value associated with enumeration
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the type.
     *
     * @param value
     *            Get type based in integer value
     * @return Enumeration
     */
    public static AssociationType getType(int value)
    {
        AssociationType retType = null; // NOPMD by gaurav_sawant
        if (value == 1)
        {
            retType = AssociationType.INTER_MODEL_ASSOCIATION; // NOPMD by gaurav_sawant
        }
        if (value == 2)
        {
            retType = AssociationType.INTRA_MODEL_ASSOCIATION;
        }
        else
        {
            throw new IllegalArgumentException();
        }
        return retType;
    }
}