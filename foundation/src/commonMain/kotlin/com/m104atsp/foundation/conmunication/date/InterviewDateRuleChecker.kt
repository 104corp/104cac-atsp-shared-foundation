package com.m104atsp.foundation.conmunication.date

import kotlinx.datetime.*
import kotlin.native.ObjCName
import kotlin.experimental.ExperimentalObjCName

/**
 * 面試時間驗證器
 * 
 * 提供面試時間戳的完整驗證功能，包括基本驗證和協作面試時段驗證。
 * 驗證規則按優先順序執行：必填檢查 → 過期檢查 → 重複檢查 → 時段範圍檢查。
 * 
 * @since 1.0.0
 * @author M104SharedLogic Team
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("InterviewDateRuleChecker")
object InterviewDateRuleChecker {

    /**
     * 驗證協作面試時間是否全部通過檢查
     * 
     * @param timestampList 面試時間戳清單（毫秒）
     * @param availableTimeList 可用時段清單，每個元素為時段的開始和結束時間（毫秒）
     * @param duration 面試持續時間（毫秒）
     * @return true 表示所有時間都有效，false 表示存在錯誤
     * 
     * @see checkCollaborativeInterviewDatesWithErrors
     * @since 1.0.0
     */
    @ObjCName("checkCollaborativeInterviewDatesPass")
    fun checkCollaborativeInterviewDatesPass(
        timestampList: List<Long>, 
        availableTimeList: List<Pair<Long, Long>>, 
        duration: Long
    ): Boolean {
        val errorList = checkCollaborativeInterviewDatesWithErrors(timestampList, availableTimeList, duration)
        return errorList.all { it == InterviewDateError.NONE }
    }

    /**
     * 檢查協作面試時間清單並返回詳細錯誤狀態
     *
     * 執行完整的面試時間驗證，包括時段範圍檢查。驗證順序按照業務優先級：
     * 1. 必填檢查：空清單視為必填錯誤
     * 2. 過期檢查：與當前系統時間比較
     * 3. 重複檢查：精確到分鐘級別的重複檢測
     * 4. 範圍檢查：確保面試時間完全落在可用時段內
     *
     * @param timestampList 面試時間戳清單（毫秒），可修改的清單
     * @param availableTimeList 可用時段清單，格式為 Pair(開始時間, 結束時間)，單位毫秒
     * @param duration 面試持續時間（毫秒），負數會自動校正為0
     * @return 對應每個時間戳的錯誤狀態清單，與輸入清單索引一一對應
     * @see InterviewDateError
     * @since 1.0.0
     */
    @ObjCName("checkCollaborativeInterviewDatesWithErrors")
    fun checkCollaborativeInterviewDatesWithErrors(
        timestampList: List<Long>, 
        availableTimeList: List<Pair<Long, Long>>, 
        duration: Long
    ): List<InterviewDateError> {
        // 負數持續時間自動校正為零
        val safeDuration = if (duration < 0) 0L else duration
        
        // 空清單檢查
        if (timestampList.isEmpty()) {
            return listOf(InterviewDateError.MUST)
        }

        val currentTime = Clock.System.now().toEpochMilliseconds()
        val minuteTimestamps = timestampList.map { it.toMinutePrecision() }
        val duplicateIndices = findDuplicateIndices(minuteTimestamps)

        // 統一使用 when 表達式進行所有驗證
        return timestampList.mapIndexed { index, timestamp ->
            val interviewEndTime = timestamp + safeDuration
            
            when {
                // 優先級1: 過期檢查
                timestamp < currentTime -> InterviewDateError.DATE_EXPIRED
                
                // 優先級2: 重複檢查（只在沒有過期錯誤時檢查）
                index in duplicateIndices -> InterviewDateError.INTERVIEW_DATE_REPEAT
                
                // 優先級3: 時段範圍檢查（只在沒有其他錯誤時檢查）
                !isTimeWithinAvailableSlots(timestamp, interviewEndTime, availableTimeList) -> 
                    InterviewDateError.OUT_OF_RANGE
                
                // 所有檢查都通過
                else -> InterviewDateError.NONE
            }
        }
    }

    /**
     * 驗證基本面試時間是否全部通過檢查
     * 
     * @param timestampList 面試時間戳清單（毫秒）
     * @return true 表示所有時間都有效，false 表示存在錯誤
     * 
     * @see checkInterviewDatesWithErrors
     * @since 1.0.0
     */
    fun checkInterviewDatesPass(timestampList: List<Long>): Boolean {
        val errorList = checkInterviewDatesWithErrors(timestampList)
        return errorList.all { it == InterviewDateError.NONE }
    }

    /**
     * 檢查基本面試時間清單並返回詳細錯誤狀態
     *
     * 執行基本面試時間驗證，不包含時段範圍檢查。驗證順序按照業務優先級：
     * 1. 必填檢查：空清單視為必填錯誤
     * 2. 過期檢查：與當前系統時間比較
     * 3. 重複檢查：精確到分鐘級別的重複檢測
     *
     * @param timestampList 面試時間戳清單（毫秒）
     * @return 對應每個時間戳的錯誤狀態清單，與輸入清單索引一一對應
     * 
     * @see InterviewDateError
     * @since 1.0.0
     */
    @ObjCName("checkInterviewDatesWithErrors")
    fun checkInterviewDatesWithErrors(timestampList: List<Long>): List<InterviewDateError> {
        // 空清單檢查
        if (timestampList.isEmpty()) {
            return listOf(InterviewDateError.MUST)
        }

        val currentTime = Clock.System.now().toEpochMilliseconds()
        val minuteTimestamps = timestampList.map { it.toMinutePrecision() }
        val duplicateIndices = findDuplicateIndices(minuteTimestamps)

        // 統一使用 when 表達式進行所有驗證
        return timestampList.mapIndexed { index, timestamp ->
            when {
                // 優先級1: 過期檢查
                timestamp < currentTime -> InterviewDateError.DATE_EXPIRED
                
                // 優先級2: 重複檢查（只在沒有過期錯誤時檢查）
                index in duplicateIndices -> InterviewDateError.INTERVIEW_DATE_REPEAT
                
                // 所有檢查都通過
                else -> InterviewDateError.NONE
            }
        }
    }

    // ==================== 私有輔助方法 ====================

    /**
     * 檢查面試時間是否完全落在可用時段內
     * 
     * @param startTime 面試開始時間（毫秒）
     * @param endTime 面試結束時間（毫秒）
     * @param availableTimeList 可用時段清單
     * @return true 表示時間落在某個可用時段內，false 表示超出所有可用時段
     */
    private fun isTimeWithinAvailableSlots(
        startTime: Long, 
        endTime: Long, 
        availableTimeList: List<Pair<Long, Long>>
    ): Boolean {
        return availableTimeList.any { slot ->
            startTime >= slot.first && endTime <= slot.second
        }
    }

    /**
     * 將時間戳轉換為分鐘精度，去除秒和毫秒部分
     *
     * 用於重複檢查時統一時間精度，避免因秒級差異而誤判為不同時間。
     * 
     * @receiver 原始時間戳（毫秒）
     * @return 分鐘精度的時間戳（毫秒）
     * 
     * 範例：1703123456789 → 1703123400000（去除 56.789 秒部分）
     * @since 1.0.0
     */
    private fun Long.toMinutePrecision(): Long = (this / 60000) * 60000

    /**
     * 找出清單中重複元素的所有索引位置
     *
     * 使用 groupBy 進行高效的重複檢測，返回所有重複元素的索引。
     * 
     * @param list 要檢查重複的時間戳清單
     * @return 所有重複元素的索引清單
     * 
     * 範例：[A, B, A, C, B] → [0, 1, 2, 4]（A 和 B 都重複，返回所有相關索引）
     * @since 1.0.0
     */
    private fun findDuplicateIndices(list: List<Long>): Set<Int> {
        val indexMap = list.withIndex().groupBy { it.value }
        return indexMap.values
            .filter { it.size > 1 }     // 找出出現次數 > 1 的值群組
            .flatten()                  // 攤平所有重複的 IndexedValue
            .map { it.index }           // 提取索引值
            .toSet()                    // 轉為 Set 提高查詢效率
    }

}