# 日期驗證系統

M104SharedLogic 中的面試時間驗證系統，提供完整的面試排程驗證功能，包含必填檢查、過期驗證和重複偵測等業務規則。

## 系統概述

日期驗證系統由兩個核心組件組成：
- **DateRuleChecker**：驗證邏輯的核心引擎
- **InterviewDateError**：錯誤狀態枚舉

這個系統專門為面試排程場景設計，實現精確到分鐘的重複檢查和基於系統時間的過期驗證。

## 核心組件

### DateRuleChecker 驗證引擎

```kotlin
object DateRuleChecker {
    fun checkDates(list: MutableList<Long>): MutableList<InterviewDateError>
}
```

**設計特點：**
- 單例物件設計，全域唯一實例
- 接受毫秒時間戳清單作為輸入
- 返回對應每個時間戳的錯誤狀態

### InterviewDateError 錯誤類型

```kotlin
enum class InterviewDateError {
    NONE,                    // 無錯誤
    MUST,                    // 必填錯誤
    DATE_EXPIRED,            // 過期錯誤  
    INTERVIEW_DATE_REPEAT    // 重複錯誤
}
```

## 驗證規則詳解

### 優先順序系統

驗證按照以下優先順序執行：

1. **必填檢查** (最高優先級)
2. **過期檢查** (中等優先級)  
3. **重複檢查** (最低優先級)

**重要：** 重複檢查 **不會覆蓋** 過期錯誤，已標記為過期的時間不會被改為重複錯誤。

### 1. 必填檢查

```kotlin
// 空清單檢查
if (list.isEmpty()) return mutableListOf(InterviewDateError.MUST)
```

**觸發條件：**
- 輸入的時間戳清單為空

**使用範例：**
```kotlin
val emptyList = mutableListOf<Long>()
val errors = DateRuleChecker.checkDates(emptyList)
// 結果: [InterviewDateError.MUST]
```

### 2. 過期檢查

```kotlin
val currentTime = Clock.System.now().toEpochMilliseconds()

val errorList = list.map { timestamp ->
    when {
        timestamp < currentTime -> InterviewDateError.DATE_EXPIRED
        else -> InterviewDateError.NONE
    }
}.toMutableList()
```

**觸發條件：**
- 時間戳小於系統當前時間

**使用範例：**
```kotlin
import kotlinx.datetime.Clock

val currentTime = Clock.System.now().toEpochMilliseconds()
val pastTime = currentTime - 86400000L // 昨天
val futureTime = currentTime + 86400000L // 明天

val timestamps = mutableListOf(pastTime, futureTime)
val errors = DateRuleChecker.checkDates(timestamps)
// 結果: [DATE_EXPIRED, NONE]
```

### 3. 重複檢查

系統使用 **分鐘精度** 進行重複檢查，忽略秒和毫秒部分：

```kotlin
private fun Long.toMinutePrecision(): Long = (this / 60000) * 60000
```

**精度轉換範例：**
```kotlin
1703123456789L.toMinutePrecision() // 1703123400000L
// 原始: 2023-12-21 10:30:56.789
// 轉換: 2023-12-21 10:30:00.000
```

**重複偵測算法：**
```kotlin
private fun findDuplicateIndices(list: List<Long>): List<Int> {
    val indexMap = list.withIndex().groupBy { it.value }
    return indexMap.values
        .filter { it.size > 1 }  // 找出出現次數 > 1 的值
        .flatten()               // 攤平所有重複的 IndexedValue
        .map { it.index }        // 提取索引
}
```

**使用範例：**
```kotlin
val currentTime = Clock.System.now().toEpochMilliseconds()
val futureBase = currentTime + 86400000L

// 相同分鐘的時間戳
val timestamp1 = futureBase
val timestamp2 = futureBase + 30000L // +30 秒，視為相同分鐘

val timestamps = mutableListOf(timestamp1, timestamp2)
val errors = DateRuleChecker.checkDates(timestamps)
// 結果: [INTERVIEW_DATE_REPEAT, INTERVIEW_DATE_REPEAT]
```

## 完整使用範例

### 基本驗證流程

