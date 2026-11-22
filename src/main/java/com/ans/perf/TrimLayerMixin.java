package com.ans.perf;

import com.ans.client.bedrock.RenderKind;
import com.ans.client.render.LayerSubmitter;
import com.ans.perfutil.PassKind;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LayerSubmitter.class, remap = false)
public abstract class TrimLayerMixin {
    @Unique
    private static final boolean ANS_ON = !"false".equals(System.getProperty("ans.trimOnTop", "true"));

    @Redirect(
        method = "submitPass",
        at = @At(
            value = "INVOKE",
            target = "Lcom/ans/client/bedrock/RenderKind;renderType(Lnet/minecraft/resources/Identifier;FF)Lnet/minecraft/client/renderer/rendertype/RenderType;"
        ),
        require = 0
    )
    private static RenderType ans$trim(RenderKind var0, Identifier var1, float var2, float var3) {
        PassKind.last = var0;
        return ANS_ON && var1 != null && "ans".equals(var1.getNamespace()) && var1.getPath().startsWith("derived/trim/")
            ? RenderTypes.entityCutoutZOffset(var1)
            : var0.renderType(var1, var2, var3);
    }
}
