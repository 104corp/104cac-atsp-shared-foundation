# M104SharedLogic

> Kotlin Multiplatform 跨平台業務邏輯共享庫

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg)](https://kotlinlang.org)
[![KMP](https://img.shields.io/badge/Kotlin-Multiplatform-orange.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![iOS](https://img.shields.io/badge/Platform-iOS-lightgrey.svg)](https://developer.apple.com/ios/)

## 🚀 專案概述

**M104SharedLogic** 是一個專注於 **純業務邏輯** 的 Kotlin Multiplatform 庫，為 iOS 和 Android 平台提供共享的業務邏輯功能。本庫不包含任何 UI 框架依賴，專門作為跨平台業務邏輯的基礎庫。

### 主要特色

- ✅ **純業務邏輯**：無 UI 依賴，專注業務規則實現
- ✅ **跨平台支持**：Android (API 24+) 和 iOS (arm64 + Simulator)  
- ✅ **日期驗證系統**：完整的面試時間驗證功能
- ✅ **平台抽象**：使用 expect/actual 模式處理平台差異
- ✅ **類型安全**：完整的 Kotlin 類型安全保障

## 🏗️ 專案結構

```
M104SharedLogic/
├── foundation/                          # 核心多平台模組
│   ├── src/
│   │   ├── commonMain/kotlin/           # 共享業務邏輯
│   │   │   └── com/m104atsp/foundation/
│   │   │       ├── Platform.kt          # 平台抽象接口
│   │   │       └── conmunication/date/  # 日期驗證系統
│   │   ├── androidMain/kotlin/          # Android 特定實現
│   │   ├── iosMain/kotlin/              # iOS 特定實現
│   │   └── commonTest/kotlin/           # 共享單元測試
├── docs/                               # 中文文檔
└── CLAUDE.md                          # 開發指引
```

## 🔧 技術規格

- **語言**：Kotlin 2.0.0
- **平台**：Android (API 24+)、iOS (arm64 + Simulator)
- **JVM 目標**：Java 17
- **主要依賴**：kotlinx-datetime 0.6.1

## 📦 安裝使用

### Android 整合

```kotlin
dependencies {
    implementation project(':foundation')
    // 或發佈版本：
    implementation 'com.m104atsp:foundation:1.0.0'
}
```

### iOS 整合

1. 建構 iOS Framework：
   ```bash
   ./gradlew :foundation:linkReleaseFrameworkIosArm64
   ```

2. 在 Xcode 專案中添加生成的 **M104Foundation** framework

3. 在 Swift 中使用：
   ```swift
   import M104Foundation
   let platform = GetPlatformKt.getPlatform()
   ```

## 🛠️ 開發命令

```bash
# 建構專案
./gradlew build

# 執行測試
./gradlew test

# 建構 Android 庫
./gradlew :foundation:assembleRelease

# 建構 iOS Framework
./gradlew :foundation:linkReleaseFrameworkIosArm64

# 清理建構
./gradlew clean
```

## 💼 核心功能

### 日期驗證系統

```kotlin
import com.m104atsp.foundation.conmunication.date.DateRuleChecker
import com.m104atsp.foundation.conmunication.date.InterviewDateError

// 驗證面試時間
val timestamps = mutableListOf(1703123456789L, 1703123400000L)
val errors = DateRuleChecker.checkDates(timestamps)

// 處理驗證結果
errors.forEach { error ->
    when (error) {
        InterviewDateError.NONE -> // 有效時間
        InterviewDateError.MUST -> // 必填錯誤
        InterviewDateError.DATE_EXPIRED -> // 過期錯誤  
        InterviewDateError.INTERVIEW_DATE_REPEAT -> // 重複錯誤
    }
}
```

### 平台資訊

```kotlin
import com.m104atsp.foundation.getPlatform

val platform = getPlatform()
val platformInfo = "Running on ${platform.name} ${platform.version}"
// 輸出: "Running on Android 34" 或 "Running on iOS 17.0"
```

## 📖 文檔

- **[中文文檔庫](./docs/README.md)** - 完整的中文使用指南
- **[CLAUDE.md](./CLAUDE.md)** - Claude Code 開發指引
- **[日期驗證詳細文檔](./foundation/src/commonMain/kotlin/com/m104atsp/foundation/conmunication/date/DateRuleChecker.md)**

## 🧪 測試

```bash
# 執行所有測試
./gradlew test

# Android 測試
./gradlew :foundation:testDebugUnitTest

# iOS 測試  
./gradlew :foundation:iosSimulatorArm64Test
```

## 📋 專案狀態

- ✅ **基礎模組**：核心業務邏輯基礎架構
- ✅ **日期驗證**：面試排程驗證系統
- ✅ **平台抽象**：Android 和 iOS 實現
- ✅ **測試覆蓋**：業務邏輯單元測試
- 🔄 **進行中**：額外業務邏輯組件

## 🤝 貢獻指引

1. **遵循架構**：僅業務邏輯，無 UI 依賴
2. **使用 expect/actual**：平台特定實現
3. **撰寫測試**：包含完整測試覆蓋
4. **更新文檔**：維護相關文檔檔案

## 📄 授權

本專案採用 MIT 授權條款

## 🔗 相關連結

- [Kotlin Multiplatform 文檔](https://kotlinlang.org/docs/multiplatform.html)
- [kotlinx-datetime 文檔](https://github.com/Kotlin/kotlinx-datetime)