package org.black_ixx.playerpoints;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.black_ixx.playerpoints.services.IModule;
import org.black_ixx.playerpoints.storage.StorageHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Vault economy layer for PlayerPoints.
 * 
 * @author Mitsugaru
 */
@SuppressWarnings("WeakerAccess")
public class PlayerPointsVaultLayer implements Economy, IModule {

    /**
     * Plugin instance.
     */
    private final PlayerPoints plugin;

    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public PlayerPointsVaultLayer(final PlayerPoints plugin) {
        this.plugin = plugin;
    }

    @Override
    public void starting() {
        // Set to low priority. Allow for other, standard economy plugins to
        // supercede ours.
        plugin.getServer().getServicesManager()
                .register(Economy.class, this, plugin, ServicePriority.Low);
    }

    @Override
    public void closing() {
        plugin.getServer().getServicesManager().unregister(Economy.class, this);
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(final double amount) {
        final StringBuilder sb = new StringBuilder();
        final int points = (int) amount;
        sb.append(points).append(" ");
        if (points == 1) {
            sb.append(currencyNameSingular());
        }
        else {
            sb.append(currencyNamePlural());
        }
        return sb.toString();
    }

    @Override
    public String currencyNamePlural() {
        return "Points";
    }

    @Override
    public String currencyNameSingular() {
        return "Point";
    }

    @Override
    public boolean hasAccount(final String playerName) {
        boolean has = false;
        final UUID id = handleTranslation(playerName);
        if (id != null) {
            has = plugin.getModuleForClass(StorageHandler.class).playerEntryExists(id.toString());
        }
        return has;
    }

    @Override
    public boolean hasAccount(final String playerName, final String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(final String playerName) {
        return plugin.getAPI().look(handleTranslation(playerName));
    }

    @Override
    public double getBalance(final String playerName, final String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(final String playerName, final double amount) {
        final int current = plugin.getAPI().look(handleTranslation(playerName));
        return current >= amount;
    }

    @Override
    public boolean has(final String playerName, final String worldName, final double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(final String playerName, final double amount) {
        final int points = (int) amount;
        final boolean result = plugin.getAPI().take(handleTranslation(playerName), points);
        final int balance = plugin.getAPI().look(handleTranslation(playerName));

        final EconomyResponse response;
        if (result) {
            response = new EconomyResponse(amount, balance,
                                           ResponseType.SUCCESS, null);
        }
        else {
            response = new EconomyResponse(amount, balance,
                                           ResponseType.FAILURE, "Lack funds");
        }
        return response;
    }

    @Override
    public EconomyResponse withdrawPlayer(final String playerName, final String worldName,
                                          final double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(final String playerName, final double amount) {
        final int points = (int) amount;
        final boolean result = plugin.getAPI().give(handleTranslation(playerName), points);
        final int balance = plugin.getAPI().look(handleTranslation(playerName));

        final EconomyResponse response;
        if (result) {
            response = new EconomyResponse(amount, balance,
                                           ResponseType.SUCCESS, null);
        }
        else {
            response = new EconomyResponse(amount, balance,
                                           ResponseType.FAILURE, null);
        }
        return response;
    }

    @Override
    public EconomyResponse depositPlayer(final String playerName, final String worldName,
                                         final double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(final String name, final String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                                   "Does not handle banks.");
    }

    @Override
    public EconomyResponse deleteBank(final String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                                   "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankBalance(final String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                                   "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankHas(final String name, final double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                                   "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankWithdraw(final String name, final double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                                   "Does not handle banks.");
    }

    @Override
    public EconomyResponse bankDeposit(final String name, final double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                                   "Does not handle banks.");
    }

    @Override
    public EconomyResponse isBankOwner(final String name, final String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                                   "Does not handle banks.");
    }

    @Override
    public EconomyResponse isBankMember(final String name, final String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                                   "Does not handle banks.");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(final String playerName) {
        // Assume true as the storage handler will dynamically add players.
        return true;
    }

    @Override
    public boolean createPlayerAccount(final String playerName, final String worldName) {
        return createPlayerAccount(playerName);
    }

    private UUID handleTranslation(final String name) {
        UUID id = null;
        try {
            UUID.fromString(name);
        }
        catch (final IllegalArgumentException e) {
            id = plugin.translateNameToUUID(name);
        }
        return id;
    }

    @Override
    public EconomyResponse createBank(final String bank, final OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                                   "Does not handle banks.");
    }

    @Override
    public boolean createPlayerAccount(final OfflinePlayer player) {
        // Assume true as the storage handler will dynamically add players.
        return true;
    }

    @Override
    public boolean createPlayerAccount(final OfflinePlayer player, final String world) {
        // Assume true as the storage handler will dynamically add players.
        return true;
    }

    @Override
    public EconomyResponse depositPlayer(final OfflinePlayer player, final double amount) {
        final int points = (int) amount;
        final boolean result = plugin.getAPI().give(player.getUniqueId(), points);
        final int balance = plugin.getAPI().look(player.getUniqueId());

        final EconomyResponse response;
        if (result) {
            response = new EconomyResponse(amount, balance,
                                           ResponseType.SUCCESS, null);
        }
        else {
            response = new EconomyResponse(amount, balance,
                                           ResponseType.FAILURE, null);
        }
        return response;
    }

    @Override
    public EconomyResponse depositPlayer(final OfflinePlayer player, final String world,
                                         final double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public double getBalance(final OfflinePlayer player) {
        return plugin.getAPI().look(player.getUniqueId());
    }

    @Override
    public double getBalance(final OfflinePlayer player, final String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(final OfflinePlayer player, final double amount) {
        final int current = plugin.getAPI().look(player.getUniqueId());
        return current >= amount;
    }

    @Override
    public boolean has(final OfflinePlayer player, final String world, final double amount) {
        return has(player, amount);
    }

    @Override
    public boolean hasAccount(final OfflinePlayer player) {
        return plugin.getModuleForClass(StorageHandler.class).playerEntryExists(player.getUniqueId().toString());
    }

    @Override
    public boolean hasAccount(final OfflinePlayer player, final String world) {
        return hasAccount(player);
    }

    @Override
    public EconomyResponse isBankMember(final String bank, final OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                                   "Does not handle banks.");
    }

    @Override
    public EconomyResponse isBankOwner(final String bank, final OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
                                   "Does not handle banks.");
    }

    @Override
    public EconomyResponse withdrawPlayer(final OfflinePlayer player, final double amount) {
        final int points = (int) amount;
        final boolean result = plugin.getAPI().take(player.getUniqueId(), points);
        final int balance = plugin.getAPI().look(player.getUniqueId());

        final EconomyResponse response;
        if (result) {
            response = new EconomyResponse(amount, balance,
                                           ResponseType.SUCCESS, null);
        }
        else {
            response = new EconomyResponse(amount, balance,
                                           ResponseType.FAILURE, "Lack funds");
        }
        return response;
    }

    @Override
    public EconomyResponse withdrawPlayer(final OfflinePlayer player, final String world,
                                          final double amount) {
        return withdrawPlayer(player, amount);
    }

}
