package com.elfmcys.yesstevemodel.client.compat.carryon;

import net.minecraft.world.entity.LivingEntity;

/**
 * STUB: libs/ 未提供 carryon jar。
 */
public class CarryOnDataHelper {

    public static boolean isPlayerCarrying(LivingEntity livingEntity) {
        return false;
    }

    public enum CarryType {
        ENTITY, BLOCK, PLAYER, NONE
    }

    public static CarryType getCarryType(net.minecraft.world.entity.player.Player player) {
        return CarryType.NONE;
    }
}
