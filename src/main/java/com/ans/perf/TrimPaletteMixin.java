package com.ans.perf;

import com.ans.client.attachable.TrimTextures;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TrimTextures.class, remap = false)
public abstract class TrimPaletteMixin {
    @Redirect(
        method = "palette",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/Identifier;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/Identifier;"),
        require = 0
    )
    private static Identifier ans$palette(String var0) {
        return Identifier.tryParse(var0.replace("textures/trims/color_palettes/", "textures/palettes/trim/"));
    }
}
