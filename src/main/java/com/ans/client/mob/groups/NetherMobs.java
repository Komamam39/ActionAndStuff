package com.ans.client.mob.groups;

import com.ans.client.molang.MolangContext;
import com.ans.client.molang.Node;
import com.ans.client.equipment.MobEquipment;
import com.ans.client.mob.AnimatedMob;
import com.ans.client.mob.nether.HappyGhastHarnessLayer;
import com.ans.client.mob.nether.ShotTracker;
import com.ans.client.molang.EngineVariables;
import com.ans.client.molang.MolangQueries;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.CrossbowItem;

public final class NetherMobs {
    private static final int BLAZE_ENGAGED_TICKS = 100;
    private static final int GHAST_SHOT_TICKS = 20;
    private static final float WITHER_INVULNERABLE_TICKS = 220.0F;

    private NetherMobs() {
    }

    public static void register() {
        blaze();
        ghasts();
        hoglins();
        AnimatedMob.builder(EntityTypes.MAGMA_CUBE).query("variant", c -> c.getSize()).register();
        piglins();
        AnimatedMob.builder(EntityTypes.STRIDER).query("is_shaking", s -> s.isSuffocating() ? 1.0 : 0.0).register();
        wither();
    }

    private static void blaze() {
        AnimatedMob.builder(EntityTypes.BLAZE).query("is_charged", b -> {
            ShotTracker.State s = blazeState(b);
            return s.charged && !s.shotThisCharge ? 1.0 : 0.0;
        }).query("has_target", b -> blazeEngaged(b) ? 1.0 : 0.0).query("facing_target_to_range_attack", b -> blazeEngaged(b) ? 1.0 : 0.0).register();
    }

    private static ShotTracker.State blazeState(Blaze b) {
        return ShotTracker.update(b, b.isOnFire(), SmallFireball.class, 4.0);
    }

    private static boolean blazeEngaged(Blaze b) {
        ShotTracker.State s = blazeState(b);
        return s.charged || s.sinceChargeEnd(b) < 100 || s.sinceShot(b) < 100;
    }

    private static void ghasts() {
        EngineVariables.registerFor(EntityTypes.GHAST, "ischarging", g -> g.isCharging() ? 1.0 : 0.0);
        AnimatedMob.builder(EntityTypes.GHAST).query("has_target", g -> {
            ShotTracker.State s = ShotTracker.update(g, g.isCharging(), LargeFireball.class, 8.0);
            return !s.charged && s.sinceShot(g) > 20 ? 0.0 : 1.0;
        }).register();
        AnimatedMob.builder(EntityTypes.HAPPY_GHAST).property("minecraft:can_move", g -> !g.staysStill()).register();
        MolangQueries.registerFor(EntityTypes.HAPPY_GHAST, "has_any_leashed_entity_of_type", (Node[] a) -> (MolangContext c) -> {
            if (c.entity instanceof HappyGhast g && g.isLeashHolder()) {
                String[] types = MolangQueries.strings(a, 0, c);

                for (Entity e : g.level().getEntities(g, g.getBoundingBox().inflate(16.0), ex -> ex instanceof Leashable l && l.getLeashHolder() == g)) {
                    for (String t : types) {
                        if (typeMatches(e, t)) {
                            return 1.0;
                        }
                    }
                }

                return 0.0;
            } else {
                return 0.0;
            }
        });
        HappyGhastHarnessLayer.install();
    }

    private static boolean typeMatches(Entity e, String bedrockId) {
        String id = bedrockId.contains(":") ? bedrockId : "minecraft:" + bedrockId;
        String key = BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).toString();
        String path = key.substring(key.indexOf(58) + 1);

        return switch (id) {
            case "minecraft:boat" -> (path.endsWith("_boat") || path.endsWith("_raft")) && !path.contains("chest");
            case "minecraft:chest_boat" -> path.endsWith("_chest_boat") || path.endsWith("_chest_raft");
            default -> key.equals(id);
        };
    }

    private static void hoglins() {
        AnimatedMob.builder(EntityTypes.HOGLIN).query("is_avoiding_block", var0 -> 0.0).register();
        EngineVariables.registerFor(EntityTypes.HOGLIN, "attack_time", h -> attackProgress(h.getAttackAnimationRemainingTicks()));
        AnimatedMob.builder(EntityTypes.ZOGLIN).register();
        EngineVariables.registerFor(EntityTypes.ZOGLIN, "attack_time", z -> attackProgress(z.getAttackAnimationRemainingTicks()));
    }

    private static double attackProgress(int remainingTicks) {
        return remainingTicks > 0 ? (11 - Math.min(10, remainingTicks)) / 10.0 : 0.0;
    }

    private static void piglins() {
        MobEquipment.humanoid(EntityTypes.PIGLIN, ModelLayers.PIGLIN_ARMOR, ModelLayers.PIGLIN_BABY_ARMOR);
        AnimatedMob.builder(EntityTypes.PIGLIN)
            .query("variant", p -> p.getMainHandItem().getItem() instanceof CrossbowItem ? 0.0 : 1.0)
            .query("is_admiring", p -> p.getArmPose() == PiglinArmPose.ADMIRING_ITEM ? 1.0 : 0.0)
            .register();
        MobEquipment.humanoid(EntityTypes.PIGLIN_BRUTE, ModelLayers.PIGLIN_BRUTE_ARMOR, null);
        AnimatedMob.builder(EntityTypes.PIGLIN_BRUTE).register();
        MobEquipment.humanoid(EntityTypes.ZOMBIFIED_PIGLIN, ModelLayers.ZOMBIFIED_PIGLIN_ARMOR, ModelLayers.ZOMBIFIED_PIGLIN_BABY_ARMOR);
        AnimatedMob.builder(EntityTypes.ZOMBIFIED_PIGLIN, "zombie_pigman").register();
    }

    private static void wither() {
        AnimatedMob.builder(EntityTypes.WITHER)
            .actorScale(var0 -> 2.0)
            .shadow(0.5F)
            .query("is_shield_powered", w -> w.isPowered() ? 1.0 : 0.0)
            .query("has_target", w -> w.getAlternativeTarget(0) > 0 ? 1.0 : 0.0)
            .query("invulnerable_ticks", WitherBoss::getInvulnerableTicks)
            .register();
        MolangQueries.registerFor(EntityTypes.WITHER, "swell_amount", (Node[] var0) -> (MolangContext c) -> {
            if (c.entity instanceof WitherBoss w) {
                int t = w.getInvulnerableTicks();
                return t > 0 ? Math.clamp(1.0F - (t - c.partialTick) / 220.0F, 0.0F, 1.0F) : 0.0;
            } else {
                return 0.0;
            }
        });
    }
}
