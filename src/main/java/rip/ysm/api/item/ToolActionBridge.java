package rip.ysm.api.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class ToolActionBridge {

    private ToolActionBridge() {
    }

    public static boolean canFishingRodCast(ItemStack stack) {
        throw new AssertionError();
    }


    public static boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        throw new AssertionError();
    }
}
