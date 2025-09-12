package com.m104atsp.foundation.conmunication.date

import kotlinx.datetime.*
import kotlin.native.ObjCName
import kotlin.experimental.ExperimentalObjCName

/**
 * 面試時間驗證結果
 * 
 * 封裝面試時間驗證的結果，提供便利的方法來處理驗證結果。
 * 專為跨平台使用設計，同時支援 Android AAR 和 iOS XCFramework。
 * 即使在編譯後的 AAR/XCFramework 中使用，也能獲得完整的類型提示和方法說明。
 * 
 * @param isValid 是否通過所有驗證
 * @param errors 錯誤列表，與輸入時間戳一一對應
 * @param errorMessage 錯誤摘要信息
 * 
 * @since 1.0.0
 * @author M104SharedLogic Team
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("InterviewDateValidationResult")
data class InterviewDateValidationResult(
    /** 是否通過所有驗證 */
    @ObjCName("isValid")
    val isValid: Boolean,
    
    /** 錯誤列表，與輸入時間戳一一對應 */
    @ObjCName("errors")
    val errors: List<InterviewDateError>,
    
    /** 錯誤摘要信息 */
    @ObjCName("errorMessage")
    val errorMessage: String
) {
    /**
     * 獲取第一個錯誤
     * 
     * @return 第一個非 NONE 的錯誤，如果沒有錯誤則返回 NONE
     * @since 1.0.0
     */
    @ObjCName("getFirstError")
    fun getFirstError(): InterviewDateError = 
        errors.firstOrNull { it != InterviewDateError.NONE } ?: InterviewDateError.NONE
    
    /**
     * 獲取錯誤數量
     * 
     * @return 錯誤的數量（不包括 NONE）
     * @since 1.0.0
     */
    @ObjCName("getErrorCount")
    fun getErrorCount(): Int = errors.count { it != InterviewDateError.NONE }
    
    /**
     * 檢查是否包含特定錯誤類型
     * 
     * @param errorType 要檢查的錯誤類型
     * @return 是否包含該錯誤類型
     * @since 1.0.0
     */
    @ObjCName("hasErrorType")
    fun hasError(errorType: InterviewDateError): Boolean = errors.contains(errorType)
    
    /**
     * 檢查是否有必填錯誤
     * 
     * 便利方法，用於快速檢查是否存在必填錯誤。
     * 
     * @return 是否存在必填錯誤
     * @since 1.0.0
     */
    @ObjCName("hasMustError")
    fun hasMustError(): Boolean = errors.contains(InterviewDateError.MUST)
    
    /**
     * 檢查是否有過期錯誤
     * 
     * 便利方法，用於快速檢查是否存在過期錯誤。
     * 
     * @return 是否存在過期錯誤
     * @since 1.0.0
     */
    @ObjCName("hasExpiredError")
    fun hasExpiredError(): Boolean = errors.contains(InterviewDateError.DATE_EXPIRED)
    
    /**
     * 檢查是否有重複錯誤
     * 
     * 便利方法，用於快速檢查是否存在重複錯誤。
     * 
     * @return 是否存在重複錯誤
     * @since 1.0.0
     */
    @ObjCName("hasRepeatError")
    fun hasRepeatError(): Boolean = errors.contains(InterviewDateError.INTERVIEW_DATE_REPEAT)
    
    /**
     * 檢查是否有範圍錯誤
     * 
     * 便利方法，用於快速檢查是否存在範圍錯誤。
     * 僅在協作面試驗證中可能出現此錯誤。
     * 
     * @return 是否存在範圍錯誤
     * @since 1.0.0
     */
    @ObjCName("hasOutOfRangeError")
    fun hasOutOfRangeError(): Boolean = errors.contains(InterviewDateError.OUT_OF_RANGE)
    
    /**
     * 獲取所有錯誤的描述
     * 
     * @return 所有錯誤的描述列表（不包括無錯誤狀態）
     * @since 1.0.0
     */
    @ObjCName("getAllErrorDescriptions")
    fun getAllErrorDescriptions(): List<String> = 
        errors.filter { it != InterviewDateError.NONE }
             .map { getErrorDescription(it) }
    
    /**
     * 獲取錯誤的本地化描述
     * 
     * @param error 錯誤類型
     * @return 錯誤的中文描述
     * @since 1.0.0
     */
    @ObjCName("getErrorDescription")
    private fun getErrorDescription(error: InterviewDateError): String = when (error) {
        InterviewDateError.NONE -> "無錯誤"
        InterviewDateError.MUST -> "面試時間為必填項目"
        InterviewDateError.DATE_EXPIRED -> "面試時間已過期"
        InterviewDateError.INTERVIEW_DATE_REPEAT -> "面試時間重複"
        InterviewDateError.OUT_OF_RANGE -> "面試時間超出可用範圍"
    }
}

