# AGENTS.md — ModernYSM-1.21.1 (OpenYSM NeoForge 移植)

本目录是将 [OpenYSMDev/ModernYSM](https://github.com/OpenYSMDev/ModernYSM)（YSM 开源替代，上游锁定 1.20.1 Forge/Fabric Architectury 工程）移植到 **Minecraft 1.21.1 NeoForge** 的工作目录。基于 [melon-444/OpenYSM-1.21.1Port](https://github.com/melon-444/OpenYSM-1.21.1Port)（v2.6.5r3）的 NeoForge 扁平化单模块工程，逐步增量到上游 2.6.6.6。

上游 1.20.1 基线 sparse-checkout 在 `/tmp/opencode/ModernYSM-upstream`（`common/` + `forge/` + `fabric/` 源码，无 `libs/` 二进制）。**临时目录，重启后需重新 clone**：
```bash
git clone --depth 1 --filter=blob:limit=2m --sparse https://github.com/OpenYSMDev/ModernYSM.git /tmp/opencode/ModernYSM-upstream
cd /tmp/opencode/ModernYSM-upstream && git sparse-checkout set common/src forge/src fabric/src && git checkout 1.20.1-forge
```

## 构建

| 命令 | 用途 |
|------|------|
| `./gradlew compileJava` | 仅编译，最快的验证手段 |
| `./gradlew clientjar` | **生成可用的 client jar**（`build/libs/ysm-*-client.jar`），含 ImageStream 依赖 |
| `./gradlew serverjar` | 生成 server jar（额外含 commons-codec） |
| `./gradlew runClient` | 启动游戏客户端测试 |

**不要用 `./gradlew build` 或 `./gradlew jarJar`**——`jarJar` 在 Java 21 下有 bug（移植版已知问题），用 `clientjar`/`serverjar` 自定义任务替代。

Gradle wrapper 用 **9.2.1**（`gradle-wrapper.properties` 锁定）。如果下载损坏，手动续传到 `~/.gradle/wrapper/dists/gradle-9.2.1-bin/<hash>/`。

## 关键约束

- **JDK 21** 必须；NeoForge 21.1.229；Parchment 2024.11.17；NeoGradle 7.1.27
- `neoforge.mods.toml` 已含 `license="MIT"`（阶段 6 已修）
- `.gitignore` 排除 `libs/*.jar`——compat 依赖 jar 不进 git，**按需放入 `libs/`**
- `build/`、`runs/`、`.gradle/` 均不提交

## NeoForge mixin 声明（极易踩坑）

**NeoForge 1.21.1 必须**在 `src/main/resources/META-INF/neoforge.mods.toml` 显式声明 mixin 配置才会加载（Forge 1.20.1 自动发现，移植时易丢）：
```toml
[[mixins]]
config="yes_steve_model.mixins.json"
```
漏掉会导致**所有 mixin 静默失效**（暂停菜单按钮、WorldRenderer、EntityRenderDispatcher 等全部不生效），且无报错。

mixins.json 里每个类都要存在，且目标方法签名必须适配 1.21.1：
- `PauseScreen.init()` 注入点需 `remap=false`（否则编译报 mapping 错误）
- 1.21.1 API 变点：`mouseScrolled(double,double,double,double)` 4 参、`renderBackground(gui,mx,my,pt)`、`EditBox.moveCursorToEnd(boolean)`、`Button`/`Checkbox` 构造器私有化需 builder、`Minecraft.getFrameTimeNs()*1e-9f`、`ResourceLocation.fromNamespaceAndPath`、`InventoryScreen.renderEntityInInventoryFollowsMouse` 10 参
- `MultiBufferSource.BufferSource.fixedBuffers` 类型是 `SequencedMap<RenderType, ByteBufferBuilder>`（1.21.1）

## libs/ 依赖（compileOnly）

`build.gradle` 用 `fileTree(dir: 'libs', include: '*.jar')` 扫描，**版本无关、文件名无关**。当前 `libs/` 含 8 个 jar（TACZ、TLM、Curios、GeckoLib、Jade、Sophisticated、ElytraSlot）。缺失的 compat 模组已 stub 化。

**ImageStream**（avif/webp/jpeg 解码）走 JitPack `com.github.TartaricAlkaline:ImageStream:-SNAPSHOT`，不走本地 jar。

## Compat stub 机制（重要）

`client/compat/` 下 25 个模组兼容模块中，**20 个是 stub**（第三方 jar 缺失）。stub 文件顶部标注 `// STUB`，方法体返回 false/null/空。原始实现备份在 `/tmp/opencode/orig_compat/`（临时）。

**恢复某个 compat**：把对应 jar 放入 `libs/`，然后从上游 `forge/src/main/java/.../compat/<mod>/` 或备份还原 Java 文件。

stub 化的 compat 列表：bettercombat, carryon, cosmeticarmorreworked (部分), create, firstperson, gun/superbwarfare, immersiveaircraft, immersivemelodies, ironsspellbooks, oculus, parcool, playeranimator, realcamera, simplehats, simpleplanes, slashblade, swem, top。另有 8 个 mixin accessor（parcool × 7 + create × 1）也是空接口 stub。

**MixinTweaker**（`mixin/plugin/MixinTweaker.java`）在运行时按 `isLoaded()` 动态注册 mixin——stub 让 `isLoaded()` 恒为 false，accessor 永不加载。

## Native 库

- 6 平台二进制在 `src/main/resources/natives/`（从上游 2.6.6.6 同步）
- `NativeLibLoader` 从 `/natives/<platform>/` 加载（不是 `/META-INF/native/`）
- 环境变量 `OYSM_DISABLE_SMID` 可禁用 native，回退纯 Java
- 阶段 3 已恢复完整 native 渲染：`GeoModel` 11 个 native 方法与二进制 JNI 符号**已对齐**（`nm -D` 验证）
- `NativeModelRenderer.renderMesh` 分支：GPU（GpuRenderPath）→ native（nComputeModelVertices）→ CPU（renderModel）
- **安卓上 native/GPU 渲染已禁用**（`NativeLibLoader.isOnAndroid()` 短路），强制 CPU 渲染

## 安卓检测（FCL / Zalith）

`NativeLibLoader.isOnAndroid()` 依赖 `MOD_ANDROID_RUNTIME` 环境变量（仅 FCL 设置）。**Zalith 启动器（ZalithLauncher2.x）不设置此变量** → `isOnAndroid()` 可能返回 false，安卓 GLES 保护失效。

当前依赖 `isOnAndroid()` 的保护点：
- `NativeModelRenderer.renderMesh`（native/GPU 渲染）✓
- `BlurStack.flush`（GUI 模糊）✓
- `Pie.draw` **无保护**——桌面 GL 着色器在安卓破坏渲染（当前已知 bug）

安卓排查法：日志里 `OS Linux arch aarch64 version Android-16` + 启动器名（`--versionType`）确认是安卓。

## rip.ysm.api 平台桩（@ExpectPlatform 残留）

上游是 Architectury 多平台，`rip.ysm.api.*` 里部分是 `@ExpectPlatform` 桩（方法体 `throw new AssertionError()`），**移植后必须逐一实现 NeoForge 版**。已实现：
- `rip.ysm.api.client.KeyMappingFactory` → NeoForge 版（`KeyConflictContext`/`KeyModifier`）
- `rip.ysm.compat.touhoulittlemaid` / `oculus` / `optifine` → 转发门面

**遇到 `AssertionError` 崩溃**，先查调用链是否落到 `rip.ysm.api` 的桩方法。

## YSMParser 子模块

`YSMParser/` 是 git submodule（`https://github.com/OpenYSM/YSMParser.git`），native 编译（`compileNative` task）需要它。当前 `compileNative` 被 `if(true) return false` 短路，不影响构建。

## 移植进度

| 阶段 | 内容 | 状态 |
|------|------|------|
| 0 | 构建加固、stub、native、ImageStream | ✅ 完成 |
| 1 | 拼音搜索核心、ClientOnly 模式、配置/语言 | ✅ 完成 |
| 1b | SearchSuggestions + PlayerModelScreen 搜索 UI | ✅ 完成 |
| 2 | rip.ysm.gui、搜索 UI、API 对齐（PlayerCapability.get 等） | ✅ 完成 |
| 3 | geckolib3 关键帧重构、GPU 渲染栈、native 恢复 | ✅ 完成 |
| 4 | 网络层：大模型分片协议、握手 brand、mixin accessor 补全 | ✅ 完成 |
| 5 | 现代配置界面、模型上传系统、女仆兼容 | ✅ 完成 |
| 6 | 版本号 2.6.6.6、mods.toml MIT | ✅ 完成 |

版本号：`2.6.6.6-NeoForge+mc1.21.1`（build.gradle 与 mods.toml 均已同步）。

## 已知未解决问题（待修）

- **Modern 轮盘全模糊**（安卓）：`Pie.draw` 用桌面 GL 着色器（`GL20`）画扇形，安卓 GLES 下破坏渲染状态 → 全屏模糊。`BlurStack` 已短路但 `Pie` 没有。
- **女仆模型不变**：`TouhouLittleMaidCompat.init()` 用 `VersionRange "[1.1.15,)"` 检查 TLM 版本，TLM 版本 `1.5.3-neoforge+mc1.21.1`（带 qualifier）比较不可靠 → `IS_LOADED=false` → 女仆渲染器/capability 未注册。曾尝试放宽版本检查被 revert（引入新 bug），需谨慎处理。
- **女仆 capability**：`MaidCapabilityEvent` 注册 `MAID_CAP` 到 `EntityMaid.TYPE`（原误注册为 `EntityType.PLAYER`，已修）。
- **女仆模型发送**：`TouhouMaidModelButton.onPress` 的服务器同步（`YsmMaidModelPackage`）曾因 Forge `CHANNEL.sendToServer` 残留被注释，已改用 `PacketDistributor.sendToServer`。

## 已知平台限制

- **安卓（FCL / Zalith）**：native/GPU 渲染已禁用（`isOnAndroid()` 时强制 CPU）。但 `Pie`/`BlurStack` 的桌面 GL 调用仍可能破坏渲染——所有 `rip.ysm.gpu` 的 GL 调用点都应加安卓保护。
- **macOS**：`GpuCapability` 自动禁用 GPU 渲染（GL 4.1 无 SSBO）。
- **轮盘界面**：Modern/classic 由 `GeneralConfig.effectiveModernRoulette()` 决定（`ROULETTE_MODE` 与 `ROULETTE_SETTINGS_MODE` 都需 MODERN），默认 CLASSIC。入口：`AnimationRouletteKey` / `ExtraAnimationKey` / `PauseScreenButtonBuilder` / `MaidAnimationRoulette`。
- **暂停菜单按钮**：`PauseScreenButtonBuilder.createButtons` 现在**所有平台**都显示（原仅安卓 `isOnAndroid()`）。

## 代码结构

```
src/main/java/
  com/elfmcys/yesstevemodel/
    YesSteveModel.java        ← @Mod 入口
    NativeLibLoader.java      ← native 加载
    client/                   ← 客户端渲染、GUI、动画、compat
    model/                    ← ServerModelManager 等模型管理
    network/                  ← 网络包（阶段 4 重写）
    geckolib3/                ← 内嵌的 geckolib 移植层
    mixin/                    ← Mixin + MixinTweaker 插件
  rip/ysm/
    gpu/                      ← GPU 渲染栈（BlurStack 已安卓短路；Pie 未保护）
    api/                      ← 平台桩（KeyMappingFactory 已 NeoForge 化，注意 AssertionError）
    compat/                   ← 平台门面（TLM/Oculus/OptiFine）
    pinyin/                   ← PinyinMatcher（拼音搜索）
    algorithms/ security/ zstd/ legacy/
  net/sourceforge/pinyin4j/   ← 内嵌拼音库（18 类）
```

## 测试

无自动化测试。验证方式：`clientjar` → 放入 `mods/` 文件夹 → 启动游戏。P0 测试目标：**TACZ + TLM** 兼容性。

**安卓实机日志**：用户放 `debug*/DebugII/` 目录（`latest.log`/`debug.log`）。排查步骤：
1. 确认 mod 加载：`grep "yes_steve_model" latest.log`
2. 确认 native 加载：`grep "native library" latest.log`
3. 确认 mixin 生效：`grep "yes_steve_model.mixins.json" debug.log`（`[[mixins]]` 声明后）
4. 崩溃栈定位：`grep -A20 "Exception" latest.log`，重点看 `at TRANSFORMER/yes_steve_model`
5. jar 完整性：构建后 `sha1sum`，与传输后比对（用户网络不稳，jar 常因传输截断损坏导致 `zip END header not found`）

**jar SHA1**：每次交付构建后必须附上（用户依赖它校验传输完整性）。

## 与上游 2.6.6.6 的差异对照（2026-08-01 子代理扫描，备查）

上游基线：`/tmp/opencode/ModernYSM-upstream`（common/ + forge/，1.20.1-forge，commit 301cc3e；临时目录，重启后需重新 clone）。本节记录 4 个模块与上游的差异与风险，**遇到对应模块问题先查这里**。

### A. TLM/女仆 compat 模块

| 文件 | 差异 | 风险 | 状态 |
|---|---|---|---|
| `client/gui/TouhouMaidModelScreen.java` | `createModelButton` override 签名必须匹配 6 参（`PlayerModelScreen.init` 调用 6 参；只 override 5 参会分派到基类返回普通 `ModelButton`，点击改的是玩家模型） | 高 | ✅ 已修（曾导致"选模改玩家"） |
| `client/gui/button/TouhouMaidModelButton.java` | 必须提供 6 参构造（委托 `super(..., targetModelId)`），5 参构造委托 6 参 | 高 | ✅ 已修 |
| `client/compat/touhoulittlemaid/TouhouLittleMaidCompat.java` | 反射调用 TLM `com.github.tartaricacid.touhoulittlemaid.compat.ysm.YsmCompat.init()`（`wakeTlmYsmCompat`）——TLM 1.5.3 全 jar 无调用者，INSTALLED 恒 false 则渲染钩子永不生效；日志确认 `[YSM] TLM YsmCompat.init() invoked via reflection` | 高 | ✅ 已修 |
| `capability/MaidCapabilityProvider.java` | MAID_CAP 不可静态 final 判定（类加载期 ModList 可能未含 TLM）：`volatile` 占位 + `ensureResolved()` 在 RegisterCapabilitiesEvent（所有 mod 构造完成）解析 | 中 | ✅ 已修 |
| `MaidAnimationRoulette.java` | `openRouletteScreen` 的 cap 必须 null 保护（上游 `ifPresent`，移植曾直接解引用 NPE）；已支持 `effectiveModernRoulette()` 分支（上游硬编码经典轮盘） | 高 | ✅ 已修 |
| `event/MaidClientTickEvent.java` | `tickMaidModel` 逻辑方向：`if (cap == null) return`（上游 `ifPresent` 空操作） | 低 | ✅ 已修 |
| `TouhouMaidTextureButton.java:45` | **网络发送被注释**：单独换纹理只改本地预览，不同步服务器（模型按钮 `TouhouMaidModelButton` 走 `PacketDistributor.sendToServer(YsmMaidModelPackage)` 正常） | 中 | ⚠️ 待修 |

### B. 渲染/动画/geckolib3 模块

- 高风险仅 1 项：`client/animation/predicate/LivingMovementAnimationPredicate.java` 的 `(Player)` 强转（上游是 `LivingEntity`）——女仆实体必 CCE，曾导致世界不显示 + 选模闪退 + 变体叠加（动画链中断连带） | ✅ 已修
- 其余全部低风险：compat 包 import 迁移、`NativeModelRenderer` 安卓短路、GPU 栈（rip/ysm/gpu）安卓短路、VertexConsumer API 1.21.1 适配
- 渲染管线（含骨骼可见性/变体逻辑，`NativeModelRenderer` scale==0 判隐）与上游一致
- 变体机制：不是多模型数组（`models[0]/[1]` = main/arm），而是同一模型内动画控制器驱动骨骼 scale 0 显隐；模型自带 controller 经 `ParallelProcessor`（`player.parallel_N`/`maid.parallel_N` 槽位）注册执行

### C. GUI/输入/网络/事件模块

| 文件 | 差异 | 风险 |
|---|---|---|
| `network/` 全部 | 整体重写：NeoForge `PacketDistributor` + `CustomPacketPayload` + versioned registrar + FragmentPacket 大模型分片协议（上游为 `rip.ysm.api.network.YSMChannel` 门面 + 自定义 Packet） | 中【重写】 |
| `TouhouMaidTextureButton` | 网络发送注释（见 A 表） | 中（已知） |
| GUI 按钮/界面工厂方法 | 除 createModelButton 外无 override 分派风险（createTextureButton 4 参、renderTexturePreview 统一 4 参均一致） | 低 |

### D. 资源/模型数据/capability/配置/工具模块

| 文件 | 差异 | 风险 |
|---|---|---|
| `resource/pojo/RawYsmModel.java` | `vehicles`/`projectiles`/`animationControllers`：`List` → `Map`（按名索引）；`RawSubEntity.identifier` 新增；`animations`/`transitions`：`List<Entry>` → `Map` | 中（结构重构，与序列化对应） |
| `resource/pojo/RawYsmModel.java` | **`mergeMultilineExpr` 默认值 `false`→`true`**（上游 TODO"什么时候默认为true"，移植版直接默认 true）——影响 molang 多行表达式合并，遇表达式解析异常先查此项 | **中 ⚠️ 行为差异** |
| `resource/pojo/RawYsmModel.java` | `legacyUnknownInt` 新增（注释"作用暫時不明確，通常為0"） | 低 |
| 音频解码 | `OggAudioStream` → `STBVorbis` 重写（安卓兼容） | 中（重写） |
| `ResourceCleanupHelper` | `util/` 子包 → 顶层 `com.elfmcys.yesstevemodel` | 低 |
| capability 注册 | Forge `AttachCapabilitiesEvent` → NeoForge `RegisterCapabilitiesEvent`（【适配】） | 低 |

### 排查提示

- 遇到 `AssertionError` 崩溃：先查 `rip.ysm.api` 平台桩（YSMChannel/ToolActionBridge/EntityDataBridge/RenderLivingBridge/BufferBuilderBridge——全树无调用方，不可达则忽略）
- 遇到变体/模型叠加：先确认动画链完整执行（parallel 槽位控制器），再查 `mergeMultilineExpr` 配置
- 报告原始文件：`.pi-subagents/artifacts/outputs/*/diff-report-*.md`（C 已落盘；B 在子代理输出中）
