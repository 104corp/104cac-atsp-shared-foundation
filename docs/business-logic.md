# BusinessLogic 業務邏輯

BusinessLogic 類是 M104SharedLogic 的核心業務邏輯入口，負責整合各種跨平台的業務功能，為 Android 和 iOS 應用提供統一的業務處理接口。

## 概述

BusinessLogic 類設計為純業務邏輯層，不包含任何 UI 相關的代碼，專注於：
- 業務規則實現
- 數據處理算法  
- 跨平台功能整合
- 領域特定功能

## 類別結構

### 基本定義

```kotlin
class BusinessLogic {
    private val platform = getPlatform()
    
    fun getPlatformInfo(): String {
        return "Running on ${platform.name} ${platform.version}"
    }
}
```

### 核心特徵

- **無狀態設計**：BusinessLogic 實例不維護複雜狀態
- **平台感知**：透過 Platform 抽象獲取平台信息
- **純業務邏輯**：專注於業務規則，不包含 UI 邏輯

## 現有功能

### 平台資訊獲取

```kotlin
fun getPlatformInfo(): String {
    return "Running on ${platform.name} ${platform.version}"
}
```

**用途：**
- 應用初始化時顯示平台信息
- 日誌記錄和調試
- 平台特定行為的判斷基礎

## 使用範例

### 基本初始化和使用

```kotlin
// 創建業務邏輯實例
val businessLogic = BusinessLogic()

// 獲取平台信息
val platformInfo = businessLogic.getPlatformInfo()
println("平台信息: $platformInfo")
// 輸出: 平台信息: Running on Android 34
```

### 在 Android 中使用

```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var businessLogic: BusinessLogic
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化業務邏輯
        businessLogic = BusinessLogic()
        
        // 使用業務邏輯
        val info = businessLogic.getPlatformInfo()
        findViewById<TextView>(R.id.platformText).text = info
    }
}
```

### 在 iOS 中使用

```swift
import M104Foundation

class ViewController: UIViewController {
    
    private var businessLogic: BusinessLogic!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // 初始化業務邏輯
        businessLogic = BusinessLogic()
        
        // 使用業務邏輯
        let info = businessLogic.getPlatformInfo()
        platformLabel.text = info
    }
}
```

## 擴展業務邏輯

### 添加數據驗證功能

```kotlin
class BusinessLogic {
    private val platform = getPlatform()
    
    fun getPlatformInfo(): String {
        return "Running on ${platform.name} ${platform.version}"
    }
    
    // 添加新的業務邏輯方法
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult.Error("郵箱不能為空")
            !email.contains("@") -> ValidationResult.Error("郵箱格式不正確")
            email.length > 254 -> ValidationResult.Error("郵箱長度過長")
            else -> ValidationResult.Success
        }
    }
    
    fun validatePhoneNumber(phone: String): ValidationResult {
        val cleanPhone = phone.replace("\\D".toRegex(), "")
        return when {
            cleanPhone.isEmpty() -> ValidationResult.Error("手機號不能為空")
            cleanPhone.length < 8 -> ValidationResult.Error("手機號太短")
            cleanPhone.length > 15 -> ValidationResult.Error("手機號太長")
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
```

### 添加數據處理功能

```kotlin
class BusinessLogic {
    // ... 現有代碼 ...
    
    /**
     * 計算兩個日期之間的工作日數量
     */
    fun calculateWorkingDays(startDate: Long, endDate: Long): Int {
        if (startDate >= endDate) return 0
        
        var workingDays = 0
        var currentTime = startDate
        
        while (currentTime < endDate) {
            val instant = Instant.fromEpochMilliseconds(currentTime)
            val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val dayOfWeek = localDate.dayOfWeek
            
            // 排除週末
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workingDays++
            }
            
            currentTime += 24 * 60 * 60 * 1000L // 加一天
        }
        
        return workingDays
    }
    
    /**
     * 格式化文件大小
     */
    fun formatFileSize(sizeInBytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = sizeInBytes.toDouble()
        var unitIndex = 0
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024.0
            unitIndex++
        }
        
        return "%.2f %s".format(size, units[unitIndex])
    }
}
```

### 整合現有組件

