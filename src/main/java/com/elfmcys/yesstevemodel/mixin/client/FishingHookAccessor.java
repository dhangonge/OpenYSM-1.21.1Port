package com.elfmcys.yesstevemodel.mixin.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({FishingHook.class})
public interface FishingHookAccessor {
    @Accessor(value="biting",remap = false)
    boolean isBiting();

    @Accessor(value="hookedIn",remap = false)
    Entity getHookedIn();
}