package io.github.mortuusars.envelope.integration.sable;

import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Projects positions out of Sable sub-level plot space into shared world coordinates.
 * Block identity lookups must still use local {@link BlockPos}; use these helpers for all spatial math.
 */
public final class MovingStructureCompat {
    private MovingStructureCompat() {
    }

    public static Vec3 projectOutOfSubLevel(Level level, Vec3 pos) {
        if (level == null || pos == null) {
            return pos;
        }
        return SableCompanion.INSTANCE.projectOutOfSubLevel(level, pos);
    }

    public static Vec3 getGlobalCenter(Level level, BlockPos pos) {
        return projectOutOfSubLevel(level, Vec3.atCenterOf(pos));
    }

    public static double distanceToSqr(Level level, BlockPos from, BlockPos to) {
        return getGlobalCenter(level, from).distanceToSqr(getGlobalCenter(level, to));
    }

    public static double distanceToSqr(Level level, Vec3 from, BlockPos to) {
        return from.distanceToSqr(getGlobalCenter(level, to));
    }

    public static int getDistanceBetween(Level level, BlockPos from, BlockPos to) {
        return (int) Math.sqrt(distanceToSqr(level, from, to));
    }

    public static boolean isWithinRange(Level level, BlockPos from, BlockPos to, double range) {
        return distanceToSqr(level, from, to) <= range * range;
    }

    public static boolean isWithinRange(Level level, Vec3 from, BlockPos to, double range) {
        return distanceToSqr(level, from, to) <= range * range;
    }

    public static BlockPos ascendTowards(Level level, BlockPos originLocal, BlockPos targetLocal, int distance) {
        Vec3 origin = getGlobalCenter(level, originLocal);
        Vec3 target = getGlobalCenter(level, targetLocal);
        Vec3 horizontal = new Vec3(target.x - origin.x, 0, target.z - origin.z);
        if (horizontal.lengthSqr() > 1.0E-4) {
            horizontal = horizontal.normalize().scale(distance);
        } else {
            horizontal = Vec3.ZERO;
        }
        return BlockPos.containing(origin.add(horizontal).add(0, distance, 0));
    }

    public static BlockPos nearestHub(Level level, BlockPos localPos) {
        BlockPos global = BlockPos.containing(getGlobalCenter(level, localPos));
        int x = Math.round(global.getX() / 1024.0F) * 1024;
        int z = Math.round(global.getZ() / 1024.0F) * 1024;
        return new BlockPos(x, 320, z);
    }
}
