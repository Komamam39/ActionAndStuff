package com.ans.perf;

import com.ans.client.equipment.EquipmentQueries;
import com.ans.client.mob.player.PlayerQueries;
import com.ans.client.molang.BedrockQueries;
import com.ans.client.molang.EngineQueries;
import com.ans.perfutil.UseDuration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {PlayerQueries.class, EquipmentQueries.class, EngineQueries.class, BedrockQueries.class}, remap = false)
public abstract class UseDurationMixin {
    @Inject(method = "install", at = @At("TAIL"))
    private static void ans$useDuration(CallbackInfo var0) {
        UseDuration.install();
    }
}