```kotlin
import com.m104atsp.foundation.conmunication.date.DateRuleChecker
import com.m104atsp.foundation.conmunication.date.InterviewDateError
import kotlinx.datetime.Clock

fun validateInterviewSchedule() {
    val currentTime = Clock.System.now().toEpochMilliseconds()
    
    // 準備測試數據
    val interviews = mutableListOf(
        currentTime + 3600000L,   // 1小時後 (有效)
        currentTime + 3630000L,   // 1小時30秒後 (與上一個重複，同一分鐘)
        currentTime - 3600000L    // 1小時前 (過期)
    )
    
    // 執行驗證
    val errors = DateRuleChecker.checkDates(interviews)
    
    // 處理結果
    interviews.forEachIndexed { index, timestamp ->
        val error = errors[index]
        val message = when (error) {
            InterviewDateError.NONE -> "✅ 有效時間"
            InterviewDateError.MUST -> "❌ 必填欄位"
            InterviewDateError.DATE_EXPIRED -> "❌ 時間已過期"
            InterviewDateError.INTERVIEW_DATE_REPEAT -> "❌ 時間重複"
        }
        
        println("面試時間 $index: $message")
    }
}
```

### 錯誤處理和用戶提示

```kotlin
class InterviewValidator {
    
    fun validateAndGetErrorMessages(timestamps: MutableList<Long>): ValidationResult {
        val errors = DateRuleChecker.checkDates(timestamps)
        val errorMessages = mutableListOf<String>()
        var hasErrors = false
        
        errors.forEachIndexed { index, error ->
            when (error) {
                InterviewDateError.MUST -> {
                    errorMessages.add("請至少選擇一個面試時間")
                    hasErrors = true
                }
                InterviewDateError.DATE_EXPIRED -> {
                    errorMessages.add("第 ${index + 1} 個選擇的時間已過期，請選擇未來的時間")
                    hasErrors = true
                }
                InterviewDateError.INTERVIEW_DATE_REPEAT -> {
                    errorMessages.add("第 ${index + 1} 個時間與其他選擇重複，請選擇不同的時間")
                    hasErrors = true
                }
                InterviewDateError.NONE -> {
                    // 無錯誤，不需要處理
                }
            }
        }
        
        return ValidationResult(
            isValid = !hasErrors,
            errors = errorMessages,
            validTimestamps = if (!hasErrors) timestamps else emptyList()
        )
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>,
    val validTimestamps: List<Long>
)
```

### 表單驗證整合

```kotlin
class InterviewScheduleForm {
    
    private val validator = InterviewValidator()
    private val selectedTimestamps = mutableListOf<Long>()
    
    fun addInterviewTime(timestamp: Long): Boolean {
        selectedTimestamps.add(timestamp)
        return validateCurrentSelection()
    }
    
    fun removeInterviewTime(index: Int): Boolean {
        if (index in selectedTimestamps.indices) {
            selectedTimestamps.removeAt(index)
            return validateCurrentSelection()
        }
        return false
    }
    
    private fun validateCurrentSelection(): Boolean {
        val result = validator.validateAndGetErrorMessages(selectedTimestamps)
        
        if (!result.isValid) {
            // 顯示錯誤訊息給用戶
            showErrorMessages(result.errors)
            return false
        }
        
        // 清除錯誤訊息
        clearErrorMessages()
        return true
    }
    
    fun submitSchedule(): SubmissionResult {
        val result = validator.validateAndGetErrorMessages(selectedTimestamps)
        
        return if (result.isValid) {
            SubmissionResult.Success(result.validTimestamps)
        } else {
            SubmissionResult.ValidationFailed(result.errors)
        }
    }
    
    private fun showErrorMessages(errors: List<String>) {
        errors.forEach { println("錯誤: $it") }
    }
    
    private fun clearErrorMessages() {
        println("驗證通過")
    }
}

sealed class SubmissionResult {
    data class Success(val timestamps: List<Long>) : SubmissionResult()
    data class ValidationFailed(val errors: List<String>) : SubmissionResult()
}
```

## Android 平台整合

### MVVM 架構整合

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

class InterviewViewModel : ViewModel() {
    
    private val _selectedDates = MutableLiveData<List<Long>>()
    val selectedDates: LiveData<List<Long>> = _selectedDates
    
