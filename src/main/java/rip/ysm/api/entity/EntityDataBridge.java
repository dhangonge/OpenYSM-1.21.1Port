package rip.ysm.api.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public final class EntityDataBridge {

    private EntityDataBridge() {
    }

    public static CompoundTag getPersistentData(Entity entity) {
        throw new AssertionError();
    }


    public static boolean shouldRiderSit(Entity vehicle) {
        throw new AssertionError();
    }
}
