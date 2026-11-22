package com.ans.perfutil;

import com.ans.client.bedrock.AnimationLookup;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;

public final class TrimCompat {
    private static final Map<Identifier, Identifier> COMPOSED = new HashMap<>();
    private static final Map<Identifier, Boolean> EXISTS = new HashMap<>();
    private static int seen = -1;
    private static Constructor<?> ctor;

    private TrimCompat() {
    }

    public static synchronized Identifier texture(ItemStack var0, EquipmentSlot var1, Identifier var2) {
        ArmorTrim var3 = var0.get(DataComponents.TRIM);
        if (var3 == null) {
            return null;
        }

        int var4 = AnimationLookup.generation();
        if (var4 != seen) {
            seen = var4;
            EXISTS.clear();
        }

        boolean var5 = var1 == EquipmentSlot.LEGS;
        Identifier var6 = var3.pattern().value().assetId();
        String var7 = var6.getPath() + (var5 ? "_leggings" : "");
        Identifier var8 = Identifier.fromNamespaceAndPath("ans", "textures/attachable/trim_patterns/" + var7 + ".png");
        if (!exists(var8)) {
            var8 = var6.withPath("textures/trims/entity/" + (var5 ? "humanoid_leggings/" : "humanoid/") + var6.getPath() + ".png");
            if (!exists(var8)) {
                return null;
            }
        }

        String var9 = var3.material().value().paletteId().getPath();
        var9 = var9.substring(var9.lastIndexOf(47) + 1);
        String var10 = BuiltInRegistries.ITEM.getKey(var0.getItem()).getPath();
        String var11 = var10.startsWith("golden_") ? "gold" : var10.substring(0, Math.max(0, var10.indexOf(95)));
        Identifier var12 = null;
        if (var9.equals(var11)) {
            var12 = palette(var9 + "_darker");
        }

        if (var12 == null) {
            var12 = palette(var9);
        }

        if (var12 == null) {
            return null;
        }

        Identifier var13 = Identifier.parse(
            "ans:derived/trim/"
                + var8.getNamespace()
                + "/"
                + strip(var8.getPath())
                + "/"
                + var12.getNamespace()
                + "/"
                + strip(var12.getPath())
                + (var2 != null ? "/on/" + var2.getNamespace() + "/" + strip(var2.getPath()) : "")
                + ".png"
        );
        Identifier var14 = var8;
        Identifier var15 = var12;
        Identifier var16 = COMPOSED.get(var13);
        if (var16 != null) {
            return var16;
        }

        try {
            if (ctor == null) {
                ctor = Class.forName("com.ans.client.attachable.TrimTextures$Composed")
                    .getDeclaredConstructor(Identifier.class, Identifier.class, Identifier.class, Identifier.class);
                ctor.setAccessible(true);
            }

            Minecraft.getInstance().getTextureManager().registerAndLoad(var13, (ReloadableTexture)ctor.newInstance(var13, var14, var15, var2));
            COMPOSED.put(var13, var13);
            return var13;
        } catch (ReflectiveOperationException | RuntimeException var18) {
            COMPOSED.put(var13, null);
            return null;
        }
    }

    private static Identifier palette(String var0) {
        Identifier var1 = Identifier.fromNamespaceAndPath("ans", "textures/attachable/trims_color_palettes_" + var0 + ".png");
        if (exists(var1)) {
            return var1;
        }

        Identifier var2 = Identifier.withDefaultNamespace("textures/palettes/trim/" + var0 + ".png");
        return exists(var2) ? var2 : null;
    }

    private static String strip(String var0) {
        return var0.replaceAll("\\.png$", "");
    }

    private static boolean exists(Identifier var0) {
        return EXISTS.computeIfAbsent(var0, var0x -> Minecraft.getInstance().getResourceManager().getResource(var0x).isPresent());
    }
}
