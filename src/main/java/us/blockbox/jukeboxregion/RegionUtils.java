package us.blockbox.jukeboxregion;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;

public final class RegionUtils {
    private RegionUtils() {
    }

    public static ProtectedRegion getRegion(final String name) {
        ProtectedRegion region = null;
        final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        if (regionContainer == null) {
            return null;
        }
        final List<RegionManager> loaded = regionContainer.getLoaded();
        if (loaded == null) {
            return null;
        }
        final List<World> worlds = Bukkit.getWorlds();
        for (final World world : worlds) {
            final RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
            if (regionManager == null) {
                continue;
            }
            region = regionManager.getRegion(name);
            if (region != null) {
                break;
            }
        }
        return region;
    }
}
