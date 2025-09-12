package com.m104atsp.foundation.conmunication.date

import kotlinx.datetime.*

object DateRuleChecker {

    fun checkCollaborativeInterviewDatesPass(timestampList: MutableList<Long>, availableTimeList: List<Pair<Long, Long>>, duration: Long): Boolean{
        val errorList = checkCollaborativeInterviewDatesViewErrors(timestampList, availableTimeList, duration)
        return errorList.all { it == InterviewDateError.NONE }
    }

    /**
     * 檢查面試時間清單的錯誤狀態
     *
     * @param timestampList 面試時間戳清單 (毫秒)
     * @param availableTimeSlots 可用時段清單
     * @param duration 面試持續時間 (毫秒)
     * @return 對應每個時間戳的錯誤狀態清單
     *
     * 驗證優先順序: 必填 -> 過期 -> 重複(精確到分鐘) -> 有沒有落在區間內
     * - 空清單: 回傳必填錯誤
     * - 過期時間: 標記為過期錯誤
     * - 重複時間: 標記為重複錯誤 (會覆蓋過期錯誤)
     * - 重複比較精確到分鐘，忽略秒和毫秒
     * - 檢查面試時間是否落在可用區間內
     */
    fun checkCollaborativeInterviewDatesViewErrors(timestampList: MutableList<Long>, availableTimeList: List<Pair<Long, Long>>, duration: Long): MutableList<InterviewDateError> {
        // 空清單檢查
        if (timestampList.isEmpty()) return mutableListOf(InterviewDateError.MUST)

        //取得系統時間毫秒
        val currentTime = Clock.System.now().toEpochMilliseconds()

        // 1. 初始檢查: 過期狀態
        val errorList = timestampList.map { timestamp ->
            when {
                timestamp < currentTime -> InterviewDateError.DATE_EXPIRED
                else -> InterviewDateError.NONE
            }
        }.toMutableList()

        // 2. 檢查重複: 將時間戳轉為分鐘精度
        val minuteTimestamps = timestampList.map { it.toMinutePrecision() }
        val duplicates = findDuplicateIndices(minuteTimestamps)

        // 3. 標記重複錯誤 (不覆蓋已有錯誤)
        duplicates.forEach { index ->
            if (errorList[index] ==  InterviewDateError.NONE) {
                errorList[index] = InterviewDateError.INTERVIEW_DATE_REPEAT
            }
        }
        // 4. 檢查是否落在可用區間內 timestampList + duration 有沒有超過 availableTimeSlots 的區間
        // 有的話回傳錯誤AtsEditErrorType.InterviewDateOutOfRange
        timestampList.forEachIndexed { index, timestamp ->
            if (errorList[index] == InterviewDateError.NONE) {
                val interviewEndTime = timestamp + duration
                val isWithinAvailableSlots = availableTimeList.any { slot ->
                    timestamp >= slot.first && interviewEndTime <= slot.first
                }
                if (!isWithinAvailableSlots) {
                    errorList[index] = InterviewDateError.OUT_OF_RANGE
                }
            }
        }

        return errorList
    }

    fun checkInterviewDatesPass(timestampList: MutableList<Long>): Boolean {
        val errorList = checkInterviewDatesViewErrors(timestampList)
        return errorList.all { it == InterviewDateError.NONE }
    }

    /**
     * 檢查面試時間清單的錯誤狀態
     *
     * @param list 面試時間戳清單 (毫秒)
     * @return 對應每個時間戳的錯誤狀態清單
     *
     * 驗證優先順序: 必填 -> 過期 -> 重複(精確到分鐘)
     * - 空清單: 回傳必填錯誤
     * - 過期時間: 標記為過期錯誤
     * - 重複時間: 標記為重複錯誤 (會覆蓋過期錯誤)
     * - 重複比較精確到分鐘，忽略秒和毫秒
     */
    fun checkInterviewDatesViewErrors(timestampList: MutableList<Long>): MutableList<InterviewDateError> {
        // 空清單檢查
        if (timestampList.isEmpty()) return mutableListOf(InterviewDateError.MUST)

        //取得系統時間毫秒
        val currentTime = Clock.System.now().toEpochMilliseconds()

        // 1. 初始檢查: 過期狀態
        val errorList = timestampList.map { timestamp ->
            when {
                timestamp < currentTime -> InterviewDateError.DATE_EXPIRED
                else -> InterviewDateError.NONE
            }
        }.toMutableList()

        // 2. 檢查重複: 將時間戳轉為分鐘精度
        val minuteTimestamps = timestampList.map { it.toMinutePrecision() }
        val duplicates = findDuplicateIndices(minuteTimestamps)

        // 3. 標記重複錯誤 (不覆蓋已有錯誤)
        duplicates.forEach { index ->
            if (errorList[index] == InterviewDateError.NONE) {
                errorList[index] = InterviewDateError.INTERVIEW_DATE_REPEAT
            }
        }

        return errorList
    }

    /**
     * 將時間戳轉換為分鐘精度，去除秒和毫秒部分
     *
     * @receiver 原始時間戳 (毫秒)
     * @return 分鐘精度的時間戳
     *
     * 例: 1703123456789 -> 1703123400000 (去除 56.789 秒)
     */
    private fun Long.toMinutePrecision(): Long = (this / 60000) * 60000

    /**
     * 找出清單中重複元素的所有索引位置
     *
     * @param list 要檢查的清單
     * @return 所有重複元素的索引清單
     *
     * 例: [A, B, A, C, B] -> [0, 1, 2, 4] (A 和 B 都重複)
     */
    private fun findDuplicateIndices(list: List<Long>): List<Int> {
        val indexMap = list.withIndex().groupBy { it.value }
        return indexMap.values
            .filter { it.size > 1 }  // 找出出現次數 > 1 的值
            .flatten()               // 攤平所有重複的 IndexedValue
            .map { it.index }        // 提取索引
    }

}