package us.blockbox.jukeboxregion;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import us.blockbox.customjukebox.customjukebox.CustomJukebox;
import us.blockbox.customjukebox.customjukebox.CustomJukeboxAPI;
import us.blockbox.jukeboxregion.command.CommandSong;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class JukeboxRegion extends JavaPlugin {
    private static final String CUSTOM_JUKEBOX = "CustomJukebox";
    private CustomJukeboxAPI cjb;
    private RegionSongManager songManager;
    private long checkInterval;
    private boolean enabledCleanly;

    public JukeboxRegion() {
        this.enabledCleanly = false;
    }

    private static boolean isNewEnough(final String cjbVersion) {
        final String[] split = cjbVersion.split("\\.");
        if (split.length < 3) {
            return false;
        }
        int major;
        int minor;
        int patch;
        try {
            major = Integer.parseInt(split[0]);
            minor = Integer.parseInt(split[1]);
            patch = Integer.parseInt(split[2]);
        } catch (NumberFormatException ex) {
            return false;
        }
        return isNewEnough(major, minor, patch, 1, 0, 7);
    }

    private static boolean isNewEnough(final int major, final int minor, final int patch, final int majorMin, final int minorMin, final int patchMin) {
        if (major > majorMin) {
            return true;
        }
        if (major == majorMin) {
            if (minor > minorMin) {
                return true;
            }
            return minor == minorMin && patch >= patchMin;
        }
        return false;
    }

    public void onEnable() {
        this.hookCustomJukebox();
        if (this.cjb == null) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.initConfig(this.songManager = new RegionSongManager());
        this.getCommand("jr").setExecutor(new CommandSong(this.songManager, this.cjb));
        final LoopTaskManager loopTaskManager = new LoopTaskManager(this, this.cjb);
        final PlayerSongManager playerSongManager = new PlayerSongManager(loopTaskManager);
        final RegionMonitor regionMonitor = new RegionMonitor(this.getServer(), this.songManager, playerSongManager);
        regionMonitor.runTaskTimer(this, 0L, this.checkInterval);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(playerSongManager), this);
        this.enabledCleanly = true;
    }

    public void onDisable() {
        if (this.enabledCleanly) {
            this.writeRegionConfig();
        }
    }

    private void hookCustomJukebox() {
        final Plugin cjb = this.getServer().getPluginManager().getPlugin("CustomJukebox");
        if (cjb.isEnabled()) {
            final String version = cjb.getDescription().getVersion();
                this.cjb = CustomJukebox.getPlugin().getAPI();
                this.getLogger().info("Hooked CustomJukebox " + version + '.');
        } else {
            this.getLogger().warning("Did not find CustomJukebox installed.");
        }
    }

    private void initConfig( final RegionSongManager songManager) {
        this.saveDefaultConfig();
        final FileConfiguration config = this.getConfig();
        this.checkInterval = config.getInt("checkinterval", 20);
        final Set<String> songNames = (Set<String>) this.cjb.getDiscNames().values();
        final Logger log = this.getLogger();
        final ConfigurationSection regions = config.getConfigurationSection("regions");
        for (final String s : regions.getKeys(false)) {
            final String song = regions.getString(s);
            if (songNames.contains(song)) {
                final long duration = this.cjb.getDuration(song);
                if (duration < 0L) {
                    log.severe("Unknown duration for " + song + "! It will not play in regions. Check the JukeboxRegion resource page for instructions.");
                }
            } else {
                log.info("Song \"" + song + "\" is not a " + "CustomJukebox" + " disc. It should still work but this is not yet a supported option.");
            }
            final ProtectedRegion region = RegionUtils.getRegion( s);
            if (region == null) {
                log.warning("No region found by the name of " + s + '.');
            } else {
                log.info("Setting song for " + region.getId() + " to " + song);
                songManager.setSong(region, song);
            }
        }
    }

    private void writeRegionConfig() {
        final FileConfiguration config = this.getConfig();
        final String section = "regions";
        ConfigurationSection regions = config.getConfigurationSection("regions");
        if (regions == null) {
            regions = config.createSection("regions");
        }
        final Logger log = this.getLogger();
        log.info("Writing region song configuration.");
        for (final Map.Entry<ProtectedRegion, String> entry : this.songManager.getMap().entrySet()) {
            final ProtectedRegion key = entry.getKey();
            if (key == null) {
                log.warning("Found a null region, this shouldn't happen.");
            } else {
                final String id = key.getId();
                if (id == null) {
                    log.warning("Found a region with no ID, this shouldn't happen.");
                } else {
                    final String song = entry.getValue();
                    if (song == null) {
                        log.warning("Found null song for region \"" + id + "\", this shouldn't happen.");
                    } else {
                        log.info(id + ": " + song);
                        regions.set(id, song);
                    }
                }
            }
        }
        this.saveConfig();
        log.info("Saved region song configuration.");
    }
}
