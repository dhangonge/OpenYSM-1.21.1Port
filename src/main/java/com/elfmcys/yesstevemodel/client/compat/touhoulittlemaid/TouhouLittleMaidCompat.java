package com.elfmcys.yesstevemodel.client.compat.touhoulittlemaid;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.animation.molang.TLMBinding;
import com.elfmcys.yesstevemodel.client.model.ModelResourceBundle;
import com.elfmcys.yesstevemodel.client.model.PlayerModelBundle;
import com.elfmcys.yesstevemodel.client.entity.LivingAnimatable;
import com.elfmcys.yesstevemodel.geckolib3.core.event.predicate.AnimationEvent;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.util.StringPool;
import com.elfmcys.yesstevemodel.geckolib3.core.enums.PlayState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class TouhouLittleMaidCompat {

    private static final String MOD_ID = "touhou_little_maid";
    private static final VersionRange VERSION_RANGE;
    private static boolean IS_LOADED = false;

    static {
        try {
            VERSION_RANGE = VersionRange.createFromVersionSpec("[1.1.15,)");
        } catch (InvalidVersionSpecificationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        ModList.get().getModContainerById(MOD_ID).ifPresent(modContainer -> {
            if (VERSION_RANGE.containsVersion(modContainer.getModInfo().getVersion())) {
                IS_LOADED = true;
            } else {
                IS_LOADED = !FMLEnvironment.production;
            }
            if (IS_LOADED) {
                wakeTlmYsmCompat();
                MaidEventHandler.init();
                MaidEventHandler.registerMaidRenderer();
                MaidAnimation.registerAnimationStates();
            }
        });
    }

    /**
     * TLM 1.5.3 的 YsmCompat.init() 在整个 jar 中没有任何调用者（上游死代码），导致 INSTALLED 恒为 false：
     * TLM 的 EntityMaidRenderer.initYsmModelRenderer() 第一行 if (!YsmCompat.isInstalled()) return，
     * YSM 设置的 YSM_ENTITY_MAID_RENDERER 永远不会被使用（女仆模型数据能选、但渲染不接管，模型不变）。
     * 此处反射调用 YsmCompat.init()（public static，内部按 [2.3.3,) 检查 YSM 版本，2.6.6.6-neoforge+mc1.21.1 满足），
     * 使其 INSTALLED=true。时序安全：本方法在 FMLClientSetupEvent.enqueueWork 中执行，
     * 早于 EntityRenderersEvent.RegisterRenderers（TLM 构造 EntityMaidRenderer 时钩子已生效）。
     */
    private static void wakeTlmYsmCompat() {
        try {
            Class<?> cls = Class.forName("com.github.tartaricacid.touhoulittlemaid.compat.ysm.YsmCompat");
            cls.getMethod("init").invoke(null);
            YesSteveModel.LOGGER.info("[YSM] TLM YsmCompat.init() invoked via reflection, maid renderer hook enabled");
        } catch (Throwable th) {
            YesSteveModel.LOGGER.debug("[YSM] Failed to wake TLM YsmCompat: {}", th.toString());
        }
    }

    public static Object buildControllers(PlayerModelBundle modelBundle, ModelResourceBundle resourceBundle) {
        if (IS_LOADED) {
            return MaidAnimationController.buildControllers(modelBundle, resourceBundle);
        }
        return null;
    }

    public static boolean isLoaded() {
        return IS_LOADED;
    }

    public static boolean isMaidEntity(Entity entity) {
        return isLoaded() && MaidEventHandler.isMaid(entity);
    }

    public static boolean isMaidRideable(Entity entity) {
        return isLoaded() && MaidEventHandler.isYsmModelMaid(entity);
    }

    public static boolean isSimplePlanesEntity(Entity entity) {
        return isLoaded() && MaidEventHandler.isChair(entity);
    }

    public static boolean isImmersiveAircraftEntity(Entity entity) {
        return isLoaded() && MaidEventHandler.isSit(entity);
    }

    public static boolean isMaidItem(Item item) {
        return isLoaded() && MaidEventHandler.isGohei(item);
    }

    public static String getMaidEntityId(Entity entity) {
        return isLoaded() ? MaidEventHandler.getChairModelId(entity) : StringPool.EMPTY;
    }

    public static boolean isMaidSitting(LivingEntity livingEntity) {
        return isLoaded() && MaidEventHandler.isMaidFishing(livingEntity);
    }

    public static void registerMaidAnimStates(TLMBinding tlmBinding) {
        if (isLoaded()) {
            MaidBinding.registerBindings(tlmBinding);
        } else {
            registerDummyBindings(tlmBinding);
        }
    }

    private static void registerDummyBindings(TLMBinding binding) {
        binding.livingEntityVar("is_begging", ctx -> false);
        binding.livingEntityVar("is_sitting", ctx -> false);
        binding.livingEntityVar("has_backpack", ctx -> false);
        binding.livingEntityVar("favorability_point", ctx -> 0);
        binding.livingEntityVar("favorability_level", ctx -> 0);
        binding.livingEntityVar("task_id", ctx -> StringPool.EMPTY);
        binding.livingEntityVar("schedule", ctx -> StringPool.EMPTY);
        binding.livingEntityVar("activity", ctx -> StringPool.EMPTY);
        binding.livingEntityVar("gomoku_win_count", ctx -> 0);
        binding.livingEntityVar("gomoku_rank", ctx -> 1);
        binding.livingEntityVar("game_statue", ctx -> StringPool.EMPTY);
        binding.livingEntityVar("backpack_type", ctx -> StringPool.EMPTY);
        binding.livingEntityVar("is_entity", ctx -> true);
        binding.livingEntityVar("is_statue", ctx -> false);
        binding.livingEntityVar("is_garage_kit", ctx -> false);
        binding.livingEntityVar("show_item", ctx -> StringPool.EMPTY);
    }

    @Nullable
    public static PlayState handleMaidInteraction(AnimationEvent<LivingAnimatable<?>> event, LivingEntity livingEntity, Entity entity) {
        if (isLoaded()) {
            return MaidInteractionAnimHandler.handleMaidInteractionAnim(event, livingEntity, entity);
        }
        return null;
    }

    public static void syncMaidState(LivingEntity livingEntity) {
        if (isLoaded()) {
            MaidEventHandler.setExtraRenderFlag(livingEntity);
        }
    }

    public static MaidEntityRenderer getMaidModelProvider() {
        if (isLoaded()) {
            return MaidEventHandler.getMaidRenderer();
        }
        return null;
    }

    public static boolean isMaidChatAvailable() {
        return isLoaded() && MaidAnimationRoulette.canOpenRoulette();
    }

    public static void openMaidChat() {
        if (isLoaded()) {
            MaidAnimationRoulette.openRouletteScreen();
        }
    }
}