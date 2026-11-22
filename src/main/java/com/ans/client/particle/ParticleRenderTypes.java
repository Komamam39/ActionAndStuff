package com.ans.client.particle;

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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup.RenderSetupBuilder;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

final class ParticleRenderTypes {
    private static final Snippet PARTICLE = RenderPipeline.builder()
        .withBindGroupLayout(BindGroupLayouts.GLOBALS)
        .withBindGroupLayout(BindGroupLayouts.PROJECTION)
        .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
        .withBindGroupLayout(BindGroupLayouts.FOG)
        .withVertexShader("core/particle")
        .withFragmentShader(Ans.id("core/bedrock_particle"))
        .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER2)
        .withVertexBinding(0, DefaultVertexFormat.PARTICLE)
        .withPrimitiveTopology(PrimitiveTopology.QUADS)
        .withCull(false)
        .buildSnippet();
    private static final float CUTOFF_ALPHA_TEST = 0.5F;
    private static final float CUTOFF_BLENDED = 0.0019607844F;
    static final RenderPipeline ALPHA = RenderPipeline.builder(PARTICLE)
        .withLocation(Ans.id("pipeline/bedrock_particle_alpha"))
        .withShaderDefine("ALPHA_CUTOFF", 0.5F)
        .withColorTargetState(ColorTargetState.DEFAULT)
        .withDepthStencilState(DepthStencilState.DEFAULT)
        .build();
    static final RenderPipeline BLEND = RenderPipeline.builder(PARTICLE)
        .withLocation(Ans.id("pipeline/bedrock_particle_blend"))
        .withShaderDefine("ALPHA_CUTOFF", 0.0019607844F)
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
        .build();
    static final RenderPipeline ADD = RenderPipeline.builder(PARTICLE)
        .withLocation(Ans.id("pipeline/bedrock_particle_add"))
        .withShaderDefine("ALPHA_CUTOFF", 0.0019607844F)
        .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
        .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
        .build();
    private static final Map<ParticleRenderTypes.Key, RenderType> CACHE = new ConcurrentHashMap<>();
    private static Method create;
    private static boolean warned;

    private ParticleRenderTypes() {
    }

    static @Nullable RenderType get(Identifier var0, ParticleDef.Render var1) {
        return CACHE.computeIfAbsent(new ParticleRenderTypes.Key(var0, var1), var0x -> {
            RenderPipeline var1x = switch (var0x.render()) {
                case BLEND -> BLEND;
                case ADD -> ADD;
                default -> ALPHA;
            };
            RenderSetupBuilder var2 = RenderSetup.builder(var1x).withTexture("Sampler0", var0x.texture()).useLightmap();
            if (var0x.render() == ParticleDef.Render.BLEND || var0x.render() == ParticleDef.Render.ADD) {
                var2.sortOnUpload();
            }

            try {
                return create("ans_bedrock_particle_" + var0x.render().name().toLowerCase(Locale.ROOT), var2.createRenderSetup());
            } catch (RuntimeException | ReflectiveOperationException var4) {
                if (!warned) {
                    Ans.LOGGER.warn("[ans] particle render type unavailable ({}); Bedrock particles are not drawn", var4.toString());
                }

                warned = true;
                return null;
            }
        });
    }

    private static RenderType create(String var0, RenderSetup var1) throws ReflectiveOperationException {
        if (create == null) {
            Method var2 = RenderType.class.getDeclaredMethod("create", String.class, RenderSetup.class);
            var2.setAccessible(true);
            create = var2;
        }

        return (RenderType)create.invoke(null, var0, var1);
    }

    private record Key(Identifier texture, ParticleDef.Render render) {
    }
}