    private val _validationErrors = MutableLiveData<List<String>>()
    val validationErrors: LiveData<List<String>> = _validationErrors
    
    private val _isValid = MutableLiveData<Boolean>()
    val isValid: LiveData<Boolean> = _isValid
    
    fun addInterviewDate(timestamp: Long) {
        val current = _selectedDates.value?.toMutableList() ?: mutableListOf()
        current.add(timestamp)
        validateDates(current)
    }
    
    fun removeInterviewDate(index: Int) {
        val current = _selectedDates.value?.toMutableList() ?: return
        if (index in current.indices) {
            current.removeAt(index)
            validateDates(current)
        }
    }
    
    private fun validateDates(dates: MutableList<Long>) {
        val errors = DateRuleChecker.checkDates(dates)
        val errorMessages = mutableListOf<String>()
        
        errors.forEachIndexed { index, error ->
            when (error) {
                InterviewDateError.MUST -> 
                    errorMessages.add("必須至少選擇一個面試時間")
                InterviewDateError.DATE_EXPIRED -> 
                    errorMessages.add("第 ${index + 1} 個時間已過期")
                InterviewDateError.INTERVIEW_DATE_REPEAT -> 
                    errorMessages.add("第 ${index + 1} 個時間重複")
                InterviewDateError.NONE -> {
                    // 無錯誤
                }
            }
        }
        
        _selectedDates.value = dates
        _validationErrors.value = errorMessages
        _isValid.value = errorMessages.isEmpty()
    }
}
```

### Activity 使用範例

```kotlin
class InterviewActivity : AppCompatActivity() {
    
    private lateinit var viewModel: InterviewViewModel
    private lateinit var binding: ActivityInterviewBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[InterviewViewModel::class.java]
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.validationErrors.observe(this) { errors ->
            if (errors.isNotEmpty()) {
                binding.errorText.text = errors.joinToString("\\n")
                binding.errorText.visibility = View.VISIBLE
            } else {
                binding.errorText.visibility = View.GONE
            }
        }
        
        viewModel.isValid.observe(this) { isValid ->
            binding.submitButton.isEnabled = isValid
        }
    }
    
    private fun setupClickListeners() {
        binding.addDateButton.setOnClickListener {
            showDateTimePicker()
        }
        
        binding.submitButton.setOnClickListener {
            submitInterviewSchedule()
        }
    }
    
    private fun showDateTimePicker() {
        // 顯示日期時間選擇器
        val calendar = Calendar.getInstance()
        
        DatePickerDialog(this, { _, year, month, day ->
            TimePickerDialog(this, { _, hour, minute ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, day, hour, minute, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                
                val timestamp = selectedCalendar.timeInMillis
                viewModel.addInterviewDate(timestamp)
            }, 9, 0, true).show()
        }, 
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH), 
        calendar.get(Calendar.DAY_OF_MONTH)).apply {
            // 不允許選擇過去的日期
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }
    
    private fun submitInterviewSchedule() {
        // 提交面試時程
        val dates = viewModel.selectedDates.value ?: return
        Toast.makeText(this, "已提交 ${dates.size} 個面試時間", Toast.LENGTH_SHORT).show()
    }
}
```

## iOS 平台整合

### Swift 使用範例

```swift
import M104Foundation

class InterviewViewController: UIViewController {
    
    @IBOutlet weak var errorLabel: UILabel!
    @IBOutlet weak var submitButton: UIButton!
    
    private var selectedTimestamps: [Int64] = []
    
    @IBAction func addDateTapped(_ sender: UIButton) {
        showDatePicker()
    }
    
    @IBAction func submitTapped(_ sender: UIButton) {
        if validateDates() {
            submitInterviewSchedule()
        }
    }
    
    private func showDatePicker() {
        let alert = UIAlertController(title: "選擇面試時間", message: nil, preferredStyle: .actionSheet)
        
        let datePicker = UIDatePicker()
        datePicker.datePickerMode = .dateAndTime
        datePicker.minimumDate = Date() // 不允許過去時間
        
        alert.setValue(datePicker, forKey: "contentViewController")
        
        alert.addAction(UIAlertAction(title: "確認", style: .default) { _ in
            let timestamp = Int64(datePicker.date.timeIntervalSince1970 * 1000)
            self.selectedTimestamps.append(timestamp)
            self.validateDates()
        })
        
        alert.addAction(UIAlertAction(title: "取消", style: .cancel))
        
        present(alert, animated: true)
    }
    
