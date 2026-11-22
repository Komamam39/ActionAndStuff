package com.ans.client.mob.misc;

import com.ans.client.molang.Node;
import com.ans.client.bedrock.FrameResult;
import com.ans.client.bedrock.RenderLayer;
import com.ans.client.equipment.EquipmentLayer;
import com.ans.client.molang.MolangContext;
import com.ans.client.molang.MolangQueries;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.SulfurCubeRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SulfurCubeBlockLayer<E extends LivingEntity> extends GeoRenderLayer<MobAnimatable, E, LivingEntityRenderState> {
    private static final DataTicket<BlockModelRenderState> BLOCK = DataTicket.create("ans_sulfur_cube_block", BlockModelRenderState.class);
    private static final String BONE = "rightitem";
    private static final String INNER = "inner";
    private @Nullable BlockModelResolver blocks;

    private SulfurCubeBlockLayer(GeoRenderer<MobAnimatable, E, LivingEntityRenderState> renderer) {
        super(renderer);
    }

    public static void install() {
        CompileReplacedEntityRenderLayersEvent.EVENT.register(event -> {
            if (event.getRenderer() instanceof ReplacedMobRenderer<?> r && r.getAnimatable().mob().type() == EntityTypes.SULFUR_CUBE) {
                ((ReplacedMobRenderer) r).withRenderLayer(new SulfurCubeBlockLayer(r));
            }
        });
        MolangQueries.registerFor(
            EntityTypes.SULFUR_CUBE,
            "is_item_name_any",
            (Node[] a) -> new MolangQueries.Bound() {
                @Override
                public double eval(MolangContext c) {
                    if (c.entity != null && a.length != 0) {
                        String slot = MolangQueries.string(a[0], c);
                        ItemStack s = !slot.equals("slot.weapon.mainhand") && !slot.equals("slot.armor.body")
                            ? ItemStack.EMPTY
                            : c.entity.getItemBySlot(EquipmentSlot.BODY);
                        String id = s.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(s.getItem()).toString();

                        for (int i = 1; i < a.length; i++) {
                            if (a[i].value(c) instanceof String n) {
                                if (!n.isEmpty() && !n.equals("minecraft:air")) {
                                    if (!s.isEmpty() && (n.contains(":") ? n : "minecraft:" + n).equals(id)) {
                                        return 1.0;
                                    }
                                } else if (s.isEmpty()) {
                                    return 1.0;
                                }
                            }
                        }

                        return 0.0;
                    } else {
                        return 0.0;
                    }
                }
            }
        );
    }

    public void addRenderData(MobAnimatable animatable, @Nullable E entity, LivingEntityRenderState renderState, float partialTick) {
        ItemStack body = entity instanceof SulfurCube cube ? cube.getItemBySlot(EquipmentSlot.BODY) : ItemStack.EMPTY;
        if (!body.isEmpty() && Block.byItem(body.getItem()) != Blocks.AIR) {
            BlockState state = body.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY)
                .apply(Block.byItem(body.getItem()).defaultBlockState());
            if (this.blocks == null) {
                this.blocks = new BlockModelResolver(Minecraft.getInstance().getModelManager());
            }

            BlockModelRenderState block = new BlockModelRenderState();
            this.blocks.update(block, state, SulfurCubeRenderer.BLOCK_DISPLAY_CONTEXT);
            renderState.addGeckolibData(BLOCK, block);
        } else {
            if (renderState.hasGeckolibData(BLOCK)) {
                renderState.addGeckolibData(BLOCK, new BlockModelRenderState());
            }
        }
    }

    @Override
    public void submitRenderTask(RenderPassInfo<LivingEntityRenderState> info, SubmitNodeCollector collector) {
        BlockModelRenderState block = info.getGeckolibData(BLOCK);
        FrameResult frame = info.getGeckolibData(FrameResult.TICKET);
        if (block != null && !block.isEmpty() && frame != null && !((LivingEntityRenderState)info.renderState()).isInvisible) {
            GeoBone bone = null;

            for (RenderLayer layer : frame.layers()) {
                BakedGeoModel model = this.getGeoModel().getBakedModel(layer.model());
                if (!model.isMissingno()) {
                    if (model.getBone("inner").isPresent() && layer.visible("inner")) {
                        return;
                    }

                    if (bone == null) {
                        bone = model.getBone("rightitem").orElse(null);
                    }
                }
            }

            if (bone != null) {
                PoseStack ps = info.poseStack();
                ps.pushPose();
                ps.last().set(info.getModelRenderMatrixPose());
                EquipmentLayer.toBone(ps, bone, frame.pose());
                ps.scale(0.999F, 0.999F, 0.999F);
                ps.translate(-0.5F, 0.001F, -0.5F);
                block.submit(ps, collector, info.packedLight(), OverlayTexture.NO_OVERLAY, ((LivingEntityRenderState)info.renderState()).outlineColor);
                ps.popPose();
            }
        }
    }
}
