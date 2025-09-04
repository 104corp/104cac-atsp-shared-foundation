# DateRuleChecker

面試時間驗證器，提供面試時間清單的驗證功能，檢查必填、過期及重複等錯誤狀態。

## 概述

`DateRuleChecker` 是一個單例物件(object)，專門用於驗證面試時間清單的合法性。它會根據指定的優先順序檢查時間清單中的各種錯誤狀態。

## 主要功能

### 時間驗證規則

驗證按照以下優先順序執行：
1. **必填檢查** - 空清單檢查
2. **過期檢查** - 與系統當前時間比較
3. **重複檢查** - 精確到分鐘的重複時間檢查

### 錯誤類型

配合 `InterviewDateError` 枚舉使用：

```kotlin
enum class InterviewDateError {
    NONE,                    // 無錯誤
    MUST,                    // 必填錯誤
    DATE_EXPIRED,            // 過期錯誤
    INTERVIEW_DATE_REPEAT    // 重複錯誤
}
```

## API 文檔

### 主要方法

#### `checkDates(list: MutableList<Long>): MutableList<InterviewDateError>`

檢查面試時間清單的錯誤狀態。

**參數：**
- `list: MutableList<Long>` - 面試時間戳清單（毫秒）

**返回值：**
- `MutableList<InterviewDateError>` - 對應每個時間戳的錯誤狀態清單

**驗證邏輯：**
1. 空清單檢查：如果清單為空，返回包含 `MUST` 錯誤的清單
2. 過期檢查：將每個時間戳與系統當前時間比較
3. 重複檢查：檢查是否有重複的時間（精確到分鐘）

### 輔助方法

#### `Long.toMinutePrecision(): Long`

將時間戳轉換為分鐘精度，去除秒和毫秒部分。

**範例：**
```kotlin
1703123456789L.toMinutePrecision() // 返回: 1703123400000L
```

#### `findDuplicateIndices(list: List<Long>): List<Int>`

找出清單中重複元素的所有索引位置。

**範例：**
```kotlin
val list = listOf(100L, 200L, 100L, 300L, 200L)
val duplicates = findDuplicateIndices(list) // 返回: [0, 1, 2, 4]
```

## 使用範例

```kotlin
// 建立面試時間清單（時間戳格式）
val interviewTimes = mutableListOf(
    1703123456789L,  // 2023-12-21 10:30:56.789
    1703123400000L,  // 2023-12-21 10:30:00.000 (與第一個相同分鐘)
    1609459200000L   // 2021-01-01 00:00:00.000 (過期時間)
)

// 檢查錯誤狀態
val errors = DateRuleChecker.checkDates(interviewTimes)
// 結果可能為: [INTERVIEW_DATE_REPEAT, INTERVIEW_DATE_REPEAT, DATE_EXPIRED]
```

## 重要特性

### 時間精度

重複檢查使用**分鐘精度**，意味著：
- `2023-12-21 10:30:00` 和 `2023-12-21 10:30:59` 被視為相同時間
- 秒和毫秒部分會被忽略

### 錯誤優先級

當同一個時間同時有多種錯誤時：
- 過期時間優先標記為 `DATE_EXPIRED`
- 重複檢查**不會覆蓋**已有的過期錯誤
- 只有 `NONE` 狀態的時間才會被標記為重複錯誤

### 系統時間依賴

使用 `kotlinx.datetime.Clock.System.now()` 獲取系統當前時間，確保跨平台兼容性。

## 依賴

- `kotlinx.datetime` - 用於跨平台時間處理
- `InterviewDateError` - 錯誤狀態枚舉類

## 注意事項

1. **私有方法**：所有方法都是私有的，表示這可能是內部實現，實際使用可能需要公開相關方法
2. **可變清單**：方法參數和返回值都使用 `MutableList`，注意處理可變性
3. **時區處理**：系統時間使用 UTC，如需本地時間可能需要額外處理