package com.ans.perf;

import com.ans.client.particle.ParticleDef;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.joml.Vector3d;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.ans.client.particle.Emitter", remap = false)
public abstract class ProjectileSplashMixin {
    @Shadow
    @Final
    ParticleDef def;
    @Shadow
    @Final
    @Nullable Entity actor;
    @Shadow
    @Final
    @Nullable String locator;
    @Shadow
    @Final
    Vector3d pos;
    @Shadow
    boolean bound;

    @Inject(method = "place", at = @At("RETURN"), require = 0)
    private void ans$surface(float var1, CallbackInfoReturnable<Boolean> var2) {
        if (var2.getReturnValueZ() && this.locator == null && this.actor instanceof AbstractArrow var3) {
            if (this.def != null && !this.def.id.endsWith(":aynkct") && !"false".equals(System.getProperty("ans.projectileSplash"))) {
                Level var18 = var3.level();
                if (var18 != null) {
                    BlockPos var5 = BlockPos.containing(var3.getX(), var3.getY() + 1.0, var3.getZ());
                    double var6 = Double.NaN;

                    for (int var8 = 0; var8 < 5; var8++) {
                        BlockPos var9 = var5.below(var8);
                        FluidState var10 = var18.getFluidState(var9);
                        if (var10.is(FluidTags.WATER)) {
                            if (!var18.getFluidState(var9.above()).is(FluidTags.WATER)) {
                                var6 = var9.getY() + var10.getHeight(var18, var9);
                            }
                            break;
                        }
                    }

                    if (!Double.isNaN(var6)) {
                        double var19 = var3.getX();
                        double var20 = var3.getZ();
                        double var12 = var3.yo;
                        double var14 = var3.getY();
                        if (var12 > var6 && var14 <= var6 && var12 - var14 > 1.0E-6) {
                            double var16 = (var12 - var6) / (var12 - var14);
                            var19 = var3.xo + (var3.getX() - var3.xo) * var16;
                            var20 = var3.zo + (var3.getZ() - var3.zo) * var16;
                        }

                        this.pos.set(var19, var6, var20);
                        this.bound = false;
                    }
                }
            }
        }
    }
}
