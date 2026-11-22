package com.ans.client.mob.player;

import com.ans.client.bedrock.ResourceOverrides;
import com.ans.client.mob.AnimatedMob;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset.Texture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;

public final class PlayerRendering {
    public static boolean enabled = !"false".equalsIgnoreCase(System.getProperty("ans.player", "true"));
    static final Identifier WIDE_GEOMETRY = AnimatedMob.geoId("player");
    static final Identifier SLIM_GEOMETRY = AnimatedMob.geoId("player_slim");
    static final Identifier CAPE_GEOMETRY = AnimatedMob.geoId("player_cape");
    static final Map<String, Texture> TEST_SKINS = new ConcurrentHashMap<>();

    private PlayerRendering() {
    }

    public static boolean replaces(AbstractClientPlayer player) {
        return enabled;
    }

    public static Texture skinBody(AbstractClientPlayer p) {
        Texture test = TEST_SKINS.isEmpty() ? null : TEST_SKINS.get(p.getGameProfile().name());
        return test != null ? test : p.getSkin().body();
    }

    public static void install() {
        ResourceOverrides.texture(
            EntityTypes.PLAYER, (entity, key) -> key.equals("default") && entity instanceof AbstractClientPlayer p ? skinBody(p).texturePath() : null
        );
        ResourceOverrides.geometry(
            EntityTypes.PLAYER,
            (entity, key) -> key.equals("default") && entity instanceof AbstractClientPlayer p && p.getSkin().model() == PlayerModelType.SLIM
                ? SLIM_GEOMETRY
                : null
        );
    }

    static Identifier geometry(PlayerSkin skin) {
        return skin.model() == PlayerModelType.SLIM ? SLIM_GEOMETRY : WIDE_GEOMETRY;
    }
}
