package com.fakhry.transjakarta.core.networking.model

/**
 * Transport-layer result wrapper.
 *
 * - Success: parsed payload
 * - HttpError: non-2xx responses
 * - NetworkError: connectivity/DNS/timeout/etc.
 * - UnknownError: unexpected throwables
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class HttpError(val code: Int, val message: String? = null, val rawBody: String? = null) :
        NetworkResult<Nothing>()

    data class NetworkError(val throwable: Throwable? = null) : NetworkResult<Nothing>()
    data class UnknownError(val throwable: Throwable? = null) : NetworkResult<Nothing>()
}
