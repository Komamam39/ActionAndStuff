package com.ans.perf;

import com.ans.perfutil.ArmorStandHurt;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.ans.client.mob.groups.ObjectMobs", remap = false)
public abstract class ArmorStandHurtMixin {
    @Inject(method = "lambda$armorStand$0", at = @At("HEAD"), cancellable = true, require = 0)
    private static void ans$hurtDir(ArmorStand var0, CallbackInfoReturnable<Double> var1) {
        if (!"false".equals(System.getProperty("ans.standWiggle", "true"))) {
            var1.setReturnValue(ArmorStandHurt.direction(var0));
        }
    }
}
