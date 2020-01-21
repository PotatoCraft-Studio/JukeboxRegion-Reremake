package us.blockbox.jukeboxregion;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RegionSongManager {
    private final WorldGuardPlatform platform;
    private final Map<ProtectedRegion, String> map;

    public RegionSongManager() {
        this.platform = WorldGuard.getInstance().getPlatform();
        this.map = new HashMap<>(32);
    }

    public Map<ProtectedRegion, String> getMap() {
        return Collections.unmodifiableMap(this.map);
    }

    public void setSong(final ProtectedRegion region, final String song) {
        this.map.put(region, song);
    }

    public String getSong(final ProtectedRegion region) {
        return this.map.get(region);
    }

    public RegionSong getSongAt(final Location location) {
        final RegionManager manager = this.platform.getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
        if (manager == null) {
            return null;
        }
        final ApplicableRegionSet regions = manager.getApplicableRegions(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        final ProtectedRegion region = this.getHighestPriorityWithSong(regions.getRegions());
        if (region == null) {
            return null;
        }
        final String s = this.map.get(region);
        return new RegionSong(s, region);
    }

    private ProtectedRegion getHighestPriorityWithSong(final Set<ProtectedRegion> regionSet) {
        int highestPriority = Integer.MIN_VALUE;
        ProtectedRegion highestWithSong = null;
        for (final ProtectedRegion region : regionSet) {
            final int priority = region.getPriority();
            if (priority > highestPriority && this.map.containsKey(region)) {
                highestPriority = priority;
                highestWithSong = region;
            }
        }
        return highestWithSong;
    }
}
