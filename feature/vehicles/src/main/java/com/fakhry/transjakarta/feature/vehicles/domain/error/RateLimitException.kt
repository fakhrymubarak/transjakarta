package com.fakhry.transjakarta.feature.vehicles.domain.error

class RateLimitException(
    val resetAtEpochSeconds: Long?,
    message: String,
) : Exception(message)
