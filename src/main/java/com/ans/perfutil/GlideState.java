package com.ans.perfutil;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.LivingEntity;

public final class GlideState {
    private static final int GRACE = Integer.getInteger("ans.glideGrace", 6);
    private static final Map<LivingEntity, long[]> M = new WeakHashMap<>();

    private GlideState() {
    }

    public static synchronized boolean gliding(LivingEntity var0) {
        long var1 = var0.tickCount;
        long[] var3 = M.get(var0);
        if (var0.isFallFlying()) {
            if (var3 != null && var1 - var3[0] <= GRACE) {
                var3[0] = var1;
            } else {
                M.put(var0, new long[]{var1, var1});
            }

            return true;
        } else {
            return GRACE > 0 && var3 != null && var1 - var3[0] <= GRACE && !var0.onGround() && !var0.isInWater();
        }
    }

    public static synchronized float ticks(LivingEntity var0, float var1) {
        long[] var2 = M.get(var0);
        return var2 == null ? 0.0F : (float)(var0.tickCount - var2[1]) + var1;
    }
}
