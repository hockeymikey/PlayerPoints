package org.black_ixx.playerpoints.services.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents pre-release version types.
 * 
 * @author Mitsugaru
 */
@SuppressWarnings("unused")
public class PreReleaseType implements Comparable<PreReleaseType> {

    /**
     * Empty prerelease type.
     */
    static final PreReleaseType NONE = new PreReleaseType("");

    /**
     * Normal string found in version.
     */
    private final String type;

    /**
     * Base pre-release type.
     */
    private String base;

    /**
     * List of identifiers after the base type.
     */
    private final List<String> identifiers = new ArrayList<>();

    /**
     * Private constructor.
     *
     * @param in - Type's normal string.
     */
    PreReleaseType(final String in) {
        this.type = in;
        try {
            this.base = in.substring(0, in.indexOf('.'));
        }
        catch (final IndexOutOfBoundsException e) {
            this.base = in;
        }

        final String[] ids = in.split("\\.");
        identifiers.addAll(Arrays.asList(ids).subList(1, ids.length));
    }

    public String getType() {
        return type;
    }

    public String getBase() {
        return base;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    @Override
    public String toString() {
        return type;
    }

    @Override
    public int compareTo(final PreReleaseType o) {
        if (type.isEmpty() && !o.type.isEmpty()) {
            return -1;
        }
        int compare = o.type.toLowerCase().compareTo(type.toLowerCase());

        // Compare identifiers if matching base types
        if (compare == 0) {
            final int max = Math.max(identifiers.size(), o.identifiers.size());
            for (int i = 0; i < max; i++) {
                final String ours;
                try {
                    ours = identifiers.get(i);
                }
                catch (final IndexOutOfBoundsException e) {
                    compare = 1;
                    break;
                }
                final String theirs;
                try {
                    theirs = o.identifiers.get(i);
                }
                catch (final IndexOutOfBoundsException e) {
                    compare = -1;
                    break;
                }
                if(theirs.compareTo(ours) != 0) {
                    compare = theirs.compareTo(ours);
                    break;
                }
            }
        }

        return compare;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PreReleaseType) {
            return type.equals(((PreReleaseType) obj).type);
        }
        return false;
    }

}
