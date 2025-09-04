# Platform 平台抽象

Platform 系統是 M104SharedLogic 中實現跨平台功能的核心機制，使用 Kotlin Multiplatform 的 expect/actual 模式來處理平台特定的功能。

## 概述

Platform 接口提供了訪問平台特定信息的統一方式，讓共享業務邏輯能夠獲取平台相關的資訊，同時保持代碼的跨平台性。

## 核心接口

### Platform 接口定義

```kotlin
interface Platform {
    val name: String      // 平台名稱
    val version: String   // 平台版本信息
}
```

### 平台獲取函數

```kotlin
expect fun getPlatform(): Platform
```

此函數使用 `expect` 關鍵字聲明，表示需要在各個平台模組中提供具體實現。

## 平台實現

### Android 實現

位置：`androidMain/kotlin/com/m104atsp/foundation/Platform.android.kt`

```kotlin
import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android"
    override val version: String = "${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
```

**功能說明：**
- 使用 `Build.VERSION.SDK_INT` 獲取 Android API 等級
- 提供 Android 平台的標識信息

### iOS 實現

位置：`iosMain/kotlin/com/m104atsp/foundation/Platform.ios.kt`

```kotlin
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val version: String = UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()
```

**功能說明：**
- 使用 `UIDevice.currentDevice` 獲取 iOS 系統信息
- 提供系統名稱和版本號

## 使用範例

### 基本使用

```kotlin
import com.m104atsp.foundation.getPlatform

fun showPlatformInfo() {
    val platform = getPlatform()
    println("應用正在運行於：${platform.name}")
    println("版本：${platform.version}")
}
```

### 在業務邏輯中使用

```kotlin
class PlatformService {
    private val platform = getPlatform()
    
    fun getPlatformInfo(): String {
        return "運行平台：${platform.name} ${platform.version}"
    }
    
    fun isPlatformSupported(): Boolean {
        return when {
            platform.name.contains("Android") -> true
            platform.name.contains("iOS") -> true
            else -> false
        }
    }
}
```

### 條件邏輯處理

```kotlin
fun handlePlatformSpecificBehavior() {
    val platform = getPlatform()
    
    when {
        platform.name.contains("Android") -> {
            // Android 特定邏輯
            println("處理 Android 平台邏輯")
        }
        platform.name.contains("iOS") -> {
            // iOS 特定邏輯
            println("處理 iOS 平台邏輯")
        }
        else -> {
            println("未知平台：${platform.name}")
        }
    }
}
```

## 架構設計原則

### 1. 單一職責原則

Platform 接口專注於提供平台識別信息，不包含複雜的業務邏輯。

### 2. 開放封閉原則

- **開放擴展**：可以輕易添加新的平台實現
- **封閉修改**：不需要修改現有的共享代碼

### 3. 依賴倒置原則

```kotlin
// ✅ 好的做法：依賴抽象
class SomeService(private val platform: Platform) {
    fun doSomething() {
        val info = platform.name
        // 使用平台信息...
    }
}

// ❌ 不好的做法：直接依賴具體實現
class SomeService {
    fun doSomething() {
        val platform = AndroidPlatform() // 硬編碼依賴
        // ...
    }
}
```

## 擴展 Platform 接口

如果需要添加更多平台信息，可以擴展 Platform 接口：

```kotlin
interface Platform {
    val name: String
    val version: String
    
    // 新增屬性
    val architecture: String
    val locale: String
    
    // 新增方法
    fun getDeviceId(): String
    fun isDebugMode(): Boolean
}
```

然後在各平台實現中提供具體實現：

```kotlin
// Android 實現
class AndroidPlatform : Platform {
    override val name: String = "Android"
    override val version: String = "${Build.VERSION.SDK_INT}"
    override val architecture: String = Build.CPU_ABI
    override val locale: String = Locale.getDefault().toString()
    
    override fun getDeviceId(): String {
        // Android 特定實現
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
    
    override fun isDebugMode(): Boolean {
        return BuildConfig.DEBUG
    }
}
```

## 測試策略

### 模擬平台測試

```kotlin
import kotlin.test.Test
import kotlin.test.assertTrue

class PlatformTest {
    
    @Test
    fun testPlatformInfo() {
        val platform = getPlatform()
        
        // 驗證平台信息不為空
        assertTrue(platform.name.isNotEmpty())
        assertTrue(platform.version.isNotEmpty())
        
        // 驗證平台類型
        assertTrue(
            platform.name.contains("Android") || 
            platform.name.contains("iOS")
        )
    }
}
```

### 創建測試專用平台

```kotlin
class TestPlatform(
    override val name: String = "Test",
    override val version: String = "1.0"
) : Platform

// 在測試中使用
fun createTestPlatformService(): PlatformService {
    // 可以注入測試平台...
}
```

## 最佳實踐

### 1. 保持接口簡潔

```kotlin
// ✅ 好的做法：簡潔的接口
interface Platform {
    val name: String
    val version: String
}

// ❌ 避免：過於複雜的接口
interface Platform {
    val name: String
    val version: String
    val networkManager: NetworkManager
    val fileManager: FileManager
    val databaseManager: DatabaseManager
    // ... 太多職責
}
```

### 2. 使用一致的命名

```kotlin
// ✅ 一致的命名模式
class AndroidPlatform : Platform
class IOSPlatform : Platform
class WindowsPlatform : Platform

// ❌ 不一致的命名
class AndroidImpl : Platform
class IOS_Platform : Platform  
class Win32Platform : Platform
```

### 3. 適當的錯誤處理

```kotlin
actual fun getPlatform(): Platform {
    return try {
        AndroidPlatform()
    } catch (e: Exception) {
        // 提供回退方案
        object : Platform {
            override val name = "Unknown Android"
            override val version = "Unknown"
        }
    }
}
```

## 常見用途

### 1. 日誌記錄

```kotlin
class Logger {
    private val platform = getPlatform()
    
    fun log(message: String) {
        println("[${platform.name}] $message")
    }
}
```

### 2. 功能可用性檢查

```kotlin
fun isFeatureAvailable(feature: String): Boolean {
    val platform = getPlatform()
    
    return when (feature) {
        "biometric" -> {
            platform.name.contains("Android") && 
            platform.version.toIntOrNull()?.let { it >= 23 } ?: false
        }
        "push_notifications" -> true // 所有平台都支持
        else -> false
    }
}
```

### 3. 平台特定配置

```kotlin
class AppConfig {
    private val platform = getPlatform()
    
    val maxCacheSize: Long = when {
        platform.name.contains("iOS") -> 100_000_000L // 100MB
        platform.name.contains("Android") -> 200_000_000L // 200MB
        else -> 50_000_000L // 50MB
    }
}
```

## 注意事項

1. **不要在 Platform 中包含複雜邏輯**：Platform 應該只提供基本信息
2. **避免頻繁調用 getPlatform()**：考慮在需要時緩存 Platform 實例
3. **保持向後兼容**：當修改 Platform 接口時，確保不破壞現有代碼
4. **測試所有平台**：確保每個平台實現都經過充分測試

## 相關文檔

- **[CLAUDE.md](../CLAUDE.md)** - 了解專案整體架構和開發指引
- **[日期驗證系統](./date-validation-system.md)** - 查看實際的跨平台業務邏輯示例