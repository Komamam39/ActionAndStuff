package com.ans.client.bedrock;

import com.ans.client.render.ShaderCompat;
import java.util.Locale;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public enum RenderKind {
    SOLID,
    CUTOUT,
    CUTOUT_CULL,
    TRANSLUCENT,
    TRANSLUCENT_CULL,
    TRANSLUCENT_EMISSIVE,
    EYES,
    ENERGY_SWIRL,
    STENCIL_OUTLINE,
    EMISSIVE_ALPHA,
    EMISSIVE_ALPHA_CULL,
    EMISSIVE_OPAQUE,
    EMISSIVE_OPAQUE_CULL,
    BREEZE_WIND,
    ADDITIVE,
    ADDITIVE_ONE,
    STENCIL_OUTLINE_ADDITIVE,
    INVISIBLE,
    CUTOUT_STENCIL_CLIP;

    public boolean fadesByAlpha() {
        return this == EYES || this == STENCIL_OUTLINE;
    }

    public boolean textureMatrixOffset() {
        return this == ENERGY_SWIRL || this == BREEZE_WIND || this == ADDITIVE || this == ADDITIVE_ONE || this == STENCIL_OUTLINE_ADDITIVE;
    }

    public static int fadeToAlpha(int argb) {
        int a = argb >>> 24;
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int b = argb & 0xFF;
        int max = Math.max(r, Math.max(g, b));
        if (max >= 255) {
            return argb;
        } else {
            return max == 0 ? 0 : a * max / 255 << 24 | r * 255 / max << 16 | g * 255 / max << 8 | b * 255 / max;
        }
    }

    public static RenderKind parse(String s) {
        return switch (s.toLowerCase(Locale.ROOT)) {
            case "solid" -> SOLID;
            case "cutout_cull" -> CUTOUT_CULL;
            case "translucent" -> TRANSLUCENT;
            case "translucent_cull" -> TRANSLUCENT_CULL;
            case "translucent_emissive" -> TRANSLUCENT_EMISSIVE;
            case "eyes", "emissive" -> EYES;
            case "energy_swirl" -> ENERGY_SWIRL;
            case "stencil_outline" -> STENCIL_OUTLINE;
            case "emissive_alpha" -> EMISSIVE_ALPHA;
            case "emissive_alpha_cull" -> EMISSIVE_ALPHA_CULL;
            case "emissive_opaque" -> EMISSIVE_OPAQUE;
            case "emissive_opaque_cull" -> EMISSIVE_OPAQUE_CULL;
            case "breeze_wind" -> BREEZE_WIND;
            case "additive" -> ADDITIVE;
            case "additive_one" -> ADDITIVE_ONE;
            case "stencil_outline_additive" -> STENCIL_OUTLINE_ADDITIVE;
            case "invisible" -> INVISIBLE;
            case "cutout_stencil_clip" -> CUTOUT_STENCIL_CLIP;
            default -> CUTOUT;
        };
    }

    public RenderType renderType(Identifier texture, float uOffset, float vOffset) {
        if (this == BREEZE_WIND) {
            return RenderTypes.breezeWind(texture, uOffset, vOffset);
        } else if (this != ADDITIVE && this != ADDITIVE_ONE && this != STENCIL_OUTLINE_ADDITIVE) {
            return this == ENERGY_SWIRL ? RenderTypes.energySwirl(texture, uOffset, vOffset) : this.renderType(texture);
        } else {
            return ShaderCompat.additive(texture, uOffset, vOffset, this != ADDITIVE);
        }
    }

    public RenderType renderType(Identifier texture) {
        return switch (this) {
            case SOLID -> RenderTypes.entitySolid(texture);
            case CUTOUT, EMISSIVE_ALPHA, EMISSIVE_OPAQUE, CUTOUT_STENCIL_CLIP -> RenderTypes.entityCutout(texture);
            case CUTOUT_CULL, EMISSIVE_OPAQUE_CULL -> RenderTypes.entityCutoutCull(texture);
            case TRANSLUCENT -> RenderTypes.entityTranslucent(texture);
            case TRANSLUCENT_CULL -> RenderTypes.entityTranslucentCull(texture);
            case TRANSLUCENT_EMISSIVE -> RenderTypes.entityTranslucentEmissive(texture);
            case EYES, STENCIL_OUTLINE -> RenderTypes.eyes(texture);
            case ENERGY_SWIRL -> RenderTypes.energySwirl(texture, 0.0F, 0.0F);
            case EMISSIVE_ALPHA_CULL -> RenderTypes.entityTranslucentEmissive(texture);
            case BREEZE_WIND -> RenderTypes.breezeWind(texture, 0.0F, 0.0F);
            case ADDITIVE, ADDITIVE_ONE, STENCIL_OUTLINE_ADDITIVE -> ShaderCompat.additive(texture, 0.0F, 0.0F, this != ADDITIVE);
            case INVISIBLE -> RenderTypes.entityCutout(texture);
        };
    }
}