```kotlin
class BusinessLogic {
    private val platform = getPlatform()
    
    fun getPlatformInfo(): String {
        return "Running on ${platform.name} ${platform.version}"
    }
    
    /**
     * 整合日期驗證功能
     */
    fun validateInterviewDates(timestamps: List<Long>): InterviewValidationResult {
        val mutableList = timestamps.toMutableList()
        val errors = DateRuleChecker.checkDates(mutableList)
        
        val errorMessages = mutableListOf<String>()
        var hasErrors = false
        
        errors.forEachIndexed { index, error ->
            when (error) {
                InterviewDateError.MUST -> {
                    errorMessages.add("必須選擇至少一個面試時間")
                    hasErrors = true
                }
                InterviewDateError.DATE_EXPIRED -> {
                    errorMessages.add("第 ${index + 1} 個時間已過期")
                    hasErrors = true
                }
                InterviewDateError.INTERVIEW_DATE_REPEAT -> {
                    errorMessages.add("第 ${index + 1} 個時間與其他時間衝突")
                    hasErrors = true
                }
                InterviewDateError.NONE -> {
                    // 無錯誤
                }
            }
        }
        
        return InterviewValidationResult(
            isValid = !hasErrors,
            errors = errorMessages,
            validTimestamps = if (!hasErrors) timestamps else emptyList()
        )
    }
}

data class InterviewValidationResult(
    val isValid: Boolean,
    val errors: List<String>,
    val validTimestamps: List<Long>
)
```

## 架構模式

### 1. 服務層模式

```kotlin
class BusinessLogic {
    // 注入服務依賴
    private val validationService = ValidationService()
    private val dataProcessingService = DataProcessingService()
    private val dateService = DateService()
    
    fun processUserRegistration(userData: UserData): ProcessingResult {
        // 1. 驗證數據
        val validationResult = validationService.validateUserData(userData)
        if (!validationResult.isValid) {
            return ProcessingResult.ValidationFailed(validationResult.errors)
        }
        
        // 2. 處理數據
        val processedData = dataProcessingService.processUserData(userData)
        
        // 3. 返回結果
        return ProcessingResult.Success(processedData)
    }
}
```

### 2. 工廠模式

```kotlin
class BusinessLogic {
    
    fun createValidator(type: ValidatorType): Validator {
        return when (type) {
            ValidatorType.EMAIL -> EmailValidator()
            ValidatorType.PHONE -> PhoneValidator()
            ValidatorType.DATE -> DateValidator()
            ValidatorType.INTERVIEW_DATE -> InterviewDateValidator()
        }
    }
    
    enum class ValidatorType {
        EMAIL, PHONE, DATE, INTERVIEW_DATE
    }
}
```

### 3. 策略模式

```kotlin
class BusinessLogic {
    
    fun processData(data: Any, strategy: ProcessingStrategy): ProcessingResult {
        return strategy.process(data)
    }
}

interface ProcessingStrategy {
    fun process(data: Any): ProcessingResult
}

class AndroidProcessingStrategy : ProcessingStrategy {
    override fun process(data: Any): ProcessingResult {
        // Android 特定處理邏輯
        return ProcessingResult.Success(data)
    }
}

class IOSProcessingStrategy : ProcessingStrategy {
    override fun process(data: Any): ProcessingResult {
        // iOS 特定處理邏輯
        return ProcessingResult.Success(data)
    }
}
```

## 測試策略

### 單元測試

```kotlin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BusinessLogicTest {
    
    private val businessLogic = BusinessLogic()
    
    @Test
    fun testGetPlatformInfo() {
        val info = businessLogic.getPlatformInfo()
        assertTrue(info.isNotEmpty())
        assertTrue(info.contains("Running on"))
    }
    
    @Test
    fun testValidateEmail() {
        // 測試有效郵箱
        val validResult = businessLogic.validateEmail("test@example.com")
        assertEquals(ValidationResult.Success, validResult)
        
        // 測試無效郵箱
        val invalidResult = businessLogic.validateEmail("invalid-email")
        assertTrue(invalidResult is ValidationResult.Error)
    }
    
    @Test
    fun testCalculateWorkingDays() {
        // 測試工作日計算
        val startTime = Clock.System.now().toEpochMilliseconds()
        val endTime = startTime + (7 * 24 * 60 * 60 * 1000L) // 7天後
        
        val workingDays = businessLogic.calculateWorkingDays(startTime, endTime)
        assertTrue(workingDays >= 0)
        assertTrue(workingDays <= 7)
    }
}
```

### 整合測試

```kotlin
class BusinessLogicIntegrationTest {
    
    @Test
    fun testInterviewDateValidationIntegration() {
        val businessLogic = BusinessLogic()
        val futureTimestamp = Clock.System.now().toEpochMilliseconds() + 86400000L
        
        val result = businessLogic.validateInterviewDates(listOf(futureTimestamp))
        
        assertTrue(result.isValid)
        assertEquals(1, result.validTimestamps.size)
        assertTrue(result.errors.isEmpty())
    }
}
```