    @discardableResult
    private func validateDates() -> Bool {
        // 轉換為 Kotlin MutableList
        let kotlinList = KotlinMutableArray<KotlinLong>(size: Int32(selectedTimestamps.count)) { index in
            KotlinLong(value: selectedTimestamps[Int(index)])
        }
        
        // 執行驗證
        let errors = DateRuleChecker.shared.checkDates(list: kotlinList)
        var errorMessages: [String] = []
        
        for i in 0..<errors.size {
            let error = errors.get(index: i) as! InterviewDateError
            
            switch error {
            case .must:
                errorMessages.append("請至少選擇一個面試時間")
            case .dateExpired:
                errorMessages.append("第 \\(i + 1) 個時間已過期")
            case .interviewDateRepeat:
                errorMessages.append("第 \\(i + 1) 個時間重複")
            case .none:
                break
            default:
                break
            }
        }
        
        // 更新 UI
        DispatchQueue.main.async {
            if errorMessages.isEmpty {
                self.errorLabel.text = ""
                self.errorLabel.isHidden = true
                self.submitButton.isEnabled = true
            } else {
                self.errorLabel.text = errorMessages.joined(separator: "\\n")
                self.errorLabel.isHidden = false
                self.submitButton.isEnabled = false
            }
        }
        
        return errorMessages.isEmpty
    }
    
    private func submitInterviewSchedule() {
        print("提交 \\(selectedTimestamps.count) 個面試時間")
        // 執行提交邏輯
    }
}
```

## 測試策略

### 單元測試

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.datetime.Clock

class DateRuleCheckerTest {
    
    @Test
    fun testEmptyList_returnsRequiredError() {
        val emptyList = mutableListOf<Long>()
        val errors = DateRuleChecker.checkDates(emptyList)
        
        assertEquals(1, errors.size)
        assertEquals(InterviewDateError.MUST, errors[0])
    }
    
    @Test
    fun testFutureDates_returnsNoError() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val futureTime = currentTime + 86400000L // 明天
        
        val timestamps = mutableListOf(futureTime)
        val errors = DateRuleChecker.checkDates(timestamps)
        
        assertEquals(1, errors.size)
        assertEquals(InterviewDateError.NONE, errors[0])
    }
    
    @Test
    fun testPastDates_returnsExpiredError() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val pastTime = currentTime - 86400000L // 昨天
        
        val timestamps = mutableListOf(pastTime)
        val errors = DateRuleChecker.checkDates(timestamps)
        
        assertEquals(1, errors.size)
        assertEquals(InterviewDateError.DATE_EXPIRED, errors[0])
    }
    
    @Test
    fun testDuplicateDates_sameMinute_returnsRepeatError() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val futureBase = currentTime + 86400000L
        
        // 相同分鐘的兩個時間戳
        val timestamp1 = futureBase
        val timestamp2 = futureBase + 30000L // +30秒
        
        val timestamps = mutableListOf(timestamp1, timestamp2)
        val errors = DateRuleChecker.checkDates(timestamps)
        
        assertEquals(2, errors.size)
        assertEquals(InterviewDateError.INTERVIEW_DATE_REPEAT, errors[0])
        assertEquals(InterviewDateError.INTERVIEW_DATE_REPEAT, errors[1])
    }
    
    @Test
    fun testDuplicateDates_differentMinute_returnsNoError() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val futureBase = currentTime + 86400000L
        
        // 不同分鐘的兩個時間戳
        val timestamp1 = futureBase
        val timestamp2 = futureBase + 60000L // +1分鐘
        
        val timestamps = mutableListOf(timestamp1, timestamp2)
        val errors = DateRuleChecker.checkDates(timestamps)
        
        assertEquals(2, errors.size)
        assertEquals(InterviewDateError.NONE, errors[0])
        assertEquals(InterviewDateError.NONE, errors[1])
    }
    
    @Test
    fun testPrioritySystem_expiredNotOverriddenByDuplicate() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val pastTime1 = currentTime - 86400000L // 昨天
        val pastTime2 = pastTime1 + 30000L // 昨天 +30秒 (相同分鐘)
        
        val timestamps = mutableListOf(pastTime1, pastTime2)
        val errors = DateRuleChecker.checkDates(timestamps)
        
        // 應該保持為過期錯誤，不會被重複錯誤覆蓋
        assertEquals(2, errors.size)
        assertEquals(InterviewDateError.DATE_EXPIRED, errors[0])
        assertEquals(InterviewDateError.DATE_EXPIRED, errors[1])
    }
}
```

