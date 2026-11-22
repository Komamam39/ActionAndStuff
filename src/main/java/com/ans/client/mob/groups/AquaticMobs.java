package com.ans.client.mob.groups;

import com.ans.client.molang.MolangContext;
import com.ans.client.molang.Node;
import com.ans.client.bedrock.ChangeColors;
import com.ans.client.mob.AnimatedMob;
import com.ans.client.mob.aquatic.DolphinItemLayer;
import com.ans.client.mob.aquatic.GuardianBeamLayer;
import com.ans.client.molang.EngineVariables;
import com.ans.client.molang.MolangQueries;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.axolotl.Axolotl.Variant;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.world.entity.animal.fish.TropicalFish.Base;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.item.ItemStack;

public final class AquaticMobs {
    private static final List<Variant> AXOLOTLS = List.of(Variant.LUCY, Variant.CYAN, Variant.GOLD, Variant.WILD, Variant.BLUE);
    private static final List<String> FROGS = List.of("temperate", "cold", "warm");

    private AquaticMobs() {
    }

    public static void register() {
        fish();
        squids();
        axolotl();
        dolphin();
        turtle();
        guardians();
        frog();
        nautiluses();
    }

    private static void fish() {
        AnimatedMob.builder(EntityTypes.COD).register();
        AnimatedMob.builder(EntityTypes.SALMON).actorScale(Salmon::getSalmonScale).register();
        AnimatedMob.builder(EntityTypes.PUFFERFISH).query("variant", Pufferfish::getPuffState).actorScale(var0 -> 1.2).register();
        AnimatedMob.builder(EntityTypes.TROPICAL_FISH, "tropicalfish").actorScale(var0 -> 1.3).register();
        EngineVariables.registerFor(EntityTypes.TROPICAL_FISH, "TropicalFish.Base", f -> f.getPattern().base() == Base.SMALL ? 0.0 : 1.0);
        EngineVariables.registerFor(EntityTypes.TROPICAL_FISH, "TropicalFish.Pattern", f -> f.getPattern().ordinal());
        ChangeColors.registerTextureTints(
            EntityTypes.TROPICAL_FISH, (f, var1x, i) -> ARGB.opaque((i == 0 ? f.getBaseColor() : f.getPatternColor()).getTextureDiffuseColor())
        );

        for (EntityType<? extends LivingEntity> type : List.of(EntityTypes.COD, EntityTypes.SALMON, EntityTypes.TROPICAL_FISH)) {
            EngineVariables.registerFor(type, "AnimationAmount", e -> e.tickCount);
            EngineVariables.registerFor(type, "AnimationAmountPrev", e -> e.tickCount - 1);
        }

        for (EntityType<? extends LivingEntity> type : List.of(EntityTypes.COD, EntityTypes.SALMON, EntityTypes.TROPICAL_FISH, EntityTypes.PUFFERFISH)) {
            pitchQuery(type);
        }
    }

    private static <E extends LivingEntity> void pitchQuery(EntityType<E> type) {
        partialTickQuery(type, "body_x_rotation", (e, pt) -> (double)e.getXRot(pt));
    }

    private static void squids() {
        AnimatedMob.builder(EntityTypes.SQUID).register();
        AnimatedMob.builder(EntityTypes.GLOW_SQUID).register();

        for (EntityType<? extends Squid> type : List.of(EntityTypes.SQUID, EntityTypes.GLOW_SQUID)) {
            squid(type);
        }

        EngineVariables.registerFor(EntityTypes.GLOW_SQUID, "glow_alpha", s -> Mth.clamp(s.getDarkTicksRemaining() / 10.0, 0.0, 1.0));
    }

    private static <E extends Squid> void squid(EntityType<E> type) {
        partialTickQuery(type, "body_x_rotation", (s, pt) -> (double)Mth.lerp(pt, s.xBodyRotO, s.xBodyRot));
        EngineVariables.registerForPartial(type, "squid.swim_rotation", (s, pt) -> Mth.lerp(pt, s.zBodyRotO, s.zBodyRot));
    }

