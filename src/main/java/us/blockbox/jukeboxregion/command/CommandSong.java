package us.blockbox.jukeboxregion.command;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import us.blockbox.customjukebox.customjukebox.CustomJukeboxAPI;
import us.blockbox.jukeboxregion.RegionSongManager;
import us.blockbox.jukeboxregion.RegionUtils;

public class CommandSong implements CommandExecutor {
    private static final ChatColor accent;

    static {
        accent = ChatColor.AQUA;
    }

    private final RegionSongManager songManager;
    private final CustomJukeboxAPI cjb;
    private final ChatColor failed;

    public CommandSong(final RegionSongManager songManager, final CustomJukeboxAPI cjb) {
        this.failed = ChatColor.GRAY;
        this.songManager = songManager;
        this.cjb = cjb;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!sender.hasPermission("jukeboxregion.admin")) {
            sender.sendMessage(this.failed + "You don't have permission.");
            return true;
        }
        if (args.length < 1) {
            return false;
        }
        final ProtectedRegion region = RegionUtils.getRegion(args[0]);
        if (region == null) {
            sender.sendMessage(this.failed + "No such region.");
            return true;
        }
        if (args.length < 2) {
            final String song = this.songManager.getSong(region);
            if (song == null) {
                sender.sendMessage(CommandSong.accent + "No song set for region " + region.getId() + ".");
            } else {
                sender.sendMessage(CommandSong.accent + "Song for region " + region.getId() + ": " + song);
            }
        } else {
            final String discName = args[1];
            if (this.cjb.getDiscNames().containsValue(discName)) {
                this.songManager.setSong(region, discName);
                sender.sendMessage(CommandSong.accent + "Set song for region " + region.getId() + " to " + discName + '.');
            } else {
                sender.sendMessage(this.failed + "No such custom disc: " + discName);
            }
        }
        return true;
    }
}
