package us.blockbox.jukeboxregion;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

class PlayerQuitListener implements Listener {
    private final PlayerSongManager playerSongManager;

    public PlayerQuitListener(final PlayerSongManager playerSongManager) {
        this.playerSongManager = playerSongManager;
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        this.playerSongManager.stop(e.getPlayer());
    }
}
