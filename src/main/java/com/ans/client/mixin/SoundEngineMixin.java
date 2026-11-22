package com.ans.client.mixin;

import com.ans.Ans;
import com.ans.client.sound.SoundMuting;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundEngine.PlayResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {
    @Inject(
        method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void ans$mutePackReplacedSounds(SoundInstance sound, CallbackInfoReturnable<PlayResult> cir) {
        try {
            if (SoundMuting.shouldMute(sound)) {
                cir.setReturnValue(PlayResult.NOT_STARTED);
            }
        } catch (RuntimeException e) {
            Ans.LOGGER.debug("[ans] sound filter failed for {}", sound.getIdentifier(), e);
        }
    }
}
