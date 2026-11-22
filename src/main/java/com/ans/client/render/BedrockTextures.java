package com.ans.client.render;

import com.ans.Ans;
import com.ans.client.attachable.AtlasSources;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntUnaryOperator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public final class BedrockTextures {
    private static final Map<Identifier, BedrockTextures.Recipe> COMPOSITES = new ConcurrentHashMap<>();
    private static final Map<Identifier, Identifier> RESOLVED = new ConcurrentHashMap<>();
    private static final Map<Identifier, BedrockTextures.Split> SPLITS = new ConcurrentHashMap<>();
    private static final Map<Identifier, BedrockTextures.Split> EMISSIVE_SPLITS = new ConcurrentHashMap<>();
    private static final Map<Identifier, BedrockTextures.Split> EMISSIVE_OPAQUE_SPLITS = new ConcurrentHashMap<>();
    private static final Map<Identifier, Identifier> WHITE_MASKS = new ConcurrentHashMap<>();

    public static BedrockTextures.Split emissiveOpaqueSplit(Identifier texture) {
        return EMISSIVE_OPAQUE_SPLITS.computeIfAbsent(
            texture,
            t -> new BedrockTextures.Split(
                register(derivedId(t, "olit"), t, BedrockTextures.Mode.OPAQUE_LIT), register(derivedId(t, "oglow"), t, BedrockTextures.Mode.OPAQUE_GLOW)
            )
        );
    }

    public static Identifier whiteMask(Identifier texture) {
        return WHITE_MASKS.computeIfAbsent(texture, t -> register(derivedId(t, "white"), t, BedrockTextures.Mode.WHITE));
    }

    private BedrockTextures() {
    }

    public static BedrockTextures.Part part(Identifier texture, int dstX, int dstY, boolean tinted) {
        return new BedrockTextures.Part(texture, 0, 0, -1, -1, dstX, dstY, tinted);
    }

    public static BedrockTextures.Part crop(Identifier texture, int srcX, int srcY, int width, int height, int dstX, int dstY, boolean tinted) {
        return new BedrockTextures.Part(texture, srcX, srcY, width, height, dstX, dstY, tinted);
    }

    public static void composite(Identifier bedrockTexture, int width, int height, BedrockTextures.Part... parts) {
        COMPOSITES.put(bedrockTexture, new BedrockTextures.Recipe(width, height, List.of(parts)));
    }

    public static Identifier resolve(Identifier texture) {
        if (!COMPOSITES.containsKey(texture)) {
            return texture.getPath().startsWith("textures/attachable/") && AtlasSources.has(texture)
                ? RESOLVED.computeIfAbsent(texture, t -> register(derivedId(t, "atlas"), t, BedrockTextures.Mode.RAW))
                : texture;
        } else {
            return RESOLVED.computeIfAbsent(texture, t -> register(derivedId(t, "all"), t, BedrockTextures.Mode.ALL));
        }
    }

    public static BedrockTextures.Split colorMask(Identifier texture) {
        return SPLITS.computeIfAbsent(
            texture,
            t -> new BedrockTextures.Split(
                register(derivedId(t, "plain"), t, BedrockTextures.Mode.PLAIN), register(derivedId(t, "tinted"), t, BedrockTextures.Mode.TINTED)
            )
        );
    }

    public static BedrockTextures.Split emissiveSplit(Identifier texture) {
        return EMISSIVE_SPLITS.computeIfAbsent(
            texture,
            t -> new BedrockTextures.Split(
                register(derivedId(t, "lit"), t, BedrockTextures.Mode.LIT), register(derivedId(t, "glow"), t, BedrockTextures.Mode.GLOW)
            )
        );
    }

    private static Identifier derivedId(Identifier source, String suffix) {
        String path = source.getPath().replaceAll("\\.png$", "");
        return Ans.id("derived/" + source.getNamespace() + "/" + path + "_" + suffix + ".png");
    }

    private static Identifier register(Identifier id, Identifier source, BedrockTextures.Mode mode) {
        Minecraft.getInstance().getTextureManager().registerAndLoad(id, new BedrockTextures.Derived(id, source, mode));
        return id;
    }

    private static NativeImage load(ResourceManager resources, Identifier id) throws IOException {
        return resources.getResource(id).isEmpty()
                && Minecraft.getInstance().getTextureManager().getTexture(id) instanceof DynamicTexture d
                && d.getPixels() != null
            ? d.getPixels().mappedCopy(IntUnaryOperator.identity())
            : AtlasSources.patch(resources, id, TextureContents.load(resources, id).image());
    }

    private static NativeImage bedrockImage(ResourceManager resources, Identifier texture) throws IOException {
        BedrockTextures.Recipe recipe = COMPOSITES.get(texture);
        if (recipe == null) {
            return load(resources, texture);
        }

        NativeImage out = new NativeImage(recipe.width(), recipe.height(), true);
        out.fillRect(0, 0, recipe.width(), recipe.height(), 0);

        for (BedrockTextures.Part p : recipe.parts()) {
            try (NativeImage src = load(resources, p.texture())) {
                int w = p.width() < 0 ? src.getWidth() : p.width();
                int h = p.height() < 0 ? src.getHeight() : p.height();
                int alpha = p.tinted() ? 255 : 3;

                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        int sx = p.srcX() + x;
                        int sy = p.srcY() + y;
                        int dx = p.dstX() + x;
                        int dy = p.dstY() + y;
                        if (sx < src.getWidth() && sy < src.getHeight() && dx < out.getWidth() && dy < out.getHeight()) {
                            int c = src.getPixel(sx, sy);
                            if (c >>> 24 >= 128) {
                                out.setPixel(dx, dy, alpha << 24 | c & 16777215);
                            }
                        }
                    }
                }
            }
        }

        return out;
    }

    private static final class Derived extends ReloadableTexture {
        private final Identifier source;
        private final BedrockTextures.Mode mode;

        Derived(Identifier id, Identifier source, BedrockTextures.Mode mode) {
            super(id);
            this.source = source;
            this.mode = mode;
        }

        @Override
        public TextureContents loadContents(ResourceManager resources) throws IOException {
            NativeImage image = BedrockTextures.bedrockImage(resources, this.source);
            if (this.mode == BedrockTextures.Mode.RAW) {
                return new TextureContents(image, null);
            }

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int c = image.getPixel(x, y);
                    int a = c >>> 24;
                    if (this.mode == BedrockTextures.Mode.WHITE) {
                        image.setPixel(x, y, a > 0 ? a << 24 | 16777215 : 0);
                    } else if (this.mode == BedrockTextures.Mode.GLOW) {
                        image.setPixel(x, y, a > 0 && a < 255 ? 255 - a << 24 | c & 16777215 : 0);
                    } else if (this.mode == BedrockTextures.Mode.OPAQUE_GLOW) {
                        image.setPixel(x, y, a < 255 ? 255 - a << 24 | c & 16777215 : 0);
                    } else {
                        boolean keep = switch (this.mode) {
                            case ALL, LIT -> a > 0;
                            case PLAIN -> {
                                if (a > 0 && a < 128) {
                                    yield true;
                                }

                                yield false;
                            }
                            case TINTED -> a >= 128;
                            case GLOW, WHITE, OPAQUE_GLOW -> false;
                            case OPAQUE_LIT, RAW -> true;
                        };
                        image.setPixel(x, y, keep ? 0xFF000000 | c & 16777215 : 0);
                    }
                }
            }

            return new TextureContents(image, null);
        }

        @Override
        public String toString() {
            return "BedrockTextures.Derived[" + this.source + " " + this.mode.name().toLowerCase(Locale.ROOT) + "]";
        }
    }

    private enum Mode {
        ALL,
        PLAIN,
        TINTED,
        LIT,
        GLOW,
        WHITE,
        OPAQUE_LIT,
        OPAQUE_GLOW,
        RAW;
    }

    public record Part(Identifier texture, int srcX, int srcY, int width, int height, int dstX, int dstY, boolean tinted) {
    }

    private record Recipe(int width, int height, List<BedrockTextures.Part> parts) {
    }

    public record Split(Identifier plain, Identifier tinted) {
    }
}
