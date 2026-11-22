package com.ans.client.mob.player;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.animal.parrot.Parrot.Variant;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.Nullable;

public final class PlayerRenderState extends AvatarRenderState {
    public PlayerSkin skin = DefaultPlayerSkin.getDefaultSkin();
    public boolean showCape = true;
    public boolean isSpectator;
    public float fallFlyingTimeInTicks;
    public float cameraFade = 1.0F;
    public @Nullable Variant parrotOnLeftShoulder;
    public @Nullable Variant parrotOnRightShoulder;

    @Override
    public float fallFlyingScale() {
        return Math.clamp(this.fallFlyingTimeInTicks * this.fallFlyingTimeInTicks / 100.0F, 0.0F, 1.0F);
    }
}
