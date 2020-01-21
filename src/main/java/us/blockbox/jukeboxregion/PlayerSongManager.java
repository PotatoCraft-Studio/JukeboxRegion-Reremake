package us.blockbox.jukeboxregion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSongManager {
    private final LoopTaskManager loopTaskManager;
    private final Map<UUID, RegionSong> currentSong;

    public PlayerSongManager(final LoopTaskManager loopTaskManager) {
        this.currentSong = new HashMap<>();
        this.loopTaskManager = loopTaskManager;
    }

    public RegionSong get(final Player player) {
        return this.get(player.getUniqueId());
    }

    public RegionSong get(final UUID uuid) {
        return this.currentSong.get(uuid);
    }

    public void play(final Player player, final RegionSong regionSong) {
        final UUID uuid = player.getUniqueId();
        final RegionSong old = this.currentSong.get(uuid);
        if (old != null) {
            if (old.equals(regionSong)) {
                return;
            }
            this.loopTaskManager.stop(player);
        }
        this.currentSong.put(uuid, regionSong);
        this.loopTaskManager.start(player, regionSong);
    }

    public void stop(final Player player) {
        if (this.currentSong.remove(player.getUniqueId()) != null) {
            this.loopTaskManager.stop(player);
        }
    }
}
