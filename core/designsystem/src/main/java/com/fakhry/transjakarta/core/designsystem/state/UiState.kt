package com.fakhry.transjakarta.core.designsystem.state

/**
 * UI-friendly state wrapper. Designed to map cleanly from DomainResult.
 */
sealed class UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(
        val message: String,
        val code: Int? = null,
        val data: T? = null,
        val isNetworkError: Boolean = false,
    ) : UiState<T>()

    data object Empty : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
}
