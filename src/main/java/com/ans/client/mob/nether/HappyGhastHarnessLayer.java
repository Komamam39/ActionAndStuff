package com.ans.client.mob.nether;

import com.ans.client.bedrock.FrameResult;
import com.ans.client.bedrock.RenderLayer;
import com.ans.client.equipment.EquipmentLayer;
import com.ans.client.render.MobAnimatable;
import com.ans.client.render.ReplacedMobRenderer;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.event.replacedentity.CompileReplacedEntityRenderLayersEvent;
import com.geckolib.event.replacedentity.CompileReplacedEntityRenderLayersEvent.Listener;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.animal.ghast.HappyGhastHarnessModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo.LayerType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.Nullable;

public final class HappyGhastHarnessLayer<E extends LivingEntity> extends GeoRenderLayer<MobAnimatable, E, LivingEntityRenderState> {
    private static final DataTicket<HappyGhastHarnessLayer.Data> DATA = DataTicket.create("ans_happy_ghast_harness", HappyGhastHarnessLayer.Data.class);
    private static final String BONE = "body";
    private static final float SQUEEZED = 0.93529F;
    private static final float NORMAL = 0.99375F;
    private static final float HARNESS_CENTRE_Y = -8.048004F;
    private final ReplacedMobRenderer<?> owner;
    private @Nullable HappyGhastHarnessModel model;

    private HappyGhastHarnessLayer(GeoRenderer<MobAnimatable, E, LivingEntityRenderState> renderer, ReplacedMobRenderer<?> owner) {
        super(renderer);
        this.owner = owner;
    }

    public static void install() {
        CompileReplacedEntityRenderLayersEvent.EVENT.register(event -> {
            if (event.getRenderer() instanceof ReplacedMobRenderer<?> r && r.getAnimatable().mob().type() == EntityTypes.HAPPY_GHAST) {
                ((ReplacedMobRenderer) r).withRenderLayer(new HappyGhastHarnessLayer(r, r));
            }
        });
    }

    public void addRenderData(MobAnimatable animatable, @Nullable E entity, LivingEntityRenderState renderState, float partialTick) {
        ItemStack body = entity != null && !entity.isBaby() ? entity.getItemBySlot(EquipmentSlot.BODY) : ItemStack.EMPTY;
        Equippable equippable = body.get(DataComponents.EQUIPPABLE);
        if (!body.isEmpty() && equippable != null && !equippable.assetId().isEmpty() && !hasName(entity, "saddleless", "Saddleless")) {
            HappyGhastRenderState state = new HappyGhastRenderState();
            state.bodyItem = body.copy();
            state.isRidden = entity.isVehicle();
            boolean fakeBaby = hasName(entity, "baby", "Baby", "ghastlet", "Ghastlet", "smiley", "Smiley");
            float squeeze = fakeBaby ? 0.99375F : 0.93529F;
            renderState.addGeckolibData(DATA, new HappyGhastHarnessLayer.Data(state.bodyItem, state, squeeze));
        } else {
            if (renderState.hasGeckolibData(DATA)) {
                renderState.addGeckolibData(DATA, new HappyGhastHarnessLayer.Data(ItemStack.EMPTY, new HappyGhastRenderState(), 1.0F));
            }
        }
    }

    private static boolean hasName(@Nullable LivingEntity e, String... names) {
        if (e != null && e.hasCustomName()) {
            String n = e.getCustomName().getString();

            for (String s : names) {
                if (s.equals(n)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public void submitRenderTask(RenderPassInfo<LivingEntityRenderState> info, SubmitNodeCollector collector) {
        HappyGhastHarnessLayer.Data data = info.getGeckolibData(DATA);
        FrameResult frame = info.getGeckolibData(FrameResult.TICKET);
        if (data != null && !data.stack.isEmpty() && frame != null) {
            Equippable equippable = data.stack.get(DataComponents.EQUIPPABLE);
            EquipmentLayerRenderer equipment = this.equipmentRenderer();
            if (equippable != null && !equippable.assetId().isEmpty() && equipment != null) {
                GeoBone bone = null;

                for (RenderLayer layer : frame.layers()) {
                    BakedGeoModel model = this.getGeoModel().getBakedModel(layer.model());
                    if (!model.isMissingno()) {
                        Optional<GeoBone> b = model.getBone("body");
                        if (b.isPresent()) {
                            bone = b.get();
                            break;
                        }
                    }
                }

                if (bone != null) {
                    if (this.model == null) {
                        this.model = new HappyGhastHarnessModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.HAPPY_GHAST_HARNESS));
                    }

                    PoseStack ps = info.poseStack();
                    ps.pushPose();
                    ps.last().set(info.getModelRenderMatrixPose());
                    EquipmentLayer.toBone(ps, bone, frame.pose());
                    float s = 1.0F / data.squeeze;
                    ps.scale(s, s, s);
                    ps.scale(-1.0F, -1.0F, 1.0F);
                    ps.translate(0.0F, 0.50300026F, 0.0F);
                    equipment.renderLayers(
                        LayerType.HAPPY_GHAST_BODY,
                        equippable.assetId().get(),
                        this.model,
                        data.state,
                        data.stack,
                        ps,
                        collector,
                        info.packedLight(),
                        ((LivingEntityRenderState)info.renderState()).outlineColor
                    );
                    ps.popPose();
                }
            }
        }
    }

    private @Nullable EquipmentLayerRenderer equipmentRenderer() {
        for (GeoRenderLayer<?, ?, ?> layer : this.owner.getRenderLayers()) {
            if (layer instanceof EquipmentLayer<?> e) {
                return e.equipmentRenderer();
            }
        }

        return null;
    }

    private record Data(ItemStack stack, HappyGhastRenderState state, float squeeze) {
    }
}
