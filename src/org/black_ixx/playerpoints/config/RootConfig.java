package org.black_ixx.playerpoints.config;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.services.IModule;
import org.black_ixx.playerpoints.storage.StorageType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Handler for the root plugin config.yml file.
 * 
 * @author Mitsugaru
 */
@SuppressWarnings("WeakerAccess")
public class RootConfig implements IModule {

    /**
     * Plugin instance.
     */
    private final PlayerPoints plugin;
    /**
     * Database info.
     */
    public String host, port, database, user, password, table;
    /**
     * MySQL retry limit.
     */
    public int retryLimit;
    /**
     * Import / export sql, vault and vote options.
     */
    public boolean importSQL;
    public boolean exportSQL;
    public boolean vault;
    public boolean hasPlayedBefore;
    public boolean autocompleteOnline;
    public boolean debugDatabase;
    public boolean debugUUID;
    /**
     * Storage info.
     */
    public StorageType backend, importSource, exportSource;
    public int voteAmount;
    public boolean voteOnline;
    public boolean voteEnabled;

    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public RootConfig(final PlayerPoints plugin) {
        this.plugin = plugin;
    }

    /**
     * Reload the config and the settings in memory.
     */
    public void reloadConfig() {
        // Initial relaod
        plugin.reloadConfig();
        // Grab config
        final ConfigurationSection config = plugin.getConfig();
        // Load settings
        loadSettings(config);
    }

    /**
     * Load the settings from config.
     *
     * @param config - Config to read from.
     */
    private void loadSettings(final ConfigurationSection config) {
        debugDatabase = config.getBoolean("debug.database", false);
        debugUUID = config.getBoolean("debug.uuid", false);
        vault = config.getBoolean("vault", false);
        hasPlayedBefore = config.getBoolean("restrictions.hasPlayedBefore", false);
        voteEnabled = config.getBoolean("vote.enabled", false);
        voteAmount = config.getInt("vote.amount", 100);
        voteOnline = config.getBoolean("vote.online", false);
        autocompleteOnline = config.getBoolean("restrictions.autocompleteOnline", false);
    }

    /**
     * Load storage settings only on plugin enable / disable.
     *
     * @param config
     *            - Config to read from.
     */
    private void loadStorageSettings(final ConfigurationSection config) {
        /*
         * Storage
         */
        final String back = config.getString("storage");
        assert back != null;
        if (back.equalsIgnoreCase("sqlite")) {
            backend = StorageType.SQLITE;
        } else if (back.equalsIgnoreCase("mysql")) {
            backend = StorageType.MYSQL;
        } else if (back.equalsIgnoreCase("yaml")) {
            backend = StorageType.YAML;
        } else
            backend = StorageType.JSON;
        /*
         * SQL info
         */
        host = config.getString("mysql.host", "localhost");
        port = config.getString("mysql.port", "3306");
        database = config.getString("mysql.database", "minecraft");
        user = config.getString("mysql.user", "user");
        password = config.getString("mysql.password", "password");
        table = config.getString("mysql.table", "playerpoints");
        importSQL = config.getBoolean("mysql.import.use", false);
        retryLimit = config.getInt("mysql.retry", 10);
        exportSQL = config.getBoolean("mysql.export.use", false);
        final String databaseImportSource = config.getString(
                "mysql.import.source", "YAML");
        if(databaseImportSource.equalsIgnoreCase("SQLITE")) {
            importSource = StorageType.SQLITE;
        } else {
            importSource = StorageType.YAML;
        }
        final String databaseExportSource = config.getString(
                "mysql.export.source", "MYSQL");
        assert databaseExportSource != null;
        if (databaseExportSource.equalsIgnoreCase("SQLITE")) {
            exportSource = StorageType.SQLITE;
        } else {
            exportSource = StorageType.MYSQL;
        }
    }

    /**
     * Get the storage type to use.
     * 
     * @return Storage type.
     */
    public StorageType getStorageType() {
        return backend;
    }

    @Override
    public void starting() {
     // Grab config
        final ConfigurationSection config = plugin.getConfig();
        // LinkedHashmap of defaults
        final Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.put("storage", "YAML");
        defaults.put("mysql.host", "localhost");
        defaults.put("mysql.port", 3306);
        defaults.put("mysql.database", "minecraft");
        defaults.put("mysql.user", "username");
        defaults.put("mysql.password", "pass");
        defaults.put("mysql.table", "playerpoints");
        defaults.put("mysql.import.use", false);
        defaults.put("mysql.import.source", "YAML");
        defaults.put("mysql.export.use", false);
        defaults.put("mysql.export.source", "SQLITE");
        defaults.put("mysql.retry", 10);
        defaults.put("vote.enabled", false);
        defaults.put("vote.amount", 100);
        defaults.put("vote.online", false);
        defaults.put("restrictions.autocompleteOnline", false);
        defaults.put("restrictions.hasPlayedBefore", false);
        defaults.put("debug.database", false);
        defaults.put("debug.uuid", false);
        defaults.put("vault", false);
        defaults.put("version", plugin.getDescription().getVersion());
        // Insert defaults into config file if they're not present
        for(final Entry<String, Object> e : defaults.entrySet()) {
            if(!config.contains(e.getKey())) {
                config.set(e.getKey(), e.getValue());
            }
        }
        // Save config
        plugin.saveConfig();
        // Load settings
        loadSettings(config);
        loadStorageSettings(config);
    }

    @Override
    public void closing() { }
}
