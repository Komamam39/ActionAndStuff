package com.ans.client.attachable;

import com.ans.Ans;
import com.ans.client.bedrock.AnimationLookup;
import com.ans.client.settings.Experience;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jspecify.annotations.Nullable;

public final class TrimTextures {
    public static final String PREFIX = "ans:derived/trim/";
    private static final Identifier INDEX = Ans.id("attachable/_trims.json");
    private static final Identifier KEY_PALETTE = Identifier.withDefaultNamespace("textures/trims/color_palettes/trim_palette.png");
    private static final Map<Identifier, TrimTextures.Index> INDEXES = new HashMap<>();
    private static final Map<Identifier, Identifier> COMPOSED = new HashMap<>();
    private static final Map<Identifier, Boolean> EXISTS = new HashMap<>();
    private static int seenGeneration = -1;

    private TrimTextures() {
    }

    public static synchronized @Nullable Identifier texture(ItemStack var0, EquipmentSlot var1) {
        return texture(var0, var1, null);
    }

    public static synchronized @Nullable Identifier texture(ItemStack var0, EquipmentSlot var1, @Nullable Identifier var2) {
        ArmorTrim var3 = var0.get(DataComponents.TRIM);
        if (var3 == null) {
            return null;
        }

        int var4 = AnimationLookup.generation();
        if (var4 != seenGeneration) {
            seenGeneration = var4;
            INDEXES.clear();
            EXISTS.clear();
        }

        TrimTextures.Index var5 = INDEXES.computeIfAbsent(Experience.resolve(INDEX), TrimTextures::load);
        Identifier var6 = pattern(var5, var3.pattern().value().assetId(), var1 == EquipmentSlot.LEGS);
        if (var6 == null) {
            return null;
        }

        String var7 = var3.material().value().paletteId().getPath();
        Identifier var8 = palette(var5, var7);
        if (var8 == null) {
            return null;
        }

        Identifier var9 = Identifier.parse(
            "ans:derived/trim/"
                + var6.getNamespace()
                + "/"
                + strip(var6.getPath())
                + "/"
                + var8.getNamespace()
                + "/"
                + strip(var8.getPath())
                + (var2 != null ? "/on/" + var2.getNamespace() + "/" + strip(var2.getPath()) : "")
                + ".png"
        );
        Identifier var10 = var8;
        return COMPOSED.computeIfAbsent(var9, var3x -> {
            Minecraft.getInstance().getTextureManager().registerAndLoad(var3x, new TrimTextures.Composed(var3x, var6, var10, var2));
            return (Identifier)var3x;
        });
    }

    private static String strip(String var0) {
        return var0.replaceAll("\\.png$", "");
    }

    private static @Nullable Identifier pattern(TrimTextures.Index var0, Identifier var1, boolean var2) {
        String var3 = var1.getPath() + (var2 ? "_leggings" : "");
        if ("minecraft".equals(var1.getNamespace()) && var0.patterns().containsKey(var3)) {
            return var0.patterns().get(var3);
        }

        if (!var0.javaPatterns()) {
            return null;
        }

        Identifier var4 = var1.withPath("textures/trims/entity/" + (var2 ? "humanoid_leggings/" : "humanoid/") + var1.getPath() + ".png");
        return exists(var4) ? var4 : null;
    }

    private static @Nullable Identifier palette(TrimTextures.Index var0, String var1) {
        if (var0.palettes().containsKey(var1)) {
            return var0.palettes().get(var1);
        }

        Identifier var2 = Identifier.tryParse("minecraft:textures/trims/color_palettes/" + var1 + ".png");
        return var2 != null && exists(var2) ? var2 : null;
    }

    private static boolean exists(Identifier var0) {
        return EXISTS.computeIfAbsent(var0, var0x -> Minecraft.getInstance().getResourceManager().getResource(var0x).isPresent());
    }

