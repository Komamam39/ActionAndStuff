package com.ans.client.mob;

import com.ans.client.mob.groups.AquaticMobs;
import com.ans.client.mob.groups.CritterMobs;
import com.ans.client.mob.groups.MiscMobs;
import com.ans.client.mob.groups.MonsterMobs;
import com.ans.client.mob.groups.NetherMobs;
import com.ans.client.mob.groups.ObjectMobs;
import com.ans.client.mob.groups.PetMobs;
import com.ans.client.mob.groups.PlayerMobs;
import com.ans.client.mob.groups.UndeadMobs;
import com.ans.client.mob.groups.VillageMobs;
import net.minecraft.world.entity.EntityTypes;

public final class AnsMobs {
    private AnsMobs() {
    }

    public static void registerAll() {
        AnimatedMob.builder(EntityTypes.COW).register();
        AnimatedMob.builder(EntityTypes.PIG).register();
        UndeadMobs.register();
        MonsterMobs.register();
        CritterMobs.register();
        VillageMobs.register();
        NetherMobs.register();
        MiscMobs.register();
        AquaticMobs.register();
        PetMobs.register();
        ObjectMobs.register();
        PlayerMobs.register();
    }
}
