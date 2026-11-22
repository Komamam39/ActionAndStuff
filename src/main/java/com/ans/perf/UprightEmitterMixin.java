package com.ans.perf;

import com.ans.client.particle.RuntimeEffects;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Vector3d;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RuntimeEffects.class, remap = false)
public abstract class UprightEmitterMixin {
    @Shadow
    private boolean attachable;

    @Inject(
        method = "resolve(Lnet/minecraft/world/entity/Entity;Ljava/lang/String;FLorg/joml/Vector3d;Lorg/joml/Matrix3f;Z)I",
        at = @At("RETURN"),
        require = 0
    )
    private void ans$upright(Entity var1, @Nullable String var2, float var3, Vector3d var4, Matrix3f var5, boolean var6, CallbackInfoReturnable<Integer> var7) {
        if (var2 == null && !this.attachable && var7.getReturnValueI() == 0) {
            if (!"false".equals(System.getProperty("ans.uprightEmitters"))) {
                if (!(Math.abs(var5.m11()) > 0.99F) && !(var5.determinant() < 0.0F)) {
                    float var8 = var5.m20();
                    float var9 = var5.m22();
                    if (var8 * var8 + var9 * var9 < 1.0E-6F) {
                        var8 = var5.m10();
                        var9 = var5.m12();
                    }

                    var5.rotationY((float)Math.atan2(var8, var9));
                }
            }
        }
    }
}
