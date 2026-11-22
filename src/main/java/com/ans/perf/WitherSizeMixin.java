package com.ans.perf;

import com.ans.client.bedrock.ActorScales;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ActorScales.class, remap = false)
public abstract class WitherSizeMixin {
    @Unique
    private static final float ANS_SCALE = ans$f("ans.witherScale", 0.85F);

    @Unique
    private static float ans$f(String var0, float var1) {
        try {
            return Float.parseFloat(System.getProperty(var0, Float.toString(var1)));
        } catch (NumberFormatException var3) {
            return var1;
        }
    }

    @Inject(method = "correction", at = @At("RETURN"), cancellable = true, require = 0)
    private static void ans$wither(Entity var0, CallbackInfoReturnable<Float> var1) {
        if (var0.getType() == EntityTypes.WITHER && ANS_SCALE != 1.0F) {
            var1.setReturnValue(var1.getReturnValueF() * ANS_SCALE);
        }
    }
}
