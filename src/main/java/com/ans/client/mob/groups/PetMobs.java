package com.ans.client.mob.groups;

import com.ans.client.mob.AnimatedMob;
import com.ans.client.mob.pets.FoxHeldItemLayer;
import com.ans.client.mob.pets.WolfArmorLayer;
import com.ans.client.molang.EngineVariables;
import com.ans.client.molang.MolangQueries;
import com.ans.client.render.BedrockTextures;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class PetMobs {
    private static final List<String> WOLF_VARIANTS = List.of("pale", "ashen", "black", "chestnut", "rusty", "snowy", "spotted", "striped", "woods");
    private static final List<String> CAT_VARIANTS = List.of(
        "white", "black", "red", "siamese", "british_shorthair", "calico", "persian", "ragdoll", "tabby", "all_black", "jellie"
    );

    private PetMobs() {
    }

    public static void register() {
        wolf();
        cat();
        AnimatedMob.builder(EntityTypes.OCELOT).register();
        parrot();
        fox();
        horses();
        camels();
        traderLlama();
        panda();
        polarBear();
        AnimatedMob.builder(EntityTypes.MOOSHROOM).query("variant", m -> m.getVariant().ordinal()).register();
        armadillo();
        bee();
    }

    private static void wolf() {
        AnimatedMob.builder(EntityTypes.WOLF)
            .query("variant", w -> index(WOLF_VARIANTS, w.get(DataComponents.WOLF_VARIANT)))
            .query("is_interested", w -> w.isInterested() ? 1.0 : 0.0)
            .query("is_shaking_wetness", w -> w.getShakeAnim(1.0F) > 0.0F ? 1.0 : 0.0)
            .query("tail_angle", Wolf::getTailAngle)
            .changeColor((w, pt) -> w.isTame() ? collar(w.getCollarColor()) : -1)
            .register();
        WolfArmorLayer.install();
    }

    private static void cat() {
        AnimatedMob.builder(EntityTypes.CAT)
            .query("variant", c -> index(CAT_VARIANTS, c.getVariant()))
            .query("is_sleeping", c -> c.isLying() ? 1.0 : 0.0)
            .changeColor((c, pt) -> c.isTame() ? collar(c.getCollarColor()) : -1)
            .actorScale(c -> c.isBaby() ? 0.4 : 0.8)
            .register();
        EngineVariables.registerForPartial(EntityTypes.CAT, "liedownamount", (c, pt) -> c.getLieDownAmount(pt));
        EngineVariables.registerFor(EntityTypes.CAT, "lieonplayer", c -> c.isLyingOnTopOfSleepingPlayer() ? 1.0 : 0.0);

        for (String v : CAT_VARIANTS) {
            BedrockTextures.composite(
                vanilla("cat/cat_" + v + "_tame_baby"),
                32,
                32,
                BedrockTextures.part(vanilla("cat/cat_" + v + "_baby"), 0, 0, false),
                BedrockTextures.part(vanilla("cat/cat_collar_baby"), 0, 0, true)
            );
        }
    }

    private static void parrot() {
        AnimatedMob.builder(EntityTypes.PARROT)
            .query("variant", p -> p.getVariant().ordinal())
            .query("is_dancing", p -> p.isPartyParrot() ? 1.0 : 0.0)
            .register();
    }

    private static void fox() {
        AnimatedMob.builder(EntityTypes.FOX)
            .query("variant", f -> f.getVariant().ordinal())
            .query("is_sitting", f -> f.isSitting() ? 1.0 : 0.0)
            .query("is_stalking", f -> f.isCrouching() ? 1.0 : 0.0)
            .query("has_target", f -> !f.isAggressive() && !f.isPouncing() ? 0.0 : 1.0)
            .register();
        FoxHeldItemLayer.install();
    }

    private static void horses() {
        AnimatedMob.builder(EntityTypes.HORSE).query("variant", h -> h.getVariant().ordinal()).query("mark_variant", h -> h.getMarkings().ordinal()).register();
        AnimatedMob.builder(EntityTypes.DONKEY).register();
        AnimatedMob.builder(EntityTypes.MULE).register();
        AnimatedMob.builder(EntityTypes.SKELETON_HORSE).register();
        AnimatedMob.builder(EntityTypes.ZOMBIE_HORSE).register();

        for (EntityType<? extends AbstractHorse> type : List.of(
            EntityTypes.HORSE, EntityTypes.DONKEY, EntityTypes.MULE, EntityTypes.SKELETON_HORSE, EntityTypes.ZOMBIE_HORSE
        )) {
            EngineVariables.registerFor(type, "armor_texture_slot", PetMobs::horseArmorSlot);
            EngineVariables.registerForPartial(type, "stand_anim", (h, pt) -> h.getStandAnim(pt));
            EngineVariables.registerFor(type, "shake_tail", h -> h.tailCounter > 0 ? 1.0 : 0.0);
        }

        for (String coat : List.of("horse_white", "horse_creamy", "horse_chestnut", "horse_brown", "horse_black", "horse_gray", "horse_darkbrown")) {
            saddled("horse/" + coat, 64, "horse_saddle");
        }

        saddled("horse/donkey", 64, "donkey_saddle");
        saddled("horse/mule", 64, "mule_saddle");
        saddled("horse/horse_skeleton", 64, "skeleton_horse_saddle");
        saddled("horse/horse_zombie", 64, "zombie_horse_saddle");
        saddled("camel/camel", 128, "camel_saddle");
        saddled("camel/camel_husk", 128, "camel_husk_saddle");
    }

    private static void camels() {
        AnimatedMob.builder(EntityTypes.CAMEL).actorScale(c -> c.isBaby() ? 0.45 : 1.0).register();
        AnimatedMob.builder(EntityTypes.CAMEL_HUSK).register();
    }

    private static void traderLlama() {
        EngineVariables.registerFor(EntityTypes.TRADER_LLAMA, "DecorTextureIndex", PetMobs::carpetIndex);
        AnimatedMob.builder(EntityTypes.TRADER_LLAMA).query("variant", l -> l.getVariant().ordinal()).query("mark_variant", var0 -> 1.0).register();
    }

    private static void panda() {
        AnimatedMob.builder(EntityTypes.PANDA)
            .query("variant", p -> p.getVariant().ordinal())
            .query("is_eating", p -> p.isEating() ? 1.0 : 0.0)
            .query("roll_counter", p -> p.rollCounter)
            .query("sneeze_counter", p -> p.getSneezeCounter())
            .register();
        MolangQueries.registerFor(EntityTypes.PANDA, "sit_amount", PetMobs.<Panda>partial((p, pt) -> (double)p.getSitAmount(pt)));
        MolangQueries.registerFor(EntityTypes.PANDA, "lie_amount", PetMobs.<Panda>partial((p, pt) -> (double)p.getLieOnBackAmount(pt)));
    }

    private static void polarBear() {
        AnimatedMob.builder(EntityTypes.POLAR_BEAR).register();
        MolangQueries.registerFor(EntityTypes.POLAR_BEAR, "standing_scale", PetMobs.<PolarBear>partial((b, pt) -> (double)b.getStandingAnimationScale(pt)));
    }

    private static void armadillo() {
        AnimatedMob.builder(EntityTypes.ARMADILLO).property("minecraft:armadillo_state", a -> {
            return switch (a.getState()) {
                case IDLE -> "unrolled";
                case ROLLING -> "rolled_up";
                case SCARED -> a.peekAnimationState.isStarted() ? "rolled_up_peeking" : "rolled_up";
                case UNROLLING -> "rolled_up_unrolling";
            };
        }).register();
    }

    private static void bee() {
        AnimatedMob.builder(EntityTypes.BEE)
            .property("minecraft:has_nectar", b -> b.hasNectar())
            .query("mark_variant", b -> b.hasStung() ? 1.0 : 0.0)
            .register();
    }

    private static int collar(DyeColor color) {
        return 0xFF000000 | color.getTextureDiffuseColor() & 16777215;
    }

    private static int index(List<String> order, @Nullable Holder<?> holder) {
        if (holder == null) {
            return 0;
        }

        int i = holder.unwrapKey().map(k -> order.indexOf(k.identifier().getPath())).orElse(0);
        return Math.max(0, i);
    }

    private static int horseArmorSlot(LivingEntity entity) {
        ItemStack body = entity.getItemBySlot(EquipmentSlot.BODY);
        if (body.isEmpty()) {
            return 0;
        }

        return (switch (BuiltInRegistries.ITEM.getKey(body.getItem()).getPath()) {
            case "leather_horse_armor" -> 1;
            case "iron_horse_armor" -> 2;
            case "golden_horse_armor" -> 3;
            case "diamond_horse_armor" -> 4;
            case "copper_horse_armor" -> 5;
            case "netherite_horse_armor" -> 6;
            default -> 0;
        });
    }

    private static int carpetIndex(LivingEntity entity) {
        ItemStack body = entity.getItemBySlot(EquipmentSlot.BODY);
        if (body.isEmpty()) {
            return 0;
        }

        String path = BuiltInRegistries.ITEM.getKey(body.getItem()).getPath();
        if (!path.endsWith("_carpet")) {
            return 0;
        }

        DyeColor color = DyeColor.byName(path.substring(0, path.length() - "_carpet".length()), null);
        return (color == null ? 0 : color.getId() + 1);
    }

    private static <E extends LivingEntity> MolangQueries.Factory partial(BiFunction<E, Float, Double> fn) {
        return var1 -> c -> c.entity == null ? 0.0 : fn.apply((E)c.entity, c.partialTick);
    }

    private static void saddled(String coat, int size, String saddleAsset) {
        BedrockTextures.composite(
            vanilla(coat),
            size,
            size,
            BedrockTextures.part(vanilla(coat), 0, 0, false),
            BedrockTextures.part(vanilla("equipment/" + saddleAsset + "/saddle"), 0, 0, false)
        );
    }

    private static Identifier vanilla(String entityTexture) {
        return Identifier.withDefaultNamespace("textures/entity/" + entityTexture + ".png");
    }
}
