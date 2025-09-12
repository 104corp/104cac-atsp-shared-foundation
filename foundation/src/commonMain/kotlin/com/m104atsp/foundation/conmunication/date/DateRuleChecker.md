# InterviewDateRuleChecker

面試時間驗證器，提供面試時間清單的完整驗證功能，支援基本驗證和協作面試時段驗證。

## 概述

`InterviewDateRuleChecker` 是一個單例物件(object)，專門用於驗證面試時間清單的合法性。它提供統一的驗證邏輯，根據指定的優先順序檢查時間清單中的各種錯誤狀態，並支援協作面試的時段範圍檢查。

## 主要功能

### 驗證類型

1. **基本驗證** - 適用於一般面試時間選擇場景
   - `checkInterviewDatesPass()` - 返回是否通過驗證
   - `checkInterviewDatesWithErrors()` - 返回詳細錯誤狀態

2. **協作面試驗證** - 適用於需要時段管理的協作面試場景
   - `checkCollaborativeInterviewDatesPass()` - 返回是否通過驗證
   - `checkCollaborativeInterviewDatesWithErrors()` - 返回詳細錯誤狀態

### 時間驗證規則

驗證按照以下優先順序執行：
1. **必填檢查** - 空清單檢查（最高優先級）
2. **過期檢查** - 與系統當前時間比較
3. **重複檢查** - 精確到分鐘的重複時間檢查
4. **範圍檢查** - 面試時間（含持續時間）是否落在可用時段內（僅協作面試）

### 錯誤類型

配合 `InterviewDateError` 枚舉使用：

```kotlin
enum class InterviewDateError {
    NONE,                    // 無錯誤
    MUST,                    // 必填錯誤（最高優先級）
    DATE_EXPIRED,            // 過期錯誤
    INTERVIEW_DATE_REPEAT,   // 重複錯誤
    OUT_OF_RANGE            // 時段範圍超出錯誤（僅協作面試）
}
```

## API 文檔

### 基本驗證方法

#### `checkInterviewDatesPass(timestampList: MutableList<Long>): Boolean`

檢查基本面試時間是否全部通過驗證。

**參數：**
- `timestampList: MutableList<Long>` - 面試時間戳清單（毫秒）

**返回值：**
- `Boolean` - true 表示所有時間都有效，false 表示存在錯誤

#### `checkInterviewDatesWithErrors(timestampList: MutableList<Long>): MutableList<InterviewDateError>`

檢查基本面試時間並返回詳細錯誤狀態。

**參數：**
- `timestampList: MutableList<Long>` - 面試時間戳清單（毫秒）

**返回值：**
- `MutableList<InterviewDateError>` - 對應每個時間戳的錯誤狀態清單

**驗證邏輯：**
1. 空清單檢查：如果清單為空，返回包含 `MUST` 錯誤的清單
2. 過期檢查：將每個時間戳與系統當前時間比較
3. 重複檢查：檢查是否有重複的時間（精確到分鐘）

### 協作面試驗證方法

#### `checkCollaborativeInterviewDatesPass(timestampList: MutableList<Long>, availableTimeList: List<Pair<Long, Long>>, duration: Long): Boolean`

檢查協作面試時間是否全部通過驗證。

**參數：**
- `timestampList: MutableList<Long>` - 面試時間戳清單（毫秒）
- `availableTimeList: List<Pair<Long, Long>>` - 可用時段清單，格式為 Pair(開始時間, 結束時間)
- `duration: Long` - 面試持續時間（毫秒），負數會自動校正為0

**返回值：**
- `Boolean` - true 表示所有時間都有效，false 表示存在錯誤

#### `checkCollaborativeInterviewDatesWithErrors(timestampList: MutableList<Long>, availableTimeList: List<Pair<Long, Long>>, duration: Long): MutableList<InterviewDateError>`

檢查協作面試時間並返回詳細錯誤狀態。

**參數：**
- `timestampList: MutableList<Long>` - 面試時間戳清單（毫秒）
- `availableTimeList: List<Pair<Long, Long>>` - 可用時段清單
- `duration: Long` - 面試持續時間（毫秒），負數會自動校正為0

**返回值：**
- `MutableList<InterviewDateError>` - 對應每個時間戳的錯誤狀態清單

**驗證邏輯：**
1. 空清單檢查：如果清單為空，返回包含 `MUST` 錯誤的清單
2. 過期檢查：將每個時間戳與系統當前時間比較
3. 重複檢查：檢查是否有重複的時間（精確到分鐘）
4. 範圍檢查：確保面試時間 + 持續時間完全落在可用時段內

