package com.ans.perf;

import com.ans.client.equipment.EquipmentQueries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EquipmentQueries.class, remap = false)
public abstract class HeldArmorTagsMixin {
    @Inject(method = "hasTag", at = @At("HEAD"), cancellable = true)
    private static void ans$armorTags(ItemStack var0, String var1, CallbackInfoReturnable<Boolean> var2) {
        String var3 = var1.indexOf(58) >= 0 ? var1 : "minecraft:" + var1;
        if (var3.equals("minecraft:harness")) {
            if (var0.is(ItemTags.HARNESSES)) {
                var2.setReturnValue(true);
            }
        } else {
            if (var3.startsWith("minecraft:")
                && var3.endsWith("_tier")
                && (var0.is(ItemTags.HEAD_ARMOR) || var0.is(ItemTags.CHEST_ARMOR) || var0.is(ItemTags.LEG_ARMOR) || var0.is(ItemTags.FOOT_ARMOR))) {
                String var4 = var3.substring(10, var3.length() - 5) + "_";
                if (BuiltInRegistries.ITEM.getKey(var0.getItem()).getPath().startsWith(var4)) {
                    var2.setReturnValue(true);
                }
            }
        }
    }
}
