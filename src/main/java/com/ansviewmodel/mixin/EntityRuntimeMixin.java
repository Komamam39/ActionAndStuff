package com.ansviewmodel.mixin;

import com.ans.client.bedrock.EntityRuntime;
import com.ans.client.bedrock.FrameResult;
import com.ans.client.bedrock.MobPose;
import com.ans.client.molang.MolangContext;
import com.ansviewmodel.AnsViewmodel;
import com.ansviewmodel.config.ConfigManager;
import com.ansviewmodel.config.ViewmodelConfig;
import com.ansviewmodel.render.ArmPoseDelta;
import com.geckolib.cache.animation.Animation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRuntime.class, remap = false)
public abstract class EntityRuntimeMixin {
    @Shadow
    @Final
    private String mobId;
    @Shadow
    @Final
    private MolangContext ctx;
    @Shadow
    private MobPose pose;
    @Shadow
    private boolean firstPerson;
    @Shadow
    private boolean ui;
    @Unique
    private static boolean ansViewmodel$hookLogged;
    @Unique
    private static boolean ansViewmodel$missingArmLogged;

    @Unique
    private boolean ansViewmodel$isLocalFirstPerson() {
        return this.firstPerson && !this.ui && this.ctx.attachable == null && "player".equals(this.mobId);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void ansViewmodel$beginFrame(CallbackInfoReturnable<FrameResult> cir) {
        if (this.ansViewmodel$isLocalFirstPerson()) {
            ArmPoseDelta.beginFrame();
        }
    }

    @Inject(method = "sample", at = @At("TAIL"))
    private void ansViewmodel$afterSample(CallbackInfo ci, @Local(argsOnly = true) float weight, @Local(ordinal = 0) Animation animation) {
        if (this.ansViewmodel$isLocalFirstPerson() && animation != null && "animation.player.first_person_empty_hand".equals(animation.name())) {
            if (!ansViewmodel$hookLogged) {
                ansViewmodel$hookLogged = true;
                AnsViewmodel.LOGGER.info("[ans_viewmodel] First-person base pose hook active ({} sampled, weight {})", animation.name(), weight);
            }

            ViewmodelConfig config = ConfigManager.get();
            if (config.active() && weight != 0.0F && Float.isFinite(weight)) {
                for (int arm = 0; arm < ArmPoseDelta.ARM_BONES.length; arm++) {
                    float[] values = this.pose.get(ArmPoseDelta.ARM_BONES[arm]);
                    if (values == null) {
                        if (!ansViewmodel$missingArmLogged) {
                            ansViewmodel$missingArmLogged = true;
                            AnsViewmodel.LOGGER
                                .warn("[ans_viewmodel] '{}' was not posed by {}; that arm cannot be adjusted", ArmPoseDelta.ARM_BONES[arm], animation.name());
                        }
                    } else {
                        ArmPoseDelta.apply(config, arm, values, weight);
                    }
                }
            }
        }
    }
}
