package com.ans.perf;

import com.ans.client.equipment.EquipmentQueries;
import com.ans.client.molang.MolangQueries;
import com.ans.perfutil.AvoidMobs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EquipmentQueries.class, remap = false)
public abstract class AvoidMobsMixin {
    @Inject(method = "install", at = @At("TAIL"))
    private static void ans$avoidMobs(CallbackInfo var0) {
        MolangQueries.simple("is_avoiding_mobs", AvoidMobs::query);
    }
}
