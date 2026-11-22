package com.ans.client.mob.groups;

import com.ans.client.dev.CritterSelfTest;
import com.ans.client.mob.AnimatedMob;
import com.ans.client.molang.EngineVariables;
import com.ans.client.render.BedrockTextures;
import net.minecraft.client.color.ColorLerper;
import net.minecraft.client.color.ColorLerper.Type;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public final class CritterMobs {
    private CritterMobs() {
    }

    public static void register() {
        sheep();
        AnimatedMob.builder(EntityTypes.CHICKEN).register();
        rabbit();
        goat();
        llama();
        CritterSelfTest.init();
    }

    private static void rabbit() {
        AnimatedMob.builder(EntityTypes.RABBIT).query("variant", r -> {
            return switch (r.getVariant()) {
                case BROWN -> 0.0;
                case WHITE, EVIL -> 1.0;
                case BLACK -> 2.0;
                case WHITE_SPLOTCHED -> 3.0;
                case GOLD -> 4.0;
                case SALT -> 5.0;
            };
        }).actorScale(r -> r.isBaby() ? 0.4 : 0.55).register();
    }

    private static void goat() {
        EngineVariables.registerFor(EntityTypes.GOAT, "goat_has_left_horn", g -> g.hasLeftHorn() ? 1.0 : 0.0);
        EngineVariables.registerFor(EntityTypes.GOAT, "goat_has_right_horn", g -> g.hasRightHorn() ? 1.0 : 0.0);
        EngineVariables.registerFor(EntityTypes.GOAT, "should_bow_head", g -> g.getRammingXHeadRot() > 0.0F ? 1.0 : 0.0);
        AnimatedMob.builder(EntityTypes.GOAT).register();
    }

    private static void llama() {
        EngineVariables.registerFor(EntityTypes.LLAMA, "DecorTextureIndex", CritterMobs::carpetIndex);
        AnimatedMob.builder(EntityTypes.LLAMA).query("variant", l -> l.getVariant().ordinal()).register();
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

    private static void sheep() {
        AnimatedMob.builder(EntityTypes.SHEEP)
            .query("is_grazing", s -> !(s.getHeadEatPositionScale(0.0F) > 0.0F) && !(s.getHeadEatPositionScale(1.0F) > 0.0F) ? 0.0 : 1.0)
            .changeColor(CritterMobs::woolColor)
            .register();
        BedrockTextures.composite(
            vanilla("sheep/sheep"),
            64,
            64,
            BedrockTextures.part(vanilla("sheep/sheep"), 0, 0, false),
            BedrockTextures.part(vanilla("sheep/sheep_wool_undercoat"), 0, 0, true),
            BedrockTextures.part(vanilla("sheep/sheep_wool"), 0, 32, true)
        );
        BedrockTextures.composite(
            vanilla("sheep/sheep_baby"),
            32,
            32,
            BedrockTextures.crop(vanilla("sheep/sheep_baby"), 0, 0, 32, 32, 0, 0, false),
            BedrockTextures.crop(vanilla("sheep/sheep_wool_baby"), 0, 0, 32, 32, 0, 0, true)
        );
    }

    private static int woolColor(Sheep sheep, float partialTick) {
        return hasName(sheep, "jeb_") ? ColorLerper.getLerpedColor(Type.SHEEP, sheep.tickCount + partialTick) : Type.SHEEP.getColor(sheep.getColor());
    }

    private static boolean hasName(Entity entity, String name) {
        return entity.getCustomName() != null && name.equals(entity.getCustomName().getString());
    }

    private static Identifier vanilla(String entityTexture) {
        return Identifier.withDefaultNamespace("textures/entity/" + entityTexture + ".png");
    }
}
