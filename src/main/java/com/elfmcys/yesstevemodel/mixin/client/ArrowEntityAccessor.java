package com.elfmcys.yesstevemodel.mixin.client;

import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Arrow.class})
public interface ArrowEntityAccessor {
    @Invoker(value="getPotionContents",remap = false)
    PotionContents invokeGetPotionContents();
}
