package org.black_ixx.playerpoints.models;

/**
 * Variable flags.
 * 
 * @author Mitsugaru
 */
@SuppressWarnings("unused")
public enum Flag {
    NAME("%name"),
    TAG("%tag"),
    PLAYER("%player"),
    EXTRA("%extra"),
    AMOUNT("%amount");

    /**
     * Flag field.
     */
    private final String flag;

    /**
     * Constructor.
     *
     * @param flag - Field to use.
     */
    Flag(final String flag) {
        this.flag = flag;
    }

    /**
     * Get the flag.
     * 
     * @return Flag.
     */
    public String getFlag() {
        return flag;
    }
}
