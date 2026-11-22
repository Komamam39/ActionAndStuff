package com.ans.client.mob.pets;

import com.ans.Ans;
import com.ans.client.bedrock.FrameResult;
import com.ans.client.bedrock.RenderLayer;
import com.ans.client.equipment.EquipmentLayer;
import com.ans.client.render.MobAnimatable;
import com.ans.client.render.ReplacedMobRenderer;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.cache.model.GeoQuad;
import com.geckolib.cache.model.GeoVertex;
import com.geckolib.cache.model.cuboid.CuboidGeoBone;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.event.replacedentity.CompileReplacedEntityRenderLayersEvent;
import com.geckolib.event.replacedentity.CompileReplacedEntityRenderLayersEvent.Listener;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo.LayerType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Crackiness.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public final class WolfArmorLayer<E extends LivingEntity> extends GeoRenderLayer<MobAnimatable, E, GeoRenderState> {
    private static final DataTicket<ItemStack> ARMOR = DataTicket.create("ans_wolf_armor", ItemStack.class);
    private static final String[][] SEGMENTS = new String[][]{
        {"head", "head"},
        {"body", "centroid"},
        {"upper_body", "upperbody"},
        {"tail", "tailextra"},
        {"right_hind_leg", "leg0bottom"},
        {"left_hind_leg", "leg1bottom"},
        {"right_front_leg", "legabottom"},
        {"left_front_leg", "legbbottom"}
    };
    private static final Map<Level, Identifier> CRACKS = Map.of(
        Level.LOW,
        Identifier.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_low.png"),
        Level.MEDIUM,
        Identifier.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_medium.png"),
        Level.HIGH,
        Identifier.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_high.png")
    );
    private @Nullable List<WolfArmorLayer.Segment> segments;
    private @Nullable EquipmentLayerRenderer equipment;
    private boolean failed;

    private WolfArmorLayer(GeoRenderer<MobAnimatable, E, GeoRenderState> var1) {
        super(var1);
    }

    public static void install() {
        CompileReplacedEntityRenderLayersEvent.EVENT.register(var0 -> {
            if (var0.getRenderer() instanceof ReplacedMobRenderer<?> var1 && var1.getAnimatable().mob().type() == EntityTypes.WOLF) {
                ((ReplacedMobRenderer) var1).withRenderLayer(new WolfArmorLayer(var1));
            }
        });
    }

    public void addRenderData(MobAnimatable var1, @Nullable E var2, GeoRenderState var3, float var4) {
        ItemStack var5 = var2 != null && !var2.isBaby() ? var2.getItemBySlot(EquipmentSlot.BODY) : ItemStack.EMPTY;
        if (asset(var5).isEmpty()) {
            var5 = ItemStack.EMPTY;
        }

        if (!var5.isEmpty() || var3.hasGeckolibData(ARMOR)) {
            var3.addGeckolibData(ARMOR, var5.isEmpty() ? ItemStack.EMPTY : var5.copy());
        }
    }

    private static Optional<ResourceKey<EquipmentAsset>> asset(ItemStack var0) {
        Equippable var1 = var0.isEmpty() ? null : var0.get(DataComponents.EQUIPPABLE);
        return var1 != null && var1.slot() == EquipmentSlot.BODY ? var1.assetId() : Optional.empty();
    }

    @Override
    public void submitRenderTask(RenderPassInfo<GeoRenderState> var1, SubmitNodeCollector var2) {
        ItemStack var3 = var1.getGeckolibData(ARMOR);
        FrameResult var4 = var1.getGeckolibData(FrameResult.TICKET);
        if (var3 != null && !var3.isEmpty() && var4 != null && this.init()) {
            Optional var5 = asset(var3);
            if (!var5.isEmpty()) {
                Identifier var6 = CRACKS.get(Crackiness.WOLF_ARMOR.byDamage(var3));
                PoseStack var7 = var1.poseStack();
                int var8 = var1.packedLight();
                int var9 = ((LivingEntityRenderState)var1.renderState()).outlineColor;

                for (WolfArmorLayer.Segment var11 : this.segments) {
                    GeoBone var12 = this.bone(var4, var11.bone);
                    if (var12 != null) {
                        var7.pushPose();
                        var7.last().set(var1.getModelRenderMatrixPose());
                        EquipmentLayer.toBone(var7, var12, var4.pose());
                        var7.scale(-1.0F, -1.0F, 1.0F);
                        fit(var7, var12, var11.box);
                        this.equipment
                            .renderLayers(
                                LayerType.WOLF_BODY,
                                (ResourceKey<EquipmentAsset>)var5.get(),
                                var11.model,
                                (LivingEntityRenderState)var1.renderState(),
                                var3,
                                var7,
                                var2,
                                var8,
                                var9
                            );
                        if (var6 != null) {
                            var2.submitModel(
                                var11.model,
                                (LivingEntityRenderState)var1.renderState(),
                                var7,
                                RenderTypes.wolfArmorCracks(var6),
                                var8,
                                OverlayTexture.NO_OVERLAY,
                                var9
                            );
                        }

                        var7.popPose();
                    }
                }
            }
        }
    }

    private boolean init() {
        if (this.segments != null && this.equipment != null) {
            return true;
        }

        if (this.failed) {
            return false;
        }

        try {
            Minecraft var1 = Minecraft.getInstance();
            ModelPart var2 = var1.getEntityModels().bakeLayer(ModelLayers.WOLF_ARMOR);
            ArrayList var3 = new ArrayList();

            for (String[] var7 : SEGMENTS) {
                if (var2.hasChild(var7[0])) {
                    ModelPart var8 = var2.getChild(var7[0]);
                    var3.add(new WolfArmorLayer.Segment(var7[1], new WolfArmorLayer.PartModel(var8), box(var8)));
                }
            }

            this.equipment = new EquipmentLayerRenderer(equipmentAssets(var1.getEntityRenderDispatcher()), var1.getPalettedTextureManager());
            this.segments = List.copyOf(var3);
            return true;
        } catch (ReflectiveOperationException | RuntimeException var9) {
            this.failed = true;
            Ans.LOGGER.error("[ans] wolf armour layer unavailable (logged once)", var9);
            return false;
        }
    }

    private static EquipmentAssetManager equipmentAssets(EntityRenderDispatcher var0) throws ReflectiveOperationException {
        for (Field var4 : EntityRenderDispatcher.class.getDeclaredFields()) {
            if (var4.getType() == EquipmentAssetManager.class) {
                var4.setAccessible(true);
                return (EquipmentAssetManager)var4.get(var0);
            }
        }

        throw new NoSuchFieldException("EntityRenderDispatcher equipment assets");
    }

    private static float[] box(ModelPart var0) {
        WolfArmorLayer.PartModel.zero(var0);
        float[] var1 = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
        boolean[] var2 = new boolean[]{false};
        var0.visit(
            new PoseStack(),
            (var2x, var3, var4, var5) -> {
                if (!var2[0]) {
                    var2[0] = true;

                    for (int var6 = 0; var6 < 8; var6++) {
                        Vector4f var7 = new Vector4f(
                                (var6 & 1) == 0 ? var5.minX : var5.maxX,
                                (var6 & 2) == 0 ? var5.minY : var5.maxY,
                                (var6 & 4) == 0 ? var5.minZ : var5.maxZ,
                                16.0F
                            )
                            .div(16.0F)
                            .mul(var2x.pose());
                        var1[0] = Math.min(var1[0], var7.x * 16.0F);
                        var1[1] = Math.min(var1[1], var7.y * 16.0F);
                        var1[2] = Math.min(var1[2], var7.z * 16.0F);
                        var1[3] = Math.max(var1[3], var7.x * 16.0F);
                        var1[4] = Math.max(var1[4], var7.y * 16.0F);
                        var1[5] = Math.max(var1[5], var7.z * 16.0F);
                    }
                }
            }
        );
        return var1;
    }

    private static void fit(PoseStack var0, GeoBone var1, float[] var2) {
        if (var1 instanceof CuboidGeoBone var3 && var3.cubes.length != 0) {
            float[] var4 = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};

            for (GeoQuad var8 : var3.cubes[0].quads()) {
                for (GeoVertex var12 : var8.vertices()) {
                    float var13 = -(var12.posX() * 16.0F - var1.pivotX());
                    float var14 = -(var12.posY() * 16.0F - var1.pivotY());
                    float var15 = var12.posZ() * 16.0F - var1.pivotZ();
                    var4[0] = Math.min(var4[0], var13);
                    var4[1] = Math.min(var4[1], var14);
                    var4[2] = Math.min(var4[2], var15);
                    var4[3] = Math.max(var4[3], var13);
                    var4[4] = Math.max(var4[4], var14);
                    var4[5] = Math.max(var4[5], var15);
                }
            }

            float var16 = (var4[3] - var4[0]) / (var2[3] - var2[0]);
            float var17 = (var4[4] - var4[1]) / (var2[4] - var2[1]);
            float var18 = (var4[5] - var4[2]) / (var2[5] - var2[2]);
            if (var16 > 0.0F && var17 > 0.0F && var18 > 0.0F && Float.isFinite(var16 + var17 + var18)) {
                var0.translate((var4[0] + var4[3]) / 32.0F, (var4[1] + var4[4]) / 32.0F, (var4[2] + var4[5]) / 32.0F);
                var0.scale(var16, var17, var18);
                var0.translate(-(var2[0] + var2[3]) / 32.0F, -(var2[1] + var2[4]) / 32.0F, -(var2[2] + var2[5]) / 32.0F);
            }
        }
    }

    private @Nullable GeoBone bone(FrameResult var1, String var2) {
        for (RenderLayer var4 : var1.layers()) {
            BakedGeoModel var5 = this.getGeoModel().getBakedModel(var4.model());
            if (!var5.isMissingno()) {
                Optional var6 = var5.getBone(var2);
                if (var6.isPresent()) {
                    return (GeoBone)var6.get();
                }
            }
        }

        return null;
    }

    private static final class PartModel extends Model<Object> {
        PartModel(ModelPart var1) {
            super(var1, RenderTypes::armorCutoutNoCull);
        }

        static void zero(ModelPart var0) {
            var0.x = var0.y = var0.z = 0.0F;
            var0.xRot = var0.yRot = var0.zRot = 0.0F;
        }

        @Override
        public void setupAnim(Object var1) {
            this.resetPose();
            zero(this.root);
            this.root.visible = true;
        }
    }

    private record Segment(String bone, WolfArmorLayer.PartModel model, float[] box) {
    }
}
