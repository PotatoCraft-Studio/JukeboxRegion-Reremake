package us.blockbox.jukeboxregion;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import us.blockbox.customjukebox.customjukebox.CustomJukebox;
import us.blockbox.customjukebox.customjukebox.CustomJukeboxAPI;
import us.blockbox.customjukebox.customjukebox.CustomJukeboxAPIImpl;

public class SongLoopTask {
    private final JavaPlugin plugin;
    private final Player player;
    private final RegionSong song;
    private final long lengthSeconds;
    private BukkitRunnable regionCheck;
    private BukkitRunnable loop;

    public SongLoopTask(final JavaPlugin plugin, final Player player, final RegionSong song, final long lengthSeconds) {
        this.plugin = plugin;
        this.player = player;
        this.song = song;
        this.lengthSeconds = lengthSeconds;
    }

    public void start() {
        if (this.regionCheck == null && this.loop == null) {
            (this.regionCheck = new RegionCheckTask()).runTaskTimer(this.plugin, 20L, 20L);
            if (this.lengthSeconds > 0L) {
                this.loop = new LoopTask();
                final long interval = (this.lengthSeconds + 1L) * 20L;
                this.loop.runTaskTimerAsynchronously(this.plugin, 0L, interval);
            } else {
                this.plugin.getLogger().severe("Cannot determine length of song " + this.song.getName() + "! Music will not play. Please read the installation instructions on the JukeboxRegion resource page.");
            }
        }
    }

    public void stop() {
        if (this.regionCheck != null) {
            this.regionCheck.cancel();
            this.regionCheck = null;
            this.player.stopSound(this.song.getName());
            this.player.stopSound(this.song.getName(), SoundCategory.RECORDS);
        }
        if (this.loop != null) {
            this.loop.cancel();
            this.loop = null;
        }
    }

    public RegionSong getSong() {
        return this.song;
    }

    private final class LoopTask extends BukkitRunnable {
        public void run() {
            new BukkitRunnable() {
                public void run() {
                    final String name = SongLoopTask.this.song.getName();
                    SongLoopTask.this.player.playSound(SongLoopTask.this.player.getLocation(),name, SoundCategory.RECORDS,10.0f,1.0f);
                }
            }.runTask(SongLoopTask.this.plugin);
        }
    }

    private final class RegionCheckTask extends BukkitRunnable {
        public void run() {
            final Location location = SongLoopTask.this.player.getLocation();
            if (!SongLoopTask.this.song.getRegion().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                SongLoopTask.this.stop();
            }
        }
    }
}