/**
 * 跨平台面試時間驗證器 - 統一 API
 * 
 * 提供簡潔且類型安全的面試時間驗證 API，同時針對 Android AAR 和 iOS XCFramework 優化。
 * 所有方法都包含詳細的文檔注釋，即使編譯為 AAR/XCFramework 後仍能提供良好的開發體驗。
 * 
 * 主要特性：
 * - 統一的跨平台 API 設計
 * - 類型安全的錯誤狀態回傳
 * - 詳細的錯誤描述和業務場景說明
 * - 便於 Java/Swift 互操作的方法設計
 * - 完整的 KDoc 註解支援 IDE 提示
 * - 豐富的便利方法和工具函數
 * 
 * @since 1.0.0
 * @author M104SharedLogic Team
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("InterviewDateValidator")
object InterviewDateValidator {

    /**
     * 驗證基本面試時間列表
     * 
     * 執行基本的面試時間驗證，包括必填檢查、過期檢查和重複檢查。
     * 不包含時段範圍驗證，適用於一般面試時間選擇場景。
     * 
     * 驗證規則：
     * 1. 必填檢查：清單不能為空
     * 2. 過期檢查：時間不能早於當前系統時間
     * 3. 重複檢查：同一分鐘內不能有多個時間
     * 
     * @param timestampList 面試時間戳列表（毫秒），Unix 時間戳格式
     * @return 驗證結果，包含是否有效、錯誤列表和錯誤摘要
     * 
     * 使用範例：
     * ```kotlin
     * val timestamps = listOf(1703123456789L, 1703127056789L)
     * val result = InterviewDateValidator.validateBasicInterviewDates(timestamps)
     * 
     * if (result.isValid) {
     *     // 處理有效的面試時間
     *     println("面試時間驗證通過")
     * } else {
     *     // 處理驗證錯誤
     *     println("驗證失敗：${result.errorMessage}")
     *     result.getAllErrorDescriptions().forEach { println(it) }
     * }
     * ```
     * 
     * @since 1.0.0
     * @see InterviewDateValidationResult
     * @see InterviewDateError
     */
    @ObjCName("validateBasicInterviewDates")
    fun validateBasicInterviewDates(timestampList: List<Long>): InterviewDateValidationResult {
        val errors = InterviewDateRuleChecker.checkInterviewDatesWithErrors(timestampList)
        val isValid = errors.all { it == InterviewDateError.NONE }
        val errorMessage = if (isValid) {
            "驗證通過"
        } else {
            val uniqueErrors = errors.filterNot { it == InterviewDateError.NONE }
                .distinct()
                .map { getErrorDescription(it) }
                .joinToString(", ")
            "驗證失敗：$uniqueErrors"
        }
        
        return InterviewDateValidationResult(isValid, errors, errorMessage)
    }

    /**
     * 驗證協作面試時間列表
     * 
     * 執行完整的協作面試時間驗證，除了基本驗證外，還包含時段範圍檢查。
     * 確保面試時間（包含持續時間）完全落在可用時段內，適用於需要時段管理的協作面試場景。
     * 
     * 驗證規則：
     * 1. 必填檢查：清單不能為空
     * 2. 過期檢查：時間不能早於當前系統時間
     * 3. 重複檢查：同一分鐘內不能有多個時間
     * 4. 範圍檢查：面試時間 + 持續時間必須完全在可用時段內
     * 
     * @param timestampList 面試時間戳列表（毫秒），Unix 時間戳格式
     * @param availableTimeSlots 可用時段列表，每個元素為 Pair(開始時間, 結束時間)，單位毫秒
     * @param durationMillis 面試持續時間（毫秒），負數會自動校正為0
     * @return 驗證結果，包含是否有效、錯誤列表和錯誤摘要
     * 
     * 使用範例：
     * ```kotlin
     * val timestamps = listOf(1703123456789L)
     * val availableSlots = listOf(
     *     Pair(1703120000000L, 1703130000000L), // 可用時段1
     *     Pair(1703140000000L, 1703150000000L)  // 可用時段2
     * )
     * val duration = DurationConstants.THIRTY_MINUTES
     * 
     * val result = InterviewDateValidator.validateCollaborativeInterviewDates(
     *     timestamps, availableSlots, duration
     * )
     * 
     * if (result.isValid) {
     *     println("協作面試時間驗證通過")
     * } else {
     *     println("驗證失敗：${result.errorMessage}")
     *     if (result.hasOutOfRangeError()) {
     *         println("部分時間超出可用範圍")
     *     }
     * }
     * ```
     * 
     * @since 1.0.0
     * @see InterviewDateValidationResult
     * @see InterviewDateError
     */
    @ObjCName("validateCollaborativeInterviewDates")
    fun validateCollaborativeInterviewDates(
        timestampList: List<Long>,
        availableTimeSlots: List<Pair<Long, Long>>,
        durationMillis: Long
    ): InterviewDateValidationResult {
        val errors = InterviewDateRuleChecker.checkCollaborativeInterviewDatesWithErrors(
            timestampList, availableTimeSlots, durationMillis
        )
        val isValid = errors.all { it == InterviewDateError.NONE }
        val errorMessage = if (isValid) {
            "驗證通過"
        } else {
            val uniqueErrors = errors.filterNot { it == InterviewDateError.NONE }
                .distinct()
                .map { getErrorDescription(it) }
                .joinToString(", ")
            "驗證失敗：$uniqueErrors"
        }
        
        return InterviewDateValidationResult(isValid, errors, errorMessage)
    }

    /**
     * 簡化的基本驗證方法 - 僅返回是否通過
     * 
     * 適用於只需要知道驗證結果而不關心具體錯誤信息的場景。
     * 
     * @param timestampList 面試時間戳列表（毫秒）
     * @return true 表示驗證通過，false 表示存在錯誤
     * 
     * @since 1.0.0
     */
    @ObjCName("isBasicInterviewDatesValid")
    fun isBasicInterviewDatesValid(timestampList: List<Long>): Boolean {
        return InterviewDateRuleChecker.checkInterviewDatesPass(timestampList)
    }

    /**
     * 簡化的協作驗證方法 - 僅返回是否通過
     * 
     * 適用於只需要知道驗證結果而不關心具體錯誤信息的場景。
     * 
     * @param timestampList 面試時間戳列表（毫秒）
     * @param availableTimeSlots 可用時段列表
     * @param durationMillis 面試持續時間（毫秒），負數會自動校正為0
     * @return true 表示驗證通過，false 表示存在錯誤
     * 
     * @since 1.0.0
     */
    @ObjCName("isCollaborativeInterviewDatesValid")
    fun isCollaborativeInterviewDatesValid(
        timestampList: List<Long>,
        availableTimeSlots: List<Pair<Long, Long>>,
        durationMillis: Long
    ): Boolean {
        return InterviewDateRuleChecker.checkCollaborativeInterviewDatesPass(timestampList, availableTimeSlots, durationMillis)
    }

    // ==================== 工具方法 ====================

    /**
     * 獲取錯誤的本地化描述
     * 
     * @param error 錯誤類型
     * @return 錯誤狀態的中文描述文字
     * @since 1.0.0
     */
    @ObjCName("getErrorDescription")
    fun getErrorDescription(error: InterviewDateError): String = when (error) {
        InterviewDateError.NONE -> "無錯誤"
        InterviewDateError.MUST -> "面試時間為必填項目"
        InterviewDateError.DATE_EXPIRED -> "面試時間已過期"
        InterviewDateError.INTERVIEW_DATE_REPEAT -> "面試時間重複"
        InterviewDateError.OUT_OF_RANGE -> "面試時間超出可用範圍"
    }

    /**
     * 將時間戳轉換為可讀的日期時間字符串
     * 
     * @param timestampMillis 時間戳（毫秒）
     * @return 格式化的日期時間字符串，格式：yyyy-MM-dd HH:mm:ss
     * @since 1.0.0
     */
    @ObjCName("formatTimestamp")
    fun formatTimestamp(timestampMillis: Long): String {
        return try {
            val instant = Instant.fromEpochMilliseconds(timestampMillis)
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            "${dateTime.date} ${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}:${dateTime.second.toString().padStart(2, '0')}"
        } catch (e: Exception) {
            "無效時間戳：$timestampMillis"
        }
    }

    /**
     * 獲取當前系統時間的時間戳
     * 
     * @return 當前系統時間的毫秒時間戳
     * @since 1.0.0
     */
    @ObjCName("getCurrentTimestamp")
    fun getCurrentTimestamp(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }

    /**
     * 創建時間段對象
     * 
     * 便利方法，用於創建時間段。特別適用於 Swift 使用者，
     * 避免直接使用 Kotlin 的 Pair 構造函數。
     * 
     * @param startTimeMillis 開始時間（毫秒）
     * @param endTimeMillis 結束時間（毫秒）
     * @return 時間段對象
     * @since 1.0.0
     */
    @ObjCName("createTimeSlot")
    fun createTimeSlot(startTimeMillis: Long, endTimeMillis: Long): Pair<Long, Long> {
        return Pair(startTimeMillis, endTimeMillis)
    }

    /**
     * 面試持續時間常量（毫秒）
     * 
     * 提供常用的面試時間長度，避免手動計算毫秒數。
     * 所有常量都經過 @ObjCName 註解，確保在 Swift 中有良好的命名。
     * 
     * @since 1.0.0
     */
    @ObjCName("DurationConstants")
    object DurationConstants {
        /** 15分鐘面試時間 */
        @ObjCName("fifteenMinutes")
        const val FIFTEEN_MINUTES = 15L * 60 * 1000
        
        /** 30分鐘面試時間（最常用） */
        @ObjCName("thirtyMinutes")
        const val THIRTY_MINUTES = 30L * 60 * 1000
        
        /** 45分鐘面試時間 */
        @ObjCName("fortyFiveMinutes")
        const val FORTY_FIVE_MINUTES = 45L * 60 * 1000
        
        /** 1小時面試時間 */
        @ObjCName("oneHour")
        const val ONE_HOUR = 60L * 60 * 1000
        
        /** 1.5小時面試時間 */
        @ObjCName("oneHourThirtyMinutes")
        const val ONE_HOUR_THIRTY_MINUTES = 90L * 60 * 1000
        
        /** 2小時面試時間 */
        @ObjCName("twoHours")
        const val TWO_HOURS = 120L * 60 * 1000
    }
}