package org.black_ixx.playerpoints.storage.exports;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.config.RootConfig;
import org.black_ixx.playerpoints.storage.StorageType;

/**
 * Exports data from a source to YAML.
 * 
 * @author Mitsugaru
 */
public class Exporter {

    /**
     * Plugin instance.
     */
    private final PlayerPoints plugin;

    /**
     * Contructor.
     *
     * @param plugin - Plugin instance.
     */
    public Exporter(final PlayerPoints plugin) {
        this.plugin = plugin;
    }

    /**
     * Check whether we need to import and where we are importing from.
     */
    public void checkExport() {
        final RootConfig config = plugin.getModuleForClass(RootConfig.class);
        if (config.exportSQL) {
            exportSQL(config.exportSource);
            plugin.getConfig().set("mysql.export.use", false);
            plugin.saveConfig();
        }
    }

    /**
     * Imports from SQLite / YAML to MYSQL.
     *
     * @param source - Type of storage to read from.
     */
    private void exportSQL(final StorageType source) {
        switch (source) {
            case MYSQL:
                final MySQLExport mysql = new MySQLExport(plugin);
                mysql.doExport();
                break;
            case SQLITE:
                final SQLiteExport sqlite = new SQLiteExport(plugin);
                sqlite.doExport();
            default:
                break;
        }
    }
}
