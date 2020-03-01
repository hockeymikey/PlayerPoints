package org.black_ixx.playerpoints.models;

/**
 * Holder class that will sort based on the points and by the name. Note, this
 * sorts by order of highest points first and uses player name for any matches.
 * 
 * @author Mitsugaru
 */
public class SortedPlayer implements Comparable<SortedPlayer> {

    /**
     * Player name.
     */
    private final String name;

    /**
     * Player points.
     */
    private final int points;

    /**
     * Constructor.
     *
     * @param name   - Player name.
     * @param points - Point amount.
     */
    public SortedPlayer(final String name, final int points) {
        this.name   = name;
        this.points = points;
    }

    /**
     * Get the player name.
     * 
     * @return Name of player.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the player points.
     * 
     * @return Point amount.
     */
    public int getPoints() {
        return points;
    }

    @Override
    public int compareTo(final SortedPlayer o) {
        if (this.points > o.points) {
            return -1;
        }
        else if (this.points < o.points) {
            return 1;
        }
        return this.name.compareTo(o.name);
    }
}