## 最佳實踐

### 1. 保持無狀態

```kotlin
// ✅ 好的做法：無狀態方法
class BusinessLogic {
    fun processData(input: String): String {
        return input.uppercase()
    }
}

// ❌ 避免：有狀態的設計
class BusinessLogic {
    private var lastProcessedData: String = ""
    
    fun processData(input: String): String {
        lastProcessedData = input // 維護狀態可能導致問題
        return input.uppercase()
    }
}
```

### 2. 依賴注入

```kotlin
// ✅ 好的做法：依賴注入
class BusinessLogic(
    private val dateValidator: DateValidator = DateRuleChecker,
    private val platform: Platform = getPlatform()
) {
    // 業務邏輯實現
}

// ❌ 避免：硬編碼依賴
class BusinessLogic {
    fun validate() {
        val errors = DateRuleChecker.checkDates(...) // 硬編碼依賴
    }
}
```

### 3. 錯誤處理

```kotlin
class BusinessLogic {
    
    fun safeOperation(input: String): Result<String> {
        return try {
            val result = processInput(input)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error("處理失敗: ${e.message}")
        }
    }
    
    private fun processInput(input: String): String {
        // 可能拋出異常的操作
        return input.trim()
    }
}

sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val message: String) : Result<T>()
}
```

## 進階功能範例

### 批次數據處理

```kotlin
class BusinessLogic {
    
    fun batchProcessEmails(emails: List<String>): BatchProcessResult {
        val validEmails = mutableListOf<String>()
        val invalidEmails = mutableListOf<Pair<String, String>>()
        
        emails.forEach { email ->
            when (val result = validateEmail(email)) {
                is ValidationResult.Success -> validEmails.add(email)
                is ValidationResult.Error -> invalidEmails.add(email to result.message)
            }
        }
        
        return BatchProcessResult(
            successCount = validEmails.size,
            failureCount = invalidEmails.size,
            validItems = validEmails,
            failures = invalidEmails
        )
    }
}

data class BatchProcessResult(
    val successCount: Int,
    val failureCount: Int,
    val validItems: List<String>,
    val failures: List<Pair<String, String>>
)
```

### 配置管理

```kotlin
class BusinessLogic {
    
    private val config = AppConfig.forPlatform(getPlatform())
    
    fun getMaxRetryAttempts(): Int = config.maxRetryAttempts
    fun getTimeoutDuration(): Long = config.timeoutMs
    fun isFeatureEnabled(feature: String): Boolean = config.enabledFeatures.contains(feature)
}

data class AppConfig(
    val maxRetryAttempts: Int,
    val timeoutMs: Long,
    val enabledFeatures: Set<String>
) {
    companion object {
        fun forPlatform(platform: Platform): AppConfig {
            return when {
                platform.name.contains("Android") -> AppConfig(
                    maxRetryAttempts = 3,
                    timeoutMs = 5000L,
                    enabledFeatures = setOf("push_notifications", "biometric")
                )
                platform.name.contains("iOS") -> AppConfig(
                    maxRetryAttempts = 5,
                    timeoutMs = 3000L,
                    enabledFeatures = setOf("push_notifications", "face_id")
                )
                else -> AppConfig(
                    maxRetryAttempts = 1,
                    timeoutMs = 10000L,
                    enabledFeatures = emptySet()
                )
            }
        }
    }
}
```

## 效能考量

### 1. 緩存機制

```kotlin
class BusinessLogic {
    
    private val platformInfo: String by lazy {
        "Running on ${getPlatform().name} ${getPlatform().version}"
    }
    
    fun getPlatformInfo(): String = platformInfo // 使用緩存值
}
```

### 2. 批次處理

```kotlin
class BusinessLogic {
    
    fun processLargeDataset(data: List<String>, batchSize: Int = 100): ProcessingResult {
        val results = mutableListOf<String>()
        
        data.chunked(batchSize).forEach { batch ->
            results.addAll(batch.map { item -> processItem(item) })
        }
        
        return ProcessingResult(results)
    }
}
```

## 常見用途

1. **用戶數據驗證**：表單驗證、輸入檢查
2. **業務規則實現**：價格計算、資格檢查  
3. **數據轉換**：格式化、清理、標準化
4. **平台特定邏輯**：根據平台調整行為
5. **整合各組件**：協調不同業務模組

## 相關文檔

- **[Platform 平台抽象](./platform-abstraction.md)** - 了解平台抽象機制
- **[日期驗證系統](./date-validation-system.md)** - 查看具體的業務邏輯實現範例