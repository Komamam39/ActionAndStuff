package com.ans.perf;

import com.ans.client.particle.RuntimeEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RuntimeEffects.class, remap = false)
public abstract class FpParticleScaleMixin {
    @Unique
    private static final float ANS_SCALE = ans$scale();
    @Shadow
    private boolean attachable;
    @Unique
    private static final float[] ANS_OFF = ans$off();

    @Unique
    private static float[] ans$off() {
        String var0 = System.getProperty("ans.fpParticleOffset", "-0.18,0.07,0");

        try {
            String[] var1 = var0.split(",");
            return new float[]{Float.parseFloat(var1[0].trim()), Float.parseFloat(var1[1].trim()), Float.parseFloat(var1[2].trim())};
        } catch (RuntimeException var2) {
            return new float[]{-0.18F, 0.07F, 0.0F};
        }
    }

    @Unique
    private static float ans$scale() {
        try {
            return Float.parseFloat(System.getProperty("ans.fpParticleScale", "0.5"));
        } catch (NumberFormatException var1) {
            return 0.5F;
        }
    }

    @Inject(
        method = "resolve(Lnet/minecraft/world/entity/Entity;Ljava/lang/String;FLorg/joml/Vector3d;Lorg/joml/Matrix3f;Z)I",
        at = @At("RETURN"),
        require = 0
    )
    private void ans$fpScale(Entity var1, @Nullable String var2, float var3, Vector3d var4, Matrix3f var5, boolean var6, CallbackInfoReturnable<Integer> var7) {
        if (this.attachable && var7.getReturnValueI() == 0) {
            Minecraft var8 = Minecraft.getInstance();
            if (var1 == var8.player && var8.options.getCameraType().isFirstPerson()) {
                if (ANS_SCALE != 1.0F) {
                    var5.scale(ANS_SCALE);
                }

                Quaternionf var9 = var8.gameRenderer.mainCamera().rotation();
                Vector3f var10 = var9.transform(new Vector3f(1.0F, 0.0F, 0.0F));
                Vector3f var11 = var9.transform(new Vector3f(0.0F, 1.0F, 0.0F));
                Vector3f var12 = var9.transform(new Vector3f(0.0F, 0.0F, -1.0F));
                var4.add(
                    var10.x * ANS_OFF[0] + var11.x * ANS_OFF[1] + var12.x * ANS_OFF[2],
                    var10.y * ANS_OFF[0] + var11.y * ANS_OFF[1] + var12.y * ANS_OFF[2],
                    var10.z * ANS_OFF[0] + var11.z * ANS_OFF[1] + var12.z * ANS_OFF[2]
                );
            }
        }
    }
}
