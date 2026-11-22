package com.ans.perfutil;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LivingEntity.SwingDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SwingTracker {
    private static final Logger LOG = LoggerFactory.getLogger("ans_swing");
    private static final boolean DEBUG = !"false".equalsIgnoreCase(System.getProperty("ans.swingLog", "false"));
    private static final Map<LivingEntity, long[]> STARTS = new WeakHashMap<>();
    private static final Map<LivingEntity, long[]> PENDING = new WeakHashMap<>();
    private static final int MIN_GAP = Integer.getInteger("ans.swingMinGap", 6);

    private static int eff(int var0) {
        return Math.max(var0, MIN_GAP);
    }

    private SwingTracker() {
    }

    public static synchronized void started(LivingEntity var0, boolean var1) {
        long var2 = var0.tickCount;
        long[] var4 = STARTS.get(var0);
        SwingDescription var5 = var0.getCurrentSwing();
        int var6 = var5 != null ? Math.max(1, var5.durationTicks()) : 6;
        boolean var7 = var1 && (var4 == null || var2 != var4[0]);
        if (DEBUG && var1 && var0 == Minecraft.getInstance().player) {
            LOG.info(
                "[ans_swing] golpe tick={} aceptado={} nuevo={} ticks_desde_el_anterior={} duracion={}",
                var2,
                var1,
                var7,
                var4 == null ? -1L : var2 - var4[0],
                var6
            );
        }

        if (var7 && var4 != null && MIN_GAP > 0 && var2 - var4[0] > 0L && var2 - var4[0] < MIN_GAP) {
            PENDING.put(var0, new long[]{var4[0] + MIN_GAP, eff(var6)});
        } else {
            if (var7) {
                STARTS.put(var0, new long[]{var2, eff(var6)});
                PENDING.remove(var0);
            }
        }
    }

    public static synchronized double attackTime(LivingEntity var0, float var1) {
        long[] var2 = PENDING.get(var0);
        if (var2 != null && var0.tickCount >= var2[0]) {
            STARTS.put(var0, var2);
            PENDING.remove(var0);
        }

        long[] var3 = STARTS.get(var0);
        if (var3 == null) {
            return -1.0;
        }

        double var4 = ((float)(var0.tickCount - var3[0]) + var1) / (float)var3[1];
        return !(var4 >= 1.0) && !(var4 < 0.0) ? var4 : 0.0;
    }
}
