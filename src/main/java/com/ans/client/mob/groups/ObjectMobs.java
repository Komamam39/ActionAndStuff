package com.ans.client.mob.groups;

import com.ans.Ans;
import com.ans.client.equipment.MobEquipment;
import com.ans.client.mob.AnimatedMob;
import com.ans.client.molang.BedrockProperties;
import com.ans.client.molang.EngineVariables;
import com.ans.client.render.BedrockTextures;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

public final class ObjectMobs {
    private static final Map<String, Integer> POTION_IDS = new HashMap<>();

    private ObjectMobs() {
    }

    public static void register() {
        projectiles();
        potions();
        minecarts();
        AnimatedMob.builder(EntityTypes.EXPERIENCE_ORB, "xp_orb").shadow(0.5F).register();
        AnimatedMob.builder(EntityTypes.EVOKER_FANGS, "evocation_fang").register();
        AnimatedMob.builder(EntityTypes.FIREWORK_ROCKET, "fireworks_rocket").register();
        AnimatedMob.builder(EntityTypes.END_CRYSTAL, "ender_crystal").register();
        armorStand();
    }

    private static void projectiles() {
        AnimatedMob.builder(EntityTypes.ARROW).register();
        AnimatedMob.builder(EntityTypes.SNOWBALL).register();
        AnimatedMob.builder(EntityTypes.ENDER_PEARL).register();
        AnimatedMob.builder(EntityTypes.EYE_OF_ENDER, "eye_of_ender_signal").register();
        AnimatedMob.builder(EntityTypes.EXPERIENCE_BOTTLE, "xp_bottle").register();
        AnimatedMob.builder(EntityTypes.FIREBALL).register();
        AnimatedMob.builder(EntityTypes.SMALL_FIREBALL).register();
        AnimatedMob.builder(EntityTypes.DRAGON_FIREBALL).register();
        AnimatedMob.builder(EntityTypes.SHULKER_BULLET).register();
        AnimatedMob.builder(EntityTypes.WIND_CHARGE, "wind_charge_projectile").register();
        AnimatedMob.builder(EntityTypes.BREEZE_WIND_CHARGE, "wind_charge_projectile").register();
        Identifier dangerous = Ans.id("entity/wither_skull_dangerous.json");
        AnimatedMob.builder(EntityTypes.WITHER_SKULL).bundleFor(s -> s.isDangerous() ? dangerous : null).register();
        AnimatedMob.builder(EntityTypes.EGG).property("minecraft:climate_variant", e -> {
            String v = BedrockProperties.keyPath(e.getItem().get(DataComponents.CHICKEN_VARIANT));
            return v != null ? v : "temperate";
        }).register();
    }

    private static double potionId(ThrowableItemProjectile e) {
        PotionContents contents = e.getItem().get(DataComponents.POTION_CONTENTS);
        if (contents == null) {
            return 0.0;
        }

        String key = contents.potion().map(ObjectMobs::potionKey).orElse(null);
        return key == null ? 0.0 : POTION_IDS.getOrDefault(key, 0).intValue();
    }

    private static String potionKey(Holder<Potion> p) {
        String k = BedrockProperties.keyPath(p);
        return k == null ? "" : k;
    }

    private static void potions() {
        AnimatedMob.builder(EntityTypes.SPLASH_POTION).query("variant", ObjectMobs::potionId).register();
        AnimatedMob.builder(EntityTypes.LINGERING_POTION).query("variant", ObjectMobs::potionId).register();
    }

    private static void minecarts() {
        BedrockTextures.composite(
            Identifier.withDefaultNamespace("textures/block/repeating_command_block_front_frame0.png"),
            16,
            16,
            BedrockTextures.crop(Identifier.withDefaultNamespace("textures/block/repeating_command_block_front.png"), 0, 0, 16, 16, 0, 0, false)
        );
        BedrockTextures.composite(
            Identifier.withDefaultNamespace("textures/block/repeating_command_block_back_frame0.png"),
            16,
            16,
            BedrockTextures.crop(Identifier.withDefaultNamespace("textures/block/repeating_command_block_back.png"), 0, 0, 16, 16, 0, 0, false)
        );
        minecart(EntityTypes.MINECART, "minecart");
        minecart(EntityTypes.CHEST_MINECART, "chest_minecart");
        minecart(EntityTypes.COMMAND_BLOCK_MINECART, "command_block_minecart");
        minecart(EntityTypes.HOPPER_MINECART, "hopper_minecart");
        minecart(EntityTypes.TNT_MINECART, "tnt_minecart");
    }

    private static <E extends AbstractMinecart> void minecart(EntityType<E> type, String id) {
        EngineVariables.registerForPartial(type, "rail_rotation.z", (e, pt) -> e.getXRot(pt));
        AnimatedMob.builder(type, id).yaw((e, pt) -> Mth.wrapDegrees(e.getYRot(pt))).register();
    }

    private static void armorStand() {
        MobEquipment.humanoid(EntityTypes.ARMOR_STAND, ModelLayers.ARMOR_STAND_ARMOR, ModelLayers.ARMOR_STAND_SMALL_ARMOR);
        AnimatedMob.builder(EntityTypes.ARMOR_STAND).query("hurt_direction", e -> e.level().getGameTime() - e.lastHit < 5L ? -1.0 : 0.0).register();
    }

    static {
        String[] ids = new String[]{
            "water",
            "mundane",
            "long_mundane",
            "thick",
            "awkward",
            "night_vision",
            "long_night_vision",
            "invisibility",
            "long_invisibility",
            "leaping",
            "long_leaping",
            "strong_leaping",
            "fire_resistance",
            "long_fire_resistance",
            "swiftness",
            "long_swiftness",
            "strong_swiftness",
            "slowness",
            "long_slowness",
            "water_breathing",
            "long_water_breathing",
            "healing",
            "strong_healing",
            "harming",
            "strong_harming",
            "poison",
            "long_poison",
            "strong_poison",
            "regeneration",
            "long_regeneration",
            "strong_regeneration",
            "strength",
            "long_strength",
            "strong_strength",
            "weakness",
            "long_weakness",
            "wither",
            "turtle_master",
            "long_turtle_master",
            "strong_turtle_master",
            "slow_falling",
            "long_slow_falling",
            "strong_slowness",
            "wind_charged",
            "weaving",
            "oozing",
            "infested"
        };

        for (int i = 0; i < ids.length; i++) {
            POTION_IDS.put(ids[i], i);
        }
    }
}
