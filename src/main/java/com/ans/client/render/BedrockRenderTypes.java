package com.ans.client.render;

import com.ans.Ans;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.RenderPipeline.Snippet;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.TextureTransform.OffsetTextureTransform;
import net.minecraft.resources.Identifier;

public final class BedrockRenderTypes {
    private static final Snippet MATRICES_FOG = RenderPipeline.builder()
        .withBindGroupLayout(BindGroupLayouts.GLOBALS)
        .withBindGroupLayout(BindGroupLayouts.PROJECTION)
        .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
        .withBindGroupLayout(BindGroupLayouts.FOG)
        .buildSnippet();
    private static final boolean ADDITIVE_DEPTH_BIAS = !"false".equalsIgnoreCase(System.getProperty("ans.additiveDepthBias", "true"));
    public static final RenderPipeline ADDITIVE_ALPHA = pipeline("bedrock_additive_alpha", BlendFunction.LIGHTNING);
    public static final RenderPipeline ADDITIVE_ONE = pipeline("bedrock_additive_one", BlendFunction.ADDITIVE);
    private static final Map<Object, RenderType> CACHE = new ConcurrentHashMap<>();
    private static Method create;

    private BedrockRenderTypes() {
    }

    private static RenderPipeline pipeline(String var0, BlendFunction var1) {
        return RenderPipeline.builder(MATRICES_FOG)
            .withLocation(Ans.id("pipeline/" + var0))
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withColorTargetState(new ColorTargetState(var1))
            .withCull(false)
            .withVertexBinding(0, DefaultVertexFormat.ENTITY)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withDepthStencilState(
                ADDITIVE_DEPTH_BIAS
                    ? new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 1.0F, 1.0F)
                    : new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false)
            )
            .build();
    }

    public static RenderType additive(Identifier var0, float var1, float var2, boolean var3) {
        BedrockRenderTypes.Key var4 = new BedrockRenderTypes.Key(var3, var0, var1, var2);
        RenderType var5 = CACHE.get(var4);
        if (var5 != null) {
            return var5;
        }

        if (CACHE.size() > 4096) {
            CACHE.clear();
        }

        RenderType var6;
        try {
            RenderSetup var7 = RenderSetup.builder(var3 ? ADDITIVE_ONE : ADDITIVE_ALPHA)
                .withTexture("Sampler0", var0)
                .setTextureTransform(new OffsetTextureTransform(var1, var2))
                .sortOnUpload()
                .createRenderSetup();
            var6 = create("ans_additive", var7);
        } catch (RuntimeException | ReflectiveOperationException var8) {
            Ans.LOGGER.warn("[ans] additive render type unavailable ({}), using eyes", var8.toString());
            var6 = RenderTypes.eyes(var0);
        }

        CACHE.put(var4, var6);
        return var6;
    }

    private static RenderType create(String var0, RenderSetup var1) throws ReflectiveOperationException {
        if (create == null) {
            Method var2 = RenderType.class.getDeclaredMethod("create", String.class, RenderSetup.class);
            var2.setAccessible(true);
            create = var2;
        }

        return (RenderType)create.invoke(null, var0, var1);
    }

    private record Key(boolean one, Identifier texture, float u, float v) {
    }
}
