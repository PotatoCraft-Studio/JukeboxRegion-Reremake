package us.blockbox.jukeboxregion;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class RegionMonitor extends BukkitRunnable {
    private final Server server;
    private final RegionSongManager songManager;
    private final PlayerSongManager playerSongManager;

    public RegionMonitor(final Server server, final RegionSongManager songManager, final PlayerSongManager playerSongManager) {
        this.server = server;
        this.songManager = songManager;
        this.playerSongManager = playerSongManager;
    }

    public void run() {
        for (final Player player : this.server.getOnlinePlayers()) {
            final RegionSong song = this.songManager.getSongAt(player.getLocation());
            if (song != null) {
                this.playerSongManager.play(player, song);
            } else {
                this.playerSongManager.stop(player);
            }
        }
    }
}
