package org.black_ixx.playerpoints.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@SuppressWarnings({"WeakerAccess", "unused"})
public class PlayerPointsEvent extends Event implements Cancellable {
    /**
     * Handler list.
     */
    private static final HandlerList handlers = new HandlerList();
    /**
     * Player whose points is changing.
     */
    private final UUID playerId;
    /**
     * Amount their points are being changed by. Note, this is NOT the final
     * amount that the player's points balance will be. This is the amount to
     * modify their current balance by.
     */
    private int change;
    /**
     * Cancelled flag.
     */
    private boolean cancelled;

    /**
     * Constructor.
     *
     * @param id     - Id of player.
     * @param change - Amount of change that will apply to their current balance.
     */
    public PlayerPointsEvent(final UUID id, final int change) {
        this.playerId = id;
        this.change   = change;
    }

    /**
     * Static method to get HandlerList.
     *
     * @return HandlerList.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the amount of points that the player's balance will change by.
     *
     * @return Amount of change.
     */
    public int getChange() {
        return change;
    }

    /**
     * Set the amount of change that will be used to adjust the player's
     * balance.
     *
     * @param change - Amount of change.
     */
    public void setChange(final int change) {
        this.change = change;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Get the player id.
     *
     * @return Player UUID.
     */
    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
