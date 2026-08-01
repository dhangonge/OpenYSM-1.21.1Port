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
- `neoforge.mods.toml` 当前 `license="All rights reserved"` 是**从原版 YSM 继承的错误**，实际是 MIT（阶段 6 待修）
- `.gitignore` 排除 `libs/*.jar`——compat 依赖 jar 不进 git，**按需放入 `libs/`**
- `build/`、`runs/`、`.gradle/` 均不提交

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
- **注意**：移植版的 native **渲染路径已拆除**（`NativeModelRenderer` 硬编码 `false`），native 仅用于 zstd 解压。上游 11 个 JNI 符号与移植版 3 个声明**不匹配**，恢复 native 渲染属于阶段 3

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

## 已知平台限制

- **安卓（FCL）**：native/GPU 渲染路径已禁用（`NativeLibLoader.isOnAndroid()` 时强制 CPU 渲染），因 C++ 背面剔除按桌面 GL 坐标约定编写，安卓 GLES 会误剔。见 `NativeModelRenderer.renderMesh`。
- **macOS**：`GpuCapability` 自动禁用 GPU 渲染（GL 4.1 无 SSBO）。

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
    gpu/                      ← GPU 渲染栈（完整，安卓禁用）
    compat/                   ← 平台门面（TLM/Oculus/OptiFine）
    pinyin/                   ← PinyinMatcher（拼音搜索）
    algorithms/ security/ zstd/ legacy/ api/
  net/sourceforge/pinyin4j/   ← 内嵌拼音库（18 类）
```

## 测试

无自动化测试。验证方式：`clientjar` → 放入 `mods/` 文件夹 → 启动游戏。P0 测试目标：**TACZ + TLM** 兼容性。
