package com.ans.perfutil;

import com.ans.client.molang.MolangContext;
import com.ans.client.molang.MolangQueries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public final class UseDuration {
    private static final boolean ON = !"false".equalsIgnoreCase(System.getProperty("ans.bedrockUseDuration", "true"));

    private UseDuration() {
    }

    static double remaining(LivingEntity var0, float var1) {
        return Math.max(0.001, var0.getUseItemRemainingTicks() - var1);
    }

    static boolean crossbowFull(LivingEntity var0) {
        if (var0 != null && var0.isUsingItem()) {
            ItemStack var1 = var0.getUseItem();
            return !(var1.getItem() instanceof CrossbowItem) ? false : var0.getTicksUsingItem() >= CrossbowItem.getChargeDuration(var1, var0);
        } else {
            return false;
        }
    }

    public static void install() {
        MolangQueries.simple("is_gliding", var0 -> var0.entity != null && GlideState.gliding(var0.entity) ? 1.0 : 0.0);
        MolangQueries.simple("item_is_charged", var0 -> {
            if (var0.entity == null) {
                return 0.0;
            } else {
                ItemStack var1 = var0.entity.getMainHandItem();
                if (!(var1.getItem() instanceof CrossbowItem)) {
                    return 0.0;
                } else {
                    return !CrossbowItem.isCharged(var1) && (!crossbowFull(var0.entity) || var0.entity.getUseItem() != var1) ? 0.0 : 1.0;
                }
            }
        });
        if (ON) {
            MolangQueries.simple(
                "main_hand_item_use_duration",
                var0 -> var0.entity != null && var0.entity.isUsingItem() && !crossbowFull(var0.entity) ? remaining(var0.entity, var0.partialTick) : 0.0
            );
            MolangQueries.registerFor(
                EntityTypes.PLAYER,
                "main_hand_item_use_duration",
                (Player var0) -> var0.isUsingItem() && var0.getUsedItemHand() == InteractionHand.MAIN_HAND && !crossbowFull(var0)
                    ? remaining(var0, MolangContext.current().partialTick)
                    : 0.0
            );
            MolangQueries.simple(
                "main_hand_item_max_duration",
                var0 -> {
                    if (var0.entity == null) {
                        return 0.0;
                    }

                    ItemStack var1 = var0.entity.getMainHandItem();
                    return var1.getItem() instanceof CrossbowItem
                        ? Math.max(1, CrossbowItem.getChargeDuration(var1, var0.entity))
                        : var1.getUseDuration(var0.entity);
                }
            );
        }
    }
}