    private static TrimTextures.Index load(Identifier var0) {
        HashMap var1 = new HashMap();
        HashMap var2 = new HashMap();
        boolean var3 = true;
        Optional var4 = Minecraft.getInstance().getResourceManager().getResource(var0);
        if (var4.isPresent()) {
            try (BufferedReader var5 = ((Resource)var4.get()).openAsReader()) {
                JsonObject var6 = JsonParser.parseReader(var5).getAsJsonObject();
                var3 = !var6.has("java_patterns") || var6.get("java_patterns").getAsBoolean();
                read(var6, "patterns", var1);
                read(var6, "palettes", var2);
            } catch (Exception var10) {
                Ans.LOGGER.error("[ans] Could not read {}", var0, var10);
            }
        }

        return new TrimTextures.Index(var3, Map.copyOf(var1), Map.copyOf(var2));
    }

    private static void read(JsonObject var0, String var1, Map<String, Identifier> var2) {
        if (var0.has(var1) && var0.get(var1).isJsonObject()) {
            for (Entry var4 : var0.getAsJsonObject(var1).entrySet()) {
                Identifier var5 = Identifier.tryParse(((JsonElement)var4.getValue()).getAsString());
                if (var5 != null) {
                    var2.put((String)var4.getKey(), var5);
                }
            }
        }
    }

    private static final class Composed extends ReloadableTexture {
        private final Identifier pattern;
        private final Identifier palette;
        private final @Nullable Identifier base;

        Composed(Identifier var1, Identifier var2, Identifier var3, @Nullable Identifier var4) {
            super(var1);
            this.pattern = var2;
            this.palette = var3;
            this.base = var4;
        }

        @Override
        public TextureContents loadContents(ResourceManager var1) throws IOException {
            NativeImage var2 = TextureContents.load(var1, this.pattern).image();
            NativeImage var3 = null;
            if (this.base != null && var1.getResource(this.base).isPresent()) {
                NativeImage var4 = TextureContents.load(var1, this.base).image();
                if (var4.getWidth() == var2.getWidth() && var4.getHeight() == var2.getHeight()) {
                    var3 = var4;
                } else {
                    var4.close();
                }
            }

            try (
                NativeImage var25 = TextureContents.load(var1, TrimTextures.KEY_PALETTE).image();
                NativeImage var5 = TextureContents.load(var1, this.palette).image();
            ) {
                int var6 = Math.min(var25.getWidth(), var5.getWidth());
                int[] var7 = new int[var6];
                int[] var8 = new int[var6];

                for (int var9 = 0; var9 < var6; var9++) {
                    var7[var9] = var25.getPixel(var9, 0) & 16777215;
                    var8[var9] = var5.getPixel(var9, 0) & 16777215;
                }

                for (int var26 = 0; var26 < var2.getHeight(); var26++) {
                    for (int var10 = 0; var10 < var2.getWidth(); var10++) {
                        int var11 = var2.getPixel(var10, var26);
                        if (var11 >>> 24 == 0 && var3 != null) {
                            var2.setPixel(var10, var26, var3.getPixel(var10, var26));
                        } else if (var11 >>> 24 < 128) {
                            var2.setPixel(var10, var26, 0);
                        } else {
                            int var12 = var11 & 16777215;

                            for (int var13 = 0; var13 < var6; var13++) {
                                if (var7[var13] == var12) {
                                    var12 = var8[var13];
                                    break;
                                }
                            }

                            var2.setPixel(var10, var26, 0xFF000000 | var12);
                        }
                    }
                }
            } finally {
                if (var3 != null) {
                    var3.close();
                }
            }

            return new TextureContents(var2, null);
        }

        @Override
        public String toString() {
            return "TrimTextures.Composed[" + this.pattern + " + " + this.palette + (this.base != null ? " over " + this.base : "") + "]";
        }
    }

    private record Index(boolean javaPatterns, Map<String, Identifier> patterns, Map<String, Identifier> palettes) {
    }
}
