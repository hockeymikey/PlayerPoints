package org.black_ixx.playerpoints.storage.imports;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.storage.IStorage;
import org.black_ixx.playerpoints.storage.StorageType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Import from YAML to MySQL.
 *
 * @author Mitsugaru
 */
@SuppressWarnings("WeakerAccess")
public class YAMLImport extends DatabaseImport {

    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public YAMLImport(final PlayerPoints plugin) {
        super(plugin);
    }

    @Override
    void doImport() {
        plugin.getLogger().info("Importing YAML to MySQL");
        final IStorage mysql = generator
                .createStorageHandlerForType(StorageType.MYSQL);
        final ConfigurationSection config = YamlConfiguration
                .loadConfiguration(new File(plugin.getDataFolder()
                                                    .getAbsolutePath() + "/storage.yml"));
        final ConfigurationSection points = config
                .getConfigurationSection("Points");
        assert points != null;
        points.getKeys(false).forEach(key -> mysql.setPoints(key, points.getInt(key)));
    }

}
