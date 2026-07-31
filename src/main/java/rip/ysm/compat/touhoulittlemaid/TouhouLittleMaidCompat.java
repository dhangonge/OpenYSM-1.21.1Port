package rip.ysm.compat.touhoulittlemaid;

import com.elfmcys.yesstevemodel.client.animation.molang.TLMBinding;
import com.elfmcys.yesstevemodel.client.entity.LivingAnimatable;
import com.elfmcys.yesstevemodel.client.model.ModelResourceBundle;
import com.elfmcys.yesstevemodel.client.model.PlayerModelBundle;
import com.elfmcys.yesstevemodel.geckolib3.core.enums.PlayState;
import com.elfmcys.yesstevemodel.geckolib3.core.event.predicate.AnimationEvent;
import com.elfmcys.yesstevemodel.geckolib3.geo.GeoReplacedEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

/**
 * 上游 rip.ysm.compat.touhoulittlemaid 门面（原为 @ExpectPlatform 桩），
 * 统一转发到移植版的 com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat。
 */
public final class TouhouLittleMaidCompat {

    private TouhouLittleMaidCompat() {
    }

    public static boolean isLoaded() {
        return com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.isLoaded();
    }

    public static boolean isMaidEntity(Entity entity) {
        return com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.isMaidEntity(entity);
    }

    public static boolean isMaidRideable(Entity entity) {
        return com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.isMaidRideable(entity);
    }

    public static boolean isSimplePlanesEntity(Entity entity) {
        return com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.isSimplePlanesEntity(entity);
    }

    public static boolean isImmersiveAircraftEntity(Entity entity) {
        return com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.isImmersiveAircraftEntity(entity);
    }

    public static boolean isMaidItem(Item item) {
        return com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.isMaidItem(item);
    }

    public static String getMaidEntityId(Entity entity) {
        return com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.getMaidEntityId(entity);
    }

    public static boolean isMaidSitting(LivingEntity livingEntity) {
        return com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.isMaidSitting(livingEntity);
    }

    public static void registerMaidAnimStates(TLMBinding tlmBinding) {
        com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.registerMaidAnimStates(tlmBinding);
    }

    public static PlayState handleMaidInteraction(AnimationEvent<LivingAnimatable<?>> event, LivingEntity livingEntity, Entity entity) {
        return com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.handleMaidInteraction(event, livingEntity, entity);
    }

    public static boolean isMaidChatAvailable() {
        return com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.isMaidChatAvailable();
    }

    public static void openMaidChat() {
        com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.openMaidChat();
    }

    public static Object buildControllers(PlayerModelBundle modelBundle, ModelResourceBundle resourceBundle) {
        return com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.buildControllers(modelBundle, resourceBundle);
    }

    @Nullable
    public static GeoReplacedEntityRenderer<?, ?> getMaidPreviewRenderer(LivingAnimatable<?> animatable) {
        Object renderer = com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid.TouhouLittleMaidCompat.getMaidModelProvider();
        return renderer instanceof GeoReplacedEntityRenderer<?, ?> r ? r : null;
    }
}
