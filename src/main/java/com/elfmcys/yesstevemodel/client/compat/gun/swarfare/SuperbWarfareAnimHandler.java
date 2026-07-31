package com.elfmcys.yesstevemodel.client.compat.gun.swarfare;

import com.elfmcys.yesstevemodel.client.entity.LivingAnimatable;
import com.elfmcys.yesstevemodel.geckolib3.geo.animated.AnimatedGeoModel;
import com.elfmcys.yesstevemodel.geckolib3.core.builder.ILoopType;
import com.elfmcys.yesstevemodel.geckolib3.core.event.predicate.AnimationEvent;
import com.elfmcys.yesstevemodel.geckolib3.core.enums.PlayState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * STUB: libs/ 未提供 superbwarfare jar。
 * 保留与 SWarfareCompat 对称的方法签名，供 ConditionManager 调用。
 */
public class SuperbWarfareAnimHandler {

    public static boolean isGunItem(ItemStack stack) {
        return false;
    }

    public static boolean isPlayerAiming(LivingEntity player) {
        return false;
    }

    public static void applyGunTransform(ItemStack stack, AnimatedGeoModel model, LivingEntity entity,
                                          PoseStack poseStack, int packedLightIn, float partialTicks) {
    }

    public static PlayState handleLungeMineAnim(AnimationEvent<? extends LivingAnimatable<? extends LivingEntity>> event) {
        return PlayState.CONTINUE;
    }

    public static PlayState handleTaczAnim(AnimationEvent<? extends LivingAnimatable<? extends LivingEntity>> event, String animation, ILoopType loopType) {
        return PlayState.CONTINUE;
    }

    public static PlayState handleGunHoldAnim(AnimationEvent<? extends LivingAnimatable<? extends LivingEntity>> event, ItemStack stack) {
        return PlayState.CONTINUE;
    }

    public static PlayState handleGunActionAnim(AnimationEvent<? extends LivingAnimatable<? extends LivingEntity>> event, ItemStack stack) {
        return PlayState.CONTINUE;
    }
}
