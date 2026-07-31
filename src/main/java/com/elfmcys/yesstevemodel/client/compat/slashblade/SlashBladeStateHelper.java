package com.elfmcys.yesstevemodel.client.compat.slashblade;

import com.elfmcys.yesstevemodel.geckolib3.core.builder.ILoopType;
import com.elfmcys.yesstevemodel.geckolib3.core.event.predicate.AnimationEvent;
import com.elfmcys.yesstevemodel.geckolib3.core.enums.PlayState;
import com.elfmcys.yesstevemodel.client.entity.LivingAnimatable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * STUB: libs/ 未提供 slashblade / SlashBladeResharped jar。
 */
public class SlashBladeStateHelper {

    public static boolean isSlashBlade(ItemStack itemStack) {
        return false;
    }

    public static String getSlashBladeAnimation(Object event) {
        return null;
    }

    public static String getSlashBladeAnimationFromContext(Object context) {
        return null;
    }

    public static PlayState handleSlashBladeAnim(AnimationEvent<? extends LivingAnimatable<? extends LivingEntity>> event, String animation, ILoopType loopType) {
        return PlayState.CONTINUE;
    }
}
