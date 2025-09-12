package com.m104atsp.foundation.conmunication.date

/**
 * 可用時間段物件
 *
 * 表示一個具有開始和結束時間的可用時間段，專門用於面試時間驗證。
 * 提供更清晰的語義化表達，比使用 Pair<Long, Long> 更易於理解和維護。
 *
 * @param startTime 開始時間（毫秒），Unix 時間戳格式
 * @param endTime 結束時間（毫秒），Unix 時間戳格式
 *
 * @since 1.0.0
 * @author M104SharedLogic Team
 */
data class AvailableTimeSlot(
    /** 開始時間（毫秒） */
    val startTime: Long,

    /** 結束時間（毫秒） */
    val endTime: Long
) {

    init {
        require(endTime >= startTime) {
            "結束時間($endTime)不能早於開始時間($startTime)"
        }
    }

    /**
     * 檢查指定的時間範圍是否完全在此時間段內
     *
     * @param start 開始時間（毫秒）
     * @param end 結束時間（毫秒）
     * @return true 表示整個時間範圍都在此時間段內，false 表示有部分超出
     * @since 1.0.0
     */
    fun containsRange(start: Long, end: Long): Boolean {
        return start >= startTime && end <= endTime
    }

    companion object {
        /**
         * 從 Pair 創建 AvailableTimeSlot
         *
         * 提供從現有 Pair 格式轉換的便利方法。
         *
         * @param pair Pair(開始時間, 結束時間)
         * @return AvailableTimeSlot 實例
         * @since 1.0.0
         */
        fun fromPair(pair: Pair<Long, Long>): AvailableTimeSlot {
            return AvailableTimeSlot(pair.first, pair.second)
        }
    }
}
