package com.ans.perf;

import com.ans.client.bedrock.BoneTransforms;
import com.ans.client.bedrock.FrameResult;
import com.ans.client.bedrock.MobPose;
import com.ans.client.bedrock.RenderLayer;
import com.ans.client.particle.RuntimeEffects;
import com.ans.perfutil.ItemCenter;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.cache.model.GeoLocator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RuntimeEffects.class, remap = false)
public abstract class AttachableLocatorMixin {
    @Shadow
    private FrameResult ownFrame;
    @Shadow
    private Map<BakedGeoModel, ? extends Map<String, ? extends Matrix4fc>> anchors;
    @Shadow
    private @Nullable Entity actor;
    @Unique
    private static final boolean ANS_FOLLOW_3P = "true".equals(System.getProperty("ans.locatorFollowItem3p", "false"));
    @Unique
    private static final boolean ANS_XBOW_FLIP = !"false".equals(System.getProperty("ans.crossbowFlip3p", "true"));
    @Unique
    private static final float ANS_XBOW_UP = ans$f("ans.crossbowUp3p", 0.65F);
    @Unique
    private static final boolean ANS_MIRROR_3P = !"false".equals(System.getProperty("ans.locatorMirrorX3p", "true"));
    @Unique
    private static final boolean ANS_MIRROR_X = !"false".equals(System.getProperty("ans.locatorMirrorX", "false"));
    @Unique
    private static final boolean ANS_FOLLOW = !"false".equals(System.getProperty("ans.locatorFollowItem", "true"));

    @Unique
    private static float ans$f(String var0, float var1) {
        try {
            return Float.parseFloat(System.getProperty(var0, Float.toString(var1)));
        } catch (NumberFormatException var3) {
            return var1;
        }
    }

    @Shadow
    private static GeoLocator locator(BakedGeoModel var0, String var1) {
        throw new AssertionError();
    }

    @Shadow
    private static void prep(Matrix4f var0, GeoBone var1, MobPose var2) {
        throw new AssertionError();
    }

    @Inject(method = "attachableLocator", at = @At("HEAD"), cancellable = true)
    private void ans$handLocator(Matrix4f var1, String var2, CallbackInfo var3) {
        FrameResult var4 = this.ownFrame;
        Map var5 = this.anchors;
        if (var4 != null && var5 != null) {
            for (RenderLayer var7 : var4.layers()) {
                BakedGeoModel var8 = BoneTransforms.model(var7.model());
                if (var8 != null && locator(var8, var2) != null) {
                    return;
                }
            }

            for (RenderLayer var20 : var4.layers()) {
                BakedGeoModel var21 = BoneTransforms.model(var20.model());
                if (var21 != null) {
                    Map var9 = (Map)var5.get(var21);
                    if (var9 != null && !var9.isEmpty()) {
                        Matrix4fc var10 = (Matrix4fc)var9.get("item");
                        GeoBone var11 = var21.boneLookup().get().get("item");
                        if (var10 != null && var11 != null) {
                            var1.mul(var10);
                            GeoBone var12 = var11.parent();
                            Minecraft var13 = Minecraft.getInstance();
                            boolean var14 = this.actor != null && this.actor == var13.player && var13.options.getCameraType().isFirstPerson();
                            boolean var15 = var14 ? ANS_FOLLOW : ANS_FOLLOW_3P;
                            boolean var16 = var14 ? ANS_MIRROR_X : ANS_MIRROR_3P;
                            if (var15) {
                                prep(var1, var11, var4.pose());
                                float[] var17 = ItemCenter.of(var21, var11);
                                if (var17 != null) {
                                    var1.translate(var17[0], var17[1], var17[2]);
                                } else if (var12 != null) {
                                    var1.translate(var12.pivotX() / 16.0F, var12.pivotY() / 16.0F, var12.pivotZ() / 16.0F);
                                } else {
                                    var1.translate(0.0F, 1.5F, 0.0F);
                                }
                            } else if (var12 != null) {
                                var1.translate(var12.pivotX() / 16.0F, var12.pivotY() / 16.0F, var12.pivotZ() / 16.0F);
                            } else {
                                var1.translate(0.0F, 1.5F, 0.0F);
                            }

                            if (!var14
                                && !var15
                                && ANS_XBOW_FLIP
                                && this.actor instanceof LivingEntity var22
                                && var22.getMainHandItem().getItem() instanceof CrossbowItem
                                && (!var22.isUsingItem() || var22.getTicksUsingItem() >= CrossbowItem.getChargeDuration(var22.getMainHandItem(), var22))) {
                                var1.rotateX((float) Math.PI);
                                var1.translateLocal(0.0F, ANS_XBOW_UP, 0.0F);
                            }

                            if (var16) {
                                var1.scale(-1.0F, 1.0F, 1.0F);
                            }

                            var3.cancel();
                            return;
                        }
                    }
                }
            }
        }
    }
}
