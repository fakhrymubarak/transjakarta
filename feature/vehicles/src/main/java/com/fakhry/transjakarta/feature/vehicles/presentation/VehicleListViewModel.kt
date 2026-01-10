package com.fakhry.transjakarta.feature.vehicles.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.fakhry.transjakarta.feature.vehicles.domain.error.RateLimitException
import com.fakhry.transjakarta.feature.vehicles.domain.repository.VehicleRepository
import com.fakhry.transjakarta.feature.vehicles.presentation.mapper.toUiModel
import com.fakhry.transjakarta.feature.vehicles.presentation.model.RateLimitUiState
import com.fakhry.transjakarta.feature.vehicles.presentation.model.VehicleUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    repository: VehicleRepository,
) : ViewModel() {
    val vehiclesPagingFlow: Flow<PagingData<VehicleUiModel>> =
        repository
            .getVehiclesPagingFlow()
            .map { pagingData -> pagingData.map { it.toUiModel() } }
            .cachedIn(viewModelScope)

    private val _rateLimitState = MutableStateFlow<RateLimitUiState?>(null)
    val rateLimitState: StateFlow<RateLimitUiState?> = _rateLimitState.asStateFlow()
    private var rateLimitJob: Job? = null
    private var activeResetAt: Long? = null
    internal var epochSecondsProvider: () -> Long = { System.currentTimeMillis() / 1_000 }

    fun onRateLimitDetected(error: RateLimitException) {
        val resetAt = error.resetAtEpochSeconds
        if (resetAt == null) {
            rateLimitJob?.cancel()
            activeResetAt = null
            _rateLimitState.value = RateLimitUiState(
                message = error.message ?: "Rate limit exceeded. Please try again soon.",
                remainingSeconds = 0,
                countdownLabel = "00:00",
            )
            return
        }

        if (resetAt == activeResetAt && rateLimitJob?.isActive == true) return

        activeResetAt = resetAt
        rateLimitJob?.cancel()
        rateLimitJob = viewModelScope.launch {
            while (true) {
                val remainingSeconds = calculateRemainingSeconds(resetAt)
                val countdownLabel = formatCountdown(remainingSeconds)
                val message =
                    if (remainingSeconds > 0) {
                        "Rate limit exceeded. Retry in $countdownLabel."
                    } else {
                        error.message ?: "Rate limit exceeded. Please try again soon."
                    }
                _rateLimitState.value = RateLimitUiState(
                    message = message,
                    remainingSeconds = remainingSeconds,
                    countdownLabel = countdownLabel,
                )
                if (remainingSeconds == 0L) break
                delay(1_000)
            }
        }
    }

    private fun calculateRemainingSeconds(resetAtEpochSeconds: Long): Long =
        (resetAtEpochSeconds - epochSecondsProvider()).coerceAtLeast(0)

    private fun formatCountdown(remainingSeconds: Long): String {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}
