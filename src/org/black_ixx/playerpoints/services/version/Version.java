package org.black_ixx.playerpoints.services.version;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

/**
 * Represents a semantic version.
 *
 * @author Mitsugaru
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Version implements Comparable<Version> {

    private static final Pattern COMPILE = Pattern.compile("\\s");
    /**
     * Raw version as string.
     */
    private final String version;

    /**
     * Integer separator for versions.
     */
    private static final String SEPARATOR = ".";

    /**
     * Whether we should ignore the patch number, allowing for valid versions
     * with only two values.
     */
    private boolean ignorePatch;

    /**
     * Constructor.
     * 
     * @param version
     *            - Raw version number as string.
     */
    public Version(final String version) {
        this.version = COMPILE.matcher(version).replaceAll("");
    }

    /**
     * Get the version string.
     * 
     * @return Version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the major number of this version.
     * 
     * @return Major number.
     */
    public int getMajor() {
        return parseNumber(version.substring(0, version.indexOf(SEPARATOR)));
    }

    /**
     * Get the minor number of this version.
     * 
     * @return Minor number.
     */
    public int getMinor() {
        final int first = version.indexOf(SEPARATOR);
        int second = version.indexOf(SEPARATOR, first + 1);
        if(ignorePatch) {
            second = version.length();
            if(getLastSeparatorIndex() > 0) {
                second = getLastSeparatorIndex();
            }
        }
        return parseNumber(version.substring(first + 1, second));
    }

    /**
     * Get the patch number for this version.
     * 
     * @return Patch number.
     */
    public int getPatch() {
        if(ignorePatch) {
            return 0;
        }
        final int first = version.indexOf(SEPARATOR, version.indexOf(SEPARATOR) + 1) + 1;
        int second = version.length();
        if(getLastSeparatorIndex() > 0) {
            second = getLastSeparatorIndex();
        }
        return parseNumber(version.substring(first, second));
    }

    /**
     * Get the last separator index, either the prelease or metadata symbol
     * index, whichever one comes first.
     * 
     * @return Last separator index. Returns -1 if none is found.
     */
    private int getLastSeparatorIndex() {
        int last = -1;
        final PreReleaseType type = getType();
        if(!type.equals(PreReleaseType.NONE)) {
            last = version.indexOf('-');
        } else {
            final Metadata meta = getMetadata();
            if(!meta.equals(Metadata.NONE)) {
                last = version.indexOf('+');
            }
        }
        return last;
    }

    /**
     * Get the version type flag.
     * 
     * @return Version type flag.
     */
    public PreReleaseType getType() {
        final int first = version.indexOf('-');
        int second = version.length();
        if(version.contains("+")) {
            second = version.indexOf('+');
        }
        if(first >= 0) {
            try {
                return new PreReleaseType(version.substring(first + 1, second));
            }
            catch (final IndexOutOfBoundsException ignored) {

            }
        }
        return PreReleaseType.NONE;
    }

    /**
     * Get the version metadata.
     * 
     * @return Metadata.
     */
    public Metadata getMetadata() {
        final int first = version.indexOf('+');
        if(first >= 0) {
            try {
                return new Metadata(version.substring(first + 1
                ));
            }
            catch (final IndexOutOfBoundsException ignored) {

            }
        }
        return Metadata.NONE;
    }

    /**
     * Whether or not this version should ignore the patch number.
     * 
     * @return True if patch is ignored, else false.
     */
    public boolean isIgnorePatch() {
        return ignorePatch;
    }

    /**
     * Set the ignore patch flag for this version.
     *
     * @param ignorePatch - Ignore flag.
     */
    public void setIgnorePatch(final boolean ignorePatch) {
        this.ignorePatch = ignorePatch;
    }

    /**
     * Validates the version object.
     * 
     * @return True if valid, else false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean validate() {
        boolean valid;
        try {
            getMajor();
            getMinor();
            getPatch();
            if (ignorePatch) {
                valid = StringUtils.countMatches(version, SEPARATOR) >= 1;
            }
            else {
                valid = StringUtils.countMatches(version, SEPARATOR) >= 2;
            }
            valid = valid && StringUtils.countMatches(version, "-") <= 1;
            valid = valid && StringUtils.countMatches(version, "+") <= 1;
        }
        catch (final IndexOutOfBoundsException | NumberFormatException e) {
            valid = false;
        }
        return valid;
    }

    /**
     * Attempts to parse the given string for an integer. Will guarantee a
     * non-negative number.
     *
     * @param in
     *            - String to parse.
     * @return Non-negative number. Returns 0 if there was an issue parsing the
     *         number.
     */
    private int parseNumber(final String in) {
        final int number = Integer.parseInt(in);
        if (number < 0) {
            throw new NumberFormatException(
                    "No negative numbers in a version string.");
        }
        return number;
    }

    @Override
    public int compareTo(final Version o) {
        if (getMajor() != o.getMajor()) {
            return getMajor() - o.getMajor();
        }
        else if (getMinor() != o.getMinor()) {
            return getMinor() - o.getMinor();
        }
        else if (getPatch() != o.getPatch()) {
            return getPatch() - o.getPatch();
        }
        else if (getType() != o.getType()) {
            return o.getType().compareTo(getType());
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return version.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Version) {
            return version.equals(((Version) obj).version);
        }
        return false;
    }

    @Override
    public String toString() {
        return version;
    }

}
