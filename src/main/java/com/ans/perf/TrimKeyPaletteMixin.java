package com.ans.perf;

import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.ans.client.attachable.TrimTextures$Composed", remap = false)
public abstract class TrimKeyPaletteMixin {
    @Unique
    private static final Identifier ANS_KEY = Identifier.withDefaultNamespace("textures/palettes/trim_base.png");

    @Redirect(
        method = "loadContents",
        at = @At(value = "FIELD", target = "Lcom/ans/client/attachable/TrimTextures;KEY_PALETTE:Lnet/minecraft/resources/Identifier;"),
        require = 0
    )
    private Identifier ans$key() {
        return ANS_KEY;
    }
}
