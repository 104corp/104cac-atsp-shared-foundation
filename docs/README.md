# M104SharedLogic 文件庫

歡迎來到 M104SharedLogic 的中文文檔庫。這裡專注於介紹 `commonMain` 模組下的核心業務邏輯組件。

## 📚 文檔概述

### 核心業務組件
- **[Platform 平台抽象](./platform-abstraction.md)** - 跨平台抽象接口設計
- **[日期驗證系統](./date-validation-system.md)** - 面試時間驗證完整指南

### 快速導航

**新手入門？** 建議從 [Platform 平台抽象](./platform-abstraction.md) 開始了解

**需要驗證日期？** 查看 [日期驗證系統](./date-validation-system.md)

**想了解整體架構？** 參考專案根目錄的 [CLAUDE.md](../CLAUDE.md)

## 🏗️ commonMain 模組結構

```
foundation/src/commonMain/kotlin/com/m104atsp/foundation/
├── Platform.kt                    # 平台抽象接口
└── conmunication/date/            # 日期相關業務邏輯
    ├── DateRuleChecker.kt         # 日期驗證核心邏輯
    └── InterviewDateError.kt      # 驗證錯誤類型枚舉
```

## 📖 文檔特色

- **中文為主**：所有文檔以中文為主要語言
- **代碼示例**：包含完整的 Kotlin 代碼範例
- **業務導向**：專注於業務邏輯實現和使用方式
- **實用性強**：提供實際開發中的使用場景

## 🚀 項目信息

- **項目名稱**：M104SharedLogic
- **類型**：Kotlin Multiplatform 業務邏輯庫
- **支持平台**：Android (API 24+)、iOS (arm64 + Simulator)
- **語言版本**：Kotlin 2.0.0
- **主要依賴**：kotlinx-datetime 0.6.1

## 🤝 文檔維護

如果你發現文檔有需要改進的地方：
1. 發現錯誤？請回報或提交修正
2. 內容不足？建議新增相關資訊
3. 說明不清？幫助我們改善表達

## 📱 相關連結

- **[項目主頁](../README.md)** - 項目總體介紹
- **[CLAUDE.md](../CLAUDE.md)** - Claude Code 開發指引
- **[DateRuleChecker.md](../foundation/src/commonMain/kotlin/com/m104atsp/foundation/conmunication/date/DateRuleChecker.md)** - 日期驗證詳細文檔