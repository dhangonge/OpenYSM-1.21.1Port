package com.elfmcys.yesstevemodel.client.compat.cosmeticarmorreworked;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * STUB：libs/ 未提供 cosmeticarmorreworked，编译期无 lain.mods.cos.* 可用。
 * 恢复方式：把 cosmeticarmorreworked jar 放进 libs/，再从上游 ModernYSM
 * forge/src/main/java/.../compat/cosmeticarmorreworked/CosmeticArmorCompat.java 还原实现。
 */
public class CosmeticArmorCompat {

    public static void init() {
    }

    public static boolean isLoaded() {
        return false;
    }

    public static Optional<ItemStack> getCosmeticArmor(Player player, EquipmentSlot equipmentSlot) {
        return Optional.empty();
    }
}
