package com.ans.perfutil;

import com.mojang.blaze3d.vertex.VertexConsumer;

public final class FlatLightConsumer implements VertexConsumer {
    private final VertexConsumer out;

    public FlatLightConsumer(VertexConsumer var1) {
        this.out = var1;
    }

    @Override
    public void addVertex(float var1, float var2, float var3, int var4, float var5, float var6, int var7, int var8, float var9, float var10, float var11) {
        this.out.addVertex(var1, var2, var3, var4, var5, var6, var7, var8, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public VertexConsumer addVertex(float var1, float var2, float var3) {
        this.out.addVertex(var1, var2, var3);
        return this;
    }

    @Override
    public VertexConsumer setColor(int var1, int var2, int var3, int var4) {
        this.out.setColor(var1, var2, var3, var4);
        return this;
    }

    @Override
    public VertexConsumer setColor(int var1) {
        this.out.setColor(var1);
        return this;
    }

    @Override
    public VertexConsumer setUv(float var1, float var2) {
        this.out.setUv(var1, var2);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int var1, int var2) {
        this.out.setUv1(var1, var2);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int var1, int var2) {
        this.out.setUv2(var1, var2);
        return this;
    }

    @Override
    public VertexConsumer setUv3(float var1, float var2) {
        this.out.setUv3(var1, var2);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float var1, float var2, float var3) {
        this.out.setNormal(0.0F, 1.0F, 0.0F);
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float var1) {
        this.out.setLineWidth(var1);
        return this;
    }
}
