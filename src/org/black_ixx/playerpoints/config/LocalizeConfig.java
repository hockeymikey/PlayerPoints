package org.black_ixx.playerpoints.config;

import de.leonhard.storage.Yaml;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.models.Flag;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map.Entry;

/**
 * Handles user-based messages.
 *
 * @author Mitsugaru
 */
public class LocalizeConfig {

    /**
     * YAML config.
     */
    private static Yaml config;

    /**
     * Map of config keys to values.
     */
    private static final EnumMap<LocalizeNode, String> MESSAGES = new EnumMap<>(
            LocalizeNode.class);

    /**
     * Initialize.
     *
     * @param pp - Plugin instance.
     */
    public static void init(final PlayerPoints pp) {
        /*
         * Plugin instance.
         */
        /*
         * File reference.
         */
        config = new Yaml("localization", pp.getDataFolder().getAbsolutePath());
        loadDefaults();
        loadMessages();
    }

    private static void save() {
        // Set config
        // Save the file
        config.forceReload();
    }

    public static void reload() {
        config.forceReload();
        MESSAGES.clear();
        loadDefaults();
        loadMessages();
    }

    public static void set(final String path, final Object o) {
        config.set(path, o);
        save();
    }

    public static String getString(final String path, final String def) {
        return config.getOrDefault(path, def);
    }

    private static void loadDefaults() {
        // Add to config if missing
        Arrays.stream(LocalizeNode.values()).filter(node -> !config.contains(node.getPath())).forEach(node -> config.set(node.getPath(), node.getDefaultValue()));
        save();
    }

    private static void loadMessages() {
        Arrays.stream(LocalizeNode.values()).forEach(node -> MESSAGES.put(node,
                                                                          config.getOrDefault(node.getPath(), node.getDefaultValue())));
    }

    public static String parseString(final LocalizeNode node,
                                     final EnumMap<Flag, String> replace) {
        /*
         * Thanks to @Njol for the following
         * http://forums.bukkit.org/threads/multiple
         * -classes-config-colours.79719/#post-1154761
         */
        String out = ChatColor.translateAlternateColorCodes('&',
                                                            MESSAGES.get(node));
        if (replace != null) {
            for (final Entry<Flag, String> entry : replace.entrySet()) {
                out = out
                        .replaceAll(entry.getKey().getFlag(), entry.getValue());
            }
        }
        return out;
    }

}
