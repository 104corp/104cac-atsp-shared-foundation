package com.m104atsp.foundation.conmunication.date

import kotlin.native.ObjCName
import kotlin.experimental.ExperimentalObjCName

/**
 * 面試時間驗證錯誤類型枚舉
 * 
 * 定義面試時間驗證過程中可能出現的各種錯誤狀態，按業務優先級排序。
 * 驗證器會依照優先級順序檢查，高優先級錯誤會覆蓋低優先級錯誤。
 * 
 * 優先級順序：MUST > DATE_EXPIRED > INTERVIEW_DATE_REPEAT > OUT_OF_RANGE
 * 
 * @since 1.0.0
 * @author M104SharedLogic Team
 * @see InterviewDateRuleChecker
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("InterviewDateError")
enum class InterviewDateError {
    
    /**
     * 無錯誤狀態
     * 
     * 表示面試時間通過所有驗證檢查，可以正常使用。
     * 
     * @since 1.0.0
     */
    @ObjCName("none")
    NONE,
    
    /**
     * 必填錯誤
     * 
     * 當面試時間清單為空時返回此錯誤。
     * 這是最高優先級的錯誤，表示必須提供至少一個面試時間。
     * 
     * 業務場景：
     * - 用戶未選擇任何面試時間
     * - 傳入空的時間戳清單
     * 
     * @since 1.0.0
     */
    @ObjCName("must")
    MUST,
    
    /**
     * 時間過期錯誤
     * 
     * 當面試時間早於當前系統時間時返回此錯誤。
     * 使用 kotlinx-datetime 的系統時鐘進行比較。
     * 
     * 業務場景：
     * - 選擇了過去的時間作為面試時間
     * - 系統時間已超過預定的面試時間
     * 
     * 檢查邏輯：timestamp < Clock.System.now().toEpochMilliseconds()
     * 
     * @since 1.0.0
     */
    @ObjCName("dateExpired")
    DATE_EXPIRED,
    
    /**
     * 面試時間重複錯誤
     * 
     * 當面試時間清單中存在重複時間時返回此錯誤。
     * 重複檢查精確到分鐘級別，忽略秒和毫秒差異。
     * 
     * 業務場景：
     * - 用戶選擇了相同或相近（分鐘級別）的面試時間
     * - 避免在相同時段安排多個面試
     * 
     * 檢查邏輯：將時間戳轉換為分鐘精度後比較
     * 例：14:30:15 和 14:30:45 被視為相同時間
     * 
     * @since 1.0.0
     */
    @ObjCName("interviewDateRepeat")
    INTERVIEW_DATE_REPEAT,
    
    /**
     * 時段範圍超出錯誤
     * 
     * 當面試時間（包含持續時間）超出可用時段範圍時返回此錯誤。
     * 僅在協作面試驗證中使用，確保整個面試過程都在可用時段內。
     * 
     * 業務場景：
     * - 面試開始時間在可用時段內，但結束時間超出範圍
     * - 面試時間完全不在任何可用時段內
     * - 協作面試需要檢查時段衝突
     * 
     * 檢查邏輯：startTime >= slot.first && (startTime + duration) <= slot.second
     * 
     * @since 1.0.0
     * @see InterviewDateRuleChecker.checkCollaborativeInterviewDatesWithErrors
     */
    @ObjCName("outOfRange")
    OUT_OF_RANGE;
}