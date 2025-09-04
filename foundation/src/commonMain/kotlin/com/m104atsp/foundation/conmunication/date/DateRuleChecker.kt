package com.m104atsp.foundation.conmunication.date

import kotlinx.datetime.*

object DateRuleChecker {

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
    fun checkDates(list: MutableList<Long>): MutableList<InterviewDateError> {
        // 空清單檢查
        if (list.isEmpty()) return mutableListOf(InterviewDateError.MUST)

        //取得系統時間毫秒
        val currentTime = Clock.System.now().toEpochMilliseconds()

        // 1. 初始檢查: 過期狀態
        val errorList = list.map { timestamp ->
            when {
                timestamp < currentTime -> InterviewDateError.DATE_EXPIRED
                else -> InterviewDateError.NONE
            }
        }.toMutableList()

        // 2. 檢查重複: 將時間戳轉為分鐘精度
        val minuteTimestamps = list.map { it.toMinutePrecision() }
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