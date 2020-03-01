package org.black_ixx.playerpoints.update.modules;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.services.version.Version;
import org.black_ixx.playerpoints.storage.StorageHandler;
import org.black_ixx.playerpoints.update.UpdateModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TwoZeroZeroUpdate extends UpdateModule {

    private final Map<String, Integer> cache = new HashMap<>();

    public TwoZeroZeroUpdate(final PlayerPoints plugin) {
        super(plugin);
        targetVersion = new Version("2.0.0");
    }

    @Override
    public void update() {
        // Translate player names to UUID
        final StorageHandler storageHandler = plugin.getModuleForClass(StorageHandler.class);
        final Collection<String> playerNames = storageHandler.getPlayers();
        playerNames.forEach(playerName -> cache.put(playerName, storageHandler.getPoints(playerName)));
        //Rebuild if necessary
        storageHandler.destroy();
        storageHandler.build();
        // Add entries
        for (final Map.Entry<String, Integer> entry : cache.entrySet()) {
            final UUID id = plugin.translateNameToUUID(entry.getKey());
            storageHandler.setPoints(id.toString(), entry.getValue());
        }
    }

}
