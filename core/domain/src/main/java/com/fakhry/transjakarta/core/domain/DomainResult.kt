package com.fakhry.transjakarta.core.domain

/**
 * Domain-layer result wrapper that keeps transport details out of callers.
 *
 * Use:
 * - Success: happy path with the mapped domain model.
 * - Error: business/validation/server failures; carry optional code/message/data and network flag.
 * - Empty: no content (e.g., 204, empty list).
 */
sealed class DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>()
    data class Error<T>(
        val message: String,
        val code: Int? = null,
        val data: T? = null,
        val cause: Throwable? = null,
        val isNetworkError: Boolean = false,
    ) : DomainResult<T>()

    data object Empty : DomainResult<Nothing>()
}
