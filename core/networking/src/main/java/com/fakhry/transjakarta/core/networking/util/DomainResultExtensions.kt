package com.fakhry.transjakarta.core.networking.util

import com.fakhry.transjakarta.core.domain.DomainResult
import java.io.IOException

/**
 * Wraps a suspending network call into a [DomainResult], mapping IO failures to network errors.
 */
suspend inline fun <T> mapNetworkCall(crossinline block: suspend () -> T): DomainResult<T> =
    runCatching { DomainResult.Success(block()) }.getOrElse { throwable ->
        when (throwable) {
            is IOException -> DomainResult.Error(
                message = "Network error",
                cause = throwable,
                isNetworkError = true,
            )

            else -> DomainResult.Error(
                message = throwable.message ?: "Unexpected error",
                cause = throwable,
            )
        }
    }
