package com.ans.client.attachable;

import com.ans.Ans;
import com.ans.client.bedrock.AnimationLookup;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public final class AtlasSources {
    private static final Identifier FILE = Ans.id("attachable/_atlas_sources.json");
    public static volatile boolean enabled = !"false".equalsIgnoreCase(System.getProperty("ans.atlasSources"));
    private static Map<Identifier, List<AtlasSources.Cell>> atlases = Map.of();
    private static int cellSize = 16;
    private static int seenGeneration = -1;

    private AtlasSources() {
    }

    public static boolean has(Identifier texture) {
        return !enabled ? false : current(Minecraft.getInstance().getResourceManager()).containsKey(texture);
    }

    private static synchronized Map<Identifier, List<AtlasSources.Cell>> current(ResourceManager resources) {
        int generation = AnimationLookup.generation();
        if (generation != seenGeneration) {
            seenGeneration = generation;
            atlases = read(resources);
        }

        return atlases;
    }

    private static Map<Identifier, List<AtlasSources.Cell>> read(ResourceManager resources) {
        Map<Identifier, List<AtlasSources.Cell>> out = new HashMap<>();
        Optional<Resource> res = resources.getResource(FILE);
        if (res.isEmpty()) {
            return out;
        }

        try (Reader reader = res.get().openAsReader()) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("cell")) {
                cellSize = root.get("cell").getAsInt();
            }

            for (Entry<String, JsonElement> e : root.getAsJsonObject("atlases").entrySet()) {
                Identifier atlas = Identifier.tryParse(e.getKey());
                if (atlas != null) {
                    List<AtlasSources.Cell> cells = new ArrayList<>();

                    for (JsonElement c : e.getValue().getAsJsonArray()) {
                        JsonArray a = c.getAsJsonArray();
                        Identifier src = Identifier.tryParse(a.get(2).getAsString());
                        if (src != null) {
                            cells.add(new AtlasSources.Cell(a.get(0).getAsInt(), a.get(1).getAsInt(), src));
                        }
                    }

                    out.put(atlas, List.copyOf(cells));
                }
            }
        } catch (Exception ex) {
            Ans.LOGGER.error("[ans] Could not read {}", FILE, ex);
        }

        return out;
    }

    public static NativeImage patch(ResourceManager resources, Identifier id, NativeImage image) {
        if (!enabled) {
            return image;
        }

        List<AtlasSources.Cell> cells = current(resources).get(id);
        if (cells == null) {
            return image;
        }

        int n = cellSize;

        for (AtlasSources.Cell c : cells) {
            Optional<Resource> res = resources.getResource(c.source());
            if (!res.isEmpty()) {
                try (
                    InputStream in = res.get().open();
                    NativeImage src = NativeImage.read(in);
                ) {
                    int w = src.getWidth();
                    int h = Math.min(src.getHeight(), w);
                    if (w > 0 && h > 0) {
                        for (int y = 0; y < n; y++) {
                            for (int x = 0; x < n; x++) {
                                int dx = c.x() + x;
                                int dy = c.y() + y;
                                if (dx < image.getWidth() && dy < image.getHeight()) {
                                    image.setPixel(dx, dy, src.getPixel(x * w / n, y * h / n));
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    Ans.LOGGER.warn("[ans] atlas cell source {} unreadable: {}", c.source(), ex.toString());
                }
            }
        }

        return image;
    }

    private record Cell(int x, int y, Identifier source) {
    }
}
