package com.ans.perfutil;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.decoration.ArmorStand;

public final class ArmorStandHurt {
    private static final Map<ArmorStand, long[]> STATE = new WeakHashMap<>();

    private ArmorStandHurt() {
    }

    public static synchronized double direction(ArmorStand var0) {
        long[] var1 = STATE.get(var0);
        if (var1 == null) {
            var1 = new long[]{var0.lastHit, 0L};
            STATE.put(var0, var1);
        }

        if (var0.lastHit != var1[0]) {
            var1[0] = var0.lastHit;
            var1[1] = var1[1] < 0L ? 1L : -1L;
        }

        return var1[1];
    }
}
