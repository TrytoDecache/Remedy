package com.med.remedy.data.model

data class DashboardProgress(
    val total: Int = 0,
    val completed: Int = 0
) {
    val progress: Float
        get() = if (total == 0) 0f
        else completed.toFloat() / total
}