package us.blockbox.jukeboxregion;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionSong {
    private final String name;
    private final ProtectedRegion region;

    public RegionSong(final String name, final ProtectedRegion region) {
        this.name = name;
        this.region = region;
    }

    public String getName() {
        return this.name;
    }

    public ProtectedRegion getRegion() {
        return this.region;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final RegionSong that = (RegionSong) o;
        return this.name.equals(that.name) && this.region.equals(that.region);
    }

    @Override
    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.region.hashCode();
        return result;
    }
}
