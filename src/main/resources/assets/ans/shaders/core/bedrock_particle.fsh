#version 330
#extension GL_ARB_separate_shader_objects : require

// PARTICLES / PFX: Java's core/particle fragment shader with the Bedrock material's alpha cut-off (ALPHA_CUTOFF define):
// particles_alpha / particles_opaque discard below 0.5 (Bedrock ALPHA_TEST), particles_blend / particles_add only fully
// transparent texels (Bedrock does not alpha-test blended particles). Java's own particles discard below 0.1.
// Minecraft 26.3: #include instead of #moj_import, explicit varying locations (match core/particle.vsh).

#include <minecraft:fog.glsl>
#include <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

layout(location = 0) in float sphericalVertexDistance;
layout(location = 1) in float cylindricalVertexDistance;
layout(location = 2) in vec2 texCoord0;
layout(location = 3) in vec4 vertexColor;

layout(location = 0) out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < ALPHA_CUTOFF) {
        discard;
    }
    fragColor = apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
