package com.ans.perfutil;

import com.ans.client.molang.MolangContext;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public final class AvoidMobs {
    private static final boolean ON = !"false".equalsIgnoreCase(System.getProperty("ans.avoidMobs", "true"));
    private static final Set<String> VILLAGER_FEARS = Set.of(
        "zombie", "zombie_villager", "husk", "drowned", "vindicator", "evoker", "pillager", "illusioner", "vex", "ravager", "zoglin"
    );
    private static final Set<String> SKELETON_FEARS = Set.of("wolf");
    private static final Set<String> PIGLIN_FEARS = Set.of("zombified_piglin", "zoglin");
    private static final Set<String> FOX_FEARS = Set.of("wolf", "polar_bear", "player");
    private static final Map<Entity, long[]> CACHE = new WeakHashMap<>();

    private AvoidMobs() {
    }

    private static String id(EntityType<?> var0) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(var0).getPath();
    }

    private static Set<String> fears(String var0) {
        return switch (var0) {
            case "villager", "wandering_trader" -> VILLAGER_FEARS;
            case "skeleton", "stray", "bogged", "parched" -> SKELETON_FEARS;
            case "piglin" -> PIGLIN_FEARS;
            case "fox" -> FOX_FEARS;
            default -> Set.of();
        };
    }

    public static double query(MolangContext var0) {
        Entity var1 = var0.actor;
        if (ON && var1 != null && var1.level() != null) {
            Set var2 = fears(id(var1.getType()));
            if (var2.isEmpty()) {
                return 0.0;
            }

            synchronized (CACHE) {
                long[] var4 = CACHE.get(var1);
                if (var4 != null && var4[0] == var1.tickCount) {
                    return var4[1];
                }
            }

            double var9 = compute(var1, var2) ? 1.0 : 0.0;
            synchronized (CACHE) {
                CACHE.put(var1, new long[]{var1.tickCount, (long)var9});
                return var9;
            }
        } else {
            return 0.0;
        }
    }

    private static boolean compute(Entity var0, Set<String> var1) {
        double var2 = var0.getX() - var0.xo;
        double var4 = var0.getZ() - var0.zo;
        double var6 = Math.sqrt(var2 * var2 + var4 * var4);
        if (var6 < 0.06) {
            return false;
        }

        AABB var8 = var0.getBoundingBox().inflate(8.0, 4.0, 8.0);

        for (Entity var10 : var0.level().getEntities(var0, var8, var1x -> var1x.isAlive() && var1.contains(id(var1x.getType())))) {
            if (!(var10 instanceof Player var11 && (var11.isCrouching() || var11.isSpectator() || var11.isCreative()))) {
                double var15 = var10.getX() - var0.getX();
                double var13 = var10.getZ() - var0.getZ();
                if (var15 * var2 + var13 * var4 < 0.0) {
                    return true;
                }
            }
        }

        return false;
    }
}
