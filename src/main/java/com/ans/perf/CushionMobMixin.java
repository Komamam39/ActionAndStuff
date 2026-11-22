package com.ans.perf;

import com.ans.client.mob.AnimatedMob;
import com.ans.client.mob.groups.ObjectMobs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ObjectMobs.class, remap = false)
public abstract class CushionMobMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void ans$cushion(CallbackInfo var0) {
        if (!"false".equalsIgnoreCase(System.getProperty("ans.cushion", "true"))) {
            AnimatedMob.builder(EntityTypes.CUSHION)
                .query("variant", var0x -> 15 - var0x.getColor().getId())
                .yaw((var0x, var1) -> Mth.wrapDegrees(var0x.getYRot(var1)))
                .living(false)
                .shadow(0.0F)
                .register();
        }
    }
}
