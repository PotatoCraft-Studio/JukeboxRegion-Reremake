package us.blockbox.jukeboxregion;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import us.blockbox.customjukebox.customjukebox.CustomJukeboxAPI;

import java.util.HashMap;
import java.util.Map;

public class LoopTaskManager {
    private final JavaPlugin plugin;
    private final CustomJukeboxAPI cjb;
    private final Map<Player, SongLoopTask> map;

    public LoopTaskManager(final JavaPlugin plugin, final CustomJukeboxAPI cjb) {
        this.plugin = plugin;
        this.cjb = cjb;
        this.map = new HashMap<>(Math.max(16, plugin.getServer().getMaxPlayers() / 2));
    }

    public boolean start(final Player player, final RegionSong song) {
        final long duration = this.cjb.getDuration(song.getName());
        if (duration > 0L) {
            final SongLoopTask task = new SongLoopTask(this.plugin, player, song, duration);
            task.start();
            this.map.put(player, task);
            return true;
        }
        return false;
    }

    public SongLoopTask get(final Player player) {
        return this.map.get(player);
    }

    public boolean stop(final Player player) {
        final SongLoopTask task = this.map.remove(player);
        if (task == null) {
            return false;
        }
        task.stop();
        return true;
    }
}
