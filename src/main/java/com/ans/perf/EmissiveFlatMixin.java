package com.ans.perf;

import com.ans.client.bedrock.RenderKind;
import com.ans.client.render.LayerSubmitter;
import com.ans.perfutil.FlatLightConsumer;
import com.ans.perfutil.PassKind;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector.CustomGeometryRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LayerSubmitter.class, remap = false)
public abstract class EmissiveFlatMixin {
    @Unique
    private static final boolean ANS_ON = !"false".equals(System.getProperty("ans.flatEmissive", "true"));
    @Unique
    private static final boolean ANS_GLASS = !"false".equals(System.getProperty("ans.flatGlass", "true"));

    @Redirect(
        method = "submitPass",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitCustomGeometry(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;Lnet/minecraft/client/renderer/SubmitNodeCollector$CustomGeometryRenderer;)V"
        ),
        require = 0
    )
    private static void ans$flat(OrderedSubmitNodeCollector var0, PoseStack var1, RenderType var2, CustomGeometryRenderer var3) {
        boolean var4 = ANS_GLASS && PassKind.last == RenderKind.TRANSLUCENT_CULL;
        PassKind.last = null;
        if ((!ANS_ON || !ans$emissive(var2)) && !var4) {
            var0.submitCustomGeometry(var1, var2, var3);
        } else {
            var0.submitCustomGeometry(var1, var2, (var1x, var2x) -> var3.render(var1x, new FlatLightConsumer(var2x)));
        }
    }

    @Unique
    private static boolean ans$emissive(RenderType var0) {
        if (var0.pipeline() == RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE) {
            return true;
        }

        String var1 = var0.toString();
        return var1 != null && var1.contains("translucent_emissive");
    }
}
