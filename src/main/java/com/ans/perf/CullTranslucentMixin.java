package com.ans.perf;

import com.ans.client.bedrock.EntityBundle;
import com.ans.client.bedrock.RenderKind;
import java.util.Set;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityBundle.class, remap = false)
public abstract class CullTranslucentMixin {
    @Unique
    private static final boolean ANS_ON = !"false".equals(System.getProperty("ans.cullTranslucent", "true"));
    @Unique
    private static final Set<String> ANS_CULLED = Set.of("kipfdc", "hodxwl", "ighgic", "ojnhbt");

    @Redirect(
        method = "<init>",
        at = @At(value = "NEW", target = "(Ljava/lang/String;Lcom/ans/client/bedrock/RenderKind;ZZZZ)Lcom/ans/client/bedrock/EntityBundle$Material;"),
        require = 0
    )
    private EntityBundle.Material ans$material(String var1, RenderKind var2, boolean var3, boolean var4, boolean var5, boolean var6) {
        if (ANS_ON && var2 == RenderKind.TRANSLUCENT && ANS_CULLED.contains(var1)) {
            var2 = RenderKind.TRANSLUCENT_CULL;
        }

        return new EntityBundle.Material(var1, var2, var3, var4, var5, var6);
    }
}
