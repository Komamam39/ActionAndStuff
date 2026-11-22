package com.ans.client.mob.groups;

import com.ans.client.equipment.MobEquipment;
import com.ans.client.mob.AnimatedMob;
import com.ans.client.mob.misc.CreakingRenderer;
import com.ans.client.mob.misc.EnderDragonRenderer;
import com.ans.client.mob.misc.MiscState;
import com.ans.client.mob.misc.SulfurCubeBlockLayer;
import com.ans.client.molang.EngineVariables;
import com.ans.client.molang.MolangContext;
import com.ans.client.render.BedrockTextures;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.DragonFlightHistory.Sample;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.entity.monster.warden.AngerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public final class MiscMobs {
    private MiscMobs() {
    }

    public static void register() {
        AnimatedMob.builder(EntityTypes.BAT).query("is_resting", b -> b.isResting() ? 1.0 : 0.0).register();
        AnimatedMob.builder(EntityTypes.SLIME).query("variant", s -> s.getSize()).register();
        AnimatedMob.builder(EntityTypes.SILVERFISH).register();
        AnimatedMob.builder(EntityTypes.ENDERMITE).register();
        AnimatedMob.builder(EntityTypes.PHANTOM).actorScale(p -> 1.0 + 0.15 * p.getPhantomSize()).register();
        BedrockTextures.composite(
            vanilla("phantom/phantom_bedrock"),
            64,
            64,
            BedrockTextures.part(vanilla("phantom/phantom"), 0, 0, true),
            BedrockTextures.part(vanilla("phantom/phantom_eyes"), 0, 0, false)
        );
        shulker();
        breeze();
        creaking();
        sniffer();
        warden();
        enderDragon();
        sulfurCube();
        MobEquipment.humanoid(EntityTypes.PARCHED, ModelLayers.PARCHED_ARMOR, null);
        AnimatedMob.builder(EntityTypes.PARCHED).register();
    }

    private static void shulker() {
        EngineVariables.registerFor(EntityTypes.SHULKER, "Shulker.PeekAmount", s -> s.getClientPeekAmount(MolangContext.current().partialTick));
        EngineVariables.registerFor(EntityTypes.SHULKER, "Shulker.FacingDirection", s -> s.getAttachFace().get3DDataValue());
        AnimatedMob.builder(EntityTypes.SHULKER).query("variant", s -> {
            DyeColor c = s.getColor();
            return c == null ? 16.0 : 15 - c.getId();
        }).query("has_target", s -> MiscState.shulkerRawPeek(s) >= 100 ? 1.0 : 0.0).register();
    }

    private static void breeze() {
        AnimatedMob.builder(EntityTypes.BREEZE)
            .query("is_jump_goal_jumping", b -> b.getPose() != Pose.INHALING && b.getPose() != Pose.LONG_JUMPING ? 0.0 : 1.0)
            .query("facing_target_to_range_attack", b -> b.getPose() == Pose.SHOOTING ? 1.0 : 0.0)
            .query("has_target", b -> {
                return MiscState.recentlyInCombat(b, switch (b.getPose()) {
                    case SHOOTING, INHALING, LONG_JUMPING, SLIDING -> true;
                    default -> false;
                }, 100) ? 1.0 : 0.0;
            })
            .register();
    }

    private static void creaking() {
        AnimatedMob.builder(EntityTypes.CREAKING)
            .property("minecraft:creaking_state", c -> {
                if (c.isTearingDown()) {
                    return "twitching";
                } else if (c.isActive()) {
                    return c.canMove() ? "hostile_unobserved" : "hostile_observed";
                } else {
                    return "neutral";
                }
            })
            .property("minecraft:creaking_swaying_ticks", c -> (double)MiscState.remainingTicks(c.invulnerabilityAnimationState, c, 8))
            .renderer(CreakingRenderer::new)
            .register();
        EngineVariables.registerFor(EntityTypes.CREAKING, "attack_time", c -> MiscState.swing(c.attackAnimationState, c));
    }

    private static void sniffer() {
        AnimatedMob.builder(EntityTypes.SNIFFER)
            .query("timer_flag_3", s -> s.feelingHappyAnimationState.isStarted() ? 1.0 : 0.0)
            .query("timer_flag_2", s -> s.risingAnimationState.isStarted() ? 1.0 : 0.0)
            .query("is_searching", s -> !s.isSearching() && !s.scentingAnimationState.isStarted() && !s.sniffingAnimationState.isStarted() ? 0.0 : 1.0)
            .query("is_digging", s -> s.diggingAnimationState.isStarted() ? 1.0 : 0.0)
            .register();
    }

    private static void warden() {
        AnimatedMob.builder(EntityTypes.WARDEN)
            .query("is_emerging", w -> w.getPose() == Pose.EMERGING ? 1.0 : 0.0)
            .query("is_digging", w -> w.getPose() == Pose.DIGGING ? 1.0 : 0.0)
            .query("is_roaring", w -> w.getPose() == Pose.ROARING ? 1.0 : 0.0)
            .query("is_sniffing", w -> w.getPose() == Pose.SNIFFING ? 1.0 : 0.0)
            .query("is_sonic_boom", w -> MiscState.remainingTicks(w.sonicBoomAnimationState, w, 60) > 0.0F ? 1.0 : 0.0)
            .query("has_target", w -> w.getClientAngerLevel() >= AngerLevel.ANGRY.getMinimumAnger() ? 1.0 : 0.0)
            .query("heartbeat_phase", w -> w.getHeartAnimation(MolangContext.current().partialTick))
            .query("time_since_last_vibration_detection", w -> {
                float t = w.getTendrilAnimation(MolangContext.current().partialTick);
                return t > 0.0F ? (1.0F - t) * 0.5 : -1.0;
            })
            .changeColor((w, pt) -> 16777215)
            .register();
        EngineVariables.registerFor(
            EntityTypes.WARDEN,
            "attack_time",
            w -> Math.max(w.getSwingAnimation(MolangContext.current().partialTick), MiscState.swing(w.attackAnimationState, w))
        );
        Identifier warden = vanilla("warden/warden");
        BedrockTextures.composite(
            vanilla("warden/warden_tendrils"),
            128,
            128,
            BedrockTextures.crop(warden, 52, 32, 32, 16, 52, 32, true),
            BedrockTextures.crop(warden, 58, 0, 32, 16, 58, 0, true)
        );
    }

    private static Identifier vanilla(String entityTexture) {
        return Identifier.withDefaultNamespace("textures/entity/" + entityTexture + ".png");
    }

    private static void enderDragon() {
        for (int i = 0; i < 10; i++) {
            int delay = i;
            EngineVariables.registerFor(EntityTypes.ENDER_DRAGON, "historical_frame_" + i + ".pos_y", d -> history(d, delay).y());
            EngineVariables.registerFor(EntityTypes.ENDER_DRAGON, "historical_frame_" + i + ".rot_y", d -> history(d, delay).yRot());
        }

        AnimatedMob.builder(EntityTypes.ENDER_DRAGON)
            .query(
                "has_target",
                d -> {
                    EnderDragonPhase<?> p = d.getPhaseManager().getCurrentPhase().getPhase();
                    return p != EnderDragonPhase.STRAFE_PLAYER
                            && p != EnderDragonPhase.CHARGING_PLAYER
                            && p != EnderDragonPhase.SITTING_FLAMING
                            && p != EnderDragonPhase.SITTING_ATTACKING
                        ? 0.0
                        : 1.0;
                }
            )
            .query("death_ticks", d -> d.dragonDeathTime <= 0 ? 0.0 : d.dragonDeathTime + MolangContext.current().partialTick)
            .query("body_y_rotation", d -> history(d, 0).yRot())
            .vanillaDeathRotation(false)
            .renderer(EnderDragonRenderer::new)
            .register();
    }

    private static Sample history(EnderDragon dragon, int delay) {
        return dragon.flightHistory.get(delay, MolangContext.current().partialTick);
    }

    private static void sulfurCube() {
        AnimatedMob.builder(EntityTypes.SULFUR_CUBE)
            .property("minecraft:sulfur_cube_archetype", MiscMobs::archetype)
            .query("fuse_time", c -> c.isPrimed() ? Math.max(0, c.getFuse()) : 0.0)
            .register();
        SulfurCubeBlockLayer.install();
    }

    private static String archetype(SulfurCube cube) {
        ItemStack body = cube.getItemBySlot(EquipmentSlot.BODY);
        if (body.isEmpty()) {
            return "none";
        }

        try {
            return cube.level()
                .registryAccess()
                .lookupOrThrow(Registries.SULFUR_CUBE_ARCHETYPE)
                .listElements()
                .filter(h -> body.is(h.value().items()))
                .findFirst()
                .map(h -> h.key().identifier().getPath())
                .orElse("regular");
        } catch (RuntimeException e) {
            return "regular";
        }
    }
}
