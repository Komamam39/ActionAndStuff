package com.ans.perf;

import com.ans.client.bedrock.EntityRuntime;
import com.ans.client.bedrock.FrameResult;
import com.ans.client.particle.BedrockParticles;
import com.ans.perfutil.FpFlag;
import com.geckolib.cache.model.BakedGeoModel;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.ans.client.attachable.AttachableRenderer", remap = false)
public abstract class FpParticleSpaceMixin {
    @Redirect(
        method = "submit",
        at = @At(
            value = "INVOKE",
            target = "Lcom/ans/client/particle/BedrockParticles;captureAttachable(Lcom/ans/client/bedrock/EntityRuntime;Lorg/joml/Matrix4fc;Lnet/minecraft/world/phys/Vec3;Lcom/ans/client/bedrock/FrameResult;Ljava/util/Map;)V"
        ),
        require = 0
    )
    private static void ans$capture(
        EntityRuntime var0, Matrix4fc var1, Vec3 var2, FrameResult var3, Map<BakedGeoModel, ? extends Map<String, ? extends Matrix4fc>> var4
    ) {
        if (FpFlag.active && !"false".equals(System.getProperty("ans.fpParticles", "true"))) {
            Minecraft var5 = Minecraft.getInstance();
            Matrix4fc var6 = "true".equals(System.getProperty("ans.fpParticleRot"))
                ? new Matrix4f().rotation(var5.gameRenderer.mainCamera().rotation()).mul(var1)
                : var1;
            BedrockParticles.captureAttachable(var0, var6, var5.gameRenderer.mainCamera().position(), var3, var4);
        } else {
            BedrockParticles.captureAttachable(var0, var1, var2, var3, var4);
        }
    }
}
