package com.ans.client.mob.groups;

import com.ans.client.equipment.MobEquipment;
import com.ans.client.mob.AnimatedMob;
import java.util.List;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityTypes;

public final class UndeadMobs {
    private static final List<String> PROFESSIONS = List.of(
        "none",
        "farmer",
        "fisherman",
        "shepherd",
        "fletcher",
        "librarian",
        "cartographer",
        "cleric",
        "armorer",
        "weaponsmith",
        "toolsmith",
        "butcher",
        "leatherworker",
        "mason",
        "nitwit"
    );
    private static final List<String> TYPES = List.of("plains", "desert", "jungle", "savanna", "snow", "swamp", "taiga");

    private UndeadMobs() {
    }

    public static void register() {
        MobEquipment.humanoid(EntityTypes.ZOMBIE, ModelLayers.ZOMBIE_ARMOR, ModelLayers.ZOMBIE_BABY_ARMOR);
        MobEquipment.humanoid(EntityTypes.HUSK, ModelLayers.HUSK_ARMOR, ModelLayers.HUSK_BABY_ARMOR);
        MobEquipment.humanoid(EntityTypes.DROWNED, ModelLayers.DROWNED_ARMOR, ModelLayers.DROWNED_BABY_ARMOR);
        MobEquipment.humanoid(EntityTypes.ZOMBIE_VILLAGER, ModelLayers.ZOMBIE_VILLAGER_ARMOR, ModelLayers.ZOMBIE_VILLAGER_BABY_ARMOR);
        AnimatedMob.builder(EntityTypes.ZOMBIE).register();
        AnimatedMob.builder(EntityTypes.HUSK).register();
        AnimatedMob.builder(EntityTypes.DROWNED).register();
        AnimatedMob.builder(EntityTypes.ZOMBIE_VILLAGER, "zombie_villager_v2")
            .query("variant", e -> index(PROFESSIONS, e.getVillagerData().profession()))
            .query("mark_variant", e -> index(TYPES, e.getVillagerData().type()))
            .register();
        MobEquipment.humanoid(EntityTypes.SKELETON, ModelLayers.SKELETON_ARMOR, null);
        MobEquipment.humanoid(EntityTypes.STRAY, ModelLayers.STRAY_ARMOR, null);
        MobEquipment.humanoid(EntityTypes.BOGGED, ModelLayers.BOGGED_ARMOR, null);
        AnimatedMob.builder(EntityTypes.SKELETON).register();
        AnimatedMob.builder(EntityTypes.STRAY).register();
        AnimatedMob.builder(EntityTypes.BOGGED).register();
        MobEquipment.humanoid(EntityTypes.WITHER_SKELETON, ModelLayers.WITHER_SKELETON_ARMOR, null);
        AnimatedMob.builder(EntityTypes.WITHER_SKELETON).actorScale(e -> 1.2).register();
    }

    private static int index(List<String> order, Holder<?> holder) {
        int i = holder.unwrapKey().map(k -> order.indexOf(k.identifier().getPath())).orElse(0);
        return Math.max(0, i);
    }
}
