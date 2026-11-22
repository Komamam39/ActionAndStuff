package com.ans.perf;

import com.ans.client.particle.AnsSplash;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, remap = false)
public abstract class WaterSplashMixin {
    @Inject(method = "doWaterSplashEffect", at = @At("HEAD"), require = 0)
    private void ans$splash(CallbackInfo var1) {
        AnsSplash.splash((Entity)(Object)this);
    }

    @Redirect(
        method = "doWaterSplashEffect",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"),
        require = 0
    )
    private void ans$noJavaSplash(Level var1, ParticleOptions var2, double var3, double var5, double var7, double var9, double var11, double var13) {
        if (!AnsSplash.ON || var2 != ParticleTypes.SPLASH || !var1.isClientSide()) {
            var1.addParticle(var2, var3, var5, var7, var9, var11, var13);
        }
    }
}