### 私有輔助方法

#### `Long.toMinutePrecision(): Long`

將時間戳轉換為分鐘精度，去除秒和毫秒部分。

**範例：**
```kotlin
1703123456789L.toMinutePrecision() // 返回: 1703123400000L
```

#### `findDuplicateIndices(list: List<Long>): Set<Int>`

找出清單中重複元素的所有索引位置。

**範例：**
```kotlin
val list = listOf(100L, 200L, 100L, 300L, 200L)
val duplicates = findDuplicateIndices(list) // 返回: setOf(0, 1, 2, 4)
```

#### `isTimeWithinAvailableSlots(startTime: Long, endTime: Long, availableTimeList: List<Pair<Long, Long>>): Boolean`

檢查面試時間是否完全落在可用時段內。

## 使用範例

### 基本驗證

```kotlin
// 建立面試時間清單（時間戳格式）
val interviewTimes = mutableListOf(
    1703123456789L,  // 2023-12-21 10:30:56.789
    1703123400000L,  // 2023-12-21 10:30:00.000 (與第一個相同分鐘)
    1609459200000L   // 2021-01-01 00:00:00.000 (過期時間)
)

// 檢查是否通過驗證
val isValid = InterviewDateRuleChecker.checkInterviewDatesPass(interviewTimes)

// 檢查詳細錯誤狀態
val errors = InterviewDateRuleChecker.checkInterviewDatesWithErrors(interviewTimes)
// 結果可能為: [INTERVIEW_DATE_REPEAT, INTERVIEW_DATE_REPEAT, DATE_EXPIRED]
```

### 協作面試驗證

```kotlin
val interviewTimes = mutableListOf(1703123456789L)
val availableSlots = listOf(
    Pair(1703120000000L, 1703130000000L), // 可用時段1
    Pair(1703140000000L, 1703150000000L)  // 可用時段2
)
val duration = 30 * 60 * 1000L // 30分鐘

// 檢查協作面試時間
val isValid = InterviewDateRuleChecker.checkCollaborativeInterviewDatesPass(
    interviewTimes, availableSlots, duration
)

// 檢查詳細錯誤狀態
val errors = InterviewDateRuleChecker.checkCollaborativeInterviewDatesWithErrors(
    interviewTimes, availableSlots, duration
)
```

## 重要特性

### 時間精度

重複檢查使用**分鐘精度**，意味著：
- `2023-12-21 10:30:00` 和 `2023-12-21 10:30:59` 被視為相同時間
- 秒和毫秒部分會被忽略

### 統一的驗證邏輯

使用統一的 `when` 表達式進行所有驗證檢查：
- 按業務優先級順序執行檢查
- 高優先級錯誤會覆蓋低優先級錯誤
- 清晰的邏輯分支，易於維護

### 錯誤優先級

當同一個時間同時有多種錯誤時：
1. 過期時間優先標記為 `DATE_EXPIRED`
2. 重複檢查只在沒有過期錯誤時生效
3. 範圍檢查只在沒有其他錯誤時生效

### 系統時間依賴

使用 `kotlinx.datetime.Clock.System.now()` 獲取系統當前時間，確保跨平台兼容性。

### 參數容錯性

- 負數的 `duration` 參數會自動校正為 0，不會拋出異常
- 空清單會正確處理並返回 `MUST` 錯誤

## 外部 API 設計

`InterviewDateRuleChecker` 提供完整的驗證 API，適合直接用於外部 AAR 包：
- 詳細的 KDoc 註解，編譯後仍可提供完整的 IDE 提示
- 類型安全的錯誤狀態回傳
- 完善的錯誤描述和業務場景說明
- 便於 Java 互操作的方法設計

## 依賴

- `kotlinx.datetime` - 用於跨平台時間處理
- `InterviewDateError` - 錯誤狀態枚舉類

## 版本資訊

- **版本**: 1.0.0
- **作者**: M104SharedLogic Team
- **更新日期**: 2024年

## 注意事項

1. **時區處理**：系統時間使用當前系統時區
2. **可變清單**：方法參數使用 `MutableList`，注意處理可變性
3. **性能優化**：重複檢查使用 `Set` 提高查詢效率
4. **業務邏輯**：驗證規則按照業務需求設計，可根據實際需要調整