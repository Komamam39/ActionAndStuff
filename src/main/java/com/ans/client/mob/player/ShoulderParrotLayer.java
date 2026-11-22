package com.ans.client.mob.player;

import com.ans.client.bedrock.FrameResult;
import com.ans.client.equipment.EquipmentLayer;
import com.ans.client.render.MobAnimatable;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.animal.parrot.ParrotModel.Pose;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.parrot.Parrot.Variant;
import net.minecraft.world.entity.player.Player;

final class ShoulderParrotLayer extends GeoRenderLayer<MobAnimatable, Player, LivingEntityRenderState> {
    private final ParrotModel model;

    ShoulderParrotLayer(PlayerRenderer var1, Context var2) {
        super(var1);
        this.model = new ParrotModel(var2.bakeLayer(ModelLayers.PARROT));
    }

    @Override
    public void submitRenderTask(RenderPassInfo<LivingEntityRenderState> var1, SubmitNodeCollector var2) {
        if (var1.renderState() instanceof PlayerRenderState var3
            && !var3.isInvisible
            && !var3.isSpectator
            && (var3.parrotOnLeftShoulder != null || var3.parrotOnRightShoulder != null)) {
            FrameResult var6 = var1.getGeckolibData(FrameResult.TICKET);
            GeoBone var5 = var6 == null ? null : PlayerRenderer.bone(this.getGeoModel(), var6, "body");
            if (var5 != null) {
                if (var3.parrotOnLeftShoulder != null) {
                    this.submit(var1, var2, var6, var5, var3, var3.parrotOnLeftShoulder, true);
                }

                if (var3.parrotOnRightShoulder != null) {
                    this.submit(var1, var2, var6, var5, var3, var3.parrotOnRightShoulder, false);
                }
            }
        }
    }

    private void submit(
        RenderPassInfo<LivingEntityRenderState> var1, SubmitNodeCollector var2, FrameResult var3, GeoBone var4, PlayerRenderState var5, Variant var6, boolean var7
    ) {
        PoseStack var8 = var1.poseStack();
        var8.pushPose();
        var8.last().set(var1.getModelRenderMatrixPose());
        EquipmentLayer.toBone(var8, var4, var3.pose());
        var8.scale(-1.0F, -1.0F, 1.0F);
        var8.translate(var7 ? 0.4F : -0.4F, -1.5F, 0.0F);
        ParrotRenderState var9 = new ParrotRenderState();
        var9.pose = Pose.ON_SHOULDER;
        var9.ageInTicks = var5.ageInTicks;
        var9.walkAnimationPos = var5.walkAnimationPos;
        var9.walkAnimationSpeed = var5.walkAnimationSpeed;
        var9.yRot = var5.yRot;
        var9.xRot = var5.xRot;
        var2.submitModel(this.model, var9, var8, ParrotRenderer.getVariantTexture(var6), var1.packedLight(), OverlayTexture.NO_OVERLAY, var5.outlineColor);
        var8.popPose();
    }
}
