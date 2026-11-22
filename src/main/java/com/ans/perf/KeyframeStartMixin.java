package com.ans.perf;

import com.ans.client.bedrock.EntityBundle;
import com.ans.client.bedrock.EntityRuntime;
import com.geckolib.animation.AnimationProcessor;
import com.geckolib.animation.object.EasingType;
import com.geckolib.animation.state.AnimationPoint;
import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.animation.state.ControllerState;
import com.geckolib.animation.state.AnimationPoint.Axis;
import com.geckolib.animation.state.AnimationPoint.Transform;
import com.geckolib.cache.animation.Keyframe;
import java.lang.reflect.Field;
import java.util.Set;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRuntime.class, remap = false)
public abstract class KeyframeStartMixin {
    @Unique
    private static final Set<String> ANS_SHORT = Set.of(System.getProperty("ans.shortestBlendAnims", "evrjlf,fpsgkf,cslzxk,kedqbe").split(","));
    @Unique
    private static boolean ans$wrap;
    @Unique
    private static Field ans$defField;

    @Inject(method = "sample", at = @At("HEAD"), require = 0)
    private void ans$sampleHead(@Coerce Object var1, float var2, CallbackInfo var3) {
        ans$wrap = false;
        if (!(var2 >= 0.999F) && var1 != null) {
            try {
                if (ans$defField == null) {
                    ans$defField = var1.getClass().getDeclaredField("def");
                    ans$defField.setAccessible(true);
                }

                if (ans$defField.get(var1) instanceof EntityBundle.AnimationDef var5) {
                    ans$wrap = ANS_SHORT.contains(var5.key());
                }
            } catch (ReflectiveOperationException | RuntimeException var6) {
                ans$wrap = false;
            }
        }
    }

    @Unique
    private static float ans$wrapAngle(float var0) {
        double var1 = Math.PI * 2;

        while (var0 > Math.PI) {
            var0 -= (float)var1;
        }

        while (var0 <= -Math.PI) {
            var0 += (float)var1;
        }

        return var0;
    }

    @Redirect(
        method = "sample",
        at = @At(
            value = "INVOKE",
            target = "Lcom/geckolib/animation/AnimationProcessor;findAnimationPointValue(Lcom/geckolib/animation/state/BoneSnapshot;Lcom/geckolib/animation/state/ControllerState;Lcom/geckolib/animation/state/AnimationPoint;Lcom/geckolib/animation/state/AnimationPoint;ILcom/geckolib/animation/state/AnimationPoint$Transform;Lcom/geckolib/animation/state/AnimationPoint$Axis;Lcom/geckolib/animation/object/EasingType;)F"
        )
    )
    private float ansFix$holdFirstKeyframe(
        BoneSnapshot var1, ControllerState var2, AnimationPoint var3, AnimationPoint var4, int var5, Transform var6, Axis var7, EasingType var8
    ) {
        if (var2.transitionTime() < 0.0) {
            int var9 = var3.keyFramePoints()[var5][var6.index][var7.index];
            if (var9 == -1) {
                Keyframe var10 = var3.getCurrentKeyframe(var5, var6, var7);
                if (var10 != null) {
                    float var11 = (float)var10.endValue().get(var2);
                    return ans$wrap && var6 == Transform.ROTATION ? ans$wrapAngle(var11) : var11;
                }
            }
        }

        float var12 = AnimationProcessor.findAnimationPointValue(var1, var2, var3, var4, var5, var6, var7, var8);
        return ans$wrap && var6 == Transform.ROTATION ? ans$wrapAngle(var12) : var12;
    }
}