    private static void axolotl() {
        AnimatedMob.builder(EntityTypes.AXOLOTL)
            .query("variant", a -> Math.max(0, AXOLOTLS.indexOf(a.getVariant())))
            .query("is_playing_dead", a -> a.isPlayingDead() ? 1.0 : 0.0)
            .register();
        pitchQuery(EntityTypes.AXOLOTL);
    }

    private static void dolphin() {
        AnimatedMob.builder(EntityTypes.DOLPHIN).register();
        pitchQuery(EntityTypes.DOLPHIN);
        DolphinItemLayer.install();
    }

    private static void turtle() {
        AnimatedMob.builder(EntityTypes.TURTLE).actorScale(t -> t.isBaby() ? 0.16 : 1.0).register();
    }

    private static void guardians() {
        for (EntityType<? extends Guardian> type : List.of(EntityTypes.GUARDIAN, EntityTypes.ELDER_GUARDIAN)) {
            AnimatedMob.builder(type).register();
            perType(type, "has_target", g -> !g.hasActiveAttackTarget() && !g.isAggressive() ? 0.0 : 1.0);
        }

        GuardianBeamLayer.install();
    }

    private static void frog() {
        AnimatedMob.builder(EntityTypes.FROG)
            .query("variant", f -> index(FROGS, f.getVariant()))
            .query("is_croaking", f -> f.getPose() == Pose.CROAKING ? 1.0 : 0.0)
            .query("is_eating_mob", f -> f.getPose() == Pose.USING_TONGUE ? 1.0 : 0.0)
            .register();
    }

    private static void nautiluses() {
        AnimatedMob.builder(EntityTypes.NAUTILUS).actorScale(var0 -> 1.0).register();
        AnimatedMob.builder(EntityTypes.ZOMBIE_NAUTILUS)
            .actorScale(var0 -> 1.0)
            .property("minecraft:variant", z -> z.getVariant().unwrapKey().map(k -> k.identifier().getPath()).orElse("").equals("warm") ? "coral" : "default")
            .register();

        for (EntityType<? extends AbstractNautilus> type : List.of(EntityTypes.NAUTILUS, EntityTypes.ZOMBIE_NAUTILUS)) {
            perType(type, "has_dash_cooldown", n -> n.getJumpCooldown() > 0 ? 1.0 : 0.0);
            MolangQueries.registerFor(
                type, "armor_texture_slot", (Node[] var0) -> (MolangContext c) -> c.entity == null ? 0.0 : nautilusArmor(c.entity.getItemBySlot(EquipmentSlot.BODY))
            );
        }
    }

    private static int nautilusArmor(ItemStack body) {
        if (body.isEmpty()) {
            return 0;
        }

        String path = BuiltInRegistries.ITEM.getKey(body.getItem()).getPath();
        if (!path.endsWith("_nautilus_armor")) {
            return 0;
        }

        return switch (path.substring(0, path.length() - "_nautilus_armor".length())) {
            case "copper" -> 1;
            case "iron" -> 2;
            case "golden", "gold" -> 3;
            case "diamond" -> 4;
            case "netherite" -> 5;
            default -> 0;
        };
    }

    private static int index(List<String> order, Holder<?> holder) {
        int i = holder.unwrapKey().map(k -> order.indexOf(k.identifier().getPath())).orElse(0);
        return Math.max(0, i);
    }

    private static <E extends LivingEntity> void perType(EntityType<E> type, String name, ToDoubleFunction<E> fn) {
        MolangQueries.registerFor(type, name, fn);
    }

    private static <E extends LivingEntity> void partialTickQuery(EntityType<E> type, String name, BiFunction<E, Float, Double> fn) {
        MolangQueries.registerFor(type, name, (Node[] var1x) -> (MolangContext c) -> c.entity == null ? 0.0 : fn.apply((E)c.entity, c.partialTick));
    }
}
