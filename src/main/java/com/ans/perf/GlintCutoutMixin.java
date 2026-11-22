package com.ans.perf;

import com.ans.client.bedrock.FrameResult;
import com.ans.client.bedrock.RenderLayer;
import com.ans.client.render.BedrockTextures;
import com.ans.client.render.LayerSubmitter;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LayerSubmitter.class, remap = false)
public abstract class GlintCutoutMixin {
    @Unique
    private static final boolean ANS_ON = !"false".equals(System.getProperty("ans.glintCutout", "false"));

    @Redirect(
        method = "submitGlint",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;patternedShieldGlint()Lnet/minecraft/client/renderer/rendertype/RenderType;"
        ),
        require = 0
    )
    private static RenderType ans$glint(
        RenderPassInfo<?> var0,
        OrderedSubmitNodeCollector var1,
        FrameResult var2,
        RenderLayer var3,
        BakedGeoModel var4,
        int var5,
        LayerSubmitter.ModelDrawer var6
    ) {
        return ANS_ON && var3.changeColor() >>> 24 == 0
            ? RenderTypes.armorCutoutNoCullGlint(BedrockTextures.resolve(var3.texture()))
            : RenderTypes.patternedShieldGlint();
    }
}