### 整合測試

```kotlin
class DateValidationIntegrationTest {
    
    @Test
    fun testComplexScenario() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        
        val timestamps = mutableListOf(
            currentTime + 3600000L,    // 1小時後 (有效)
            currentTime + 3630000L,    // 1小時30秒後 (重複)
            currentTime - 1800000L,    // 30分鐘前 (過期)
            currentTime + 7200000L     // 2小時後 (有效)
        )
        
        val errors = DateRuleChecker.checkDates(timestamps)
        
        assertEquals(4, errors.size)
        assertEquals(InterviewDateError.INTERVIEW_DATE_REPEAT, errors[0]) // 重複
        assertEquals(InterviewDateError.INTERVIEW_DATE_REPEAT, errors[1]) // 重複
        assertEquals(InterviewDateError.DATE_EXPIRED, errors[2])          // 過期
        assertEquals(InterviewDateError.NONE, errors[3])                  // 有效
    }
}
```

## 效能優化

### 大量數據處理

對於大量時間戳的處理，可以考慮分批驗證：

```kotlin
class OptimizedDateValidator {
    
    fun validateLargeDataset(
        timestamps: List<Long>, 
        batchSize: Int = 1000
    ): List<InterviewDateError> {
        val results = mutableListOf<InterviewDateError>()
        
        timestamps.chunked(batchSize).forEach { batch ->
            val mutableBatch = batch.toMutableList()
            val batchErrors = DateRuleChecker.checkDates(mutableBatch)
            results.addAll(batchErrors)
        }
        
        return results
    }
}
```

### 緩存優化

```kotlin
class CachedDateValidator {
    
    private val validationCache = mutableMapOf<List<Long>, List<InterviewDateError>>()
    
    fun validateWithCache(timestamps: List<Long>): List<InterviewDateError> {
        return validationCache.getOrPut(timestamps) {
            val mutableList = timestamps.toMutableList()
            DateRuleChecker.checkDates(mutableList)
        }
    }
}
```

## 常見問題解答

### Q: 為什麼重複檢查只精確到分鐘？
A: 這是基於面試排程的實際需求設計的。在面試安排中，通常不需要區分同一分鐘內的不同秒數，這樣的設計更符合實際使用場景。

### Q: 能否自定義驗證規則的優先順序？
A: 目前的實現是固定的優先順序。如果需要自定義，建議擴展 DateRuleChecker 或創建新的驗證器。

### Q: 如何處理時區問題？
A: 系統使用 `kotlinx-datetime` 的 `Clock.System.now()` 獲取 UTC 時間。建議在傳入時間戳前，先將本地時間轉換為統一的時區。

### Q: 驗證器是否線程安全？
A: 是的，DateRuleChecker 是無狀態的單例對象，可以安全地在多線程環境中使用。

## 未來擴展方向

1. **自定義驗證規則**：支持可配置的驗證邏輯
2. **時區支持**：增加時區感知的驗證功能  
3. **批次優化**：針對大數據集的性能優化
4. **錯誤細化**：提供更詳細的錯誤信息

## 相關文檔

- **[Platform 平台抽象](./platform-abstraction.md)** - 了解跨平台抽象機制
- **[CLAUDE.md](../CLAUDE.md)** - 查看專案整體架構和開發指引
- **[DateRuleChecker.md](../foundation/src/commonMain/kotlin/com/m104atsp/foundation/conmunication/date/DateRuleChecker.md)** - 原始詳細文檔