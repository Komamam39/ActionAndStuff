package com.ans.perf;

import com.ans.client.molang.MolangQueries;
import com.ans.perfutil.WaterNear;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.ans.client.mob.player.PlayerQueries", remap = false)
public abstract class PlayerSwimMixin {
    @Inject(method = "perType", at = @At("TAIL"), require = 0)
    private static void ans$swim(CallbackInfo var0) {
        if (!"false".equals(System.getProperty("ans.swimSurface", "true"))) {
            MolangQueries.registerFor(
                EntityTypes.PLAYER, "is_swimming", (Player var0x) -> !var0x.isSwimming() && (!var0x.isVisuallySwimming() || !WaterNear.near(var0x)) ? 0.0 : 1.0
            );
        }
    }
}
