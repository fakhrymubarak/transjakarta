package com.fakhry.transjakarta.feature.vehicles.presentation.model

data class RateLimitUiState(
    val message: String,
    val remainingSeconds: Long,
    val countdownLabel: String,
) {
    val retryEnabled: Boolean
        get() = remainingSeconds == 0L
}
